package com.ProgramacionV.Peluqueria.service;

import com.ProgramacionV.Peluqueria.entity.EstadoMaquina;
import com.ProgramacionV.Peluqueria.entity.Maquina;
import com.ProgramacionV.Peluqueria.repository.MaquinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MaquinaService {

    @Autowired
    private MaquinaRepository maquinaRepository;

    public List<Maquina> listarTodas() {
        return maquinaRepository.findByOrderByNombreAsc();
    }

    public Optional<Maquina> buscarPorId(Long id) {
        return maquinaRepository.findById(id);
    }

    public Maquina guardar(Maquina maquina) {
        return maquinaRepository.save(maquina);
    }

    public void eliminar(Long id) {
        maquinaRepository.deleteById(id);
    }

    // Registra un uso de la máquina e incrementa el contador
    public void registrarUso(Long id) {
        maquinaRepository.findById(id).ifPresent(m -> {
            m.setUsosTotales(m.getUsosTotales() + 1);
            maquinaRepository.save(m);
        });
    }

    // Registra que se hizo mantenimiento hoy y vuelve a OPERATIVA
    public void registrarMantenimiento(Long id) {
        maquinaRepository.findById(id).ifPresent(m -> {
            m.setFechaUltimoMantenimiento(LocalDate.now());
            m.setEstado(EstadoMaquina.OPERATIVA);
            maquinaRepository.save(m);
        });
    }

    // Cambia el estado de una máquina
    public void cambiarEstado(Long id, EstadoMaquina nuevoEstado) {
        maquinaRepository.findById(id).ifPresent(m -> {
            m.setEstado(nuevoEstado);
            maquinaRepository.save(m);
        });
    }

    // Retorna las máquinas que necesitan mantenimiento
    public List<Maquina> listarQueNecesitanMantenimiento() {
        return maquinaRepository.findAll().stream()
                .filter(Maquina::necesitaMantenimiento)
                .toList();
    }
}
