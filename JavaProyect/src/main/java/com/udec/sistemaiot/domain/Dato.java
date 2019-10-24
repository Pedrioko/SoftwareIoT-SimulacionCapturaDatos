package com.udec.sistemaiot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "datos")
public class Dato {


    @Transient
    public static final String SEQUENCE_NAME = "datos_sequence";

    @Id
    private long id;
    private double humedad;
    private double temperatura;
    private Date fecha_creacion;

    public Dato() {
    }

    public Dato(double humedad, double temperatura, Date fecha_creacion) {
        this.humedad = humedad;
        this.temperatura = temperatura;
        this.fecha_creacion = fecha_creacion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getHumedad() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad = humedad;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
    }

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    @Override
    public String toString() {
        return "Dato{" +
                "id=" + id +
                ", humedad=" + humedad +
                ", temperatura=" + temperatura +
                ", fecha_creacion=" + fecha_creacion +
                '}';
    }
}
