package fr.polytech.project4a.reseau.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import fr.polytech.project4a.reseau.serveur.IStatistique;

public class Client implements IAccessRessource {
	
	private String IPServeur;
	private int port;
	private String name;
	
	public Client(String IPServeur, int port, String name) throws IOException {
		this.port = port;
		this.IPServeur = IPServeur;
		this.name = name;
	}
	
	@Override
	public String[] listMidi() throws IOException{
		SocketChannel sChannel = SocketChannel.open();
		sChannel.configureBlocking(true);
		
		String[] listMidi = new String[0];
		if (sChannel.connect(new InetSocketAddress(IPServeur, port))) {
			PrintStream ps = new PrintStream(sChannel.socket().getOutputStream());
			ps.println("ListMidi:"+name);
			
			ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
			try {
				listMidi = (String[]) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			ps.close();
			ois.close();
		}
		
		sChannel.close();
		return(listMidi);
	}

	@Override
	
	public File downloadMidi(String fichier) throws ClassNotFoundException, IOException {
		SocketChannel sChannel = SocketChannel.open();
		sChannel.configureBlocking(true);
		
        File fichierMidi = File.createTempFile("tmp", null, new File("./"));
        fichierMidi.deleteOnExit();
        System.out.println("File path: "+fichierMidi.getAbsolutePath());
		if (sChannel.connect(new InetSocketAddress(IPServeur, port))) {
			PrintStream ps = new PrintStream(sChannel.socket().getOutputStream());
			ps.println(fichier+":"+name);
			
			ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
			try {
				FileOutputStream out = new FileOutputStream(fichierMidi);
				out.write((byte[]) ois.readObject());
				out.close();
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
			ps.close();
			ois.close();
		}
		
		sChannel.close();
		return(fichierMidi);
	}

	@Override
	public IStatistique getStatistique() throws ClassNotFoundException, IOException{
		SocketChannel sChannel = SocketChannel.open();
		sChannel.configureBlocking(true);
		
		IStatistique statistique = null;
		if (sChannel.connect(new InetSocketAddress(IPServeur, port))) {
			PrintStream ps = new PrintStream(sChannel.socket().getOutputStream());
			ps.println("Statisique:"+name);
			
			ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
			try {
				statistique = (IStatistique) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			ps.close();
			ois.close();
		}
		
		sChannel.close();
		return(statistique);
	}

}
