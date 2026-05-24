package com.ProgramacionV.Peluqueria.controller;

import com.ProgramacionV.Peluqueria.entity.Usuario;
import com.ProgramacionV.Peluqueria.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String raiz() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Cargamos el usuario completo desde la BD para tener nombre, rol, etc.
        usuarioService.buscarPorUsername(userDetails.getUsername())
                .ifPresent(usuario -> model.addAttribute("usuarioActual", usuario));

        return "home";     // → templates/home.html
    }
}
