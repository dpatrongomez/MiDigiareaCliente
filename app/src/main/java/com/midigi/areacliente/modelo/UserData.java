package com.midigi.areacliente.modelo;

public class UserData {
    String tipo_usuario;
    private String internet;
    private String minutos;
    private String euros;
    private String num_telf;
    private String fecha_renovacion;

    public UserData(String tipo_usuario, String internet, String minutos, String euros, String num_telf, String fecha_renovacion) {
        this.tipo_usuario=tipo_usuario;
        this.internet = internet;
        this.minutos = minutos;
        this.euros=euros;
        this.num_telf=num_telf;
        this.fecha_renovacion=fecha_renovacion;
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

    public String getFecha_renovacion() {
        return fecha_renovacion;
    }
}
