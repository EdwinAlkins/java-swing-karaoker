package fr.polytech.project4a.ihm.midiUtility;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.JCheckBox;

import fr.polytech.project4a.ihm.handler.MidiHandler;

public class MidiPlayer {

	private List<MidiHandler> listener;
	private Sequencer seq;
	private Sequence sequence;
	private MidiMetaEvent metaEvent;
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;

	public MidiPlayer() {
		this.listener = new ArrayList<MidiHandler>();
		try {

			this.seq = MidiSystem.getSequencer(true);
			seq.open();
		} catch (MidiUnavailableException e) {

			e.printStackTrace();
		}

	}

	public void addHandler(MidiHandler a) {
		this.listener.add(a);
	}

	public void removeHandler() {
		this.listener.clear();
	}

	public void setMid(File file) {
		seq.stop();
		try {
			seq.close();
			this.seq = MidiSystem.getSequencer(true);
			seq.open();

			this.sequence = MidiSystem.getSequence(file);
			seq.setSequence(this.sequence);
			metaEvent = new MidiMetaEvent(listener, seq);
			seq.addMetaEventListener(metaEvent);
			listener.forEach(e -> e.eventTimeMax(seq.getMicrosecondLength()));
		} catch (MidiUnavailableException | IOException | InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		if (seq.isOpen()) {
			seq.start();

		}
	}

	public void stop() {
		if (seq.isOpen()) {
			seq.stop();
		}
	}

	public void setTempo(float newTempo) {
		if (seq.isOpen()) {
			seq.setTempoFactor(newTempo);

		}
	}

	public void setTime(long time) {
		if (seq.isOpen()) {
			seq.setMicrosecondPosition(time);

		}
	}

	public void setTranspose(int transposition) {
		if (seq.isOpen()) {
			stop();
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					MidiMessage message = event.getMessage();

					if (message instanceof ShortMessage) {
						ShortMessage sm = (ShortMessage) message;
						if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF) {
							try {
								sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() + transposition*12,
										sm.getData2());
							} catch (InvalidMidiDataException e) {
								e.printStackTrace();
							}
						}
					}

				}
				start();
			}

		}
	}

	public JCheckBox[] getBox() {
		List<JCheckBox> res = new Vector<JCheckBox>();
		Set<String> set = new HashSet<String>();
		if(seq.isOpen()) {
			set.add("humain");
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					MidiEvent event = track.get(i);
					MidiMessage message = event.getMessage();

					if(message instanceof MetaMessage) {
						MetaMessage mm = (MetaMessage)message;
						String data = new String(mm.getData());
						Pattern p = Pattern.compile("[.]*\\[([a-zA-Z_0-9]+)\\].*$");
						Matcher matcher = p.matcher(data);
						if(matcher.matches()) {
							set.add(matcher.group(1));
						}
					}
				}
			}
		}
		for(String str:set) {
			JCheckBox chb = new JCheckBox(str,true);
			chb.setActionCommand(str);
			chb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println(chb.isSelected());
					if(!chb.isSelected()) {
						metaEvent.getTxtkar().addMute(chb.getActionCommand());
					}
					else {
						metaEvent.getTxtkar().removeMute(chb.getActionCommand());
					}
					System.out.println(metaEvent.getTxtkar().getMute().toString());
				}
			});
			res.add(chb);
		}
		return (JCheckBox[]) res.toArray(new JCheckBox[res.size()]);
	}

	public void setMute(HashSet<String> mute) {
		metaEvent.getTxtkar().setMute(mute);
	}

}
