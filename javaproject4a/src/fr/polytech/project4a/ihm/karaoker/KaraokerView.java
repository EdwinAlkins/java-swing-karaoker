package fr.polytech.project4a.ihm.karaoker;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import fr.polytech.project4a.ihm.handler.KaraokerFrameHandler;
import fr.polytech.project4a.ihm.handler.MidiHandler;
import fr.polytech.project4a.ihm.midiUtility.MidiPlayer;
import fr.polytech.project4a.ihm.midiUtility.TextKaraoker;
import fr.polytech.project4a.reseau.client.IAccessRessource;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import net.miginfocom.swing.MigLayout;
import javax.swing.JSpinner;
import javax.swing.JSlider;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.awt.Cursor;
import java.awt.Font;

public class KaraokerView extends JPanel implements MidiHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1680974841335041053L;

	private List<KaraokerFrameHandler> listListener;

	private JComboBox<String> comboBoxListMidi;

	private IAccessRessource accessRes;
	
	private MidiPlayer midiReader;

	private JProgressBar timerBar;

	private JLabel lblTxtkaraoker;

	private JLabel lblVitesseValue;

	private JSlider sliderVitesse;

	private JSpinner spinnerTranspose;

	private JButton btnPause;

	private JButton btnPlay;
	private JButton btnMute;
	private JLabel lblType;
	
	/**
	 * Create the panel.
	 */
	public KaraokerView() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelNorth = new JPanel();
		add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new MigLayout("", "[68.00,grow,fill][297px][51px][]", "[21px,grow]"));
		
		lblType = new JLabel("normal");
		lblType.setFont(new Font("Tahoma", Font.BOLD, 17));
		panelNorth.add(lblType, "cell 0 0,alignx trailing");
		
		comboBoxListMidi = new JComboBox<String>();
		panelNorth.add(comboBoxListMidi, "cell 1 0,growx,aligny top");
		
		JButton btnLoadMidi = new JButton("load");
		btnLoadMidi.addActionListener(getActionLoadMidi());
		panelNorth.add(btnLoadMidi, "cell 2 0,alignx left,aligny top");
		
		JButton btnDeconnexion = new JButton("deconnexion");
		btnDeconnexion.addActionListener(getActionDeconnexion());
		panelNorth.add(btnDeconnexion, "cell 3 0");
		
		JPanel panelCenter = new JPanel();
		add(panelCenter, BorderLayout.CENTER);
		panelCenter.setLayout(new MigLayout("", "[grow][fill][grow]", "[grow][fill][grow]"));
		
		lblTxtkaraoker = new JLabel("txtKaraoker");
		lblTxtkaraoker.setFont(new Font("Verdana", Font.PLAIN, 16));
		panelCenter.add(lblTxtkaraoker, "cell 1 1,alignx leading,growy");
		
		JPanel panelSouth = new JPanel();
		add(panelSouth, BorderLayout.SOUTH);
		panelSouth.setLayout(new MigLayout("", "[center][center][43px][39px][266px,grow]", "[][21px,fill]"));
		
		JLabel lblVitesse = new JLabel("vitesse");
		panelSouth.add(lblVitesse, "cell 0 0");
		
		JLabel lblTonalit = new JLabel("\"tonalit\u00E9\"");
		panelSouth.add(lblTonalit, "cell 1 0");
		
		sliderVitesse = new JSlider();
		sliderVitesse.addChangeListener(getChangeListenerTempoChange());
		
		btnMute = new JButton("Mute");
		btnMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				midiReader.setMute(new HashSet<String>());
				JOptionPane.showConfirmDialog(null, midiReader.getBox(), "Erreur de connexion",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		panelSouth.add(btnMute, "cell 2 0");
		
		lblVitesseValue = new JLabel("1.00");
		panelSouth.add(lblVitesseValue, "flowy,cell 0 1");
		sliderVitesse.setPreferredSize(new Dimension(40, 22));
		sliderVitesse.setValue(100);
		sliderVitesse.setMaximum(200);
		panelSouth.add(sliderVitesse, "cell 0 1");
		
		spinnerTranspose = new JSpinner();
		spinnerTranspose.addChangeListener(getChangeListenerTransposeChange());
		panelSouth.add(spinnerTranspose, "cell 1 1");
		
		btnPause = new JButton("| |");
		btnPause.addActionListener(getActionPause());
		panelSouth.add(btnPause, "cell 2 1,grow");
		
		btnPlay = new JButton(">");
		btnPlay.addActionListener(getActionPlay());
		panelSouth.add(btnPlay, "cell 3 1,grow");
		
		timerBar = new JProgressBar();
		timerBar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		timerBar.setMaximum(100);
		timerBar.addMouseListener(getTimerBarMouseActionListener());
		timerBar.addMouseMotionListener(getTimerBarMouserDraggedListener());
		panelSouth.add(timerBar, "cell 4 1,grow");

		listListener = new ArrayList<KaraokerFrameHandler>();
		
		midiReader = new MidiPlayer();
		midiReader.addHandler(this);
	}

	private MouseMotionAdapter getTimerBarMouserDraggedListener() {
		return new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(timerBar.isEnabled()) {
					int val = 0;
					final int size = timerBar.getSize().width;
					double diff =(double)timerBar.getMaximum()/(double)size;
					val = (int) Math.round(e.getX()*diff);
					timerBar.setValue(val);
				}
			}
		};
	}

	private MouseAdapter getTimerBarMouseActionListener() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if(Objects.nonNull(midiReader) && timerBar.isEnabled()) {
					midiReader.stop();
				}
			}
			public void mouseReleased(MouseEvent e) {
				if(Objects.nonNull(midiReader) && timerBar.isEnabled()) {
					midiReader.setTime((long)timerBar.getValue());
					midiReader.start();
				}
			}
			public void mouseClicked(MouseEvent e) {
				if(Objects.nonNull(midiReader) && timerBar.isEnabled()) {
					midiReader.stop();
					
					int val = 0;
					final int size = timerBar.getSize().width;
					double diff =(double)timerBar.getMaximum()/(double)size;
					val = (int) Math.round(e.getX()*diff);
					timerBar.setValue(val);
					
					midiReader.setTime((long)timerBar.getValue());
					midiReader.start();
				}
			}
		};
	}
	
	private ActionListener getActionLoadMidi() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Objects.nonNull(midiReader) && Objects.nonNull(accessRes) && comboBoxListMidi.getSelectedIndex() != -1) {
					try {
						File f = accessRes.downloadMidi(comboBoxListMidi.getSelectedItem().toString());
						midiReader.setMid(f);
						setEnable(true);
					} catch (ClassNotFoundException | IOException e1) {
						e1.printStackTrace();
						JOptionPane pane = new JOptionPane("Perte de connexion", JOptionPane.ERROR_MESSAGE);
						JDialog dialog = pane.createDialog("Erreur");
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
						clean();
						listListener.stream().forEach(l -> l.deconnexion());
					}
				}
			}
		};
	}
	
	private ActionListener getActionDeconnexion() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				midiReader.stop();
				listListener.stream().forEach(l -> l.deconnexion());
			}
		};
	}
	
	private ActionListener getActionPlay() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Objects.nonNull(midiReader)) {
					midiReader.start();
				}
			}
		};
	}
	
	private ActionListener getActionPause() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Objects.nonNull(midiReader)) {
					midiReader.stop();
				}
			}
		};
	}
	
	private ChangeListener getChangeListenerTempoChange() {
		return new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(Objects.nonNull(midiReader)) {
					JSlider obj = (JSlider)e.getSource();
					float t = obj.getValue()/100f;
					lblVitesseValue.setText(""+t);
					midiReader.setTempo(t);
				}
			}
		};
	}
	
	private ChangeListener getChangeListenerTransposeChange() {
		return new ChangeListener() {
			private int val = 0;
			public void stateChanged(ChangeEvent e) {
				if(Objects.nonNull(midiReader)) {
					JSpinner obj = (JSpinner)e.getSource();
					int tmp = Integer.valueOf(obj.getValue().toString());
					int t = tmp - val;
					val = tmp;
					midiReader.setTranspose(t);
				}
			}
		};
	}
	
	private void setEnable(boolean b) {
		timerBar.setEnabled(b);
		sliderVitesse.setEnabled(b);
		spinnerTranspose.setEnabled(b);
		btnPause.setEnabled(b);
		btnPlay.setEnabled(b);
		btnMute.setEnabled(b);
	}
	
	public void updateData() {
		try {
			comboBoxListMidi.removeAllItems();
			String[] listMidi = this.accessRes.listMidi();
			for(String name:listMidi) comboBoxListMidi.addItem(name);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane pane = new JOptionPane("Perte de connexion", JOptionPane.ERROR_MESSAGE);
			JDialog dialog = pane.createDialog("Erreur");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			clean();
			listListener.stream().forEach(l -> l.deconnexion());
		}
	}
	
	public void setAccessResource(IAccessRessource accessRes) {
		this.accessRes = accessRes;
	}
	
	public void clean() {
		comboBoxListMidi.setSelectedIndex(-1);
		comboBoxListMidi.removeAllItems();
		lblTxtkaraoker.setText("");
		timerBar.setValue(0);
		spinnerTranspose.setValue(0);
		sliderVitesse.setValue(100);
		lblVitesseValue.setText("1.00");
		
		setEnable(false);
		
	}

	public void addListener(KaraokerFrameHandler handler) {
		listListener.add(handler);
	}
	
	public void removeListener(KaraokerFrameHandler handler) {
		listListener.remove(handler);
	}
	
	public void removeAllListener() {
		listListener.clear();
	}

	@Override
	public void eventText(TextKaraoker txt) {
		lblType.setText(txt.getType());
		lblTxtkaraoker.setText(txt.toString());
	}

	@Override
	public void eventTime(long t) {
		timerBar.setValue((int)t);
	}

	@Override
	public void eventTimeMax(long t) {
		timerBar.setMaximum((int)t);
	}
}
