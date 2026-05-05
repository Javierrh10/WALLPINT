package com.wallpint.wallpint.controller;

import com.wallpint.wallpint.model.Factura;
import com.wallpint.wallpint.service.FacturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    // GET: http://localhost:8080/api/facturas
    @GetMapping
    public ResponseEntity<List<Factura>> obtenerTodasLasFacturas() {
        return new ResponseEntity<>(facturaService.obtenerTodas(), HttpStatus.OK);
    }

    // POST: http://localhost:8080/api/facturas
    @PostMapping
    public ResponseEntity<Factura> crearFactura(@RequestBody Factura factura) {
        Factura nuevaFactura = facturaService.guardarFactura(factura);
        return new ResponseEntity<>(nuevaFactura, HttpStatus.CREATED);
    }
}
