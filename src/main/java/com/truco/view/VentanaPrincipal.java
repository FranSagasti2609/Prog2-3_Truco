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

    //Crear ventanas para pedir el nombre al usuario. 
    public String pedirNombreUsuario() {
        // Crear un JDialog modal para solicitar el nombre del usuario
        JDialog dialog = new JDialog(this, "Ingrese su nombre", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this); // Centra el diálogo en la ventana principal

        // Crear el campo de texto
        JTextField campoNombre = new JTextField();
        JButton botonConfirmar = new JButton("Confirmar");

        // Panel para el campo de texto
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(new JLabel("Nombre del Usuario:"), BorderLayout.NORTH);
        panelCentro.add(campoNombre, BorderLayout.CENTER);
        
        // Panel para los botones
        JPanel panelInferior = new JPanel(new FlowLayout());
        panelInferior.add(botonConfirmar);

        // Agregar componentes al diálogo
        dialog.add(panelCentro, BorderLayout.CENTER);
        dialog.add(panelInferior, BorderLayout.SOUTH);

        // Variable para almacenar el nombre ingresado
        final String[] nombreUsuario = {null};

        // Acción del botón Confirmar
        botonConfirmar.addActionListener(e -> {
            nombreUsuario[0] = campoNombre.getText().trim();
            dialog.dispose(); // Cerrar el diálogo después de confirmar
        });

        // Mostrar el diálogo
        dialog.setVisible(true);

        return nombreUsuario[0];
    }

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
        //Creo el jugador 1 y le pido su nombre.
        jugadores.add(new Jugador());
        JOptionPane.showMessageDialog(this,"Jugador 1, ingrese su nombre en la siguiente ventana.");
        jugadores.get(0).setNick(pedirNombreUsuario());

         //Creo el jugador 2 y le pido su nombre.
        jugadores.add(new Jugador());
        JOptionPane.showMessageDialog(this,"Jugador 2, ingrese su nombre en la siguiente ventana.");
        jugadores.get(1).setNick(pedirNombreUsuario());
        
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

        //Creo al jugador y le pido su nombre.
        jugadoresIA.add(new Jugador());
        JOptionPane.showMessageDialog(this,"Jugador, ingrese su nombre en la siguiente ventana.");
        jugadoresIA.get(0).setNick(pedirNombreUsuario());

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
    
        // Mostrar la carta jugada en el panel central
        JButton cartaView = new JButton(carta.toString());
        cartaView.setPreferredSize(new Dimension(80, 120));
        panelCartasCentro.add(cartaView);
    
        // Añadir la carta a las jugadas y removerla de la mano del jugador
        cartasJugadas.add(carta);
        jugador.getCartas().remove(carta);
    
        // Cambiar turno al siguiente jugador
        turnoActual = (turnoActual + 1) % jugadores.size();
    
        // Si ambos jugadores han jugado una carta, evaluar la mano
        if (cartasJugadas.size() == 2) {
            evaluarGanadorDeMano();  // Limpia el panel después de evaluar la mano
            rondaActual++;
        }
    
        // Muestra la sugerencia de la IA o cambia el turno al siguiente jugador en PvP
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
        // cartaSugerida es la carta que la IA sugiere jugar
        String cartaSugerida = trucoAI.decidirJugada(cartasIA, cartasJugadas, rondaActual);
    
        if (!contraIA) { // Al jugar humano contra humano, se brindan las sugerencias
            JOptionPane.showMessageDialog(this, "La IA sugiere jugar al usuario " + jugadorIA.getNick() + ": " + cartaSugerida + ".", "Sugerencia de jugada de IA.", JOptionPane.INFORMATION_MESSAGE);
        } else if(turnoActual == 1){ //Si estamos contra la IA, y ES SU TURNO, que juegue una carta. Con esto garantizamos un flujo correcto de funcionamiento.
            Carta cartaElegida = null;
            for (Carta carta : cartasIA) {
                if (carta.toString().equals(cartaSugerida)) {
                    cartaElegida = carta;
                    break;
                }
            }
    
            if (cartaElegida != null) {
                // Añadir la carta jugada al panel central
                JButton cartaViewIA = new JButton(cartaElegida.toString());
                cartaViewIA.setPreferredSize(new Dimension(80, 120));
                panelCartasCentro.add(cartaViewIA);
                
                // Agregar la carta a las cartas jugadas y removerla de la mano del jugador IA
                cartasJugadas.add(cartaElegida);
                jugadorIA.getCartas().remove(cartaElegida);
                
                JOptionPane.showMessageDialog(this, "La IA jugo la carta: " + cartaElegida.toString());

                // Refrescar el panel para que la carta se muestre inmediatamente
                panelCartasCentro.revalidate();
                panelCartasCentro.repaint();

                //Evaluamos mano, cuando la IA o humano juega.
                if(cartasJugadas.size() == 2) {
                    evaluarGanadorDeMano();
                    rondaActual++;
                }

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