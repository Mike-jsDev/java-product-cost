package app.servlet;

import app.model.Order;
import app.model.Product;
import app.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private OrderRepository repository;
    private OrderServlet servlet;
    private ObjectMapper objectMapper;
    private StringWriter responseBody;

    @BeforeEach
    void setUp() throws Exception {
        repository = new OrderRepository();
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        servlet = new OrderServlet(repository, objectMapper);

        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    @Test
    void doPost_shouldCreateOrder() throws Exception {
        String json = """
                {
                  "date": "2026-09-23",
                  "products": [
                    {"id": 1, "name": "Laptop", "cost": 1000},
                    {"id": 2, "name": "Mouse", "cost": 50}
                  ]
                }
                """;
        mockRequestBody(json);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Order saved = repository.findById(1L).orElseThrow();
        assertEquals(new BigDecimal("1050"), saved.getCost());
        assertEquals(2, saved.getProducts().size());
        assertTrue(responseBody.toString().contains("Laptop"));
    }

    @Test
    void doGet_shouldReturnOrderById() throws Exception {
        Order order = repository.save(sampleOrder(null));
        when(request.getParameter("id")).thenReturn(String.valueOf(order.getId()));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseBody.toString().contains("\"id\":" + order.getId()));
        assertTrue(responseBody.toString().contains("Phone"));
    }

    @Test
    void doGet_shouldReturn404WhenOrderMissing() throws Exception {
        when(request.getParameter("id")).thenReturn("99");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(responseBody.toString().contains("was not found"));
    }

    @Test
    void doPut_shouldUpdateOrder() throws Exception {
        Order existing = repository.save(sampleOrder(null));
        String json = """
                {
                  "id": %d,
                  "date": "2026-09-24",
                  "products": [
                    {"id": 10, "name": "Tablet", "cost": 500}
                  ]
                }
                """.formatted(existing.getId());
        mockRequestBody(json);

        servlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        Order updated = repository.findById(existing.getId()).orElseThrow();
        assertEquals(LocalDate.of(2026, 9, 24), updated.getDate());
        assertEquals(new BigDecimal("500"), updated.getCost());
        assertEquals("Tablet", updated.getProducts().get(0).getName());
    }

    @Test
    void doDelete_shouldRemoveOrder() throws Exception {
        Order existing = repository.save(sampleOrder(null));
        when(request.getParameter("id")).thenReturn(String.valueOf(existing.getId()));

        servlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(repository.findById(existing.getId()).isEmpty());
        assertTrue(responseBody.toString().contains("\"deleted\":true"));
    }

    @Test
    void doPost_shouldRejectEmptyProducts() throws Exception {
        mockRequestBody("""
                {"date":"2026-09-23","products":[]}
                """);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseBody.toString().contains("at least one product"));
    }

    private Order sampleOrder(Long id) {
        return new Order(
                id,
                LocalDate.of(2026, 9, 23),
                new BigDecimal("700"),
                List.of(new Product(1L, "Phone", new BigDecimal("700")))
        );
    }

    private void mockRequestBody(String json) throws IOException {
        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return inputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
        when(request.getInputStream()).thenReturn(servletInputStream);
    }
}
