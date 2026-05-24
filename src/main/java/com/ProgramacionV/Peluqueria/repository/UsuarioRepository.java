package com.ProgramacionV.Peluqueria.repository;

import com.ProgramacionV.Peluqueria.entity.Rol;
import com.ProgramacionV.Peluqueria.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByActivoTrue();
    List<Usuario> findByRolAndActivoTrue(Rol rol);
    boolean existsByUsername(String username);
}
