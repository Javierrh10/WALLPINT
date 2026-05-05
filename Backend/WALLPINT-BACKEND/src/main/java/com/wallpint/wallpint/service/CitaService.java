package com.wallpint.wallpint.service;

import com.wallpint.wallpint.dto.CitaDTO;
import com.wallpint.wallpint.dto.CrearCitaRequest;
import com.wallpint.wallpint.model.Cita;
import com.wallpint.wallpint.model.Cliente;
import com.wallpint.wallpint.model.Pintor;
import com.wallpint.wallpint.model.Presupuesto;
import com.wallpint.wallpint.repository.CitaRepository;
import com.wallpint.wallpint.repository.ClienteRepository;
import com.wallpint.wallpint.repository.PintorRepository;
import com.wallpint.wallpint.repository.PresupuestoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository citaRepository;
    private final ClienteRepository clienteRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final PintorRepository pintorRepository;

    public CitaService(
            CitaRepository citaRepository,
            ClienteRepository clienteRepository,
            PresupuestoRepository presupuestoRepository,
            PintorRepository pintorRepository
    ) {
        this.citaRepository = citaRepository;
        this.clienteRepository = clienteRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.pintorRepository = pintorRepository;
    }

    private static final java.util.Set<String> ESTADOS_VALIDOS = java.util.Set.of(
            "PENDIENTE", "CONFIRMADA", "EN_CURSO", "COMPLETADA", "CANCELADA"
    );

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerTodas() {
        // Más recientes primero — el panel admin las ordena así por defecto
        return citaRepository.findAll(
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC,
                        "fechaHora"
                )
        ).stream().map(CitaDTO::fromEntity).toList();
    }

    /** Reemplaza la lista completa de pintores asignados a una cita. */
    @Transactional
    public CitaDTO asignarPintores(Long citaId, List<Long> pintorIds) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        List<Pintor> pintores = new ArrayList<>();
        if (pintorIds != null) {
            for (Long pid : pintorIds) {
                Pintor p = pintorRepository.findById(pid)
                        .orElseThrow(() -> new RuntimeException("Pintor no encontrado: " + pid));
                pintores.add(p);
            }
        }
        cita.setPintores(pintores);
        return CitaDTO.fromEntity(citaRepository.save(cita));
    }

    @Transactional(readOnly = true)
    public CitaDTO obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .map(CitaDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
    }

    @Transactional
    public CitaDTO cambiarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstado);
        }
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(nuevoEstado.toUpperCase());
        return CitaDTO.fromEntity(citaRepository.save(cita));
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerPorCliente(Long clienteId) {
        return citaRepository.findByClienteIdOrderByFechaHoraDesc(clienteId)
                .stream().map(CitaDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> obtenerPorPintor(Long pintorId) {
        return citaRepository.findCitasAsignadasAlPintor(pintorId)
                .stream().map(CitaDTO::fromEntity).toList();
    }

    @Transactional
    public CitaDTO crearCita(CrearCitaRequest req) {
        Cliente cliente = clienteRepository.findById(req.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Bloquear doble reserva: el cliente no puede tener otra cita activa
        // exactamente en la misma fecha y hora.
        boolean yaTieneCita = citaRepository.existsByClienteIdAndFechaHoraAndEstadoNot(
                req.getClienteId(), req.getFechaHora(), "CANCELADA"
        );
        if (yaTieneCita) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya tienes una cita reservada en esa fecha y franja"
            );
        }

        // Un presupuesto solo puede tener una cita activa a la vez.
        // Si quiere cambiarla, antes debe cancelar la anterior.
        boolean presupuestoYaTieneCita = citaRepository.existsByPresupuestoIdAndEstadoNot(
                req.getPresupuestoId(), "CANCELADA"
        );
        if (presupuestoYaTieneCita) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este presupuesto ya tiene una cita activa. Cancélala antes de crear otra."
            );
        }

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setFechaHora(req.getFechaHora());
        cita.setFranja(req.getFranja());
        cita.setEstado("PENDIENTE");
        cita.setNotasInternas(req.getNotas());

        Presupuesto presupuesto = presupuestoRepository.findById(req.getPresupuestoId())
                .orElseThrow(() -> new RuntimeException("Presupuesto no encontrado"));

        // El presupuesto debe pertenecer al mismo cliente — sin esta comprobación,
        // alguien podría asociar una cita a un presupuesto ajeno.
        if (!presupuesto.getCliente().getId().equals(cliente.getId())) {
            throw new RuntimeException("El presupuesto no pertenece al cliente");
        }
        cita.setPresupuesto(presupuesto);

        Cita guardada = citaRepository.save(cita);
        return CitaDTO.fromEntity(guardada);
    }

    @Transactional
    public void eliminar(Long id) {
        citaRepository.deleteById(id);
    }
}
