package co.edu.escuelaing.parcial1arep;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class HttpServer {

    private static final int PORT = 36000;
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
                System.out.println("entre por aqui con path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + addComand(path);
            } else if (path.startsWith("/list")) {
                System.out.println("entre por aqui con path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + listComand(path);
            } else if (path.startsWith("/clear")) {
                System.out.println("entre por aqui con path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + clearComand(path);
            } else if (path.startsWith("/stats")) {
                System.out.println("entre por aqui con path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + statsComand(path);
            }else if (path.startsWith("/media")) {
                System.out.println("entre por aqui con path----------------" + path);
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + mediaComand();
            } else {
                outputLine = getNotFoundResponse();
            }
            

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }
    
    private static double calcularMedia(ArrayList<Double> numeros) {
        if (numeros.isEmpty()) {
            return 0;
        }
        double suma = 0;
        for (double num : numeros) {
            suma += num;
        }
        return suma / numeros.size();
    }

    private static String mediaComand() {
        if (dataStore.isEmpty()) {
            return "{\"status\":\"ERR\",\"error\":\"empty_list\"}";
        }

        ArrayList<Double> numbers = new ArrayList<>();
        for (String s : dataStore) {
            numbers.add(Double.parseDouble(s));
        }

        double mean = calcularMedia(numbers);

        return "{\"status\":\"ok\",\"mean\":\"" + mean + "\"}";
    }

    private static String addComand(String path) {
        try {
            String query = path.split("\\?")[1];
            String param = query.split("=")[1];
            double num = Double.parseDouble(param);
            dataStore.add(param);

            return "{\"status\":\"ok\",\"added\":\"" + param + "\",\"count\":\"" + dataStore.size() + "\"}";
        } catch (Exception e) {
            return "{\"status\":\"ERR\",\"error\":\"invalidNumber\"}";
        }
    }


    private static String listComand(String path) {
        return "{\"status\":\"ok\",\"values\":" + dataStore.toString() + "}";
    }

    private static String clearComand(String path) {
        dataStore.clear();
        return "{\"status\":\"ok\",\"message\":\"list_cleared\"}";
    }

    private static String statsComand(String path) {
        if (dataStore.isEmpty()) {
            return "{\"status\":\"ERR\",\"error\":\"empty_list\"}";
        }
        ArrayList<Double> numbers = new ArrayList<>();
        for (String s : dataStore) {
            numbers.add(Double.parseDouble(s));
        }

        double mean = calcularMedia(numbers);
        double variance = numbers.stream().mapToDouble(n -> Math.pow(n - mean, 2)).average().orElse(0.0);
        double stddev = Math.sqrt(variance);

        return "{\"status\":\"ok\",\"mean\":\"" + mean + "\",\"stddev\":\"" + stddev + "\",\"count\":\"" + numbers.size() + "\"}";
    }


    private static String getNotFoundResponse() {
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
                + "</body>\n"
                + "</html>\n";
    }
    

}
