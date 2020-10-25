import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.*;

import quick.dbtable.DBTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



public class AdminPsswd extends javax.swing.JInternalFrame  {
	

	
	private JDialog dialog; //Muestra los errores
	private JTextField txtAdmin;
	private JPasswordField passwordField;
	private DBTable tabla;  
	private JPanel panel;
	
	
	public AdminPsswd() {
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
		
		setPreferredSize(new Dimension(800, 600));
		 this.setBounds(0, 0, 801, 600);
         setVisible(true);
		this.setTitle("Administrador");
		getContentPane().setLayout(null);
		
		txtAdmin = new JTextField();
		txtAdmin.setHorizontalAlignment(SwingConstants.CENTER);
		txtAdmin.setBounds(222, 193, 66, 20);
		txtAdmin.setText("Admin");
		txtAdmin.setEditable(false);
		getContentPane().add(txtAdmin);
		txtAdmin.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(298, 193, 66, 20);
		getContentPane().add(passwordField);
		
		JButton btnEntrar = new JButton("Entrar");
		btnEntrar.setBounds(374, 192, 89, 23);
		getContentPane().add(btnEntrar);
		
		panel = new JPanel();
		panel.setBounds(0, 0, 784, 561);
		panel.setBackground(Color.white);
		getContentPane().add(panel);
			 
			 
		// crea la tabla  
    	tabla = new DBTable();	 
    	
		btnEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				conectarBD();
				siguienteVentana();
			}
		});
		
	}
	
	private void conectarBD()
	   {
	         try
	         {
	            String driver ="com.mysql.cj.jdbc.Driver";
	        	String servidor = "localhost:3306";
	        	String baseDatos = "parquimetros"; 
	        	String usuario = "admin";
	        	char[] clavee = passwordField.getPassword();
	        	String clave = concatenar(clavee);
	            String uriConexion = "jdbc:mysql://" + servidor + "/" + 
	        	                     baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
	   
	       //establece una conexión con la  B.D. "batallas"  usando directamante una tabla DBTable    
	            tabla.connectDatabase(driver, uriConexion, usuario, clave);
	           
	         }
	         catch (SQLException ex)
	         {
	            JOptionPane.showMessageDialog(this,
	                           "La contraseña es incorrecta. Vuelva a intentarlo.\n" 
	                            + ex.getMessage(),
	                            "Error",
	                            JOptionPane.ERROR_MESSAGE);
	            System.out.println("SQLException: " + ex.getMessage());
	            System.out.println("SQLState: " + ex.getSQLState());
	            System.out.println("VendorError: " + ex.getErrorCode());
	         }
	         catch (ClassNotFoundException e)
	         {
	            e.printStackTrace();
	         }
	      
	   }
	
	private String concatenar(char[] clave) {
		String s = new String();
		for (char c : clave) {
			s = s + c;
		}
		
		return s;
	}
	
	private void siguienteVentana() {
		VentanaAdmin admin = new VentanaAdmin();
		getContentPane().validate();
		getContentPane().revalidate();
		getContentPane().removeAll();
		getContentPane().add(admin);
		getContentPane().repaint();
		setContentPane(getContentPane());
	}
}



















