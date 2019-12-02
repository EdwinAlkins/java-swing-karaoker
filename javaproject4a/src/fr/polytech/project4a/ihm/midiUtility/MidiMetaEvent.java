package fr.polytech.project4a.ihm.midiUtility;

import java.util.List;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequencer;

import fr.polytech.project4a.ihm.handler.MidiHandler;

public class MidiMetaEvent implements MetaEventListener {

	private List<MidiHandler> listener;
	private Sequencer seq;
	private TextKaraoker txtkar;

	public MidiMetaEvent(List<MidiHandler> listener, Sequencer seq) {
		this.listener = listener;
		this.seq = seq;
		this.txtkar = new TextKaraoker();
	}

	@Override
	public void meta(MetaMessage meta) {
		if(seq.getMicrosecondPosition() > 500 && meta.getType() == 1) {
			String newtext = new String(meta.getData());
			this.txtkar.matche(newtext);
			this.listener.forEach(e -> {
				e.eventText(this.txtkar);
				e.eventTime(seq.getMicrosecondPosition());
			});
		} else if(seq.getMicrosecondPosition() > 500 && meta.getType() == 6) {
			String type = new String(meta.getData());
			this.txtkar.setType(type);
		}
	}

	public TextKaraoker getTxtkar() {
		return txtkar;
	}
}
