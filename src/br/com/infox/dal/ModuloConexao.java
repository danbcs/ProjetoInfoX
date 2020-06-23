/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.infox.dal;

import java.sql.*;

/**
 *
 * @author bento
 */
public class ModuloConexao {
    //conexão com banco de dados
    public static Connection conector() {
        java.sql.Connection conexao;
        
//driver mysql
        //String driver = "com.mysql.jdbc.Driver";
        
        //info DB
        String url = "jdbc:mysql://localhost:3306/dbinfox";
        String user = "dan";
        String password = "123";
        
        //Estabelecer conexão com Banco
        try {
            //Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            return null;
        }
    }
}
