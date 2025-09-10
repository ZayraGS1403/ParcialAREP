package co.edu.escuelaing.parcial1arep;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

/**
 *
 * @author Zayra.gutierrez-s
 */
public class Facade {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:36000";
    private static final int PORT = 37000;

    public static void main(String[] args) throws IOException {
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
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
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
                    firstLine = inputLine;
                    isFirstLine = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            System.out.println(firstLine);

            String path = firstLine.split(" ")[1];

            if (path.startsWith("/cliente")) {
                outputLine = readStaticFile();
            } else if (path.startsWith("/add") ) {
                String result = connection(path);
                if (result.contains("Error")){
                    outputLine = "HTTP/1.1 400 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + result;
                }else {
                    outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + result;
                }
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

    private static String readStaticFile() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("src/main/java/resorces/index.html"));
        String inputLine;
        StringBuffer resp = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            resp.append(inputLine + "\n");
        }
        in.close();
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + resp.toString();
    }

    public static String connection(String path) throws IOException {
        String query = path.split("\\?")[1];

        URL obj = new URL(GET_URL + path.replace(" ", ""));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int respCode = con.getResponseCode();
        System.out.println("GET resp Code :" + respCode);
        if (respCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer resp = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                resp.append(inputLine);
            }
            in.close();
            return resp.toString();

        } else {
            System.out.println("Request no funcionó o no hay servidor escuchando");
            return getNotFoundResponse();
        }
    }

    private static String getNotFoundResponse() {
        return "HTTP/1.1 400 ERROR\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "<meta charset=\"UTF-8\">\n"
                + "<title>NOT FOUND</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<h1>Not Found T-T</h1>\n"
                + "</body>\n"
                + "</html>\n";
    }
}
