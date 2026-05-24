package com.ProgramacionV.Peluqueria.repository;

import com.ProgramacionV.Peluqueria.entity.Cliente;
import com.ProgramacionV.Peluqueria.entity.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
    List<Descuento> findByClienteAndActivoTrue(Cliente cliente);
    List<Descuento> findByActivoTrue();
}
