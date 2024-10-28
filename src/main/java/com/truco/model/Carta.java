package com.truco.model;

public class Carta {
    //Atributos de una carta, un palo (simbolito) y valor
    private String palo;
    private int valor;

    //Constructor de la clase
    public Carta(String palo, int valor) {
        this.palo = palo;
        this.valor = valor;
    }

    //Metodos get y set de los atributos
    public String getPalo() {
        return palo;
    }

    public void setPalo(String palo) {
        this.palo = palo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    @Override
    
    public String toString() {
    
        String abreviaturaPalo;
        //abreviamos los palos a letras para que entren en las cartas
        switch (palo.toLowerCase()) {
            case "espadas":
                abreviaturaPalo = "E";
                break;
            case "oro":
                abreviaturaPalo = "O";
                break;
            case "bastos":
                abreviaturaPalo = "B";
                break; 
            case "copas":
                abreviaturaPalo = "C";
                break;
            default:
                abreviaturaPalo = " "; 
        }
        return valor + " " + abreviaturaPalo;  // Ejemplo: "7E" para "7 de Espadas"
    }
}
