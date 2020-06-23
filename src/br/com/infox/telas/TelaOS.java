/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.infox.telas;

import java.sql.*;
import br.com.infox.dal.ModuloConexao;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author bento
 */
public class TelaOS extends javax.swing.JInternalFrame {

    Connection conexao;
    PreparedStatement pst;
    ResultSet rs;
    private String tipo;

    /**
     * Creates new form TelaOS
     */
    public TelaOS() {
        initComponents();
        lblErroLogin.setText("");
        conexao = ModuloConexao.conector();
        lblStatus.setText(conexao != null ? "DB conectado" : "DB desconectado");
        tipo = "Orçamento"; // por padrão se inicia em orçamento
    }

    private void limparCampos() {
        txtClienteID.setText(null);
        txtClientePesquisar.setText(null);
        txtData.setText(null);
        txtOS.setText(null);
        txtOSDef.setText(null);
        txtOSEquip.setText(null);
        txtOSServ.setText(null);
        txtOSTec.setText(null);
        txtOSValor.setText(null);
    }

    private boolean hasIDInDB() {
        String sql = "select * from tbclientes where idcli=?";
        if (txtClienteID.getText().isEmpty()) {
            return false;
        }
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtClienteID.getText());
            // Executa Query
            rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);

        }
        return false;
    }

    private boolean hasOSIDInDB() {
        String sql = "select * from tbos where os=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtOS.getText());
            // Executa Query
            rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);

        }
        return false;
    }

    private void adicionar() {

        String sql = "insert into tbos (tipo, situacao, equipamento, defeito, servico, tecnico, valor, idcli) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?) ;";
        if (txtClienteID.getText().isEmpty()
                || txtOSEquip.getText().isEmpty()
                || txtOSDef.getText().isEmpty()) {
            lblErroLogin.setText("Preencha todos os campos obrigatórios");
        } else {
            if (hasIDInDB() && !hasOSIDInDB()) {
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, tipo);
                    pst.setString(2, cboOSSit.getSelectedItem().toString());
                    pst.setString(3, txtOSEquip.getText());
                    pst.setString(4, txtOSDef.getText());
                    pst.setString(5, txtOSServ.getText());
                    pst.setString(6, txtOSTec.getText());
                    pst.setString(7, txtOSValor.getText().replace(",", "."));
                    pst.setString(8, txtClienteID.getText());
                    // Executa Query
                    if (pst.executeUpdate() > 0) {
                        lblErroLogin.setText("OS foi adicionado ao DB ");
                        limparCampos();
                    } else {
                        lblErroLogin.setText("Não foi possivel add ao DB ");
                    }
                    conexao.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } else {
                if (hasOSIDInDB()) {
                    lblErroLogin.setText("ID OS já existente no DB");
                } else {
                    lblErroLogin.setText("Cliente não existente no DB");
                }
            }
        }
    }

    private void pesquisarClienteDB() {
        String sql = "select * from tbclientes where nomecli like ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtClientePesquisar.getText() + "%");
            rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
            model.setNumRows(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    //retorna os dados da tabela do BD, cada campo e um coluna.
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(4)
                });

            }
            //tblClientes.add;
            //tblClientes.setModel(DbUtils.resultSetToTableModel(rs));
            conexao.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void setarCampos() {
        String sql = "select * from tbclientes where idcli=?";
        int linha = tblClientes.getSelectedRow();
        txtClienteID.setText(tblClientes.getValueAt(linha, 0).toString());
    }

    private void consultar() {
        String num_os = JOptionPane.showInputDialog("Número da OS");
        String sql = "select * from tbos where os=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, num_os);
            // Executa Query
            rs = pst.executeQuery();

            if (rs.next()) {
                txtOS.setText(rs.getString(1));
                txtData.setText(rs.getString(2));
                if (rs.getString(3).equals("Orçamento")) {
                    rbtOrc.setSelected(true);
                    tipo = "Orçamento";
                } else {
                    rbtOS.setSelected(true);
                    tipo = "Ordem de Serviço";
                }
                cboOSSit.setSelectedItem(rs.getObject(4));
                txtOSEquip.setText(rs.getString(5));
                txtOSDef.setText(rs.getString(6));
                txtOSServ.setText(rs.getString(7));
                txtOSTec.setText(rs.getString(8));
                txtOSValor.setText(rs.getString(9));
                txtClienteID.setText(rs.getString(10));

            } else {
                lblErroLogin.setText("ID do usuário não encontrado ");
            }
            conexao.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void atualizar() {
        String sql = "update tbos set tipo=?, situacao=?, equipamento=?, defeito=?, servico=?, tecnico=?, valor=?, idcli=? where os=?;";

        if (txtClienteID.getText().isEmpty()
                || txtOSEquip.getText().isEmpty()
                || txtOSDef.getText().isEmpty()) {
            lblErroLogin.setText("Preencha todos os campos obrigatórios");
        } else {
            if (hasOSIDInDB()) {
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, tipo);
                    pst.setString(2, cboOSSit.getSelectedItem().toString());
                    pst.setString(3, txtOSEquip.getText());
                    pst.setString(4, txtOSDef.getText());
                    pst.setString(5, txtOSServ.getText());
                    pst.setString(6, txtOSTec.getText());
                    pst.setString(7, txtOSValor.getText());
                    pst.setString(8, txtClienteID.getText());
                    pst.setString(9, txtOS.getText());

                    // Executa Query
                    pst.executeUpdate();

                    if (hasOSIDInDB()) {
                        lblErroLogin.setText("Dados foram atualizados no DB ");
                    } else {
                        lblErroLogin.setText("Não foi possivel add ao DB ");
                    }
                    conexao.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } else {
                lblErroLogin.setText("ID OS não foi encontrado no DB");
            }
        }
    }

    private void deletar() {
        if (!hasIDInDB() || !hasOSIDInDB()) {
            lblErroLogin.setText("ID não foi encontrado no DB");
        } else {
            JLabel label = new JLabel("Digite a sua senha: ");
            JPasswordField jpf = new JPasswordField();
            JOptionPane.showConfirmDialog(null, new Object[]{label, jpf}, "Password Admin:", JOptionPane.OK_CANCEL_OPTION);
            String compSenha = new String(jpf.getPassword());

            if (!compSenha.equals(TelaLogin.getSenhaSudo())) {
                lblErroLogin.setText("Senha incorreta, digite senha do usuário logado");
            } else {
                String sql = "delete from tbos where os = ?;";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtClienteID.getText());

                    // Executa Query
                    pst.executeUpdate();

                    if (!hasIDInDB()) {
                        lblErroLogin.setText("OS foi removido do DB ");
                        limparCampos();
                    } else {
                        lblErroLogin.setText("Não foi possivel remover OS do DB ");
                    }
                    conexao.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtOS = new javax.swing.JTextField();
        txtData = new javax.swing.JTextField();
        rbtOrc = new javax.swing.JRadioButton();
        rbtOS = new javax.swing.JRadioButton();
        printOS = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtClientePesquisar = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        txtClienteID = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboOSSit = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtOSEquip = new javax.swing.JTextField();
        txtOSDef = new javax.swing.JTextField();
        txtOSServ = new javax.swing.JTextField();
        txtOSTec = new javax.swing.JTextField();
        txtOSValor = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btnOSAdicionar = new javax.swing.JButton();
        btnOSLer = new javax.swing.JButton();
        btnOSAtualizar = new javax.swing.JButton();
        btnOSDelete = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblErroLogin = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastro de OS");
        setPreferredSize(new java.awt.Dimension(626, 449));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Dados Ordem de Serviço", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel1.setText("Nº OS");

        jLabel2.setText("Data");

        txtOS.setEditable(false);
        txtOS.setEnabled(false);

        txtData.setEditable(false);
        txtData.setFont(new java.awt.Font("Ubuntu", 0, 13)); // NOI18N
        txtData.setEnabled(false);

        buttonGroup1.add(rbtOrc);
        rbtOrc.setSelected(true);
        rbtOrc.setText("Orçamento");
        rbtOrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOrcActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtOS);
        rbtOS.setText("Ordem de Serviço");
        rbtOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOSActionPerformed(evt);
            }
        });

        printOS.setText("print");
        printOS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printOSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtData)
                        .addContainerGap())))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtOS)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(rbtOrc)
                        .addGap(26, 26, 26)
                        .addComponent(printOS)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtOS, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtOrc)
                    .addComponent(printOS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtOS))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Buscar Clientes"));

        txtClientePesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtClientePesquisarKeyReleased(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/search-icon.png"))); // NOI18N
        jLabel3.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/search-icon.png"))); // NOI18N

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome", "Fone"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);
        if (tblClientes.getColumnModel().getColumnCount() > 0) {
            tblClientes.getColumnModel().getColumn(0).setMinWidth(50);
            tblClientes.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblClientes.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        jLabel10.setText("ID *");

        txtClienteID.setEditable(false);
        txtClienteID.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtClientePesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtClienteID, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtClienteID, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtClientePesquisar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

        jLabel4.setText("Situação *");

        cboOSSit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Equipamento *");

        jLabel6.setText("Defeito *");

        jLabel7.setText("Serviço");

        jLabel8.setText("Técnico");

        txtOSValor.setText("0");

        jLabel9.setText("Valor Total");

        btnOSAdicionar.setBackground(new java.awt.Color(3, 163, 96));
        btnOSAdicionar.setForeground(new java.awt.Color(254, 254, 254));
        btnOSAdicionar.setText("EMITIR");
        btnOSAdicionar.setToolTipText("");
        btnOSAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSAdicionarActionPerformed(evt);
            }
        });

        btnOSLer.setBackground(new java.awt.Color(19, 92, 166));
        btnOSLer.setForeground(new java.awt.Color(254, 254, 254));
        btnOSLer.setText("CONSULTAR");
        btnOSLer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSLerActionPerformed(evt);
            }
        });

        btnOSAtualizar.setBackground(new java.awt.Color(222, 191, 58));
        btnOSAtualizar.setForeground(new java.awt.Color(254, 254, 254));
        btnOSAtualizar.setText("ATUALIZAR");
        btnOSAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSAtualizarActionPerformed(evt);
            }
        });

        btnOSDelete.setBackground(new java.awt.Color(191, 16, 32));
        btnOSDelete.setForeground(new java.awt.Color(254, 254, 254));
        btnOSDelete.setText("DELETAR");
        btnOSDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOSDeleteActionPerformed(evt);
            }
        });

        btnLimpar.setBackground(new java.awt.Color(61, 61, 61));
        btnLimpar.setForeground(new java.awt.Color(254, 254, 254));
        btnLimpar.setText("LIMPAR");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        jLabel11.setForeground(java.awt.Color.gray);
        jLabel11.setText("( * ) Campos Obrigatórios");

        lblStatus.setForeground(java.awt.Color.gray);
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblStatus.setText("Conectado");

        lblErroLogin.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        lblErroLogin.setForeground(new java.awt.Color(172, 2, 2));
        lblErroLogin.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblErroLogin.setText("usuário e/ou senha não encontrados ");
        lblErroLogin.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboOSSit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel11)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6)
                                .addComponent(jLabel7)
                                .addComponent(jLabel8))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtOSTec, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel9)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtOSValor))
                                .addComponent(txtOSServ, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtOSDef, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtOSEquip, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(lblErroLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(btnOSAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnOSLer, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnOSAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnOSDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(189, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(cboOSSit, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtOSEquip, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtOSDef, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtOSServ, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtOSTec, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOSValor, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblErroLogin)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOSAdicionar)
                    .addComponent(btnOSLer)
                    .addComponent(btnOSAtualizar)
                    .addComponent(btnOSDelete)
                    .addComponent(btnLimpar))
                .addContainerGap())
        );

        setBounds(0, 0, 806, 470);
    }// </editor-fold>//GEN-END:initComponents

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // TODO add your handling code here:
        lblErroLogin.setText("");
        try {
            if (conexao.isClosed() == true) {
                conexao = ModuloConexao.conector();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        if (conexao != null) {
            lblStatus.setText("DB conectado");
            //tblClientes.setVisible(true);
            setarCampos();
        } else {
            lblStatus.setText("DB desconectado");
            lblErroLogin.setText("Não foi possivel se conectar ao DB");
        }
    }//GEN-LAST:event_tblClientesMouseClicked

    private void txtClientePesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtClientePesquisarKeyReleased
        // TODO add your handling code here:
        lblErroLogin.setText("");
        if (!txtClientePesquisar.getText().isEmpty()) {
            try {
                if (conexao.isClosed() == true) {
                    conexao = ModuloConexao.conector();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
            if (conexao != null) {
                lblStatus.setText("DB conectado");
                //tblClientes.setVisible(true);
                pesquisarClienteDB();
            } else {
                lblStatus.setText("DB desconectado");
                lblErroLogin.setText("Não foi possivel se conectar ao DB");
            }
        } else {
            DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
            model.setNumRows(0);
        }
    }//GEN-LAST:event_txtClientePesquisarKeyReleased

    private void btnOSAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSAdicionarActionPerformed
        // TODO add your handling code here:
        lblErroLogin.setText("");
        try {
            if (conexao.isClosed() == true) {
                conexao = ModuloConexao.conector();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        if (conexao != null) {
            lblStatus.setText("DB conectado");
            adicionar();
        } else {
            lblStatus.setText("DB desconectado");
            lblErroLogin.setText("Não foi possivel se conectar ao DB");
        }
    }//GEN-LAST:event_btnOSAdicionarActionPerformed

    private void btnOSLerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSLerActionPerformed
        lblErroLogin.setText("");
        try {
            if (conexao.isClosed() == true) {
                conexao = ModuloConexao.conector();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        if (conexao != null) {
            lblStatus.setText("DB conectado");
            consultar();
            txtClientePesquisar.setEnabled(false);
        } else {
            lblStatus.setText("DB desconectado");
            lblErroLogin.setText("Não foi possivel se conectar ao DB");
        }
    }//GEN-LAST:event_btnOSLerActionPerformed

    private void btnOSAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSAtualizarActionPerformed
        // TODO add your handling code here:
        lblErroLogin.setText("");
        try {
            if (conexao.isClosed() == true) {
                conexao = ModuloConexao.conector();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        if (conexao != null) {
            lblStatus.setText("DB conectado");
            atualizar();
        } else {
            lblStatus.setText("DB desconectado");
            lblErroLogin.setText("Não foi possivel se conectar ao DB");
        }
    }//GEN-LAST:event_btnOSAtualizarActionPerformed

    private void btnOSDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOSDeleteActionPerformed
        // TODO add your handling code here:
        lblErroLogin.setText("");
        try {
            if (conexao.isClosed() == true) {
                conexao = ModuloConexao.conector();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        if (conexao != null) {
            lblStatus.setText("DB conectado");
            deletar();
        } else {
            lblStatus.setText("DB desconectado");
            lblErroLogin.setText("Não foi possivel se conectar ao DB");
        }
    }//GEN-LAST:event_btnOSDeleteActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        // TODO add your handling code here:
        limparCampos();
        lblErroLogin.setText("");
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        model.setNumRows(0);
        txtClientePesquisar.setEnabled(true);
    }//GEN-LAST:event_btnLimparActionPerformed

    private void rbtOrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOrcActionPerformed
        // TODO add your handling code here:
        tipo = "Orçamento";
    }//GEN-LAST:event_rbtOrcActionPerformed

    private void rbtOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOSActionPerformed
        // TODO add your handling code here:
        tipo = "Ordem de Serviço";
    }//GEN-LAST:event_rbtOSActionPerformed

    private void printOSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printOSActionPerformed
        lblErroLogin.setText("");

        try {
            if (conexao.isClosed() == true) {
                conexao = ModuloConexao.conector();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        if (conexao != null) {
            lblStatus.setText("DB conectado");
            if (hasOSIDInDB()) {
                int confirma = JOptionPane.showConfirmDialog(null, "Confirma a impresão da OS?", "Atenção", JOptionPane.YES_NO_OPTION);
                if (confirma == JOptionPane.YES_OPTION) {
                    try {
                        HashMap filtro = new HashMap();
                        filtro.put("os", Integer.parseInt(txtOS.getText()));
                        JasperPrint print = JasperFillManager.fillReport("MyReports/PrintOS.jasper", filtro, conexao);
                        JasperViewer.viewReport(print, false);
                    } catch (JRException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }

                }
            } else {
                lblErroLogin.setText("OS não encontrada no DB");
            }
        } else {
            lblStatus.setText("DB desconectado");
            lblErroLogin.setText("Não foi possivel se conectar ao DB");
        }
    }//GEN-LAST:event_printOSActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnOSAdicionar;
    private javax.swing.JButton btnOSAtualizar;
    private javax.swing.JButton btnOSDelete;
    private javax.swing.JButton btnOSLer;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboOSSit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblErroLogin;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JButton printOS;
    private javax.swing.JRadioButton rbtOS;
    private javax.swing.JRadioButton rbtOrc;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtClienteID;
    private javax.swing.JTextField txtClientePesquisar;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtOS;
    private javax.swing.JTextField txtOSDef;
    private javax.swing.JTextField txtOSEquip;
    private javax.swing.JTextField txtOSServ;
    private javax.swing.JTextField txtOSTec;
    private javax.swing.JTextField txtOSValor;
    // End of variables declaration//GEN-END:variables
}
