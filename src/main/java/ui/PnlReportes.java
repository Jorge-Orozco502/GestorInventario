package ui;

import modelo.ExportadorReportes;
import dao.ReporteDAO;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.openpdf.text.DocumentException;
import java.text.SimpleDateFormat;

public class PnlReportes extends javax.swing.JPanel {

    private modelo.Usuario usuarioActual;

    public PnlReportes() {
        this(null);
    }

    public PnlReportes(modelo.Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        initComponents();
        configurarBotonesReportes();
    }

    private void configurarBotonesReportes() {
        Calendar inicioMes = Calendar.getInstance();
        inicioMes.set(Calendar.DAY_OF_MONTH, 1);

        java.util.Date desdeMes = inicioMes.getTime();
        java.util.Date hoy = new java.util.Date();

        dcDesdeVentas.setDateFormatString("dd/MM/yyyy");
        dcDesdeVentas.setDate(desdeMes);
        dcHastaVentas.setDateFormatString("dd/MM/yyyy");
        dcHastaVentas.setDate(hoy);

        dcDesdeProductos.setDateFormatString("dd/MM/yyyy");
        dcDesdeProductos.setDate(desdeMes);
        dcHastaProductos.setDateFormatString("dd/MM/yyyy");
        dcHastaProductos.setDate(hoy);

        cmbMetodoPago.setModel(
                new javax.swing.DefaultComboBoxModel<>(
                        new String[]{
                            "TODOS",
                            "EFECTIVO",
                            "TARJETA",
                            "TRANSFERENCIA",
                            "CREDITO"
                        }
                )
        );

        jLabel2.setText("Ventas por período");
        jLabel11.setText("Ventas registradas");
        jLabel12.setText("Pendientes*");
        jLabel12.setToolTipText(
                "La tabla venta no tiene un campo de estado."
        );

        jTable4.setModel(new DefaultTableModel(
                new String[]{"Producto", "Cantidad vendida", "Total vendido"}, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });

        btnFiltrar.addActionListener(e -> filtrarVentasPorPeriodo());

        btnFiltrarMasVendidos.addActionListener(
                e -> filtrarProductosMasVendidos()
        );

        btnExportarEXCEL.addActionListener(e -> exportarExcel());

        filtrarVentasPorPeriodo();
        filtrarProductosMasVendidos();
    }

