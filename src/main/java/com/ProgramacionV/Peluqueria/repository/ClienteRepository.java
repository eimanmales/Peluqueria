package com.ProgramacionV.Peluqueria.repository;

import com.ProgramacionV.Peluqueria.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    List<Cliente> findByOrderByApellidoAsc();
}
