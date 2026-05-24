package com.ProgramacionV.Peluqueria.service;

import com.ProgramacionV.Peluqueria.entity.Cliente;
import com.ProgramacionV.Peluqueria.entity.Descuento;
import com.ProgramacionV.Peluqueria.repository.ClienteRepository;
import com.ProgramacionV.Peluqueria.repository.DescuentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DescuentoService {

    @Autowired
    private DescuentoRepository descuentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // ── Consultas ────────────────────────────────────────────

    public List<Descuento> listarTodos() {
        return descuentoRepository.findAll();
    }

    public List<Descuento> listarActivos() {
        return descuentoRepository.findByActivoTrue();
    }

    public Optional<Descuento> buscarPorId(Long id) {
        return descuentoRepository.findById(id);
    }

    /** Retorna descuentos vigentes del cliente (activos y dentro de fecha). */
    public List<Descuento> descuentosVigentesDelCliente(Cliente cliente) {
        return descuentoRepository.findByClienteAndActivoTrue(cliente)
                .stream()
                .filter(Descuento::esVigente)
                .toList();
    }

    // ── Lógica de negocio ────────────────────────────────────

    /**
     * Verifica si el cliente ha acumulado suficientes cortes para
     * activar algún descuento automático.
     * Retorna el primer descuento aplicable, o null si no hay ninguno.
     */
    public Descuento buscarDescuentoAplicable(Long clienteId) {
        return clienteRepository.findById(clienteId).map(cliente -> {
            int cortes = cliente.getCortesAcumulados();
            return descuentoRepository.findByClienteAndActivoTrue(cliente)
                    .stream()
                    .filter(Descuento::esVigente)
                    .filter(d -> cortes >= d.getCortesRequeridos())
                    .findFirst()
                    .orElse(null);
        }).orElse(null);
    }

    /**
     * Verifica todos los clientes y activa los descuentos que
     * corresponden según sus cortes acumulados.
     * Se puede llamar manualmente o programar con @Scheduled.
     */
    public int verificarDescuentosAutomaticos() {
        List<Cliente> clientes = clienteRepository.findAll();
        int activados = 0;
        for (Cliente cliente : clientes) {
            List<Descuento> pendientes = descuentoRepository.findByClienteAndActivoTrue(cliente)
                    .stream()
                    .filter(d -> !d.esVigente())
                    .toList();
            // Por ahora solo contamos los elegibles (la activación es manual vía UI)
        }
        return activados;
    }

    // ── CRUD ─────────────────────────────────────────────────

    public Descuento guardar(Descuento descuento) {
        return descuentoRepository.save(descuento);
    }

    public void toggleActivo(Long id) {
        descuentoRepository.findById(id).ifPresent(d -> {
            d.setActivo(!d.getActivo());
            descuentoRepository.save(d);
        });
    }

    public void eliminar(Long id) {
        descuentoRepository.deleteById(id);
    }
}
