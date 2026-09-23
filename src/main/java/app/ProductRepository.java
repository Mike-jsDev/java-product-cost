package app;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository {

    private final List<Product> products = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        save(new Product(null, "Laptop", new BigDecimal("1200.00")));
        save(new Product(null, "Phone", new BigDecimal("800.00")));
        save(new Product(null, "Headphones", new BigDecimal("150.00")));
        save(new Product(null, "Keyboard", new BigDecimal("90.00")));
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
            products.add(product);
            return product;
        }

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(product.getId())) {
                products.set(i, product);
                return product;
            }
        }

        products.add(product);
        return product;
    }

    public Optional<Product> findById(Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst();
    }

    public List<Product> findAll() {
        return new ArrayList<>(products);
    }

    public boolean deleteById(Long id) {
        return products.removeIf(product -> product.getId().equals(id));
    }
}
