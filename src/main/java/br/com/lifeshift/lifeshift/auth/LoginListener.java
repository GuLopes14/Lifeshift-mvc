package br.com.lifeshift.lifeshift.auth;

import br.com.lifeshift.lifeshift.service.UsuarioService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * LoginListener desabilitado pois o registro é feito em UsuarioService.loadUser()
 * Mantido para referência, mas não está ativo
 */
//@Component // Comentado para desabilitar
public class LoginListener implements ApplicationListener<AuthenticationSuccessEvent> {
    
    private final UsuarioService usuarioService;

    public LoginListener(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Não faz nada - registro é feito em UsuarioService.loadUser()
        // usuarioService.register((OAuth2User) event.getAuthentication().getPrincipal());
    }
}
