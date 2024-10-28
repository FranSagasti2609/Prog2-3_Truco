package com.truco.ai;
//Librerias
import com.truco.model.Carta;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;

//Creamos la clase TrucoAI
public class TrucoAI {
    //Atributos
    private OpenAIClient openAIClient;
    
    //Constructor (Metodo dentro de la clase que se llama igual que la clase)
    public TrucoAI(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    //Metodo
    public String decidirJugada(List<Carta> cartasIA, List<Carta> cartasJugadas, int rondaActual) {
        int maxIntentos = 4;
        int intentoActual = 0;

        while (intentoActual < maxIntentos) {
            try {
                // Construir el JSON de la solicitud
                JSONObject solicitud = new JSONObject();
                solicitud.put("model", "gpt-4o-mini");
                solicitud.put("max_tokens", 300);

                // Instrucciones personalizadas para la IA
                JSONArray mensajes = new JSONArray();
                JSONObject sistema = new JSONObject();
                sistema.put("role", "system");
                sistema.put("content", "Eres un jugador experto en Truco Argentino. Las cartas tienen el siguiente orden de fuerza de mayor a menor: 1 de Espadas, 1 de Bastos, 7 de Espadas, 7 de Oro, 3, 2, 1, 12, 11, 10, 7, 6, 5, 4. Responde solo con el nombre de la carta que deberías jugar en el formato exacto 'X de Y'. No des ninguna explicación.");

                JSONObject usuario = new JSONObject();
                usuario.put("role", "user");
                usuario.put("content", construirInstruccion(cartasIA));

                mensajes.put(sistema);
                mensajes.put(usuario);
                solicitud.put("messages", mensajes);
                
                // Mostrar el contenido del JSON de la solicitud
                System.out.println("Enviando solicitud a la IA:");
                System.out.println(solicitud.toString(2)); // Formato JSON con indentación para mayor legibilidad


                // Enviar la solicitud a la API de OpenAI
                String respuestaJSON = openAIClient.enviarSolicitud(solicitud.toString());

                // Mostrar el contenido de la respuesta
                System.out.println("Respuesta recibida de la IA:");
                System.out.println(respuestaJSON);
                
                // Parsear la respuesta JSON para obtener la carta sugerida
                JSONObject respuestaObj = new JSONObject(respuestaJSON);
                String cartaSugerida = respuestaObj
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                return cartaSugerida;

            } catch (Exception e) {
                // Manejo del error 429 - Too Many Requests
                if (e.getMessage().contains("HTTP response code: 429")) {
                    intentoActual++;
                    System.out.println("Error 429: Demasiadas solicitudes. Reintentando en " + (int) Math.pow(2, intentoActual) + " segundos...");
                    try {
                        // Esperar un tiempo exponencial antes de reintentar
                        Thread.sleep((int) Math.pow(2, intentoActual) * 1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    e.printStackTrace();
                    return "Error al decidir la jugada.";
                }
            }
        }
        // Si todos los intentos fallan, devolver un mensaje de error
        return "No se pudo obtener la decisión de la IA después de varios intentos.";
    }

    private String construirInstruccion(List<Carta> cartasIA) {
        StringBuilder instruccion = new StringBuilder();
        instruccion.append("Estas son las cartas en tu mano: ");
        for (Carta carta : cartasIA) {
            instruccion.append(carta.toString()).append(", ");
        }
        instruccion.append("Elige la mejor carta para jugar en este momento.");
        return instruccion.toString();
    }
}
