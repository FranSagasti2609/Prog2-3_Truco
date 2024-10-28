package com.truco.controller;

import com.truco.model.Carta;
import com.truco.model.Jugador;
import com.truco.model.Partida;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JuegoController {
    private Partida partida;
    private List<Carta> mazo;

    //Constructor
    public JuegoController() {
        this.mazo = crearMazo();
        this.partida = null;
    }

    private List<Carta> crearMazo() {
        List<Carta> mazo = new ArrayList<>();
        String[] palos = {"Espadas", "Bastos", "Oro", "Copas"};
        int[] valores = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};

        for (String palo : palos) {
            for (int valor : valores) {
                mazo.add(new Carta(palo, valor));
            }
        }
        return mazo;
    }

    public void iniciarPartida(List<Jugador> jugadores) {
        if (jugadores.size() < 2) {
            throw new IllegalArgumentException("Debe haber al menos dos jugadores.");
        }
        this.partida = new Partida(jugadores);
        Collections.shuffle(mazo);
        repartirCartas(jugadores, 3); // Repartir 3 cartas a cada jugador
        System.out.println("Partida iniciada con " + jugadores.size() + " jugadores.");
    }

    public Carta repartirCarta() {
        if (mazo.isEmpty()) {
            throw new IllegalStateException("El mazo está vacío.");
        }
        return mazo.remove(0);
    }

    public void repartirCartas(List<Jugador> jugadores, int cartasPorJugador) {
        if (mazo.size() < cartasPorJugador * jugadores.size()) {
            throw new IllegalStateException("No hay suficientes cartas en el mazo.");
        }

        for (Jugador jugador : jugadores) {
            jugador.setCartas(new ArrayList<>()); // Reiniciar las cartas del jugador
            for (int i = 0; i < cartasPorJugador; i++) {
                jugador.getCartas().add(repartirCarta());
            }
        }
    }

    public boolean terminarPartida() {
        for(Jugador jugador : this.partida.getJugadores()){
            if(jugador.getPuntaje() >= this.partida.getPuntajeMaximo()){
                System.out.println("La partida ha terminado por puntaje maximo!!.");
            return true;
            }
        } return false;
    }
    
    public Jugador evaluarGanador(List<Carta> cartasJugadas, List<Jugador> jugadores) {
    if (cartasJugadas.size() != 2) {
        throw new IllegalArgumentException("La evaluación debe hacerse con exactamente dos cartas.");
    }

    // Lista de fuerza para determinar el puntaje
    String[] ordenFuerza = {
        "1 E", "1 B", "7 E", "7 O",
        "3", "2", "1", "12", "11", "10", "7", "6", "5", "4"};

    // Calcular los puntajes de las dos cartas basados en el orden de la lista de fuerza
    int[] puntajes = new int[2];
    for (int i = 0; i < 2; i++) {
        Carta carta = cartasJugadas.get(i);
        String nombreCarta = carta.toString();

        // Intentar encontrar coincidencia exacta en los primeros 4 elementos
        int indexExacto = indexOf(nombreCarta, ordenFuerza, 0, 4);
        //Aca evaluan los bravos (4 cartas con mayor poder)
        if (indexExacto != -1) {
            // Si se encuentra una coincidencia exacta en los primeros 4 elementos
            puntajes[i] = 14 - indexExacto;
        } else {
            //Aca se evaluan el resto de cartas (sin bravos)
            // Buscar coincidencia parcial a partir de la posición 5 en adelante
            int indexParcial = indexOf(nombreCarta, ordenFuerza, 4, ordenFuerza.length);
            if (indexParcial != -1) {
                // Puntaje basado en la posición encontrada en la búsqueda parcial
                puntajes[i] = 14 - indexParcial;
            } else {
                // Si no se encuentra en ninguna de las búsquedas, puntaje más bajo posible
                puntajes[i] = -1;
            }
        }
    }

    // Imprimir los puntajes, las cartas y los jugadores en la terminal
    System.out.println("Evaluación de la mano:");
    for (int i = 0; i < 2; i++) {
        System.out.println("Jugador: " + jugadores.get(i).getNick() + ", Carta: " + cartasJugadas.get(i) + ", Puntaje: " + puntajes[i]);
    }

    // Comparar los puntajes para determinar el ganador
    if (puntajes[0] > puntajes[1]) {
        System.out.println("El ganador es: " + jugadores.get(0).getNick());
        jugadores.get(0).sumarPuntos(1); //Sumo un punto para el ganador.
        return jugadores.get(0); // Gana el jugador 1

    } else if (puntajes[0] < puntajes[1]) {
        System.out.println("El ganador es: " + jugadores.get(1).getNick());
        jugadores.get(1).sumarPuntos(1); //Sumo un punto para el ganador.
        return jugadores.get(1); // Gana el jugador 2
        
    } else {
        // Si los puntajes son iguales, gana el primer jugador en la lista (jugador 1)
        System.out.println("Es un empate.");
        return null;
    }
  
}

// Método auxiliar para buscar el índice exacto o parcial de una carta en un rango de la lista de fuerza
private int indexOf(String nombreCarta, String[] listaFuerza, int inicio, int fin) {
    for (int i = inicio; i < fin; i++) {
        if (i < 4) {
            // Coincidencia exacta en los primeros 4 elementos
            if (listaFuerza[i].equals(nombreCarta)) {
                return i;
            }
        } else {
            // Coincidencia parcial en el resto de la lista
            if (nombreCarta.contains(listaFuerza[i])) {
                return i;
            }
        }
    }
    return -1; // No encontrado
}


}
