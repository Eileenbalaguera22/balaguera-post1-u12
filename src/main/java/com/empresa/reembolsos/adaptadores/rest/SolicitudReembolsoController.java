package com.empresa.reembolsos.adaptadores.rest;

import com.empresa.reembolsos.aplicacion.SolicitudReembolsoService;
import com.empresa.reembolsos.dominio.EstadoSolicitud;
import com.empresa.reembolsos.dominio.SolicitudReembolso;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudReembolsoController {

    private final SolicitudReembolsoService servicio;

    public SolicitudReembolsoController(
            SolicitudReembolsoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<SolicitudReembolso> crear(
            @RequestBody SolicitudReembolso solicitud) {

        return ResponseEntity.ok(
                servicio.crear(solicitud));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<SolicitudReembolso> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request) {

        return ResponseEntity.ok(
                servicio.cambiarEstado(id, request.estado()));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudReembolso>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    public record CambiarEstadoRequest(
            EstadoSolicitud estado) {
    }
}