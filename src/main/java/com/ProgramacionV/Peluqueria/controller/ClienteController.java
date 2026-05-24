package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.Cliente;
import com.ProgramacionV.Peluqueria.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
@PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // GET /clientes → lista todos los clientes
    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model modelo) {
        if (buscar != null && !buscar.isBlank()) {
            modelo.addAttribute("clientes", clienteService.buscarPorNombre(buscar));
            modelo.addAttribute("buscar", buscar);
        } else {
            modelo.addAttribute("clientes", clienteService.listarTodos());
        }
        return "clientes/lista";
    }

    // GET /clientes/nuevo → formulario para crear un cliente
    @GetMapping("/nuevo")
    public String formularioNuevo(Model modelo) {
        modelo.addAttribute("cliente", new Cliente());
        modelo.addAttribute("titulo", "Nuevo Cliente");
        return "clientes/formulario";
    }

    // GET /clientes/editar/{id} → formulario para editar un cliente
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model modelo,
                                   RedirectAttributes flash) {
        Cliente cliente = clienteService.buscarPorId(id).orElse(null);
        if (cliente == null) {
            flash.addFlashAttribute("error", "Cliente no encontrado.");
            return "redirect:/clientes";
        }
        modelo.addAttribute("cliente", cliente);
        modelo.addAttribute("titulo", "Editar Cliente");
        return "clientes/formulario";
    }

    // POST /clientes/guardar → guarda el cliente (nuevo o editado)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Cliente cliente, RedirectAttributes flash) {
        clienteService.guardar(cliente);
        flash.addFlashAttribute("exito", "Cliente guardado correctamente.");
        return "redirect:/clientes";
    }

    // GET /clientes/eliminar/{id} → elimina un cliente
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        clienteService.eliminar(id);
        flash.addFlashAttribute("exito", "Cliente eliminado.");
        return "redirect:/clientes";
    }
}
