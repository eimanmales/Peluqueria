package com.ProgramacionV.Peluqueria.repository;

import com.ProgramacionV.Peluqueria.entity.Cita;
import com.ProgramacionV.Peluqueria.entity.EstadoCita;
import com.ProgramacionV.Peluqueria.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstado(EstadoCita estado);
    List<Cita> findByPeluqueroAndEstado(Usuario peluquero, EstadoCita estado);
    List<Cita> findByEstadoInOrderByFechaHoraAsc(List<EstadoCita> estados);

    @Query("SELECT c FROM Cita c WHERE c.peluquero = :peluquero " +
           "AND c.fechaHora BETWEEN :inicio AND :fin ORDER BY c.fechaHora ASC")
    List<Cita> findCitasDelPeluqueroPorFecha(
            @Param("peluquero") Usuario peluquero,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
