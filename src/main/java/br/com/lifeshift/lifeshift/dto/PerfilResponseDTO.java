package br.com.lifeshift.lifeshift.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilResponseDTO {

    private Long id;
    private String profissaoAtual;
    private String profissaoDesejada;
    private String habilidadesAtuais;
    private Long usuarioId;
    private String usuarioNome;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
