package fr.polytech.project4a.ihm;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fr.polytech.project4a.reseau.serveur.Serveur;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class ServeurApp {

	private JFrame frame;
	private JTextField textField;
	private JButton btnRun;
	private Serveur serveur;
	private JLabel label;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServeurApp window = new ServeurApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServeurApp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[grow][][][fill][grow]", "[grow][][grow]"));
		
		JLabel lblPort = new JLabel("port :");
		frame.getContentPane().add(lblPort, "cell 1 1");
		
		textField = new JTextField(Serveur.port+"");
		frame.getContentPane().add(textField, "cell 2 1,growx");
		textField.setColumns(10);
		
		btnRun = new JButton("run");
		btnRun.addActionListener(new ActionListener() {
			private boolean run = false;
			public void actionPerformed(ActionEvent e) {
				run = !run;
				if(run) {
					serveur.configServeur(Integer.valueOf(textField.getText()));
						new Thread(() -> {
							try {
								serveur.run();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}).start();
						ImageIcon icon = new ImageIcon(new ImageIcon("resources/serveurWorking.gif").getImage().
								getScaledInstance(-1, 150, Image.SCALE_DEFAULT));
						label.setIcon(icon);
						btnRun.setText("stop");
				} else {
					try {
						serveur.stop();
						ImageIcon icon = new ImageIcon(new ImageIcon("resources/serveurBroke.png").getImage().
								getScaledInstance(-1, 150, Image.SCALE_AREA_AVERAGING));
						label.setIcon(icon);
						btnRun.setText("run");
					} catch (IOException e1) {
						run = !run;
						e1.printStackTrace();
					}
				}
			}
		});
		frame.getContentPane().add(btnRun, "cell 3 1");
		
		label = new JLabel("");
		frame.getContentPane().add(label, "cell 2 2,alignx center,aligny center");
		
		try {
			serveur = new Serveur();
			Serveur.bindServeur(serveur);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
