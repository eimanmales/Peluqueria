package com.ProgramacionV.Peluqueria.service;

import com.ProgramacionV.Peluqueria.entity.Cita;
import com.ProgramacionV.Peluqueria.entity.Pago;
import com.ProgramacionV.Peluqueria.entity.Cliente;
import com.ProgramacionV.Peluqueria.entity.Descuento;
import com.ProgramacionV.Peluqueria.repository.CitaRepository;
import com.ProgramacionV.Peluqueria.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;
    
    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private DescuentoService descuentoService;

    // ── Consultas ────────────────────────────────────────────

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public List<Pago> listarPorPeriodo(LocalDate inicio, LocalDate fin) {
        return pagoRepository.findByFechaBetweenOrderByFechaDesc(
                inicio.atStartOfDay(),
                fin.plusDays(1).atStartOfDay());
    }

    public Optional<Pago> buscarPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public BigDecimal totalDelDia() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia    = inicioDia.plusDays(1);
        return pagoRepository.totalRecaudadoEnPeriodo(inicioDia, finDia);
    }

    public BigDecimal totalPorPeriodo(LocalDate inicio, LocalDate fin) {
        return pagoRepository.totalRecaudadoEnPeriodo(
                inicio.atStartOfDay(),
                fin.plusDays(1).atStartOfDay());
    }

    // ── Registro de pago ─────────────────────────────────────

    /**
     * Registra un pago, genera el número de factura automáticamente
     * y suma un corte acumulado al cliente de la cita.
     */
    
    
    @Transactional
    public Pago registrar(Pago pago) {

        if (pago.getCita() != null) {

            // 🔥 VOLVER A TRAER CITA COMPLETA
            Cita cita = citaRepository.findById(
                    pago.getCita().getId()
            ).orElseThrow();

            Cliente cliente = cita.getCliente();

            Descuento descuento = descuentoService
                    .buscarDescuentoAplicable(cliente.getId());

            if (descuento != null) {

                BigDecimal montoOriginal = pago.getMonto();

                BigDecimal descuentoValor = montoOriginal
                        .multiply(descuento.getPorcentaje())
                        .divide(BigDecimal.valueOf(100));

                pago.setMonto(montoOriginal.subtract(descuentoValor));
            }

            // ✅ asignar cita real al pago
            pago.setCita(cita);
        }

        pago.setNumeroFactura("FAC-" + System.currentTimeMillis());

        return pagoRepository.save(pago);
    }



    // ── Generación de número de factura ──────────────────────

    /**
     * Formato: FAC-YYYYMMDD-XXXXX
     * Ej:      FAC-20260524-00042
     */
    private String generarNumeroFactura() {
        String fecha      = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long   correlativo = pagoRepository.count() + 1;
        return String.format("FAC-%s-%05d", fecha, correlativo);
    }

    // ── Cálculo de monto con descuento aplicado ───────────────

    /**
     * Aplica un porcentaje de descuento al monto original.
     * Ej: monto=50000, porcentaje=10  →  45000
     */
    public BigDecimal aplicarDescuento(BigDecimal montoOriginal, BigDecimal porcentaje) {
        if (porcentaje == null || porcentaje.compareTo(BigDecimal.ZERO) == 0) {
            return montoOriginal;
        }
        BigDecimal factor    = BigDecimal.ONE.subtract(porcentaje.divide(BigDecimal.valueOf(100)));
        return montoOriginal.multiply(factor).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
