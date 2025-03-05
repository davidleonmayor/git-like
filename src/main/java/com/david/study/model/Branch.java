package com.david.study.model;

import java.time.LocalDate;

public class Branch extends Commit {
    public Branch(LocalDate fecha, String id, String mensaje) {
        super(fecha, id, mensaje);
    }
}
