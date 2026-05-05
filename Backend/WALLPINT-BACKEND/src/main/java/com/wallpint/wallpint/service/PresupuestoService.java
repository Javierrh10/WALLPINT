package com.wallpint.wallpint.service;

import com.wallpint.wallpint.model.EstadoPresupuesto;
import com.wallpint.wallpint.model.Presupuesto;
import com.wallpint.wallpint.model.TipoPresupuesto;
import com.wallpint.wallpint.repository.CitaRepository;
import com.wallpint.wallpint.repository.PresupuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PresupuestoService {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private CitaRepository citaRepository;

    public List<Presupuesto> obtenerTodos() {
        return presupuestoRepository.findAll();
    }

    public List<Presupuesto> obtenerPorCliente(Long clienteId) {
        return presupuestoRepository.findByClienteIdOrderByFechaSolicitudDesc(clienteId);
    }

    public Optional<Presupuesto> obtenerPorId(Long id) {
        return presupuestoRepository.findById(id);
    }

    public Presupuesto guardar(Presupuesto presupuesto) {
        return presupuestoRepository.save(presupuesto);
    }

    /**
     * Elimina un presupuesto. Antes borra cualquier cita asociada para evitar
     * que la foreign key de citas.presupuesto_id rompa la integridad.
     */
    @Transactional
    public void eliminar(Long id) {
        citaRepository.deleteByPresupuestoId(id);
        presupuestoRepository.deleteById(id);
    }

    /**
     * Convierte un presupuesto ORIENTATIVO en DEFINITIVO con los datos ajustados
     * tras la visita técnica. El pintor puede modificar todos los campos
     * técnicos y económicos, no solo el precio.
     *
     * Estrategia de cálculo:
     *  - Si vienen costeMateriales y costeManoObra → total = (mat + mano) * 1.21
     *  - Si solo viene nuevoTotal → se distribuye 40/60 entre materiales y mano
     *  - Si no viene ni uno ni otro → mantiene los costes actuales
     */
    @Transactional
    public Presupuesto marcarDefinitivo(Long id, com.wallpint.wallpint.dto.MarcarDefinitivoRequest req) {
        Presupuesto p = presupuestoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        if (p.getTipo() == TipoPresupuesto.DEFINITIVO) {
            throw new IllegalStateException("El presupuesto ya es definitivo");
        }

        // Datos técnicos: solo se actualizan si vienen
        if (req.getTotalM2() != null) p.setTotalM2(redondear(req.getTotalM2()));
        if (req.getLitrosPintura() != null) p.setLitrosPintura(req.getLitrosPintura());
        if (req.getHorasEstimadas() != null) p.setHorasEstimadas(redondear(req.getHorasEstimadas()));
        if (req.getNumPintores() != null) p.setNumPintores(req.getNumPintores());

        // Económicos: dos modos según qué venga
        boolean tieneDesglose = req.getCosteMateriales() != null && req.getCosteManoObra() != null;
        if (tieneDesglose) {
            double materiales = req.getCosteMateriales();
            double manoObra = req.getCosteManoObra();
            double subtotal = materiales + manoObra;
            double total = subtotal * 1.21;
            double iva = total - subtotal;

            p.setCosteMateriales(redondear(materiales));
            p.setCosteManoObra(redondear(manoObra));
            p.setIva(redondear(iva));
            p.setTotal(redondear(total));
        } else if (req.getNuevoTotal() != null) {
            double total = req.getNuevoTotal();
            double subtotal = total / 1.21;
            double iva = total - subtotal;

            p.setTotal(redondear(total));
            p.setIva(redondear(iva));
            p.setCosteMateriales(redondear(subtotal * 0.4));
            p.setCosteManoObra(redondear(subtotal * 0.6));
        }
        // Si no vienen costes ni total, se mantienen los del orientativo.

        p.setTipo(TipoPresupuesto.DEFINITIVO);
        p.setEstado(EstadoPresupuesto.PENDIENTE_ACEPTACION);

        return presupuestoRepository.save(p);
    }

    /**
     * El cliente acepta o rechaza un presupuesto DEFINITIVO que está
     * pendiente de su decisión.
     */
    @Transactional
    public Presupuesto responder(Long id, boolean aceptar) {
        Presupuesto p = presupuestoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        if (p.getEstado() != EstadoPresupuesto.PENDIENTE_ACEPTACION) {
            throw new IllegalStateException(
                    "Solo se puede responder a presupuestos PENDIENTE_ACEPTACION (estado actual: " + p.getEstado() + ")"
            );
        }

        p.setEstado(aceptar ? EstadoPresupuesto.ACEPTADO : EstadoPresupuesto.RECHAZADO);
        return presupuestoRepository.save(p);
    }

    private double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}