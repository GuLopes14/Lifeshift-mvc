package br.com.lifeshift.lifeshift.service;

import br.com.lifeshift.lifeshift.config.RabbitMQConfiguration;
import br.com.lifeshift.lifeshift.dto.PlanoRequestDTO;
import br.com.lifeshift.lifeshift.dto.PlanoResponseDTO;
import br.com.lifeshift.lifeshift.exception.BusinessException;
import br.com.lifeshift.lifeshift.exception.ResourceNotFoundException;
import br.com.lifeshift.lifeshift.model.Perfil;
import br.com.lifeshift.lifeshift.model.Plano;
import br.com.lifeshift.lifeshift.model.Usuario;
import br.com.lifeshift.lifeshift.repository.PerfilRepository;
import br.com.lifeshift.lifeshift.repository.PlanoRepository;
import br.com.lifeshift.lifeshift.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanoService {

    private final PlanoRepository planoRepository;
    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final GroqAIService groqAIService;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @CacheEvict(value = "planos", allEntries = true)
    public PlanoResponseDTO gerarPlanoAssicrono(PlanoRequestDTO request, String userEmail) {
        log.info("Enviando requisição para fila: geração de plano para perfil ID: {}", request.getPerfilId());

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        Perfil perfil = perfilRepository.findById(request.getPerfilId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil", "id", request.getPerfilId()));

        // Verifica se o perfil pertence ao usuário
        if (!perfil.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Perfil não pertence ao usuário autenticado");
        }

        // Envia para fila RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitMQConfiguration.PLANO_EXCHANGE,
                RabbitMQConfiguration.PLANO_ROUTING_KEY,
                request
        );

        log.info("Requisição enviada para fila com sucesso");

        // Retorna resposta imediata
        return PlanoResponseDTO.builder()
                .titulo("Plano em Processamento")
                .descricao("Seu plano de requalificação está sendo gerado pela IA. Em breve estará disponível.")
                .usuarioId(usuario.getId())
                .usuarioNome(usuario.getNome())
                .build();
    }

    @Transactional
    @CacheEvict(value = "planos", allEntries = true)
    public PlanoResponseDTO gerarPlanoSincrono(PlanoRequestDTO request, String userEmail) {
        log.info("Gerando plano síncrono para perfil ID: {}", request.getPerfilId());

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        Perfil perfil = perfilRepository.findById(request.getPerfilId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil", "id", request.getPerfilId()));

        // Verifica se o perfil pertence ao usuário
        if (!perfil.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Perfil não pertence ao usuário autenticado");
        }

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
                .usuario(usuario)
                .perfil(perfil)
                .build();

        plano = planoRepository.save(plano);
        log.info("Plano gerado e salvo com sucesso. ID: {}", plano.getId());

        return mapToResponseDTO(plano);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "planos", key = "#id")
    public PlanoResponseDTO buscarPorId(Long id, String userEmail) {
        log.info("Buscando plano ID: {}", id);

        Plano plano = planoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano", "id", id));

        // Verifica se o plano pertence ao usuário
        if (!plano.getUsuario().getEmail().equals(userEmail)) {
            throw new BusinessException("Usuário não autorizado a visualizar este plano");
        }

        return mapToResponseDTO(plano);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "planos", key = "#userEmail + '_' + #pageable.pageNumber")
    public Page<PlanoResponseDTO> listarPorUsuario(String userEmail, Pageable pageable) {
        log.info("Listando planos do usuário: {} - Página: {}", userEmail, pageable.getPageNumber());

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        Page<Plano> planos = planoRepository.findAllByUsuarioId(usuario.getId(), pageable);
        return planos.map(this::mapToResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<PlanoResponseDTO> listarHistoricoPorUsuario(String userEmail) {
        log.info("Buscando histórico de planos do usuário: {}", userEmail);

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        // Busca todos os planos com paginação e ordena em memória
        Page<Plano> planos = planoRepository.findAllByUsuarioId(usuario.getId(), Pageable.unpaged());
        
        return planos.stream()
                .sorted((p1, p2) -> p2.getDataGeracao().compareTo(p1.getDataGeracao()))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "planos", allEntries = true)
    public void deletarPlano(Long id, String userEmail) {
        log.info("Deletando plano ID: {}", id);

        Plano plano = planoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano", "id", id));

        // Verifica se o plano pertence ao usuário
        if (!plano.getUsuario().getEmail().equals(userEmail)) {
            throw new BusinessException("Usuário não autorizado a deletar este plano");
        }

        planoRepository.delete(plano);
        log.info("Plano deletado com sucesso. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public long contarPlanosPorUsuario(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        return planoRepository.findAllByUsuarioId(usuario.getId(), Pageable.unpaged()).getTotalElements();
    }

    private PlanoResponseDTO mapToResponseDTO(Plano plano) {
        return PlanoResponseDTO.builder()
                .id(plano.getId())
                .titulo(plano.getTitulo())
                .descricao(plano.getDescricao())
                .conteudoGerado(plano.getConteudoGerado())
                .usuarioId(plano.getUsuario().getId())
                .usuarioNome(plano.getUsuario().getNome())
                .dataGeracao(plano.getDataGeracao())
                .build();
    }
}
