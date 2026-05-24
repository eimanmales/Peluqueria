package com.ProgramacionV.Peluqueria.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    // Contador para el sistema de descuentos acumulativos
    @Column(name = "cortes_acumulados", nullable = false)
    @Builder.Default
    private Integer cortesAcumulados = 0;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
