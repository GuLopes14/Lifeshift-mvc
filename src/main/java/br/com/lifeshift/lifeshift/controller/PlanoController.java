package br.com.lifeshift.lifeshift.controller;

import br.com.lifeshift.lifeshift.dto.PlanoRequestDTO;
import br.com.lifeshift.lifeshift.dto.PlanoResponseDTO;
import br.com.lifeshift.lifeshift.service.PlanoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planos")
@RequiredArgsConstructor
public class PlanoController {

    private final PlanoService planoService;

    @PostMapping("/gerar")
    public ResponseEntity<PlanoResponseDTO> gerarPlanoSincrono(
            @Valid @RequestBody PlanoRequestDTO request,
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        PlanoResponseDTO response = planoService.gerarPlanoSincrono(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/gerar-async")
    public ResponseEntity<PlanoResponseDTO> gerarPlanoAssincrono(
            @Valid @RequestBody PlanoRequestDTO request,
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        PlanoResponseDTO response = planoService.gerarPlanoAssicrono(request, userEmail);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        PlanoResponseDTO response = planoService.buscarPorId(id, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PlanoResponseDTO>> listarPlanos(
            @AuthenticationPrincipal OAuth2User principal,
            @PageableDefault(size = 10, sort = "dataGeracao", direction = Sort.Direction.DESC) Pageable pageable) {
        
        String userEmail = principal.getAttribute("email");
        Page<PlanoResponseDTO> response = planoService.listarPorUsuario(userEmail, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<PlanoResponseDTO>> listarHistorico(
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        List<PlanoResponseDTO> response = planoService.listarHistoricoPorUsuario(userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> contarPlanos(
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        long count = planoService.contarPlanosPorUsuario(userEmail);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPlano(
            @PathVariable Long id,
            @AuthenticationPrincipal OAuth2User principal) {
        
        String userEmail = principal.getAttribute("email");
        planoService.deletarPlano(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}
