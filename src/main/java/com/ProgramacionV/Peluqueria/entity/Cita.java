package com.ProgramacionV.Peluqueria.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cita")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;
    
    
    @OneToOne(mappedBy = "cita")
    private Pago pago;


    // El peluquero asignado a esta cita
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peluquero_id", nullable = false)
    private Usuario peluquero;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String notas;

    // Tiempo de espera calculado automáticamente en minutos
    @Column(name = "tiempo_espera_min")
    @Builder.Default
    private Integer tiempoEsperaMin = 0;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
