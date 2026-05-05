package com.wallpint.wallpint.controller;

import com.wallpint.wallpint.model.Estancia;
import com.wallpint.wallpint.service.EstanciaService;
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
@RequestMapping("/api/estancias")
public class EstanciaController {
    private final EstanciaService estanciaService;

    public EstanciaController(EstanciaService estanciaService) {
        this.estanciaService = estanciaService;
    }

    // GET: http://localhost:8080/api/estancias
    @GetMapping
    public ResponseEntity<List<Estancia>> obtenerTodasLasEstancias() {
        return new ResponseEntity<>(estanciaService.obtenerTodas(), HttpStatus.OK);
    }

    // POST: http://localhost:8080/api/estancias
    @PostMapping
    public ResponseEntity<Estancia> crearEstancia(@RequestBody Estancia estancia) {
        Estancia nuevaEstancia = estanciaService.guardar(estancia);
        return new ResponseEntity<>(nuevaEstancia, HttpStatus.CREATED);
    }
}
