package br.com.lifeshift.lifeshift.controller;

import br.com.lifeshift.lifeshift.dto.PerfilRequestDTO;
import br.com.lifeshift.lifeshift.dto.PerfilResponseDTO;
import br.com.lifeshift.lifeshift.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfis")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @PostMapping
    public ResponseEntity<PerfilResponseDTO> criarPerfil(
            @Valid @RequestBody PerfilRequestDTO request,
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        PerfilResponseDTO response = perfilService.criarPerfil(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> atualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody PerfilRequestDTO request,
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        PerfilResponseDTO response = perfilService.atualizarPerfil(id, request, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerfilResponseDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        PerfilResponseDTO response = perfilService.buscarPorId(id, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/meu-perfil")
    public ResponseEntity<PerfilResponseDTO> buscarMeuPerfil(
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        PerfilResponseDTO response = perfilService.buscarPorUsuario(userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PerfilResponseDTO>> listarTodos() {
        List<PerfilResponseDTO> response = perfilService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPerfil(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        perfilService.deletarPerfil(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
