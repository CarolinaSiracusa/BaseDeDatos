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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
	String legajo;
	String ubicacion;
	LocalDate miFecha;
	LocalDateTime miHora;
	int idasociado_con;
	
	public VentanaInspector(String legajo) {
		super();
		this.legajo=legajo;
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
		String[] patentes = obtenerPatentes();
		listModel = new DefaultListModel();
		listUbicParq = new JList(listModel);
		listUbicParq.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String select= listUbicParq.getSelectedValue().toString();
				if(verificarAsociacion()) {
					accede(select);
					sigVentana(select,patentes,idasociado_con);
				}
			}
		});
		listUbicParq.setBounds(0, 65, 229, 233);
		panelUbParq.add(listUbicParq);
		
		obtenerUbicaciones();
		repaint();
		
		
		CBUbicaciones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ubicacion = CBUbicaciones.getSelectedItem().toString();
				obtenerParquimetros(ubicacion);
				repaint();
			}
		});
		
		
		//Hay que verificar que ese inspector trabaje ese dia y turno, en esa ubicacion
		
		if(verificarAsociacion()) {
			
		}
		
	
		
		
		repaint();
		
	}
	
	private void sigVentana(String id_parq, String[] patentes, int id_asoc) {
		UnidadParquimetro ins = new UnidadParquimetro(id_parq,patentes,id_asoc);
		getContentPane().validate();
		getContentPane().revalidate();
		getContentPane().removeAll();
		getContentPane().add(ins);
		getContentPane().repaint();
		setContentPane(getContentPane());
	}
	
	private void accede(String select) {
		String [] datos= select.split(" ",4);
		String sql = "INSERT INTO accede VALUES (" + legajo + ", " + datos[2] +", " + miFecha +", " + miHora+ ");";
		try {
			Statement stmt=this.conexionBD.createStatement();
			stmt.execute(sql);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	private boolean verificarAsociacion() {
		//id_asociado_con, legajo, calle, altura, dia y turno
		boolean verificar=false;
		String sql = "SELECT * FROM asociado_con WHERE legajo="+ legajo +";";
		try {
			Statement stmt=this.conexionBD.createStatement();
			ResultSet rs= stmt.executeQuery(sql);
			miFecha = LocalDate.now(); //a-m-d
			String day = miFecha.getDayOfWeek().toString();
			String dia= toSpanish(day);
			miHora = LocalDateTime.now();
			int hora = miHora.getHour();
			String turno= turno(hora);
			idasociado_con=rs.getString(1);
			while(rs.next()) {
				if(rs.getString(5).equals(dia) && rs.getString(6).equals(turno)) {
					String[] datosUbicacion= ubicacion.split(" ",2);
					if(rs.getString(3).equals(datosUbicacion[0]) && rs.getString(4).equals(datosUbicacion[1])){
						verificar=true;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return verificar;
	}
	
	private String turno(int hora) {
		String toReturn="";
		if(hora>=8 && hora<=13) {
			toReturn="M";
		}
		else {//Habria que validar 20:00?
			if(hora>=14 && hora<=19) {
				toReturn="T";
			}
			else {
				JOptionPane.showMessageDialog(null, "Horario no valido", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return toReturn;
	}
	private String toSpanish(String day) {
		String toReturn=" ";
		switch (day)
		{
		case "SUNDAY": toReturn= "Do";
		case "MONDAY": toReturn= "Lu";
		case "TUESDAY": toReturn= "Ma";
		case "WEDNESDAY": toReturn= "Mi";
		case "THURSDAY": toReturn= "Ju";
		case "FRIDAY": toReturn= "Vi";
		case "SATURDAY": toReturn= "Sa";
		}
		return toReturn;
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
