package com.midigi.areacliente.modelo;

public class Usuario {
    private String internet;
    private String minutos;

    public Usuario(String internet, String minutos) {
        this.internet = internet;
        this.minutos = minutos;
    }

    public String getInternet() {
        return internet;
    }

    public String getMinutos() {
        return minutos;
    }
}
