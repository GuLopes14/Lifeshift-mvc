package br.com.lifeshift.lifeshift.service;

import br.com.lifeshift.lifeshift.config.RabbitMQConfiguration;
import br.com.lifeshift.lifeshift.dto.PlanoRequestDTO;
import br.com.lifeshift.lifeshift.exception.ResourceNotFoundException;
import br.com.lifeshift.lifeshift.model.Perfil;
import br.com.lifeshift.lifeshift.model.Plano;
import br.com.lifeshift.lifeshift.repository.PerfilRepository;
import br.com.lifeshift.lifeshift.repository.PlanoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanoConsumer {

    private final PerfilRepository perfilRepository;
    private final PlanoRepository planoRepository;
    private final GroqAIService groqAIService;

    @RabbitListener(queues = RabbitMQConfiguration.PLANO_QUEUE)
    public void processarGeracaoPlano(PlanoRequestDTO request) {
        log.info("Processando geração de plano para perfil ID: {}", request.getPerfilId());

        try {
            Perfil perfil = perfilRepository.findById(request.getPerfilId())
                    .orElseThrow(() -> new ResourceNotFoundException("Perfil", "id", request.getPerfilId()));

            // Gera o plano usando Groq AI
            String conteudoGerado = groqAIService.gerarPlanoRequalificacao(perfil);

            // Cria o título baseado nas profissões
            String titulo = String.format("Plano de Transição: %s → %s",
                    perfil.getProfissaoAtual(), perfil.getProfissaoDesejada());

            String descricao = String.format("Plano de requalificação de 6 meses gerado com IA para transição de %s para %s",
                    perfil.getProfissaoAtual(), perfil.getProfissaoDesejada());

            Plano plano = Plano.builder()
                    .titulo(titulo)
                    .descricao(descricao)
                    .conteudoGerado(conteudoGerado)
                    .usuario(perfil.getUsuario())
                    .build();

            planoRepository.save(plano);
            log.info("Plano gerado e salvo com sucesso via fila. ID: {}", plano.getId());

        } catch (Exception e) {
            log.error("Erro ao processar geração de plano: {}", e.getMessage(), e);
            // Aqui você poderia enviar para uma DLQ (Dead Letter Queue)
            throw e;
        }
    }
}
