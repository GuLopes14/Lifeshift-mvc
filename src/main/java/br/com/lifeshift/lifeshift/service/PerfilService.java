package br.com.lifeshift.lifeshift.service;

import br.com.lifeshift.lifeshift.dto.PerfilRequestDTO;
import br.com.lifeshift.lifeshift.dto.PerfilResponseDTO;
import br.com.lifeshift.lifeshift.exception.BusinessException;
import br.com.lifeshift.lifeshift.exception.ResourceNotFoundException;
import br.com.lifeshift.lifeshift.model.Perfil;
import br.com.lifeshift.lifeshift.model.Usuario;
import br.com.lifeshift.lifeshift.repository.PerfilRepository;
import br.com.lifeshift.lifeshift.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    @CacheEvict(value = "perfis", allEntries = true)
    public PerfilResponseDTO criarPerfil(PerfilRequestDTO request, String userEmail) {
        log.info("Criando/atualizando perfil para usuário: {}", userEmail);

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        // Verifica se já existe um perfil para o usuário
        var perfilExistente = perfilRepository.findByUsuarioId(usuario.getId());

        if (perfilExistente.isPresent()) {
            log.info("Perfil já existe para usuário {}. Atualizando...", userEmail);
            Perfil perfil = perfilExistente.get();
            perfil.setProfissaoAtual(request.getProfissaoAtual());
            perfil.setProfissaoDesejada(request.getProfissaoDesejada());
            perfil.setHabilidadesAtuais(request.getHabilidadesAtuais());

            perfil = perfilRepository.save(perfil);
            log.info("Perfil atualizado com sucesso. ID: {}", perfil.getId());
            return mapToResponseDTO(perfil);
        }

        // Cria novo perfil
        Perfil perfil = Perfil.builder()
                .profissaoAtual(request.getProfissaoAtual())
                .profissaoDesejada(request.getProfissaoDesejada())
                .habilidadesAtuais(request.getHabilidadesAtuais())
                .usuario(usuario)
                .build();

        perfil = perfilRepository.save(perfil);
        log.info("Perfil criado com sucesso. ID: {}", perfil.getId());

        return mapToResponseDTO(perfil);
    }

    @Transactional
    @CacheEvict(value = "perfis", allEntries = true)
    public PerfilResponseDTO atualizarPerfil(Long id, PerfilRequestDTO request, String userEmail) {
        log.info("Atualizando perfil ID: {} para usuário: {}", id, userEmail);

        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil", "id", id));

        // Verifica se o perfil pertence ao usuário
        if (!perfil.getUsuario().getEmail().equals(userEmail)) {
            throw new BusinessException("Usuário não autorizado a atualizar este perfil");
        }

        perfil.setProfissaoAtual(request.getProfissaoAtual());
        perfil.setProfissaoDesejada(request.getProfissaoDesejada());
        perfil.setHabilidadesAtuais(request.getHabilidadesAtuais());

        perfil = perfilRepository.save(perfil);
        log.info("Perfil atualizado com sucesso. ID: {}", perfil.getId());

        return mapToResponseDTO(perfil);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "perfis", key = "#id")
    public PerfilResponseDTO buscarPorId(Long id, String userEmail) {
        log.info("Buscando perfil ID: {}", id);

        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil", "id", id));

        // Verifica se o perfil pertence ao usuário
        if (!perfil.getUsuario().getEmail().equals(userEmail)) {
            throw new BusinessException("Usuário não autorizado a visualizar este perfil");
        }

        return mapToResponseDTO(perfil);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "perfis", key = "#userEmail")
    public PerfilResponseDTO buscarPorUsuario(String userEmail) {
        log.info("Buscando perfil do usuário: {}", userEmail);

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", userEmail));

        Perfil perfil = perfilRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado para o usuário: " + userEmail));

        return mapToResponseDTO(perfil);
    }

    @Transactional(readOnly = true)
    public List<PerfilResponseDTO> listarTodos() {
        log.info("Listando todos os perfis");
        return perfilRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "perfis", allEntries = true)
    public void deletarPerfil(Long id, String userEmail) {
        log.info("Deletando perfil ID: {}", id);

        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil", "id", id));

        // Verifica se o perfil pertence ao usuário
        if (!perfil.getUsuario().getEmail().equals(userEmail)) {
            throw new BusinessException("Usuário não autorizado a deletar este perfil");
        }

        perfilRepository.delete(perfil);
        log.info("Perfil deletado com sucesso. ID: {}", id);
    }

    private PerfilResponseDTO mapToResponseDTO(Perfil perfil) {
        return PerfilResponseDTO.builder()
                .id(perfil.getId())
                .profissaoAtual(perfil.getProfissaoAtual())
                .profissaoDesejada(perfil.getProfissaoDesejada())
                .habilidadesAtuais(perfil.getHabilidadesAtuais())
                .usuarioId(perfil.getUsuario().getId())
                .usuarioNome(perfil.getUsuario().getNome())
                .dataCriacao(perfil.getDataCriacao())
                .dataAtualizacao(perfil.getDataAtualizacao())
                .build();
    }
}
