package ui;

import dao.UsuarioDAO;
import modelo.Usuario;
import modelo.Administrador;
import modelo.Empleado;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PnlUsuarios extends javax.swing.JPanel {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public PnlUsuarios() {
        initComponents();
        configurarTabla();
        cargarUsuarios();

        btnEliminar.addActionListener(e -> eliminarUsuario());

    }

    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{
                    "ID Usuario", "Tipo Usuario", "Nombre",
                    "Contraseña", "Nombre Completo", "Activo",
                    "Fecha_creacion"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tblUsuarios.setModel(modelo);
        tblUsuarios.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION
        );
    }

    private void cargarUsuarios() {
        DefaultTableModel modelo = (DefaultTableModel) tblUsuarios.getModel();
        modelo.setRowCount(0);

        try {
            List<Usuario> usuarios = usuarioDAO.listarUsuarios();

            if (usuarios == null) {
                return;
            }

            for (Usuario usuario : usuarios) {
                String tipo;

                if (usuario instanceof Administrador) {
                    tipo = "ADMINISTRADOR";
                } else if (usuario instanceof Empleado) {
                    tipo = "EMPLEADO";
                } else {
                    tipo = "DESCONOCIDO";
                }

                modelo.addRow(new Object[]{
                    usuario.getIdUsuario(),
                    tipo,
                    usuario.getNombreUsuario(),
                    "••••••••",
                    usuario.getNombreCompleto(),
                    usuario.isActivo() ? "Sí" : "No",
                    usuario.getFechaCreacion()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar usuarios: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registrarNuevoUsuario() {
        try {
            String tipo = cbTipoUsuario.getSelectedItem().toString();
            String nombreUsuario = txtNombreUsuario.getText().trim();
            String passwordPlano = txtUsuario.getText().trim();
            String nombreCompleto = txtNombreCompleto.getText().trim();
            boolean activo = cboxActivo.isSelected();

            if (nombreUsuario.isEmpty()
                    || passwordPlano.isEmpty()
                    || nombreCompleto.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Por favor llena todos los campos obligatorios.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Usuario nuevoUsuario;

            if ("ADMINISTRADOR".equalsIgnoreCase(tipo)) {
                nuevoUsuario = new Administrador(
                        nombreUsuario,
                        passwordPlano,
                        nombreCompleto
                );
            } else {
                nuevoUsuario = new Empleado(
                        nombreUsuario,
                        passwordPlano,
                        nombreCompleto
                );
            }

            nuevoUsuario.setActivo(activo);

            UsuarioDAO dao = new UsuarioDAO();
            boolean exito = dao.insertarUsuario(nuevoUsuario, tipo);

            if (exito) {
                JOptionPane.showMessageDialog(
                        this,
                        "Usuario registrado exitosamente.",
                        "Registro completado",
                        JOptionPane.INFORMATION_MESSAGE
                );

                limpiarCampos();
                cargarUsuarios();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo registrar el usuario. "
                        + "Verifica los datos y la conexión a la base de datos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al registrar el usuario: " + e.getMessage(),
                    "Excepción",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void eliminarUsuario() {
        int fila = tblUsuarios.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un usuario de la tabla."
            );
            return;
        }

        int filaModelo = tblUsuarios.convertRowIndexToModel(fila);
        int id = Integer.parseInt(
                tblUsuarios.getModel().getValueAt(filaModelo, 0).toString()
        );

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas eliminar el usuario seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (usuarioDAO.eliminarUsuario(id)) {
                JOptionPane.showMessageDialog(
                        this, "Usuario eliminado correctamente."
                );
                limpiarCampos();
                cargarUsuarios();
            } else {
                JOptionPane.showMessageDialog(
                        this, "No se pudo eliminar el usuario."
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al eliminar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void limpiarCampos() {
        idUsuario.setText("");
        txtNombreUsuario.setText("");
        txtUsuario.setText("");
        txtNombreCompleto.setText("");

        cbTipoUsuario.setSelectedIndex(0);
        cboxActivo.setSelected(true);

        tblUsuarios.clearSelection();
    }

    private void mostrarUsuarioSeleccionado() {
        int fila = tblUsuarios.getSelectedRow();

        if (fila == -1) {
            return;
        }

        int filaModelo = tblUsuarios.convertRowIndexToModel(fila);

        idUsuario.setText(
                tblUsuarios.getModel().getValueAt(filaModelo, 0).toString()
        );

        cbTipoUsuario.setSelectedItem(
                tblUsuarios.getModel().getValueAt(filaModelo, 1).toString()
        );

        txtNombreUsuario.setText(
                tblUsuarios.getModel().getValueAt(filaModelo, 2).toString()
        );

        txtNombreCompleto.setText(
                tblUsuarios.getModel().getValueAt(filaModelo, 4).toString()
        );

        cboxActivo.setSelected(
                "Sí".equals(
                        tblUsuarios.getModel().getValueAt(filaModelo, 5).toString()
                )
        );

        txtUsuario.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        idUsuario = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtNombreUsuario = new javax.swing.JTextField();
        cbTipoUsuario = new javax.swing.JComboBox<>();
        txtUsuario = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtNombreCompleto = new javax.swing.JTextField();
        cboxActivo = new javax.swing.JCheckBox();
        btnRegistrar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Gestión de Usuarios");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        idUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idUsuarioActionPerformed(evt);
            }
        });

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Usuario", "Tipo Usuario", "Nombre", "Contraseña", "Nombre Completo", "Activo", "Fecha_creacion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUsuarios.setColumnSelectionAllowed(true);
        tblUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuarios);
        tblUsuarios.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jLabel3.setText("ID Usuario");

        jLabel4.setText("Tipo Usuario");

        jLabel5.setText("Nombre");

        cbTipoUsuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMINISTRADOR", "EMPLEADO" }));

        jLabel7.setText("Contraseña");

        jLabel8.setText("Nombre Completo");

        cboxActivo.setText("Activo");

        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarActionPerformed(evt);
            }
        });

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 827, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cboxActivo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLimpiar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnActualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEliminar)
                        .addGap(18, 18, 18)
                        .addComponent(btnRegistrar)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(idUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbTipoUsuario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(186, 217, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtNombreCompleto)
                                .addContainerGap())))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(idUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreCompleto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboxActivo)
                    .addComponent(btnRegistrar)
                    .addComponent(btnEliminar)
                    .addComponent(btnActualizar)
                    .addComponent(btnLimpiar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void idUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idUsuarioActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_idUsuarioActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        // TODO add your handling code here:
        registrarNuevoUsuario();
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void tblUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUsuariosMouseClicked
        // TODO add your handling code here:
        mostrarUsuarioSeleccionado();
    }//GEN-LAST:event_tblUsuariosMouseClicked

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        // TODO add your handling code here:
        try {
            if (idUsuario.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Selecciona un usuario de la tabla."
                );
                return;
            }

            String tipo = cbTipoUsuario.getSelectedItem().toString();
            String nombreUsuario = txtNombreUsuario.getText().trim();
            String passwordNueva = txtUsuario.getText();
            String nombreCompleto = txtNombreCompleto.getText().trim();

            if (nombreUsuario.isEmpty() || nombreCompleto.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "El nombre de usuario y el nombre completo son obligatorios."
                );
                return;
            }

            Usuario usuario;

            if ("ADMINISTRADOR".equalsIgnoreCase(tipo)) {
                usuario = new Administrador(
                        nombreUsuario,
                        passwordNueva,
                        nombreCompleto
                );
            } else {
                usuario = new Empleado(
                        nombreUsuario,
                        passwordNueva,
                        nombreCompleto
                );
            }

            usuario.setIdUsuario(Integer.parseInt(idUsuario.getText().trim()));
            usuario.setActivo(cboxActivo.isSelected());

            int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas guardar los cambios del usuario?",
                    "Confirmar actualización",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            UsuarioDAO dao = new UsuarioDAO();

            if (dao.actualizarUsuario(usuario)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Usuario actualizado correctamente."
                );

                limpiarCampos();
                cargarUsuarios();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar el usuario. Verifica los datos."
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al actualizar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_btnActualizarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JComboBox<String> cbTipoUsuario;
    private javax.swing.JCheckBox cboxActivo;
    private javax.swing.JTextField idUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtNombreCompleto;
    private javax.swing.JTextField txtNombreUsuario;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
