package com.ecommerce.cart;

import com.ecommerce.cart.model.Supplier;
import com.ecommerce.cart.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SupplierServiceTest {

    private SupplierService supplierService;

    @BeforeEach
    void setUp() {
        supplierService = new SupplierService();
    }

    @Test
    void testInitialSuppliersLoaded() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        assertNotNull(suppliers);
        assertFalse(suppliers.isEmpty());
        assertTrue(suppliers.size() >= 5);

        // Verificar que cada proveedor contenga nombre, dirección, latitud y longitud válidas
        for (Supplier s : suppliers) {
            assertNotNull(s.getName(), "El nombre del proveedor no debe ser nulo");
            assertNotNull(s.getAddress(), "La dirección no debe ser nula");
            assertNotNull(s.getLatitude(), "La latitud es requerida para OpenStreetMap");
            assertNotNull(s.getLongitude(), "La longitud es requerida para OpenStreetMap");
            assertTrue(s.getLatitude() >= -90.0 && s.getLatitude() <= 90.0, "Latitud dentro de rango");
            assertTrue(s.getLongitude() >= -180.0 && s.getLongitude() <= 180.0, "Longitud dentro de rango");
        }
    }

    @Test
    void testAddSupplier() {
        Supplier newSupplier = new Supplier(
                null,
                "Proveedor Test San Rafael",
                "test@sanrafael.com",
                "+54 260 411-2233",
                "Av. Hipólito Yrigoyen 500, San Rafael, Mendoza",
                -34.6177,
                -68.3301,
                "Componentes de Red",
                "San Rafael"
        );

        Supplier created = supplierService.addSupplier(newSupplier);
        assertNotNull(created.getId());
        assertEquals("Proveedor Test San Rafael", created.getName());

        Optional<Supplier> retrieved = supplierService.getSupplierById(created.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(-34.6177, retrieved.get().getLatitude());
        assertEquals(-68.3301, retrieved.get().getLongitude());
    }

    @Test
    void testDeleteSupplier() {
        List<Supplier> initialList = supplierService.getAllSuppliers();
        int initialCount = initialList.size();
        Long idToDelete = initialList.get(0).getId();

        boolean deleted = supplierService.deleteSupplier(idToDelete);
        assertTrue(deleted);
        assertEquals(initialCount - 1, supplierService.getAllSuppliers().size());
        assertTrue(supplierService.getSupplierById(idToDelete).isEmpty());
    }
}
