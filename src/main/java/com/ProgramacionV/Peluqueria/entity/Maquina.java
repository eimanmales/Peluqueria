package com.ProgramacionV.Peluqueria.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "maquina")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 80)
    private String tipo;

    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    @Column(name = "fecha_ultimo_mantenimiento")
    private LocalDate fechaUltimoMantenimiento;

    // Cada cuántos días se debe hacer mantenimiento
    @Column(name = "frecuencia_mantenimiento_dias", nullable = false)
    @Builder.Default
    private Integer frecuenciaMantenimientoDias = 30;

    @Column(name = "usos_totales", nullable = false)
    @Builder.Default
    private Integer usosTotales = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoMaquina estado = EstadoMaquina.OPERATIVA;

    // Retorna true si la máquina necesita mantenimiento según la fecha
    public boolean necesitaMantenimiento() {
        if (fechaUltimoMantenimiento == null) return true;
        return fechaUltimoMantenimiento
                .plusDays(frecuenciaMantenimientoDias)
                .isBefore(LocalDate.now());
    }
}
