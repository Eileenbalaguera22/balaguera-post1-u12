package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.SolicitudReembolso;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidadorDocumentosSoporte
        extends ValidadorSolicitud {

    @Override
    protected ResultadoValidacion validarPropio(
            SolicitudReembolso solicitud,
            List<SolicitudReembolso> pendientes) {

        if (!solicitud.isDocumentosSoporte()) {
            return ResultadoValidacion.rechazar(
                    "Faltan documentos soporte");
        }

        return ResultadoValidacion.ok();
    }
}
