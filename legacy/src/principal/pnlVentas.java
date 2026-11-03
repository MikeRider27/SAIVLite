/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package principal;

import alertas.ErrorAlert;
import alertas.SuccessAlert;
import alertas.WarningAlert;
import conexion.ConexionBD;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import librerias.RSTool;
import ticket.PrintTicket;
import ventas.Cobrar;
import ventas.Operaciones;
import ventas.Sentencias;

/**
 *
 * @author RojeruSan
 */
public class pnlVentas extends javax.swing.JPanel {

    /**
     * Creates new form pnlVentas
     */
    public pnlVentas() {
        initComponents();

        if (conexion.ConexionBD.conect == null) {
            new ConexionBD().conexion();
        }

        this.tabla.setCursor(new Cursor(12));
        this.scroll.getViewport().setBackground(Color.WHITE);

        this.tabla.setDefaultRenderer(Object.class, new modelosTablas.TablaVentas());

        Operaciones.setListarCaja();
        Operaciones.totalCaja();
        this.lblNumVenta.setText(String.valueOf(Operaciones.extraerIDVenta()));
        this.lblNumVenta.setVisible(false);

        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtCodigo.requestFocus();
            }
        });
        timer.setRepeats(true);
        timer.start();

        addEventKey();

        setToolTip();

    }

    private void addEventKey() {

        KeyStroke f1 = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
        Action f1Action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                new ventas.Productos(new JFrame(), true).setVisible(true);
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f1, "F1");
        this.getActionMap().put("F1", f1Action);

        //---------------------------------------------------------------------------
        KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false);
        Action f2Action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (tabla.getRowCount() > 0) {
                    Cobrar c = new Cobrar(new JFrame(), true);
                    c.lblTotal.setText("$" + lblTotal.getText());
                    c.totalCobrar = Double.parseDouble(lblTotal.getText());
                    c.txtPagoCon.setText(lblTotal.getText());
                    c.txtPagoCon.setSelectionStart(0);
                    c.txtPagoCon.setSelectionEnd(c.txtPagoCon.getText().length());
                    c.lblCantidad.setText(String.valueOf(Operaciones.totalCantidad()));
                    c.setVisible(true);

                    if (c.ventaSinTicket) {
                        realizarVenta();
                    }

                    if (c.ventaConTicket) {
                        realizarVentaTicket();
                    }
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f2, "F2");
        this.getActionMap().put("F2", f2Action);

        //------------------------------------------------------------------------------
        KeyStroke f3 = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false);
        Action f3Action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (tabla.getSelectedRow() > -1) {
                    int id = Integer.parseInt(tabla.getValueAt(tabla.getSelectedRow(), 0).toString());
                    Sentencias s = new Sentencias();
                    s.setIdCaja(id);
                    if (Operaciones.isEliminadoCaja(s)) {
                        Operaciones.setListarCaja();
                        Operaciones.totalCaja();
                    }
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f3, "F3");
        this.getActionMap().put("F3", f3Action);

        //--------------------------------------------------------------------------------
        KeyStroke f4 = KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, false);
        Action f4Action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (Operaciones.isVaciadoCaja()) {
                    Operaciones.setListarCaja();
                    Operaciones.totalCaja();
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f4, "F4");
        this.getActionMap().put("F4", f4Action);

        //--------------------------------------------------------------------------------
        KeyStroke mas = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0);
        Action masAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (tabla.getSelectedRowCount() > 0) {
                    int fila = tabla.getSelectedRow();
                    int cantidad = Integer.parseInt(tabla.getValueAt(fila, 2).toString());

                    if (!tabla.getValueAt(fila, 4).toString().equals("Ilimitadas")) {
                        int existencias = Integer.parseInt(tabla.getValueAt(fila, 4).toString());

                        if (cantidad != existencias) {
                            actualizarCantidad(1);
                        } else {
                            ErrorAlert e1 = new ErrorAlert(new JFrame(), true);
                            e1.msj1.setText("Inventario insuficiente,");
                            e1.msj2.setText("hay: " + existencias + " disponibles.");
                            e1.msj3.setText("");
                            e1.setVisible(true);
                        }
                    } else {
                        actualizarCantidad(1);
                    }
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(mas, "ADD");
        this.getActionMap().put("ADD", masAction);

        //--------------------------------------------------------------------------------
        KeyStroke menos = KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0, false);
        Action menosAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (tabla.getSelectedRowCount() > 0) {
                    int fila = tabla.getSelectedRow();
                    int cantidad = Integer.parseInt(tabla.getValueAt(fila, 2).toString());

                    if (cantidad != 1) {
                        actualizarCantidad(-1);
                    } else {
                        WarningAlert w = new WarningAlert(new JFrame(), true);
                        w.msj1.setText("Se quitara este producto de la lista.");
                        w.msj2.setText("");
                        w.msj3.setText("");
                        w.setVisible(true);

                        if (w.hecho) {
                            int id = Integer.parseInt(tabla.getValueAt(tabla.getSelectedRow(), 0).toString());
                            Sentencias s = new Sentencias();
                            s.setIdCaja(id);
                            if (Operaciones.isEliminadoCaja(s)) {
                                Operaciones.setListarCaja();
                                Operaciones.totalCaja();
                            }
                        }
                    }
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(menos, "SUBTRACT");
        this.getActionMap().put("SUBTRACT", menosAction);

        //--------------------------------------------------------------------------------
        KeyStroke subir = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true);
        Action subirAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (tabla.getSelectedRowCount() > 0) {
                    int fila = tabla.getSelectedRow();

                    if (fila == 0) {
                        tabla.setRowSelectionInterval(tabla.getRowCount() - 1, tabla.getRowCount() - 1);
                    } else {
                        tabla.setRowSelectionInterval(fila - 1, fila - 1);
                    }
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(subir, "UP");
        this.getActionMap().put("UP", subirAction);

        //--------------------------------------------------------------------------------
        KeyStroke bajar = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true);
        Action bajarAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (tabla.getSelectedRowCount() > 0) {
                    int fila = tabla.getSelectedRow();

                    if (tabla.getRowCount() == (fila + 1)) {
                        tabla.setRowSelectionInterval(0, 0);
                    } else {
                        tabla.setRowSelectionInterval(fila + 1, fila + 1);
                    }
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(bajar, "DOWN");
        this.getActionMap().put("DOWN", bajarAction);
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
        scroll = new javax.swing.JScrollPane();
        tabla = new rojerusan.RSTableMetro();
        txtCodigo = new rojeru_san.rsfield.RSTextMaterial();
        jLabel1 = new javax.swing.JLabel();
        btnBuscar = new rojeru_san.RSButtonRiple();
        jPanel1 = new javax.swing.JPanel();
        lblFecha = new rojeru_san.rsdate.RSLabelFecha();
        jLabel6 = new javax.swing.JLabel();
        lblHora = new rojeru_san.rsdate.RSLabelHora();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblNumVenta = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        btnQuitar = new rojeru_san.RSButtonRiple();
        btnCancelar = new rojeru_san.RSButtonRiple();
        btnCobrar = new rojeru_san.RSButtonRiple();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        scroll.setBorder(null);

        tabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Código de barras", "Cantidad", "Descripción", "Existencias", "Precio", "Importe", "categoria", "precio_compra", "utiliza_inventario"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
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
        tabla.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        tabla.setFuenteHead(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        tabla.setGrosorBordeFilas(0);
        tabla.setHover(true);
        tabla.setMultipleSeleccion(false);
        tabla.setRowHeight(30);
        tabla.setSelectionBackground(new java.awt.Color(99, 70, 250));
        tabla.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(tabla);
        if (tabla.getColumnModel().getColumnCount() > 0) {
            tabla.getColumnModel().getColumn(0).setMinWidth(0);
            tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
            tabla.getColumnModel().getColumn(0).setMaxWidth(0);
            tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
            tabla.getColumnModel().getColumn(1).setMaxWidth(1000);
            tabla.getColumnModel().getColumn(2).setPreferredWidth(50);
            tabla.getColumnModel().getColumn(2).setMaxWidth(1000);
            tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
            tabla.getColumnModel().getColumn(4).setMaxWidth(1000);
            tabla.getColumnModel().getColumn(5).setPreferredWidth(100);
            tabla.getColumnModel().getColumn(5).setMaxWidth(1000);
            tabla.getColumnModel().getColumn(6).setPreferredWidth(100);
            tabla.getColumnModel().getColumn(6).setMaxWidth(1000);
            tabla.getColumnModel().getColumn(7).setMinWidth(0);
            tabla.getColumnModel().getColumn(7).setPreferredWidth(0);
            tabla.getColumnModel().getColumn(7).setMaxWidth(0);
            tabla.getColumnModel().getColumn(8).setMinWidth(0);
            tabla.getColumnModel().getColumn(8).setPreferredWidth(0);
            tabla.getColumnModel().getColumn(8).setMaxWidth(0);
            tabla.getColumnModel().getColumn(9).setMinWidth(50);
            tabla.getColumnModel().getColumn(9).setPreferredWidth(50);
            tabla.getColumnModel().getColumn(9).setMaxWidth(50);
        }

        txtCodigo.setForeground(new java.awt.Color(0, 0, 0));
        txtCodigo.setColorMaterial(new java.awt.Color(69, 87, 252));
        txtCodigo.setPlaceholder("Código del producto");
        txtCodigo.setSelectionColor(new java.awt.Color(220, 23, 111));
        txtCodigo.setSoloNumeros(true);
        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodigoKeyPressed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/principal/lblCod.png"))); // NOI18N

        btnBuscar.setBackground(new java.awt.Color(69, 87, 252));
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/principal/btnBuscar.png"))); // NOI18N
        btnBuscar.setText("Buscar sin código (F1)");
        btnBuscar.setColorHover(new java.awt.Color(173, 187, 194));
        btnBuscar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(247, 247, 247));

        lblFecha.setForeground(new java.awt.Color(93, 97, 92));
        lblFecha.setFont(new java.awt.Font("Roboto Bold", 1, 24)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ventas/lblDate.png"))); // NOI18N

        lblHora.setForeground(new java.awt.Color(93, 97, 92));
        lblHora.setFont(new java.awt.Font("Roboto Bold", 1, 24)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/ventas/lblHora.png"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Roboto", 1, 36)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("MXN");

        lblTotal.setBackground(new java.awt.Color(51, 51, 51));
        lblTotal.setFont(new java.awt.Font("Roboto", 1, 36)); // NOI18N
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotal.setText("0.00");

        jLabel5.setFont(new java.awt.Font("Roboto", 1, 36)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("TOTAL: $");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblHora, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 196, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                .addComponent(lblTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7, jLabel8, lblFecha, lblHora, lblTotal});

        jPanel3.setBackground(new java.awt.Color(247, 247, 247));

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel3.setText("VENTA DE PRODUCTOS");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblNumVenta.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        lblNumVenta.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblNumVenta.setText("0");

        jPanel5.setBackground(new java.awt.Color(247, 247, 247));

        btnQuitar.setBackground(new java.awt.Color(69, 87, 252));
        btnQuitar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/principal/btnQuitar.png"))); // NOI18N
        btnQuitar.setText("Quitar producto (F3)");
        btnQuitar.setColorHover(new java.awt.Color(173, 187, 194));
        btnQuitar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnQuitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarActionPerformed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(243, 66, 53));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/principal/btnCancelar.png"))); // NOI18N
        btnCancelar.setText("Cancelar Venta (F4)");
        btnCancelar.setColorHover(new java.awt.Color(173, 187, 194));
        btnCancelar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnCobrar.setBackground(new java.awt.Color(69, 87, 252));
        btnCobrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/principal/btnCobrar.png"))); // NOI18N
        btnCobrar.setText("Cobrar venta(F2)");
        btnCobrar.setColorHover(new java.awt.Color(173, 187, 194));
        btnCobrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCobrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCobrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCancelar, btnCobrar, btnQuitar});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnQuitar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnCobrar, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnCancelar, btnCobrar, btnQuitar});

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scroll)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNumVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblNumVenta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        new ventas.Productos(new JFrame(), true).setVisible(true);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed

        WarningAlert w = new WarningAlert(new JFrame(), true);

        if (this.tabla.getRowCount() > 0) {
            w.msj1.setText("Estas seguro(a)?");
            w.msj2.setText("Se elminaran los productos de la tabla.");
            w.msj3.setText("");
            w.setVisible(true);
            if (w.hecho) {

                if (Operaciones.isVaciadoCaja()) {
                    Operaciones.setListarCaja();
                    Operaciones.totalCaja();
                }
            }
        }


    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnQuitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarActionPerformed
        if (this.tabla.getSelectedRow() > -1) {
            int id = Integer.parseInt(this.tabla.getValueAt(this.tabla.getSelectedRow(), 0).toString());
            Sentencias s = new Sentencias();
            s.setIdCaja(id);
            if (Operaciones.isEliminadoCaja(s)) {
                Operaciones.setListarCaja();
                Operaciones.totalCaja();
            }
        }
    }//GEN-LAST:event_btnQuitarActionPerformed

    private void btnCobrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCobrarActionPerformed
        if (this.tabla.getRowCount() > 0) {
            Cobrar c = new Cobrar(new JFrame(), true);
            c.lblTotal.setText("$" + this.lblTotal.getText());
            c.totalCobrar = Double.parseDouble(this.lblTotal.getText());
            c.txtPagoCon.setText(this.lblTotal.getText());
            c.txtPagoCon.setSelectionStart(0);
            c.txtPagoCon.setSelectionEnd(c.txtPagoCon.getText().length());
            c.lblCantidad.setText(String.valueOf(Operaciones.totalCantidad()));
            c.setVisible(true);

            if (c.ventaSinTicket) {
                realizarVenta();
            }

            if (c.ventaConTicket) {
                realizarVentaTicket();
            }
        }
    }//GEN-LAST:event_btnCobrarActionPerformed

    private void txtCodigoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!this.txtCodigo.getText().isEmpty()) {
                enviarCaja();
                this.txtCodigo.setText("");
            }
        }
    }//GEN-LAST:event_txtCodigoKeyPressed

    private void enviarCaja() {

        if (Operaciones.isExisteEnAlmacen(this.txtCodigo.getText())) {

            if (Operaciones.isInventaro_utiliza(this.txtCodigo.getText())) {
                if (Operaciones.extraerCantidad(this.txtCodigo.getText()) < Operaciones.extraerExistencias(this.txtCodigo.getText())) {
                    if (!Operaciones.isRCaja(this.txtCodigo.getText(), 1, true)) {
                        ErrorAlert e = new ErrorAlert(new JFrame(), true);
                        e.msj1.setText("Ha ocurrido un error inesperado.");
                        e.msj2.setText("");
                        e.msj3.setText("");
                        e.setVisible(true);
                    }
                } else {
                    ErrorAlert e = new ErrorAlert(new JFrame(), true);
                    e.msj1.setText("Inventario insuficiente.");
                    e.msj2.setText("");
                    e.msj3.setText("");
                    e.setVisible(true);
                }
            } else {
                if (!Operaciones.isRCaja(this.txtCodigo.getText(), 1, false)) {
                    ErrorAlert e = new ErrorAlert(new JFrame(), true);
                    e.msj1.setText("Ha ocurrido un error inesperado.");
                    e.msj2.setText("");
                    e.msj3.setText("");
                    e.setVisible(true);
                }
            }
        } else {
            ErrorAlert e = new ErrorAlert(new JFrame(), true);
            e.msj1.setText("Producto no encontrado.");
            e.msj2.setText("");
            e.msj3.setText("");
            e.setVisible(true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private rojeru_san.RSButtonRiple btnBuscar;
    private rojeru_san.RSButtonRiple btnCancelar;
    private rojeru_san.RSButtonRiple btnCobrar;
    private rojeru_san.RSButtonRiple btnQuitar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    public static rojeru_san.rsdate.RSLabelFecha lblFecha;
    public static rojeru_san.rsdate.RSLabelHora lblHora;
    private javax.swing.JLabel lblNumVenta;
    public static javax.swing.JLabel lblTotal;
    private javax.swing.JScrollPane scroll;
    public static rojerusan.RSTableMetro tabla;
    private rojeru_san.rsfield.RSTextMaterial txtCodigo;
    // End of variables declaration//GEN-END:variables

    private void realizarVenta() {
        // el que parsea
        SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy");
        // el que formatea
        SimpleDateFormat formateador = new SimpleDateFormat("YYYY-MM-dd");

        Sentencias s = new Sentencias();

        int contarRegistros = 0;

        for (int i = 0; i < this.tabla.getRowCount(); i++) {

            try {
                s.setNumVenta(Integer.parseInt(this.lblNumVenta.getText()));
                s.setCodigoVenta(this.tabla.getValueAt(i, 1).toString());
                s.setCantidadVenta(Integer.parseInt(this.tabla.getValueAt(i, 2).toString()));
                s.setImporteVenta(Double.parseDouble(this.tabla.getValueAt(i, 6).toString()));

                java.util.Date fecha = parseador.parse(this.lblFecha.getFecha());
                s.setFechaVenta(String.valueOf(formateador.format(fecha)));
                s.setHoraVenta(this.lblHora.getHora());
                s.setCajero(principal.Principal.lblUsuario.getText());
                s.setCategoria(this.tabla.getValueAt(i, 7).toString());
                s.setPrecio(Double.parseDouble(this.tabla.getValueAt(i, 8).toString()));
                s.setPrecio_venta(Double.parseDouble(this.tabla.getValueAt(i, 5).toString()));
                s.setDescripcion(this.tabla.getValueAt(i, 3).toString());
                
                if(this.tabla.getValueAt(i, 9).toString().equals("true")){
                    s.setInventario_utiliza(true);
                }else{
                    s.setInventario_utiliza(false);
                }

                if (Operaciones.isRegistradoVenta(s)) {

                    ventas.pnlVentasToDay.txtFecha.setDatoFecha(new Date());
                    // el que formatea
                    SimpleDateFormat formateador1 = new SimpleDateFormat("YYYY-MM-dd");

                    Date date = ventas.pnlVentasToDay.txtFecha.getDatoFecha();

                    System.out.println(String.valueOf(formateador1.format(date)));
                    Operaciones.setListarVentas(String.valueOf(formateador1.format(date)));

                    contarRegistros++;

                    if (!this.tabla.getValueAt(i, 4).toString().equals("Ilimitadas")) {
                        int existencias = Integer.parseInt(this.tabla.getValueAt(i, 4).toString()) - Integer.parseInt(this.tabla.getValueAt(i, 2).toString());
                        s.setExistenciasAlmacen(existencias);
                        s.setCodigoCaja(this.tabla.getValueAt(i, 1).toString());

                        if (Operaciones.isEditadoExistencias(s)) {
                            almacen.Operaciones.setListar("");
                        }
                    }

                }
            } catch (ParseException ex) {
                Logger.getLogger(pnlVentas.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (contarRegistros == this.tabla.getRowCount()) {
            if (Operaciones.isVaciadoCaja()) {
                Operaciones.setListarCaja();
                Operaciones.totalCaja();
                this.lblNumVenta.setText(String.valueOf(Operaciones.extraerIDVenta()));

                SuccessAlert ss = new SuccessAlert(new JFrame(), true);
                ss.msj1.setText("Venta realizada con éxito");
                ss.msj2.setText("");
                ss.msj3.setText("");
                ss.setVisible(true);
            } else {
                ErrorAlert e = new ErrorAlert(new JFrame(), true);
                e.msj1.setText("Hubo un problema al intentar");
                e.msj2.setText("registrar la venta.");
                e.msj3.setText("");
                e.setVisible(true);
            }
        }
    }

    private void realizarVentaTicket() {
        // el que parsea
        SimpleDateFormat parseador = new SimpleDateFormat("dd/MM/yyyy");
        // el que formatea
        SimpleDateFormat formateador = new SimpleDateFormat("YYYY-MM-dd");

        Sentencias s = new Sentencias();

        int contarRegistros = 0;

        double total = 0;
        int cantidadProducto = 0;

        for (int i = 0; i < this.tabla.getRowCount(); i++) {

            try {

                total = total + Double.parseDouble(this.tabla.getValueAt(i, 6).toString());
                cantidadProducto = cantidadProducto + Integer.parseInt(this.tabla.getValueAt(i, 2).toString());

                s.setNumVenta(Integer.parseInt(this.lblNumVenta.getText()));
                s.setCodigoVenta(this.tabla.getValueAt(i, 1).toString());
                s.setCantidadVenta(Integer.parseInt(this.tabla.getValueAt(i, 2).toString()));
                s.setImporteVenta(Double.parseDouble(this.tabla.getValueAt(i, 6).toString()));

                java.util.Date fecha = parseador.parse(this.lblFecha.getFecha());
                s.setFechaVenta(String.valueOf(formateador.format(fecha)));
                s.setHoraVenta(this.lblHora.getHora());
                s.setCajero(principal.Principal.lblUsuario.getText());
                s.setCategoria(this.tabla.getValueAt(i, 7).toString());
                s.setPrecio(Double.parseDouble(this.tabla.getValueAt(i, 8).toString()));
                s.setPrecio_venta(Double.parseDouble(this.tabla.getValueAt(i, 5).toString()));
                s.setDescripcion(this.tabla.getValueAt(i, 3).toString());
                
                if(this.tabla.getValueAt(i, 9).toString().equals("true")){
                    s.setInventario_utiliza(true);
                }else{
                    s.setInventario_utiliza(false);
                }

                if (Operaciones.isRegistradoVenta(s)) {

                    ventas.pnlVentasToDay.txtFecha.setDatoFecha(new Date());
                    // el que formatea
                    SimpleDateFormat formateador1 = new SimpleDateFormat("YYYY-MM-dd");

                    Date date = ventas.pnlVentasToDay.txtFecha.getDatoFecha();

                    System.out.println(String.valueOf(formateador1.format(date)));
                    Operaciones.setListarVentas(String.valueOf(formateador1.format(date)));

                    contarRegistros++;

                    if (!this.tabla.getValueAt(i, 4).toString().equals("Ilimitadas")) {
                        int existencias = Integer.parseInt(this.tabla.getValueAt(i, 4).toString()) - Integer.parseInt(this.tabla.getValueAt(i, 2).toString());
                        s.setExistenciasAlmacen(existencias);
                        s.setCodigoCaja(this.tabla.getValueAt(i, 1).toString());

                        if (Operaciones.isEditadoExistencias(s)) {
                            almacen.Operaciones.setListar("");
                        }
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(pnlVentas.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        if (contarRegistros == this.tabla.getRowCount()) {
            if (Operaciones.isVaciadoCaja()) {
                generarTicket();
                Operaciones.setListarCaja();
                Operaciones.totalCaja();
                this.lblNumVenta.setText(String.valueOf(Operaciones.extraerIDVenta()));

                SuccessAlert ss = new SuccessAlert(new JFrame(), true);
                ss.msj1.setText("Venta realizada con éxito");
                ss.msj2.setText("");
                ss.msj3.setText("");
                ss.setVisible(true);
            } else {
                ErrorAlert e = new ErrorAlert(new JFrame(), true);
                e.msj1.setText("Hubo un problema al intentar");
                e.msj2.setText("registrar la venta.");
                e.msj3.setText("");
                e.setVisible(true);
            }
        }
    }

    public void generarTicket() {
        double total = 0;

        PrintTicket.AddCabecera(configuracion.Operaciones.getNegocio());
        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddCabecera(configuracion.Operaciones.getDireccion());
        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddCabecera("TELEFÓNO: " + configuracion.Operaciones.getTelefono());
        PrintTicket.AddCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddCabecera("RFC: " + configuracion.Operaciones.getRFC());
        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddSubCabecera("LE ATENDÍO: " + principal.Principal.lblUsuario.getText());
        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddSubCabecera("" + this.lblFecha.getFecha() + "        " + this.lblHora.getHora());
        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddSubCabecera("ART | CANT | PRECIO | IMPORTE");
        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
        PrintTicket.AddSubCabecera(PrintTicket.DibujarLinea(32));
        PrintTicket.AddSubCabecera(PrintTicket.DarEspacio());
        for (int i = 0; i < this.tabla.getRowCount(); i++) {
            total = total + Double.parseDouble(this.tabla.getValueAt(i, 6).toString());

            if (this.tabla.getValueAt(i, 3).toString().length() > 12) {
                PrintTicket.AddItem(
                        this.tabla.getValueAt(i, 3).toString().substring(0, 12) + "..",
                        this.tabla.getValueAt(i, 2).toString(),
                        "$" + this.tabla.getValueAt(i, 5).toString(),
                        "$" + this.tabla.getValueAt(i, 6).toString());
            } else {
                PrintTicket.AddItem(
                        this.tabla.getValueAt(i, 3).toString(),
                        this.tabla.getValueAt(i, 2).toString(),
                        "$" + this.tabla.getValueAt(i, 5).toString(),
                        "$" + this.tabla.getValueAt(i, 6).toString());
            }

            PrintTicket.AddItem("", "", "", PrintTicket.DarEspacio());
        }
        PrintTicket.AddItem("", "", "", PrintTicket.DarEspacio());
        PrintTicket.AddTotal(PrintTicket.DibujarLinea(31));
        PrintTicket.AddTotal(PrintTicket.DarEspacio());
        PrintTicket.AddTotal("TOTAL: $" + total);
        PrintTicket.AddTotal(PrintTicket.DarEspacio());
        PrintTicket.AddPieLinea(PrintTicket.DibujarLinea(32));
        PrintTicket.AddPieLinea(PrintTicket.DarEspacio());
        PrintTicket.AddPieLinea(configuracion.Operaciones.getGracias());
        PrintTicket.AddPieLinea(PrintTicket.DarEspacio());
        PrintTicket.AddPieLinea(configuracion.Operaciones.getWWW());
        PrintTicket.AddPieLinea(PrintTicket.DarEspacio());
        PrintTicket.ImprimirDocumento();
    }

    private void actualizarCantidad(int cantidad) {
        int fila = this.tabla.getSelectedRow();
        if (Operaciones.isRCaja(this.tabla.getValueAt(fila, 1).toString(), cantidad, true)) {
        }
    }

    private void setToolTip() {
        lblNumVenta.setToolTipText(RSTool.head + RSTool.body + "Número de venta" + RSTool.pie);
    }
}
