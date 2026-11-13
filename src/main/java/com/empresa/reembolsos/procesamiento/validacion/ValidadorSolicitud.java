package com.empresa.reembolsos.procesamiento.validacion;

import com.empresa.reembolsos.dominio.SolicitudReembolso;
import java.util.List;

public abstract class ValidadorSolicitud {

    private ValidadorSolicitud siguiente;

    public ValidadorSolicitud siguiente(
            ValidadorSolicitud siguiente) {

        this.siguiente = siguiente;
        return siguiente;
    }

    public ResultadoValidacion validar(
            SolicitudReembolso solicitud,
            List<SolicitudReembolso> pendientes) {

        ResultadoValidacion resultado =
                validarPropio(solicitud, pendientes);

        if (!resultado.valida() || siguiente == null) {
            return resultado;
        }

        return siguiente.validar(solicitud, pendientes);
    }

    protected abstract ResultadoValidacion validarPropio(
            SolicitudReembolso solicitud,
            List<SolicitudReembolso> pendientes);
}