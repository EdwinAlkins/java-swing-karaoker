package fr.polytech.project4a.reseau.serveur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Statistique implements IStatistique {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4204269113366037760L;
	private static Statistique instance = null;
	private static final File saveMapStat = new File("./resources/mapStat.ser");
	private static final File saveMapUser = new File("./resources/mapUser.ser");
	private HashMap<String, Integer> mapStat;
	private HashMap<String, Integer> mapUser;

	private Statistique() {
		this.mapStat = load(saveMapStat);
		this.mapUser = load(saveMapUser);
	}

	public static Statistique getInstance() {
		if (instance == null)
			instance = new Statistique();
		return instance;
	}
	
	public void increment(String user, String music) {
		if (mapUser.containsKey(user)) {
			int newVal = mapUser.get(user)+1;
			mapUser.replace(user, newVal);
			
		} else {
			mapUser.put(user, 1);
		}
		
		if (mapStat.containsKey(music)) {
			int newValMusic = mapStat.get(music)+1;
			mapStat.replace(music, newValMusic);
		}
		else {
			mapStat.put(music, 1);
		}
		saveAll();
	}
	
	@Override
	public String getUser() {
		/*String user = "";
		int val = 0;
		
		for (Entry<String, Integer> entry : mapUser.entrySet()) {
			if (entry.getValue()> val) {
				user = entry.getKey();
				val =  entry.getValue();
			} 
			
		}
		return user;*/
		return Collections.max(mapUser.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
	}
	
	@Override
	public int getStatMusic(String music) {
		return mapStat.getOrDefault(music, 0);
		
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Integer> load(File file){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if(file.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				map = (HashMap<String, Integer>)ois.readObject();
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	private void saveAll() {
		save(mapStat, saveMapStat);
		save(mapUser, saveMapUser);
	}
	private void save(HashMap<String, Integer> map, File file) {
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(map);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		return "Statistique [mapStat=" + mapStat + ", mapUser=" + mapUser + "]";
	}
}	