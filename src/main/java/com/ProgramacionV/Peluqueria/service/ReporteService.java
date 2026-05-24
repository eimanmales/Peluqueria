package com.ProgramacionV.Peluqueria.service;

import com.ProgramacionV.Peluqueria.entity.*;
import com.ProgramacionV.Peluqueria.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    @Autowired private PagoRepository     pagoRepository;
    @Autowired private ClienteRepository  clienteRepository;
    @Autowired private CitaRepository     citaRepository;
    @Autowired private MaquinaRepository  maquinaRepository;

    private static final DateTimeFormatter FMT_FECHA  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT     = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color COLOR_ENCABEZADO       = new Color(31, 56, 100);   // azul oscuro
    private static final Color COLOR_FILA_PAR         = new Color(238, 242, 255); // azul muy claro

    // ════════════════════════════════════════════════════════
    //  1. REPORTE DE PAGOS
    // ════════════════════════════════════════════════════════
    public byte[] generarReportePagos(LocalDate inicio, LocalDate fin) {
        List<Pago> pagos = pagoRepository.findByFechaBetweenOrderByFechaDesc(
                inicio.atStartOfDay(), fin.plusDays(1).atStartOfDay());
        BigDecimal total = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            agregarEncabezado(doc, "Reporte de Pagos",
                    "Período: " + inicio.format(FMT_FECHA) + " — " + fin.format(FMT_FECHA));

            // Tabla
            PdfPTable tabla = crearTabla(new float[]{1.5f, 2.5f, 2.5f, 2f, 2f, 2f},
                    "# Factura", "Cliente", "Servicio", "Método", "Fecha", "Monto");

            int fila = 0;
            for (Pago p : pagos) {
                Color bg = (fila++ % 2 == 0) ? Color.WHITE : COLOR_FILA_PAR;
                String cliente = p.getCita() != null ? p.getCita().getCliente().getNombreCompleto() : "—";
                String servicio = p.getCita() != null ? p.getCita().getServicio().getNombre() : "—";
                agregarFila(tabla, bg,
                        p.getNumeroFactura(),
                        cliente,
                        servicio,
                        p.getMetodoPago().name(),
                        p.getFecha().format(FMT_DT),
                        "$ " + p.getMonto().toPlainString());
            }
            doc.add(tabla);

            // Total
            doc.add(new Paragraph(" "));
            Paragraph pTotal = new Paragraph("Total recaudado: $ " + total.toPlainString(),
                    new Font(Font.HELVETICA, 13, Font.BOLD, COLOR_ENCABEZADO));
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            doc.add(pTotal);

            agregarPieDePagina(doc, pagos.size() + " registros");

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte de pagos", e);
        } finally {
            doc.close();
        }
        return baos.toByteArray();
    }

    // ════════════════════════════════════════════════════════
    //  2. REPORTE DE CLIENTES
    // ════════════════════════════════════════════════════════
    public byte[] generarReporteClientes() {
        List<Cliente> clientes = clienteRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            agregarEncabezado(doc, "Reporte de Clientes",
                    "Generado el " + LocalDate.now().format(FMT_FECHA));

            PdfPTable tabla = crearTabla(new float[]{0.5f, 2f, 2f, 2f, 1.5f},
                    "#", "Nombre", "Teléfono", "Correo", "Cortes Acum.");

            int fila = 0;
            for (Cliente c : clientes) {
                Color bg = (fila++ % 2 == 0) ? Color.WHITE : COLOR_FILA_PAR;
                agregarFila(tabla, bg,
                        String.valueOf(c.getId()),
                        c.getNombreCompleto(),
                        c.getTelefono() != null ? c.getTelefono() : "—",
                        c.getEmail()    != null ? c.getEmail()    : "—",
                        String.valueOf(c.getCortesAcumulados()));
            }
            doc.add(tabla);
            agregarPieDePagina(doc, clientes.size() + " clientes registrados");

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte de clientes", e);
        } finally {
            doc.close();
        }
        return baos.toByteArray();
    }

    // ════════════════════════════════════════════════════════
    //  3. REPORTE DE CITAS
    // ════════════════════════════════════════════════════════
    public byte[] generarReporteCitas(LocalDate inicio, LocalDate fin) {
        List<Cita> citas = citaRepository.findAll().stream()
                .filter(c -> {
                    LocalDate fecha = c.getFechaHora().toLocalDate();
                    return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
                }).toList();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4.rotate());

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            agregarEncabezado(doc, "Reporte de Citas",
                    "Período: " + inicio.format(FMT_FECHA) + " — " + fin.format(FMT_FECHA));

            PdfPTable tabla = crearTabla(new float[]{2f, 2f, 2f, 2f, 1.5f, 2f},
                    "Cliente", "Servicio", "Peluquero", "Fecha y Hora", "Espera", "Estado");

            int fila = 0;
            for (Cita c : citas) {
                Color bg = (fila++ % 2 == 0) ? Color.WHITE : COLOR_FILA_PAR;
                agregarFila(tabla, bg,
                        c.getCliente().getNombreCompleto(),
                        c.getServicio().getNombre(),
                        c.getPeluquero().getNombreCompleto(),
                        c.getFechaHora().format(FMT_DT),
                        c.getTiempoEsperaMin() + " min",
                        c.getEstado().name());
            }
            doc.add(tabla);
            agregarPieDePagina(doc, citas.size() + " citas en el período");

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte de citas", e);
        } finally {
            doc.close();
        }
        return baos.toByteArray();
    }

    // ════════════════════════════════════════════════════════
    //  4. REPORTE DE MANTENIMIENTO DE EQUIPOS
    // ════════════════════════════════════════════════════════
    public byte[] generarReporteMantenimiento() {
        List<Maquina> maquinas = maquinaRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            agregarEncabezado(doc, "Reporte de Mantenimiento de Equipos",
                    "Generado el " + LocalDate.now().format(FMT_FECHA));

            PdfPTable tabla = crearTabla(new float[]{2f, 1.5f, 1f, 2f, 1.5f, 2f},
                    "Nombre", "Tipo", "Usos", "Último Mant.", "Estado", "Alerta");

            int fila = 0;
            for (Maquina m : maquinas) {
                Color bg = m.necesitaMantenimiento()
                        ? new Color(255, 243, 205)  // amarillo claro si necesita mantenimiento
                        : (fila % 2 == 0 ? Color.WHITE : COLOR_FILA_PAR);
                fila++;

                String ultimoMant = m.getFechaUltimoMantenimiento() != null
                        ? m.getFechaUltimoMantenimiento().format(FMT_FECHA) : "Nunca";
                String alerta = m.necesitaMantenimiento() ? "⚠ PENDIENTE" : "OK";

                agregarFila(tabla, bg,
                        m.getNombre(),
                        m.getTipo() != null ? m.getTipo() : "—",
                        String.valueOf(m.getUsosTotales()),
                        ultimoMant,
                        m.getEstado().name(),
                        alerta);
            }
            doc.add(tabla);

            long pendientes = maquinas.stream().filter(Maquina::necesitaMantenimiento).count();
            agregarPieDePagina(doc, maquinas.size() + " equipos  |  " + pendientes + " con mantenimiento pendiente");

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte de mantenimiento", e);
        } finally {
            doc.close();
        }
        return baos.toByteArray();
    }

    // ════════════════════════════════════════════════════════
    //  MÉTODOS AUXILIARES COMPARTIDOS
    // ════════════════════════════════════════════════════════

    private void agregarEncabezado(Document doc, String titulo, String subtitulo)
            throws DocumentException {
        Font fTitulo    = new Font(Font.HELVETICA, 18, Font.BOLD,  COLOR_ENCABEZADO);
        Font fSubtitulo = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.GRAY);
        Font fEmpresa   = new Font(Font.HELVETICA, 10, Font.ITALIC, Color.GRAY);

        Paragraph empresa = new Paragraph("Sistema de Gestión — Peluquería", fEmpresa);
        empresa.setAlignment(Element.ALIGN_CENTER);
        doc.add(empresa);

        Paragraph pTitulo = new Paragraph(titulo, fTitulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        pTitulo.setSpacingBefore(4);
        doc.add(pTitulo);

        Paragraph pSub = new Paragraph(subtitulo, fSubtitulo);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(14);
        doc.add(pSub);
    }

    private PdfPTable crearTabla(float[] anchos, String... encabezados)
            throws DocumentException {
        PdfPTable tabla = new PdfPTable(anchos.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(anchos);
        tabla.setSpacingBefore(4);

        Font fEnc = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
        for (String enc : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(enc, fEnc));
            celda.setBackgroundColor(COLOR_ENCABEZADO);
            celda.setPadding(7);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }
        return tabla;
    }

    private void agregarFila(PdfPTable tabla, Color bg, String... valores) {
        Font fDato = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
        for (String valor : valores) {
            PdfPCell celda = new PdfPCell(new Phrase(valor != null ? valor : "—", fDato));
            celda.setBackgroundColor(bg);
            celda.setPadding(5);
            tabla.addCell(celda);
        }
    }

    private void agregarPieDePagina(Document doc, String texto) throws DocumentException {
        Font fPie = new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY);
        Paragraph pie = new Paragraph(
                "Generado el " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                + "  |  " + texto, fPie);
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(12);
        doc.add(pie);
    }
}
