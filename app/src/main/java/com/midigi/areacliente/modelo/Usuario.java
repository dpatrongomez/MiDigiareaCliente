package com.midigi.areacliente.modelo;

public class Usuario {
    String tipo_usuario;
    private String internet;
    private String minutos;
    private String euros;
    private String num_telf;

    public Usuario(String tipo_usuario,String internet, String minutos, String euros, String num_telf) {
        this.tipo_usuario=tipo_usuario;
        this.internet = internet;
        this.minutos = minutos;
        this.euros=euros;
        this.num_telf=num_telf;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
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
