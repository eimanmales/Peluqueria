package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.Servicio;
import com.ProgramacionV.Peluqueria.service.ImagenService;
import com.ProgramacionV.Peluqueria.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired private ServicioService servicioService;
    @Autowired private ImagenService   imagenService;

    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("servicios", servicioService.listarTodos());
        return "servicios/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public String formularioNuevo(Model modelo) {
        Servicio servicio = new Servicio();
        servicio.setActivo(true);        // ← CORRECCIÓN: evita null
        servicio.setDuracionMinutos(30);
        modelo.addAttribute("servicio", servicio);
        modelo.addAttribute("titulo", "Nuevo Servicio");
        return "servicios/formulario";
    }

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

    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public String guardar(
            @ModelAttribute Servicio servicio,
            @RequestParam(name = "imagenArchivo", required = false) MultipartFile imagenArchivo,
            RedirectAttributes flash) {
        try {
            if (servicio.getActivo() == null) {
                servicio.setActivo(true);  // ← CORRECCIÓN: null safety
            }
            if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
                imagenService.eliminarImagen(servicio.getImagenUrl());
                String nuevaUrl = imagenService.guardarImagen(imagenArchivo);
                servicio.setImagenUrl(nuevaUrl);
            }
            servicioService.guardar(servicio);
            flash.addFlashAttribute("exito", "Servicio guardado correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/servicios";
    }

    @GetMapping("/desactivar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String desactivar(@PathVariable Long id, RedirectAttributes flash) {
        servicioService.desactivar(id);
        flash.addFlashAttribute("exito", "Servicio desactivado.");
        return "redirect:/servicios";
    }
}