package com.midigi.areacliente.modelo;


public class Usuario {
    private String telefono;
    private String contraseña;

    public Usuario(String telefono, String contraseña) {
        this.telefono = telefono;
        this.contraseña = contraseña;
    }

    public Usuario() {
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    @Override
    public String toString() {
        return this.getTelefono();
    }
}
