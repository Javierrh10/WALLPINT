package com.wallpint.wallpint.controller;

import com.wallpint.wallpint.dto.AsignarPintoresRequest;
import com.wallpint.wallpint.dto.CitaDTO;
import com.wallpint.wallpint.dto.CrearCitaRequest;
import com.wallpint.wallpint.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CitaDTO> listar() {
        return citaService.obtenerTodas();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<CitaDTO> listarPorCliente(@PathVariable Long clienteId) {
        return citaService.obtenerPorCliente(clienteId);
    }

    @GetMapping("/pintor/{pintorId}")
    @PreAuthorize("hasRole('PINTOR') or hasRole('ADMIN')")
    public List<CitaDTO> listarPorPintor(@PathVariable Long pintorId) {
        return citaService.obtenerPorPintor(pintorId);
    }

    @PostMapping
    public ResponseEntity<CitaDTO> crear(@Valid @RequestBody CrearCitaRequest req) {
        CitaDTO creada = citaService.crearCita(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cambia el estado de una cita (uso del admin).
     * Estados válidos: PENDIENTE, CONFIRMADA, EN_CURSO, COMPLETADA, CANCELADA.
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PINTOR')")
    public ResponseEntity<CitaDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado
    ) {
        return ResponseEntity.ok(citaService.cambiarEstado(id, nuevoEstado));
    }

    /** Detalle completo de una cita (con pintores asignados). Admin y pintor. */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PINTOR')")
    public ResponseEntity<CitaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }

    /** Admin: reemplaza la lista de pintores asignados a la cita. */
    @PutMapping("/{id}/pintores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CitaDTO> asignarPintores(
            @PathVariable Long id,
            @RequestBody AsignarPintoresRequest req
    ) {
        return ResponseEntity.ok(citaService.asignarPintores(id, req.getPintorIds()));
    }
}
