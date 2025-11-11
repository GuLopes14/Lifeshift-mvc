package br.com.lifeshift.lifeshift.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "plano")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{plano.titulo.notblank}")
    @Size(min = 5, max = 200, message = "{plano.titulo.size}")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Size(max = 1000, message = "{plano.descricao.size}")
    @Column(length = 1000)
    private String descricao;

    @NotBlank(message = "{plano.conteudoGerado.notblank}")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudoGerado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataGeracao;

    @PrePersist
    protected void onCreate() {
        dataGeracao = LocalDateTime.now();
    }
}
