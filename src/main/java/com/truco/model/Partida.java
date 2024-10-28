package com.truco.model;

//libreria para trabajar con listas (de jugadores)
import java.util.List;

public class Partida {
    //atributos
    private List<Jugador> jugadores;
    private int puntajeMaximo;

    //constructor
    public Partida(List<Jugador> jugadores) {
        this.jugadores = jugadores;
        this.puntajeMaximo = 30; // Puntaje máximo por partida
    }
    //metodos get y set
    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public int getPuntajeMaximo() {
        return puntajeMaximo;
    }

    public void setPuntajeMaximo(int puntajeMaximo) {
        this.puntajeMaximo = puntajeMaximo;
    }
}
