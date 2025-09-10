package co.edu.escuelaing.parcial1arep;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class HttpServer {

    private static final int PORT = 9001;
    private static LinkedList<String> dataStore = new LinkedList<>();

    public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }

        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir en el puerto " + PORT + "...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Falló la conexión con el cliente.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;

            boolean isFirstLine = true;
            String firstLine = "";

            while ((inputLine = in.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    firstLine = inputLine;
                }
                if (!in.ready()) {
                    break;
                }
            }
            System.out.println("Recibí: " + firstLine);

            String path = firstLine.split(" ")[1];

            if (path.startsWith("/add")) {
                System.out.println("entre por aquicon path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + addComand(path);
            } else if (path.startsWith("/list")) {
                System.out.println("entre por aquicon path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + listComand(path);
            } else if (path.startsWith("/clear")) {
                System.out.println("entre por aquicon path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + clearComand(path);
            } else if (path.startsWith("/stats")) {
                System.out.println("entre por aquicon path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + statsComand(path);
            } else {
                outputLine = getMethodNotSupportedResponse();
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    //metodo para add
    private static String addComand(String path) {
        String query = path.split("\\?")[1];
        String param = query.split("=")[1];

        dataStore.add(param);

        // { "status": "ok", "added": "3.5", "count": "1" }
        return "{\"status\":\"" + "ok" + "\","
                + "\"added\":\"" + param + "\","
                + "\"count\":\"" + 1 + "\","
                + "}";
        
        // error 400
        // { "status": "ERR", "error": "invalidNumber" }
        
        
    }

    private static String listComand(String path) {
        String query = path.split("\\?")[1];
        String param = query.split("=")[1];

        String value = dataStore.get(param);

        // { "status": "ok", "values": "[3.5,2.0,1.0]" }
        return "{\"status\":\"" + param + "\","
                + "\"values\":\"" + value + "\""
                + "}";
    }

    private static String clearComand(String path) {
        String query = path.split("\\?")[1];
        String param = query.split("=")[1];

        String value = dataStore.get(param);

        // { "status": "ok", "message": "listCleared" }
        return "{\"status\":\"" + param + "\","
                + "\"message\":\"" + value + "\""
                + "}";
    }

    private static String statsComand(String path) {
        String query = path.split("\\?")[1];
        String param = query.split("=")[1];

        String value = dataStore.element();

        // { "status": "ok", "mean": "5.16666666",  "stddev": 3.2071349027, "count": 3 }
        return "{\"status\":\"" + param + "\","
                + "\"mean\":\"" + value + "\""
                + "\"stddev\":\"" + value + "\""
                + "\"count\":\"" + value + "\""
                + "}";

        // respuesta 409
        // {  "status": "ERR",  "error": "empty_list"}

    }

    private static String getMethodNotSupportedResponse() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>Title of the document</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>No soportado</h1>\n"
                + "<h1>{\"name\":\"John\"}</h1>\n"
                + "</body>\n"
                + "</html>\n";
    }

}
