package fr.polytech.project4a.reseau.serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Objects;

public class ClientTraitement extends Thread {
	
	private static final String REGEX_SEPARATOR = ":";
	private SocketChannel sChannel;
	private HashMap<String, Serializable> mapRmi;

	public ClientTraitement(SocketChannel sChannel, HashMap<String, Serializable> mapRmi) {
		this.sChannel = sChannel;
		this.mapRmi = mapRmi;
	}

	@Override
	public void run() {
		if(Objects.nonNull(sChannel) && sChannel.isConnected()) {
			Serveur.debug(sChannel, "connexion open");
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(sChannel.socket().getInputStream()));
				String line = in.readLine();
				String[] splitLine = line.split(REGEX_SEPARATOR);
				Serveur.debug(sChannel, "Object:" + splitLine.toString() + " as send");
				
				if(!splitLine[0].equals("ListMidi") && !splitLine[0].equals("Statisique")) {
					Statistique.getInstance().increment(splitLine[1], splitLine[0]);
				}
				
				ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
				oos.writeObject(mapRmi.getOrDefault(splitLine[0], "unknow"));
				
				in.close();
				oos.close();
				sChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Serveur.debug(sChannel, "connexion close");
		}
	}
}
