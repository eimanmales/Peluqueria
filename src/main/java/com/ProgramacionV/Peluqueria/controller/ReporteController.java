package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // GET /reportes → pantalla de selección de reportes
    @GetMapping
    public String index(Model modelo) {
        modelo.addAttribute("hoy", LocalDate.now());
        modelo.addAttribute("inicioMes", LocalDate.now().withDayOfMonth(1));
        return "reportes/index";
    }

    // GET /reportes/pagos?inicio=...&fin=... → descarga PDF de pagos
    @GetMapping("/pagos")
    public ResponseEntity<byte[]> reportePagos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            byte[] pdf = reporteService.generarReportePagos(inicio, fin);
            String nombre = "reporte-pagos-" + inicio.format(DateTimeFormatter.BASIC_ISO_DATE) + ".pdf";
            return construirRespuestaPdf(pdf, nombre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /reportes/clientes → descarga PDF de clientes
    @GetMapping("/clientes")
    public ResponseEntity<byte[]> reporteClientes() {
        try {
            byte[] pdf = reporteService.generarReporteClientes();
            return construirRespuestaPdf(pdf, "reporte-clientes.pdf");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /reportes/citas?inicio=...&fin=... → descarga PDF de citas
    @GetMapping("/citas")
    public ResponseEntity<byte[]> reporteCitas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            byte[] pdf = reporteService.generarReporteCitas(inicio, fin);
            String nombre = "reporte-citas-" + inicio.format(DateTimeFormatter.BASIC_ISO_DATE) + ".pdf";
            return construirRespuestaPdf(pdf, nombre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /reportes/mantenimiento → descarga PDF de equipos
    @GetMapping("/mantenimiento")
    public ResponseEntity<byte[]> reporteMantenimiento() {
        try {
            byte[] pdf = reporteService.generarReporteMantenimiento();
            return construirRespuestaPdf(pdf, "reporte-mantenimiento.pdf");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ── Método auxiliar para construir la respuesta HTTP con el PDF ──
    private ResponseEntity<byte[]> construirRespuestaPdf(byte[] pdf, String nombreArchivo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // "attachment" hace que el navegador lo descargue en lugar de abrirlo
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(nombreArchivo).build());
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
