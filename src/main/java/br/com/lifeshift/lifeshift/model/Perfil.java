package br.com.lifeshift.lifeshift.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{perfil.profissaoAtual.notblank}")
    @Size(min = 3, max = 100, message = "{perfil.profissaoAtual.size}")
    @Column(nullable = false, length = 100)
    private String profissaoAtual;

    @NotBlank(message = "{perfil.profissaoDesejada.notblank}")
    @Size(min = 3, max = 100, message = "{perfil.profissaoDesejada.size}")
    @Column(nullable = false, length = 100)
    private String profissaoDesejada;

    @NotBlank(message = "{perfil.habilidadesAtuais.notblank}")
    @Size(min = 10, max = 500, message = "{perfil.habilidadesAtuais.size}")
    @Column(nullable = false, length = 500)
    private String habilidadesAtuais;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
