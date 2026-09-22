package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Temporary browser demo (HTTP). Not part of the TCP homework — easy to delete later.
 * Open: http://localhost:8090
 */
public class BrowserDemo {

    private static final Logger log = LoggerFactory.getLogger(BrowserDemo.class);
    private static final int PORT = 8090;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Map<String, LocalDateTime> activeClients = new ConcurrentHashMap<>();
    private final AtomicInteger clientCounter = new AtomicInteger(1);
    private final List<String> eventLog = new ArrayList<>();

    public void start() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/", this::handlePage);
        httpServer.createContext("/api/connect", this::handleConnect);
        httpServer.createContext("/api/command", this::handleCommand);
        httpServer.createContext("/api/status", this::handleStatus);
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.start();

        log.info("[BROWSER-DEMO] Open http://localhost:{}", PORT);
        addEvent("[SERVER] Browser demo started on port " + PORT);
    }

    private synchronized void addEvent(String message) {
        eventLog.add(0, LocalDateTime.now().format(TIME) + "  " + message);
        if (eventLog.size() > 50) {
            eventLog.remove(eventLog.size() - 1);
        }
        log.info(message);
    }

    private void handlePage(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "text/plain", "Method not allowed");
            return;
        }
        send(exchange, 200, "text/html; charset=utf-8", HTML);
    }

    private void handleConnect(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "application/json", "{\"error\":\"POST only\"}");
            return;
        }

        String name = "Client-" + clientCounter.getAndIncrement();
        activeClients.put(name, LocalDateTime.now());
        addEvent("[SERVER] " + name + " успішно підключився");

        String json = "{\"name\":\"" + name + "\",\"message\":\"Connected as " + name + "\"}";
        send(exchange, 200, "application/json", json);
    }

    private void handleCommand(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "application/json", "{\"error\":\"POST only\"}");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String name = queryParam(body, "name");
        String command = queryParam(body, "command");

        if (name == null || !activeClients.containsKey(name)) {
            send(exchange, 400, "application/json",
                    "{\"error\":\"Unknown client. Press Connect first.\"}");
            return;
        }

        addEvent("[SERVER] " + name + " sent command: " + command);

        if ("exit".equalsIgnoreCase(command == null ? "" : command.trim())) {
            activeClients.remove(name);
            addEvent("[SERVER] " + name + " відключився");
            send(exchange, 200, "application/json",
                    "{\"ok\":true,\"disconnected\":true,\"message\":\"Bye, " + name + "\"}");
            return;
        }

        send(exchange, 200, "application/json",
                "{\"ok\":true,\"disconnected\":false,\"message\":\"[SERVER] Unknown command: "
                        + escape(command) + "\"}");
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        StringBuilder clientsJson = new StringBuilder("[");
        boolean first = true;
        for (Map.Entry<String, LocalDateTime> entry : activeClients.entrySet()) {
            if (!first) {
                clientsJson.append(',');
            }
            first = false;
            clientsJson.append("{\"name\":\"")
                    .append(entry.getKey())
                    .append("\",\"connectedAt\":\"")
                    .append(entry.getValue().format(TIME))
                    .append("\"}");
        }
        clientsJson.append(']');

        StringBuilder eventsJson = new StringBuilder("[");
        first = true;
        synchronized (this) {
            for (String event : eventLog) {
                if (!first) {
                    eventsJson.append(',');
                }
                first = false;
                eventsJson.append('"').append(escape(event)).append('"');
            }
        }
        eventsJson.append(']');

        String json = "{\"activeCount\":" + activeClients.size()
                + ",\"clients\":" + clientsJson
                + ",\"events\":" + eventsJson + "}";
        send(exchange, 200, "application/json", json);
    }

    private static String queryParam(String body, String key) {
        if (body == null || body.isBlank()) {
            return null;
        }
        for (String part : body.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static void send(HttpExchange exchange, int code, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException {
        new BrowserDemo().start();
        System.out.println("Open in browser: http://localhost:" + PORT);
    }

    private static final String HTML = """
            <!DOCTYPE html>
            <html lang="uk">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1" />
              <title>Client-Server Demo</title>
              <style>
                :root {
                  --bg: #0f1419;
                  --card: #1a2332;
                  --text: #e7ecf3;
                  --muted: #8b9bb4;
                  --accent: #3d8bfd;
                  --ok: #3dd68c;
                  --warn: #f5a524;
                }
                * { box-sizing: border-box; }
                body {
                  margin: 0;
                  font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, sans-serif;
                  background: radial-gradient(1200px 600px at 10% -10%, #1b3a5f 0%, var(--bg) 55%);
                  color: var(--text);
                  min-height: 100vh;
                  padding: 32px 20px;
                }
                h1 { margin: 0 0 8px; font-size: 1.6rem; }
                .sub { color: var(--muted); margin-bottom: 24px; }
                .grid {
                  display: grid;
                  grid-template-columns: 1.1fr 1fr;
                  gap: 16px;
                  max-width: 980px;
                }
                @media (max-width: 800px) { .grid { grid-template-columns: 1fr; } }
                .card {
                  background: color-mix(in srgb, var(--card) 92%, white 8%);
                  border: 1px solid #2a3a52;
                  border-radius: 16px;
                  padding: 18px;
                }
                .row { display: flex; gap: 8px; flex-wrap: wrap; margin-top: 12px; }
                button, input {
                  font: inherit;
                  border-radius: 10px;
                  border: 1px solid #33455f;
                  padding: 10px 14px;
                }
                input {
                  flex: 1;
                  min-width: 160px;
                  background: #0d1520;
                  color: var(--text);
                }
                button {
                  background: var(--accent);
                  color: white;
                  border: none;
                  cursor: pointer;
                  font-weight: 600;
                }
                button.secondary { background: #2b3648; }
                button:disabled { opacity: 0.5; cursor: not-allowed; }
                .badge {
                  display: inline-block;
                  padding: 4px 10px;
                  border-radius: 999px;
                  background: #243247;
                  color: var(--ok);
                  font-size: 0.85rem;
                }
                ul { list-style: none; padding: 0; margin: 12px 0 0; }
                li {
                  padding: 8px 10px;
                  border-radius: 8px;
                  background: #121b28;
                  margin-bottom: 6px;
                  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
                  font-size: 0.9rem;
                }
                #log {
                  height: 320px;
                  overflow: auto;
                  background: #0b111a;
                  border-radius: 12px;
                  padding: 12px;
                  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
                  font-size: 0.85rem;
                  line-height: 1.45;
                  color: #c9d7ea;
                }
                .hint { color: var(--muted); font-size: 0.9rem; margin-top: 10px; }
              </style>
            </head>
            <body>
              <h1>Client ↔ Server (browser demo)</h1>
              <p class="sub">Тимчасова HTTP-сторінка. TCP ДЗ лишається на порту 8080.</p>

              <div class="grid">
                <section class="card">
                  <div>Статус: <span class="badge" id="me">не підключено</span></div>
                  <div class="row">
                    <button id="connectBtn" type="button">Connect</button>
                    <button id="exitBtn" class="secondary" type="button" disabled>exit</button>
                  </div>
                  <div class="row">
                    <input id="commandInput" placeholder="команда (наприклад hello або exit)" disabled />
                    <button id="sendBtn" type="button" disabled>Send</button>
                  </div>
                  <p class="hint">Відкрий кілька вкладок = кілька клієнтів (Client-1, Client-2…).</p>
                  <h3>Активні з'єднання</h3>
                  <ul id="clients"></ul>
                </section>

                <section class="card">
                  <h3>Лог сервера</h3>
                  <div id="log"></div>
                </section>
              </div>

              <script>
                let clientName = null;

                const me = document.getElementById('me');
                const clientsEl = document.getElementById('clients');
                const logEl = document.getElementById('log');
                const connectBtn = document.getElementById('connectBtn');
                const exitBtn = document.getElementById('exitBtn');
                const sendBtn = document.getElementById('sendBtn');
                const commandInput = document.getElementById('commandInput');

                function setConnected(name) {
                  clientName = name;
                  const on = !!name;
                  me.textContent = on ? name : 'не підключено';
                  connectBtn.disabled = on;
                  exitBtn.disabled = !on;
                  sendBtn.disabled = !on;
                  commandInput.disabled = !on;
                }

                async function connect() {
                  const res = await fetch('/api/connect', { method: 'POST' });
                  const data = await res.json();
                  setConnected(data.name);
                  refresh();
                }

                async function sendCommand(command) {
                  if (!clientName) return;
                  const body = new URLSearchParams({ name: clientName, command });
                  const res = await fetch('/api/command', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body
                  });
                  const data = await res.json();
                  if (data.disconnected) setConnected(null);
                  refresh();
                }

                async function refresh() {
                  const res = await fetch('/api/status');
                  const data = await res.json();
                  clientsEl.innerHTML = data.clients.length
                    ? data.clients.map(c => `<li>${c.name} · since ${c.connectedAt}</li>`).join('')
                    : '<li>немає активних клієнтів</li>';
                  logEl.innerHTML = data.events.map(e => `<div>${e}</div>`).join('');
                }

                connectBtn.onclick = connect;
                exitBtn.onclick = () => sendCommand('exit');
                sendBtn.onclick = () => {
                  const value = commandInput.value.trim();
                  if (!value) return;
                  sendCommand(value);
                  commandInput.value = '';
                };
                commandInput.addEventListener('keydown', (e) => {
                  if (e.key === 'Enter') sendBtn.click();
                });

                setInterval(refresh, 1000);
                refresh();
              </script>
            </body>
            </html>
            """;
}
