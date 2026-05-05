package com.wallpint.wallpint.repository;

import com.wallpint.wallpint.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Esta interfaz define el repositorio para la entidad Usuario,
 * proporcionando métodos para realizar operaciones CRUD
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);

    // Método para buscar un usuario por su correo electrónico
    Optional<Usuario> findByEmail(String email);
}
