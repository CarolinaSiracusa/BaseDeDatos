import javax.swing.JButton;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Ventana extends javax.swing.JFrame  {
	
	
	public Ventana() {
		super();
		initGUI();
	}
	
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
	setPreferredSize(new Dimension(800, 600));
	 this.setBounds(0, 0, 801, 600);
    setVisible(true);
    this.setTitle("Bienvenido");
	getContentPane().setLayout(new GridLayout(1, 2, 0, 0));
	
	JButton inspector = new JButton("SOY INSPECTOR");
	
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
	
	JButton admin = new JButton("SOY ADMIN");
	admin.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AdminPsswd ad = new AdminPsswd();
			getContentPane().validate();
			getContentPane().revalidate();
			getContentPane().removeAll();
			getContentPane().add(ad);
			getContentPane().repaint();
			setContentPane(getContentPane());
		}
	});
	getContentPane().add(admin);
}
	

}
