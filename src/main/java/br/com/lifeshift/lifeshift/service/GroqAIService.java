package br.com.lifeshift.lifeshift.service;

import br.com.lifeshift.lifeshift.exception.BusinessException;
import br.com.lifeshift.lifeshift.model.Perfil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GroqAIService {

    @Value("${groq.api.key:}")
    private String groqApiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String groqModel;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GroqAIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gera um plano de requalificação de 6 meses usando Groq API
     */
    public String gerarPlanoRequalificacao(Perfil perfil) {
        try {
            String prompt = construirPrompt(perfil);
            String response = chamarGroqAPI(prompt);

            return extrairConteudoJSON(response);
        } catch (Exception e) {
            log.error("Erro ao gerar plano de requalificação: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao gerar plano de requalificação. Verifique a configuração da API Groq.", e);
        }
    }

    private String construirPrompt(Perfil perfil) {
        return String.format(
            "Você é uma IA especializada em orientação profissional e educação inclusiva. " +
            "Gere um plano de requalificação de 6 meses para alguém que atualmente é %s e deseja se tornar %s. " +
            "As habilidades atuais da pessoa são: %s. " +
            "\n\nCaso a transição seja muito distante (por exemplo, entre áreas técnicas e medicina), gere um plano preparatório realista " +
            "focado em fundamentos, introdução e habilidades transferíveis." +
            "\n\nPara cada mês, indique cursos GRATUITOS disponíveis apenas nas seguintes plataformas reais: " +
            "Coursera (coursera.org), Udemy (udemy.com), edX (edx.org), Khan Academy (khanacademy.org) e YouTube (youtube.com)." +
            "\nMonte o link como uma busca válida na plataforma correspondente. Por exemplo: " +
            "https://www.coursera.org/search?query=introducao+a+programacao (use + ao invés de espaços)." +
            "\n\nRetorne APENAS em formato JSON, com a estrutura exata:" +
            "\n{ \"plano\": [ { \"mes\": 1, \"curso\": \"Nome do curso\", \"descricao\": \"Descrição breve\", \"plataforma\": \"Coursera|Udemy|edX|Khan Academy|YouTube\", \"link\": \"URL DE BUSCA VÁLIDA\" } ] }",
            perfil.getProfissaoAtual(),
            perfil.getProfissaoDesejada(),
            perfil.getHabilidadesAtuais()
        );
    }

    private String chamarGroqAPI(String prompt) {
        if (groqApiKey == null || groqApiKey.isEmpty()) {
            log.error("Groq API Key não configurada.");
            throw new BusinessException("Groq API Key não configurada. Configure a chave no application.properties");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqModel);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "Você é uma IA que ajuda pessoas a planejar sua transição de carreira."),
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    groqApiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Erro ao chamar Groq API: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao chamar Groq API: " + e.getMessage(), e);
        }
    }

    private String extrairConteudoJSON(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode choices = root.path("choices");
            
            if (choices.isArray() && choices.size() > 0) {
                String content = choices.get(0).path("message").path("content").asText();
                
                // Remove markdown se existir
                content = content.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
                
                // Valida se é JSON válido
                objectMapper.readTree(content);
                
                // Converte JSON para HTML formatado
                return formatarPlanoParaHTML(content);
            }
            
            throw new BusinessException("Resposta da IA em formato inválido");
        } catch (Exception e) {
            log.error("Erro ao extrair JSON da resposta: {}", e.getMessage(), e);
            throw new BusinessException("Erro ao processar resposta da IA", e);
        }
    }

    /**
     * Converte o JSON do plano para HTML formatado
     */
    private String formatarPlanoParaHTML(String jsonContent) {
        try {
            JsonNode planoNode = objectMapper.readTree(jsonContent);
            JsonNode meses = planoNode.path("plano");
            
            if (!meses.isArray()) {
                return jsonContent; // Retorna o JSON original se não estiver no formato esperado
            }
            
            StringBuilder html = new StringBuilder();
            html.append("<div class='plano-container'>");
            html.append("<h1>Plano de Transição de Carreira - 6 Meses</h1>");
            html.append("<p class='intro'>Siga este roteiro mês a mês para alcançar sua nova carreira com sucesso.</p>");
            
            for (JsonNode mes : meses) {
                int numeroMes = mes.path("mes").asInt();
                String curso = mes.path("curso").asText("Não especificado");
                String descricao = mes.path("descricao").asText("Sem descrição");
                String plataforma = mes.path("plataforma").asText("Plataforma não informada");
                String link = mes.path("link").asText("");

                html.append("<div class='mes-card'>");
                html.append("<h2>📅 Mês ").append(numeroMes).append("</h2>");
                html.append("<h3>").append(escapeHtml(curso)).append("</h3>");
                html.append("<p><strong>Descrição:</strong> ").append(escapeHtml(descricao)).append("</p>");
                html.append("<p><strong>Plataforma Recomendada:</strong> <code>").append(escapeHtml(plataforma)).append("</code></p>");

                // Adiciona link do curso se disponível
                if (link != null && !link.isEmpty() && !link.equals("null")) {
                    html.append("<a href='").append(escapeHtml(link)).append("' target='_blank' class='course-link'>");
                    html.append("<svg class='w-4 h-4 mr-2' fill='none' stroke='currentColor' viewBox='0 0 24 24'>");
                    html.append("<path stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14'/>");
                    html.append("</svg>");
                    html.append("Acessar Curso");
                    html.append("</a>");
                }

                html.append("</div>");
            }
            
            html.append("<div class='footer-tips'>");
            html.append("<h3>💡 Dicas Importantes</h3>");
            html.append("<ul>");
            html.append("<li>Dedique pelo menos 1-2 horas diárias aos estudos</li>");
            html.append("<li>Pratique com projetos reais desde o início</li>");
            html.append("<li>Conecte-se com profissionais da área no LinkedIn</li>");
            html.append("<li>Documente seu progresso em um portfólio ou GitHub</li>");
            html.append("</ul>");
            html.append("</div>");
            
            html.append("</div>");
            
            return html.toString();
            
        } catch (Exception e) {
            log.error("Erro ao formatar plano para HTML: {}", e.getMessage(), e);
            return "<p>Erro ao formatar o plano. Conteúdo original: " + jsonContent + "</p>";
        }
    }

    /**
     * Escapa HTML para prevenir XSS
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
