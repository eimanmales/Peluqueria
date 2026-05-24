package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.Servicio;
import com.ProgramacionV.Peluqueria.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    // GET /servicios → lista todos los servicios activos
    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("servicios", servicioService.listarTodos());
        return "servicios/lista";
    }

    // GET /servicios/nuevo → formulario para crear un servicio
    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public String formularioNuevo(Model modelo) {
        modelo.addAttribute("servicio", new Servicio());
        modelo.addAttribute("titulo", "Nuevo Servicio");
        return "servicios/formulario";
    }

    // GET /servicios/editar/{id} → formulario para editar un servicio
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public String formularioEditar(@PathVariable Long id, Model modelo,
                                   RedirectAttributes flash) {
        Servicio servicio = servicioService.buscarPorId(id).orElse(null);
        if (servicio == null) {
            flash.addFlashAttribute("error", "Servicio no encontrado.");
            return "redirect:/servicios";
        }
        modelo.addAttribute("servicio", servicio);
        modelo.addAttribute("titulo", "Editar Servicio");
        return "servicios/formulario";
    }

    // POST /servicios/guardar → guarda el servicio (nuevo o editado)
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public String guardar(@ModelAttribute Servicio servicio, RedirectAttributes flash) {
        servicioService.guardar(servicio);
        flash.addFlashAttribute("exito", "Servicio guardado correctamente.");
        return "redirect:/servicios";
    }

    // GET /servicios/desactivar/{id} → desactiva un servicio
    @GetMapping("/desactivar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String desactivar(@PathVariable Long id, RedirectAttributes flash) {
        servicioService.desactivar(id);
        flash.addFlashAttribute("exito", "Servicio desactivado.");
        return "redirect:/servicios";
    }
}
