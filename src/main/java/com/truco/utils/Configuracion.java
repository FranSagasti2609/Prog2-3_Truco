package com.truco.utils;

import java.io.InputStream;
import java.util.Properties;

public class Configuracion {
    public static String obtenerApiKey() {
        Properties propiedades = new Properties();
        try (InputStream input = Configuracion.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("No se pudo encontrar el archivo config.properties");
                return null;
            }
            // Cargar el archivo de propiedades
            propiedades.load(input);
            return propiedades.getProperty("openai.api.key");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
