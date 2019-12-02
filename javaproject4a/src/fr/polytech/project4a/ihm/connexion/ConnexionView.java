package fr.polytech.project4a.ihm.connexion;

import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import fr.polytech.project4a.ihm.handler.KaraokerFrameHandler;
import fr.polytech.project4a.reseau.client.Client;
import fr.polytech.project4a.reseau.client.IAccessRessource;

import javax.swing.JButton;
import javax.swing.JDialog;

import net.miginfocom.swing.MigLayout;

public class ConnexionView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6250926128374037190L;
	private JTextField textIP;
	private JTextField textPort;
	
	public static final String DEFAULT_IP = "127.0.0.1";
	public static final int DEFAULT_PORT = 9876;
	
	
	private List<KaraokerFrameHandler> listListener;
	private JTextField txtDefault;

	/**
	 * Create the panel.
	 */
	public ConnexionView() {
		setLayout(new MigLayout("", "[grow][][grow][18px][87px][29px][86px][80px][grow]", "[grow][21px][grow]"));
		
		JLabel lblNom = new JLabel("nom : ");
		add(lblNom, "cell 1 1,alignx trailing");
		
		txtDefault = new JTextField();
		txtDefault.setText("default");
		add(txtDefault, "cell 2 1,growx");
		txtDefault.setColumns(10);
		
		JLabel lblIP = new JLabel("ip : ");
		add(lblIP, "cell 3 1,alignx left,aligny center");
		
		textIP = new JTextField();
		add(textIP, "cell 4 1,growx,aligny center");
		textIP.setColumns(10);
		
		JLabel lblPort = new JLabel("port : ");
		add(lblPort, "cell 5 1,alignx left,aligny center");
		
		textPort = new JTextField();
		add(textPort, "cell 6 1,growx,aligny center");
		textPort.setColumns(10);
		
		JButton btnConnexion = new JButton("connexion");
		btnConnexion.addActionListener(getActionConnexion());
		add(btnConnexion, "cell 7 1,growx,aligny top");
		
		listListener = new ArrayList<KaraokerFrameHandler>();
	}
	
	public void clean() {
		textIP.setText(DEFAULT_IP);
		textPort.setText(""+DEFAULT_PORT);
	}
	
	public void addListener(KaraokerFrameHandler handler) {
		listListener.add(handler);
	}
	
	private ActionListener getActionConnexion() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String ip = textIP.getText();
				int port = Integer.valueOf(textPort.getText());
				String user = txtDefault.getText();
				try {
					IAccessRessource client = new Client(ip, port,user);//TODO:ajouter le nom dans le client
					listListener.stream().forEach(l -> l.connexion(client));
				} catch (IOException err) {
					err.printStackTrace();
					JOptionPane pane = new JOptionPane("Erreur lors de la creation du client", JOptionPane.ERROR_MESSAGE);
					JDialog dialog = pane.createDialog("Connexion");
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}
			}
		};
	}
}
