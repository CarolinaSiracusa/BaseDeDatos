
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
//import javax.swing.JScrollPane;
//import javax.swing.JTable;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;



import quick.dbtable.*;
import javax.swing.JList;
import java.awt.Color;

@SuppressWarnings("serial")
public class VentanaAdmin extends javax.swing.JInternalFrame {

	private JPanel pnlInferior;
	private JButton btnejecutar;
	// private JScrollPane scrTabla;
	// private JTable tabla;
	private DBTable tabla;
	private JList listTablas;
	private String[] tablas;

	protected Connection conexionBD = null;
	protected int seleccionado = -1;
	private JTextField txtConsultas;
	private JPanel panelDer;
	private JList list;

	public VentanaAdmin() {
		super();
		getContentPane().setBackground(new Color(255, 255, 204 ));
		initGUI();
	}

	private void initGUI() {
		try 
	      {
	         javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	      } 
	     catch(Exception e) 
	      {
	         e.printStackTrace();
	      }
		try {
			setPreferredSize(new Dimension(850, 650));
			this.setResizable(true);		
			this.setBounds(0, 0, 850, 650);
			this.setTitle("Consultas Administrador");
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.setResizable(true);
			this.setVisible(true);
			this.conectarBD();

			// Utilizando DBTable
			// crea la tabla
			tabla = new DBTable();
			tabla.setBounds(0, 0, 558, 403);				
			getContentPane().setLayout(null);
			
			// Agregar la tabla al frame (no necesita JScrollPane como Jtable)
			getContentPane().add(tabla);
			//Creando lugar de Consultas
			txtConsultas = new JTextField();
			txtConsultas.setBounds(0, 0, 833, 90);
			txtConsultas.setText("Escriba su consulta");
			txtConsultas.setColumns(10);
			txtConsultas.setBackground(new Color(255, 245, 157));
				
			pnlInferior = new JPanel();
			pnlInferior.setBounds(0, 414, 834, 156);
			getContentPane().add(pnlInferior);
			pnlInferior.setPreferredSize(new java.awt.Dimension(638, 88));
			pnlInferior.setLayout(null);
			pnlInferior.setBackground(new Color(255, 255, 204 ));
			pnlInferior.add(txtConsultas);
				
			btnejecutar = new JButton();
			btnejecutar.setBounds(303, 113, 73, 32);
			pnlInferior.add(btnejecutar);
			btnejecutar.setText("Ejecutar");
			btnejecutar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					//Ejecutar lo que se ponga en el texto
					String consulta = txtConsultas.getText();
					ejecutar(consulta);
				}
			});
						//11 items
			panelDer = new JPanel();
			panelDer.setBounds(557, 0, 277, 403);
			getContentPane().add(panelDer);
							
							
			tablas = obtenerTablas();
			panelDer.setLayout(new GridLayout(0, 1, 0, 0));
			listTablas = new JList(tablas);
			panelDer.add(listTablas);
			listTablas.setBackground(new Color(178, 223, 219));
			DefaultListModel listModel = new DefaultListModel();
			list = new JList(listModel);
			panelDer.add(list);
			list.setBackground(new Color(224, 242, 241));
			listTablas.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					listModel.clear();
					String [] atr = mostrarInfo();
					for (String a : atr) {
						listModel.addElement(a);
					}
					repaint();
				}
			});
				
			
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void thisComponentShown(ComponentEvent evt) {
		this.conectarBD();
		//this.refrescar();
	}

	private void thisComponentHidden(ComponentEvent evt) {
		this.desconectarBD();
	}
	
	private void conectarBD() {
		if (this.conexionBD == null) {
			try {
				String servidor = "localhost:3306";
				String baseDatos = "parquimetros";
				String usuario = "admin";
				String clave = "admin";
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
			}
		}
	}

	private void desconectarBD() {
		if (this.conexionBD != null) {
			try {
				this.conexionBD.close();
				this.conexionBD = null;
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}
	}
	
	private void ejecutar(String query) {
		String arr[] = query.split(" ", 2);
		String primeraPalabra = arr[0].toUpperCase();		
		if (primeraPalabra.equals("SELECT")) {			
			refrescarTabla(query);
		}
		else {
			abmTabla(query);
		}
	}
	
	private void abmTabla(String query) {
		try {
			Statement stmt = this.conexionBD.createStatement();

			//String sql = "SELECT * FROM " + t;
			stmt.execute(query);
			// actualiza el contenido de la tabla con los datos del resulset rs
			//tabla.refresh(rs);
			 for (int i = 0; i < tabla.getColumnCount(); i++)
	    	  { // para que muestre correctamente los valores de tipo TIME (hora)  		   		  
	    		 if	 (tabla.getColumn(i).getType()==Types.TIME)  
	    		 {    		 
	    		    tabla.getColumn(i).setType(Types.CHAR);  
	  	       	 }
	    		 // cambiar el formato en que se muestran los valores de tipo DATE
	    		 if	 (tabla.getColumn(i).getType()==Types.DATE)
	    		 {
	    		    tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
	    		 }
	          }  
			//rs.close();
			stmt.close();
		} catch (SQLException ex) { JOptionPane.showMessageDialog(this,
                "Modificación invalida.\n" 
                        + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

		
	}
	
	private void refrescarTabla(String query) {
		try {
			Statement stmt = this.conexionBD.createStatement();
			//String sql = "SELECT * FROM " + t;
			ResultSet rs = stmt.executeQuery(query);
			// actualiza el contenido de la tabla con los datos del resulset rs
			tabla.refresh(rs);
			 for (int i = 0; i < tabla.getColumnCount(); i++)
	    	  { // para que muestre correctamente los valores de tipo TIME (hora)  		   		  
	    		 if	 (tabla.getColumn(i).getType()==Types.TIME)  
	    		 {    		 
	    		    tabla.getColumn(i).setType(Types.CHAR);  
	  	       	 }
	    		 // cambiar el formato en que se muestran los valores de tipo DATE
	    		 if	 (tabla.getColumn(i).getType()==Types.DATE)
	    		 {
	    		    tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
	    		 }
	          }  
			rs.close();
			stmt.close();
		} catch (SQLException ex) {
			 JOptionPane.showMessageDialog(this,
                     "Consulta Invalida.\n" 
                      + ex.getMessage(),
                      "Error",
                      JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}	
	}
	
	//Todas las tablas de la base de datos
	private String[] obtenerTablas() {		
		String[] tablas = new String[11];		
		try {
			DatabaseMetaData md = this.conexionBD.getMetaData();
			String[] types = {"TABLE"};
			ResultSet  rs= md.getTables(null, null, null, new String[] {
			         "TABLE"
		      });
			int i = 0;
			while (rs.next()) {
				String tblName = rs.getString("TABLE_NAME");
				tablas[i]= (tblName);
				i++;				
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return tablas;
	}
	
	
	private String[] mostrarInfo() {
		
		int seleccionado = listTablas.getSelectedIndex();
		String [] atributos= null;
		String tabla = tablas[seleccionado];
		String sql = "SELECT * FROM " + tabla + ";";
		Statement stmt;
		try {
			stmt = this.conexionBD.createStatement();
			ResultSet  rs= stmt.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int count = metaData.getColumnCount();
			atributos = new String[count];
			for (int i = 1; i<=count; i++) {
				atributos[i-1] = metaData.getColumnName(i);
				
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		refrescarTabla("SELECT * FROM " + tabla);
		return atributos;
	}
}























