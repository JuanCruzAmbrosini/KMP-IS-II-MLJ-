package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de generar el reporte en formato Excel (.xlsx)
 * correspondiente al Inciso 2.d: "Listado de libros disponibles".
 * Utiliza Apache POI de acuerdo a la guía audiovisual.
 */
@Service
public class ReporteExcelService {

    private static final DateTimeFormatter FECHA_HORA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] generarReporteLibrosDisponibles(List<Libro> librosDisponibles) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Libros Disponibles");
            sheet.setDisplayGridlines(true);

            // ==================== ESTILOS ====================
            // 1. Estilo para el Título Principal
            CellStyle estiloTitulo = workbook.createCellStyle();
            Font fontTitulo = workbook.createFont();
            fontTitulo.setFontName("Calibri");
            fontTitulo.setFontHeightInPoints((short) 16);
            fontTitulo.setBold(true);
            fontTitulo.setColor(IndexedColors.WHITE.getIndex());
            estiloTitulo.setFont(fontTitulo);
            estiloTitulo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
            estiloTitulo.setVerticalAlignment(VerticalAlignment.CENTER);

            // 2. Estilo para el Subtítulo / Metadata
            CellStyle estiloSubtitulo = workbook.createCellStyle();
            Font fontSubtitulo = workbook.createFont();
            fontSubtitulo.setFontName("Calibri");
            fontSubtitulo.setFontHeightInPoints((short) 10);
            fontSubtitulo.setItalic(true);
            fontSubtitulo.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            estiloSubtitulo.setFont(fontSubtitulo);
            estiloSubtitulo.setAlignment(HorizontalAlignment.CENTER);
            estiloSubtitulo.setVerticalAlignment(VerticalAlignment.CENTER);

            // 3. Estilo para Encabezados de Columnas
            CellStyle estiloHeader = workbook.createCellStyle();
            Font fontHeader = workbook.createFont();
            fontHeader.setFontName("Calibri");
            fontHeader.setFontHeightInPoints((short) 11);
            fontHeader.setBold(true);
            fontHeader.setColor(IndexedColors.WHITE.getIndex());
            estiloHeader.setFont(fontHeader);
            estiloHeader.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloHeader.setAlignment(HorizontalAlignment.CENTER);
            estiloHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            aplicarBordes(estiloHeader);

            // 4. Estilos para Celdas de Datos (Normal y Fila Par para cebra)
            CellStyle estiloDato = workbook.createCellStyle();
            Font fontDato = workbook.createFont();
            fontDato.setFontName("Calibri");
            fontDato.setFontHeightInPoints((short) 10);
            estiloDato.setFont(fontDato);
            aplicarBordes(estiloDato);

            CellStyle estiloDatoPar = workbook.createCellStyle();
            estiloDatoPar.setFont(fontDato);
            estiloDatoPar.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            // Usamos un fondo muy suave o sin relleno
            aplicarBordes(estiloDatoPar);

            // Centrado
            CellStyle estiloCentrado = workbook.createCellStyle();
            estiloCentrado.cloneStyleFrom(estiloDato);
            estiloCentrado.setAlignment(HorizontalAlignment.CENTER);

            // Numérico (Páginas / Año)
            CellStyle estiloNumero = workbook.createCellStyle();
            estiloNumero.cloneStyleFrom(estiloDato);
            estiloNumero.setAlignment(HorizontalAlignment.RIGHT);

            // Badge Estado Disponible (Verde claro)
            CellStyle estiloDisponible = workbook.createCellStyle();
            Font fontDisponible = workbook.createFont();
            fontDisponible.setFontName("Calibri");
            fontDisponible.setFontHeightInPoints((short) 10);
            fontDisponible.setBold(true);
            fontDisponible.setColor(IndexedColors.DARK_GREEN.getIndex());
            estiloDisponible.setFont(fontDisponible);
            estiloDisponible.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            estiloDisponible.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloDisponible.setAlignment(HorizontalAlignment.CENTER);
            aplicarBordes(estiloDisponible);

            // Estilo Fila Total
            CellStyle estiloTotal = workbook.createCellStyle();
            Font fontTotal = workbook.createFont();
            fontTotal.setFontName("Calibri");
            fontTotal.setFontHeightInPoints((short) 11);
            fontTotal.setBold(true);
            fontTotal.setColor(IndexedColors.DARK_BLUE.getIndex());
            estiloTotal.setFont(fontTotal);
            estiloTotal.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            estiloTotal.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            aplicarBordes(estiloTotal);

            // ==================== CONSTRUCCIÓN DEL DOCUMENTO ====================
            int filaIdx = 0;

            // Fila 0: Título Principal
            Row filaTitulo = sheet.createRow(filaIdx++);
            filaTitulo.setHeightInPoints(32);
            Cell celdaTitulo = filaTitulo.createCell(0);
            celdaTitulo.setCellValue("SISTEMA DE GESTIÓN DE BIBLIOTECA - CATÁLOGO DE LIBROS DISPONIBLES");
            celdaTitulo.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            // Fila 1: Subtítulo con metadata
            Row filaSub = sheet.createRow(filaIdx++);
            filaSub.setHeightInPoints(20);
            Cell celdaSub = filaSub.createCell(0);
            String fechaGeneracion = LocalDateTime.now().format(FECHA_HORA_FMT);
            int cantDisponibles = (librosDisponibles != null) ? librosDisponibles.size() : 0;
            celdaSub.setCellValue("Reporte emitido el: " + fechaGeneracion + " | Estado: Libros en inventario sin préstamos activos | Total: " + cantDisponibles);
            celdaSub.setCellStyle(estiloSubtitulo);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            // Fila 2: Separador en blanco
            sheet.createRow(filaIdx++);

            // Fila 3: Encabezados de Tabla
            Row filaHeaders = sheet.createRow(filaIdx++);
            filaHeaders.setHeightInPoints(26);
            String[] headers = {
                    "ID Libro", "Título del Libro", "Autor(es)", "Año Publicación", "Género", "Páginas", "Estado"
            };
            for (int i = 0; i < headers.length; i++) {
                Cell c = filaHeaders.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(estiloHeader);
            }

            // Filas de Datos
            if (librosDisponibles == null || librosDisponibles.isEmpty()) {
                Row filaVacia = sheet.createRow(filaIdx++);
                Cell cVacia = filaVacia.createCell(0);
                cVacia.setCellValue("Actualmente no existen libros disponibles sin préstamo en el catálogo.");
                cVacia.setCellStyle(estiloDato);
                sheet.addMergedRegion(new CellRangeAddress(filaIdx - 1, filaIdx - 1, 0, 6));
            } else {
                for (Libro lib : librosDisponibles) {
                    Row fila = sheet.createRow(filaIdx++);
                    fila.setHeightInPoints(20);

                    // 0. ID
                    Cell cId = fila.createCell(0);
                    cId.setCellValue(lib.getId());
                    cId.setCellStyle(estiloCentrado);

                    // 1. Título
                    Cell cTitulo = fila.createCell(1);
                    cTitulo.setCellValue(lib.getTitulo() != null ? lib.getTitulo() : "");
                    cTitulo.setCellStyle(estiloDato);

                    // 2. Autores
                    String autoresTxt = "";
                    if (lib.getAutores() != null && !lib.getAutores().isEmpty()) {
                        autoresTxt = lib.getAutores().stream()
                                .map(a -> a.getNombre() + " " + a.getApellido())
                                .collect(Collectors.joining(", "));
                    } else if (lib.getAutor() != null && !lib.getAutor().isBlank()) {
                        autoresTxt = lib.getAutor();
                    } else {
                        autoresTxt = "Sin autor asignado";
                    }
                    Cell cAutores = fila.createCell(2);
                    cAutores.setCellValue(autoresTxt);
                    cAutores.setCellStyle(estiloDato);

                    // 3. Año
                    Cell cAnio = fila.createCell(3);
                    cAnio.setCellValue(lib.getFecha());
                    cAnio.setCellStyle(estiloCentrado);

                    // 4. Género
                    Cell cGenero = fila.createCell(4);
                    cGenero.setCellValue(lib.getGenero() != null ? lib.getGenero() : "");
                    cGenero.setCellStyle(estiloDato);

                    // 5. Páginas
                    Cell cPaginas = fila.createCell(5);
                    cPaginas.setCellValue(lib.getPaginas());
                    cPaginas.setCellStyle(estiloNumero);

                    // 6. Estado
                    Cell cEstado = fila.createCell(6);
                    cEstado.setCellValue("DISPONIBLE");
                    cEstado.setCellStyle(estiloDisponible);
                }
            }

            // Fila Resumen al final
            Row filaTotal = sheet.createRow(filaIdx++);
            filaTotal.setHeightInPoints(24);
            Cell cTotalLabel = filaTotal.createCell(0);
            cTotalLabel.setCellValue("TOTAL DE LIBROS DISPONIBLES PARA ALQUILER:");
            cTotalLabel.setCellStyle(estiloTotal);
            sheet.addMergedRegion(new CellRangeAddress(filaIdx - 1, filaIdx - 1, 0, 4));

            for (int col = 1; col <= 4; col++) {
                Cell c = filaTotal.createCell(col);
                c.setCellStyle(estiloTotal);
            }

            Cell cTotalNum = filaTotal.createCell(5);
            cTotalNum.setCellValue(cantDisponibles);
            cTotalNum.setCellStyle(estiloTotal);

            Cell cTotalEst = filaTotal.createCell(6);
            cTotalEst.setCellValue("En Estantería");
            cTotalEst.setCellStyle(estiloTotal);

            // Ajustar automáticamente el ancho de todas las columnas con un margen extra
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int anchoActual = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.max(anchoActual + 1200, 3500));
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel de libros disponibles", e);
        }
    }

    private void aplicarBordes(CellStyle estilo) {
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        estilo.setTopBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
        estilo.setBottomBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
        estilo.setLeftBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
        estilo.setRightBorderColor(IndexedColors.GREY_40_PERCENT.getIndex());
    }
}

