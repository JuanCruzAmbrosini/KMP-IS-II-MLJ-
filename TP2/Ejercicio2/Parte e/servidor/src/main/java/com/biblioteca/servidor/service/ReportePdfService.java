package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio encargado de generar el reporte en formato PDF
 * correspondiente al Inciso 2.d: "Listado de las personas que realizaron alquiler de libros".
 * Utiliza OpenPDF (compatible con iText) de acuerdo a la guía audiovisual.
 */
@Service
public class ReportePdfService {

    private static final DateTimeFormatter FECHA_HORA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Paleta de colores profesional
    private static final Color COLOR_PRIMARIO = new Color(30, 41, 59);      // Slate 800 (#1e293b)
    private static final Color COLOR_SECUNDARIO = new Color(71, 85, 105);   // Slate 600 (#475569)
    private static final Color COLOR_ACENTO = new Color(14, 116, 144);       // Cyan 700 (#0e7490)
    private static final Color COLOR_FILA_PAR = new Color(248, 250, 252);    // Slate 50 (#f8fafc)
    private static final Color COLOR_FILA_IMPAR = Color.WHITE;
    private static final Color COLOR_BORDE = new Color(226, 232, 240);       // Slate 200 (#e2e8f0)
    private static final Color COLOR_RESUMEN = new Color(241, 245, 249);     // Slate 100 (#f1f5f9)

