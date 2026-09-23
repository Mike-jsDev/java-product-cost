package app;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    public CustomerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Customer save(Customer customer) {
        String sql = """
                INSERT INTO customer (full_name, email, social_security_number)
                VALUES (?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, customer.getFullName());
                statement.setString(2, customer.getEmail());
                statement.setString(3, customer.getSocialSecurityNumber());
                return statement;
            }, keyHolder);
        } catch (Exception e) {
            throw new DaoOperationException("Failed to save customer with email " + customer.getEmail(), e);
        }

        Number key = null;
        if (keyHolder.getKeys() != null && keyHolder.getKeys().get("id") instanceof Number number) {
            key = number;
        }
        if (key == null) {
            throw new DaoOperationException("Failed to save customer: generated id is missing");
        }
        customer.setId(key.longValue());
        return customer;
    }

    public Customer findById(Long id) {
        String sql = """
                SELECT id, full_name, email, social_security_number
                FROM customer
                WHERE id = ?
                """;
        try {
            List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper, id);
            if (customers.isEmpty()) {
                throw new DaoOperationException("Customer with id " + id + " was not found");
            }
            return customers.get(0);
        } catch (DaoOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoOperationException("Failed to find customer with id " + id, e);
        }
    }

    public Customer update(Customer customer) {
        if (customer.getId() == null) {
            throw new DaoOperationException("Customer id is required for update");
        }

        String sql = """
                UPDATE customer
                SET full_name = ?, email = ?, social_security_number = ?
                WHERE id = ?
                """;
        try {
            int updated = jdbcTemplate.update(
                    sql,
                    customer.getFullName(),
                    customer.getEmail(),
                    customer.getSocialSecurityNumber(),
                    customer.getId()
            );
            if (updated == 0) {
                throw new DaoOperationException("Customer with id " + customer.getId() + " was not found");
            }
            return customer;
        } catch (DaoOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoOperationException("Failed to update customer with id " + customer.getId(), e);
        }
    }

    public boolean deleteById(Long id) {
        String sql = "DELETE FROM customer WHERE id = ?";
        try {
            int deleted = jdbcTemplate.update(sql, id);
            if (deleted == 0) {
                throw new DaoOperationException("Customer with id " + id + " was not found");
            }
            return true;
        } catch (DaoOperationException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoOperationException("Failed to delete customer with id " + id, e);
        }
    }

    public List<Customer> findAll() {
        String sql = """
                SELECT id, full_name, email, social_security_number
                FROM customer
                ORDER BY id
                """;
        try {
            return jdbcTemplate.query(sql, customerRowMapper);
        } catch (Exception e) {
            throw new DaoOperationException("Failed to load customers", e);
        }
    }

    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS customer (
                    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    full_name VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    social_security_number VARCHAR(64) NOT NULL UNIQUE
                )
                """;
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            throw new DaoOperationException("Failed to create customer table", e);
        }
    }

    public void clear() {
        try {
            jdbcTemplate.execute("TRUNCATE TABLE customer RESTART IDENTITY");
        } catch (Exception e) {
            throw new DaoOperationException("Failed to clear customer table", e);
        }
    }
}
