package app.servlet;

import app.model.Order;
import app.model.Product;
import app.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet(name = "OrderServlet", urlPatterns = "/orders")
public class OrderServlet extends HttpServlet {

    private final OrderRepository repository;
    private final ObjectMapper objectMapper;

    public OrderServlet() {
        this(new OrderRepository(), createObjectMapper());
    }

    public OrderServlet(OrderRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Order order = objectMapper.readValue(request.getInputStream(), Order.class);
            validateOrder(order);
            prepareOrder(order);
            Order saved = repository.save(order);
            writeJson(response, HttpServletResponse.SC_CREATED, saved);
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid order JSON: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            writeJson(response, HttpServletResponse.SC_OK, repository.findAll());
            return;
        }

        try {
            Long id = Long.valueOf(idParam);
            Optional<Order> order = repository.findById(id);
            if (order.isEmpty()) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Order with id " + id + " was not found");
                return;
            }
            writeJson(response, HttpServletResponse.SC_OK, order.get());
        } catch (NumberFormatException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Order id must be a number");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Order order = objectMapper.readValue(request.getInputStream(), Order.class);
            if (order.getId() == null) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Order id is required for update");
                return;
            }
            validateOrder(order);
            prepareOrder(order);
            Order updated = repository.update(order);
            writeJson(response, HttpServletResponse.SC_OK, updated);
        } catch (IllegalArgumentException e) {
            writeError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid order JSON: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Order id is required");
            return;
        }

        try {
            Long id = Long.valueOf(idParam);
            boolean deleted = repository.deleteById(id);
            if (!deleted) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Order with id " + id + " was not found");
                return;
            }
            writeJson(response, HttpServletResponse.SC_OK, Map.of("deleted", true, "id", id));
        } catch (NumberFormatException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Order id must be a number");
        }
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null");
        }
        List<Product> products = order.getProducts();
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product");
        }
        for (Product product : products) {
            if (product.getName() == null || product.getName().isBlank()) {
                throw new IllegalArgumentException("Product name must not be blank");
            }
            if (product.getCost() == null || product.getCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Product cost must be >= 0");
            }
        }
    }

    private void prepareOrder(Order order) {
        if (order.getDate() == null) {
            order.setDate(LocalDate.now());
        }
        BigDecimal total = order.getProducts().stream()
                .map(Product::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setCost(total);
    }

    private void writeJson(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        writeJson(response, status, Map.of("error", message));
    }
}
