package ui;

import dao.ProductoDAO;
import dao.VentaDAO;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.JFrame;
import modelo.Producto;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FrmPanelPrincipal extends javax.swing.JFrame {

    public FrmPanelPrincipal() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        mostrarVentasHoy();
        actualizarDashboard();
        new javax.swing.Timer(30_000, e -> actualizarDashboard()).start();
        txtResultadoVentasHoy.setEditable(false);
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        String fechaTexto = fechaActual.format(formato);
        fechaTexto = fechaTexto.substring(0, 1).toUpperCase() + fechaTexto.substring(1);

        txtFechaActual.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtFechaActual.setText(fechaTexto);
    }

    private void actualizarDashboard() {
        mostrarVentasHoy();
        cargarInventarioCritico();
    }

    private void cargarInventarioCritico() {
        ProductoDAO productoDAO = new ProductoDAO();

        List<Producto> stockBajo = productoDAO.listarStockBajo();
        List<Producto> agotados = productoDAO.listarAgotados();

        llenarTabla(tblStockBajo, stockBajo);
        llenarTabla(tblStockAgotado, agotados);
    }

    private void llenarTabla(javax.swing.JTable tabla, List<Producto> productos) {
        javax.swing.table.DefaultTableModel modelo
                = (javax.swing.table.DefaultTableModel) tabla.getModel();

        modelo.setRowCount(0);

        for (Producto p : productos) {
            modelo.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                p.getExistenciaActual()
            });
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupInventario = new javax.swing.JPopupMenu();
        itemMateriaPrima = new javax.swing.JMenuItem();
        popupVentas = new javax.swing.JPopupMenu();
        itemClientes = new javax.swing.JMenuItem();
        itemVentas = new javax.swing.JMenuItem();
        popupCompras = new javax.swing.JPopupMenu();
        itemRegistrarCompra = new javax.swing.JMenuItem();
        itemProvedores = new javax.swing.JMenuItem();
        popupProduccion = new javax.swing.JPopupMenu();
        itemOrdenesProduccion = new javax.swing.JMenuItem();
        itemProductosProceso = new javax.swing.JMenuItem();
        popupReportes = new javax.swing.JPopupMenu();
        itemReportes = new javax.swing.JMenuItem();
        itemHistorial = new javax.swing.JMenuItem();
        popupUsuarios = new javax.swing.JPopupMenu();
        itemUsuarios = new javax.swing.JMenuItem();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        pnlGeneral = new javax.swing.JPanel();
        pnlEncabezado = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        btnCerrarSesion = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        pnlLateral = new javax.swing.JPanel();
        btnInventario = new javax.swing.JButton();
        btnVentas = new javax.swing.JButton();
        btnUsuarios = new javax.swing.JButton();
        btnPanelPrincipal = new javax.swing.JButton();
        btnProveedores = new javax.swing.JButton();
        btnReportes = new javax.swing.JButton();
        btnHistorial = new javax.swing.JButton();
        btnProduccion = new javax.swing.JButton();
        pnlCentral = new javax.swing.JPanel();
        lblBienvenida = new javax.swing.JLabel();
        pnlVentasDia = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblNuevasVentas = new javax.swing.JLabel();
        lblTotalVenta1 = new javax.swing.JLabel();
        txtResultadoVentasHoy = new javax.swing.JTextField();
        pnlInventarioCritico = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblStockBajo = new javax.swing.JLabel();
        lblProductoAgotado = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStockBajo = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStockAgotado = new javax.swing.JTable();
        pnlAccesosDirectos = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnRegistrarProducto = new javax.swing.JButton();
        btnAccesoHistorial = new javax.swing.JButton();
        btnAccesoRegistrarVenta = new javax.swing.JButton();
        txtFechaActual = new javax.swing.JLabel();

        itemMateriaPrima.setText("Materia Prima");
        itemMateriaPrima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemMateriaPrimaActionPerformed(evt);
            }
        });
        popupInventario.add(itemMateriaPrima);

        itemClientes.setText("Registrar Clientes");
        itemClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemClientesActionPerformed(evt);
            }
        });
        popupVentas.add(itemClientes);

        itemVentas.setText("Registrar Ventas");
        itemVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemVentasActionPerformed(evt);
            }
        });
        popupVentas.add(itemVentas);

        itemRegistrarCompra.setText("Registrar Compras");
        popupCompras.add(itemRegistrarCompra);

        itemProvedores.setText("Proveedores");
        popupCompras.add(itemProvedores);

        itemOrdenesProduccion.setText("Ordenes de Produccion");
        popupProduccion.add(itemOrdenesProduccion);

        itemProductosProceso.setText("Productos en Proceso ");
        popupProduccion.add(itemProductosProceso);

        itemReportes.setText("Reportes");
        popupReportes.add(itemReportes);

        itemHistorial.setText("Historial ");
        popupReportes.add(itemHistorial);

        itemUsuarios.setText("Usuarios");
        popupUsuarios.add(itemUsuarios);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(1000, 0));

        pnlEncabezado.setBackground(new java.awt.Color(247, 237, 228));
        pnlEncabezado.setForeground(new java.awt.Color(51, 204, 255));
        pnlEncabezado.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Administrador ");
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel7.setOpaque(true);
        pnlEncabezado.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 0, 150, 40));

        btnCerrarSesion.setText("CERRAR SESIÓN");
        btnCerrarSesion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnCerrarSesion.setOpaque(true);
        btnCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarSesionActionPerformed(evt);
            }
        });
        pnlEncabezado.add(btnCerrarSesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(1240, 0, 100, 40));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Sistema de Gestión - Ventanas de PVC ALVIDE ");
        pnlEncabezado.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, -3, -1, 30));

        pnlLateral.setBackground(new java.awt.Color(247, 237, 228));
        pnlLateral.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        btnInventario.setBackground(new java.awt.Color(179, 217, 190));
        btnInventario.setText("Productos");
        btnInventario.setToolTipText("");
        btnInventario.setActionCommand("");
        btnInventario.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 246, 240)));
        btnInventario.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnInventario.setOpaque(true);
        btnInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventarioActionPerformed(evt);
            }
        });

        btnVentas.setBackground(new java.awt.Color(179, 217, 190));
        btnVentas.setText("Ventas/Clientes ");
        btnVentas.setToolTipText("");
        btnVentas.setActionCommand("");
        btnVentas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 246, 240)));
        btnVentas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVentasActionPerformed(evt);
            }
        });

        btnUsuarios.setBackground(new java.awt.Color(179, 217, 190));
        btnUsuarios.setText("Administracion de Usuarios");
        btnUsuarios.setToolTipText("");
        btnUsuarios.setActionCommand("");
        btnUsuarios.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 246, 240)));
        btnUsuarios.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsuariosActionPerformed(evt);
            }
        });

        btnPanelPrincipal.setBackground(new java.awt.Color(179, 219, 193));
        btnPanelPrincipal.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPanelPrincipal.setText("Panel Principal");
        btnPanelPrincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPanelPrincipalActionPerformed(evt);
            }
        });

        btnProveedores.setBackground(new java.awt.Color(179, 217, 190));
        btnProveedores.setText("Proveedores");
        btnProveedores.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 246, 240)));
        btnProveedores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProveedoresActionPerformed(evt);
            }
        });

        btnReportes.setBackground(new java.awt.Color(179, 217, 190));
        btnReportes.setText("Reportes");
        btnReportes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 246, 240)));
        btnReportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportesActionPerformed(evt);
            }
        });

        btnHistorial.setBackground(new java.awt.Color(179, 217, 190));
        btnHistorial.setText("Historial");
        btnHistorial.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 246, 240)));
        btnHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistorialActionPerformed(evt);
            }
        });

        btnProduccion.setBackground(new java.awt.Color(179, 217, 190));
        btnProduccion.setText("Producción");
        btnProduccion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(238, 246, 240)));
        btnProduccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProduccionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLateralLayout = new javax.swing.GroupLayout(pnlLateral);
        pnlLateral.setLayout(pnlLateralLayout);
        pnlLateralLayout.setHorizontalGroup(
            pnlLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLateralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlLateralLayout.createSequentialGroup()
                        .addGroup(pnlLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnUsuarios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnInventario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnProveedores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnReportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnHistorial, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLateralLayout.createSequentialGroup()
                        .addGap(0, 25, Short.MAX_VALUE)
                        .addComponent(btnPanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLateralLayout.createSequentialGroup()
                        .addComponent(btnProduccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        pnlLateralLayout.setVerticalGroup(
            pnlLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLateralLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(btnPanelPrincipal)
                .addGap(17, 17, 17)
                .addComponent(btnInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnProveedores, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnProduccion, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnReportes, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUsuarios, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(363, Short.MAX_VALUE))
        );

        pnlCentral.setBackground(new java.awt.Color(255, 255, 255));
        pnlCentral.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblBienvenida.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBienvenida.setText("¡Bienvenido!");
        lblBienvenida.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        pnlVentasDia.setBackground(new java.awt.Color(204, 0, 204));
        pnlVentasDia.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(182, 216, 192), 4));
        pnlVentasDia.setOpaque(false);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Resumen Ventas del Dia");

        lblNuevasVentas.setText("Nuevas Ventas:");

        lblTotalVenta1.setText("Total Hoy:");

        txtResultadoVentasHoy.setBorder(null);
        txtResultadoVentasHoy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtResultadoVentasHoyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlVentasDiaLayout = new javax.swing.GroupLayout(pnlVentasDia);
        pnlVentasDia.setLayout(pnlVentasDiaLayout);
        pnlVentasDiaLayout.setHorizontalGroup(
            pnlVentasDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVentasDiaLayout.createSequentialGroup()
                .addGroup(pnlVentasDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlVentasDiaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlVentasDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlVentasDiaLayout.createSequentialGroup()
                                .addComponent(lblNuevasVentas)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(pnlVentasDiaLayout.createSequentialGroup()
                                .addComponent(lblTotalVenta1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtResultadoVentasHoy))))
                    .addGroup(pnlVentasDiaLayout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        pnlVentasDiaLayout.setVerticalGroup(
            pnlVentasDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVentasDiaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNuevasVentas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlVentasDiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalVenta1)
                    .addComponent(txtResultadoVentasHoy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(83, Short.MAX_VALUE))
        );

        pnlInventarioCritico.setBackground(new java.awt.Color(246, 237, 230));
        pnlInventarioCritico.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(182, 216, 192), 4));
        pnlInventarioCritico.setOpaque(false);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setText("Inventario Critico");

        lblStockBajo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblStockBajo.setText("Stock Bajo:");

        lblProductoAgotado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblProductoAgotado.setText("Sin Productos/Agotado:");

        tblStockBajo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Existencia"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblStockBajo.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(tblStockBajo);
        tblStockBajo.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        tblStockAgotado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Existencia"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblStockAgotado.setColumnSelectionAllowed(true);
        jScrollPane2.setViewportView(tblStockAgotado);
        tblStockAgotado.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        javax.swing.GroupLayout pnlInventarioCriticoLayout = new javax.swing.GroupLayout(pnlInventarioCritico);
        pnlInventarioCritico.setLayout(pnlInventarioCriticoLayout);
        pnlInventarioCriticoLayout.setHorizontalGroup(
            pnlInventarioCriticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlInventarioCriticoLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(447, 447, 447))
            .addGroup(pnlInventarioCriticoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlInventarioCriticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlInventarioCriticoLayout.createSequentialGroup()
                        .addComponent(lblStockBajo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblProductoAgotado)
                        .addGap(408, 408, 408))
                    .addGroup(pnlInventarioCriticoLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 521, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jScrollPane2)
                        .addContainerGap())))
        );
        pnlInventarioCriticoLayout.setVerticalGroup(
            pnlInventarioCriticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlInventarioCriticoLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(pnlInventarioCriticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStockBajo)
                    .addComponent(lblProductoAgotado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlInventarioCriticoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlAccesosDirectos.setBackground(new java.awt.Color(246, 237, 230));
        pnlAccesosDirectos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(182, 216, 192), 4));
        pnlAccesosDirectos.setOpaque(false);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel5.setText("Accesos Directos");

        btnRegistrarProducto.setBackground(new java.awt.Color(179, 219, 193));
        btnRegistrarProducto.setText("Registrar Producto");
        btnRegistrarProducto.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnRegistrarProducto.setSelected(true);
        btnRegistrarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarProductoActionPerformed(evt);
            }
        });

        btnAccesoHistorial.setBackground(new java.awt.Color(179, 219, 193));
        btnAccesoHistorial.setText("Historial");
        btnAccesoHistorial.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAccesoHistorial.setSelected(true);
        btnAccesoHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccesoHistorialActionPerformed(evt);
            }
        });

        btnAccesoRegistrarVenta.setBackground(new java.awt.Color(179, 219, 193));
        btnAccesoRegistrarVenta.setText("Registrar Ventas");
        btnAccesoRegistrarVenta.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnAccesoRegistrarVenta.setSelected(true);
        btnAccesoRegistrarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccesoRegistrarVentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlAccesosDirectosLayout = new javax.swing.GroupLayout(pnlAccesosDirectos);
        pnlAccesosDirectos.setLayout(pnlAccesosDirectosLayout);
        pnlAccesosDirectosLayout.setHorizontalGroup(
            pnlAccesosDirectosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAccesosDirectosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAccesoRegistrarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRegistrarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAccesoHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(167, 167, 167))
            .addGroup(pnlAccesosDirectosLayout.createSequentialGroup()
                .addGap(365, 365, 365)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlAccesosDirectosLayout.setVerticalGroup(
            pnlAccesosDirectosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccesosDirectosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(55, 55, 55)
                .addGroup(pnlAccesosDirectosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAccesoHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegistrarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAccesoRegistrarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        txtFechaActual.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtFechaActual.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);

        javax.swing.GroupLayout pnlCentralLayout = new javax.swing.GroupLayout(pnlCentral);
        pnlCentral.setLayout(pnlCentralLayout);
        pnlCentralLayout.setHorizontalGroup(
            pnlCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCentralLayout.createSequentialGroup()
                .addGroup(pnlCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCentralLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(pnlInventarioCritico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlCentralLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(pnlCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCentralLayout.createSequentialGroup()
                                .addComponent(pnlVentasDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(pnlAccesosDirectos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(pnlCentralLayout.createSequentialGroup()
                                .addComponent(txtFechaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBienvenida, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        pnlCentralLayout.setVerticalGroup(
            pnlCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCentralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblBienvenida)
                    .addComponent(txtFechaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCentralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlVentasDia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlAccesosDirectos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlInventarioCritico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(260, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlGeneralLayout = new javax.swing.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addComponent(pnlLateral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCentral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(pnlEncabezado, javax.swing.GroupLayout.DEFAULT_SIZE, 1376, Short.MAX_VALUE)
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGeneralLayout.createSequentialGroup()
                .addComponent(pnlEncabezado, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlCentral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlLateral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 825, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void mostrarGestionClientes(String nit) {
        PnlClientes panelClientes = new PnlClientes(nit);

        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.add(panelClientes, java.awt.BorderLayout.CENTER);

        pnlCentral.revalidate();
        pnlCentral.repaint();
    }

    private void mostrarVentasHoy() {
        VentaDAO ventaDAO = new VentaDAO();
        BigDecimal totalHoy = ventaDAO.obtenerTotalVentasHoy();

        txtResultadoVentasHoy.setText(String.format("Q %,.2f", totalHoy));
        txtResultadoVentasHoy.setEditable(false);
    }


    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventarioActionPerformed
        // TODO add your handling code here:
        PnlProducto pnlMateriaPrima = new PnlProducto();

        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());

        pnlCentral.add(pnlMateriaPrima, java.awt.BorderLayout.CENTER);

        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_btnInventarioActionPerformed

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuariosActionPerformed
        ui.PnlUsuarios pnlVentas = new ui.PnlUsuarios();
        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.add(pnlVentas, java.awt.BorderLayout.CENTER);
        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_btnUsuariosActionPerformed

    private void btnVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVentasActionPerformed
        popupVentas.show(btnVentas, 0, btnVentas.getHeight());
        PnlVentas pnlVentas = new PnlVentas();

        pnlCentral.add(pnlVentas, java.awt.BorderLayout.CENTER);
        pnlCentral.revalidate();
        pnlCentral.repaint();

    }//GEN-LAST:event_btnVentasActionPerformed

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarSesionActionPerformed
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea cerrar sesión?",
                "Confirmar Cierre de Sesión",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            this.dispose();
            new FrmMenuInicio().setVisible(true);
        }
    }//GEN-LAST:event_btnCerrarSesionActionPerformed

    private void btnAccesoRegistrarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccesoRegistrarVentaActionPerformed
        ui.PnlVentas pnlVentas = new ui.PnlVentas();
        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.add(pnlVentas, java.awt.BorderLayout.CENTER);
        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_btnAccesoRegistrarVentaActionPerformed

    private void btnAccesoHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccesoHistorialActionPerformed
        ui.PnlHistorialll pnlHistorial = new ui.PnlHistorialll();
        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.add(pnlHistorial, java.awt.BorderLayout.CENTER);
        pnlCentral.revalidate();
        pnlCentral.repaint();     // TODO add your handling code here:
    }//GEN-LAST:event_btnAccesoHistorialActionPerformed

    private void btnRegistrarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarProductoActionPerformed
        ui.PnlProducto pnlProducto = new ui.PnlProducto();
        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.add(pnlProducto, java.awt.BorderLayout.CENTER);
        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_btnRegistrarProductoActionPerformed

    private void itemMateriaPrimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemMateriaPrimaActionPerformed
        // TODO add your handling code here:
        PnlProducto pnlMateriaPrima = new PnlProducto();

        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.removeAll();
        pnlCentral.add(pnlMateriaPrima, java.awt.BorderLayout.CENTER);

        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_itemMateriaPrimaActionPerformed

    private void itemClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemClientesActionPerformed
        ui.PnlClientes pnlClientes = new ui.PnlClientes();

        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.add(pnlClientes, java.awt.BorderLayout.CENTER);
        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_itemClientesActionPerformed

    private void btnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProveedoresActionPerformed
        // TODO add your handling code here:
        ui.PnlProveedores pnlVentas = new ui.PnlProveedores();

        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.add(pnlVentas, java.awt.BorderLayout.CENTER);
        pnlCentral.revalidate();
        pnlCentral.repaint();

    }//GEN-LAST:event_btnProveedoresActionPerformed

    private void itemVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemVentasActionPerformed
        // TODO add your handling code here:
        ui.PnlVentas pnlVentas = new ui.PnlVentas();

        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());
        pnlCentral.add(pnlVentas, java.awt.BorderLayout.CENTER);
        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_itemVentasActionPerformed

    private void btnReportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportesActionPerformed
        // TODO add your handling code here:
        PnlReportes pnlReportes = new PnlReportes();

        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());

        pnlCentral.add(pnlReportes, java.awt.BorderLayout.CENTER);

        pnlCentral.revalidate();
        pnlCentral.repaint();

    }//GEN-LAST:event_btnReportesActionPerformed

    private void btnProduccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProduccionActionPerformed
        // TODO add your handling code here:
        PnlTrabajoFabricacion pnlTrabajoFabricacion = new PnlTrabajoFabricacion();

        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());

        pnlCentral.add(pnlTrabajoFabricacion, java.awt.BorderLayout.CENTER);

        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_btnProduccionActionPerformed

    private void btnHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistorialActionPerformed
        // TODO add your handling code here:
        PnlHistorialll pnlHistorial = new PnlHistorialll();

        pnlCentral.removeAll();
        pnlCentral.setLayout(new java.awt.BorderLayout());

        pnlCentral.add(pnlHistorial, java.awt.BorderLayout.CENTER);

        pnlCentral.revalidate();
        pnlCentral.repaint();
    }//GEN-LAST:event_btnHistorialActionPerformed

    private void btnPanelPrincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPanelPrincipalActionPerformed
        FrmPanelPrincipal nuevoPanel = new FrmPanelPrincipal();
        nuevoPanel.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPanelPrincipalActionPerformed

    private void txtResultadoVentasHoyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtResultadoVentasHoyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtResultadoVentasHoyActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccesoHistorial;
    private javax.swing.JButton btnAccesoRegistrarVenta;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JButton btnHistorial;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnPanelPrincipal;
    private javax.swing.JButton btnProduccion;
    private javax.swing.JButton btnProveedores;
    private javax.swing.JButton btnRegistrarProducto;
    private javax.swing.JButton btnReportes;
    private javax.swing.JButton btnUsuarios;
    private javax.swing.JButton btnVentas;
    private javax.swing.JMenuItem itemClientes;
    private javax.swing.JMenuItem itemHistorial;
    private javax.swing.JMenuItem itemMateriaPrima;
    private javax.swing.JMenuItem itemOrdenesProduccion;
    private javax.swing.JMenuItem itemProductosProceso;
    private javax.swing.JMenuItem itemProvedores;
    private javax.swing.JMenuItem itemRegistrarCompra;
    private javax.swing.JMenuItem itemReportes;
    private javax.swing.JMenuItem itemUsuarios;
    private javax.swing.JMenuItem itemVentas;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblNuevasVentas;
    private javax.swing.JLabel lblProductoAgotado;
    private javax.swing.JLabel lblStockBajo;
    private javax.swing.JLabel lblTotalVenta1;
    private javax.swing.JPanel pnlAccesosDirectos;
    private javax.swing.JPanel pnlCentral;
    private javax.swing.JPanel pnlEncabezado;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlInventarioCritico;
    private javax.swing.JPanel pnlLateral;
    private javax.swing.JPanel pnlVentasDia;
    private javax.swing.JPopupMenu popupCompras;
    private javax.swing.JPopupMenu popupInventario;
    private javax.swing.JPopupMenu popupProduccion;
    private javax.swing.JPopupMenu popupReportes;
    private javax.swing.JPopupMenu popupUsuarios;
    private javax.swing.JPopupMenu popupVentas;
    private javax.swing.JTable tblStockAgotado;
    private javax.swing.JTable tblStockBajo;
    private javax.swing.JLabel txtFechaActual;
    private javax.swing.JTextField txtResultadoVentasHoy;
    // End of variables declaration//GEN-END:variables
}
