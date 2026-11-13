package com.empresa.reembolsos.procesamiento.notificacion;

import com.empresa.reembolsos.dominio.EstadoSolicitud;
import com.empresa.reembolsos.dominio.SolicitudReembolso;

/**
 * Contrato para los interesados en cambios de estado.
 */
public interface ObservadorCambioEstado {

    void onCambioEstado(
            SolicitudReembolso solicitud,
            EstadoSolicitud estadoAnterior);
}