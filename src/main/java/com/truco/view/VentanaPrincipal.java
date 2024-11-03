package com.truco.view;

import com.truco.ai.OpenAIClient;
import com.truco.ai.TrucoAI;
import com.truco.controller.JuegoController;
import com.truco.model.Carta;
import com.truco.model.Jugador;
import com.truco.utils.Configuracion;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    public String pedirNombreUsuario() {
        JDialog dialog = new JDialog(this, "Ingrese su nombre", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JTextField campoNombre = new JTextField();
        JButton botonConfirmar = new JButton("Confirmar");

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(new JLabel("Nombre del Usuario:"), BorderLayout.NORTH);
        panelCentro.add(campoNombre, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout());
        panelInferior.add(botonConfirmar);

        dialog.add(panelCentro, BorderLayout.CENTER);
        dialog.add(panelInferior, BorderLayout.SOUTH);

        final String[] nombreUsuario = {null};

        botonConfirmar.addActionListener(e -> {
            nombreUsuario[0] = campoNombre.getText().trim();
            dialog.dispose();
        });

        dialog.setVisible(true);

        return nombreUsuario[0];
    }

    private JuegoController juegoController;
    private List<Jugador> jugadores;
    private JPanel panelCartasCentro;
    private List<Carta> cartasJugadas;
    private List<PanelCartasJugador> panelesJugadores;
    private int rondaActual;
    private JLabel bienvenida;
    private JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    private int turnoActual;
    private Jugador jugadorVentajoso;
    private boolean contraIA;
    private JLabel puntajeIA;  // JLabel para mostrar el puntaje de la IA

    public VentanaPrincipal() {
        setTitle("Truco Master!");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        juegoController = new JuegoController();
        cartasJugadas = new ArrayList<>();
        turnoActual = 0;
        rondaActual = 1;
        panelesJugadores = new ArrayList<>();

        bienvenida = new JLabel("Bienvenido al juego de Truco", SwingConstants.CENTER);
        bienvenida.setFont(new Font("Serif", Font.BOLD, 24));
        add(bienvenida, BorderLayout.CENTER);

        add(panelInferior, BorderLayout.SOUTH);

        JButton iniciarButton = new JButton("Jugar: Jugador vs Jugador");
        iniciarButton.addActionListener(e -> iniciarPartida());
        panelInferior.add(iniciarButton);

        JButton pvsIA = new JButton("Jugar: Jugador vs IA");
        pvsIA.addActionListener(e -> iniciarPartidaIA());
        panelInferior.add(pvsIA);
    }

    private void iniciarPartida() {
        contraIA = false;
        jugadores = new ArrayList<>();

        jugadores.add(new Jugador());
        JOptionPane.showMessageDialog(this, "Jugador 1, ingrese su nombre en la siguiente ventana.");
        jugadores.get(0).setNick(pedirNombreUsuario());

        jugadores.add(new Jugador());
        JOptionPane.showMessageDialog(this, "Jugador 2, ingrese su nombre en la siguiente ventana.");
        jugadores.get(1).setNick(pedirNombreUsuario());

        juegoController.iniciarPartida(jugadores);
        jugadorVentajoso = jugadores.get(0);

        remove(bienvenida);
        panelInferior.setVisible(false);
        mostrarPanelCartas(jugadores, false);

        panelCartasCentro = new JPanel(new FlowLayout());
        panelCartasCentro.setBorder(BorderFactory.createTitledBorder("Cartas Jugadas"));
        add(panelCartasCentro, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void iniciarPartidaIA() {
        contraIA = true;
        List<Jugador> jugadoresIA = new ArrayList<>();

        jugadoresIA.add(new Jugador());
        JOptionPane.showMessageDialog(this, "Jugador, ingrese su nombre en la siguiente ventana.");
        jugadoresIA.get(0).setNick(pedirNombreUsuario());

        jugadoresIA.add(new Jugador("IA"));
        jugadores = jugadoresIA;

        juegoController.iniciarPartida(jugadores);
        jugadorVentajoso = jugadores.get(0);

        remove(bienvenida);
        panelInferior.setVisible(false);
        mostrarPanelCartas(jugadores, true);

        JPanel panelPuntajeIA = new JPanel(new FlowLayout());
        panelPuntajeIA.setBorder(BorderFactory.createTitledBorder("Puntos IA"));

        puntajeIA = new JLabel("Puntos: " + jugadores.get(1).getPuntaje());
        panelPuntajeIA.add(puntajeIA);

        add(panelPuntajeIA, BorderLayout.EAST);

        panelCartasCentro = new JPanel(new FlowLayout());
        panelCartasCentro.setBorder(BorderFactory.createTitledBorder("Cartas Jugadas"));
        add(panelCartasCentro, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void limpiarPanelCartas() {
        for (PanelCartasJugador panel : panelesJugadores) {
            panel.removeAll();
            panel.revalidate();
            panel.repaint();
        }
    }

    private void mostrarPanelCartas(List<Jugador> jugadores, boolean vsIA) {
        limpiarPanelCartas();

        JPanel panelCartas = new JPanel(new GridLayout(1, jugadores.size()));

        for (Jugador jugador : jugadores) {
            if (vsIA && jugador.getNick().equals("IA")) {
                continue;
            }
            PanelCartasJugador panelJugador = new PanelCartasJugador(jugador);
            panelesJugadores.add(panelJugador);
            panelCartas.add(panelJugador);
        }
        add(panelCartas, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    public boolean turnoJugador(Jugador jugador) {
        return jugadores.indexOf(jugador) == turnoActual;
    }

    public void jugarCarta(Jugador jugador, Carta carta, JButton cartaButton) {
        if (!turnoJugador(jugador)) {
            JOptionPane.showMessageDialog(this, "No es el turno de " + jugador.getNick());
            return;
        }

        cartaButton.setEnabled(false);
        cartaButton.setVisible(false);

        JButton cartaView = new JButton(carta.toString());
        cartaView.setPreferredSize(new Dimension(80, 120));
        panelCartasCentro.add(cartaView);

        cartasJugadas.add(carta);
        jugador.getCartas().remove(carta);

        turnoActual = (turnoActual + 1) % jugadores.size();

        if (cartasJugadas.size() == 2) {
            evaluarGanadorDeMano();
            rondaActual++;
        }

        if (turnoActual == 0) {
            jugarCartaIA(jugadores.get(0), jugadores.get(0).getCartas());
        } else {
            jugarCartaIA(jugadores.get(1), jugadores.get(1).getCartas());
        }

        revalidate();
        repaint();
    }

    public void jugarCartaIA(Jugador jugadorIA, List<Carta> cartasIA) {
        String apiKey = Configuracion.obtenerApiKey();
        if (apiKey == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la clave de API.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TrucoAI trucoAI = new TrucoAI(new OpenAIClient(apiKey));
        String cartaSugerida = trucoAI.decidirJugada(cartasIA, cartasJugadas, rondaActual);

        if (!contraIA) {
            JOptionPane.showMessageDialog(this, "La IA sugiere jugar al usuario " + jugadorIA.getNick() + ": " + cartaSugerida + ".", "Sugerencia de jugada de IA.", JOptionPane.INFORMATION_MESSAGE);
        } else if (turnoActual == 1) {
            Carta cartaElegida = null;
            for (Carta carta : cartasIA) {
                if (carta.toString().equals(cartaSugerida)) {
                    cartaElegida = carta;
                    break;
                }
            }

            if (cartaElegida != null) {
                JButton cartaViewIA = new JButton(cartaElegida.toString());
                cartaViewIA.setPreferredSize(new Dimension(80, 120));
                panelCartasCentro.add(cartaViewIA);

                cartasJugadas.add(cartaElegida);
                jugadorIA.getCartas().remove(cartaElegida);

                JOptionPane.showMessageDialog(this, "La IA jugo la carta: " + cartaElegida.toString());

                panelCartasCentro.revalidate();
                panelCartasCentro.repaint();

                if (cartasJugadas.size() == 2) {
                    evaluarGanadorDeMano();
                    rondaActual++;
                }

                turnoActual = 0;
            } else {
                JOptionPane.showMessageDialog(this, "La IA no pudo seleccionar una carta válida.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void desactivarJuego() {
        getContentPane().removeAll();

        bienvenida = new JLabel("¡La partida ha terminado!", SwingConstants.CENTER);
        bienvenida.setFont(new Font("Serif", Font.BOLD, 24));
        add(bienvenida, BorderLayout.CENTER);

        panelInferior.setVisible(true);
        add(panelInferior, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void evaluarGanadorDeMano() {
        Jugador ganador = juegoController.evaluarGanador(cartasJugadas, jugadores);

        if (ganador != null) {
            JOptionPane.showMessageDialog(this, "El ganador de la mano " + rondaActual + " es: " + ganador.getNick());
        } else {
            JOptionPane.showMessageDialog(this, "La mano " + rondaActual + " terminó en empate!");
        }

        for (PanelCartasJugador panel : panelesJugadores) {
            panel.actualizarPuntaje();
        }

        cartasJugadas.clear();
        panelCartasCentro.removeAll();

        if (contraIA) {
            puntajeIA.setText("Puntos: " + jugadores.get(1).getPuntaje());
        }

        if (jugadores.get(0).getCartas().isEmpty() && jugadores.get(1).getCartas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Repartiendo nuevas cartas...");
            juegoController.repartirCartas(jugadores, 3);
            limpiarPanelCartas();
            mostrarPanelCartas(jugadores, true);
        }

        if (juegoController.terminarPartida()) {
            JOptionPane.showMessageDialog(this, "La partida ha terminado! El ganador es: " + ganador.getNick(),
                                          "Partida Finalizada", JOptionPane.INFORMATION_MESSAGE);
            desactivarJuego();
            return;
        }

        if (ganador != null) {
            turnoActual = jugadores.indexOf(ganador);
        }

        if (contraIA && turnoActual == 1) {
            jugarCartaIA(jugadores.get(1), jugadores.get(1).getCartas());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
