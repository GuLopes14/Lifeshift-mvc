package br.com.lifeshift.lifeshift.controller;

import br.com.lifeshift.lifeshift.dto.PerfilRequestDTO;
import br.com.lifeshift.lifeshift.dto.PlanoRequestDTO;
import br.com.lifeshift.lifeshift.service.PerfilService;
import br.com.lifeshift.lifeshift.service.PlanoService;
import br.com.lifeshift.lifeshift.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AppController {

    private final PerfilService perfilService;
    private final PlanoService planoService;
    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/intro")
    public String intro() {
        return "intro";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

    @PostMapping("/criar-perfil")
    public String criarPerfil(
            @Valid @ModelAttribute PerfilRequestDTO perfilRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal OAuth2User principal,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // Validar campos
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("perfil", perfilRequest);
            return "perfil";
        }
        
        // Validações customizadas
        if (perfilRequest.getProfissaoAtual().trim().length() < 3) {
            model.addAttribute("error", "A profissão atual deve ter no mínimo 3 caracteres");
            model.addAttribute("perfil", perfilRequest);
            return "perfil";
        }
        
        if (perfilRequest.getProfissaoDesejada().trim().length() < 3) {
            model.addAttribute("error", "A profissão desejada deve ter no mínimo 3 caracteres");
            model.addAttribute("perfil", perfilRequest);
            return "perfil";
        }
        
        if (perfilRequest.getHabilidadesAtuais().trim().length() < 20) {
            model.addAttribute("error", "Por favor, descreva suas habilidades com mais detalhes (mínimo 20 caracteres)");
            model.addAttribute("perfil", perfilRequest);
            return "perfil";
        }
        
        String userEmail = principal.getAttribute("email");
        
        try {
            // Criar perfil
            var perfil = perfilService.criarPerfil(perfilRequest, userEmail);
            
            // Gerar plano síncrono
            PlanoRequestDTO planoRequest = PlanoRequestDTO.builder()
                    .perfilId(perfil.getId())
                    .build();
            
            var plano = planoService.gerarPlanoSincrono(planoRequest, userEmail);
            
            // Redirecionar para loading com o ID do plano
            return "redirect:/loading?planoId=" + plano.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao criar o perfil: " + e.getMessage());
            model.addAttribute("perfil", perfilRequest);
            return "perfil";
        }
    }

    @GetMapping("/loading")
    public String loading(@RequestParam Long planoId, Model model) {
        model.addAttribute("planoId", planoId);
        return "loading";
    }

    @GetMapping("/plano")
    public String plano(@RequestParam Long id, 
                             @AuthenticationPrincipal OAuth2User principal,
                             Model model) {
        String userEmail = principal.getAttribute("email");
        var plano = planoService.buscarPorId(id, userEmail);
        model.addAttribute("plano", plano);
        return "plano";
    }

    @GetMapping("/planos-salvos")
    public String planosSalvos(@AuthenticationPrincipal OAuth2User principal, Model model) {
        String userEmail = principal.getAttribute("email");
        var planos = planoService.listarPorUsuario(userEmail, PageRequest.of(0, 10));
        model.addAttribute("planos", planos.getContent());
        return "planos-salvos";
    }

    @PostMapping("/deletar-plano")
    public String deletarPlano(@RequestParam Long planoId,
                              @AuthenticationPrincipal OAuth2User principal) {
        String userEmail = principal.getAttribute("email");
        planoService.deletarPlano(planoId, userEmail);
        return "redirect:/planos-salvos";
    }

    @GetMapping("/conta")
    public String conta(@AuthenticationPrincipal OAuth2User principal, Model model) {
        String email = principal.getAttribute("email");
        
        // Busca o usuário do banco de dados
        var usuario = usuarioService.findByEmail(email);
        
        model.addAttribute("userName", usuario.getNome());
        model.addAttribute("userEmail", usuario.getEmail());
        model.addAttribute("userPicture", usuario.getPicture());
        
        return "conta";
    }
}
