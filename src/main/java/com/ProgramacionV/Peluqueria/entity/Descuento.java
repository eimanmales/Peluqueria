package com.ProgramacionV.Peluqueria.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "descuento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(length = 200)
    private String descripcion;

    // Porcentaje de descuento aplicado (ej: 10.00 = 10%)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    // Número de cortes necesarios para activar el descuento
    @Column(name = "cortes_requeridos", nullable = false)
    @Builder.Default
    private Integer cortesRequeridos = 10;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    // Verifica si el descuento está vigente hoy
    public boolean esVigente() {
        LocalDate hoy = LocalDate.now();
        if (fechaInicio != null && hoy.isBefore(fechaInicio)) return false;
        if (fechaFin != null && hoy.isAfter(fechaFin)) return false;
        return activo;
    }
}
