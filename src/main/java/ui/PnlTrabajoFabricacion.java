package ui;

import dao.TrabajoFabricacionDAO;
import modelo.TrabajoFabricacion;
import modelo.ProductoTerminado;
import modelo.Usuario;
import conexion.conexionBD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

class ItemCombo {

    private int id;
    private String texto;

    public ItemCombo(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return texto;
    }
}

public class PnlTrabajoFabricacion extends javax.swing.JPanel {

    private TrabajoFabricacionDAO trabajoDAO;

    public PnlTrabajoFabricacion() {
        initComponents();
        trabajoDAO = new TrabajoFabricacionDAO();

        cargarUsuariosCombo();
        cargarTablaTrabajos();
        configurarFechaActual();
        generarSugerenciasAutoincrementales();

        tblTrabajos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblTrabajos.getSelectedRow() != -1) {
                cargarDatosSeleccionados();
            }
        });

    }

    private void configurarFechaActual() {
        if (txtFecha.getText().trim().isEmpty()) {
            txtFecha.setText(LocalDate.now().toString());
        }
    }

    private void cargarUsuariosCombo() {
        cbIdUsuarioResponsable.removeAllItems();
        String sql = "SELECT id_usuario, nombre_completo, nombre_usuario FROM usuario WHERE activo = 1";
        try (Connection con = conexionBD.obtenerConexion(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id_usuario");
                String nombre = rs.getString("nombre_completo") + " (" + rs.getString("nombre_usuario") + ")";
                cbIdUsuarioResponsable.addItem(new ItemCombo(id, nombre).toString());
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar usuarios en el combo: " + e.getMessage());
        }
    }

    private void generarSugerenciasAutoincrementales() {
        try (Connection con = conexionBD.obtenerConexion(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(SUBSTRING(numero_trabajo, 4) AS UNSIGNED)) AS max_num FROM trabajo_fabricacion WHERE numero_trabajo REGEXP '^[A-Za-z]+[0-9]+$'")) {
            int siguienteNum = 1;
            if (rs.next() && rs.getObject("max_num") != null) {
                siguienteNum = rs.getInt("max_num") + 1;
            }
            txtNumeroTrabajo.setText("TRB" + String.format("%03d", siguienteNum));
        } catch (Exception e) {
            txtNumeroTrabajo.setText("TRB001");
        }

        try (Connection con = conexionBD.obtenerConexion(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(codigo AS UNSIGNED)) AS max_prod FROM producto WHERE codigo REGEXP '^[0-9]+$'")) {
            int siguienteProd = 1;
            if (rs.next() && rs.getObject("max_prod") != null) {
                siguienteProd = rs.getInt("max_prod") + 1;
            }
            txtCodigoProductoTerminado.setText(String.valueOf(siguienteProd));
        } catch (Exception e) {
            txtCodigoProductoTerminado.setText("1");
        }
    }

    public void cargarTablaTrabajos() {
        DefaultTableModel modeloTabla = (DefaultTableModel) tblTrabajos.getModel();
        modeloTabla.setRowCount(0);
        List<TrabajoFabricacion> lista = trabajoDAO.listarTrabajos();
        for (TrabajoFabricacion t : lista) {
            Object[] fila = {
                t.getNumeroTrabajo(),
                t.getFecha(),
                t.getDescripcion(),
                t.getProductoTerminado() != null ? t.getProductoTerminado().getCodigo() : "",
                "PRODUCTO_TERMINADO",
                t.getUsuarioResponsable() != null ? t.getUsuarioResponsable().getIdUsuario() : ""
            };
            modeloTabla.addRow(fila);
        }
    }

    private void cargarDatosSeleccionados() {
        int fila = tblTrabajos.getSelectedRow();
        if (fila != -1) {
            txtNumeroTrabajo.setText(tblTrabajos.getValueAt(fila, 0) != null ? tblTrabajos.getValueAt(fila, 0).toString() : "");
            txtFecha.setText(tblTrabajos.getValueAt(fila, 1) != null ? tblTrabajos.getValueAt(fila, 1).toString() : "");
            txtDescripcion.setText(tblTrabajos.getValueAt(fila, 2) != null ? tblTrabajos.getValueAt(fila, 2).toString() : "");
            txtCodigoProductoTerminado.setText(tblTrabajos.getValueAt(fila, 3) != null ? tblTrabajos.getValueAt(fila, 3).toString() : "");

            Object idUsuarioObj = tblTrabajos.getValueAt(fila, 5);
            if (idUsuarioObj != null) {
                String idBuscado = idUsuarioObj.toString();
                for (int i = 0; i < cbIdUsuarioResponsable.getItemCount(); i++) {
                    String item = cbIdUsuarioResponsable.getItemAt(i);
                    if (item.contains("(")) {
                    }
                }
            }
        }
    }

    private void guardarTrabajo() {
        try {
            String numTrabajo = txtNumeroTrabajo.getText().trim();
            String fechaStr = txtFecha.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String codProducto = txtCodigoProductoTerminado.getText().trim();

            if (numTrabajo.isEmpty() || fechaStr.isEmpty() || codProducto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete los campos obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tipoProdSeleccionado = "PRODUCTO_TERMINADO";
            if (cbTipoProducto != null) {
                cbTipoProducto.setSelectedItem("PRODUCTO_TERMINADO");
            }

            LocalDate fecha = LocalDate.parse(fechaStr);

            ProductoTerminado prod = new ProductoTerminado();
            prod.setCodigo(codProducto);
            prod.setNombre("Producto " + codProducto);

            int idUsuarioSeleccionado = 1;
            try {
                String seleccion = (String) cbIdUsuarioResponsable.getSelectedItem();
                if (seleccion != null) {
                    Connection conId = conexionBD.obtenerConexion();
                    PreparedStatement psId = conId.prepareStatement("SELECT id_usuario FROM usuario WHERE CONCAT(nombre_completo, ' (', nombre_usuario, ')') = ?");
                    psId.setString(1, seleccion);
                    ResultSet rsId = psId.executeQuery();
                    if (rsId.next()) {
                        idUsuarioSeleccionado = rsId.getInt("id_usuario");
                    }
                    conId.close();
                }
            } catch (Exception ignored) {
            }

            Usuario usuario = new Usuario() {
                @Override
                public boolean tienePermiso(String accion) {
                    return true;
                }
            };
            usuario.setIdUsuario(idUsuarioSeleccionado);

            TrabajoFabricacion trabajo = new TrabajoFabricacion();
            trabajo.setNumeroTrabajo(numTrabajo);
            trabajo.setFecha(fecha);
            trabajo.setDescripcion(descripcion);
            trabajo.setTipoProducto(tipoProdSeleccionado);
            trabajo.setProductoTerminado(prod);
            trabajo.setUsuarioResponsable(usuario);

            boolean exito = trabajoDAO.insertarTrabajo(trabajo);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Trabajo de fabricación registrado correctamente.");
                cargarTablaTrabajos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar. Verifique que el código de producto exista como producto terminado en la BD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en los datos ingresados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        generarSugerenciasAutoincrementales();
        configurarFechaActual();
        txtDescripcion.setText("");
        tblTrabajos.clearSelection();
    }

    private void actualizarTrabajo() {
        try {
            String numTrabajo = txtNumeroTrabajo.getText().trim();
            String fechaStr = txtFecha.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String codProducto = txtCodigoProductoTerminado.getText().trim();

            if (numTrabajo.isEmpty() || fechaStr.isEmpty() || codProducto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla o complete los campos obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate fecha = LocalDate.parse(fechaStr);

            ProductoTerminado prod = new ProductoTerminado();
            prod.setCodigo(codProducto);

            int idUsuarioSeleccionado = 1;
            try {
                String seleccion = (String) cbIdUsuarioResponsable.getSelectedItem();
                if (seleccion != null) {
                    Connection conId = conexionBD.obtenerConexion();
                    PreparedStatement psId = conId.prepareStatement("SELECT id_usuario FROM usuario WHERE CONCAT(nombre_completo, ' (', nombre_usuario, ')') = ?");
                    psId.setString(1, seleccion);
                    ResultSet rsId = psId.executeQuery();
                    if (rsId.next()) {
                        idUsuarioSeleccionado = rsId.getInt("id_usuario");
                    }
                    conId.close();
                }
            } catch (Exception ignored) {
            }

            Usuario usuario = new Usuario() {
                @Override
                public boolean tienePermiso(String accion) {
                    return true;
                }
            };
            usuario.setIdUsuario(idUsuarioSeleccionado);

            TrabajoFabricacion trabajo = new TrabajoFabricacion();
            trabajo.setNumeroTrabajo(numTrabajo);
            trabajo.setFecha(fecha);
            trabajo.setDescripcion(descripcion);
            trabajo.setTipoProducto("PRODUCTO_TERMINADO");
            trabajo.setProductoTerminado(prod);
            trabajo.setUsuarioResponsable(usuario);

            boolean exito = trabajoDAO.actualizarTrabajo(trabajo);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Trabajo de fabricación actualizado correctamente.");
                cargarTablaTrabajos();
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar el trabajo de fabricación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en los datos para actualizar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarTrabajo() {
        try {
            String numTrabajo = txtNumeroTrabajo.getText().trim();

            if (numTrabajo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione o ingrese el número de trabajo que desea eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el trabajo de fabricación " + numTrabajo + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean exito = trabajoDAO.eliminarTrabajo(numTrabajo);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Trabajo de fabricación eliminado correctamente.");
                    cargarTablaTrabajos();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el registro en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al procesar la eliminación: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        txtBuscarOrden = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTrabajos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNumeroTrabajo = new javax.swing.JTextField();
        txtFecha = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        txtCodigoProductoTerminado = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        cbTipoProducto = new javax.swing.JComboBox<>();
        cbIdUsuarioResponsable = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        jCheckBox1.setText("jCheckBox1");

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setText("Produccion");

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtBuscarOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarOrdenActionPerformed(evt);
            }
        });

        tblTrabajos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero_Trabajo", "Fecha", "Descripción", "Codigo_Producto_Terminado", "Tipo Producto", "ID Usuario Responsable"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTrabajos.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(tblTrabajos);
        tblTrabajos.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel1.setText("Número_trabajo");

        jLabel3.setText("Fecha");

        jLabel4.setText("Descripción");

        jLabel5.setText("Codigo_Producto_Terminado");

        jLabel6.setText("Tipo_Producto");

        jLabel7.setText("ID Usuario Responsable");

        txtNumeroTrabajo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroTrabajoActionPerformed(evt);
            }
        });

        txtFecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaActionPerformed(evt);
            }
        });

        btnAgregar.setBackground(new java.awt.Color(0, 102, 0));
        btnAgregar.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(102, 0, 51));
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnActualizar.setBackground(new java.awt.Color(255, 153, 51));
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnLimpiar.setBackground(new java.awt.Color(0, 102, 102));
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        cbTipoProducto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PRODUCTO_TERMINADO" }));
        cbTipoProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTipoProductoActionPerformed(evt);
            }
        });

        cbIdUsuarioResponsable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbIdUsuarioResponsableActionPerformed(evt);
            }
        });

        jLabel9.setText("Buscar Número de Trabajo");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLimpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNumeroTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtCodigoProductoTerminado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cbTipoProducto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbIdUsuarioResponsable, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30)))
                .addGap(0, 0, 0))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNumeroTrabajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigoProductoTerminado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoProducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbIdUsuarioResponsable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtBuscarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtBuscarOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel8.setText("CLASE TRABAJO FABRICACION ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(106, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    private void txtBuscarOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarOrdenActionPerformed

    }//GEN-LAST:event_txtBuscarOrdenActionPerformed

    private void txtNumeroTrabajoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroTrabajoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroTrabajoActionPerformed

    private void txtFechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        limpiarFormulario();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        // TODO add your handling code here:
        actualizarTrabajo();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        int fila = tblTrabajos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un trabajo de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        eliminarTrabajo();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        // TODO add your handling code here:
        guardarTrabajo();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void cbTipoProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTipoProductoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbTipoProductoActionPerformed

    private void cbIdUsuarioResponsableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbIdUsuarioResponsableActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbIdUsuarioResponsableActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cbIdUsuarioResponsable;
    private javax.swing.JComboBox<String> cbTipoProducto;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblTrabajos;
    private javax.swing.JTextField txtBuscarOrden;
    private javax.swing.JTextField txtCodigoProductoTerminado;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtNumeroTrabajo;
    // End of variables declaration//GEN-END:variables
}
