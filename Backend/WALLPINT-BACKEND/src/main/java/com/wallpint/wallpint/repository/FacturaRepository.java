package com.wallpint.wallpint.repository;

import com.wallpint.wallpint.model.Factura;
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
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    // Método para encontrar una factura por su número único
    Optional<Factura> findByNumeroFactura(String numeroFactura);

    // Método para encontrar una factura por el ID del presupuesto asociado
    Optional<Factura> findByPresupuestoId(Long presupuestoId);
}
