import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import quick.dbtable.DBTable;
import javax.swing.JLabel;

public class InspectorPsswd extends javax.swing.JInternalFrame  {
	private JDialog dialog; //Muestra los errores
	private JTextField txtInspector;
	private JPasswordField passwordField;
	private DBTable tabla;  
	private JPanel panel;
	private JLabel lblUnidadPersonalDel;
	protected Connection conexionBD = null;
	
	public InspectorPsswd() {
		super();
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
		//Dimensionamos
		setPreferredSize(new Dimension(900, 700));
		this.setResizable(true);
		this.setBounds(0, 0, 900, 700);
        setVisible(true);
		this.setTitle("Inspector");
		getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(0, 0, 884, 700);
		panel.setOpaque(true);
		panel.setBackground(new Color(197, 202, 233));
		getContentPane().add(panel);
		panel.setLayout(null);
		
		lblUnidadPersonalDel = new JLabel("UNIDAD PERSONAL DEL INSPECTOR");
		lblUnidadPersonalDel.setHorizontalAlignment(SwingConstants.CENTER);
		lblUnidadPersonalDel.setBounds(0, 48, 884, 84);
		panel.add(lblUnidadPersonalDel);
		
		JButton btnEntrar = new JButton("Entrar");
		btnEntrar.setBounds(456, 250, 63, 23);
		panel.add(btnEntrar);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(333, 251, 101, 20);
		panel.add(passwordField);
		
		txtInspector = new JTextField();
		txtInspector.setBounds(235, 251, 77, 20);
		panel.add(txtInspector);
		txtInspector.setHorizontalAlignment(SwingConstants.CENTER);
		txtInspector.setColumns(10);
		
		JLabel lblLegajo = new JLabel("Legajo");
		lblLegajo.setBounds(235, 233, 46, 14);
		panel.add(lblLegajo);
		
		JLabel lblConstrasea = new JLabel("Contrase\u00F1a");
		lblConstrasea.setBounds(333, 233, 77, 14);
		panel.add(lblConstrasea);
		tabla = new DBTable();	 
		conectarBD();
		
		btnEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (verificarCorrectitud()) {
					siguienteVentana();
				}
				else {
					JOptionPane.showMessageDialog(null, "Datos Incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
			 
			 
		// crea la tabla  
    	
		
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
	
	private boolean verificarCorrectitud()  {
		String legajo = txtInspector.getText();
		String contraseña = concatenar(passwordField.getPassword());
		String sql = "SELECT legajo , PASSWORD FROM inspectores;";
		Statement stmt;
		try {
			String contra = getMD5Hash(contraseña);
			stmt = this.conexionBD.createStatement();
			ResultSet  rs= stmt.executeQuery(sql);
			
			while(rs.next()) {
				int leg = rs.getInt(1);
				String l= String.valueOf(leg);
				String pswd = rs.getString(2);
				
				
				if (legajo.equals(l) && contra.equals(pswd)) {
					return true;
				}
				
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 

		

		return false;
	}
	
	
	
	//Pasar la contraseña a MD5
	public static String getMD5Hash(String s) throws NoSuchAlgorithmException {

		String result = s;
		if (s != null) {
		    MessageDigest md = MessageDigest.getInstance("MD5"); // or "SHA-1"
		    md.update(s.getBytes());
		    BigInteger hash = new BigInteger(1, md.digest());
		    result = hash.toString(16);
		    while (result.length() < 32) { // 40 for SHA-1
		        result = "0" + result;
		    }
		}
		return result; }
	
	private String concatenar(char[] clave) {
		String s = new String();
		for (char c : clave) {
			s = s + c;
		}
		
		return s;
	}
	
	private void siguienteVentana() {
		VentanaInspector ins = new VentanaInspector(txtInspector.getText());
		getContentPane().validate();
		getContentPane().revalidate();
		getContentPane().removeAll();
		getContentPane().add(ins);
		getContentPane().repaint();
		setContentPane(getContentPane());
	}
}



