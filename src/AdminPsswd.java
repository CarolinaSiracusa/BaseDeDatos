import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.*;

import quick.dbtable.DBTable;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.awt.event.ActionEvent;



public class AdminPsswd extends javax.swing.JInternalFrame  {
	
	//Declaramos las variables 
	private JDialog dialog; //Muestra los errores
	private JTextField txtAdmin;
	private JPasswordField txtPwd;
	private DBTable tabla;  
	private JPanel panel;
	private JButton entrar;
	
	//Constructor
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
		//Dimensionamos
		setPreferredSize(new Dimension(900, 700));
		this.setResizable(true);
		this.setBounds(0, 0, 900, 700);
        setVisible(true);
		this.setTitle("Administrador");
		getContentPane().setLayout(null);
		
		panel = new JPanel();
		panel.setBounds(0, 0,900, 700);
		panel.setOpaque(true);
		panel.setBackground(new Color(255, 243, 224));
		getContentPane().add(panel);
		panel.setLayout(null);
		
		//Elementos
		txtAdmin = new JTextField();
		txtAdmin.setBounds(251, 255, 86, 20);
		panel.add(txtAdmin);
		txtAdmin.setHorizontalAlignment(SwingConstants.CENTER);
		txtAdmin.setText("Admin");
		txtAdmin.setEditable(false);
		txtAdmin.setColumns(10);
		txtPwd = new JPasswordField();
		txtPwd.setLocation(347, 255);
		panel.add(txtPwd);
		txtPwd.setHorizontalAlignment(SwingConstants.CENTER);
		txtPwd.setSize(115,20);
		
		entrar = new JButton("Entrar");
		entrar.setBounds(472, 254, 63, 23);
		panel.add(entrar);
		//Si entramos correctamente cambiamos de Frame sino no
		entrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean exitosa=conectarBD();
				if(exitosa) {
					siguienteVentana();
				}
			}
		});
			 
			 
		// crea la tabla  
    	tabla = new DBTable();	 
		
	}
	
	private boolean conectarBD() {
			boolean exito=false;
	         try
	         {
	            String driver ="com.mysql.cj.jdbc.Driver";
	        	String servidor = "localhost:3306";
	        	String baseDatos = "parquimetros"; 
	        	String usuario = "admin";
	        	char[] clavee = txtPwd.getPassword();
	        	String clave = concatenar(clavee);
	            String uriConexion = "jdbc:mysql://" + servidor + "/" + 
	        	                     baseDatos +"?serverTimezone=America/Argentina/Buenos_Aires";
	   
	       //establece una conexión con la  B.D. "batallas"  usando directamante una tabla DBTable    
	            tabla.connectDatabase(driver, uriConexion, usuario, clave);
	            exito=true;
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
	      return exito;
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
		//getContentPane().validate();
		//getContentPane().revalidate();
		getContentPane().removeAll();
		getContentPane().add(admin);
		validate();
		//setContentPane(admin);
		//getContentPane().repaint();
		//setContentPane(getContentPane());
	}
}

