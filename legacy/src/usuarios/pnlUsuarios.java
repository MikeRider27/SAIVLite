/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package usuarios;

import alertas.ErrorAlert;
import alertas.SuccessAlert;
import alertas.WarningAlert;
import conexion.ConexionBD;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import librerias.RSTool;

/**
 *
 * @author RojeruSan
 */
public class pnlUsuarios extends javax.swing.JPanel {

    private int PosicionMouse;
    private int id = 0;
    public static boolean isRegistrar = true;
    public static String userTemp = "";

    /**
     * Creates new form pnlUsuarios
     */
    public pnlUsuarios() {
        initComponents();

        if (conexion.ConexionBD.conect == null) {
            new ConexionBD().conexion();
        }

        this.txtNombre.requestFocus();
        this.tabla.setCursor(new Cursor(12));
        this.scroll.getViewport().setBackground(Color.WHITE);
        this.menu.add(pnlMenu);

        id = Operaciones.extraerIDMax();
        setLimpiar();

        deshabilitarPegar();

        setToolTip();

        lblBlockMayus.setIcon(null);//ENVIO NULL LA IMAGEN
    }

    private void deshabilitarPegar() {
        InputMap map1 = this.txtBuscar.getInputMap(this.txtBuscar.WHEN_FOCUSED);
        map1.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), "null");

        InputMap map2 = this.txtNombre.getInputMap(this.txtNombre.WHEN_FOCUSED);
        map2.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), "null");

        InputMap map3 = this.txtUsuario.getInputMap(this.txtUsuario.WHEN_FOCUSED);
        map3.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), "null");

        InputMap map4 = this.txtPass.getInputMap(this.txtPass.WHEN_FOCUSED);
        map4.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK), "null");
    }

    private void setLimpiar() {
        this.txtNombre.requestFocus();
        this.txtNombre.setNextFocusableComponent(this.txtUsuario);
        this.txtUsuario.setNextFocusableComponent(this.txtPass);

        this.txtNombre.setText("");
        this.txtUsuario.setText("");
        this.txtPass.setText("");

        this.btnRegistrar.setIcon(new ImageIcon(this.getClass().getResource("/img/usuarios/btnRegistrar.png")));
        this.btnRegistrar.setText("REGISTRAR");
        this.btnCancelar.setVisible(false);

        isRegistrar = true;
        Operaciones.setListar("");
        marcarDefault();
    }

    private boolean isLlenado() {
        if (this.txtNombre.getText().isEmpty()
                || this.txtUsuario.getText().isEmpty()
                || this.txtPass.getText().isEmpty()) {

            return false;
        } else {
            return true;
        }
    }

    public void marcarDefault() {
        this.todoAlmacen.setSelected(false);
        this.registrarCategoria.setSelected(true);
        this.registrarAlmacen.setSelected(true);
        this.editarAlmacen.setSelected(false);
        this.borrarAlmacen.setSelected(false);
        this.exportarAlmacen.setSelected(false);
        this.importarAlmacen.setSelected(false);
        this.imprimirAlmacen.setSelected(true);

        this.todoVentas.setSelected(false);
        this.registrarVentas.setSelected(true);
        this.reportesVentas.setSelected(false);
        this.ultimasVentas.setSelected(false);

        this.todoCajeros.setSelected(false);
        this.registrarCajero.setSelected(false);
        this.editarCajero.setSelected(false);
        this.borrarCajero.setSelected(false);

        this.todoConfig.setSelected(false);
        this.crearConfig.setSelected(true);
        this.restoreConfig.setSelected(false);
    }

    public void marcarAlmacen() {
        if (this.todoAlmacen.isSelected()) {
            this.registrarCategoria.setSelected(true);
            this.registrarAlmacen.setSelected(true);
            this.editarAlmacen.setSelected(true);
            this.borrarAlmacen.setSelected(true);
            this.exportarAlmacen.setSelected(true);
            this.importarAlmacen.setSelected(true);
            this.imprimirAlmacen.setSelected(true);
        } else {
            this.registrarCategoria.setSelected(false);
            this.registrarAlmacen.setSelected(false);
            this.editarAlmacen.setSelected(false);
            this.borrarAlmacen.setSelected(false);
            this.exportarAlmacen.setSelected(false);
            this.importarAlmacen.setSelected(false);
            this.imprimirAlmacen.setSelected(false);
        }
    }

    public void marcarVentas() {
        if (this.todoVentas.isSelected()) {
            this.registrarVentas.setSelected(true);
            this.reportesVentas.setSelected(true);
            this.ultimasVentas.setSelected(true);
        } else {
            this.registrarVentas.setSelected(false);
            this.reportesVentas.setSelected(false);
            this.ultimasVentas.setSelected(false);
        }
    }

    public void marcarCajeros() {
        if (this.todoCajeros.isSelected()) {
            this.registrarCajero.setSelected(true);
            this.editarCajero.setSelected(true);
            this.borrarCajero.setSelected(true);
        } else {
            this.registrarCajero.setSelected(false);
            this.editarCajero.setSelected(false);
            this.borrarCajero.setSelected(false);
        }
    }

    public void marcarConfig() {
        if (this.todoConfig.isSelected()) {
            this.crearConfig.setSelected(true);
            this.restoreConfig.setSelected(true);
        } else {
            this.crearConfig.setSelected(false);
            this.restoreConfig.setSelected(false);
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

        menu = new rojerusan.RSPopuMenu();
        pnlMenu = new javax.swing.JPanel();
        btnBorrar = new rojeru_san.RSButtonRiple();
        btnEditar = new rojeru_san.RSButtonRiple();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        tabla = new rojerusan.RSTableMetro();
        txtBuscar = new rojeru_san.rsfield.RSTextMaterial();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtNombre = new rojeru_san.rsfield.RSTextMaterial();
        txtUsuario = new rojeru_san.rsfield.RSTextMaterial();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtPass = new rojeru_san.rsfield.RSPassMaterial();
        lblBlockMayus = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        todoAlmacen = new usuarios.RSCheckBox();
        registrarCategoria = new usuarios.RSCheckBox();
        registrarAlmacen = new usuarios.RSCheckBox();
        editarAlmacen = new usuarios.RSCheckBox();
        borrarAlmacen = new usuarios.RSCheckBox();
        exportarAlmacen = new usuarios.RSCheckBox();
        importarAlmacen = new usuarios.RSCheckBox();
        imprimirAlmacen = new usuarios.RSCheckBox();
        jPanel6 = new javax.swing.JPanel();
        todoVentas = new usuarios.RSCheckBox();
        registrarVentas = new usuarios.RSCheckBox();
        reportesVentas = new usuarios.RSCheckBox();
        ultimasVentas = new usuarios.RSCheckBox();
        jPanel7 = new javax.swing.JPanel();
        todoCajeros = new usuarios.RSCheckBox();
        registrarCajero = new usuarios.RSCheckBox();
        editarCajero = new usuarios.RSCheckBox();
        borrarCajero = new usuarios.RSCheckBox();
        jPanel8 = new javax.swing.JPanel();
        todoConfig = new usuarios.RSCheckBox();
        crearConfig = new usuarios.RSCheckBox();
        restoreConfig = new usuarios.RSCheckBox();
        btnCancelar = new rojeru_san.RSButtonRiple();
        btnRegistrar = new rojeru_san.RSButtonRiple();
        jPanel10 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        menu.setBackground(new java.awt.Color(255, 255, 255));
        menu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));

        pnlMenu.setBackground(new java.awt.Color(255, 255, 255));

        btnBorrar.setBackground(new java.awt.Color(69, 87, 252));
        btnBorrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuarios/btnEliminar.png"))); // NOI18N
        btnBorrar.setText("ELIMINAR");
        btnBorrar.setColorHover(new java.awt.Color(87, 103, 253));
        btnBorrar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBorrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnBorrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarActionPerformed(evt);
            }
        });

        btnEditar.setBackground(new java.awt.Color(69, 87, 252));
        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuarios/btnEditar.png"))); // NOI18N
        btnEditar.setText("EDITAR");
        btnEditar.setColorHover(new java.awt.Color(87, 103, 253));
        btnEditar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlMenuLayout = new javax.swing.GroupLayout(pnlMenu);
        pnlMenu.setLayout(pnlMenuLayout);
        pnlMenuLayout.setHorizontalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        pnlMenuLayout.setVerticalGroup(
            pnlMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuLayout.createSequentialGroup()
                .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(btnBorrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        scroll.setBorder(null);

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOMBRE", "NOMBRE DE USUARIO", "CONTRASEÑA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla.setAltoHead(30);
        tabla.setColorBackgoundHead(new java.awt.Color(69, 87, 252));
        tabla.setColorBordeHead(new java.awt.Color(255, 255, 255));
        tabla.setColorFilasBackgound2(new java.awt.Color(240, 240, 240));
        tabla.setColorFilasForeground1(new java.awt.Color(0, 0, 0));
        tabla.setColorFilasForeground2(new java.awt.Color(0, 0, 0));
        tabla.setColorHoverBackgound(new java.awt.Color(69, 87, 252));
        tabla.setColorSelBackgound(new java.awt.Color(69, 87, 252));
        tabla.setFont(new java.awt.Font("Roboto", 0, 11)); // NOI18N
        tabla.setFuenteFilas(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        tabla.setFuenteFilasSelect(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        tabla.setFuenteHead(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        tabla.setGrosorBordeFilas(0);
        tabla.setHover(true);
        tabla.setMultipleSeleccion(false);
        tabla.setRowHeight(30);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMouseClicked(evt);
            }
        });
        scroll.setViewportView(tabla);
        if (tabla.getColumnModel().getColumnCount() > 0) {
            tabla.getColumnModel().getColumn(0).setMinWidth(0);
            tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
            tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        }

        txtBuscar.setForeground(new java.awt.Color(0, 0, 0));
        txtBuscar.setColorMaterial(new java.awt.Color(69, 87, 252));
        txtBuscar.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        txtBuscar.setPlaceholder("Buscar...");
        txtBuscar.setSelectionColor(new java.awt.Color(99, 70, 250));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuarios/label-buscar.png"))); // NOI18N

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuarios/label-usuario.png"))); // NOI18N

        txtNombre.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre.setColorMaterial(new java.awt.Color(69, 87, 252));
        txtNombre.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        txtNombre.setMayusculas(true);
        txtNombre.setPlaceholder("Nombre completo (*)");
        txtNombre.setSelectionColor(new java.awt.Color(220, 23, 111));

        txtUsuario.setForeground(new java.awt.Color(0, 0, 0));
        txtUsuario.setColorMaterial(new java.awt.Color(69, 87, 252));
        txtUsuario.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        txtUsuario.setPlaceholder("Nombre de usuario (*)");
        txtUsuario.setSelectionColor(new java.awt.Color(220, 23, 111));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuarios/label-nombre.png"))); // NOI18N

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuarios/label-contraseña.png"))); // NOI18N

        txtPass.setForeground(new java.awt.Color(0, 0, 0));
        txtPass.setToolTipText("");
        txtPass.setColorMaterial(new java.awt.Color(69, 87, 252));
        txtPass.setPlaceholder("Contraseña (*)");
        txtPass.setSelectionColor(new java.awt.Color(220, 23, 111));
        txtPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPassKeyReleased(evt);
            }
        });

        lblBlockMayus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/login/label-bloq-mayusculas.png"))); // NOI18N
        lblBlockMayus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lblBlockMayusKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel8)
                                .addComponent(jLabel9))
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBlockMayus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(9, 9, 9)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8)
                                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9)
                                    .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(lblBlockMayus))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(248, 248, 248));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(248, 248, 248)), "PERMISOS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Roboto", 1, 14))); // NOI18N

        jPanel5.setBackground(new java.awt.Color(248, 248, 248));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(99, 70, 250)), "ALMACÉN", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12), new java.awt.Color(114, 114, 114))); // NOI18N

        todoAlmacen.setBorder(null);
        todoAlmacen.setForeground(new java.awt.Color(114, 114, 114));
        todoAlmacen.setText("Marcar todo");
        todoAlmacen.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        todoAlmacen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todoAlmacenActionPerformed(evt);
            }
        });

        registrarCategoria.setBorder(null);
        registrarCategoria.setForeground(new java.awt.Color(114, 114, 114));
        registrarCategoria.setSelected(true);
        registrarCategoria.setText("Registrar Categorías");
        registrarCategoria.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        registrarAlmacen.setBorder(null);
        registrarAlmacen.setForeground(new java.awt.Color(114, 114, 114));
        registrarAlmacen.setSelected(true);
        registrarAlmacen.setText("Registrar Productos");
        registrarAlmacen.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        registrarAlmacen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registrarAlmacenActionPerformed(evt);
            }
        });

        editarAlmacen.setBorder(null);
        editarAlmacen.setForeground(new java.awt.Color(114, 114, 114));
        editarAlmacen.setText("Editar Productos");
        editarAlmacen.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        borrarAlmacen.setBorder(null);
        borrarAlmacen.setForeground(new java.awt.Color(114, 114, 114));
        borrarAlmacen.setText("Eliminar Productos");
        borrarAlmacen.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        exportarAlmacen.setBorder(null);
        exportarAlmacen.setForeground(new java.awt.Color(114, 114, 114));
        exportarAlmacen.setText("Exportar a Exel");
        exportarAlmacen.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        importarAlmacen.setBorder(null);
        importarAlmacen.setForeground(new java.awt.Color(114, 114, 114));
        importarAlmacen.setText("Importar a Exel");
        importarAlmacen.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        imprimirAlmacen.setBorder(null);
        imprimirAlmacen.setForeground(new java.awt.Color(114, 114, 114));
        imprimirAlmacen.setSelected(true);
        imprimirAlmacen.setText("Imprimir Inventario");
        imprimirAlmacen.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(todoAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(borrarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(registrarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(registrarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(editarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(exportarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(importarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(imprimirAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {borrarAlmacen, editarAlmacen, exportarAlmacen, importarAlmacen, imprimirAlmacen, registrarAlmacen, registrarCategoria, todoAlmacen});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(todoAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(registrarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(registrarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(exportarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imprimirAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(borrarAlmacen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(248, 248, 248));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(99, 70, 250)), "VENTAS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Roboto", 1, 12), new java.awt.Color(114, 114, 114))); // NOI18N

        todoVentas.setBorder(null);
        todoVentas.setForeground(new java.awt.Color(114, 114, 114));
        todoVentas.setText("Marcar Todo");
        todoVentas.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        todoVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todoVentasActionPerformed(evt);
            }
        });

        registrarVentas.setBorder(null);
        registrarVentas.setForeground(new java.awt.Color(114, 114, 114));
        registrarVentas.setSelected(true);
        registrarVentas.setText("Registrar Ventas");
        registrarVentas.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        reportesVentas.setBorder(null);
        reportesVentas.setForeground(new java.awt.Color(114, 114, 114));
        reportesVentas.setText("Reportes");
        reportesVentas.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        ultimasVentas.setBorder(null);
        ultimasVentas.setForeground(new java.awt.Color(114, 114, 114));
        ultimasVentas.setText("Útimas Ventas");
        ultimasVentas.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(todoVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(registrarVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reportesVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ultimasVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {registrarVentas, reportesVentas, todoVentas, ultimasVentas});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(todoVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registrarVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportesVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ultimasVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(248, 248, 248));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(99, 70, 250)), "CAJEROS", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Roboto", 1, 12), new java.awt.Color(114, 114, 114))); // NOI18N

        todoCajeros.setBorder(null);
        todoCajeros.setForeground(new java.awt.Color(114, 114, 114));
        todoCajeros.setText("Marcar Todo");
        todoCajeros.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        todoCajeros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todoCajerosActionPerformed(evt);
            }
        });

        registrarCajero.setBorder(null);
        registrarCajero.setForeground(new java.awt.Color(114, 114, 114));
        registrarCajero.setText("Registrar Cajeros");
        registrarCajero.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        editarCajero.setBorder(null);
        editarCajero.setForeground(new java.awt.Color(114, 114, 114));
        editarCajero.setText("Editar Cajeros");
        editarCajero.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        editarCajero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarCajeroActionPerformed(evt);
            }
        });

        borrarCajero.setBorder(null);
        borrarCajero.setForeground(new java.awt.Color(114, 114, 114));
        borrarCajero.setText("Eliminar Cajeros");
        borrarCajero.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(todoCajeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(registrarCajero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editarCajero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(borrarCajero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {borrarCajero, editarCajero, registrarCajero, todoCajeros});

        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(todoCajeros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(registrarCajero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editarCajero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(borrarCajero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(248, 248, 248));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 104, 178)), "CONFIGURACIÓN", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Roboto", 1, 12), new java.awt.Color(114, 114, 114))); // NOI18N

        todoConfig.setBorder(null);
        todoConfig.setForeground(new java.awt.Color(114, 114, 114));
        todoConfig.setText("Marcar Todo");
        todoConfig.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        todoConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                todoConfigActionPerformed(evt);
            }
        });

        crearConfig.setBorder(null);
        crearConfig.setForeground(new java.awt.Color(114, 114, 114));
        crearConfig.setSelected(true);
        crearConfig.setText("Crear Respaldos");
        crearConfig.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        restoreConfig.setBorder(null);
        restoreConfig.setForeground(new java.awt.Color(114, 114, 114));
        restoreConfig.setText("Restaurar Respaldos");
        restoreConfig.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(todoConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(crearConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(restoreConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {crearConfig, restoreConfig, todoConfig});

        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(todoConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crearConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(restoreConfig, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btnCancelar.setBackground(new java.awt.Color(243, 66, 53));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuarios/btn-cancelar.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setColorHover(new java.awt.Color(173, 187, 194));
        btnCancelar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnRegistrar.setBackground(new java.awt.Color(69, 87, 252));
        btnRegistrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/almacen/btn-registrar.png"))); // NOI18N
        btnRegistrar.setText("Registar");
        btnRegistrar.setColorHover(new java.awt.Color(173, 187, 194));
        btnRegistrar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRegistrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(247, 247, 247));

        jLabel4.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel4.setText("CAJEROS");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBorrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarActionPerformed
        if (privilegios.Operaciones.EliminarCajero(principal.Principal.lblID.getText())) {
            this.menu.setVisible(false);
            int fila = this.tabla.getSelectedRow();
            id = Integer.parseInt(this.tabla.getValueAt(fila, 0).toString());

            String usuario = this.tabla.getValueAt(fila, 2).toString();

            WarningAlert w = new WarningAlert(new JFrame(), true);
            w.msj1.setText("Se eliminara el cajero " + usuario);
            w.msj2.setText("de manera permanente del sistema.");
            w.msj3.setText("");
            w.setVisible(true);

            if (w.hecho) {
                if (isEliminado()) {
                    setLimpiar();
                    id = Operaciones.extraerIDMax();

                    SuccessAlert s = new SuccessAlert(new JFrame(), true);
                    s.msj1.setText("Cajero eliminado con éxito");
                    s.msj2.setText("");
                    s.msj3.setText("");
                    s.setVisible(true);
                } else {
                    ErrorAlert e = new ErrorAlert(new JFrame(), true);
                    e.msj1.setText("Algo salio mal. No fue posible");
                    e.msj2.setText("continuar con esta acción.");
                    e.msj3.setText("");
                    e.setVisible(true);
                }
            } else {
                id = Operaciones.extraerIDMax();
            }
        } else {
            ErrorAlert e = new ErrorAlert(new JFrame(), true);
            e.msj1.setText("No cuentas con los privilegios");
            e.msj2.setText("para acceder a esta opción.");
            e.msj3.setText("");
            e.setVisible(true);
        }
    }//GEN-LAST:event_btnBorrarActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        if (privilegios.Operaciones.EditarCajero(principal.Principal.lblID.getText())) {
            this.menu.setVisible(false);
            int fila = this.tabla.getSelectedRow();
            id = Integer.parseInt(this.tabla.getValueAt(fila, 0).toString());
            Operaciones.extraerDatos(this, String.valueOf(id));
        } else {
            ErrorAlert e = new ErrorAlert(new JFrame(), true);
            e.msj1.setText("No cuentas con los privilegios");
            e.msj2.setText("para acceder a esta opción.");
            e.msj3.setText("");
            e.setVisible(true);
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void todoAlmacenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todoAlmacenActionPerformed
        marcarAlmacen();
    }//GEN-LAST:event_todoAlmacenActionPerformed

    private void todoVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todoVentasActionPerformed
        marcarVentas();
    }//GEN-LAST:event_todoVentasActionPerformed

    private void todoCajerosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todoCajerosActionPerformed
        marcarCajeros();
    }//GEN-LAST:event_todoCajerosActionPerformed

    private void todoConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_todoConfigActionPerformed
        marcarConfig();
    }//GEN-LAST:event_todoConfigActionPerformed

    private void registrarAlmacenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registrarAlmacenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_registrarAlmacenActionPerformed

    private void editarCajeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarCajeroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_editarCajeroActionPerformed

    private void tablaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMouseClicked
        int row = tabla.rowAtPoint(evt.getPoint());
        if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
            this.tabla.setRowSelectionInterval(row, row);
            PosicionMouse = evt.getY() / 16;
            menu.show(evt.getComponent(), evt.getX(), evt.getY());
        } else {
            this.tabla.setRowSelectionInterval(row, row);
        }
    }//GEN-LAST:event_tablaMouseClicked

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        Operaciones.setListar(this.txtBuscar.getText());
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        setLimpiar();
        id = Operaciones.extraerIDMax();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        if (privilegios.Operaciones.RegistrarCajero(principal.Principal.lblID.getText())) {
            if (isLlenado()) {
                if (isRegistrar) {
                    if (Operaciones.isExiste(this.txtUsuario.getText())) {
                        ErrorAlert e = new ErrorAlert(new JFrame(), true);
                        e.msj1.setText("El nombre de usuario que intentas");
                        e.msj2.setText("registrar ya éxiste.");
                        e.msj3.setText("");
                        e.setVisible(true);
                    } else {
                        if (isRegistrado()) {
                            setLimpiar();
                            Operaciones.selecionaFila(String.valueOf(id));
                            id = Operaciones.extraerIDMax();

                            marcarDefault();

                            SuccessAlert s = new SuccessAlert(new JFrame(), true);
                            s.msj1.setText("Nuevo cajero dado de alta");
                            s.msj2.setText("con éxito.");
                            s.msj3.setText("");
                            s.setVisible(true);
                        } else {
                            ErrorAlert e = new ErrorAlert(new JFrame(), true);
                            e.msj1.setText("Algo salio mal. No fue posible");
                            e.msj2.setText("registrar el nuevo cajero.");
                            e.msj3.setText("");
                            e.setVisible(true);
                        }
                    }
                } else {
                    if (Operaciones.isExiste(this.txtUsuario.getText()) && !userTemp.equals(this.txtUsuario.getText())) {
                        ErrorAlert e = new ErrorAlert(new JFrame(), true);
                        e.msj1.setText("El nombre de usuario que intentas");
                        e.msj2.setText("guardar ya éxiste.");
                        e.msj3.setText("");
                        e.setVisible(true);
                    } else {
                        if (isEditado()) {
                            setLimpiar();
                            Operaciones.selecionaFila(String.valueOf(id));
                            id = Operaciones.extraerIDMax();

                            marcarDefault();

                            SuccessAlert s = new SuccessAlert(new JFrame(), true);
                            s.msj1.setText("Datos guardados con éxito");
                            s.msj2.setText("");
                            s.msj3.setText("");
                            s.setVisible(true);
                        } else {

                            ErrorAlert e = new ErrorAlert(new JFrame(), true);
                            e.msj1.setText("Algo salio mal. No fue posible");
                            e.msj2.setText("guardar los cambios.");
                            e.msj3.setText("");
                            e.setVisible(true);
                        }
                    }
                }
            } else {
                setLimpiar();

                ErrorAlert e = new ErrorAlert(new JFrame(), true);
                e.msj1.setText("Todos los campos marcados con (*)");
                e.msj2.setText("son requeridos.");
                e.msj3.setText("");
                e.setVisible(true);
            }
        } else {
            ErrorAlert e = new ErrorAlert(new JFrame(), true);
            e.msj1.setText("No cuentas con los privilegios");
            e.msj2.setText("para acceder a esta opción.");
            e.msj3.setText("");
            e.setVisible(true);
        }
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void lblBlockMayusKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lblBlockMayusKeyReleased

    }//GEN-LAST:event_lblBlockMayusKeyReleased

    private void txtPassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassKeyReleased
        boolean capsActivo = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
        if (capsActivo == true) {
            System.out.println("Me activaste");
            lblBlockMayus.setIcon(new ImageIcon(getClass().getResource("/img/login/label-bloq-mayusculas.png")));
        }
        if (capsActivo == false) {
            lblBlockMayus.setIcon(null);//ENVIO NULL LA IMAGEN
        }
    }//GEN-LAST:event_txtPassKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static usuarios.RSCheckBox borrarAlmacen;
    public static usuarios.RSCheckBox borrarCajero;
    private rojeru_san.RSButtonRiple btnBorrar;
    public static rojeru_san.RSButtonRiple btnCancelar;
    private rojeru_san.RSButtonRiple btnEditar;
    public static rojeru_san.RSButtonRiple btnRegistrar;
    public static usuarios.RSCheckBox crearConfig;
    public static usuarios.RSCheckBox editarAlmacen;
    public static usuarios.RSCheckBox editarCajero;
    public static usuarios.RSCheckBox exportarAlmacen;
    public static usuarios.RSCheckBox importarAlmacen;
    public static usuarios.RSCheckBox imprimirAlmacen;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    public static javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lblBlockMayus;
    private rojerusan.RSPopuMenu menu;
    private javax.swing.JPanel pnlMenu;
    public static usuarios.RSCheckBox registrarAlmacen;
    public static usuarios.RSCheckBox registrarCajero;
    public static usuarios.RSCheckBox registrarCategoria;
    public static usuarios.RSCheckBox registrarVentas;
    public static usuarios.RSCheckBox reportesVentas;
    public static usuarios.RSCheckBox restoreConfig;
    private javax.swing.JScrollPane scroll;
    public static rojerusan.RSTableMetro tabla;
    public static usuarios.RSCheckBox todoAlmacen;
    public static usuarios.RSCheckBox todoCajeros;
    public static usuarios.RSCheckBox todoConfig;
    public static usuarios.RSCheckBox todoVentas;
    private rojeru_san.rsfield.RSTextMaterial txtBuscar;
    public static rojeru_san.rsfield.RSTextMaterial txtNombre;
    public static rojeru_san.rsfield.RSPassMaterial txtPass;
    public static rojeru_san.rsfield.RSTextMaterial txtUsuario;
    public static usuarios.RSCheckBox ultimasVentas;
    // End of variables declaration//GEN-END:variables

    private boolean isRegistrado() {
        int errores = 0;
        Sentencias s = new Sentencias();

        s.setId(this.id);
        s.setNombre(this.txtNombre.getText());
        s.setUsuario(this.txtUsuario.getText());
        s.setPassword(this.txtPass.getText());

        if (Operaciones.isRegistrado(s)) {

            privilegios.Sentencias p = new privilegios.Sentencias();

            // PRIVILEGIOS SOBRE EL ALMACÉN       
            p.setId_user(this.id);
            p.setRegistrar_cat(this.registrarCategoria.isSelected());
            p.setRegistrar_pro(this.registrarAlmacen.isSelected());
            p.setEditar_pro(this.editarAlmacen.isSelected());
            p.setEliminar_pro(this.borrarAlmacen.isSelected());
            p.setExportar(this.exportarAlmacen.isSelected());
            p.setImportar(this.importarAlmacen.isSelected());
            p.setImprimir(this.imprimirAlmacen.isSelected());

            if (!privilegios.Operaciones.isRegistradoAlmacen(p)) {
                errores++;
            }

            // PRIVILEGIOS SOBRE LAS VENTAS      
            p.setId_user(this.id);
            p.setRegistrar(this.registrarVentas.isSelected());
            p.setReportes(this.reportesVentas.isSelected());
            p.setUltimas_ventas(this.ultimasVentas.isSelected());

            if (!privilegios.Operaciones.isRegistradoVentas(p)) {
                errores++;
            }

            // PRIVILEGIOS SOBRE LOS CAJEROS      
            p.setId_user(this.id);
            p.setRegistrarCajero(this.registrarCajero.isSelected());
            p.setEditar(this.editarCajero.isSelected());
            p.setEliminar(this.borrarCajero.isSelected());

            if (!privilegios.Operaciones.isRegistradoCajero(p)) {
                errores++;
            }

            // PRIVILEGIOS SOBRE LA CONFIGURACIÓN      
            p.setId_user(this.id);
            p.setCrear_backup(this.crearConfig.isSelected());
            p.setRestore_backup(this.restoreConfig.isSelected());

            if (!privilegios.Operaciones.isRegistradoConfig(p)) {
                errores++;
            }

            if (errores != 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isEditado() {
        int errores = 0;
        Sentencias s = new Sentencias();

        s.setNombre(this.txtNombre.getText());
        s.setUsuario(this.txtUsuario.getText());
        s.setPassword(this.txtPass.getText());
        s.setId(this.id);

        if (Operaciones.isEditado(s)) {
            privilegios.Sentencias p = new privilegios.Sentencias();

            // PRIVILEGIOS SOBRE EL ALMACÉN       
            p.setId_user(this.id);
            p.setRegistrar_cat(this.registrarCategoria.isSelected());
            p.setRegistrar_pro(this.registrarAlmacen.isSelected());
            p.setEditar_pro(this.editarAlmacen.isSelected());
            p.setEliminar_pro(this.borrarAlmacen.isSelected());
            p.setExportar(this.exportarAlmacen.isSelected());
            p.setImportar(this.importarAlmacen.isSelected());
            p.setImprimir(this.imprimirAlmacen.isSelected());

            if (!privilegios.Operaciones.isEditadoAlmacen(p)) {
                errores++;
            }

            // PRIVILEGIOS SOBRE LAS VENTAS      
            p.setId_user(this.id);
            p.setRegistrar(this.registrarVentas.isSelected());
            p.setReportes(this.reportesVentas.isSelected());
            p.setUltimas_ventas(this.ultimasVentas.isSelected());

            if (!privilegios.Operaciones.isEditadoVentas(p)) {
                errores++;
            }

            // PRIVILEGIOS SOBRE LOS CAJEROS      
            p.setId_user(this.id);
            p.setRegistrarCajero(this.registrarCajero.isSelected());
            p.setEditar(this.editarCajero.isSelected());
            p.setEliminar(this.borrarCajero.isSelected());

            if (!privilegios.Operaciones.isEditadoCajero(p)) {
                errores++;
            }

            // PRIVILEGIOS SOBRE LA CONFIGURACIÓN      
            p.setId_user(this.id);
            p.setCrear_backup(this.crearConfig.isSelected());
            p.setRestore_backup(this.restoreConfig.isSelected());

            if (!privilegios.Operaciones.isEditadoConfig(p)) {
                errores++;
            }

            if (errores != 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private boolean isEliminado() {
        int errores = 0;
        Sentencias s = new Sentencias();
        s.setId(this.id);

        if (Operaciones.isEliminado(s)) {

            privilegios.Sentencias p = new privilegios.Sentencias();

            //ALMACÉN            
            p.setId_user(this.id);

            if (!privilegios.Operaciones.isEliminadoAlmacen(p)) {
                errores++;
            }

            //VENTAS                       
            if (!privilegios.Operaciones.isEliminadoVentas(p)) {
                errores++;
            }

            //CAJEROS                       
            if (!privilegios.Operaciones.isEliminadoCajero(p)) {
                errores++;
            }

            //CONFIGURACIÓN                       
            if (!privilegios.Operaciones.isEliminadoConfig(p)) {
                errores++;
            }

            if (errores != 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void setToolTip() {
        txtNombre.setToolTipText(RSTool.head + RSTool.body + "Nombre completo del Cajero" + RSTool.pie);
        txtUsuario.setToolTipText(RSTool.head + RSTool.body + "Nombre de usuario para el Cajero" + RSTool.pie);
        txtPass.setToolTipText(RSTool.head + RSTool.body + "Contraseña para el Cajero" + RSTool.pie);
    }
}
