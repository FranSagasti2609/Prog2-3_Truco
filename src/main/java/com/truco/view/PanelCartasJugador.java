package com.truco.view;

import com.truco.model.Carta;
import com.truco.model.Jugador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PanelCartasJugador extends JPanel {
    //atributos del panel
    private Jugador jugador;
    private List<JButton> botonesCartas;
    private JLabel puntosJug;

    public PanelCartasJugador(Jugador jugador) {
        this.jugador = jugador;
        //Label para el puntaje
        puntosJug = new JLabel("Puntos:" + jugador.getPuntaje());

        this.botonesCartas = new ArrayList<>();
        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder(jugador.getNick()));

        // Agregar las cartas como botones ocultos
        for (Carta carta : jugador.getCartas()) {
            JButton cartaButton = new JButton("Dorso"); // Mostramos el dorso para ocultar la carta
            cartaButton.setPreferredSize(new Dimension(80, 120)); //tamaño de la figura
            cartaButton.setToolTipText("Haz clic derecho para ver la carta"); //texto que se muestra al pasar el mouse arriba

            // Configurar eventos de clic derecho e izquierdo
            cartaButton.addMouseListener(new MouseAdapter() {
                @Override
                //Esto es cuando el mouse se clickea, veremos el click derecho e izquierdo
                
                //Gestionar el click derecho al MANTENER
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // Al pulsar el botón derecho se muestran las cartas
                        cartaButton.setText(carta.toString());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // Al soltar el botón derecho se oculta la carta
                        cartaButton.setText("Dorso");
                    }
                }

                //Gestionar el click izquierdo AL PULSAR
                public void mouseClicked(MouseEvent e) {

                    if (SwingUtilities.isLeftMouseButton(e)) { 
                        
                        //click izquierdo
                        VentanaPrincipal ventanaPrincipal = buscarVentanaPrincipal();
                        if (ventanaPrincipal != null) {
                            //si es el turno del jugador correspondiente entonces AHI quito la carta visualmente
                            if(ventanaPrincipal.turnoJugador(jugador)){
        
                            ventanaPrincipal.jugarCarta(jugador, carta, cartaButton);
                            // Ocultar y desactivar la carta seleccionada
                            cartaButton.setEnabled(false);
                            cartaButton.setVisible(false);
                            // Forzar la actualización de la interfaz gráfica
                            revalidate();
                            repaint();
                            } else JOptionPane.showMessageDialog(null, "No es tu turno.");
                            
                        }
                    }
                }
            });

            botonesCartas.add(cartaButton);
            add(cartaButton);
            
        }
        add(puntosJug, BorderLayout.EAST);
    }

    //Método para actualizar el puntaje, sino quedaría fijo en 0
    public void actualizarPuntaje(){
        puntosJug.setText("Puntos: " + jugador.getPuntaje());
    }

        // Método para buscar la instancia de VentanaPrincipal en la jerarquía de componentes
    private VentanaPrincipal buscarVentanaPrincipal() {
        Component component = this;
        while (component != null && !(component instanceof VentanaPrincipal)) {
            component = component.getParent();
        }
        return (VentanaPrincipal) component;
    }
}