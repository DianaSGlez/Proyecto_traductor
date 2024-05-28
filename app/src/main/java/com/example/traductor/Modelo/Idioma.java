package com.example.traductor.Modelo;

public class Idioma {
    String codigo_idioma;
    String titulo_idioma;

    public Idioma(String codigo_idioma, String titulo_idioma) {
        this.codigo_idioma = codigo_idioma;
        this.titulo_idioma = titulo_idioma;
    }

    public String getCodigo_idioma() {
        return codigo_idioma;
    }
    public void setCodigo_idioma(String codigo_idioma) {
        this.codigo_idioma = codigo_idioma;
    }

    public String getTitulo_idioma() {
        return titulo_idioma;
    }

    public void setTitulo_idioma(String titulo_idioma) {
        this.titulo_idioma = titulo_idioma;
    }
}
