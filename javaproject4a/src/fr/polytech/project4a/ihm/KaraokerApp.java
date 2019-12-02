package fr.polytech.project4a.ihm;

import java.awt.EventQueue;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fr.polytech.project4a.ihm.connexion.ConnexionView;
import fr.polytech.project4a.ihm.handler.KaraokerFrameHandler;
import fr.polytech.project4a.ihm.karaoker.KaraokerView;
import fr.polytech.project4a.reseau.client.IAccessRessource;
import fr.polytech.project4a.reseau.serveur.IStatistique;

import java.awt.CardLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;
import java.awt.event.ActionEvent;

public class KaraokerApp implements KaraokerFrameHandler{

	private static final String CONNEXION_VIEW = "ConnexionView";
	private static final String KARAOKER_VIEW = "KaraokerView";
	private JFrame frame;
	private CardLayout cardlayout;
	private ConnexionView connexionPanal;
	private KaraokerView karaokerPanel;
	private JMenuBar menuBar;
	private JMenu mnStatistique;
	private JMenuItem mntmBestUser;
	private JMenuItem mntmStatistiqueMusic;
	private IAccessRessource client;

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
					KaraokerApp window = new KaraokerApp();
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
	public KaraokerApp() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		cardlayout = new CardLayout(0, 0);
		frame.getContentPane().setLayout(cardlayout);
		
		connexionPanal = new ConnexionView();
		connexionPanal.addListener(this);
		frame.getContentPane().add(connexionPanal, CONNEXION_VIEW);
		
		karaokerPanel = new KaraokerView();
		karaokerPanel.addListener(this);
		frame.getContentPane().add(karaokerPanel, KARAOKER_VIEW);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		mnStatistique = new JMenu("Statistique");
		menuBar.add(mnStatistique);
		
		mntmBestUser = new JMenuItem("best user");
		mntmBestUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Objects.nonNull(client)) {
					try {
						IStatistique statisique = client.getStatistique();
						String user = statisique.getUser();
						final JComponent[] inputs = new JComponent[] 
							{ 
								new JLabel("Best user:"), 
								new JLabel(user) 
							}; 
	
						JOptionPane.showConfirmDialog(null, inputs, "Best user",
								JOptionPane.PLAIN_MESSAGE);
					} catch (ClassNotFoundException | IOException e1) {
						e1.printStackTrace();
					}
				} else {
					JOptionPane.showConfirmDialog(null, new JComponent[]{new JLabel("Erreur de connexion")}, "Erreur de connexion",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnStatistique.add(mntmBestUser);
		
		mntmStatistiqueMusic = new JMenuItem("statistique music");
		mntmStatistiqueMusic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Objects.nonNull(client)) {
					String[] listMidi = new String[0];
					try {
						listMidi = client.listMidi();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					JComboBox<String> music = new JComboBox<String>(listMidi);
					final JComponent[] inputs = new JComponent[] 
						{ 
							new JLabel("music:"), 
							music 
						};

					int result = JOptionPane.showConfirmDialog(null, inputs, "Music statistique",
							JOptionPane.PLAIN_MESSAGE);
					if(result == JOptionPane.OK_OPTION && music.getSelectedIndex() != -1) {
						String nameMusic = music.getItemAt(music.getSelectedIndex());
						try {
							IStatistique statisique = client.getStatistique();
							int res = statisique.getStatMusic(nameMusic);
							final JComponent[] inputsv2 = new JComponent[] 
								{ 
									new JLabel(nameMusic+":"), 
									new JLabel(res+" ecoute")
								}; 
							JOptionPane.showConfirmDialog(null, inputsv2, "Music statistique",
									JOptionPane.PLAIN_MESSAGE);
						} catch (ClassNotFoundException | IOException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					JOptionPane.showConfirmDialog(null, new JComponent[]{new JLabel("Erreur de connexion")}, "Erreur de connexion",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnStatistique.add(mntmStatistiqueMusic);
		
		connexionPanal.clean();
		karaokerPanel.clean();
	}

	@Override
	public void connexion(IAccessRessource client) {
		this.client = client;
		System.out.println("connexion");
		karaokerPanel.clean();
		karaokerPanel.setAccessResource(client);
		cardlayout.show(frame.getContentPane(), KARAOKER_VIEW);
		karaokerPanel.updateData();
	}

	@Override
	public void deconnexion() {
		this.client = null;
		System.out.println("deconnexion");
		connexionPanal.clean();
		cardlayout.show(frame.getContentPane(), CONNEXION_VIEW);
	}

}
