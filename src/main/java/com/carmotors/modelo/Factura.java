package com.carmotors.modelo;

import java.time.LocalDateTime;

/**
 * Entidad que representa una factura emitida para un servicio de taller finalizado.
 */
public class Factura {

    private Integer id;
    private int servicioId;
    private LocalDateTime fechaEmision;
    private double totalRepuestos;
    private double totalManoObra;
    private double impuestos;
    private double totalFinal;

    public Factura() {
    }

    public Factura(int servicioId, LocalDateTime fechaEmision,
                   double totalRepuestos, double totalManoObra,
                   double impuestos, double totalFinal) {
        this.servicioId = servicioId;
        this.fechaEmision = fechaEmision;
        this.totalRepuestos = totalRepuestos;
        this.totalManoObra = totalManoObra;
        this.impuestos = impuestos;
        this.totalFinal = totalFinal;
    }

    public Factura(Integer id, int servicioId, LocalDateTime fechaEmision,
                   double totalRepuestos, double totalManoObra,
                   double impuestos, double totalFinal) {
        this.id = id;
        this.servicioId = servicioId;
        this.fechaEmision = fechaEmision;
        this.totalRepuestos = totalRepuestos;
        this.totalManoObra = totalManoObra;
        this.impuestos = impuestos;
        this.totalFinal = totalFinal;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getServicioId() { return servicioId; }
    public void setServicioId(int servicioId) { this.servicioId = servicioId; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public double getTotalRepuestos() { return totalRepuestos; }
    public void setTotalRepuestos(double totalRepuestos) { this.totalRepuestos = totalRepuestos; }

    public double getTotalManoObra() { return totalManoObra; }
    public void setTotalManoObra(double totalManoObra) { this.totalManoObra = totalManoObra; }

    public double getImpuestos() { return impuestos; }
    public void setImpuestos(double impuestos) { this.impuestos = impuestos; }

    public double getTotalFinal() { return totalFinal; }
    public void setTotalFinal(double totalFinal) { this.totalFinal = totalFinal; }

    @Override
    public String toString() {
        return "Factura{" +
                "id=" + id +
                ", servicioId=" + servicioId +
                ", fechaEmision=" + fechaEmision +
                ", totalRepuestos=" + totalRepuestos +
                ", totalManoObra=" + totalManoObra +
                ", impuestos=" + impuestos +
                ", totalFinal=" + totalFinal +
                '}';
    }
}
