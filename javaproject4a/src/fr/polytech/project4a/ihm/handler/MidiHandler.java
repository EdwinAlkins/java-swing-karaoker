package fr.polytech.project4a.ihm.handler;

import fr.polytech.project4a.ihm.midiUtility.TextKaraoker;

public interface MidiHandler {
	public void eventText(TextKaraoker s);
	public void eventTime(long temps);
	public void eventTimeMax(long tempsMax);
}