package com.wallpint.wallpint.repository;

import com.wallpint.wallpint.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    // Para listar todas las citas de un cliente
    List<Cita> findByClienteId(Long clienteId);

    // Listar citas de un cliente ordenadas por fecha (más recientes primero)
    List<Cita> findByClienteIdOrderByFechaHoraDesc(Long clienteId);

    // Borrar todas las citas de un presupuesto (usado al eliminar el presupuesto)
    long deleteByPresupuestoId(Long presupuestoId);

    // Comprobar si el cliente ya tiene cita activa en esa fecha+hora exacta
    boolean existsByClienteIdAndFechaHoraAndEstadoNot(Long clienteId, java.time.LocalDateTime fechaHora, String estado);

    // Comprobar si un presupuesto ya tiene una cita activa (no cancelada)
    boolean existsByPresupuestoIdAndEstadoNot(Long presupuestoId, String estado);

    // Citas asignadas a un pintor concreto (relación @ManyToMany), más recientes primero
    @Query("SELECT c FROM Cita c JOIN c.pintores p WHERE p.id = :pintorId ORDER BY c.fechaHora DESC")
    List<Cita> findCitasAsignadasAlPintor(@Param("pintorId") Long pintorId);

    /**
     * ¿Existe alguna cita del presupuesto X en estado Y donde el pintor con
     * ese email está asignado? Se usa para autorizar al pintor a editar el
     * presupuesto solo cuando tiene visita en curso.
     */
    @Query("""
        SELECT COUNT(c) > 0 FROM Cita c
        JOIN c.pintores p
        WHERE c.presupuesto.id = :presupuestoId
          AND c.estado = :estado
          AND p.email = :email
        """)
    boolean existsByPresupuestoIdAndEstadoAndPintorEmail(
            @Param("presupuestoId") Long presupuestoId,
            @Param("estado") String estado,
            @Param("email") String email
    );

    // Para el calendario: buscar citas en un rango de fechas
    List<Cita> findByFechaHoraBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Para filtrar por estado (Ej: "PROGRAMADA", "CANCELADA", "COMPLETADA", "EN_CURSO")
    List<Cita> findByEstado(String estado);
}
