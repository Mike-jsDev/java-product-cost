package app;

import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Objects;

public class ClientConnection {

    private final String name;
    private final LocalDateTime connectedAt;
    private final Socket socket;

    public ClientConnection(String name, LocalDateTime connectedAt, Socket socket) {
        this.name = name;
        this.connectedAt = connectedAt;
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientConnection that = (ClientConnection) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ClientConnection{name='" + name
                + "', connectedAt=" + connectedAt
                + ", remote=" + socket.getRemoteSocketAddress() + '}';
    }
}
