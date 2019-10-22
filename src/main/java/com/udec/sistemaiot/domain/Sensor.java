/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udec.sistemaiot.domain;

import org.springframework.data.annotation.Id;

/**
 *
 * @author PedroD
 */
public class Sensor {
    @Id
    private long id;
    private String nombre;
    private String descripcion;
    private String puerto;

    public Sensor() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    @Override
    public String toString() {
        return "Sensor{" + "id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", puerto=" + puerto + '}';
    }

}
