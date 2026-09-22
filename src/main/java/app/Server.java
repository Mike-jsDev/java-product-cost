package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static final int PORT = 8080;

    private final Map<String, ClientConnection> activeConnections = new ConcurrentHashMap<>();
    private final AtomicInteger clientCounter = new AtomicInteger(1);
    private volatile boolean running = true;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info("[SERVER] Server started on port {}", PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                ClientConnection connection = registerClient(clientSocket);
                Thread clientThread = new Thread(() -> handleClient(connection), connection.getName());
                clientThread.setDaemon(true);
                clientThread.start();
            }
        } catch (IOException e) {
            log.error("[SERVER] Server failed", e);
            throw new IllegalStateException("Server failed to start or accept connections", e);
        }
    }

    private ClientConnection registerClient(Socket clientSocket) {
        String name = "Client-" + clientCounter.getAndIncrement();
        ClientConnection connection = new ClientConnection(name, LocalDateTime.now(), clientSocket);
        activeConnections.put(name, connection);
        log.info("[SERVER] {} успішно підключився", name);
        log.info("[SERVER] Active connections: {}", activeConnections.size());
        return connection;
    }

    private void handleClient(ClientConnection connection) {
        String name = connection.getName();
        Socket socket = connection.getSocket();

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            writer.println("[SERVER] Welcome, " + name);

            String command;
            while ((command = reader.readLine()) != null) {
                log.info("[SERVER] {} sent command: {}", name, command);
                String normalized = command.trim();

                if ("exit".equalsIgnoreCase(normalized)) {
                    writer.println("[SERVER] Bye, " + name);
                    break;
                }

                writer.println("[SERVER] Unknown command: " + command);
            }
        } catch (IOException e) {
            log.error("[SERVER] Error while handling {}", name, e);
        } finally {
            disconnect(name);
        }
    }

    private void disconnect(String name) {
        ClientConnection connection = activeConnections.remove(name);
        if (connection == null) {
            return;
        }

        try {
            if (!connection.getSocket().isClosed()) {
                connection.getSocket().close();
            }
        } catch (IOException e) {
            log.error("[SERVER] Failed to close socket for {}", name, e);
        }

        log.info("[SERVER] {} відключився", name);
        log.info("[SERVER] Active connections: {}", activeConnections.size());
    }

    public static void main(String[] args) {
        new Server().start();
    }
}
