/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.infox.dal;

import java.sql.Connection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bento
 */
public class ModuloConexaoTest {
    
    public ModuloConexaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of conector method, of class ModuloConexao.
     */
    @Test
    public void testConector() {
        System.out.println("Test conector");
        Connection expResult = null;
        Connection result = ModuloConexao.conector();
        if(result.equals(expResult)) {
            fail("Falha ao tentar se conectar ao DB");
        } else {
            System.out.println("Conexão OK!");
        }   
    }
}
