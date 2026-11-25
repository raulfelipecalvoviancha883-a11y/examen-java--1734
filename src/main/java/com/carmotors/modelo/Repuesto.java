package com.carmotors.modelo;

public class Repuesto {

    private Integer id;
    private String codigo;
    private String nombre;
    private int repuestoTipoId;
    private int proveedorId;
    private double precioCompra;
    private double precioVenta;
    private int stockActual;
    private int stockMinimo;
    private String ubicacion;

    public Repuesto() {
    }

    public Repuesto(String codigo, String nombre, int repuestoTipoId, int proveedorId,
                    double precioCompra, double precioVenta, int stockActual, int stockMinimo, String ubicacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.repuestoTipoId = repuestoTipoId;
        this.proveedorId = proveedorId;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.ubicacion = ubicacion;
    }

    public Repuesto(Integer id, String codigo, String nombre, int repuestoTipoId, int proveedorId,
                    double precioCompra, double precioVenta, int stockActual, int stockMinimo, String ubicacion) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.repuestoTipoId = repuestoTipoId;
        this.proveedorId = proveedorId;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.ubicacion = ubicacion;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getRepuestoTipoId() { return repuestoTipoId; }
    public void setRepuestoTipoId(int repuestoTipoId) { this.repuestoTipoId = repuestoTipoId; }

    public int getProveedorId() { return proveedorId; }
    public void setProveedorId(int proveedorId) { this.proveedorId = proveedorId; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    @Override
    public String toString() {
        return "Repuesto{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipoId=" + repuestoTipoId +
                ", proveedorId=" + proveedorId +
                ", compra=" + precioCompra +
                ", venta=" + precioVenta +
                ", stock=" + stockActual +
                ", min=" + stockMinimo +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }
}
