package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.*;
import com.ProgramacionV.Peluqueria.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/pagos")
@PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
public class PagoController {

    @Autowired private PagoService      pagoService;
    @Autowired private CitaService      citaService;
    @Autowired private DescuentoService descuentoService;
    @Autowired private ClienteService   clienteService;

    // GET /pagos → lista todos los pagos
    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("pagos", pagoService.listarTodos());
        modelo.addAttribute("totalHoy", pagoService.totalDelDia());
        return "pagos/lista";
    }

    // GET /pagos/nuevo?citaId=X → formulario de cobro para una cita
    @GetMapping("/nuevo")
    public String formularioNuevo(@RequestParam(required = false) Long citaId, Model modelo) {
        Pago pago = new Pago();

        if (citaId != null) {
            citaService.buscarPorId(citaId).ifPresent(cita -> {
                pago.setCita(cita);
                pago.setMonto(cita.getServicio().getPrecio());

                // Verificar si hay descuento disponible para el cliente
                Descuento descuento = descuentoService.buscarDescuentoAplicable(
                        cita.getCliente().getId());
                if (descuento != null) {
                    modelo.addAttribute("descuentoDisponible", descuento);
                    BigDecimal montoConDescuento = pagoService.aplicarDescuento(
                            cita.getServicio().getPrecio(), descuento.getPorcentaje());
                    modelo.addAttribute("montoConDescuento", montoConDescuento);
                }
            });
        }

        modelo.addAttribute("pago", pago);
        modelo.addAttribute("metodos", MetodoPago.values());
        // Citas COMPLETADAS sin pago previo
        modelo.addAttribute("citas", citaService.listarTodas().stream()
                .filter(c -> c.getEstado() == EstadoCita.COMPLETADA)
                .filter(c -> c.getPago() == null)
                .toList());
        return "pagos/formulario"; 
    }

    // POST /pagos/registrar → registra el pago y genera factura
    @PostMapping("/registrar")
    public String registrar(@ModelAttribute Pago pago, RedirectAttributes flash) {
        Pago pagoPersistido = pagoService.registrar(pago);
        flash.addFlashAttribute("exito",
                "Pago registrado. Factura: " + pagoPersistido.getNumeroFactura());
        return "redirect:/pagos";
    }

    // GET /pagos/factura/{id} → muestra el detalle de una factura
    @GetMapping("/factura/{id}")
    public String verFactura(@PathVariable Long id, Model modelo,
                             RedirectAttributes flash) {
        Pago pago = pagoService.buscarPorId(id).orElse(null);
        if (pago == null) {
            flash.addFlashAttribute("error", "Pago no encontrado.");
            return "redirect:/pagos";
        }
        modelo.addAttribute("pago", pago);
        return "pagos/factura";
    }
}
