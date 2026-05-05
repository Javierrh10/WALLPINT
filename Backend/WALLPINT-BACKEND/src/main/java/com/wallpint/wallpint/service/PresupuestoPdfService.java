package com.wallpint.wallpint.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.wallpint.wallpint.model.Cliente;
import com.wallpint.wallpint.model.Estancia;
import com.wallpint.wallpint.model.Presupuesto;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class PresupuestoPdfService {

    // Paleta corporativa (debe casar con la app)
    private static final Color AZUL_OSCURO = new Color(0x00, 0x2B, 0x5B);
    private static final Color AZUL_MEDIO = new Color(0x00, 0x66, 0xCC);
    private static final Color GRIS_TEXTO = new Color(0x6B, 0x72, 0x80);
    private static final Color GRIS_FONDO = new Color(0xF4, 0xF7, 0xFB);
    private static final Color GRIS_CLARO = new Color(0xE5, 0xE7, 0xEB);

    /**
     * Genera el PDF del presupuesto. Devuelve los bytes para que el controller
     * los sirva como respuesta HTTP.
     */
    public byte[] generar(Presupuesto p) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            cabecera(doc, p);
            datosCliente(doc, p);
            tablaEstancias(doc, p);
            resumenTecnico(doc, p);
            desgloseEconomico(doc, p);
            piePagina(doc);

            doc.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    // ====== Bloques del documento ======

    private void cabecera(Document doc, Presupuesto p) throws DocumentException {
        Font fontMarca = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, AZUL_OSCURO);
        Font fontSubt = FontFactory.getFont(FontFactory.HELVETICA, 10, GRIS_TEXTO);

        Paragraph marca = new Paragraph("WallPint", fontMarca);
        marca.setSpacingAfter(2f);
        doc.add(marca);

        Paragraph slogan = new Paragraph("Servicios profesionales de pintura", fontSubt);
        slogan.setSpacingAfter(20f);
        doc.add(slogan);

        // Línea separadora
        doc.add(separadorHorizontal());

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, AZUL_OSCURO);
        Paragraph titulo = new Paragraph("Presupuesto " + tipoTexto(p), fontTitulo);
        titulo.setSpacingBefore(16f);
        titulo.setSpacingAfter(4f);
        doc.add(titulo);

        Font fontRef = FontFactory.getFont(FontFactory.HELVETICA, 11, GRIS_TEXTO);
        String fecha = p.getFechaSolicitud() != null
                ? p.getFechaSolicitud().format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES")))
                : "—";
        Paragraph ref = new Paragraph("Referencia: " + safe(p.getReferencia()) + "    ·    Fecha: " + fecha, fontRef);
        ref.setSpacingAfter(20f);
        doc.add(ref);
    }

    private void datosCliente(Document doc, Presupuesto p) throws DocumentException {
        Cliente c = p.getCliente();
        if (c == null) return;

        seccion(doc, "DATOS DEL CLIENTE");

        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setSpacingAfter(20f);
        try {
            t.setWidths(new float[]{1, 2});
        } catch (DocumentException ignore) {}
        t.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        t.getDefaultCell().setPadding(4f);

        Font fl = FontFactory.getFont(FontFactory.HELVETICA, 10, GRIS_TEXTO);
        Font fv = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, AZUL_OSCURO);

        addFila(t, "Nombre", safe(c.getNombre()) + " " + safe(c.getApellidos()), fl, fv);
        addFila(t, "Email", safe(c.getEmail()), fl, fv);
        addFila(t, "Teléfono", safe(c.getTelefono()), fl, fv);
        if (c.getDireccion() != null && !c.getDireccion().isBlank()) {
            addFila(t, "Dirección", c.getDireccion(), fl, fv);
        }

        doc.add(t);
    }

    private void tablaEstancias(Document doc, Presupuesto p) throws DocumentException {
        if (p.getEstancias() == null || p.getEstancias().isEmpty()) return;

        seccion(doc, "ESTANCIAS (" + p.getEstancias().size() + ")");

        PdfPTable t = new PdfPTable(new float[]{2.2f, 1.5f, 1.4f, 1f, 1f, 1f});
        t.setWidthPercentage(100);
        t.setSpacingAfter(16f);

        addCabeceraTabla(t, "Estancia", "Dimensiones", "Estado paredes", "Puertas", "Ventanas", "Capas");

        Font fc = FontFactory.getFont(FontFactory.HELVETICA, 9, AZUL_OSCURO);
        boolean alterna = false;
        for (Estancia e : p.getEstancias()) {
            Color fondo = alterna ? GRIS_FONDO : Color.WHITE;
            String dims = e.getAncho() + "×" + e.getLargo() + "×" + e.getAlto() + " m";
            String estado = e.getEstadoParedes() != null ? e.getEstadoParedes().name() : "—";
            t.addCell(celdaTexto(safe(e.getNombre()), fc, fondo, Element.ALIGN_LEFT));
            t.addCell(celdaTexto(dims, fc, fondo, Element.ALIGN_CENTER));
            t.addCell(celdaTexto(estado, fc, fondo, Element.ALIGN_CENTER));
            t.addCell(celdaTexto(String.valueOf(e.getNumPuertas()), fc, fondo, Element.ALIGN_CENTER));
            t.addCell(celdaTexto(String.valueOf(e.getNumVentanas()), fc, fondo, Element.ALIGN_CENTER));
            t.addCell(celdaTexto(String.valueOf(e.getNumCapas()), fc, fondo, Element.ALIGN_CENTER));
            alterna = !alterna;
        }

        doc.add(t);
    }

    private void resumenTecnico(Document doc, Presupuesto p) throws DocumentException {
        seccion(doc, "RESUMEN TÉCNICO");

        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);
        t.setSpacingAfter(16f);

        addStat(t, "Superficie", fmt(p.getTotalM2()) + " m²");
        addStat(t, "Pintura", (p.getLitrosPintura() != null ? p.getLitrosPintura() : 0) + " L");
        addStat(t, "Horas", fmt(p.getHorasEstimadas()) + " h");
        addStat(t, "Pintores", String.valueOf(p.getNumPintores() != null ? p.getNumPintores() : 0));

        doc.add(t);
    }

    private void desgloseEconomico(Document doc, Presupuesto p) throws DocumentException {
        seccion(doc, "DESGLOSE ECONÓMICO");

        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setSpacingAfter(16f);
        try {
            t.setWidths(new float[]{3, 1});
        } catch (DocumentException ignore) {}

        Font flabel = FontFactory.getFont(FontFactory.HELVETICA, 11, GRIS_TEXTO);
        Font fvalor = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, AZUL_OSCURO);
        Font ftotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE);

        addLineaDesglose(t, "Materiales", fmtEuro(p.getCosteMateriales()), flabel, fvalor, false);
        addLineaDesglose(t, "Mano de obra", fmtEuro(p.getCosteManoObra()), flabel, fvalor, false);
        addLineaDesglose(t, "IVA (21%)", fmtEuro(p.getIva()), flabel, fvalor, false);
        addLineaDesglose(t, "TOTAL", fmtEuro(p.getTotal()), ftotal, ftotal, true);

        doc.add(t);
    }

    private void piePagina(Document doc) throws DocumentException {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, GRIS_TEXTO);
        Paragraph aviso = new Paragraph(
                "Este presupuesto es orientativo y se ha calculado a partir de las medidas y características aportadas por el cliente. "
              + "El precio definitivo podrá ajustarse tras la visita técnica del equipo de pintores.",
                f
        );
        aviso.setSpacingBefore(20f);
        aviso.setAlignment(Element.ALIGN_JUSTIFIED);
        doc.add(aviso);

        Paragraph contacto = new Paragraph(
                "WallPint · soporte@wallpint.com",
                FontFactory.getFont(FontFactory.HELVETICA, 8, GRIS_TEXTO)
        );
        contacto.setAlignment(Element.ALIGN_CENTER);
        contacto.setSpacingBefore(20f);
        doc.add(contacto);
    }

    // ====== Helpers ======

    private void seccion(Document doc, String titulo) throws DocumentException {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, AZUL_MEDIO);
        Paragraph p = new Paragraph(titulo, f);
        p.setSpacingAfter(8f);
        doc.add(p);
    }

    private Paragraph separadorHorizontal() {
        Paragraph p = new Paragraph(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(0.6f, 100, GRIS_CLARO, Element.ALIGN_CENTER, -2)));
        p.setSpacingAfter(8f);
        return p;
    }

    private void addFila(PdfPTable t, String label, String valor, Font fl, Font fv) {
        t.addCell(new Phrase(label, fl));
        t.addCell(new Phrase(valor, fv));
    }

    private void addCabeceraTabla(PdfPTable t, String... titulos) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String tit : titulos) {
            PdfPCell c = new PdfPCell(new Phrase(tit.toUpperCase(), f));
            c.setBackgroundColor(AZUL_OSCURO);
            c.setHorizontalAlignment(Element.ALIGN_CENTER);
            c.setPadding(8f);
            c.setBorder(Rectangle.NO_BORDER);
            t.addCell(c);
        }
    }

    private PdfPCell celdaTexto(String texto, Font f, Color fondo, int align) {
        PdfPCell c = new PdfPCell(new Phrase(texto, f));
        c.setBackgroundColor(fondo);
        c.setHorizontalAlignment(align);
        c.setPadding(6f);
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    private void addStat(PdfPTable t, String label, String valor) {
        Font fl = FontFactory.getFont(FontFactory.HELVETICA, 9, GRIS_TEXTO);
        Font fv = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, AZUL_OSCURO);

        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.BOX);
        c.setBorderColor(GRIS_CLARO);
        c.setPadding(12f);
        c.addElement(new Paragraph(label.toUpperCase(), fl));
        c.addElement(new Paragraph(valor, fv));
        t.addCell(c);
    }

    private void addLineaDesglose(PdfPTable t, String label, String valor, Font fl, Font fv, boolean total) {
        Color fondo = total ? AZUL_OSCURO : Color.WHITE;
        PdfPCell cl = new PdfPCell(new Phrase(label, fl));
        cl.setBorder(Rectangle.NO_BORDER);
        cl.setBackgroundColor(fondo);
        cl.setPadding(total ? 12f : 6f);
        PdfPCell cv = new PdfPCell(new Phrase(valor, fv));
        cv.setBorder(Rectangle.NO_BORDER);
        cv.setBackgroundColor(fondo);
        cv.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cv.setPadding(total ? 12f : 6f);
        t.addCell(cl);
        t.addCell(cv);
    }

    private String tipoTexto(Presupuesto p) {
        if (p.getTipo() == null) return "orientativo";
        return p.getTipo().name().toLowerCase();
    }

    private String fmt(Double v) {
        return v == null ? "0" : String.format(Locale.forLanguageTag("es-ES"), "%.2f", v);
    }

    private String fmtEuro(Double v) {
        return fmt(v) + " €";
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
