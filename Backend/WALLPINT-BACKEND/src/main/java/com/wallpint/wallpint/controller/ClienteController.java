package com.wallpint.wallpint.controller;

import com.wallpint.wallpint.dto.ClienteResumenDTO;
import com.wallpint.wallpint.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /** Listado de clientes (admin). Devuelve DTO sin datos sensibles. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteResumenDTO>> listar() {
        List<ClienteResumenDTO> clientes = clienteService.obtenerTodos().stream()
                .map(ClienteResumenDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    /** Detalle de un cliente concreto (admin). */
    @GetMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResumenDTO> obtener(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(c -> ResponseEntity.ok(ClienteResumenDTO.fromEntity(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** Editar los datos de un cliente (admin). */
    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResumenDTO> editar(
            @PathVariable Long id,
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody
            com.wallpint.wallpint.dto.EditarClienteRequest req
    ) {
        return ResponseEntity.ok(
                ClienteResumenDTO.fromEntity(clienteService.editarCliente(id, req))
        );
    }
}
