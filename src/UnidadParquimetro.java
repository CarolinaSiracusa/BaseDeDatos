import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
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
	private Date miFecha;
	
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
		this.setTitle("Unidad Parquimetros");
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setResizable(true);
		this.setVisible(true);
		//this.setBackground(new Color(187, 222, 251));
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);		
		
		tabla = new DBTable();
		tabla.setBounds(0,0, 884, 670);
		panel.add(tabla);
		tabla.setEditable(false);
		tabla.setVisible(true);
		//n´umero de multa, fecha, hora, calle, altura, patente del auto y legajo del inspector.
		//NATURAL JOIN de asociado_con y multa	
		conectarBD();
		generarMultas();
		mostrarMultas();
		
		repaint();
	}
	
	private void generarMultas() {
		//Primero buscamos la ubicacion del parquimetro
		String [] datos= ubicacion.split(" ",2);
		String sql= "SELECT patente FROM estacionados WHERE calle="+ "'"+datos[0]+"'"+" and altura="+"'"+datos[1]+"'"+";";
		String multa;
		try {
			Statement stmt= this.conexionBD.createStatement();
			Statement stmt2= this.conexionBD.createStatement();
			ResultSet rs= stmt.executeQuery(sql);
			miFecha= new Date();
			java.sql.Date fecha= java.sql.Date.valueOf((new SimpleDateFormat("yyyy-MM-dd")).format(miFecha));
			java.sql.Time hora= java.sql.Time.valueOf((new SimpleDateFormat("HH:mm:ss")).format(miFecha));
			int i;
			boolean corte=false;
			while(rs.next()) {
				for(i=0;i<patentes.length && !corte;i++) {
						if(patentes[i].equals(rs.getString(1))) {
							patentes[i]="";
							corte=true;
					}
				}
			}
			//values: numero, fecha, hora, patente, id_asociado_con
			for(String p:patentes) {
				if(!(p.equals(""))) {
					multa="INSERT INTO multa (fecha, hora, patente, id_asociado_con) VALUES (" + "'"+fecha+"'" +"," + "'"+hora+"'"+ "," + "'"+ p +"'" +","+"'"+ id_asoc+"'"+");";
					stmt2.execute(multa);
				}
			}
		rs.close();
		stmt.close();
		stmt2.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			System.out.println("GENERAR");
		}	
	}
	
	//n´umero de multa, fecha, hora, calle, altura, patente del auto y legajo del inspector.
	//NATURAL JOIN de asociado_con y multa	
	private void mostrarMultas() {
		
		String sql="SELECT DISTINCT numero, fecha, hora, calle, altura, patente, legajo FROM multa NATURAL JOIN asociado_con;";		
		try {
			Statement stmt= this.conexionBD.createStatement();
			ResultSet rs= stmt.executeQuery(sql);
			tabla.refresh(rs);
			System.out.println("Holaaaaaaaaa");
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
			System.out.println("Mostrar");
		}
	}	
	
	private void conectarBD() {
		if (this.conexionBD == null) {
			try {
				String servidor = "localhost:3306";
				String baseDatos = "parquimetros";
				String usuario = "inspector";
				String clave = "inspector";
				String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos
						+ "?serverTimezone=America/Argentina/Buenos_Aires";
				this.conexionBD = DriverManager.getConnection(uriConexion, usuario, clave);				
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(this,
						"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
				System.out.println("CONECTARUNIDAD");
			}
		}
	}
}
