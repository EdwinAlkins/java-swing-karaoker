package fr.polytech.project4a.reseau.client;

import java.io.File;
import java.io.IOException;

import fr.polytech.project4a.reseau.serveur.IStatistique;

public interface IAccessRessource {
	
	public String[] listMidi() throws IOException;
	public File downloadMidi(String fichier) throws IOException, ClassNotFoundException;
	public IStatistique getStatistique() throws ClassNotFoundException, IOException;

}
