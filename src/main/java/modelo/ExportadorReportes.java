package modelo;

import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.openpdf.text.Font;

import java.util.Locale;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExportadorReportes {

    private ExportadorReportes() {
    }

    public static void exportarExcel(
            String archivo,
            String resumen,
            JTable... tablas) throws IOException {

        try (Workbook libro = new XSSFWorkbook()) {

            org.apache.poi.ss.usermodel.Font fuenteTitulo
                    = libro.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 16);
            fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());

            CellStyle estiloTitulo = libro.createCellStyle();
            estiloTitulo.setFont(fuenteTitulo);
            estiloTitulo.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex());
            estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloTitulo.setVerticalAlignment(VerticalAlignment.CENTER);

            org.apache.poi.ss.usermodel.Font fuenteEtiqueta
                    = libro.createFont();
            fuenteEtiqueta.setBold(true);

            CellStyle estiloEtiqueta = libro.createCellStyle();
            estiloEtiqueta.setFont(fuenteEtiqueta);
            estiloEtiqueta.setFillForegroundColor(
                    IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            estiloEtiqueta.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloEtiqueta.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle estiloValor = libro.createCellStyle();
            estiloValor.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloValor.setWrapText(true);

            org.apache.poi.ss.usermodel.Font fuenteEncabezado
                    = libro.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());

            CellStyle estiloEncabezado = libro.createCellStyle();
            estiloEncabezado.setFont(fuenteEncabezado);
            estiloEncabezado.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloEncabezado.setAlignment(HorizontalAlignment.CENTER);
            estiloEncabezado.setVerticalAlignment(VerticalAlignment.CENTER);
            estiloEncabezado.setWrapText(true);

            CellStyle estiloTexto = libro.createCellStyle();
            estiloTexto.setVerticalAlignment(VerticalAlignment.CENTER);

            DataFormat formatos = libro.createDataFormat();

            CellStyle estiloNumero = libro.createCellStyle();
            estiloNumero.setDataFormat(
                    formatos.getFormat("#,##0.##"));

            CellStyle estiloMoneda = libro.createCellStyle();
            estiloMoneda.setDataFormat(
                    formatos.getFormat("\"Q\" #,##0.00"));

            Sheet hojaResumen = libro.createSheet("Resumen");

            Row titulo = hojaResumen.createRow(0);
            titulo.setHeightInPoints(30);

            Cell celdaTitulo = titulo.createCell(0);
            celdaTitulo.setCellValue("Reporte de ventas");
            celdaTitulo.setCellStyle(estiloTitulo);

            hojaResumen.addMergedRegion(
                    new CellRangeAddress(0, 0, 0, 1));

            String[] datos = resumen == null
                    ? new String[0]
                    : resumen.split("\\s*\\|\\s*");

            int filaActual = 2;

            for (String dato : datos) {

                if (dato == null || dato.trim().isEmpty()) {
                    continue;
                }

                int separador = dato.indexOf(':');

                Row fila = hojaResumen.createRow(filaActual++);

                Cell etiqueta = fila.createCell(0);
                Cell valor = fila.createCell(1);

                if (separador >= 0) {

                    etiqueta.setCellValue(
                            dato.substring(0, separador).trim());

                    valor.setCellValue(
                            dato.substring(separador + 1).trim());

                } else {

                    etiqueta.setCellValue("Información");
                    valor.setCellValue(dato.trim());
                }

                etiqueta.setCellStyle(estiloEtiqueta);
                valor.setCellStyle(estiloValor);
            }

            hojaResumen.setColumnWidth(0, 6500);
            hojaResumen.setColumnWidth(1, 11000);
            hojaResumen.createFreezePane(0, 2);

            String[] nombresHojas = {
                "Ventas", "Productos", "Actividad"
            };

            for (int t = 0; tablas != null && t < tablas.length; t++) {

                JTable tablaOrigen = tablas[t];

                if (tablaOrigen == null) {
                    continue;
                }

                TableModel modelo = tablaOrigen.getModel();

                String nombreHoja = t < nombresHojas.length
                        ? nombresHojas[t]
                        : "Tabla" + (t + 1);

                Sheet hoja = libro.createSheet(nombreHoja);

                Row encabezado = hoja.createRow(0);
                encabezado.setHeightInPoints(30);

                for (int c = 0; c < modelo.getColumnCount(); c++) {

                    var celdaEncabezado = encabezado.createCell(c);
                    celdaEncabezado.setCellValue(modelo.getColumnName(c));
                    celdaEncabezado.setCellStyle(estiloEncabezado);
                }

                for (int r = 0; r < modelo.getRowCount(); r++) {

                    Row fila = hoja.createRow(r + 1);

                    for (int c = 0; c < modelo.getColumnCount(); c++) {

                        Object valor = modelo.getValueAt(r, c);
                        Cell celda = fila.createCell(c);

                        if (valor instanceof Number numero) {

                            celda.setCellValue(numero.doubleValue());

                            String nombreColumna
                                    = modelo.getColumnName(c)
                                            .toLowerCase(Locale.ROOT);

                            if (nombreColumna.contains("total")
                                    || nombreColumna.contains("subtotal")
                                    || nombreColumna.contains("descuento")
                                    || nombreColumna.equals("iva")
                                    || nombreColumna.contains("precio")) {

                                celda.setCellStyle(estiloMoneda);

                            } else {

                                celda.setCellStyle(estiloNumero);
                            }

                        } else {

                            celda.setCellValue(
                                    valor == null ? "" : valor.toString());

                            celda.setCellStyle(estiloTexto);
                        }
                    }
                }

                for (int c = 0; c < modelo.getColumnCount(); c++) {

                    hoja.autoSizeColumn(c);

                    int ancho = hoja.getColumnWidth(c);

                    hoja.setColumnWidth(
                            c,
                            Math.min(ancho + 500, 14000));
                }

                hoja.createFreezePane(0, 1);

                if (modelo.getColumnCount() > 0) {

                    hoja.setAutoFilter(
                            new CellRangeAddress(
                                    0,
                                    Math.max(0, modelo.getRowCount()),
                                    0,
                                    modelo.getColumnCount() - 1));
                }
            }

            try (FileOutputStream salida
                    = new FileOutputStream(archivo)) {

                libro.write(salida);
            }
        }
    }

    public static void exportarPdf(
            String archivo,
            String resumen,
            JTable... tablas) throws IOException, DocumentException {

        Document documento = new Document(
                PageSize.A4.rotate(), 28, 28, 28, 28
        );

        try (FileOutputStream salida = new FileOutputStream(archivo)) {

            PdfWriter.getInstance(documento, salida);
            documento.open();

            try {

                documento.add(new Paragraph(
                        "Informe de reportes",
                        new Font(Font.HELVETICA, 16, Font.BOLD)
                ));

                documento.add(new Paragraph(" "));

                Font fuenteResumen = new Font(Font.HELVETICA, 10, Font.NORMAL);
                Font fuenteEtiqueta = new Font(Font.HELVETICA, 10, Font.BOLD);

                if (resumen != null && !resumen.trim().isEmpty()) {

                    String[] datos = resumen.split("\\s*\\|\\s*");

                    for (String dato : datos) {
                        Paragraph linea = new Paragraph();

                        int separador = dato.indexOf(':');

                        if (separador >= 0) {
                            String etiqueta = dato.substring(0, separador + 1);
                            String valor = dato.substring(separador + 1).trim();

                            linea.add(new Phrase(etiqueta + " ", fuenteEtiqueta));
                            linea.add(new Phrase(valor, fuenteResumen));
                        } else {
                            linea.add(new Phrase(dato.trim(), fuenteResumen));
                        }

                        linea.setSpacingAfter(4f);
                        documento.add(linea);
                    }
                }

                documento.add(new Paragraph(" "));
                documento.add(new Paragraph(" "));

                String[] titulos = {
                    "Ventas por período",
                    "Productos más vendidos",
                    "Actividad reciente"
                };

                for (int t = 0; t < tablas.length; t++) {

                    JTable tablaOrigen = tablas[t];

                    if (tablaOrigen == null) {
                        continue;
                    }

                    TableModel modelo = tablaOrigen.getModel();

                    String titulo = t < titulos.length
                            ? titulos[t]
                            : "Tabla " + (t + 1);

                    documento.add(new Paragraph(
                            titulo,
                            new Font(Font.HELVETICA, 12, Font.BOLD)
                    ));

                    documento.add(new Paragraph(" "));

                    if (modelo.getColumnCount() > 0) {

                        PdfPTable tablaPDF
                                = new PdfPTable(modelo.getColumnCount());

                        tablaPDF.setWidthPercentage(100);
                        tablaPDF.setSpacingAfter(10f);

                        for (int c = 0;
                                c < modelo.getColumnCount(); c++) {

                            tablaPDF.addCell(
                                    new Phrase(
                                            modelo.getColumnName(c) == null
                                            ? ""
                                            : modelo.getColumnName(c)
                                    )
                            );
                        }

                        for (int r = 0;
                                r < modelo.getRowCount(); r++) {

                            for (int c = 0;
                                    c < modelo.getColumnCount(); c++) {

                                Object valor = modelo.getValueAt(r, c);

                                tablaPDF.addCell(
                                        valor == null
                                                ? ""
                                                : valor.toString()
                                );
                            }
                        }

                        documento.add(tablaPDF);
                    }
                }

            } finally {
                if (documento.isOpen()) {
                    documento.close();
                }
            }
        }
    }

}
