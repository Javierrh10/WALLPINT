package com.wallpint.wallpint.repository;

import com.wallpint.wallpint.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Método para buscar un cliente por su email (para login)
    Optional<Cliente> findByEmail(String email);
}
