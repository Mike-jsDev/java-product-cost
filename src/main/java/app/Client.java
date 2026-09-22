package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private static final String HOST = "localhost";

    public void start() {
        try (
                Socket socket = new Socket(HOST, Server.PORT);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            log.info("[CLIENT] Connected to server {}:{}", HOST, Server.PORT);

            Thread readerThread = new Thread(() -> readServerMessages(serverReader), "server-reader");
            readerThread.setDaemon(true);
            readerThread.start();

            String line;
            while ((line = consoleReader.readLine()) != null) {
                writer.println(line);
                log.info("[CLIENT] Sent command: {}", line);

                if ("exit".equalsIgnoreCase(line.trim())) {
                    log.info("[CLIENT] Exit command sent, closing connection");
                    break;
                }
            }
        } catch (IOException e) {
            log.error("[CLIENT] Connection error", e);
            throw new IllegalStateException("Failed to connect or communicate with server", e);
        }
    }

    private void readServerMessages(BufferedReader serverReader) {
        try {
            String message;
            while ((message = serverReader.readLine()) != null) {
                System.out.println(message);
                log.info("[CLIENT] Received: {}", message);
            }
        } catch (IOException e) {
            log.error("[CLIENT] Failed while reading server messages", e);
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}
