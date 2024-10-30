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
    private JuegoController juegoController;
    private List<Jugador> jugadores;
    private JPanel panelCartasCentro;
    private List<Carta> cartasJugadas;
    private List<PanelCartasJugador> panelesJugadores;
    private int rondaActual;
    private JLabel bienvenida;  // Declarar bienvenida como variable de instancia
    
    // Crear panel inferior y configurar FlowLayout
    private JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
   
    //Variables para gestionar los turnos y el caso de empate
    private int turnoActual;
    private Jugador jugadorVentajoso;
    
    //Variable para detectar si se juega o no con IA
    private boolean contraIA;

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
        

        // Configurar interfaz
        bienvenida = new JLabel("Bienvenido al juego de Truco", SwingConstants.CENTER);  // Asignar bienvenida
        bienvenida.setFont(new Font("Serif", Font.BOLD, 24));
        add(bienvenida, BorderLayout.CENTER);  // Añadir al JFrame

        //Agrego panel inferior con boton pvp y pvsIA
        add(panelInferior, BorderLayout.SOUTH);

        //Boton para jugar pvp
        JButton iniciarButton = new JButton("Jugar: Jugador vs Jugador");
        iniciarButton.addActionListener(e -> iniciarPartida());
        panelInferior.add(iniciarButton);

        //Boton para jugar PvsIA
        JButton pvsIA = new JButton("Jugar: Jugador vs IA");
        pvsIA.addActionListener(e -> iniciarPartidaIA());
        panelInferior.add(pvsIA);

        }

    private void iniciarPartida() {
        contraIA = false;
        jugadores = new ArrayList<>();
        jugadores.add(new Jugador("Jugador 1"));
        jugadores.add(new Jugador("Jugador 2"));
        juegoController.iniciarPartida(jugadores);

        jugadorVentajoso = jugadores.get(0); //Definimos el jugador "1" como el que tiene ventaja, ya que el inicia.
        
        remove(bienvenida);  // Ahora podemos eliminar bienvenida al ser una variable de instancia
        panelInferior.setVisible(false); //oculto botones al iniciar partida
        mostrarPanelCartas(jugadores,false);

        panelCartasCentro = new JPanel(new FlowLayout());
        panelCartasCentro.setBorder(BorderFactory.createTitledBorder("Cartas Jugadas"));
        add(panelCartasCentro, BorderLayout.CENTER);

        revalidate();  // Actualizar la interfaz
        repaint();     // Redibujar la ventana
    }

    private void iniciarPartidaIA(){
        contraIA = true;
        // Crear lista de jugadores con un jugador humano y una IA
        List<Jugador> jugadoresIA = new ArrayList<>();
        jugadoresIA.add(new Jugador("Jugador 1"));
        jugadoresIA.add(new Jugador("IA"));
    
        // Asignar la lista de IA a la variable de instancia `jugadores`
        jugadores = jugadoresIA;
    
       juegoController.iniciarPartida(jugadores);
    
       jugadorVentajoso = jugadores.get(0);
    
        // Remover la pestania de bienvenida y botones
        remove(bienvenida);
        panelInferior.setVisible(false);    

        // Mostrar el panel de cartas usando la lista actualizada de jugadores
        mostrarPanelCartas(jugadores,true);
            
        // Configurar el panel central para mostrar cartas jugadas
        panelCartasCentro = new JPanel(new FlowLayout());
        panelCartasCentro.setBorder(BorderFactory.createTitledBorder("Cartas Jugadas"));
        add(panelCartasCentro, BorderLayout.CENTER);
    
        // Actualizar la interfaz
        revalidate();
        repaint();
    }
     
    private void limpiarPanelCartas() {
        for (PanelCartasJugador panel : panelesJugadores) {
            panel.removeAll();  // Elimina todos los componentes del panel
            panel.revalidate(); // Revalida el panel para que se reflejen los cambios
            panel.repaint();    // Redibuja el panel
        }
    }

    private void mostrarPanelCartas(List<Jugador> jugadores, boolean vsIA) {
        //Primero quitamos los contenidos de paneles anteriores.
        limpiarPanelCartas();

        JPanel panelCartas = new JPanel(new GridLayout(1, jugadores.size()));

        for (Jugador jugador : jugadores) {
            //Si se juega contra la IA gestionamos el panel de sus cartas
            if (vsIA && jugador.getNick().equals("IA")) {
                continue; // Omitir el panel de la IA en modo contra IA
            }
            PanelCartasJugador panelJugador = new PanelCartasJugador(jugador);
            panelesJugadores.add(panelJugador);
            panelCartas.add(panelJugador);
        }
        add(panelCartas, BorderLayout.NORTH);
        revalidate();
        repaint();
    }    

    //Método para verificar si el turno es actual
    public boolean turnoJugador(Jugador jugador) {
        return jugadores.indexOf(jugador) == turnoActual;
    }
    

    public void jugarCarta(Jugador jugador, Carta carta, JButton cartaButton) {
        // Verifica que sea el turno del jugador
        System.out.println("Jugador: " + jugadores.indexOf(jugador) + "--- turnoActual: " + turnoActual);
      
        if (!turnoJugador(jugador)) {
            JOptionPane.showMessageDialog(this, "No es el turno de " + jugador.getNick());
            return;
        }
        
        // Ocultar la carta seleccionada y hacerla no clickeable
        cartaButton.setEnabled(false);
        cartaButton.setVisible(false);

        // Añadir la carta al centro
        JLabel cartaLabel = new JLabel(carta.toString());
        cartaLabel.setPreferredSize(new Dimension(80, 120));
        panelCartasCentro.add(cartaLabel);
        cartasJugadas.add(carta);   

        //Borrar carta, esto con el fin de que la mano disponible baje y asi poder repartir luego
        jugador.getCartas().remove(carta);

        // Cambiar turno al siguiente jugador
        turnoActual = (turnoActual + 1) % jugadores.size();
        //System.out.println("turnoActual cambio a : " + turnoActual);

     
        // Si ambos jugadores han jugado una carta, evaluar la mano
        if (cartasJugadas.size() == 2) {
            evaluarGanadorDeMano();
            // Limpiar las cartas jugadas para la próxima ronda
            cartasJugadas.clear();
            panelCartasCentro.removeAll();
            rondaActual++;
        }

        // La IA sugiere una carta para el jugador actual
        if (turnoActual == 0) {

            jugarCartaIA(jugadores.get(0), jugadores.get(0).getCartas()); // Sugerencia para J1
        } else {
            jugarCartaIA(jugadores.get(1), jugadores.get(1).getCartas()); // Sugerencia para J2
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
        // cartaSugerida es la carta que la IA sugiere jugar
        String cartaSugerida = trucoAI.decidirJugada(cartasIA, cartasJugadas, rondaActual);
    
        if (!contraIA) { // Al jugar humano contra humano, se brindan las sugerencias
            JOptionPane.showMessageDialog(this, "La IA sugiere jugar al usuario " + jugadorIA.getNick() + ": " + cartaSugerida + ".", "Sugerencia de jugada de IA.", JOptionPane.INFORMATION_MESSAGE);
        } else { // la IA juega directamente en modo PvE
            Carta cartaElegida = null;
            for (Carta carta : cartasIA) {
                if (carta.toString().equals(cartaSugerida)) {
                    cartaElegida = carta;
                    break;
                }
            }
    
            if (cartaElegida != null) {
                JLabel cartaLabel = new JLabel(cartaElegida.toString());
                cartaLabel.setPreferredSize(new Dimension(80, 120));
                panelCartasCentro.add(cartaLabel);
                cartasJugadas.add(cartaElegida);
                jugadorIA.getCartas().remove(cartaElegida);
    
                // Refrescar el panel para que la carta se muestre inmediatamente
                panelCartasCentro.revalidate();
                panelCartasCentro.repaint();
    
                // Cambiar turno al jugador humano
                turnoActual = 0;
            } else {
                JOptionPane.showMessageDialog(this, "La IA no pudo seleccionar una carta válida.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
        

    private void desactivarJuego() {
        // Remover todos los componentes del contenido actual de la ventana
        getContentPane().removeAll();
    
        // Agregar nuevamente el mensaje de bienvenida y el botón de inicio
        bienvenida = new JLabel("¡La partida ha terminado!", SwingConstants.CENTER);
        bienvenida.setFont(new Font("Serif", Font.BOLD, 24));
        add(bienvenida, BorderLayout.CENTER);
       
       //Reactivo los botones y los agrego a las ventana.
        panelInferior.setVisible(true);
        add(panelInferior, BorderLayout.SOUTH);
    
        // Actualizar la interfaz
        revalidate();
        repaint();
    }
    
    private void evaluarGanadorDeMano() {
        // Determinar el ganador de la mano utilizando el método de JuegoController
        Jugador ganador = juegoController.evaluarGanador(cartasJugadas, jugadores);

        //Vemos que ocurrió en la ronda, si hubo o no empate. Si hubo empate, mostramos al ganador, sino nadie.
        if(ganador!= null){
            JOptionPane.showMessageDialog(this, "El ganador de la mano " + rondaActual + " es: " + ganador.getNick());
        } else {
            JOptionPane.showMessageDialog(this, "La mano " + rondaActual + " terminó en empate!");
        }
        
        // Llamar a actualizarPuntaje() en cada panel después de sumar puntos
        for (PanelCartasJugador panel : panelesJugadores) {
            panel.actualizarPuntaje();
        }

         // Limpiar las cartas jugadas y el panel central para la próxima ronda
        cartasJugadas.clear();
        panelCartasCentro.removeAll();
        
        //Repartir cartas si ambos jugadores se han quedado sin cartas. 
        if(jugadores.get(0).getCartas().isEmpty() && jugadores.get(1).getCartas().isEmpty()){
            //Reparto cartas
            JOptionPane.showMessageDialog(this, "Repartiendo nuevas cartas...");
            juegoController.repartirCartas(jugadores, 3);
            //Actualizo la interfaz
            limpiarPanelCartas();
            mostrarPanelCartas(jugadores,true);
        }

        //Verificar si esta la condicion de finalizar la partida
        if(juegoController.terminarPartida()){
            JOptionPane.showMessageDialog(this, "La partida ha terminado! El ganador es: " + ganador.getNick(), 
                                      "Partida Finalizada", JOptionPane.INFORMATION_MESSAGE);
            //Ya no se puede jugar más, se debe inicar partida.
            desactivarJuego();
            return;
        }

         // Cambiar turno si hay un ganador, mantenerlo si hay empate
         if (ganador != null) {
            turnoActual = jugadores.indexOf(ganador);
        }
        
        //Si estoy contra la IA, gano la IA juega ella
        if(contraIA && turnoActual == 1){
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