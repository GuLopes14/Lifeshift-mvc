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
public class PlanoResponseDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private String conteudoGerado;
    private Long usuarioId;
    private String usuarioNome;
    private LocalDateTime dataGeracao;
}
