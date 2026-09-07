package com.company.automation.demo;

import com.company.automation.config.ConfigReader;
import com.company.automation.constants.AppConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DemoShopServer {
    private static final Logger LOG = LogManager.getLogger(DemoShopServer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, UserRecord> USERS = new ConcurrentHashMap<>();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final List<ProductRecord> PRODUCTS = List.of(
            new ProductRecord("ceramic-mug", "Ceramic Mug", "$12.00"),
            new ProductRecord("canvas-tote", "Canvas Tote", "$24.00"),
            new ProductRecord("notebook", "Notebook", "$8.50")
    );

    private static HttpServer server;

    private DemoShopServer() {
    }

    public static synchronized void start() throws IOException {
        if (server != null) {
            return;
        }
        int configuredPort = portFromBaseUrl();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", configuredPort), 0);
        server.createContext("/login", DemoShopServer::handleLogin);
        server.createContext("/logout", DemoShopServer::handleLogout);
        server.createContext("/home", DemoShopServer::handleHome);
        server.createContext("/cart", DemoShopServer::handleCart);
        server.createContext("/checkout", DemoShopServer::handleCheckout);
        server.createContext("/api/users", DemoShopServer::handleCreateUser);
        server.createContext("/api/products", DemoShopServer::handleProducts);
        server.createContext("/static/", DemoShopServer::handleStatic);
        server.setExecutor(null);
        server.start();
        int port = server.getAddress().getPort();
        String origin = "http://127.0.0.1:" + port;
        System.setProperty("baseUrl", origin + "/login");
        System.setProperty("apiBaseUrl", origin);
        LOG.info("Demo shop listening on {}", origin);
    }

    public static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            USERS.clear();
            SESSIONS.clear();
        }
    }

    private static int portFromBaseUrl() {
        try {
            String base = ConfigReader.get("baseUrl");
            int start = base.lastIndexOf(':') + 1;
            int end = base.indexOf('/', start);
            String port = end > 0 ? base.substring(start, end) : base.substring(start);
            return Integer.parseInt(port);
        } catch (Exception exception) {
            return 18080;
        }
    }

    private static void handleLogin(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            writeHtml(exchange, 200, load("demo-app/login.html"));
            return;
        }
        if ("POST".equals(exchange.getRequestMethod())) {
            Map<String, String> form = readForm(exchange);
            UserRecord user = USERS.get(form.getOrDefault("username", ""));
            if (user == null || !user.password.equals(form.getOrDefault("password", ""))) {
                writeJson(exchange, 401, Map.of("message", AppConstants.LOGIN_ERROR));
                return;
            }
            String sessionId = UUID.randomUUID().toString();
            SESSIONS.put(sessionId, new Session(user));
            Headers headers = exchange.getResponseHeaders();
            headers.add("Set-Cookie", AppConstants.SESSION_COOKIE + "=" + sessionId + "; Path=/; HttpOnly");
            headers.add("Location", "/home");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
            return;
        }
        exchange.sendResponseHeaders(405, -1);
        exchange.close();
    }

    private static void handleLogout(HttpExchange exchange) throws IOException {
        String sessionId = sessionId(exchange);
        if (sessionId != null) {
            SESSIONS.remove(sessionId);
        }
        Headers headers = exchange.getResponseHeaders();
        headers.add("Set-Cookie", AppConstants.SESSION_COOKIE + "=; Path=/; Max-Age=0");
        headers.add("Location", "/login");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private static void handleHome(HttpExchange exchange) throws IOException {
        Session session = requireSession(exchange);
        if (session == null) {
            return;
        }
        String products = PRODUCTS.stream()
                .map(product -> """
                        <article class="product" data-test="product-%s">
                          <h2>%s</h2>
                          <p data-test="price-%s">%s</p>
                          <form method="post" action="/cart">
                            <input type="hidden" name="productId" value="%s"/>
                            <button data-test="add-%s" type="submit">Add to cart</button>
                          </form>
                        </article>
                        """.formatted(product.id, product.name, product.id, product.price, product.id, product.id))
                .reduce("", String::concat);
        String html = load("demo-app/home.html")
                .replace("{{header}}", header(session))
                .replace("{{firstName}}", session.user.firstName)
                .replace("{{products}}", products);
        writeHtml(exchange, 200, html);
    }

    private static void handleCart(HttpExchange exchange) throws IOException {
        Session session = requireSession(exchange);
        if (session == null) {
            return;
        }
        Map<String, String> form = readForm(exchange);
        String productId = form.get("productId");
        PRODUCTS.stream().filter(product -> product.id.equals(productId)).findFirst()
                .ifPresent(product -> session.cart.add(product));
        Headers headers = exchange.getResponseHeaders();
        headers.add("Location", "/home");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private static void handleCheckout(HttpExchange exchange) throws IOException {
        Session session = requireSession(exchange);
        if (session == null) {
            return;
        }
        if ("GET".equals(exchange.getRequestMethod())) {
            String summary = session.cart.isEmpty()
                    ? AppConstants.EMPTY_CART_MESSAGE
                    : session.cart.size() + " item(s) — " + session.cart.get(0).name;
            String html = load("demo-app/checkout.html")
                    .replace("{{header}}", header(session))
                    .replace("{{summary}}", summary);
            writeHtml(exchange, 200, html);
            return;
        }
        if ("POST".equals(exchange.getRequestMethod())) {
            String total = session.cart.isEmpty() ? "$0.00" : session.cart.get(0).price;
            String html = load("demo-app/confirmation.html")
                    .replace("{{header}}", header(session))
                    .replace("{{firstName}}", session.user.firstName)
                    .replace("{{total}}", total);
            session.cart.clear();
            writeHtml(exchange, 200, html);
        }
    }

    private static void handleCreateUser(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        JsonNode body = MAPPER.readTree(exchange.getRequestBody());
        UserRecord user = new UserRecord(
                text(body, "firstName"),
                text(body, "lastName"),
                text(body, "username"),
                text(body, "password")
        );
        USERS.put(user.username, user);
        writeJson(exchange, 201, Map.of(
                "firstName", user.firstName,
                "lastName", user.lastName,
                "username", user.username,
                "password", user.password
        ));
    }

    private static void handleProducts(HttpExchange exchange) throws IOException {
        writeJson(exchange, 200, PRODUCTS);
    }

    private static void handleStatic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().replace("/static/", "demo-app/");
        String body = load(path);
        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", path.endsWith(".css") ? "text/css" : "text/plain");
        write(exchange, 200, body.getBytes(StandardCharsets.UTF_8));
    }

    private static Session requireSession(HttpExchange exchange) throws IOException {
        Session session = SESSIONS.get(sessionId(exchange));
        if (session == null) {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Location", "/login");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
            return null;
        }
        return session;
    }

    private static String sessionId(HttpExchange exchange) {
        List<String> cookies = exchange.getRequestHeaders().get("Cookie");
        if (cookies == null) {
            return null;
        }
        for (String header : cookies) {
            for (String part : header.split(";")) {
                String[] pair = part.trim().split("=", 2);
                if (pair.length == 2 && AppConstants.SESSION_COOKIE.equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    private static String header(Session session) {
        return load("demo-app/header.html")
                .replace("{{firstName}}", session.user.firstName)
                .replace("{{cartCount}}", String.valueOf(session.cart.size()));
    }

    private static Map<String, String> readForm(HttpExchange exchange) throws IOException {
        String raw = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> form = new ConcurrentHashMap<>();
        if (raw.isBlank()) {
            return form;
        }
        for (String pair : raw.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            form.put(key, value);
        }
        return form;
    }

    private static String load(String resource) {
        try (InputStream stream = DemoShopServer.class.getClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException("Missing resource " + resource);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static void writeHtml(HttpExchange exchange, int status, String html) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        write(exchange, status, html.getBytes(StandardCharsets.UTF_8));
    }

    private static void writeJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = MAPPER.writeValueAsBytes(body);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        write(exchange, status, bytes);
    }

    private static void write(HttpExchange exchange, int status, byte[] bytes) throws IOException {
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null ? "" : value.asText();
    }

    private record UserRecord(String firstName, String lastName, String username, String password) {
    }

    private record ProductRecord(String id, String name, String price) {
    }

    private static final class Session {
        private final UserRecord user;
        private final List<ProductRecord> cart = new ArrayList<>();

        private Session(UserRecord user) {
            this.user = user;
        }
    }
}
