package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.Cita;
import com.ProgramacionV.Peluqueria.entity.EstadoCita;
import com.ProgramacionV.Peluqueria.entity.Rol;
import com.ProgramacionV.Peluqueria.service.CitaService;
import com.ProgramacionV.Peluqueria.service.ClienteService;
import com.ProgramacionV.Peluqueria.service.ServicioService;
import com.ProgramacionV.Peluqueria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/citas")
@PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
public class CitaController {

    @Autowired private CitaService citaService;
    @Autowired private ClienteService clienteService;
    @Autowired private ServicioService servicioService;
    @Autowired private UsuarioService usuarioService;

    // GET /citas → lista todas las citas
    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("citas", citaService.listarTodas());
        modelo.addAttribute("estados", EstadoCita.values());
        return "citas/lista";
    }

    // GET /citas/turnos → vista de turnos en tiempo real con tiempo de espera
    @GetMapping("/turnos")
    public String turnos(Model modelo) {
        modelo.addAttribute("citasActivas", citaService.listarCitasActivas());
        return "citas/turnos";
    }

    // GET /citas/nueva → formulario para agendar una nueva cita
    @GetMapping("/nueva")
    public String formularioNuevo(Model modelo) {
        modelo.addAttribute("cita", new Cita());
        modelo.addAttribute("titulo", "Nueva Cita");
        cargarDatosFormulario(modelo);
        return "citas/formulario";
    }

    // GET /citas/editar/{id} → formulario para editar una cita
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model modelo,
                                   RedirectAttributes flash) {
        Cita cita = citaService.buscarPorId(id).orElse(null);
        if (cita == null) {
            flash.addFlashAttribute("error", "Cita no encontrada.");
            return "redirect:/citas";
        }
        if (cita.getEstado() == EstadoCita.COMPLETADA || cita.getEstado() == EstadoCita.CANCELADA) {
            flash.addFlashAttribute("error", "No se puede editar una cita " + cita.getEstado() + ".");
            return "redirect:/citas";
        }
        modelo.addAttribute("cita", cita);
        modelo.addAttribute("titulo", "Editar Cita");
        cargarDatosFormulario(modelo);
        return "citas/formulario";
    }

    // POST /citas/guardar → guarda la cita (nueva o editada)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cita cita, RedirectAttributes flash) {
        citaService.guardar(cita);
        flash.addFlashAttribute("exito",
                "Cita agendada. Tiempo de espera estimado: " + cita.getTiempoEsperaMin() + " min.");
        return "redirect:/citas";
    }

    // GET /citas/estado/{id}/{estado} → cambia el estado de una cita
    @GetMapping("/estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable Long id,
                                @PathVariable EstadoCita estado,
                                RedirectAttributes flash) {
        citaService.cambiarEstado(id, estado);
        flash.addFlashAttribute("exito", "Estado actualizado a: " + estado);
        return "redirect:/citas";
    }

    // GET /citas/cancelar/{id} → cancela una cita
    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable Long id, RedirectAttributes flash) {
        citaService.cancelar(id);
        flash.addFlashAttribute("exito", "Cita cancelada.");
        return "redirect:/citas";
    }

    // Carga clientes, servicios y peluqueros en el modelo del formulario
    private void cargarDatosFormulario(Model modelo) {
        modelo.addAttribute("clientes",   clienteService.listarTodos());
        modelo.addAttribute("servicios",  servicioService.listarActivos());
        modelo.addAttribute("peluqueros", usuarioService.listarPeluqueros());
    }
}
