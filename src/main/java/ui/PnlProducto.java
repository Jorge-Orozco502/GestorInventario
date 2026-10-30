package ui;

import dao.ProductoDAO;
import java.math.BigDecimal;
import modelo.Producto;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import modelo.EstadoProducto;
import modelo.ProductoTerminado;

public class PnlProducto extends javax.swing.JPanel {

    public PnlProducto() {

        initComponents();
        cargarTablaProductos();
    }

    private void cargarTablaProductos() {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tblProductos.getModel();
        modelo.setRowCount(0);

        ProductoDAO dao = new ProductoDAO();
        List<Producto> lista = dao.listarProductos();

        for (Producto p : lista) {
            String tipoTexto = "PRODUCTO_TERMINADO";
            if (p instanceof modelo.MateriaPrima) {
                tipoTexto = "MATERIA_PRIMA";
            } else if (p instanceof modelo.Herramienta) {
                tipoTexto = "HERRAMIENTA";
            }

            modelo.addRow(new Object[]{
                p.getCodigo(),
                tipoTexto,
                p.getNombre(),
                p.getCategoria(),
                p.getDescripcion(),
                p.getUnidadMedida(),
                p.getPrecioCompra(),
                p.getPrecioVenta(),
                p.getExistenciaActual(),
                p.getExistenciaMinima(),
                p.getEstadoProducto()
            });
        }
    }

    private void filtrarTabla() {
        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tblProductos.getModel();
        modelo.setRowCount(0);

        String nombre = jTextField1.getText().trim();
        String categoria = cbCateogriaFiltro.getSelectedItem() != null ? cbCateogriaFiltro.getSelectedItem().toString().trim() : "Todos";

        String estadoSeleccionado = cbEstadoFiltro.getSelectedItem() != null ? cbEstadoFiltro.getSelectedItem().toString().trim() : "Todos";
        String estado = "Todos";

        if (!estadoSeleccionado.equalsIgnoreCase("Todos")) {
            estado = estadoSeleccionado.toUpperCase().replace(" ", "_");
        }

        ProductoDAO dao = new ProductoDAO();
        List<Producto> lista = dao.filtrarProductos(nombre, categoria, estado);

        for (Producto p : lista) {
            String tipoTexto = "PRODUCTO_TERMINADO";
            if (p instanceof modelo.MateriaPrima) {
                tipoTexto = "MATERIA_PRIMA";
            } else if (p instanceof modelo.Herramienta) {
                tipoTexto = "HERRAMIENTA";
            }

            modelo.addRow(new Object[]{
                p.getCodigo(),
                tipoTexto,
                p.getNombre(),
                p.getCategoria(),
                p.getDescripcion(),
                p.getUnidadMedida(),
                p.getPrecioCompra(),
                p.getPrecioVenta(),
                p.getExistenciaActual(),
                p.getExistenciaMinima(),
                p.getEstadoProducto()
            });
        }
    }

