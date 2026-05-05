package com.wallpint.wallpint.service;

import com.wallpint.wallpint.model.Factura;
import com.wallpint.wallpint.repository.FacturaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */
@Service
public class FacturaService {
    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    // Listar todas las facturas
    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }

    // Generar o guardar una nueva factura
    public Factura guardarFactura(Factura factura) {
        // Asignar fecha actual si no viene en el JSON
        if (factura.getFechaEmision() == null) {
            factura.setFechaEmision(LocalDateTime.now());
        }

        // Estado por defecto
        if (factura.getEstado() == null || factura.getEstado().isEmpty()) {
            factura.setEstado("PENDIENTE");
        }

        return facturaRepository.save(factura);
    }
}
