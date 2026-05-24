package com.ProgramacionV.Peluqueria.repository;

import com.ProgramacionV.Peluqueria.entity.Bitacora;
import com.ProgramacionV.Peluqueria.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {
    List<Bitacora> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);
    List<Bitacora> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);
    List<Bitacora> findTop50ByOrderByFechaHoraDesc();
}
