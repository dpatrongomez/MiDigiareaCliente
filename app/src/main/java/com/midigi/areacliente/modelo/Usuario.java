package com.midigi.areacliente.modelo;

public class Usuario {
    private String internet;
    private String minutos;
    private String euros;
    private String num_telf;

    public Usuario(String internet, String minutos, String euros, String num_telf) {
        this.internet = internet;
        this.minutos = minutos;
        this.euros=euros;
        this.num_telf=num_telf;
    }

    public String getEuros() {
        return euros;
    }

    public String getNum_telf() {
        return num_telf;
    }

    public String getInternet() {
        return internet;
    }

    public String getMinutos() {
        return minutos;
    }
}
