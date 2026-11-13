package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.springframework.stereotype.Component;
import com.empresa.reembolsos.infraestructura.RepositorioSolicitudesJpa;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

@Component
public class ValidadorDuplicados extends ValidadorSolicitud {

    @Override
    protected ResultadoValidacion validarPropio(
            SolicitudReembolso solicitud,
            List<SolicitudReembolso> pendientes) {

        YearMonth periodoSolicitud =
                YearMonth.from(solicitud.getFechaGasto());

        boolean existeDuplicado = pendientes.stream()
                .anyMatch(p ->
                        Objects.equals(
                                p.getEmpleadoId(),
                                solicitud.getEmpleadoId())
                        &&
                        p.getCategoria() == solicitud.getCategoria()
                        &&
                        YearMonth.from(p.getFechaGasto())
                                .equals(periodoSolicitud)
                );

        if (existeDuplicado) {
            return ResultadoValidacion.rechazar(
                    "Ya existe una solicitud pendiente "
                            + "del mismo empleado, categoría y período");
        }

        return ResultadoValidacion.ok();
    }
}