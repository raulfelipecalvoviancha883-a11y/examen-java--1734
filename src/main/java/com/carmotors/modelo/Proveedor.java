package com.carmotors.modelo;

import java.util.ArrayList;
import java.util.List;

public class Proveedor {

    private Integer id;
    private String nombre;
    private String identificacionFiscal;
    private String contacto;
    private String telefono;
    private String correo;
    private String direccion;
    private List<String> suministrosOfrecidos = new ArrayList<>();

    public Proveedor() {
    }

    public Proveedor(String nombre, String contacto) {
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public Proveedor(Integer id, String nombre, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public Proveedor(Integer id, String nombre, String identificacionFiscal, String contacto, String telefono, String correo, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.identificacionFiscal = identificacionFiscal;
        this.contacto = contacto;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.suministrosOfrecidos = new ArrayList<>();
    }

    public Proveedor(Integer id, String nombre, String identificacionFiscal, String contacto, String telefono, String correo, String direccion, List<String> suministrosOfrecidos) {
        this.id = id;
        this.nombre = nombre;
        this.identificacionFiscal = identificacionFiscal;
        this.contacto = contacto;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.suministrosOfrecidos = (suministrosOfrecidos != null) ? new ArrayList<>(suministrosOfrecidos) : new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacionFiscal() {
        return identificacionFiscal;
    }

    public void setIdentificacionFiscal(String identificacionFiscal) {
        this.identificacionFiscal = identificacionFiscal;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<String> getSuministrosOfrecidos() {
        return suministrosOfrecidos;
    }

    public void setSuministrosOfrecidos(List<String> suministrosOfrecidos) {
        this.suministrosOfrecidos = (suministrosOfrecidos != null) ? new ArrayList<>(suministrosOfrecidos) : new ArrayList<>();
    }

    public void agregarSuministro(String suministro) {
        if (suministro != null && !suministro.trim().isEmpty()) {
            if (this.suministrosOfrecidos == null) {
                this.suministrosOfrecidos = new ArrayList<>();
            }
            this.suministrosOfrecidos.add(suministro.trim());
        }
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", identificacionFiscal='" + identificacionFiscal + '\'' +
                ", contacto='" + contacto + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correo='" + correo + '\'' +
                ", direccion='" + direccion + '\'' +
                ", suministrosOfrecidos=" + suministrosOfrecidos +
                '}';
    }
}
