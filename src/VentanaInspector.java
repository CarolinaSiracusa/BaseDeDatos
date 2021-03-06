import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
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
import javax.swing.border.BevelBorder;

public class VentanaInspector extends javax.swing.JInternalFrame{
	private JTextField textField;
	protected Connection conexionBD = null;
	//private DBTable tabla;  
	private JComboBox CBUbicaciones;
	private DefaultListModel listModel;
	private JList listUbicParq;
	private String legajo;
	private String ubicacion;
	private Date miFecha;
	private int idasociado_con;

	public VentanaInspector(String legajo) {
		super();
		this.legajo=legajo;
		initGUI();
		//tabla = new DBTable();	 
		
	}
	
	
	private void initGUI() {
		
		
		setPreferredSize(new Dimension(900, 700));
		this.setResizable(true);
		this.setBounds(0, 0, 900, 700);
		this.setTitle("Ventana Inspector");
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setResizable(true);
		this.setVisible(true);
		this.setBackground(new Color(187, 222, 251));
		
		conectarBD();
		
		getContentPane().setLayout(null);
		this.setTitle("Inspector");
		getContentPane().setVisible(true);
		JPanel panelPatentes = new JPanel();
		panelPatentes.setBounds(10, 11, 241, 299);
		panelPatentes.setLayout(null);
		panelPatentes.setOpaque(false);
		getContentPane().add(panelPatentes);
		
		textField = new JTextField();
		textField.setBounds(10, 74, 221, 142);
		textField.setBackground(new Color(250, 250, 250));
		panelPatentes.add(textField);
		textField.setColumns(10);
	
		JLabel lblAgregue = new JLabel("<html>Agregue las patentes, separadas por <br/>coma, sin espacios</html>");
		lblAgregue.setBounds(10, 6, 204, 57);
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
		panelUbParq.setLayout(null);
		panelUbParq.setOpaque(false);
		getContentPane().add(panelUbParq);
		
		
		
		CBUbicaciones = new JComboBox();
		CBUbicaciones.setBounds(0, 0, 229, 45);
		CBUbicaciones.setToolTipText("Ubicaciones");
		
		panelUbParq.add(CBUbicaciones);
		String[] patentes = obtenerPatentes();
		listModel = new DefaultListModel();
		listUbicParq = new JList(listModel);
		listUbicParq.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		listUbicParq.setVisible(false);
		listUbicParq.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String select= listUbicParq.getSelectedValue().toString();
				if(verificarAsociacion()) {		
					accede(select);
					sigVentana(patentes,idasociado_con,ubicacion);
				}
				else {
					JOptionPane.showMessageDialog(null, "Datos Incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		listUbicParq.setBounds(0, 65, 229, 233);
		listUbicParq.setBackground(new Color(204, 204, 255 ));
		panelUbParq.add(listUbicParq);		
		obtenerUbicaciones();
		repaint();
		CBUbicaciones.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ubicacion = CBUbicaciones.getSelectedItem().toString();
				listUbicParq.setVisible(true);
				obtenerParquimetros(ubicacion);
				repaint();
			}
		});
		
		
		//Hay que verificar que ese inspector trabaje ese dia y turno, en esa ubicacion
		
		repaint();
		
	}
	
	//Siguiente ventana
	private void sigVentana(String[] patentes, int id_asoc, String ubicacion) {
		UnidadParquimetro uni = new UnidadParquimetro(patentes,id_asoc,ubicacion);
		getContentPane().validate();
		getContentPane().revalidate();
		getContentPane().removeAll();
		getContentPane().add(uni);
		getContentPane().repaint();
		setContentPane(getContentPane());
	}
	
	//Registrando los accesos
	private void accede(String select) {
		String [] datos= select.split(" ",4);
		java.sql.Date fecha= java.sql.Date.valueOf((new SimpleDateFormat("yyyy-MM-dd")).format(miFecha));
		java.sql.Time hora= java.sql.Time.valueOf((new SimpleDateFormat("HH:mm:ss")).format(miFecha));
		String sql = "INSERT INTO accede VALUES (" + legajo + ", " + datos[1] +", " + "'"+fecha+"'" +", " + "'"+hora+"'" + ");";
		try {
			Statement stmt=this.conexionBD.createStatement();
			stmt.execute(sql);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	//Verifica que el inspector este asociado correctamente
	private boolean verificarAsociacion() {
		//id_asociado_con, legajo, calle, altura, dia y turno
		boolean verificar=false;
		String sql = "SELECT * FROM asociado_con WHERE legajo="+ legajo +";";
		try {
			Statement stmt=this.conexionBD.createStatement();
			ResultSet rs= stmt.executeQuery(sql);
			miFecha = new Date(); //a-m-d
			String day= new SimpleDateFormat("EEEEE").format(miFecha); 
			String dia= diaSemana(day);
			int hora = Integer.parseInt(new SimpleDateFormat("H").format(miFecha));
			String turno= turno(hora);
			while(rs.next()) {
				if(rs.getString(5).equals(dia) && rs.getString(6).equals(turno)) {
					idasociado_con=rs.getInt(1);
					String[] datosUbicacion= ubicacion.split(" ",2);
					if(rs.getString(3).equals(datosUbicacion[0]) && rs.getString(4).equals(datosUbicacion[1])){
						verificar=true;
					}
				}
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return verificar;
	}
	
	//Devuelve el turno segun la hora
	private String turno(int hora) {
		String toReturn="";
		if(hora>=8 && hora<=13) {
			toReturn="m";
		}
		else {//Habria que validar 20:00?
			if(hora>=14 && hora<=22) {
				toReturn="t";
			}
			else {
				JOptionPane.showMessageDialog(null, "Horario no valido", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return toReturn;
	}
	
	//Devuelve el dia en espa�ol
	private String diaSemana(String day) {
		String toReturn=" ";
		switch (day)
		{
		case "domingo": toReturn= "do"; break;
		case "lunes": toReturn= "lu"; break;
		case "martes": toReturn= "ma";break;
		case "miercoles": toReturn= "mi";break;
		case "jueves": toReturn= "ju";break;
		case "viernes": toReturn= "vi";break;
		case "sabado": toReturn= "sa";break;
		}
		return toReturn;
	}
	
	private String[] obtenerPatentes() {
		String[] arreglo = textField.getText().split(",");
		return arreglo;
	}
	
	//Obtenemos parquimetros de acuerdo a la ubicacion
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
			rs.close();
			stmt.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			System.out.println("OBTENERPARQ");
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
				System.out.println("CONECTARBD");
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
			rs.close();
			stmt.close();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this,
					"Se produjo un error al intentar conectarse a la base de datos.\n" + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			System.out.println("OBTENERUBIC");
		}		
	}		
}
