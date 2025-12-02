package com.dam.accesodatos.miprimeraapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tarea {

    private int id;
    private String titulo;
    private boolean completada;
}
