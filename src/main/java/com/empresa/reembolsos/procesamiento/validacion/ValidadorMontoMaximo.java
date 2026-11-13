
package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.CategoriaGasto;
import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ValidadorMontoMaximo extends ValidadorSolicitud {

    private static final BigDecimal TOPE_DIARIO_VIAJE =
            new BigDecimal("150000");

    @Override
    protected ResultadoValidacion validarPropio(
            SolicitudReembolso solicitud,
            List<SolicitudReembolso> pendientes) {

        if (solicitud.getMontoSolicitado() == null
                || solicitud.getMontoSolicitado().signum() <= 0) {
            return ResultadoValidacion.rechazar(
                    "El monto debe ser mayor que cero");
        }

        if (solicitud.getCategoria() == CategoriaGasto.VIAJE) {

            if (solicitud.getDiasViaje() <= 0) {
                return ResultadoValidacion.rechazar(
                        "Los días de viaje deben ser mayores que cero");
            }

            BigDecimal topeTotal = TOPE_DIARIO_VIAJE.multiply(
                    BigDecimal.valueOf(solicitud.getDiasViaje()));

            if (solicitud.getMontoSolicitado().compareTo(topeTotal) > 0) {
                return ResultadoValidacion.rechazar(
                        "Monto excede el tope de la categoría VIAJE");
            }
        }

        return ResultadoValidacion.ok();
    }
}
