package com.mnn.alarma;

import android.net.Uri;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class Alarma {
    private String alarmaId;
    private String titulo;
    private int hora;
    private int minuto;
    private String tono;
    private List<String> dias;
    private List<Integer> diasNumeric;
    private String nota;
    private String uriTonePath;
    private boolean activado;

    public String getAlarmaId() {
        return alarmaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Alarma() {
    }

    public Alarma(String titulo, int hora, int minuto, String tono, List<String> dias, String nota, boolean activado, String tonoPath) {
        alarmaId = RandomStringUtils.randomNumeric(5);
        this.titulo = titulo;
        this.hora = hora;
        this.minuto = minuto;
        this.tono = tono;
        this.dias = dias;
        this.nota = nota;
        this.activado = activado;
        this.uriTonePath = tonoPath;
    }

    public void setAlarmaId(String alarmaId) {
        this.alarmaId = alarmaId;
    }

    public List<Integer> getDiasNumeric() {
        return diasNumeric;
    }

    public void setDiasNumeric(List<Integer> diasNumeric) {
        this.diasNumeric = diasNumeric;
    }

    public String getUriTonePath() {
        return uriTonePath;
    }

    public void setUriTonePath(String uriTonePath) {
        this.uriTonePath = uriTonePath;
    }

    public boolean isActivado() {
        return activado;
    }

    public void setActivado(boolean activado) {
        this.activado = activado;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }

    public String getTono() {
        return tono;
    }

    public void setTono(String tono) {
        this.tono = tono;
    }

    public List<String> getDias() {
        return dias;
    }

    public void setDias(List<String> dias) {
        this.dias = dias;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }


}
