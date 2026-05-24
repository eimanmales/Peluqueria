package com.ProgramacionV.Peluqueria.service;

import com.ProgramacionV.Peluqueria.entity.Servicio;
import com.ProgramacionV.Peluqueria.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    // Retorna todos los servicios activos ordenados por nombre
    public List<Servicio> listarActivos() {
        return servicioRepository.findByActivoTrueOrderByNombreAsc();
    }

    // Retorna TODOS los servicios (para el admin)
    public List<Servicio> listarTodos() {
        return servicioRepository.findAll();
    }

    // Busca un servicio por su id
    public Optional<Servicio> buscarPorId(Long id) {
        return servicioRepository.findById(id);
    }

    // Guarda un servicio nuevo o actualiza uno existente
    public Servicio guardar(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    // Desactiva un servicio en lugar de borrarlo (buena práctica)
    public void desactivar(Long id) {
        servicioRepository.findById(id).ifPresent(s -> {
            s.setActivo(false);
            servicioRepository.save(s);
        });
    }
}
