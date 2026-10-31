package modelo;

import dao.ClienteDAO;
import modelo.Cliente;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class DlgClienteRapido extends JDialog {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final String nit;
    private final boolean nuevaSucursal;

    private final JTextField txtNit = new JTextField(22);
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtTelefono = new JTextField(22);
    private final JTextField txtDireccion = new JTextField(22);

    private Cliente clienteGuardado = null;

    public DlgClienteRapido(Window padre, String nit, String nombreSugerido,
            String telefonoSugerido, boolean nuevaSucursal) {

        super(padre,
                nuevaSucursal ? "Nueva sucursal" : "Registrar cliente",
                Dialog.ModalityType.APPLICATION_MODAL);

        this.nit = nit;
        this.nuevaSucursal = nuevaSucursal;

        txtNit.setText(nit);
        txtNit.setEditable(false);
        txtNombre.setText(nombreSugerido == null ? "" : nombreSugerido);
        txtTelefono.setText(telefonoSugerido == null ? "" : telefonoSugerido);

        construirPantalla();

        pack();
        setResizable(false);
        setLocationRelativeTo(padre);

        SwingUtilities.invokeLater(()
                -> (nuevaSucursal ? txtDireccion : txtNombre).requestFocusInWindow());
    }

    private void construirPantalla() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        agregarFila(formulario, gbc, 0, "NIT:", txtNit);
        agregarFila(formulario, gbc, 1, "Nombre:", txtNombre);
        agregarFila(formulario, gbc, 2, "Teléfono:", txtTelefono);
        agregarFila(formulario, gbc, 3, "Dirección:", txtDireccion);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botones.add(btnCancelar);
        botones.add(btnGuardar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(formulario, BorderLayout.CENTER);
        getContentPane().add(botones, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnGuardar);
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int fila,
            String etiqueta, JTextField campo) {

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gbc);
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nit.isEmpty() || nit.equals("CF")) {
            aviso("El NIT CF es el cliente Consumidor Final y ya se maneja "
                    + "desde la venta (nombre y dirección opcionales).");
            return;
        }

        if (nombre.isEmpty()) {
            aviso("El nombre es obligatorio.");
            txtNombre.requestFocusInWindow();
            return;
        }

        try {
            List<Cliente> existentes = clienteDAO.listarPorNit(nit);

            if (!existentes.isEmpty() && direccion.isEmpty()) {
                aviso("Este NIT ya tiene otro registro.\n"
                        + "Escribe la dirección para distinguir la sucursal.");
                txtDireccion.requestFocusInWindow();
                return;
            }

            Cliente duplicado = clienteDAO.buscarMismaSucursal(nit, direccion, null);
            if (duplicado != null) {
                JOptionPane.showMessageDialog(this,
                        "Ya existe " + duplicado.getNombreCliente()
                        + " con este NIT y esa misma dirección.\n"
                        + "No se puede registrar dos veces.",
                        "Cliente duplicado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!existentes.isEmpty() && !nuevaSucursal) {
                Cliente referencia = existentes.get(0);
                String dondeEsta = (referencia.getDireccion() == null
                        || referencia.getDireccion().isBlank())
                        ? "(sin dirección)"
                        : referencia.getDireccion();

                int respuesta = JOptionPane.showConfirmDialog(this,
                        "Ya existe " + referencia.getNombreCliente()
                        + " con este NIT en " + dondeEsta + ".\n¿Es otra sucursal?",
                        "NIT ya registrado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (respuesta != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Cliente nuevo = new Cliente("", nombre, telefono, nit, direccion);
            clienteDAO.registrarConCodigoAutomatico(nuevo);

            clienteGuardado = nuevo;
            dispose();

        } catch (IllegalArgumentException e) {
            aviso(e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el cliente:\n" + e.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Atención",
                JOptionPane.WARNING_MESSAGE);
    }

    public Cliente getClienteGuardado() {
        return clienteGuardado;
    }
}
