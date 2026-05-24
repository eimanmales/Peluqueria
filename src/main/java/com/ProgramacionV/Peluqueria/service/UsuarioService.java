package com.ProgramacionV.Peluqueria.service;

import com.ProgramacionV.Peluqueria.entity.Bitacora;
import com.ProgramacionV.Peluqueria.entity.Rol;
import com.ProgramacionV.Peluqueria.entity.Usuario;
import com.ProgramacionV.Peluqueria.repository.BitacoraRepository;
import com.ProgramacionV.Peluqueria.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BitacoraRepository bitacoraRepository;

    // ── CRUD ────────────────────────────────────────────────

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    public List<Usuario> listarPeluqueros() {
        return usuarioRepository.findByRolAndActivoTrue(Rol.PELUQUERO);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void desactivar(Long id) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setActivo(false);
            usuarioRepository.save(u);
        });
    }

    public boolean existeUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    // ── BITÁCORA ─────────────────────────────────────────────

    /**
     * Registra una acción en la bitácora del sistema.
     *
     * @param usuario  Usuario que realizó la acción (puede ser null)
     * @param accion   Código breve de la acción (ej: "LOGIN", "CREAR_CITA")
     * @param detalle  Descripción más larga
     * @param request  Para capturar la IP del cliente
     */
    @Transactional
    public void registrarEnBitacora(Usuario usuario, String accion, String detalle,
                                     HttpServletRequest request) {
        String ip = obtenerIpCliente(request);
        Bitacora entrada = Bitacora.builder()
                .usuario(usuario)
                .accion(accion)
                .detalle(detalle)
                .ipAddress(ip)
                .build();
        bitacoraRepository.save(entrada);
    }

    @Transactional
    public void registrarEnBitacora(Usuario usuario, String accion, String detalle) {
        Bitacora entrada = Bitacora.builder()
                .usuario(usuario)
                .accion(accion)
                .detalle(detalle)
                .build();
        bitacoraRepository.save(entrada);
    }

    public List<Bitacora> listarBitacora() {
        return bitacoraRepository.findTop50ByOrderByFechaHoraDesc();
    }

    // ── UTILIDADES ───────────────────────────────────────────

    private String obtenerIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
