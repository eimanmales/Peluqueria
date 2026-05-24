package com.ProgramacionV.Peluqueria.repository;

import com.ProgramacionV.Peluqueria.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFechaBetweenOrderByFechaDesc(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.fecha BETWEEN :inicio AND :fin")
    BigDecimal totalRecaudadoEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}