    private void configurarEventosBusqueda() {
        jTextField1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla();
            }
        });

        cbCateogriaFiltro.addActionListener(e -> filtrarTabla());
        cbEstadoFiltro.addActionListener(e -> filtrarTabla());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbCateogriaFiltro = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cbEstadoFiltro = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnFiltrar = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnRegistrar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        cbCategoria = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtUnidadMedida = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtPrecioCompra = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtPrecioVenta = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtExistenciaActual = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtExistenciaMinima = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        cbEstado = new javax.swing.JComboBox<>();
        cbTipoProducto = new javax.swing.JComboBox<>();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel1.setText("Categoría/Tipo Producto ");

        cbCateogriaFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos\t", "Perfiles", "Vidrios", "Accesorios ", "Prod. Terminados" }));
        cbCateogriaFiltro.setToolTipText("");
        cbCateogriaFiltro.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cbCateogriaFiltro.setOpaque(true);
        cbCateogriaFiltro.setVerifyInputWhenFocusTarget(false);
        cbCateogriaFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCateogriaFiltroActionPerformed(evt);
            }
        });

        jLabel2.setText("Estado");

        cbEstadoFiltro.setEditable(true);
        cbEstadoFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Activo", "Inactivo", "Agotado", "Bajo en Stock" }));
        cbEstadoFiltro.setToolTipText("");
        cbEstadoFiltro.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cbEstadoFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEstadoFiltroActionPerformed(evt);
            }
        });

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Tipo Producto", "Nombre", "Categoría", "Descripcion", "Unidad Medida", "Precio Compra", "Precio Venta", "Existencia Actual", "Existencia mínima", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProductos.setColumnSelectionAllowed(true);
        tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProductos);
        tblProductos.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jPanel2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Gestión de Invetario y Productos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(539, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnFiltrar.setBackground(new java.awt.Color(153, 0, 153));
        btnFiltrar.setForeground(new java.awt.Color(204, 255, 255));
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        jLabel3.setText("Buscar por nombre");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Filtrar");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Registrar Producto");

        btnRegistrar.setBackground(new java.awt.Color(0, 102, 0));
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(204, 255, 255));
        btnRegistrar.setText("Registrar");
        btnRegistrar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnRegistrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnRegistrar.setSelected(true);
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(153, 0, 0));
        btnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar");
        btnEliminar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnEliminar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnEliminar.setSelected(true);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnActualizar.setBackground(new java.awt.Color(204, 204, 204));
        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizar.setForeground(new java.awt.Color(255, 255, 255));
        btnActualizar.setText("Actualizar");
        btnActualizar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnActualizar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnActualizar.setSelected(true);
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnLimpiar.setBackground(new java.awt.Color(0, 51, 204));
        btnLimpiar.setForeground(new java.awt.Color(204, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jLabel7.setText("Codigo");

        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        jLabel8.setText("Tipo Producto");

        jLabel17.setText("Nombre");

        cbCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos\t", "Perfiles", "Vidrios", "Accesorios ", "Prod. Terminados" }));

        jLabel10.setText("Categoría");

        jLabel11.setText("Descripción");

        jLabel13.setText("Unidad Medida");

        txtPrecioCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecioCompraActionPerformed(evt);
            }
        });

        jLabel18.setText("Precio Compra");

        jLabel19.setText("Precio Venta");

        jLabel20.setText("Existencia Actual");

        jLabel15.setText("Existencia Mínima");

        jLabel16.setText("Estado");

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Inactivo", "Agotado", "Bajo en Stock" }));

        cbTipoProducto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MATERIA_PRIMA", "HERRAMIENTA", "PRODUCTO_TERMINADO" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(cbTipoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(cbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtUnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPrecioVenta)
                    .addComponent(jLabel19))
                .addContainerGap(17, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jLabel20))
                    .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtExistenciaMinima)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrecioCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel13)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtUnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel11)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(28, 28, 28))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cbTipoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtExistenciaActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel15)
                            .addGap(28, 28, 28))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtExistenciaMinima, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 842, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbCateogriaFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbEstadoFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrar))
                            .addComponent(jLabel2)))
                    .addComponent(jLabel5))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbEstadoFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnFiltrar)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbCateogriaFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(106, 106, 106))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 668, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtPrecioCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecioCompraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrecioCompraActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        try {
            String codigo = txtCodigo.getText().trim();
            if (codigo.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Debe seleccionar un producto para actualizar.");
                return;
            }

            Object selectedTipo = cbTipoProducto.getSelectedItem();
            String tipoSeleccionado = (selectedTipo != null) ? selectedTipo.toString().trim() : "PRODUCTO_TERMINADO";

            String nombre = txtNombre.getText().trim();

            Object selectedCat = cbCategoria.getSelectedItem();
            String categoria = (selectedCat != null) ? selectedCat.toString().trim() : "";

            String descripcion = txtDescripcion.getText().trim();
            String unidadMedida = txtUnidadMedida.getText().trim();
            BigDecimal precioCompra = new BigDecimal(txtPrecioCompra.getText().trim());
            BigDecimal precioVenta = new BigDecimal(txtPrecioVenta.getText().trim());
            double existenciaActual = Double.parseDouble(txtExistenciaActual.getText().trim());
            double existenciaMinima = Double.parseDouble(txtExistenciaMinima.getText().trim());

            Object selectedEstado = cbEstado.getSelectedItem();
            String estadoSeleccionado = (selectedEstado != null) ? selectedEstado.toString().trim().toUpperCase().replace(" ", "_") : "ACTIVO";
            EstadoProducto estado = EstadoProducto.valueOf(estadoSeleccionado);

            Producto producto;
            if (tipoSeleccionado.equalsIgnoreCase("MATERIA_PRIMA")) {
                producto = new modelo.MateriaPrima(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estado);
            } else if (tipoSeleccionado.equalsIgnoreCase("HERRAMIENTA")) {
                producto = new modelo.Herramienta(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estado);
            } else {
                producto = new ProductoTerminado(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estado);
            }

            ProductoDAO dao = new ProductoDAO();
            if (dao.actualizarProducto(producto)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Producto actualizado correctamente.");
                cargarTablaProductos();
                btnLimpiarActionPerformed(null);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Error al actualizar el producto en la base de datos.");
            }
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor verifique que los campos numéricos y de precios sean válidos.");
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + e.getMessage());
        }
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe ingresar o seleccionar el codigo del producto a eliminar.");
            return;
        }

        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(this, "Esta seguro de eliminar este producto?", "Confirmacion", javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            ProductoDAO dao = new ProductoDAO();
            if (dao.eliminarProducto(codigo)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
                cargarTablaProductos();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "No se pudo eliminar el producto.");
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        // TODO add your handling code here:
        try {
            String codigo = txtCodigo.getText().trim();
            String tipoProductoStr = cbTipoProducto.getSelectedItem() != null ? cbTipoProducto.getSelectedItem().toString().trim() : "PRODUCTO_TERMINADO";

            String nombre = txtNombre.getText().trim();
            String categoria = cbCategoria.getSelectedItem().toString();
            String descripcion = txtDescripcion.getText().trim();
            String unidadMedida = txtUnidadMedida.getText().trim();
            BigDecimal precioCompra = new BigDecimal(txtPrecioCompra.getText().trim());
            BigDecimal precioVenta = new BigDecimal(txtPrecioVenta.getText().trim());
            double existenciaActual = Double.parseDouble(txtExistenciaActual.getText().trim());
            double existenciaMinima = Double.parseDouble(txtExistenciaMinima.getText().trim());

            String estadoSeleccionado = cbEstado.getSelectedItem().toString().trim().toUpperCase().replace(" ", "_");
            EstadoProducto estado = EstadoProducto.valueOf(estadoSeleccionado);

            Producto producto = new ProductoTerminado(
                    codigo, nombre, categoria, descripcion, unidadMedida,
                    precioCompra, precioVenta, existenciaActual, existenciaMinima, estado
            );

            ProductoDAO dao = new ProductoDAO();
            if (dao.insertarProducto(producto)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Producto registrado correctamente.");
                cargarTablaProductos();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Error al registrar el producto en la base de datos.");
            }
        } catch (NumberFormatException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor verifique los campos numéricos y de precios.");
        }
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void cbCateogriaFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCateogriaFiltroActionPerformed
        // TODO add your handling code here:
        filtrarTabla();
    }//GEN-LAST:event_cbCateogriaFiltroActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        // TODO add your handling code here:
        filtrarTabla();


    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void cbEstadoFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEstadoFiltroActionPerformed
        // TODO add your handling code here:
        filtrarTabla();
    }//GEN-LAST:event_cbEstadoFiltroActionPerformed

    private void tblProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductosMouseClicked
        // TODO add your handling code here:
        int filaSeleccionada = tblProductos.getSelectedRow();

        if (filaSeleccionada != -1) {
            txtCodigo.setText(tblProductos.getValueAt(filaSeleccionada, 0) != null ? tblProductos.getValueAt(filaSeleccionada, 0).toString() : "");

            String tipoTabla = tblProductos.getValueAt(filaSeleccionada, 1) != null ? tblProductos.getValueAt(filaSeleccionada, 1).toString() : "";
            cbTipoProducto.setSelectedItem(tipoTabla);

            txtNombre.setText(tblProductos.getValueAt(filaSeleccionada, 2) != null ? tblProductos.getValueAt(filaSeleccionada, 2).toString() : "");

            Object catObj = tblProductos.getValueAt(filaSeleccionada, 3);
            if (catObj != null) {
                cbCategoria.setSelectedItem(catObj.toString());
            }

            txtDescripcion.setText(tblProductos.getValueAt(filaSeleccionada, 4) != null ? tblProductos.getValueAt(filaSeleccionada, 4).toString() : "");
            txtUnidadMedida.setText(tblProductos.getValueAt(filaSeleccionada, 5) != null ? tblProductos.getValueAt(filaSeleccionada, 5).toString() : "");
            txtPrecioCompra.setText(tblProductos.getValueAt(filaSeleccionada, 6) != null ? tblProductos.getValueAt(filaSeleccionada, 6).toString() : "");
            txtPrecioVenta.setText(tblProductos.getValueAt(filaSeleccionada, 7) != null ? tblProductos.getValueAt(filaSeleccionada, 7).toString() : "");
            txtExistenciaActual.setText(tblProductos.getValueAt(filaSeleccionada, 8) != null ? tblProductos.getValueAt(filaSeleccionada, 8).toString() : "");
            txtExistenciaMinima.setText(tblProductos.getValueAt(filaSeleccionada, 9) != null ? tblProductos.getValueAt(filaSeleccionada, 9).toString() : "");

            String estadoTabla = tblProductos.getValueAt(filaSeleccionada, 10) != null ? tblProductos.getValueAt(filaSeleccionada, 10).toString() : "";
            cbEstado.setSelectedIndex(-1);
            for (int i = 0; i < cbEstado.getItemCount(); i++) {
                String item = cbEstado.getItemAt(i);
                if (item != null && item.trim().equalsIgnoreCase(estadoTabla)) {
                    cbEstado.setSelectedIndex(i);
                    break;
                }
            }
        }
    }//GEN-LAST:event_tblProductosMouseClicked

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        txtCodigo.setText("");
        if (cbTipoProducto.getItemCount() > 0) {
            cbTipoProducto.setSelectedIndex(0);
        }
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtUnidadMedida.setText("");
        txtPrecioCompra.setText("");
        txtPrecioVenta.setText("");
        txtExistenciaActual.setText("");
        txtExistenciaMinima.setText("");

        if (cbCategoria.getItemCount() > 0) {
            cbCategoria.setSelectedIndex(0);
        }
        if (cbEstado.getItemCount() > 0) {
            cbEstado.setSelectedIndex(0);
        }

        tblProductos.clearSelection();
        txtCodigo.setEnabled(true);
        cargarTablaProductos();
    }//GEN-LAST:event_btnLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cbCategoria;
    private javax.swing.JComboBox<String> cbCateogriaFiltro;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JComboBox<String> cbEstadoFiltro;
    private javax.swing.JComboBox<String> cbTipoProducto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtExistenciaActual;
    private javax.swing.JTextField txtExistenciaMinima;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecioCompra;
    private javax.swing.JTextField txtPrecioVenta;
    private javax.swing.JTextField txtUnidadMedida;
    // End of variables declaration//GEN-END:variables
}
