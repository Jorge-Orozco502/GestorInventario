package ui;

import modelo.ComprobanteVentaPDF;
import modelo.DlgClienteRapido;
import modelo.Cliente;
import modelo.Producto;
import modelo.Usuario;
import modelo.Venta;
import modelo.DetalleVenta;
import modelo.FormaPago;
import dao.VentaDAO;
import dao.ProductoDAO;
import dao.ClienteDAO;
import dao.UsuarioDAO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.math.RoundingMode;

public class PnlVentas extends javax.swing.JPanel {

    private final dao.VentaDAO ventaDAO = new dao.VentaDAO();
    private final dao.ProductoDAO productoDAO = new dao.ProductoDAO();
    private final dao.ClienteDAO clienteDAO = new dao.ClienteDAO();
    private final dao.UsuarioDAO usuarioDAO = new dao.UsuarioDAO();

    public PnlVentas() {
        initComponents();

        txtNumeroVenta.setEditable(false);
        txtFecha.setEditable(false);

        configurarTablaDetalle();
        cargarDatosDesdeBD();

        prepararNuevaVenta();

        jTextField1.setEditable(false);
        jTextField3.setEditable(false);
        jTextField4.setEditable(false);
        jTextField5.setEditable(false);
        txtTotalFinal.setEditable(false);

        txtNitVenta.setToolTipText("Escribe el NIT (o CF) y presiona Enter o Tab");
        cbCodigoCliente.setToolTipText("Clientes / sucursales que tienen ese NIT");
        jTextField2.setToolTipText("Porcentaje de descuento para el próximo producto que agregues");
        txtNombreCF.setToolTipText("Opcional: nombre del cliente CF");
        txtDireccionCF.setToolTipText("Opcional: dirección del cliente CF");

        construirPanelExtraCliente();
        configurarComboClientes();

        txtNitVenta.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (e.isTemporary() || e.getOppositeComponent() == btnCancelar) {
                    return;
                }
                buscarClientePorNit(null);
            }
        });

        txtNitVenta.setText("CF");
        buscarClientePorNit(null);
    }

    private void prepararNuevaVenta() {
        txtNumeroVenta.setText("Se asignará al guardar");
        txtFecha.setText(LocalDate.now().toString());
    }

    //cliente
    private static final String SEPARADOR = " – ";

    private final List<Cliente> clientesCombo = new ArrayList<>();
    private boolean actualizandoCombo = false;

    private String ultimoNitOfrecido = "";
    private boolean ofreciendoRegistro = false;

    private final javax.swing.JLabel lblNombreCliente = new javax.swing.JLabel(" ");
    private final javax.swing.JButton btnNuevaSucursal = new javax.swing.JButton("Nueva sucursal");
    private final javax.swing.JLabel lblNombreCF = new javax.swing.JLabel("Nombre:");
    private final javax.swing.JTextField txtNombreCF = new javax.swing.JTextField(12);
    private final javax.swing.JLabel lblDireccionCF = new javax.swing.JLabel("Dirección:");
    private final javax.swing.JTextField txtDireccionCF = new javax.swing.JTextField(16);

    private void construirPanelExtraCliente() {
        pnlExtraCliente.removeAll();
        pnlExtraCliente.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        pnlExtraCliente.setOpaque(false);

        lblNombreCliente.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK),
                javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5)));

        pnlExtraCliente.add(lblNombreCliente);
        pnlExtraCliente.add(btnNuevaSucursal);
        pnlExtraCliente.add(lblNombreCF);
        pnlExtraCliente.add(txtNombreCF);
        pnlExtraCliente.add(lblDireccionCF);
        pnlExtraCliente.add(txtDireccionCF);

        btnNuevaSucursal.addActionListener(e -> registrarNuevaSucursal());

        actualizarPanelExtra("");
    }

    private void actualizarPanelExtra(String nit) {
        boolean esCF = nit.equals("CF");

        lblNombreCF.setVisible(esCF);
        txtNombreCF.setVisible(esCF);
        lblDireccionCF.setVisible(esCF);
        txtDireccionCF.setVisible(esCF);
        btnNuevaSucursal.setVisible(!esCF && !clientesCombo.isEmpty());

        if (!esCF) {
            txtNombreCF.setText("");
            txtDireccionCF.setText("");
        }

        pnlExtraCliente.revalidate();
        pnlExtraCliente.repaint();
    }

    private void configurarComboClientes() {
        cbCodigoCliente.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                String texto = value == null ? "" : value.toString();

                if (index == -1) {
                    int pos = texto.indexOf(SEPARADOR);
                    if (pos > 0) {
                        texto = texto.substring(0, pos);
                    }
                }
                return super.getListCellRendererComponent(
                        list, texto, index, isSelected, cellHasFocus);
            }
        });

        cbCodigoCliente.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                ensancharLista();
            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
            }
        });

        cbCodigoCliente.addActionListener(e -> {
            if (!actualizandoCombo) {
                mostrarClienteSeleccionado();
            }
        });
    }

    private void ensancharLista() {
        java.awt.FontMetrics fm = cbCodigoCliente.getFontMetrics(cbCodigoCliente.getFont());
        int ancho = cbCodigoCliente.getWidth();

        for (int i = 0; i < cbCodigoCliente.getItemCount(); i++) {
            ancho = Math.max(ancho, fm.stringWidth(cbCodigoCliente.getItemAt(i)) + 40);
        }

        Object hijo = cbCodigoCliente.getUI().getAccessibleChild(cbCodigoCliente, 0);

        if (hijo instanceof javax.swing.JPopupMenu popup
                && popup.getComponentCount() > 0
                && popup.getComponent(0) instanceof javax.swing.JScrollPane scroll) {

            java.awt.Dimension tamano
                    = new java.awt.Dimension(ancho, scroll.getPreferredSize().height);
            scroll.setPreferredSize(tamano);
            scroll.setMaximumSize(tamano);
        }
    }

    private void buscarClientePorNit(String codigoPreferido) {
        String nit = ClienteDAO.normalizarNit(txtNitVenta.getText());
        txtNitVenta.setText(nit);

        if (nit.isEmpty()) {
            cargarComboClientes(new ArrayList<>(), null);
            actualizarPanelExtra(nit);
            ultimoNitOfrecido = "";
            mostrarMensajeCliente("Escribe el NIT o CF", null);
            return;
        }

        List<Cliente> encontrados;

        try {
            encontrados = clienteDAO.listarPorNit(nit);
        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo buscar el cliente en la base de datos:\n"
                    + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
            return;
        }

        cargarComboClientes(encontrados, codigoPreferido);
        actualizarPanelExtra(nit);

        if (encontrados.isEmpty()) {
            if (nit.equals("CF")) {
                mostrarMensajeCliente(
                        "No existe el cliente CF. Regístralo en Clientes con NIT CF.", false);
                return;
            }

            mostrarMensajeCliente("Cliente no registrado", false);

            if (!nit.equals(ultimoNitOfrecido)) {
                ultimoNitOfrecido = nit;
                javax.swing.SwingUtilities.invokeLater(() -> ofrecerRegistrarCliente(nit));
            }
            return;
        }

        ultimoNitOfrecido = "";

        if (encontrados.size() > 1 && obtenerClienteSeleccionado() == null) {
            lblNombreCliente.setForeground(new java.awt.Color(204, 102, 0));
            lblNombreCliente.setText(
                    "Hay " + encontrados.size() + " sucursales con este NIT: elige una");
            javax.swing.SwingUtilities.invokeLater(() -> cbCodigoCliente.showPopup());
        }
    }

    private void cargarComboClientes(List<Cliente> lista, String codigoPreferido) {
        actualizandoCombo = true;

        try {
            cbCodigoCliente.removeAllItems();
            clientesCombo.clear();

            int seleccionar = (lista.size() == 1) ? 0 : -1;

            for (int i = 0; i < lista.size(); i++) {
                Cliente c = lista.get(i);
                clientesCombo.add(c);
                cbCodigoCliente.addItem(descripcionCliente(c));

                if (c.getCodigoCliente().equals(codigoPreferido)) {
                    seleccionar = i;
                }
            }

            cbCodigoCliente.setSelectedIndex(seleccionar);

        } finally {
            actualizandoCombo = false;
        }

        mostrarClienteSeleccionado();
    }

    private Cliente obtenerClienteSeleccionado() {
        int i = cbCodigoCliente.getSelectedIndex();
        return (i >= 0 && i < clientesCombo.size()) ? clientesCombo.get(i) : null;
    }

    private String descripcionCliente(Cliente c) {
        StringBuilder texto = new StringBuilder(c.getCodigoCliente())
                .append(SEPARADOR).append(c.getNombreCliente());

        if (c.getDireccion() != null && !c.getDireccion().isBlank()) {
            texto.append(SEPARADOR).append(c.getDireccion().trim());
        }
        return texto.toString();
    }

    private void mostrarClienteSeleccionado() {
        Cliente c = obtenerClienteSeleccionado();

        if (c == null) {
            return;
        }

        String texto = c.getNombreCliente();
        if (c.getDireccion() != null && !c.getDireccion().isBlank()) {
            texto += SEPARADOR + c.getDireccion().trim();
        }

        mostrarMensajeCliente(texto, true);
    }

    private void mostrarMensajeCliente(String texto, Boolean ok) {
        if (ok == null) {
            lblNombreCliente.setForeground(java.awt.Color.GRAY);
        } else if (ok) {
            lblNombreCliente.setForeground(new java.awt.Color(0, 128, 0));
        } else {
            lblNombreCliente.setForeground(java.awt.Color.RED);
        }
        lblNombreCliente.setText(texto);
        lblNombreCliente.setToolTipText(texto);
    }

    private void ofrecerRegistrarCliente(String nit) {
        if (ofreciendoRegistro
                || !nit.equals(ClienteDAO.normalizarNit(txtNitVenta.getText()))) {
            return;
        }

        ofreciendoRegistro = true;
        try {
            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "El NIT " + nit + " no está registrado.\n"
                    + "¿Deseas registrar al cliente ahora?",
                    "Registrar cliente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (respuesta == JOptionPane.YES_OPTION) {
                abrirRegistroCliente(nit, "", "", false);
            }
        } finally {
            ofreciendoRegistro = false;
        }
    }

    private void registrarNuevaSucursal() {
        String nit = ClienteDAO.normalizarNit(txtNitVenta.getText());

        if (nit.isEmpty() || nit.equals("CF") || clientesCombo.isEmpty()) {
            return;
        }

        Cliente base = clientesCombo.get(0);
        abrirRegistroCliente(nit, base.getNombreCliente(), base.getTelefono(), true);
    }

    private void abrirRegistroCliente(String nit, String nombre, String telefono,
            boolean nuevaSucursal) {

        DlgClienteRapido dialogo = new DlgClienteRapido(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                nit, nombre, telefono, nuevaSucursal
        );
        dialogo.setVisible(true);

        Cliente guardado = dialogo.getClienteGuardado();

        if (guardado != null) {
            ultimoNitOfrecido = "";
            buscarClientePorNit(guardado.getCodigoCliente());
        }
    }

    private void exigirCliente() {
        String nit = ClienteDAO.normalizarNit(txtNitVenta.getText());

        if (nit.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Escribe el NIT del cliente (o CF) antes de confirmar la venta.",
                    "Cliente requerido",
                    JOptionPane.WARNING_MESSAGE
            );
            txtNitVenta.requestFocusInWindow();
            return;
        }

        ultimoNitOfrecido = "";
        buscarClientePorNit(null);

        if (clientesCombo.size() > 1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este NIT tiene varias sucursales.\n"
                    + "Elige una en la lista antes de confirmar.",
                    "Elige la sucursal",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void configurarTablaDetalle() {
        javax.swing.table.DefaultTableModel modeloTabla
                = new javax.swing.table.DefaultTableModel(
                        new Object[][]{},
                        new String[]{
                            "Código producto",
                            "Cantidad",
                            "Precio unitario",
                            "Subtotal",
                            "IVA",
                            "Total",
                            "Desc. %"
                        }
                ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblDetalleVenta.setModel(modeloTabla);
    }

    private void cargarDatosDesdeBD() {
        txtFecha.setText(java.time.LocalDate.now().toString());

        jComboBox3.removeAllItems();
        java.util.List<modelo.Usuario> listaUsuarios = usuarioDAO.listarUsuarios();
        for (modelo.Usuario u : listaUsuarios) {
            jComboBox3.addItem(u.getNombreUsuario());
        }
    }

    private void limpiarVenta() {
        txtNitVenta.setText("CF");
        txtNombreCF.setText("");
        txtDireccionCF.setText("");
        ultimoNitOfrecido = "";
        buscarClientePorNit(null);

        DefaultTableModel modelo
                = (DefaultTableModel) tblDetalleVenta.getModel();

        modelo.setRowCount(0);

        jTextField1.setText("0.00");
        jTextField2.setText("");
        jTextField3.setText("0.00");
        jTextField4.setText("0.00");
        jTextField5.setText("0.00");
        txtTotalFinal.setText("0.00");

        prepararNuevaVenta();
    }

    private void calcularTotalesYMontos() {
        BigDecimal subtotalGeneral = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;
        BigDecimal totalFinal = BigDecimal.ZERO;
        BigDecimal totalDescuentos = BigDecimal.ZERO;

        for (int i = 0; i < tblDetalleVenta.getRowCount(); i++) {
            BigDecimal cantidad = new BigDecimal(
                    tblDetalleVenta.getValueAt(i, 1).toString()
            );

            BigDecimal precioUnitario = new BigDecimal(
                    tblDetalleVenta.getValueAt(i, 2).toString()
            );

            BigDecimal subtotalLinea = new BigDecimal(
                    tblDetalleVenta.getValueAt(i, 3).toString()
            );

            BigDecimal ivaLinea = new BigDecimal(
                    tblDetalleVenta.getValueAt(i, 4).toString()
            );

            BigDecimal totalLinea = new BigDecimal(
                    tblDetalleVenta.getValueAt(i, 5).toString()
            );

            BigDecimal totalOriginal = precioUnitario
                    .multiply(cantidad)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal descuentoLinea = totalOriginal
                    .subtract(totalLinea)
                    .max(BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

            subtotalGeneral = subtotalGeneral.add(subtotalLinea);
            totalIva = totalIva.add(ivaLinea);
            totalFinal = totalFinal.add(totalLinea);
            totalDescuentos = totalDescuentos.add(descuentoLinea);
        }

        subtotalGeneral = subtotalGeneral.setScale(2, RoundingMode.HALF_UP);
        totalIva = totalIva.setScale(2, RoundingMode.HALF_UP);
        totalFinal = totalFinal.setScale(2, RoundingMode.HALF_UP);
        totalDescuentos = totalDescuentos.setScale(2, RoundingMode.HALF_UP);

        jTextField1.setText(subtotalGeneral.toPlainString());
        jTextField3.setText(totalIva.toPlainString());
        jTextField4.setText(totalFinal.toPlainString());
        jTextField5.setText(totalDescuentos.toPlainString());
        txtTotalFinal.setText(totalFinal.toPlainString());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cbFormaDePago = new javax.swing.JLabel();
        txtNumeroVenta = new javax.swing.JTextField();
        txtFecha = new javax.swing.JTextField();
        cbCodigoCliente = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        txtNitVenta = new javax.swing.JTextField();
        pnlExtraCliente = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDetalleVenta = new javax.swing.JTable();
        btnAgregarProducto = new javax.swing.JButton();
        btnEliminarSeleccion = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtSubtotalGeneral = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtTotalFinal = new javax.swing.JTextField();
        btnCancelar = new javax.swing.JButton();
        btnConfirmar = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel11.setText("Número Venta");

        jLabel12.setText("Fecha");

        jLabel13.setText("Cliente (NIT)");

        jLabel14.setText("Usuario Responsable");

        cbFormaDePago.setText("Forma de Pago");
        cbFormaDePago.setToolTipText("");

        txtNumeroVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroVentaActionPerformed(evt);
            }
        });

        cbCodigoCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INTERACTIVOS" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Efectivo", "Tarjeta de Crédito", "Tarjeta de Débito", "Transferencia" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Subtotal");

        jLabel3.setText("Descuento %");

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel4.setText("Total IVA");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel5.setText("Total");

        txtNitVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNitVentaActionPerformed(evt);
            }
        });

        pnlExtraCliente.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlExtraClienteLayout = new javax.swing.GroupLayout(pnlExtraCliente);
        pnlExtraCliente.setLayout(pnlExtraClienteLayout);
        pnlExtraClienteLayout.setHorizontalGroup(
            pnlExtraClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlExtraClienteLayout.setVerticalGroup(
            pnlExtraClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 28, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbCodigoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtNumeroVenta)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jLabel12)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jLabel13))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNitVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(112, 112, 112)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbFormaDePago)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 92, Short.MAX_VALUE))
                    .addComponent(pnlExtraCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(cbFormaDePago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNumeroVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNitVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbCodigoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlExtraCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        tblDetalleVenta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero Venta", "Fecha", "Codigo Cliente", "id Usuario Responsable", "Forma de Pago", "Subtotal", "Total Descuentos", "Total IVA", "Total Final"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDetalleVenta.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(tblDetalleVenta);
        tblDetalleVenta.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        btnAgregarProducto.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnAgregarProducto.setText("Agregar Producto");
        btnAgregarProducto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnAgregarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProductoActionPerformed(evt);
            }
        });

        btnEliminarSeleccion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnEliminarSeleccion.setText("Eliminar Selección");
        btnEliminarSeleccion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnEliminarSeleccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarSeleccionActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Total Final:");

        txtTotalFinal.setEditable(false);
        txtTotalFinal.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtTotalFinal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTotalFinal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtTotalFinal.setEnabled(false);
        txtTotalFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalFinalActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(255, 153, 153));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("CANCELAR");
        btnCancelar.setSelected(true);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnConfirmar.setBackground(new java.awt.Color(0, 204, 102));
        btnConfirmar.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        btnConfirmar.setForeground(new java.awt.Color(204, 255, 255));
        btnConfirmar.setText("CONFIRMAR");
        btnConfirmar.setSelected(true);
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText("Total Descuentos:");

        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(txtSubtotalGeneral)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConfirmar)
                .addGap(31, 31, 31))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(27, 27, 27)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(89, 89, 89)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotalFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(txtSubtotalGeneral))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTotalFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btnEliminarSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminarSeleccion, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Registrar Venta ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        try {
            DefaultTableModel modeloTabla
                    = (DefaultTableModel) tblDetalleVenta.getModel();

            if (modeloTabla.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debe agregar al menos un producto al detalle de la venta.",
                        "Atención",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Cliente cliente = obtenerClienteSeleccionado();

            if (cliente == null) {
                exigirCliente();
                return;
            }

            if (jComboBox3.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecciona un usuario responsable.",
                        "Usuario requerido",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String nombreUsuarioSel
                    = jComboBox3.getSelectedItem().toString();

            Usuario usuarioResponsable = null;
            List<Usuario> listaUsuarios = usuarioDAO.listarUsuarios();

            for (Usuario u : listaUsuarios) {
                if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuarioSel)) {
                    usuarioResponsable = u;
                    break;
                }
            }

            if (usuarioResponsable == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró el usuario responsable seleccionado.",
                        "Usuario no encontrado",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (jComboBox2.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecciona una forma de pago.",
                        "Forma de pago",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String seleccionCombo
                    = jComboBox2.getSelectedItem().toString();

            FormaPago formaPago;

            switch (seleccionCombo) {
                case "Efectivo":
                    formaPago = FormaPago.EFECTIVO;
                    break;

                case "Tarjeta de Crédito":
                case "Tarjeta de Débito":
                    formaPago = FormaPago.TARJETA;
                    break;

                case "Transferencia":
                    formaPago = FormaPago.TRANSFERENCIA;
                    break;

                case "Crédito":
                    formaPago = FormaPago.CREDITO;
                    break;

                default:
                    JOptionPane.showMessageDialog(
                            this,
                            "La forma de pago seleccionada no es válida.",
                            "Forma de pago",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
            }

            BigDecimal subtotalGeneral
                    = new BigDecimal(jTextField1.getText().trim());

            BigDecimal totalDescuentos
                    = new BigDecimal(jTextField5.getText().trim());

            BigDecimal totalIva
                    = new BigDecimal(jTextField3.getText().trim());

            BigDecimal totalFinal
                    = new BigDecimal(txtTotalFinal.getText().trim());

            if (subtotalGeneral.signum() < 0
                    || totalDescuentos.signum() < 0
                    || totalIva.signum() < 0
                    || totalFinal.signum() < 0) {
                throw new IllegalArgumentException(
                        "Los montos de la venta no pueden ser negativos."
                );
            }

            List<DetalleVenta> detallesVenta = new ArrayList<>();

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String codProd
                        = modeloTabla.getValueAt(i, 0).toString();

                double cantidad = Double.parseDouble(
                        modeloTabla.getValueAt(i, 1).toString()
                );

                BigDecimal precioUnit = new BigDecimal(
                        modeloTabla.getValueAt(i, 2).toString()
                );

                BigDecimal subtotalLinea = new BigDecimal(
                        modeloTabla.getValueAt(i, 3).toString()
                );

                BigDecimal ivaLinea = new BigDecimal(
                        modeloTabla.getValueAt(i, 4).toString()
                );

                BigDecimal totalLinea = new BigDecimal(
                        modeloTabla.getValueAt(i, 5).toString()
                );

                if (!Double.isFinite(cantidad) || cantidad <= 0) {
                    throw new IllegalArgumentException(
                            "La cantidad del producto " + codProd
                            + " no es válida."
                    );
                }

                Producto prod
                        = productoDAO.buscarProductoPorCodigo(codProd);

                if (prod == null) {
                    throw new IllegalArgumentException(
                            "No se encontró el producto " + codProd
                    );
                }

                DetalleVenta detalle
                        = new DetalleVenta(prod, cantidad, precioUnit);

                detalle.setSubtotal(subtotalLinea);
                detalle.setIvaLinea(ivaLinea);
                detalle.setTotalLinea(totalLinea);

                detallesVenta.add(detalle);
            }

            Venta nuevaVenta = new Venta(
                    "PENDIENTE",
                    LocalDate.now(),
                    formaPago,
                    subtotalGeneral,
                    totalDescuentos,
                    totalIva,
                    totalFinal,
                    cliente,
                    usuarioResponsable
            );

            nuevaVenta.setDetallesVenta(detallesVenta);

            if ("CF".equals(ClienteDAO.normalizarNit(cliente.getNitCF()))) {
                nuevaVenta.setNombreCf(txtNombreCF.getText());
                nuevaVenta.setDireccionCf(txtDireccionCF.getText());
            }

            boolean exito = ventaDAO.insertarVenta(nuevaVenta);

            if (exito) {
                JOptionPane.showMessageDialog(
                        this,
                        "Venta registrada correctamente.\n"
                        + "Número de venta: " + nuevaVenta.getNumeroVenta(),
                        "Venta registrada",
                        JOptionPane.INFORMATION_MESSAGE
                );

                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "¿Deseas generar y guardar el comprobante PDF de esta venta?",
                        "Generar comprobante",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    try {
                        java.io.File archivo = ComprobanteVentaPDF.guardarPDF(
                                nuevaVenta,
                                this
                        );

                        if (archivo != null) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Comprobante guardado correctamente en:\n"
                                    + archivo.getAbsolutePath(),
                                    "Comprobante generado",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                        }

                    } catch (java.io.IOException
                            | org.openpdf.text.DocumentException
                            | IllegalArgumentException e) {

                        JOptionPane.showMessageDialog(
                                this,
                                "La venta se registró correctamente, pero no se pudo "
                                + "generar el comprobante PDF.\n\n"
                                + "Detalle: " + e.getMessage(),
                                "Error al generar comprobante",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
                limpiarVenta();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar la venta. Revisa la conexión "
                        + "a la base de datos y los datos ingresados.",
                        "Error al registrar venta",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al procesar la venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpiarVenta();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnEliminarSeleccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarSeleccionActionPerformed
        int fila = tblDetalleVenta.getSelectedRow();

        if (fila < 0) {
            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Selecciona el producto que quieres quitar."
            );
            return;
        }

        javax.swing.table.DefaultTableModel modelo
                = (javax.swing.table.DefaultTableModel) tblDetalleVenta.getModel();

        modelo.removeRow(fila);
        calcularTotalesYMontos();
    }//GEN-LAST:event_btnEliminarSeleccionActionPerformed

    private void btnAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductoActionPerformed
        java.math.BigDecimal porcentaje = java.math.BigDecimal.ZERO;
        String textoDescuento = jTextField2.getText()
                .trim().replace("%", "").replace(",", ".").trim();

        if (!textoDescuento.isEmpty()) {
            try {
                porcentaje = new java.math.BigDecimal(textoDescuento);
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "Escribe un porcentaje de descuento numérico (por ejemplo 5).",
                        "Descuento no válido",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );
                jTextField2.requestFocusInWindow();
                return;
            }

            if (porcentaje.compareTo(java.math.BigDecimal.ZERO) < 0
                    || porcentaje.compareTo(new java.math.BigDecimal("100")) > 0) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        "El descuento debe estar entre 0 y 100.",
                        "Descuento no válido",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                );
                jTextField2.requestFocusInWindow();
                return;
            }
        }
        porcentaje = porcentaje.setScale(2, java.math.RoundingMode.HALF_UP);

        java.util.List<modelo.Producto> lista = productoDAO.listarProductos();

        if (lista == null || lista.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No hay productos registrados en la base de datos.", "Atención", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opciones = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            opciones[i] = lista.get(i).getCodigo() + " - " + lista.get(i).getNombre();
        }

        String seleccion = (String) javax.swing.JOptionPane.showInputDialog(
                this,
                "Seleccione un Producto de la BD:",
                "Agregar Producto",
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion != null) {
            String codIngresado = seleccion.split(" - ")[0].trim();
            modelo.Producto productoBD = productoDAO.buscarProductoPorCodigo(codIngresado);

            if (productoBD != null) {
                String cantStr = javax.swing.JOptionPane.showInputDialog(
                        this,
                        "Cantidad a llevar:",
                        "1.0"
                );

                if (cantStr == null) {
                    return;
                }

                double cantidad;

                try {
                    cantidad = Double.parseDouble(cantStr.trim());
                } catch (NumberFormatException ex) {
                    javax.swing.JOptionPane.showMessageDialog(
                            this,
                            "Escribe una cantidad numérica válida."
                    );
                    return;
                }

                if (!Double.isFinite(cantidad) || cantidad <= 0) {
                    javax.swing.JOptionPane.showMessageDialog(
                            this,
                            "La cantidad debe ser mayor que cero."
                    );
                    return;
                }

                java.math.BigDecimal precioUnitario = productoBD.getPrecioVenta();

                java.math.BigDecimal cantidadBD
                        = java.math.BigDecimal.valueOf(cantidad);

                java.math.BigDecimal totalOriginal = precioUnitario
                        .multiply(cantidadBD)
                        .setScale(2, java.math.RoundingMode.HALF_UP);

                java.math.BigDecimal descuentoLinea = totalOriginal
                        .multiply(porcentaje)
                        .divide(new java.math.BigDecimal("100"), 2,
                                java.math.RoundingMode.HALF_UP);

                java.math.BigDecimal totalLinea = totalOriginal
                        .subtract(descuentoLinea)
                        .setScale(2, java.math.RoundingMode.HALF_UP);

                java.math.BigDecimal subtotal = totalLinea
                        .divide(new java.math.BigDecimal("1.12"), 2,
                                java.math.RoundingMode.HALF_UP);

                java.math.BigDecimal iva = totalLinea
                        .subtract(subtotal)
                        .setScale(2, java.math.RoundingMode.HALF_UP);

                javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblDetalleVenta.getModel();

                model.addRow(new Object[]{
                    productoBD.getCodigo(),
                    cantidad,
                    precioUnitario,
                    subtotal,
                    iva,
                    totalLinea,
                    porcentaje
                });

                jTextField2.setText("");

                calcularTotalesYMontos();
            }
        }
    }//GEN-LAST:event_btnAgregarProductoActionPerformed

    private void txtNitVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNitVentaActionPerformed
        // TODO add your handling code here:
        ultimoNitOfrecido = "";
        buscarClientePorNit(null);
    }//GEN-LAST:event_txtNitVentaActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        calcularTotalesYMontos();
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        calcularTotalesYMontos();
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void txtNumeroVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroVentaActionPerformed
        cbCodigoCliente.requestFocus();
    }//GEN-LAST:event_txtNumeroVentaActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void txtTotalFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalFinalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalFinalActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarProducto;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnEliminarSeleccion;
    private javax.swing.JComboBox<String> cbCodigoCliente;
    private javax.swing.JLabel cbFormaDePago;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JPanel pnlExtraCliente;
    private javax.swing.JTable tblDetalleVenta;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtNitVenta;
    private javax.swing.JTextField txtNumeroVenta;
    private javax.swing.JLabel txtSubtotalGeneral;
    private javax.swing.JTextField txtTotalFinal;
    // End of variables declaration//GEN-END:variables

}
