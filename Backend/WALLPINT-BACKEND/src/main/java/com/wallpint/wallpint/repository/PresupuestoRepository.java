package com.wallpint.wallpint.repository;

import com.wallpint.wallpint.model.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {

    // Buscar todos los presupuestos de un cliente, más recientes primero
    List<Presupuesto> findByClienteIdOrderByFechaSolicitudDesc(Long clienteId);
}