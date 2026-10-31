package modelo;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import modelo.DetalleVenta;
import modelo.Venta;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.Paragraph;
import org.openpdf.text.PageSize;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

public class ComprobanteVentaPDF {

    public static File guardarPDF(Venta venta, Component ventanaPadre)
            throws IOException, DocumentException {

        if (venta == null || venta.getDetallesVenta().isEmpty()) {
            throw new IllegalArgumentException(
                    "La venta no tiene productos para generar el comprobante."
            );
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar comprobante de venta");
        selector.setFileFilter(
                new FileNameExtensionFilter("Archivo PDF (*.pdf)", "pdf")
        );
        selector.setSelectedFile(
                new File("Comprobante_" + venta.getNumeroVenta() + ".pdf")
        );

        int opcion = selector.showSaveDialog(ventanaPadre);

        if (opcion != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getAbsolutePath() + ".pdf");
        }

        Document documento = new Document(PageSize.A4, 36, 36, 36, 36);

        try (FileOutputStream salida = new FileOutputStream(archivo)) {
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font negrita = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font subtitulo = new Font(Font.HELVETICA, 14, Font.BOLD);

            documento.add(new Paragraph("COMPROBANTE DE VENTA", titulo));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Proforma: Apreciable usuario este es un comprobante meramente interno", subtitulo));
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "Número de venta: " + venta.getNumeroVenta()
            ));
            documento.add(new Paragraph(
                    "Fecha: " + venta.getFecha()
            ));
            documento.add(new Paragraph(
                    "Cliente: " + venta.getDescripcionCliente()
            ));
            documento.add(new Paragraph(
                    "Forma de pago: " + venta.getFormaPago()
            ));
            documento.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(
                    new float[]{3.2f, 1.2f, 1.6f, 1.6f, 1.2f, 1.6f}
            );
            tabla.setWidthPercentage(100);

            agregarCelda(tabla, "Producto", negrita);
            agregarCelda(tabla, "Cantidad", negrita);
            agregarCelda(tabla, "Precio unitario", negrita);
            agregarCelda(tabla, "Subtotal", negrita);
            agregarCelda(tabla, "IVA", negrita);
            agregarCelda(tabla, "Total", negrita);

            for (DetalleVenta detalle : venta.getDetallesVenta()) {
                String nombreProducto = detalle.getProducto() == null
                        ? "Producto"
                        : detalle.getProducto().getNombre();

                tabla.addCell(nombreProducto);
                tabla.addCell(String.valueOf(detalle.getCantidadSolicitada()));
                tabla.addCell(moneda(detalle.getPrecioUnitario()));
                tabla.addCell(moneda(detalle.getSubtotal()));
                tabla.addCell(moneda(detalle.getIvaLinea()));
                tabla.addCell(moneda(detalle.getTotalLinea()));
            }

            documento.add(tabla);
            documento.add(new Paragraph(" "));

            documento.add(new Paragraph(
                    "Subtotal: " + moneda(venta.getSubtotalGeneral())
            ));

            documento.add(new Paragraph(
                    "Descuento aplicado (ya incluido en el total): "
                    + moneda(venta.getTotalDescuentos())
            ));

            documento.add(new Paragraph(
                    "IVA: " + moneda(venta.getTotalIva())
            ));
            documento.add(new Paragraph(
                    "TOTAL: " + moneda(venta.getTotalFinal()), negrita
            ));

            documento.close();
        }
        return archivo;
    }

    private static void agregarCelda(
            PdfPTable tabla, String texto, Font fuente) {

        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        tabla.addCell(celda);
    }

    private static String moneda(BigDecimal valor) {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }
        return "Q " + valor.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
