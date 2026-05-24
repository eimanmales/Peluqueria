package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.Descuento;
import com.ProgramacionV.Peluqueria.service.ClienteService;
import com.ProgramacionV.Peluqueria.service.DescuentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/descuentos")
@PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
public class DescuentoController {

    @Autowired private DescuentoService descuentoService;
    @Autowired private ClienteService   clienteService;

    // GET /descuentos → lista todos los descuentos
    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("descuentos", descuentoService.listarTodos());
        return "descuentos/lista";
    }

    // GET /descuentos/nuevo → formulario para crear descuento
    @GetMapping("/nuevo")
    public String formularioNuevo(Model modelo) {
        modelo.addAttribute("descuento", new Descuento());
        modelo.addAttribute("titulo", "Nuevo Descuento");
        modelo.addAttribute("clientes", clienteService.listarTodos());
        return "descuentos/formulario";
    }

    // GET /descuentos/editar/{id} → formulario para editar
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model modelo,
                                   RedirectAttributes flash) {
        Descuento descuento = descuentoService.buscarPorId(id).orElse(null);
        if (descuento == null) {
            flash.addFlashAttribute("error", "Descuento no encontrado.");
            return "redirect:/descuentos";
        }
        modelo.addAttribute("descuento", descuento);
        modelo.addAttribute("titulo", "Editar Descuento");
        modelo.addAttribute("clientes", clienteService.listarTodos());
        return "descuentos/formulario";
    }

    // POST /descuentos/guardar → guarda el descuento
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Descuento descuento, RedirectAttributes flash) {
        descuentoService.guardar(descuento);
        flash.addFlashAttribute("exito", "Descuento guardado correctamente.");
        return "redirect:/descuentos";
    }

    // GET /descuentos/toggle/{id} → activa o desactiva un descuento
    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes flash) {
        descuentoService.toggleActivo(id);
        flash.addFlashAttribute("exito", "Estado del descuento actualizado.");
        return "redirect:/descuentos";
    }

    // GET /descuentos/eliminar/{id} → elimina un descuento
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        descuentoService.eliminar(id);
        flash.addFlashAttribute("exito", "Descuento eliminado.");
        return "redirect:/descuentos";
    }
}
