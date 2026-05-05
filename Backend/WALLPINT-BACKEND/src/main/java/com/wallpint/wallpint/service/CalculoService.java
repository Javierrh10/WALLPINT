package com.wallpint.wallpint.service;

import com.wallpint.wallpint.dto.CalcularPresupuestoRequest;
import com.wallpint.wallpint.dto.EstanciaRequest;
import com.wallpint.wallpint.model.*;
import com.wallpint.wallpint.repository.ClienteRepository;
import com.wallpint.wallpint.repository.PresupuestoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoService {

    // === CONSTANTES DE CÁLCULO (justificadas en la memoria) ===
    private static final double SUPERFICIE_PUERTA = 1.6;        // m² (0,8 × 2 m estándar)
    private static final double SUPERFICIE_VENTANA = 1.5;       // m² (1,5 × 1 m media)
    private static final double RENDIMIENTO_PINTURA = 10.0;     // m² por litro
    private static final double RENDIMIENTO_PINTOR = 8.0;       // m² por hora
    private static final double PRECIO_LITRO_PINTURA = 15.0;    // €/L
    private static final double PRECIO_EXTRAS_M2 = 0.5;         // €/m² (consumibles)
    private static final double PRECIO_HORA_PINTOR = 22.0;      // €/h
    private static final double IVA = 0.21;                     // 21%

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Calcula el presupuesto orientativo a partir de los datos de las estancias.
     * Aplica el algoritmo descrito en la memoria del TFG.
     */
    public Presupuesto calcular(CalcularPresupuestoRequest request) {

        // 1. Buscar cliente
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // 2. Crear presupuesto
        Presupuesto p = new Presupuesto();
        p.setCliente(cliente);
        p.setFechaSolicitud(LocalDateTime.now());
        p.setTipo(TipoPresupuesto.ORIENTATIVO);
        p.setEstado(EstadoPresupuesto.ORIENTATIVO);
        p.setReferencia("PRE-" + System.currentTimeMillis());

        // 3. Acumuladores
        double totalM2 = 0.0;
        double coefPinturaPonderado = 0.0;   // ponderado por m² de cada estancia
        double coefTiempoPonderado = 0.0;
        List<Estancia> estancias = new ArrayList<>();

        // 4. Procesar cada estancia
        for (EstanciaRequest req : request.getEstancias()) {
            double m2 = calcularM2Estancia(req);
            totalM2 += m2;

            double[] coefs = obtenerCoeficientes(req.getEstadoParedes());
            coefPinturaPonderado += coefs[0] * m2;
            coefTiempoPonderado += coefs[1] * m2;

            // Crear y enlazar entidad Estancia
            Estancia e = new Estancia();
            e.setNombre(req.getNombre());
            e.setAncho(req.getAncho());
            e.setLargo(req.getLargo());
            e.setAlto(req.getAlto());
            e.setEstadoParedes(req.getEstadoParedes());
            e.setNumPuertas(req.getNumPuertas());
            e.setNumVentanas(req.getNumVentanas());
            e.setColor(req.getColor());
            e.setNumCapas(req.getNumCapas());
            e.setIncluirTecho(req.getIncluirTecho());
            e.setPresupuesto(p);
            estancias.add(e);
        }
        p.setEstancias(estancias);

        // 5. Coeficientes medios ponderados
        double coefPintura = totalM2 > 0 ? coefPinturaPonderado / totalM2 : 1.0;
        double coefTiempo = totalM2 > 0 ? coefTiempoPonderado / totalM2 : 1.0;

        // 6. Litros de pintura (redondeado hacia arriba)
        int litros = (int) Math.ceil((totalM2 / RENDIMIENTO_PINTURA) * coefPintura);

        // 7. Horas estimadas
        double horas = (totalM2 / RENDIMIENTO_PINTOR) * coefTiempo;

        // 8. Número de pintores
        int numPintores;
        if (horas <= 8) numPintores = 1;
        else if (horas <= 24) numPintores = 2;
        else numPintores = 3;

        // 9. Costes
        double costeMateriales = (litros * PRECIO_LITRO_PINTURA) + (totalM2 * PRECIO_EXTRAS_M2);
        double costeManoObra = horas * PRECIO_HORA_PINTOR;
        double subtotal = costeMateriales + costeManoObra;
        double iva = subtotal * IVA;
        double total = subtotal + iva;

        // 10. Asignar al presupuesto (redondeado a 2 decimales)
        p.setTotalM2(redondear(totalM2));
        p.setLitrosPintura(litros);
        p.setHorasEstimadas(redondear(horas));
        p.setNumPintores(numPintores);
        p.setCosteMateriales(redondear(costeMateriales));
        p.setCosteManoObra(redondear(costeManoObra));
        p.setIva(redondear(iva));
        p.setTotal(redondear(total));

        // 11. Guardar (las estancias se guardan en cascada)
        return presupuestoRepository.save(p);
    }

    /**
     * Calcula los m² netos de una estancia: paredes − puertas − ventanas (+ techo si aplica) × capas.
     */
    private double calcularM2Estancia(EstanciaRequest e) {
        double paredes = 2 * (e.getAncho() + e.getLargo()) * e.getAlto();
        double descuentos = (e.getNumPuertas() * SUPERFICIE_PUERTA)
                + (e.getNumVentanas() * SUPERFICIE_VENTANA);
        double m2 = paredes - descuentos;

        if (Boolean.TRUE.equals(e.getIncluirTecho())) {
            m2 += e.getAncho() * e.getLargo();
        }

        m2 *= e.getNumCapas();

        return Math.max(m2, 0); // Por seguridad (puertas y ventanas no pueden dar negativo)
    }

    /**
     * Devuelve los coeficientes [pintura, tiempo] según el estado de la pared.
     */
    private double[] obtenerCoeficientes(EstadoPared estado) {
        return switch (estado) {
            case NUEVO -> new double[]{1.0, 1.0};
            case IMPRIMACION -> new double[]{1.15, 1.2};
            case DETERIORADO -> new double[]{1.3, 1.5};
        };
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}