import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Ventana extends javax.swing.JFrame  {
	
	private JButton inspector;
	private JButton admin;
	
	//Constructor	
	public Ventana() {
		super();
		initGUI();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//Inicializacion
	public static void main(String [] args) {
			
			SwingUtilities.invokeLater(new Runnable() {
		         public void run() {
		            Ventana vp = new Ventana();
		            vp.setLocationRelativeTo(null);
		            vp.setVisible(true);
		         }
		      });
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
		//Seteamos dimensiones
		setPreferredSize(new Dimension(900, 700));
		this.setResizable(true);
		this.setBounds(0, 0, 900, 700);
	    setVisible(true);
	    this.setTitle("Bienvenido");
		getContentPane().setLayout(new GridLayout(1, 2, 0, 0));
		
		//Declaramos botones
		inspector = new JButton("SOY INSPECTOR");
		inspector.setBackground(new Color(232, 218, 239));
		inspector.setOpaque(true);
		inspector.setBorderPainted(false);
		
		admin = new JButton("SOY ADMIN");
		admin.setBackground(new Color(255, 249, 196));
		admin.setOpaque(true);
		admin.setBorderPainted(false);
		
		//Creamos los oyentes y los agregamos
		inspector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InspectorPsswd ins = new InspectorPsswd();
				getContentPane().validate();
				getContentPane().revalidate();
				getContentPane().removeAll();
				getContentPane().add(ins);
				getContentPane().repaint();
				setContentPane(getContentPane());
			}
		});
		getContentPane().add(inspector);
		
		
		admin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AdminPsswd ad = new AdminPsswd();
			//	getContentPane().validate();
			//	getContentPane().revalidate();
				getContentPane().removeAll();
				add(ad);
				validate();
				//getContentPane().add(ad);
				//getContentPane().repaint();
				//setContentPane(getContentPane());
				
			}
		});
		getContentPane().add(admin);
	}		
}
