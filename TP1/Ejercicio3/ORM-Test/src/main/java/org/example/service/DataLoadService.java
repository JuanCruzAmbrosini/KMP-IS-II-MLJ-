package org.example.service;

import org.example.entity.Product;
import org.example.entity.Client;
import org.example.repository.ProductRepository;
import org.example.repository.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DataLoadService implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;

    public DataLoadService(ProductRepository productRepository, ClientRepository clientRepository) {
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Load initial data into the database
        loadProducts();
        loadClients();
        measureIndexedVsNonIndexedQueries();
        System.out.println("Data loading completed.");
    }

    // Cargar 50mil productos si no existen
    private void loadProducts() {
        for (int i = 1; i <= 1000; i++) {
            // Si el producto no existe, lo creo
            if (!productRepository.existsById((long) i)) {
                Product product = new Product();
                product.setName("Product " + i);
                product.setPrice(Math.random() * 100);
                product.setStock((int) (Math.random() * 100));
                productRepository.save(product);
            }
        }
    }

    // Cargar 50mil clientes si no existen
    private void loadClients() {
        for (int i = 1; i <= 50000; i++) {
            // Si el cliente no existe, lo creo
            if (!clientRepository.existsById((long) i)) {
                Client client = new Client();
                client.setName("Client " + i);
                client.setEmail("client" + i + "@example.com");
                client.setDni(20000000 + i);
                clientRepository.save(client);
            }
        }
    }

    private void measureIndexedVsNonIndexedQueries() {
        int randomNumber = (int) (Math.random() * 50000) + 1;
        int sampleDni = 20000000 + randomNumber;
        String sampleEmail = "client" + randomNumber + "@example.com";
        int iterations = 500;

        // Warm-up to reduce first-call overhead.
        clientRepository.findByDni(sampleDni);
        clientRepository.findByEmail(sampleEmail);

        long indexedTotalNanos = 0;
        long nonIndexedTotalNanos = 0;

        for (int i = 0; i < iterations; i++) {
            long startIndexed = System.nanoTime();
            clientRepository.findByDni(sampleDni);
            indexedTotalNanos += System.nanoTime() - startIndexed;

            long startNonIndexed = System.nanoTime();
            clientRepository.findByEmail(sampleEmail);
            nonIndexedTotalNanos += System.nanoTime() - startNonIndexed;
        }

        double indexedAvgMs = indexedTotalNanos / (double) iterations / 1_000_000.0;
        double nonIndexedAvgMs = nonIndexedTotalNanos / (double) iterations / 1_000_000.0;
        double ratio = indexedAvgMs > 0 ? nonIndexedAvgMs / indexedAvgMs : 0;

        System.out.printf("Average query time (indexed - dni): %.6f ms%n", indexedAvgMs);
        System.out.printf("Average query time (non-indexed - email): %.6f ms%n", nonIndexedAvgMs);
        System.out.printf("Non-indexed / indexed ratio: %.2f x%n", ratio);
    }
    
    private void getClientById(Long id) {
        Client client = clientRepository.findById(id).orElse(null);
        if (client != null) {
            System.out.println("Client found: " + client.getName() + ", Email: " + client.getEmail());
        } else {
            System.out.println("Client with ID " + id + " not found.");
        }
    }

    private void getProductById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            System.out.println("Product found: " + product.getName() + ", Price: " + product.getPrice() + ", Stock: " + product.getStock());
        } else {
            System.out.println("Product with ID " + id + " not found.");
        }
    }

}
