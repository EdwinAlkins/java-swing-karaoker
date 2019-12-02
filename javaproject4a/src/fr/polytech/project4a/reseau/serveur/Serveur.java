package fr.polytech.project4a.reseau.serveur;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Serveur {
	public static final int port = 9876;
	
	private ServerSocketChannel ssChannel;
	private HashMap<String, Serializable> mapRmi;

	public Serveur() throws IOException {
		this(new HashMap<String, Serializable>());
		
	}
	
	public Serveur(HashMap<String, Serializable> mapRmi) throws IOException {
		this.ssChannel = ServerSocketChannel.open();
		this.mapRmi = mapRmi;
		
		ssChannel.configureBlocking(true);
		ssChannel.socket().bind(new InetSocketAddress(port));
	}
	
	public void configServeur(int newport) {
		try {
			ssChannel.close();
			this.ssChannel = ServerSocketChannel.open();
			ssChannel.configureBlocking(true);
			ssChannel.socket().bind(new InetSocketAddress(newport));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void bind(String key, Serializable obj) {
		this.mapRmi.put(key, obj);
	}
	
	public void run() throws IOException {
		System.out.println("Serveur is running");
		while (true) {
			SocketChannel sChannel = ssChannel.accept();

			System.out.println();
			debug(sChannel, "new client");
			Thread t = new ClientTraitement(sChannel,mapRmi);
			t.setDaemon(true);
	        t.start();
		}
	}
	
	public void stop() throws IOException {
		ssChannel.close();
	}
	
	static void debug(SocketChannel sChannel, String msg) {
		if(Objects.nonNull(sChannel)) {
			StringBuffer buff = new StringBuffer();
			buff.append("[")
			.append(sChannel.socket().getInetAddress().getHostAddress().toString())
			.append('/').append(sChannel.socket().getPort()).append("] ").append(msg);
			System.out.println(buff.toString());
		}
	}
	
	public static void main(String[] args) {
		try {
			Serveur serveur = new Serveur();
			bindServeur(serveur);
			
			serveur.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void bindServeur(Serveur serveur) {
		File dir = new File("./resources/");
		File[] listF = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getPath().endsWith("mid");
			}
		});
		ArrayList<String> listMidi = new ArrayList<String>();
		for(File currentFile:listF) {
			String name = currentFile.getName().replaceAll(".mid", "");
			try {
				FileInputStream fileInputStream = new FileInputStream(currentFile);
				byte[] data = fileInputStream.readAllBytes();
				serveur.bind(name, data);
				listMidi.add(name);
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		serveur.bind("ListMidi", (String[]) listMidi.toArray(new String[listMidi.size()]));
		serveur.bind("Statisique", (IStatistique)Statistique.getInstance());
	}
}