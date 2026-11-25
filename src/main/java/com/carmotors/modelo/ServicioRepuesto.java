package com.carmotors.modelo;

public class ServicioRepuesto {

    private int servicioId;
    private int repuestoId;
    private int cantidadUsada;
    private double precioUnitarioVenta;

    public ServicioRepuesto() {
    }

    public ServicioRepuesto(int servicioId, int repuestoId, int cantidadUsada, double precioUnitarioVenta) {
        this.servicioId = servicioId;
        this.repuestoId = repuestoId;
        this.cantidadUsada = cantidadUsada;
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public int getServicioId() {
        return servicioId;
    }

    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

    public int getRepuestoId() {
        return repuestoId;
    }

    public void setRepuestoId(int repuestoId) {
        this.repuestoId = repuestoId;
    }

    public int getCantidadUsada() {
        return cantidadUsada;
    }

    public void setCantidadUsada(int cantidadUsada) {
        this.cantidadUsada = cantidadUsada;
    }

    public double getPrecioUnitarioVenta() {
        return precioUnitarioVenta;
    }

    public void setPrecioUnitarioVenta(double precioUnitarioVenta) {
        this.precioUnitarioVenta = precioUnitarioVenta;
    }

    public double getSubtotal() {
        return this.cantidadUsada * this.precioUnitarioVenta;
    }

    @Override
    public String toString() {
        return "ServicioRepuesto{" +
                "servicioId=" + servicioId +
                ", repuestoId=" + repuestoId +
                ", cantidadUsada=" + cantidadUsada +
                ", precioUnitarioVenta=" + precioUnitarioVenta +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
