package ui;

import dao.HistorialDAO;
import modelo.RegistroHistorial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

public class PnlHistorialll extends javax.swing.JPanel {

    /**
     * Creates new form PnlHistorialll
     */
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private final HistorialDAO historialDAO = new HistorialDAO();
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PnlHistorialll() {
        initComponents();
        jLabel1.setText("Buscar por fecha o referencia");
        configurarTabla();
        configurarEventos();
        cargarHistorial();
    }

    private void exportarPdf() {
        if (tblReporte.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Historial.pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = fc.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".pdf");
        }

        Document doc = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(archivo));
            doc.open();

            Font fTitulo = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font fHeader = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font fCelda = new Font(Font.HELVETICA, 9);

            doc.add(new Paragraph("Historial de Compras y Ventas", fTitulo));
            doc.add(new Paragraph("Generado: "
                    + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fCelda));
            doc.add(new Paragraph(" "));

            PdfPTable pdf = new PdfPTable(tblReporte.getColumnCount());
            pdf.setWidthPercentage(100);

            for (int c = 0; c < tblReporte.getColumnCount(); c++) {
                PdfPCell h = new PdfPCell(new Phrase(tblReporte.getColumnName(c), fHeader));
                h.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                pdf.addCell(h);
            }
            for (int r = 0; r < tblReporte.getRowCount(); r++) {
                for (int c = 0; c < tblReporte.getColumnCount(); c++) {
                    Object v = tblReporte.getValueAt(r, c);
                    pdf.addCell(new Phrase(v == null ? "" : v.toString(), fCelda));
                }
            }
            doc.add(pdf);

            JOptionPane.showMessageDialog(this, "PDF generado correctamente.");
        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (doc.isOpen()) {
                doc.close();
            }
        }
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID Registro", "Fecha", "Tipo de Movimiento", "Referencia", "Total", "Usuario Responsable"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblReporte.setModel(modeloTabla);

        sorter = new TableRowSorter<>(modeloTabla);
        for (int i = 0; i < modeloTabla.getColumnCount(); i++) {
            sorter.setSortable(i, false);
        }
        tblReporte.setRowSorter(sorter);

        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        tblReporte.getColumnModel().getColumn(4).setCellRenderer(derecha);
    }

    private void configurarEventos() {
        dcDesdeActividad.addPropertyChangeListener("date", e -> cargarHistorial());
        dcHastaAdctividad.addPropertyChangeListener("date", e -> cargarHistorial());

        cbTipoHistorial.addActionListener(e -> cargarHistorial());

        txtBuscarPorFechaReferencia.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarBusqueda();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarBusqueda();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarBusqueda();
            }
        });
    }

    private void aplicarBusqueda() {
        String texto = txtBuscarPorFechaReferencia.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 1, 3));
        }
    }

    private LocalDate aLocalDate(java.util.Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private String tipoParaDao() {
        String sel = String.valueOf(cbTipoHistorial.getSelectedItem());
        if ("HISTORIAL DE VENTAS".equals(sel)) {
            return "VENTA";
        }
        if ("HISTORIAL DE PRODUCTOS".equals(sel)) {
            return "COMPRA";
        }
        return "TODOS";
    }

    private String tipoParaTabla(String tipo) {
        if ("COMPRA".equalsIgnoreCase(tipo)) {
            return "PRODUCTO";
        }
        return tipo;
    }

    private void cargarHistorial() {
        LocalDate desde = aLocalDate(dcDesdeActividad.getDate());
        LocalDate hasta = aLocalDate(dcHastaAdctividad.getDate());

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            JOptionPane.showMessageDialog(this, "La fecha 'Desde' no puede ser mayor que 'Hasta'.",
                    "Rango inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<RegistroHistorial> registros = historialDAO.obtenerHistorial(desde, hasta, tipoParaDao());

        modeloTabla.setRowCount(0);
        for (RegistroHistorial r : registros) {
            modeloTabla.addRow(new Object[]{
                r.getId(),
                r.getFecha() != null ? r.getFecha().format(FMT_FECHA) : "—",
                tipoParaTabla(r.getTipoMovimiento()),
                r.getReferencia(),
                r.getTotal() != null ? String.format("Q %,.2f", r.getTotal()) : "—",
                r.getUsuarioResponsable() != null ? r.getUsuarioResponsable() : "—"
            });
        }
        aplicarBusqueda();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel7 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        dcDesdeActividad = new com.toedter.calendar.JDateChooser();
        dcHastaAdctividad = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReporte = new javax.swing.JTable();
        txtBuscarPorFechaReferencia = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbTipoHistorial = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel7.setPreferredSize(new java.awt.Dimension(375, 269));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Actividad Reciente");

        jLabel5.setText("Desde:");

        jLabel16.setText("Hasta:");

        tblReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Registro", "Fecha", "Tipo de Movimiento", "Referencia", "Usuario Responsable"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblReporte);

        jLabel1.setText("Buscar por recha o referencia");

        jLabel2.setText("Tipo de Historial");

        cbTipoHistorial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "HISTORIAL DE VENTAS", "HISTORIAL DE PRODUCTOS" }));
        cbTipoHistorial.addActionListener(this::cbTipoHistorialActionPerformed);

        jButton1.setText("Exportar Informe Completo (PDF)");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dcDesdeActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel16)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dcHastaAdctividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(49, 49, 49)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(43, 43, 43)
                                        .addComponent(jLabel2))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(txtBuscarPorFechaReferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(cbTipoHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 149, Short.MAX_VALUE))
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel5)
                    .addComponent(dcDesdeActividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcHastaAdctividad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBuscarPorFechaReferencia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbTipoHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Historial");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel3)
                .addContainerGap(855, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 938, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel3)
                .addContainerGap(604, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        exportarPdf();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cbTipoHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTipoHistorialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbTipoHistorialActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbTipoHistorial;
    private com.toedter.calendar.JDateChooser dcDesdeActividad;
    private com.toedter.calendar.JDateChooser dcHastaAdctividad;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReporte;
    private javax.swing.JTextField txtBuscarPorFechaReferencia;
    // End of variables declaration//GEN-END:variables
}
