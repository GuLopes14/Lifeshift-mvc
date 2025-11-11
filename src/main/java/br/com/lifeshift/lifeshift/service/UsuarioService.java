package br.com.lifeshift.lifeshift.service;

import br.com.lifeshift.lifeshift.model.Usuario;
import br.com.lifeshift.lifeshift.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UsuarioService extends DefaultOAuth2UserService {
    
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra ou atualiza um usuário após login OAuth2
     */
    @Transactional
    public Usuario register(OAuth2User principal) {
        String email = principal.getAttribute("email");
        log.info("Registrando/atualizando usuário com email: {}", email);

        var optionalUser = usuarioRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            log.info("Usuário não encontrado. Criando novo usuário...");
            Usuario newUser = Usuario.fromOAuth2User(principal);
            Usuario saved = usuarioRepository.save(newUser);
            log.info("Usuário criado com ID: {}", saved.getId());
            return saved;
        } else {
            log.info("Usuário já existe com ID: {}. Retornando usuário existente.", optionalUser.get().getId());
            return optionalUser.get();
        }
    }

    /**
     * Busca um usuário por email
     */
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("LoadUser chamado para processar autenticação OAuth2");
        OAuth2User principal = super.loadUser(userRequest);
        register(principal);
        return principal;
    }
}
