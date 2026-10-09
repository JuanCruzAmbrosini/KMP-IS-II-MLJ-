package com.biblioteca.servidor;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Domicilio;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Localidad;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.service.ReporteExcelService;
import com.biblioteca.servidor.service.ReportePdfService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportesServiceTest {

    @Test
    public void testGeneracionPdfAlquileres() {
        ReportePdfService pdfService = new ReportePdfService();

        Localidad loc = new Localidad(1L, "Mendoza Capital");
        Domicilio dom = new Domicilio(1L, "San Martin", 1040, "-32.88", "-68.84", loc);
        Autor autor = new Autor(1L, "Gabriel", "Garcia Marquez", "Nobel");
        Libro libro = new Libro(1L, "Cien Anos de Soledad", 1967, "Realismo Magico", 417, "GGM", LocalDate.now().plusDays(1), List.of(autor));

        Persona persona = new Persona(1L, "Juan", "Perez", 12345678, "juan@test.com", LocalDate.of(1990, 5, 20), dom, new ArrayList<>(List.of(libro)));

        byte[] pdfBytes = pdfService.generarReportePersonasAlquileres(List.of(persona));

        Assertions.assertNotNull(pdfBytes);
        Assertions.assertTrue(pdfBytes.length > 500, "El PDF generado debe contener bytes válidos");
        // Verificar firma de archivo PDF (%PDF-)
        String header = new String(pdfBytes, 0, 5);
        Assertions.assertEquals("%PDF-", header, "La cabecera debe ser de tipo PDF");
    }

    @Test
    public void testGeneracionExcelLibrosDisponibles() {
        ReporteExcelService excelService = new ReporteExcelService();

        Autor autor = new Autor(1L, "Jorge Luis", "Borges", "Escritor");
        Libro libro = new Libro(2L, "El Aleph", 1949, "Cuentos", 146, "JLB", null, List.of(autor));

        byte[] excelBytes = excelService.generarReporteLibrosDisponibles(List.of(libro));

        Assertions.assertNotNull(excelBytes);
        Assertions.assertTrue(excelBytes.length > 1000, "El archivo XLSX generado debe contener bytes válidos");
        // Verificar firma ZIP/OOXML (PK..)
        Assertions.assertEquals(0x50, excelBytes[0] & 0xFF);
        Assertions.assertEquals(0x4B, excelBytes[1] & 0xFF);
    }
}
