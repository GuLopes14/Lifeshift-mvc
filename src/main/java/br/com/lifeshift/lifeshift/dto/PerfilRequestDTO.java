package br.com.lifeshift.lifeshift.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilRequestDTO {

    @NotBlank(message = "{perfil.profissaoAtual.notblank}")
    @Size(min = 3, max = 100, message = "{perfil.profissaoAtual.size}")
    private String profissaoAtual;

    @NotBlank(message = "{perfil.profissaoDesejada.notblank}")
    @Size(min = 3, max = 100, message = "{perfil.profissaoDesejada.size}")
    private String profissaoDesejada;

    @NotBlank(message = "{perfil.habilidadesAtuais.notblank}")
    @Size(min = 10, max = 500, message = "{perfil.habilidadesAtuais.size}")
    private String habilidadesAtuais;
}
