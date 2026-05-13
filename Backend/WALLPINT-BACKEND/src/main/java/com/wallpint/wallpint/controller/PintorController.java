package com.wallpint.wallpint.controller;

import com.wallpint.wallpint.dto.PintorResumenDTO;
import com.wallpint.wallpint.service.PintorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pintores")
public class PintorController {

    private final PintorService pintorService;

    public PintorController(PintorService pintorService) {
        this.pintorService = pintorService;
    }

    /** Listado de pintores (admin). Solo expone datos seguros, no passwordHash. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PintorResumenDTO>> obtenerTodos() {
        List<PintorResumenDTO> pintores = pintorService.obtenerTodos().stream()
                .map(PintorResumenDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(pintores);
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PintorResumenDTO>> obtenerActivos() {
        List<PintorResumenDTO> pintores = pintorService.obtenerActivos().stream()
                .map(PintorResumenDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(pintores);
    }

    /** Activa o desactiva un pintor. Inactivos no aparecen al asignar a citas. */
    @PutMapping("/{id}/activo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PintorResumenDTO> cambiarActivo(
            @PathVariable Long id,
            @RequestParam boolean activo
    ) {
        return ResponseEntity.ok(
                PintorResumenDTO.fromEntity(pintorService.cambiarEstadoActivo(id, activo))
        );
    }

    /** Detalle de un pintor concreto (admin). */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PintorResumenDTO> obtener(@PathVariable Long id) {
        return pintorService.obtenerPorId(id)
                .map(p -> ResponseEntity.ok(PintorResumenDTO.fromEntity(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Editar los datos de un pintor (admin). No toca contraseña ni activo. */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PintorResumenDTO> editar(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody
            com.wallpint.wallpint.dto.EditarPintorRequest req
    ) {
        return ResponseEntity.ok(
                PintorResumenDTO.fromEntity(pintorService.editarPintor(id, req))
        );
    }
}
