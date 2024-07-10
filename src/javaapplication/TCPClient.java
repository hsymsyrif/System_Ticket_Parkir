package javaapplication;

import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(hostname, port);
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true);
             InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Enter username:");
            String username = consoleReader.readLine();
            System.out.println("Enter password:");
            String password = consoleReader.readLine();

            writer.println(username);
            writer.println(password);

            String response = reader.readLine();
            System.out.println(response);

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
