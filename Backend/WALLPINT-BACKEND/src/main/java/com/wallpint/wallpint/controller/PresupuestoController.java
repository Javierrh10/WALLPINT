package com.wallpint.wallpint.controller;

import com.wallpint.wallpint.dto.CalcularPresupuestoRequest;
import com.wallpint.wallpint.dto.MarcarDefinitivoRequest;
import com.wallpint.wallpint.model.Presupuesto;
import com.wallpint.wallpint.service.CalculoService;
import com.wallpint.wallpint.service.PresupuestoPdfService;
import com.wallpint.wallpint.service.PresupuestoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presupuestos")
public class PresupuestoController {

    @Autowired
    private PresupuestoService presupuestoService;

    @Autowired
    private CalculoService calculoService;

    @Autowired
    private PresupuestoPdfService pdfService;

    @GetMapping
    public List<Presupuesto> listar() {
        return presupuestoService.obtenerTodos();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Presupuesto> listarPorCliente(@PathVariable Long clienteId) {
        return presupuestoService.obtenerPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Presupuesto> obtener(@PathVariable Long id) {
        return presupuestoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        presupuestoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calcular")
    public ResponseEntity<Presupuesto> calcular(@Valid @RequestBody CalcularPresupuestoRequest request) {
        Presupuesto resultado = calculoService.calcular(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    /**
     * Admin/pintor convierte el presupuesto en DEFINITIVO con el precio
     * ajustado tras la visita técnica.
     */
    @PutMapping("/{id}/definitivo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PINTOR')")
    public ResponseEntity<Presupuesto> marcarDefinitivo(
            @PathVariable Long id,
            @Valid @RequestBody MarcarDefinitivoRequest req
    ) {
        return ResponseEntity.ok(presupuestoService.marcarDefinitivo(id, req));
    }

    /**
     * Edita los datos del presupuesto (técnicos y económicos).
     *  - Admin: puede editar siempre.
     *  - Pintor: solo si tiene una cita asociada al presupuesto en EN_CURSO.
     * El service comprueba el rol y lanza 403 si el pintor no está autorizado.
     */
    @PutMapping("/{id}/editar")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PINTOR')")
    public ResponseEntity<Presupuesto> editarPresupuesto(
            @PathVariable Long id,
            @Valid @RequestBody MarcarDefinitivoRequest req,
            org.springframework.security.core.Authentication auth
    ) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(
                presupuestoService.editarPresupuesto(id, req, auth.getName(), esAdmin)
        );
    }

    /**
     * Cliente acepta o rechaza un presupuesto DEFINITIVO pendiente.
     */
    @PutMapping("/{id}/responder")
    public ResponseEntity<Presupuesto> responder(
            @PathVariable Long id,
            @RequestParam boolean aceptar
    ) {
        return ResponseEntity.ok(presupuestoService.responder(id, aceptar));
    }

    /**
     * Genera y devuelve el PDF del presupuesto.
     * @Transactional para que las relaciones lazy (cliente, estancias) se carguen
     * mientras la sesión Hibernate está abierta.
     */
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        return presupuestoService.obtenerPorId(id)
                .map(p -> {
                    byte[] pdf = pdfService.generar(p);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.setContentDispositionFormData(
                            "attachment",
                            "presupuesto-" + p.getReferencia() + ".pdf"
                    );
                    return ResponseEntity.ok().headers(headers).body(pdf);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}