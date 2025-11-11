package br.com.lifeshift.lifeshift.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider = Provider.GOOGLE;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }

    public static Usuario fromOAuth2User(OAuth2User principal) {
        Object pictureAttr = principal.getAttribute("picture");
        return Usuario.builder()
                .nome(principal.getAttribute("name"))
                .email(principal.getAttribute("email"))
                .picture(pictureAttr != null ? pictureAttr.toString() : null)
                .provider(Provider.GOOGLE)
                .providerId(principal.getAttribute("sub")) 
                .build();
    }

    public enum Provider {
        GOOGLE
    }
}
