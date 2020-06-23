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

/**
 *
 * @author bento
 */
public class TelaUsuario extends javax.swing.JInternalFrame {

    Connection conexao;
    PreparedStatement pst;
    ResultSet rs;

    /**
     * Creates new form TelaUsuario
     */
    public TelaUsuario() {
        initComponents();
        lblErroLogin.setText("");
        conexao = ModuloConexao.conector();
        lblStatus.setText(conexao != null ? "DB conectado" : "DB desconectado");
    }
    
    private void limparCampos() {
        txtUserNome.setText(null);
        txtUserFone.setText(null);
        txtUserLogin.setText(null);
        txtUserSenha.setText(null);
        cbUserPerfil.setSelected(false);
    }
    private boolean hasIDInDB() {
        String sql = "select * from tbusuarios where iduser=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtUserID.getText());
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

    private void consultar() {
        String sql = "select * from tbusuarios where iduser=?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtUserID.getText());
            // Executa Query
            rs = pst.executeQuery();

            if (rs.next()) {
                txtUserNome.setText(rs.getString(2));
                txtUserFone.setText(rs.getString(3));
                txtUserLogin.setText(rs.getString(4));
                txtUserSenha.setText(rs.getString(5));
                cbUserPerfil.setSelected(rs.getBoolean(6));

            } else {
                lblErroLogin.setText("ID do usuário não encontrado ");
            }
            conexao.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void adicionar() {
        String sql = "insert into tbusuarios (iduser, usuario, fone, login, senha, perfil_adm) "
                + "values (?, ?, ?, ?, ?, ?);";
        if (txtUserLogin.getText().isEmpty()
                || txtUserSenha.getText().isEmpty()
                || txtUserNome.getText().isEmpty()) {
            lblErroLogin.setText("Preencha todos os campos obrigatórios");
        } else {
            if (!hasIDInDB()) {
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtUserID.getText());
                    pst.setString(2, txtUserNome.getText());
                    pst.setString(3, txtUserFone.getText());
                    pst.setString(4, txtUserLogin.getText());
                    pst.setString(5, txtUserSenha.getText());
                    pst.setString(6, (cbUserPerfil.getSelectedObjects() != null ? "1" : "0"));
                    // Executa Query
                    pst.executeUpdate();

                    if (hasIDInDB()) {
                        lblErroLogin.setText(txtUserLogin.getText() + " foi adicionado ao DB ");
                    } else {
                        lblErroLogin.setText("Não foi possivel add ao DB ");
                    }
                    conexao.close();
                } catch (SQLException e) {
                    if (e.toString().contains(txtUserLogin.getText())) {
                        lblErroLogin.setText("Usuário "+txtUserLogin.getText()+" já existe no DB");
                    } else {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            } else {
                lblErroLogin.setText("ID já existente no DB");
            }
        }
    }

    private void atualizar() {
        String sql = "update tbusuarios set usuario = ?, fone=?, login=?, senha=?, perfil_adm=? where iduser=?;";
        if (txtUserLogin.getText().isEmpty()
                || txtUserSenha.getText().isEmpty()
                || txtUserNome.getText().isEmpty()) {
            lblErroLogin.setText("Preencha todos os campos obrigatórios");
        } else {
            if (hasIDInDB()) {
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtUserNome.getText());
                    pst.setString(2, txtUserFone.getText());
                    pst.setString(3, txtUserLogin.getText());
                    pst.setString(4, txtUserSenha.getText());
                    pst.setString(5, (cbUserPerfil.getSelectedObjects() != null ? "1" : "0"));
                    pst.setString(6, txtUserID.getText());
                    // Executa Query
                    pst.executeUpdate();

                    if (hasIDInDB()) {
                        lblErroLogin.setText("Dados do "+txtUserLogin.getText() + " foram atualizados no DB ");
                    } else {
                        lblErroLogin.setText("Não foi possivel add ao DB ");
                    }
                    conexao.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } else {
                lblErroLogin.setText("ID não foi encontrado no DB");
            }
        }
    }

    private void deletar() {
        if (txtUserID.getText().isEmpty() || !hasIDInDB()) {
            lblErroLogin.setText("ID não foi encontrado no DB");
        } else {
            JLabel label = new JLabel("Digite a sua senha: ");
            JPasswordField jpf = new JPasswordField();
            JOptionPane.showConfirmDialog(null, new Object[]{label, jpf}, "Password Admin:", JOptionPane.OK_CANCEL_OPTION);
            String compSenha = new String(jpf.getPassword());

            if (!compSenha.equals(TelaLogin.getSenhaSudo())) {
                lblErroLogin.setText("Senha incorreta, digite senha do usuário logado");
            } else {
                String sql = "delete from tbusuarios where iduser=?;";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, txtUserID.getText());

                    // Executa Query
                    pst.executeUpdate();

                    if (!hasIDInDB()) {
                        lblErroLogin.setText("Usuário foi removido do DB ");
                        limparCampos();
                    } else {
                        lblErroLogin.setText("Não foi possivel remover usuário do DB ");
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

        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbUserPerfil = new javax.swing.JCheckBox();
        txtUserNome = new javax.swing.JTextField();
        txtUserLogin = new javax.swing.JTextField();
        txtUserSenha = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtUserID = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtUserFone = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btnUserCriar = new javax.swing.JButton();
        btnUserLer = new javax.swing.JButton();
        btnUserAtualizar = new javax.swing.JButton();
        btnUserDelete = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        lblErroLogin = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Usuários");
        setPreferredSize(new java.awt.Dimension(620, 449));

        jLabel2.setFont(new java.awt.Font("URW Gothic L", 2, 64)); // NOI18N
        jLabel2.setForeground(java.awt.SystemColor.control);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/infox/icones/Admin-icon.png"))); // NOI18N
        jLabel2.setText("usuários ");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setToolTipText("");
        jPanel1.setEnabled(false);
        jPanel1.setName("Cadastro"); // NOI18N

        cbUserPerfil.setText("Admin");

        txtUserNome.setToolTipText("Nome");

        jLabel1.setText("ID *");

        jLabel3.setText("Nome *");

        jLabel4.setText("Usuário *");

        jLabel5.setText("Senha *");

        txtUserID.setToolTipText("ID");

        jLabel6.setText("Perfil");

        jLabel7.setFont(new java.awt.Font("Sahadeva", 1, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Cadastro de Usuários");

        jLabel8.setText("Telefone");

        txtUserFone.setToolTipText("Telefone");

        jLabel9.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        jLabel9.setForeground(java.awt.Color.gray);
        jLabel9.setText("( * ) Campos Obrigatórios");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(43, 43, 43)
                                .addComponent(cbUserPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(56, 56, 56)
                                .addComponent(txtUserID, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(43, 92, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(txtUserFone, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUserNome)
                            .addComponent(txtUserLogin, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtUserSenha, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtUserID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtUserFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtUserNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtUserLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtUserSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbUserPerfil)
                            .addComponent(jLabel6)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel9)))
                .addGap(17, 17, 17))
        );

        btnUserCriar.setBackground(new java.awt.Color(3, 163, 96));
        btnUserCriar.setForeground(new java.awt.Color(254, 254, 254));
        btnUserCriar.setText("ADICIONAR");
        btnUserCriar.setToolTipText("");
        btnUserCriar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserCriarActionPerformed(evt);
            }
        });

        btnUserLer.setBackground(new java.awt.Color(19, 92, 166));
        btnUserLer.setForeground(new java.awt.Color(254, 254, 254));
        btnUserLer.setText("CONSULTAR");
        btnUserLer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserLerActionPerformed(evt);
            }
        });

        btnUserAtualizar.setBackground(new java.awt.Color(222, 191, 58));
        btnUserAtualizar.setForeground(new java.awt.Color(254, 254, 254));
        btnUserAtualizar.setText("ATUALIZAR");
        btnUserAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserAtualizarActionPerformed(evt);
            }
        });

        btnUserDelete.setBackground(new java.awt.Color(191, 16, 32));
        btnUserDelete.setForeground(new java.awt.Color(254, 254, 254));
        btnUserDelete.setText("DELETAR");
        btnUserDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserDeleteActionPerformed(evt);
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

        lblStatus.setForeground(java.awt.Color.gray);
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblErroLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(btnUserCriar, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUserLer, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUserAtualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUserDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLimpar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblErroLogin)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUserCriar)
                    .addComponent(btnUserLer)
                    .addComponent(btnUserAtualizar)
                    .addComponent(btnUserDelete)
                    .addComponent(btnLimpar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(55, 55, 55))
        );

        jPanel1.getAccessibleContext().setAccessibleName("Cadastro Usuário");

        setBounds(0, 0, 620, 449);
    }// </editor-fold>//GEN-END:initComponents

    private void btnUserLerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserLerActionPerformed
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
        } else {
            lblStatus.setText("DB desconectado");
            lblErroLogin.setText("Não foi possivel se conectar ao DB");
        }
    }//GEN-LAST:event_btnUserLerActionPerformed

    private void btnUserCriarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserCriarActionPerformed
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
    }//GEN-LAST:event_btnUserCriarActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        // TODO add your handling code here:
        limparCampos();
        lblErroLogin.setText("");
    }//GEN-LAST:event_btnLimparActionPerformed

    private void btnUserAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserAtualizarActionPerformed
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
    }//GEN-LAST:event_btnUserAtualizarActionPerformed

    private void btnUserDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserDeleteActionPerformed
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
    }//GEN-LAST:event_btnUserDeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnUserAtualizar;
    private javax.swing.JButton btnUserCriar;
    private javax.swing.JButton btnUserDelete;
    private javax.swing.JButton btnUserLer;
    private javax.swing.JCheckBox cbUserPerfil;
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
    private javax.swing.JLabel lblErroLogin;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextField txtUserFone;
    private javax.swing.JTextField txtUserID;
    private javax.swing.JTextField txtUserLogin;
    private javax.swing.JTextField txtUserNome;
    private javax.swing.JTextField txtUserSenha;
    // End of variables declaration//GEN-END:variables
}
