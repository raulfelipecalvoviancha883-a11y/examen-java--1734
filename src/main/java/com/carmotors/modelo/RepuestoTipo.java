package com.carmotors.modelo;

import java.util.Objects;

public class RepuestoTipo {

    private Integer id;
    private String nombre;

    public RepuestoTipo() {
    }

    public RepuestoTipo(String nombre) {
        this.nombre = nombre;
    }

    public RepuestoTipo(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepuestoTipo that = (RepuestoTipo) o;
        return Objects.equals(id, that.id) && Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre);
    }

    @Override
    public String toString() {
        return "RepuestoTipo{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
