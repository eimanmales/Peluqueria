package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.Rol;
import com.ProgramacionV.Peluqueria.entity.Usuario;
import com.ProgramacionV.Peluqueria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    // ── USUARIOS ────────────────────────────────────────────

    // GET /admin/usuarios → lista todos los usuarios
    @GetMapping("/usuarios")
    public String listarUsuarios(Model modelo) {
        modelo.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/usuarios";
    }

    // GET /admin/usuarios/nuevo → formulario para crear usuario
    @GetMapping("/usuarios/nuevo")
    public String formularioNuevo(Model modelo) {
        modelo.addAttribute("usuario", new Usuario());
        modelo.addAttribute("titulo", "Nuevo Usuario");
        modelo.addAttribute("roles", Rol.values());
        return "admin/formulario-usuario";
    }

    // GET /admin/usuarios/editar/{id} → formulario para editar usuario
    @GetMapping("/usuarios/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model modelo,
                                   RedirectAttributes flash) {
        Usuario usuario = usuarioService.buscarPorId(id).orElse(null);
        if (usuario == null) {
            flash.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/admin/usuarios";
        }
        modelo.addAttribute("usuario", usuario);
        modelo.addAttribute("titulo", "Editar Usuario");
        modelo.addAttribute("roles", Rol.values());
        return "admin/formulario-usuario";
    }

    // POST /admin/usuarios/guardar → guarda el usuario
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 RedirectAttributes flash) {
        // Verificar si el username ya existe (solo para usuarios nuevos)
        if (usuario.getId() == null && usuarioService.existeUsername(usuario.getUsername())) {
            flash.addFlashAttribute("error",
                    "El nombre de usuario '" + usuario.getUsername() + "' ya está en uso.");
            return "redirect:/admin/usuarios/nuevo";
        }
        usuarioService.guardar(usuario);
        flash.addFlashAttribute("exito", "Usuario guardado correctamente.");
        return "redirect:/admin/usuarios";
    }

    // GET /admin/usuarios/desactivar/{id} → desactiva un usuario
    @GetMapping("/usuarios/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id, RedirectAttributes flash) {
        usuarioService.desactivar(id);
        flash.addFlashAttribute("exito", "Usuario desactivado.");
        return "redirect:/admin/usuarios";
    }

    // ── BITÁCORA ────────────────────────────────────────────

    // GET /admin/bitacora → muestra los últimos 50 registros
    @GetMapping("/bitacora")
    public String verBitacora(Model modelo) {
        modelo.addAttribute("registros", usuarioService.listarBitacora());
        return "admin/bitacora";
    }
}
