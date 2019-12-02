package fr.polytech.project4a.reseau.serveur;

import java.io.Serializable;

public interface IStatistique extends Serializable{

	String getUser();

	int getStatMusic(String music);

}