package com.javalab.jpahibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductRepositoryTest {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("productPU");
        entityManager = entityManagerFactory.createEntityManager();
        repository = new ProductRepository(entityManager);
    }

    @AfterEach
    void tearDown() {
        entityManager.close();
        entityManagerFactory.close();
    }

    @Test
    void saveAssignsGeneratedIdToNewProduct() {
        Product product = new Product("ノート", 150, 100);

        Product saved = repository.save(product);

        assertNotNull(saved.getId());
    }

    @Test
    void findByIdReturnsSavedProduct() {
        Product saved = repository.save(new Product("ノート", 150, 100));

        Optional<Product> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("ノート", found.get().getName());
        assertEquals(150, found.get().getPrice());
        assertEquals(100, found.get().getStock());
    }

    @Test
    void findByIdReturnsEmptyWhenProductDoesNotExist() {
        Optional<Product> found = repository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAllReturnsAllSavedProducts() {
        repository.save(new Product("ノート", 150, 100));
        repository.save(new Product("ペン", 100, 50));

        List<Product> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void updateReflectsChangesToExistingProduct() {
        Product saved = repository.save(new Product("ノート", 150, 100));
        saved.setPrice(200);
        saved.setStock(80);

        repository.update(saved);
        Optional<Product> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(200, found.get().getPrice());
        assertEquals(80, found.get().getStock());
    }

    @Test
    void deleteByIdRemovesProductSoThatFindByIdReturnsEmpty() {
        Product saved = repository.save(new Product("ノート", 150, 100));

        boolean deleted = repository.deleteById(saved.getId());

        assertTrue(deleted);
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }
}
