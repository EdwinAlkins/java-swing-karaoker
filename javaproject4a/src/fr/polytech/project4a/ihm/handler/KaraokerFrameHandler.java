package fr.polytech.project4a.ihm.handler;

import fr.polytech.project4a.reseau.client.IAccessRessource;

public interface KaraokerFrameHandler {

	void connexion(IAccessRessource client);
	void deconnexion();
}
