import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.swing.*;

import quick.dbtable.DBTable;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;

public class UnidadParquimetro extends javax.swing.JInternalFrame {
	//Declaramos las variables
	private DBTable tabla;
	private Connection conexionBD;
	private String[] patentes;
	private int id_asoc;
	private String ubicacion;
	
	public UnidadParquimetro(String [] patentes,int id_asoc, String ubicacion) {
		super();
		this.patentes=patentes;
		this.id_asoc=id_asoc;
		this.ubicacion=ubicacion;
		initGUI();
		
	}
	private void initGUI() {
		//Declaramos las variables
		
		setPreferredSize(new Dimension(900, 700));
		this.setResizable(true);
		this.setBounds(0, 0, 900, 700);
		this.setTitle("Consultas Administrador");
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setResizable(true);
		this.setClosable(true);
		this.setVisible(true);
		this.setMaximizable(true);
		//this.setBackground(new Color(187, 222, 251));
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);		
		
		tabla = new DBTable();
		tabla.setBounds(0,0, 900, 700);
		panel.add(tabla);
		tabla.setEditable(false);
	//n´umero de multa, fecha, hora, calle, altura, patente del auto y legajo del inspector.
	//NATURAL JOIN de asociado_con y multa	
		generarMultas();
		mostrarMultas();
	}
	
	private void generarMultas() {
		//Primero buscamos la ubicacion del parquimetro
		String [] datos= ubicacion.split(" ",2);
		String sql= "SELECT patente FROM estacionados WHERE "+ datos[0] +"=calle and "+datos[1] +"=altura;";
		String multa;
		try {
			Statement stmt= this.conexionBD.createStatement();
			ResultSet rs= stmt.executeQuery(sql);
			LocalDate miFecha = LocalDate.now();
			LocalDateTime miHora = LocalDateTime.now();
			while(rs.next()) {
				if(!(Arrays.asList(patentes).contains(rs.getString(1)))) {
					//values: numero, fecha, hora, patente, id_asociado_con 
					multa="INSERT INTO multas (fecha, hora, patente, id_asociado_con) VALUES (" + miFecha +"," + miHora+ "," + rs.getString(1) +","+ id_asoc+");";
					stmt.execute(multa);
				}
			}
		rs.close();
		stmt.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}	
	}
	
	//n´umero de multa, fecha, hora, calle, altura, patente del auto y legajo del inspector.
	//NATURAL JOIN de asociado_con y multa	
	private void mostrarMultas() {
		String sql="SELECT numero, fecha. hora, calle, altura, patente, legajo FROM multa NATURAL JOIN asociado_con;";
		try {
			Statement stmt= this.conexionBD.createStatement();
			ResultSet rs= stmt.executeQuery(sql);
			tabla.refresh(rs);
			rs.close();
			stmt.close();
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}	
}
