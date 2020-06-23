/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.infox.telas;

import java.sql.*;
import br.com.infox.dal.ModuloConexao;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author bento
 */
public class TelaCliente extends javax.swing.JInternalFrame {

    Connection conexao;
    PreparedStatement pst;
    ResultSet rs;

    /**
     * Creates new form TelaCliente
     */
    public TelaCliente() {
        initComponents();
        lblErroLogin.setText("");
        conexao = ModuloConexao.conector();
        lblStatus.setText(conexao != null ? "DB conectado" : "DB desconectado");
    }
    
    private void limparCampos() {
        txtClienteID.setText(null);
        txtClienteEmail.setText(null);
        txtClienteEnd.setText(null);
        txtClienteFone.setText(null);
        txtClienteNome.setText(null);
        txtClientePesquisar.setText(null);
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

    private boolean hasClienteInDB() {
        String sql = "select * from tbclientes where nomecli=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtClienteNome.getText());
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
        String sql = "insert into tbclientes (nomecli, endcli, fonecli, emailcli) "
                + "values (?, ?, ?, ?);";
        if (txtClienteNome.getText().isEmpty() ||
            txtClienteFone.getText().isEmpty()) {
            lblErroLogin.setText("Preencha todos os campos obrigatórios");
        } else {
            if (!hasClienteInDB()) {
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtClienteNome.getText());
                    pst.setString(2, txtClienteEnd.getText());
                    pst.setString(3, txtClienteFone.getText());
                    pst.setString(4, txtClienteEmail.getText());
                    // Executa Query
                    if (pst.executeUpdate() > 0) {
                        lblErroLogin.setText(txtClienteNome.getText() + " foi adicionado ao DB ");
                    } else {
                        lblErroLogin.setText("Não foi possivel add ao DB ");
                    }
                    conexao.close();
                } catch (SQLException e) {
                    if (e.toString().contains(txtClienteNome.getText())) {
                        lblErroLogin.setText("Cliente " + txtClienteNome.getText() + " já existe no DB");
                    } else {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            } else {
                lblErroLogin.setText("Cliente já existente no DB");
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
                    rs.getString(2)
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
        txtClienteNome.setText(tblClientes.getValueAt(linha, 1).toString());
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tblClientes.getValueAt(linha, 0).toString());
            // Executa Query
            rs = pst.executeQuery();

            if (rs.next()) {
                txtClienteID.setText(rs.getString(1));
                txtClienteEnd.setText(rs.getString(3));
                txtClienteFone.setText(rs.getString(4));
                txtClienteEmail.setText(rs.getString(5));
            }
            conexao.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }
    
    private void atualizar() {
        String sql = "update tbclientes set nomecli = ?, endcli=?, fonecli=?, emailcli=? where idcli = ?;";
        if (txtClienteNome.getText().isEmpty() || 
            txtClienteFone.getText().isEmpty() || 
            txtClienteID.getText().isEmpty()) 
        {
            lblErroLogin.setText("Preencha todos os campos obrigatórios");
        } else {
            if (hasIDInDB()) {
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtClienteNome.getText());
                    pst.setString(2, txtClienteEnd.getText());
                    pst.setString(3, txtClienteFone.getText());
                    pst.setString(4, txtClienteEmail.getText());
                    pst.setString(5, txtClienteID.getText());
                    
                    // Executa Query
                    pst.executeUpdate();

                    if (hasClienteInDB()) {
                        lblErroLogin.setText("Dados do "+txtClienteNome.getText() + " foram atualizados no DB ");
                    } else {
                        lblErroLogin.setText("Não foi possivel add ao DB ");
                    }
                    conexao.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } else {
                lblErroLogin.setText("Cliente não foi encontrado no DB");
            }
        }
    }

    private void deletar() {
        if (!hasIDInDB()) {
            lblErroLogin.setText("ID não foi encontrado no DB");
        } else {
            JLabel label = new JLabel("Digite a sua senha: ");
            JPasswordField jpf = new JPasswordField();
            JOptionPane.showConfirmDialog(null, new Object[]{label, jpf}, "Password Admin:", JOptionPane.OK_CANCEL_OPTION);
            String compSenha = new String(jpf.getPassword());

            if (!compSenha.equals(TelaLogin.getSenhaSudo())) {
                lblErroLogin.setText("Senha incorreta, digite senha do usuário logado");
            } else {
                String sql = "delete from tbclientes where idcli = ?;";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtClienteID.getText());

                    // Executa Query
                    pst.executeUpdate();

                    if (!hasIDInDB()) {
                        lblErroLogin.setText("Cliente foi removido do DB ");
                        limparCampos();
                    } else {
                        lblErroLogin.setText("Não foi possivel remover cliente do DB ");
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

        jPanel1 = new javax.swing.JPanel();
        txtClienteNome = new javax.swing.JTextField();
        txtClienteEnd = new javax.swing.JTextField();
        txtClienteEmail = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtClienteFone = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtClienteID = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnClienteCriar = new javax.swing.JButton();
        btnClienteAtualizar = new javax.swing.JButton();
        btnClienteDelete = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        lblErroLogin = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtClientePesquisar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setPreferredSize(new java.awt.Dimension(620, 449));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setToolTipText("");
        jPanel1.setEnabled(false);
        jPanel1.setName("Cadastro"); // NOI18N

        txtClienteNome.setToolTipText("Nome");

        jLabel3.setText("Nome *");

        jLabel4.setText("Endereço");

        jLabel5.setText("Email");

        jLabel7.setFont(new java.awt.Font("Sahadeva", 1, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Cadastro de Clientes");

        jLabel8.setText("Telefone *");

        txtClienteFone.setToolTipText("Telefone");

        jLabel9.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        jLabel9.setForeground(java.awt.Color.gray);
        jLabel9.setText("( * ) Campos Obrigatórios");

        txtClienteID.setEditable(false);

        jLabel6.setText("ID (increm)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(txtClienteFone, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(txtClienteID, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9))
                            .addComponent(txtClienteNome, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                            .addComponent(txtClienteEnd, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtClienteEmail, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(3, 3, 3))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClienteNome, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClienteEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtClienteEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtClienteFone, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtClienteID, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)
                        .addComponent(jLabel6)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        btnClienteCriar.setBackground(new java.awt.Color(3, 163, 96));
        btnClienteCriar.setForeground(new java.awt.Color(254, 254, 254));
        btnClienteCriar.setText("ADICIONAR");
        btnClienteCriar.setToolTipText("");
        btnClienteCriar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteCriarActionPerformed(evt);
            }
        });

        btnClienteAtualizar.setBackground(new java.awt.Color(222, 191, 58));
        btnClienteAtualizar.setForeground(new java.awt.Color(254, 254, 254));
        btnClienteAtualizar.setText("ATUALIZAR");
        btnClienteAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteAtualizarActionPerformed(evt);
            }
        });

        btnClienteDelete.setBackground(new java.awt.Color(191, 16, 32));
        btnClienteDelete.setForeground(new java.awt.Color(254, 254, 254));
        btnClienteDelete.setText("DELETAR");
        btnClienteDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteDeleteActionPerformed(evt);
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

        lblErroLogin.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        lblErroLogin.setForeground(new java.awt.Color(172, 2, 2));
        lblErroLogin.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblErroLogin.setText("usuário e/ou senha não encontrados ");
        lblErroLogin.setToolTipText("");

        lblStatus.setForeground(java.awt.Color.gray);
        lblStatus.setText("Conectado");

        jLabel2.setFont(new java.awt.Font("URW Gothic L", 2, 48)); // NOI18N
        jLabel2.setForeground(java.awt.SystemColor.control);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/Clients-icon.png"))); // NOI18N
        jLabel2.setText("clientes");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        txtClientePesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtClientePesquisarKeyReleased(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/search-icon.png"))); // NOI18N
        jLabel1.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/search-icon.png"))); // NOI18N

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nome"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblErroLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStatus))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtClientePesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnClienteCriar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnClienteAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnClienteDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLimpar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblErroLogin)
                    .addComponent(lblStatus))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClienteCriar)
                    .addComponent(btnClienteAtualizar)
                    .addComponent(btnClienteDelete)
                    .addComponent(btnLimpar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(txtClientePesquisar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2))))
        );

        setBounds(0, 0, 626, 449);
    }// </editor-fold>//GEN-END:initComponents

    private void btnClienteCriarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteCriarActionPerformed
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

    }//GEN-LAST:event_btnClienteCriarActionPerformed

    private void btnClienteAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteAtualizarActionPerformed
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
    }//GEN-LAST:event_btnClienteAtualizarActionPerformed

    private void btnClienteDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteDeleteActionPerformed
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
    }//GEN-LAST:event_btnClienteDeleteActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        // TODO add your handling code here:
        lblErroLogin.setText("");
        limparCampos();
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        model.setNumRows(0);
    }//GEN-LAST:event_btnLimparActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClienteAtualizar;
    private javax.swing.JButton btnClienteCriar;
    private javax.swing.JButton btnClienteDelete;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblErroLogin;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtClienteEmail;
    private javax.swing.JTextField txtClienteEnd;
    private javax.swing.JTextField txtClienteFone;
    private javax.swing.JTextField txtClienteID;
    private javax.swing.JTextField txtClienteNome;
    private javax.swing.JTextField txtClientePesquisar;
    // End of variables declaration//GEN-END:variables
}
