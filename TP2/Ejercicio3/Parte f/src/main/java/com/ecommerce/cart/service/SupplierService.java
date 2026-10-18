package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Supplier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SupplierService {

    private final List<Supplier> suppliers = new ArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public SupplierService() {
        initDefaultSuppliers();
    }

    private void initDefaultSuppliers() {
        suppliers.add(new Supplier(
                idSequence.getAndIncrement(),
                "TechLog Mendoza Centro",
                "contacto@techlog-mendoza.com.ar",
                "+54 261 425-9988",
                "Av. San Martín 1020, Ciudad de Mendoza, Mendoza",
                -32.89084,
                -68.84584,
                "Periféricos y Teclados Mecánicos",
                "Mendoza"
        ));

        suppliers.add(new Supplier(
                idSequence.getAndIncrement(),
                "Distribuidora Cuyo Hardware",
                "ventas@cuyohardware.com.ar",
                "+54 261 439-1122",
                "Av. San Martín Sur 1450, Godoy Cruz, Mendoza",
                -32.92312,
                -68.84711,
                "Mouses Ópticos y Auriculares",
                "Godoy Cruz"
        ));

        suppliers.add(new Supplier(
                idSequence.getAndIncrement(),
                "Andes Microchips & Componentes",
                "info@andesmicro.com.ar",
                "+54 261 497-3344",
                "Maza 250, Maipú, Mendoza",
                -32.98124,
                -68.78310,
                "Procesadores y Memorias RAM",
                "Maipú"
        ));

        suppliers.add(new Supplier(
                idSequence.getAndIncrement(),
                "Logística Gamer Buenos Aires",
                "distribucion@gamerba.com.ar",
                "+54 11 4312-5500",
                "Av. Alicia Moreau de Justo 740, Puerto Madero, CABA",
                -34.61435,
                -58.36671,
                "Placas de Video y Monitores",
                "Buenos Aires"
        ));

        suppliers.add(new Supplier(
                idSequence.getAndIncrement(),
                "Centro Tecnológico Centro Córdoba",
                "soporte@cordobatec.com.ar",
                "+54 351 422-7788",
                "Av. Colón 1200, Córdoba Capital",
                -31.41160,
                -64.19520,
                "Almacenamiento SSD y Fuentes",
                "Córdoba"
        ));
    }

    public synchronized List<Supplier> getAllSuppliers() {
        return new ArrayList<>(suppliers);
    }

    public synchronized Optional<Supplier> getSupplierById(Long id) {
        return suppliers.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public synchronized Supplier addSupplier(Supplier supplier) {
        if (supplier.getId() == null) {
            supplier.setId(idSequence.getAndIncrement());
        }
        suppliers.add(supplier);
        return supplier;
    }

    public synchronized boolean deleteSupplier(Long id) {
        return suppliers.removeIf(s -> s.getId().equals(id));
    }
}
