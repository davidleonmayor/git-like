package com.david.study;

import java.time.LocalDate;

public class Commit {
    private String id;
    private String mensaje;
    private LocalDate fecha;

    public Commit(LocalDate fecha, String id, String mensaje) {
        this.fecha = fecha;
        this.id = id;
        this.mensaje = mensaje;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