    private void filtrarProductosMasVendidos() {
        try {
            LocalDate desde = obtenerFecha(dcDesdeProductos);
            LocalDate hasta = obtenerFecha(dcHastaProductos);

            validarRango(desde, hasta, "productos más vendidos");

            ReporteDAO reporteDAO = new ReporteDAO();

            llenarTabla(
                    jTable4,
                    reporteDAO.productosMasVendidos(
                            desde,
                            hasta,
                            usuarioActual == null
                                    ? null
                                    : usuarioActual.getIdUsuario()
                    )
            );

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Revisa las fechas",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al consultar los productos: " + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void filtrarVentasPorPeriodo() {
        try {
            LocalDate desde = obtenerFecha(dcDesdeVentas);
            LocalDate hasta = obtenerFecha(dcHastaVentas);

            validarRango(desde, hasta, "ventas");

            ReporteDAO reporteDAO = new ReporteDAO();

            llenarTabla(
                    tblVentasPeriodo,
                    reporteDAO.ventas(
                            desde,
                            hasta,
                            String.valueOf(cmbMetodoPago.getSelectedItem()),
                            usuarioActual == null
                                    ? null
                                    : usuarioActual.getIdUsuario()
                    )
            );

            actualizarResumen();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Revisa las fechas",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al consultar las ventas: " + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private LocalDate obtenerFecha(
            com.toedter.calendar.JDateChooser selector) {

        java.util.Date fechaElegida = selector.getDate();

        if (fechaElegida == null) {
            return null;
        }

        return fechaElegida.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void validarRango(
            LocalDate desde,
            LocalDate hasta,
            String nombreRango) {

        if (desde == null || hasta == null) {
            throw new IllegalArgumentException(
                    "Selecciona las fechas Desde y Hasta de " + nombreRango + "."
            );
        }

        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha Desde no puede ser posterior a Hasta en "
                    + nombreRango + "."
            );
        }
    }

    private void filtrarReportes() {
        try {
            LocalDate desdeVentas = obtenerFecha(dcDesdeVentas);
            LocalDate hastaVentas = obtenerFecha(dcHastaVentas);

            LocalDate desdeProductos = obtenerFecha(dcDesdeProductos);
            LocalDate hastaProductos = obtenerFecha(dcHastaProductos);

            validarRango(desdeVentas, hastaVentas, "ventas");
            validarRango(desdeProductos, hastaProductos, "productos");

            ReporteDAO reporteDAO = new ReporteDAO();

            llenarTabla(
                    tblVentasPeriodo,
                    reporteDAO.ventas(
                            desdeVentas,
                            hastaVentas,
                            String.valueOf(cmbMetodoPago.getSelectedItem()),
                            usuarioActual == null ? null : usuarioActual.getIdUsuario()
                    )
            );

            llenarTabla(
                    jTable4,
                    reporteDAO.productosMasVendidos(
                            desdeProductos,
                            hastaProductos,
                            usuarioActual == null ? null : usuarioActual.getIdUsuario()
                    )
            );

            actualizarResumen();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Revisa las fechas",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No fue posible cargar el reporte: " + ex.getMessage(),
                    "Error de consulta",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void llenarTabla(
            javax.swing.JTable tabla,
            java.util.List<Object[]> filas) {

        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);

        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    private void actualizarResumen() {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        for (int fila = 0; fila < tblVentasPeriodo.getRowCount(); fila++) {
            Object valor = tblVentasPeriodo.getValueAt(fila, 6);

            if (valor instanceof java.math.BigDecimal monto) {
                total = total.add(monto);
            }
        }

        int cantidadVentas = tblVentasPeriodo.getRowCount();

        lblTotalVendido.setText(
                String.format(java.util.Locale.US, "Q %,.2f", total)
        );

        lblVentasCompletadas.setText(String.valueOf(cantidadVentas));

        lblVentasPendientes.setText("N/D");

        java.math.BigDecimal promedio = cantidadVentas == 0
                ? java.math.BigDecimal.ZERO
                : total.divide(
                        java.math.BigDecimal.valueOf(cantidadVentas),
                        2,
                        java.math.RoundingMode.HALF_UP
                );

        lblPromedioVenta.setText(
                String.format(java.util.Locale.US, "Q %,.2f", promedio)
        );
    }

    private String obtenerResumenActual() {

        SimpleDateFormat formatoFecha = new SimpleDateFormat(
                "d 'de' MMMM 'de' yyyy",
                new java.util.Locale("es", "GT")
        );

        java.util.Date fechaDesde = dcDesdeVentas.getDate();
        java.util.Date fechaHasta = dcHastaVentas.getDate();

        if (fechaDesde == null || fechaHasta == null) {
            throw new IllegalArgumentException(
                    "Seleccione las fechas de inicio y finalización."
            );
        }

        String desde = formatoFecha.format(fechaDesde);
        String hasta = formatoFecha.format(fechaHasta);

        return "Período: "
                + desde
                + " a "
                + hasta
                + " | Forma de pago: "
                + cmbMetodoPago.getSelectedItem()
                + " | Total vendido: "
                + lblTotalVendido.getText()
                + " | Ventas registradas: "
                + lblVentasCompletadas.getText()
                + " | Promedio por venta: "
                + lblPromedioVenta.getText();
    }

    private File elegirArchivo(
            String titulo,
            String extension,
            String descripcion) {

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle(titulo);
        selector.setSelectedFile(new File("Reporte_Ventas." + extension));
        selector.setFileFilter(
                new FileNameExtensionFilter(descripcion, extension)
        );

        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File archivo = selector.getSelectedFile();

        if (!archivo.getName().toLowerCase().endsWith("." + extension)) {
            archivo = new File(
                    archivo.getParentFile(),
                    archivo.getName() + "." + extension
            );
        }

        if (archivo.exists()) {
            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "El archivo ya existe. ¿Deseas reemplazarlo?",
                    "Confirmar reemplazo",
                    JOptionPane.YES_NO_OPTION
            );

            if (respuesta != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        return archivo;
    }

    private void exportarExcel() {
        File archivo = elegirArchivo(
                "Guardar reporte de Excel",
                "xlsx",
                "Libro de Excel (*.xlsx)"
        );

        if (archivo == null) {
            return;
        }

        try {
            ExportadorReportes.exportarExcel(
                    archivo.getAbsolutePath(),
                    obtenerResumenActual(),
                    tblVentasPeriodo,
                    jTable4
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Reporte Excel guardado en:\n" + archivo.getAbsolutePath()
            );
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo crear el Excel: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void exportarPdf() {
        File archivo = elegirArchivo(
                "Guardar informe PDF",
                "pdf",
                "Documento PDF (*.pdf)"
        );

        if (archivo == null) {
            return;
        }

        try {
            ExportadorReportes.exportarPdf(
                    archivo.getAbsolutePath(),
                    obtenerResumenActual(),
                    tblVentasPeriodo,
                    jTable4
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Informe PDF guardado en:\n" + archivo.getAbsolutePath()
            );
        } catch (java.io.IOException | DocumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo crear el PDF: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbMetodoPago = new javax.swing.JComboBox<>();
        btnFiltrar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVentasPeriodo = new javax.swing.JTable();
        dcDesdeVentas = new com.toedter.calendar.JDateChooser();
        dcHastaVentas = new com.toedter.calendar.JDateChooser();
        btnDescargarPDF = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblTotalVendido = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        lblVentasCompletadas = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        lblVentasPendientes = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        lblPromedioVenta = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tblProductosMasVendidos = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        dcDesdeProductos = new com.toedter.calendar.JDateChooser();
        dcHastaProductos = new com.toedter.calendar.JDateChooser();
        btnFiltrarMasVendidos = new javax.swing.JButton();
        btnExportarEXCEL = new javax.swing.JButton();

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setPreferredSize(new java.awt.Dimension(1110, 770));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Reportes");

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Mis Ventas por Período");

        jLabel3.setText("Desde:");

        jLabel7.setText("Hasta:");

        jLabel8.setText("Metodo de pago");

        cmbMetodoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODOS", "EFECTIVO", "TARJETA", "TRANSFERENCIA", "CREDITO" }));

        btnFiltrar.setBackground(new java.awt.Color(153, 255, 255));
        btnFiltrar.setText("Filtrar");

        tblVentasPeriodo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Venta", "Fecha", "Cliente", "Subtotal", "Descuento", "IVA", "Total", "Cantidad"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblVentasPeriodo);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcDesdeVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(dcHastaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(cmbMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(btnFiltrar))
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jLabel7))
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cmbMetodoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnFiltrar))
                            .addComponent(dcDesdeVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(dcHastaVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnDescargarPDF.setText("Descargar Informe Completo (PDF)");
        btnDescargarPDF.addActionListener(this::btnDescargarPDFActionPerformed);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("Resumen de mis Ventas");

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setText("Total Vendido");

        lblTotalVendido.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotalVendido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalVendido.setText("Q 3,664.40");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel10))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblTotalVendido, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalVendido)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel11.setText("Ventas Completadas");

        lblVentasCompletadas.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblVentasCompletadas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasCompletadas.setText("2");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVentasCompletadas, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVentasCompletadas)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Ventas Pendientes");

        lblVentasPendientes.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblVentasPendientes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVentasPendientes.setText("1");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(lblVentasPendientes, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVentasPendientes)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setText("Promedio por Venta");

        lblPromedioVenta.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblPromedioVenta.setText("Q 2,008.33");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                    .addComponent(lblPromedioVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPromedioVenta)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel12.setPreferredSize(new java.awt.Dimension(375, 269));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setText("Productos más vendidos");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Cantidad Vendida", "Total Vendido"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductosMasVendidos.setViewportView(jTable4);

        jLabel4.setText("Desde:");

        jLabel15.setText("Hasta:");

        btnFiltrarMasVendidos.setBackground(new java.awt.Color(153, 255, 255));
        btnFiltrarMasVendidos.setText("Filtrar");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel6)
                .addGap(50, 50, 50)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcDesdeProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcHastaProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFiltrarMasVendidos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tblProductosMasVendidos)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel4))
                    .addComponent(dcHastaProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcDesdeProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(btnFiltrarMasVendidos))
                .addGap(13, 13, 13)
                .addComponent(tblProductosMasVendidos, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnExportarEXCEL.setText("Exportar EXCEL");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExportarEXCEL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDescargarPDF)
                        .addGap(39, 39, 39))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnDescargarPDF)
                            .addComponent(btnExportarEXCEL)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 781, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDescargarPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDescargarPDFActionPerformed
        // TODO add your handling code here:
        exportarPdf();
    }//GEN-LAST:event_btnDescargarPDFActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDescargarPDF;
    private javax.swing.JButton btnExportarEXCEL;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFiltrarMasVendidos;
    private javax.swing.JComboBox<String> cmbMetodoPago;
    private com.toedter.calendar.JDateChooser dcDesdeProductos;
    private com.toedter.calendar.JDateChooser dcDesdeVentas;
    private com.toedter.calendar.JDateChooser dcHastaProductos;
    private com.toedter.calendar.JDateChooser dcHastaVentas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable4;
    private javax.swing.JLabel lblPromedioVenta;
    private javax.swing.JLabel lblTotalVendido;
    private javax.swing.JLabel lblVentasCompletadas;
    private javax.swing.JLabel lblVentasPendientes;
    private javax.swing.JScrollPane tblProductosMasVendidos;
    private javax.swing.JTable tblVentasPeriodo;
    // End of variables declaration//GEN-END:variables
}
