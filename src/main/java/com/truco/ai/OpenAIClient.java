package com.truco.ai;
//Librerias.
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAIClient {
    //Se brinda el enlace de la API.
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private String apiKey;

    //Constructor de la clase (API URL es igual para todo objeto de la clase _static_)
    public OpenAIClient(String apiKey) {
        this.apiKey = apiKey;   
    }

    //Método para realizar la solicitud y obtener respuesta de la API
    public String enviarSolicitud(String jsonSolicitud) throws Exception {
        //Definimos el cuerpo de la solicitud que se le hace la API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSolicitud))
                .build();
        var client = HttpClient.newHttpClient();
        //Se recibe la solicitud y se recibe como un STRING
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //System.out.println(response.body());
        

        return response.body();
    }
}
