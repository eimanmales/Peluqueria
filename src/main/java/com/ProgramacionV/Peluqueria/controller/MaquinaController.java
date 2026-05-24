package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.EstadoMaquina;
import com.ProgramacionV.Peluqueria.entity.Maquina;
import com.ProgramacionV.Peluqueria.service.MaquinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/maquinas")
@PreAuthorize("hasAnyRole('ADMIN','PELUQUERO')")
public class MaquinaController {

    @Autowired
    private MaquinaService maquinaService;

    // GET /maquinas → lista todas las máquinas
    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("maquinas", maquinaService.listarTodas());
        modelo.addAttribute("alertas", maquinaService.listarQueNecesitanMantenimiento());
        modelo.addAttribute("estados", EstadoMaquina.values());
        return "maquinas/lista";
    }

    // GET /maquinas/nueva → formulario para registrar una máquina
    @GetMapping("/nueva")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioNuevo(Model modelo) {
        modelo.addAttribute("maquina", new Maquina());
        modelo.addAttribute("titulo", "Nueva Máquina");
        modelo.addAttribute("estados", EstadoMaquina.values());
        return "maquinas/formulario";
    }

    // GET /maquinas/editar/{id} → formulario para editar
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formularioEditar(@PathVariable Long id, Model modelo,
                                   RedirectAttributes flash) {
        Maquina maquina = maquinaService.buscarPorId(id).orElse(null);
        if (maquina == null) {
            flash.addFlashAttribute("error", "Máquina no encontrada.");
            return "redirect:/maquinas";
        }
        modelo.addAttribute("maquina", maquina);
        modelo.addAttribute("titulo", "Editar Máquina");
        modelo.addAttribute("estados", EstadoMaquina.values());
        return "maquinas/formulario";
    }

    // POST /maquinas/guardar → guarda la máquina
    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@ModelAttribute Maquina maquina, RedirectAttributes flash) {
        maquinaService.guardar(maquina);
        flash.addFlashAttribute("exito", "Máquina guardada correctamente.");
        return "redirect:/maquinas";
    }

    // GET /maquinas/mantenimiento/{id} → registra mantenimiento hoy
    @GetMapping("/mantenimiento/{id}")
    public String registrarMantenimiento(@PathVariable Long id, RedirectAttributes flash) {
        maquinaService.registrarMantenimiento(id);
        flash.addFlashAttribute("exito", "Mantenimiento registrado. Máquina vuelve a OPERATIVA.");
        return "redirect:/maquinas";
    }

    // GET /maquinas/uso/{id} → registra un uso de la máquina
    @GetMapping("/uso/{id}")
    public String registrarUso(@PathVariable Long id, RedirectAttributes flash) {
        maquinaService.registrarUso(id);
        flash.addFlashAttribute("exito", "Uso registrado.");
        return "redirect:/maquinas";
    }

    // GET /maquinas/eliminar/{id} → elimina una máquina
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        maquinaService.eliminar(id);
        flash.addFlashAttribute("exito", "Máquina eliminada.");
        return "redirect:/maquinas";
    }
}
