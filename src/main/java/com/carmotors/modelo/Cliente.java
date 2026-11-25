package com.carmotors.modelo;

public class Cliente {
    private Integer id;
    private String nombre;
    private String documentoIdentidad;
    private String telefono;
    private String correoElectronico;

    // Constructor vacío
    public Cliente() {
    }

    // Constructor sin ID (ideal para cuando vas a registrar un cliente nuevo que aún no tiene ID autogenerado)
    public Cliente(String nombre, String documentoIdentidad, String telefono, String correoElectronico) {
        this.nombre = nombre;
        this.documentoIdentidad = documentoIdentidad;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }

    // Constructor completo (ideal para cuando traes los datos desde la base de datos)
    public Cliente(Integer id, String nombre, String documentoIdentidad, String telefono, String correoElectronico) {
        this.id = id;
        this.nombre = nombre;
        this.documentoIdentidad = documentoIdentidad;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }

    // Getters y Setters
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

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    // Métodos alias para compatibilidad
    public String getEmail() {
        return correoElectronico;
    }

    public void setEmail(String email) {
        this.correoElectronico = email;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", documentoIdentidad='" + documentoIdentidad + '\'' +
                ", telefono='" + telefono + '\'' +
                ", correoElectronico='" + correoElectronico + '\'' +
                '}';
    }
}