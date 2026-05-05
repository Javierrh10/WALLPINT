package com.wallpint.wallpint.repository;

import com.wallpint.wallpint.model.Estancia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Repository
public interface EstanciaRepository extends JpaRepository<Estancia, Long> {
    // Método para encontrar estancias por el ID del presupuesto
    List<Estancia> findByPresupuestoId(Long presupuestoId);
}
