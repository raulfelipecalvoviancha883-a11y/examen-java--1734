package com.carmotors.modelo;

import java.time.LocalDateTime;

public class Servicio {

    public static final int ESTADO_RECIBIDO = 1;
    public static final int ESTADO_EN_DIAGNOSTICO = 2;
    public static final int ESTADO_EN_PROCESO = 3;
    public static final int ESTADO_FINALIZADO = 4;
    public static final int ESTADO_CANCELADO = 5;

    private Integer id;
    private int vehiculoId;
    private int mantenimientoTipoId;
    private LocalDateTime fechaRecepcion;
    private LocalDateTime fechaEntrega;
    private String descripcionProblema;
    private double costoManoObra;
    private int estadoId;

    public Servicio() {
    }

    public Servicio(Integer id, int vehiculoId, int mantenimientoTipoId, LocalDateTime fechaRecepcion,
                    LocalDateTime fechaEntrega, String descripcionProblema, double costoManoObra, int estadoId) {
        this.id = id;
        this.vehiculoId = vehiculoId;
        this.mantenimientoTipoId = mantenimientoTipoId;
        this.fechaRecepcion = fechaRecepcion;
        this.fechaEntrega = fechaEntrega;
        this.descripcionProblema = descripcionProblema;
        this.costoManoObra = costoManoObra;
        this.estadoId = estadoId;
    }

    public Servicio(int vehiculoId, int mantenimientoTipoId, LocalDateTime fechaRecepcion,
                    LocalDateTime fechaEntrega, String descripcionProblema, double costoManoObra, int estadoId) {
        this(null, vehiculoId, mantenimientoTipoId, fechaRecepcion, fechaEntrega, descripcionProblema, costoManoObra, estadoId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(int vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public int getMantenimientoTipoId() {
        return mantenimientoTipoId;
    }

    public void setMantenimientoTipoId(int mantenimientoTipoId) {
        this.mantenimientoTipoId = mantenimientoTipoId;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public void setFechaRecepcion(LocalDateTime fechaRecepcion) {
        this.fechaRecepcion = fechaRecepcion;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getDescripcionProblema() {
        return descripcionProblema;
    }

    public void setDescripcionProblema(String descripcionProblema) {
        this.descripcionProblema = descripcionProblema;
    }

    public double getCostoManoObra() {
        return costoManoObra;
    }

    public void setCostoManoObra(double costoManoObra) {
        this.costoManoObra = costoManoObra;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }

    public String getNombreEstado() {
        return switch (this.estadoId) {
            case ESTADO_RECIBIDO -> "Recibido";
            case ESTADO_EN_DIAGNOSTICO -> "En Diagnóstico";
            case ESTADO_EN_PROCESO -> "En Proceso";
            case ESTADO_FINALIZADO -> "Finalizado";
            case ESTADO_CANCELADO -> "Cancelado";
            default -> "Desconocido (" + estadoId + ")";
        };
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "id=" + id +
                ", vehiculoId=" + vehiculoId +
                ", mantenimientoTipoId=" + mantenimientoTipoId +
                ", fechaRecepcion=" + fechaRecepcion +
                ", fechaEntrega=" + fechaEntrega +
                ", descripcionProblema='" + descripcionProblema + '\'' +
                ", costoManoObra=" + costoManoObra +
                ", estadoId=" + estadoId +
                " (" + getNombreEstado() + ")" +
                '}';
    }
}
