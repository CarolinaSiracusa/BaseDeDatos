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
import java.util.ArrayList;

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
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class VentanaInspector extends javax.swing.JInternalFrame{
	private JTextField textField;
	protected Connection conexionBD = null;
	//private DBTable tabla;  
	JComboBox CBUbicaciones;
	DefaultListModel listModel;
	JList listUbicParq;
	
	
	public VentanaInspector() {
		super();
		initGUI();
		//tabla = new DBTable();	 
		
	}
	
	
	private void initGUI() {
		
		
		setPreferredSize(new Dimension(800, 600));
		this.setResizable(true);
	
		this.setBounds(0, 0, 801, 600);
		this.setTitle("Consultas Administrador");
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setResizable(true);
		this.setClosable(true);
		this.setVisible(true);
		this.setMaximizable(true);
		
		conectarBD();
		
		getContentPane().setLayout(null);
		this.setTitle("Inspector");
		getContentPane().setVisible(true);
		JPanel panelPatentes = new JPanel();
		panelPatentes.setBounds(10, 11, 241, 299);
		getContentPane().add(panelPatentes);
		panelPatentes.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 74, 221, 113);
		panelPatentes.add(textField);
		textField.setColumns(10);
		
		JLabel lblAgregue = new JLabel("Agregue");
		lblAgregue.setBounds(66, 6, 109, 57);
		panelPatentes.add(lblAgregue);
		
		JButton btnNewButton = new JButton("AgregarPatentes");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(52, 265, 139, 23);
		panelPatentes.add(btnNewButton);
		
		JPanel panelUbParq = new JPanel();
		panelUbParq.setBounds(261, 11, 229, 299);
		getContentPane().add(panelUbParq);
		panelUbParq.setLayout(null);
		
		
		CBUbicaciones = new JComboBox();
		CBUbicaciones.setBounds(0, 0, 229, 45);
		
		CBUbicaciones.setToolTipText("Ubicaciones");
		
		panelUbParq.add(CBUbicaciones);
		
		listModel = new DefaultListModel();
		listUbicParq = new JList(listModel);
		listUbicParq.setBounds(0, 65, 229, 233);
		panelUbParq.add(listUbicParq);
		
		obtenerUbicaciones();
		repaint();
		
		
		CBUbicaciones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String ubicacion = CBUbicaciones.getSelectedItem().toString();
				obtenerParquimetros(ubicacion);
				repaint();
			}
		});
		
		
		//Hay que verificar que ese inspector trabaje ese dia y turno, en esa ubicacion
		
		String[] patentes = obtenerPatentes();
		
		
		repaint();
		
	}
	
	//QUE HACEMOS CON LAS PATENTES?
	private String[] obtenerPatentes() {
		String[] arreglo = textField.getText().split(",");
		
		
		return arreglo;
	}
	
	
	private void obtenerParquimetros(String ubicacion) {
		String ubic[] = ubicacion.split(" ", 2);
		String sql = "SELECT * FROM parquimetros;";
		Statement stmt;
		try {
			stmt = this.conexionBD.createStatement();
			ResultSet  rs= stmt.executeQuery(sql);
			listModel.clear();
			while (rs.next()) {
				String calleparq= rs.getString(3);
				String alturaparq= rs.getString(4);
				if (calleparq.equals(ubic[0]) && alturaparq.equals(ubic[1])) {
					String datosParq = "ID: " + rs.getString(1) + " Nro: "+ rs.getString(2);
					listModel.addElement(datosParq);
				}
			}
			
			
			
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
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
			}
		}
	}
	
	private void obtenerUbicaciones() {
		String sql = "SELECT DISTINCT calle, altura FROM parquimetros;";
		Statement stmt;
		try {
			stmt = this.conexionBD.createStatement();
			ResultSet  rs= stmt.executeQuery(sql);

			while (rs.next()) {
				String calle = rs.getString(1);
				String altura = rs.getString(2);
				String ubic = calle + " " + altura;
				CBUbicaciones.addItem(ubic);
			}
			
			
			
			
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
