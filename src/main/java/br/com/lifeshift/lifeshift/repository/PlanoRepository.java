package br.com.lifeshift.lifeshift.repository;

import br.com.lifeshift.lifeshift.model.Plano;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long> {
    Page<Plano> findAllByUsuarioId(Long usuarioId, Pageable pageable);
}
