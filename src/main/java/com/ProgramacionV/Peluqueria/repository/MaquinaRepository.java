package com.ProgramacionV.Peluqueria.repository;

import com.ProgramacionV.Peluqueria.entity.EstadoMaquina;
import com.ProgramacionV.Peluqueria.entity.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaquinaRepository extends JpaRepository<Maquina, Long> {
    List<Maquina> findByEstado(EstadoMaquina estado);
    List<Maquina> findByOrderByNombreAsc();
}