    public byte[] generarReportePersonasAlquileres(List<Persona> personas) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Orientación Horizontal (Landscape) para una tabla de préstamos espaciosa y legible
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Tipografías
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_PRIMARIO);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 11, COLOR_SECUNDARIO);
            Font fontMeta = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, COLOR_SECUNDARIO);
            Font fontHeaderTabla = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font fontCelda = FontFactory.getFont(FontFactory.HELVETICA, 9, COLOR_PRIMARIO);
            Font fontCeldaBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, COLOR_PRIMARIO);
            Font fontCeldaLibro = FontFactory.getFont(FontFactory.HELVETICA, 8, COLOR_PRIMARIO);
            Font fontResumen = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_PRIMARIO);

            // 1. Encabezado del documento
            Paragraph pTitulo = new Paragraph("SISTEMA DE GESTIÓN DE BIBLIOTECA", fontTitulo);
            pTitulo.setAlignment(Element.ALIGN_CENTER);
            pTitulo.setSpacingAfter(4);
            document.add(pTitulo);

            Paragraph pSub = new Paragraph("Reporte Oficial: Listado de Personas con Alquileres de Libros Activos", fontSubtitulo);
            pSub.setAlignment(Element.ALIGN_CENTER);
            pSub.setSpacingAfter(6);
            document.add(pSub);

            String fechaEmision = LocalDateTime.now().format(FECHA_HORA_FMT);
            Paragraph pMeta = new Paragraph("Fecha de Emisión: " + fechaEmision + "  |  Origen: Sistema de Préstamos Central", fontMeta);
            pMeta.setAlignment(Element.ALIGN_CENTER);
            pMeta.setSpacingAfter(18);
            document.add(pMeta);

            // 2. Tabla principal de datos
            // Columnas: ID | Nombre Completo | DNI | Contacto (Email) | Domicilio | Libros Alquilados | Cant.
            float[] anchos = {5f, 18f, 11f, 20f, 20f, 26f, 7f};
            PdfPTable tabla = new PdfPTable(anchos);
            tabla.setWidthPercentage(100);
            tabla.setHeaderRows(1);

            // Encabezados de columnas
            String[] cabeceras = {
                    "#", "Persona", "DNI", "Correo Electrónico", "Domicilio & Localidad", "Libros Alquilados (Vencimiento)", "Total"
            };

            for (String cabecera : cabeceras) {
                PdfPCell celda = new PdfPCell(new Phrase(cabecera, fontHeaderTabla));
                celda.setBackgroundColor(COLOR_PRIMARIO);
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celda.setPadding(7);
                celda.setBorderColor(COLOR_PRIMARIO);
                tabla.addCell(celda);
            }

            // Filas de datos
            int totalLibrosPrestados = 0;
            int indice = 0;

            if (personas == null || personas.isEmpty()) {
                PdfPCell celdaVacia = new PdfPCell(new Phrase("No se registran personas con alquileres activos en el sistema.", fontCeldaBold));
                celdaVacia.setColspan(7);
                celdaVacia.setHorizontalAlignment(Element.ALIGN_CENTER);
                celdaVacia.setPadding(15);
                tabla.addCell(celdaVacia);
            } else {
                for (Persona p : personas) {
                    Color fondoFila = (indice % 2 == 0) ? COLOR_FILA_PAR : COLOR_FILA_IMPAR;
                    int cantLibros = (p.getLibros() != null) ? p.getLibros().size() : 0;
                    totalLibrosPrestados += cantLibros;

                    // 1. ID
                    PdfPCell cId = new PdfPCell(new Phrase(String.valueOf(p.getId()), fontCeldaBold));
                    cId.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cId.setBackgroundColor(fondoFila);
                    cId.setBorderColor(COLOR_BORDE);
                    cId.setPadding(6);
                    tabla.addCell(cId);

                    // 2. Nombre y Apellido
                    PdfPCell cNombre = new PdfPCell(new Phrase(p.getApellido() + ", " + p.getNombre(), fontCeldaBold));
                    cNombre.setBackgroundColor(fondoFila);
                    cNombre.setBorderColor(COLOR_BORDE);
                    cNombre.setPadding(6);
                    tabla.addCell(cNombre);

                    // 3. DNI
                    PdfPCell cDni = new PdfPCell(new Phrase(String.valueOf(p.getDni()), fontCelda));
                    cDni.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cDni.setBackgroundColor(fondoFila);
                    cDni.setBorderColor(COLOR_BORDE);
                    cDni.setPadding(6);
                    tabla.addCell(cDni);

                    // 4. Email
                    String emailTxt = (p.getEmail() != null && !p.getEmail().isBlank()) ? p.getEmail() : "Sin correo";
                    PdfPCell cEmail = new PdfPCell(new Phrase(emailTxt, fontCelda));
                    cEmail.setBackgroundColor(fondoFila);
                    cEmail.setBorderColor(COLOR_BORDE);
                    cEmail.setPadding(6);
                    tabla.addCell(cEmail);

                    // 5. Domicilio
                    String domTxt = "Sin domicilio";
                    if (p.getDomicilio() != null) {
                        domTxt = p.getDomicilio().getCalle() + " " + p.getDomicilio().getNumero();
                        if (p.getDomicilio().getLocalidad() != null) {
                            domTxt += " (" + p.getDomicilio().getLocalidad().getDenominacion() + ")";
                        }
                    }
                    PdfPCell cDom = new PdfPCell(new Phrase(domTxt, fontCelda));
                    cDom.setBackgroundColor(fondoFila);
                    cDom.setBorderColor(COLOR_BORDE);
                    cDom.setPadding(6);
                    tabla.addCell(cDom);

                    // 6. Libros Alquilados
                    Phrase fraseLibros = new Phrase();
                    if (p.getLibros() != null && !p.getLibros().isEmpty()) {
                        for (int i = 0; i < p.getLibros().size(); i++) {
                            Libro lib = p.getLibros().get(i);
                            String vencTxt = (lib.getFechaVencimientoDevolucion() != null)
                                    ? lib.getFechaVencimientoDevolucion().format(FECHA_FMT)
                                    : "Sin fecha";
                            String linea = "• " + lib.getTitulo() + " [Vence: " + vencTxt + "]";
                            if (i < p.getLibros().size() - 1) {
                                linea += "\n";
                            }
                            fraseLibros.add(new Chunk(linea, fontCeldaLibro));
                        }
                    } else {
                        fraseLibros.add(new Chunk("Sin libros", fontCeldaLibro));
                    }
                    PdfPCell cLibros = new PdfPCell(fraseLibros);
                    cLibros.setBackgroundColor(fondoFila);
                    cLibros.setBorderColor(COLOR_BORDE);
                    cLibros.setPadding(6);
                    tabla.addCell(cLibros);

                    // 7. Cantidad
                    PdfPCell cCant = new PdfPCell(new Phrase(String.valueOf(cantLibros), fontCeldaBold));
                    cCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cCant.setBackgroundColor(fondoFila);
                    cCant.setBorderColor(COLOR_BORDE);
                    cCant.setPadding(6);
                    tabla.addCell(cCant);

                    indice++;
                }
            }

            document.add(tabla);

            // 3. Cuadro de resumen estadístico al pie de la tabla
            document.add(new Paragraph(" "));
            PdfPTable tablaResumen = new PdfPTable(new float[]{60f, 40f});
            tablaResumen.setWidthPercentage(100);

            PdfPCell cResumenEtiqueta = new PdfPCell(new Phrase(
                    "TOTALES GENERALES DEL REPORTE:\n" +
                            "• Personas con Préstamos Activos: " + (personas != null ? personas.size() : 0) + "\n" +
                            "• Total de Libros en Alquiler: " + totalLibrosPrestados,
                    fontResumen
            ));
            cResumenEtiqueta.setBackgroundColor(COLOR_RESUMEN);
            cResumenEtiqueta.setBorderColor(COLOR_BORDE);
            cResumenEtiqueta.setPadding(8);
            tablaResumen.addCell(cResumenEtiqueta);

            PdfPCell cFirma = new PdfPCell(new Phrase(
                    "Documento generado automáticamente conforme al Ejercicio 2.d.\n" +
                            "Ingeniería del Software II - Universidad Tecnológica Nacional (FRM)",
                    fontMeta
            ));
            cFirma.setBackgroundColor(COLOR_RESUMEN);
            cFirma.setBorderColor(COLOR_BORDE);
            cFirma.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cFirma.setPadding(8);
            tablaResumen.addCell(cFirma);

            document.add(tablaResumen);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el reporte PDF de personas con alquileres", e);
        }

        return out.toByteArray();
    }
}
