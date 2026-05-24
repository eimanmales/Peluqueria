package com.ProgramacionV.Peluqueria.service;

import com.ProgramacionV.Peluqueria.entity.Cita;
import com.ProgramacionV.Peluqueria.entity.EstadoCita;
import com.ProgramacionV.Peluqueria.entity.Usuario;
import com.ProgramacionV.Peluqueria.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    // Retorna todas las citas
    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    // Retorna citas PENDIENTE y EN_CURSO ordenadas por fecha (para la vista de turnos)
    public List<Cita> listarCitasActivas() {
        return citaRepository.findByEstadoInOrderByFechaHoraAsc(
                List.of(EstadoCita.PENDIENTE, EstadoCita.EN_CURSO));
    }

    // Retorna citas de un peluquero específico con un estado dado
    public List<Cita> listarPorPeluqueroYEstado(Usuario peluquero, EstadoCita estado) {
        return citaRepository.findByPeluqueroAndEstado(peluquero, estado);
    }

    // Busca una cita por su id
    public Optional<Cita> buscarPorId(Long id) {
        return citaRepository.findById(id);
    }

    // ──────────────────────────────────────────────────────────────
    //  CÁLCULO DE TIEMPO DE ESPERA
    //  Lógica: se suman las duraciones de los servicios de todas las
    //  citas PENDIENTE/EN_CURSO del mismo peluquero en el mismo día,
    //  que estén programadas ANTES de la nueva cita.
    // ──────────────────────────────────────────────────────────────
    public int calcularTiempoEspera(Usuario peluquero, LocalDateTime fechaHoraNueva) {

        // Inicio y fin del día de la nueva cita
        LocalDateTime inicioDia = fechaHoraNueva.toLocalDate().atStartOfDay();
        LocalDateTime finDia    = inicioDia.plusDays(1);

        // Citas activas del peluquero en ese día, anteriores a la nueva
        List<Cita> citasAnteriores = citaRepository
                .findCitasDelPeluqueroPorFecha(peluquero, inicioDia, fechaHoraNueva)
                .stream()
                .filter(c -> c.getEstado() == EstadoCita.PENDIENTE
                          || c.getEstado() == EstadoCita.EN_CURSO)
                .toList();

        // Se suman los minutos de duración de cada servicio
        return citasAnteriores.stream()
                .mapToInt(c -> c.getServicio().getDuracionMinutos())
                .sum();
    }

    // Guarda una cita nueva (calcula el tiempo de espera automáticamente)
    public Cita guardar(Cita cita) {
        int espera = calcularTiempoEspera(cita.getPeluquero(), cita.getFechaHora());
        cita.setTiempoEsperaMin(espera);
        return citaRepository.save(cita);
    }

    // Cambia el estado de una cita (PENDIENTE → EN_CURSO → COMPLETADA)
    public void cambiarEstado(Long id, EstadoCita nuevoEstado) {
        citaRepository.findById(id).ifPresent(c -> {
            c.setEstado(nuevoEstado);
            citaRepository.save(c);
        });
    }

    // Cancela una cita
    public void cancelar(Long id) {
        cambiarEstado(id, EstadoCita.CANCELADA);
    }
}
