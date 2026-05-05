package com.wallpint.wallpint.repository;

import com.wallpint.wallpint.model.Pintor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Repository
public interface PintorRepository extends JpaRepository<Pintor, Long> {
    // Método para buscar un pintor por su email (para login)
    Optional<Pintor> findByEmail(String email);

    // Método para listar solo los pintores activos
    List<Pintor> findByActivoTrue();
}
