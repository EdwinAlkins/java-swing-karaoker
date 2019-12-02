package fr.polytech.project4a.ihm.midiUtility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextKaraoker{
		private HashMap<String, String> mapVoix;
		private HashMap<String, String> mapColor;
		private HashSet<String> mute;
		private Pattern pattern;
		private String pDebut;
		private String type;
		
		public TextKaraoker() {
			mapVoix = new HashMap<String, String>();
			mapColor = new HashMap<String, String>();
			mute = new HashSet<String>();
			type = "normal";
			pattern = Pattern.compile("[/|\\\\|\n|\r]*\\[([a-zA-Z_0-9]+)\\](.*)$");
			pDebut = ".*[/|\\\\|\n|\r].*";
		}
		public void matche(String str) {
			/*str=str.replace("ÃƒÆ’Ã‚Â§ÃƒÆ’Ã‚Â ", "ça");
			str=str.replace("ÃƒÆ’Ã‚Â©", "é");
			str=str.replace("ÃƒÆ’Ã‚Â", "à");
			str=str.replace("ÃƒÂ¨", "è");
			str=str.replace("Ãª", "ê");
			if(str.contains(new String(new char[]{13}))) str = "/";
			if(str.isEmpty())str = "/";*/
			Pattern.compile("^\\(\\)$").matcher(str);
			Matcher m = pattern.matcher(str);
			if(m.matches()) {
				String txt;
				if(str.matches(pDebut)) txt = "";
				else txt = mapVoix.getOrDefault(m.group(1), "");
				mapVoix.put(m.group(1), txt+m.group(2));
			} else {
				m = Pattern.compile("[/|\\\\|\n|\r]*([a-zA-Z_0-9\\ èéçàâêîô',\\.]*)").matcher(str);
				if(m.matches()) {
					String txt;
					if(str.matches(pDebut)) txt = "";
					else txt = mapVoix.getOrDefault("humain", "");
					mapVoix.put("humain", txt+m.group(1));
				}
			}
		}
		
		public void setMute(HashSet<String> mute) {
			this.mute = mute;
		}
		
		public void addMute(String str) {
			this.mute.add(str);
			
		}
		public void removeMute(String str) {
			this.mute.remove(str);
		}
		
		public Set<String> getMute(){
			return this.mute;
		}
		
		public String getType() {
			return type;
		}
		@Override
		public String toString() {
			StringBuffer buff = new StringBuffer();
			buff.append("<html>");
			for(Entry<String, String> entry:mapVoix.entrySet()) {
				if(!entry.getValue().isEmpty() && !mute.contains(entry.getKey())) {
					String color = "";
					if(!mapColor.containsKey(entry.getKey())) mapColor.put(entry.getKey(),"#"+String.format("%06x", new Random().nextInt()));
					color = mapColor.get(entry.getKey());
					buff.append("<p style=\"color:").append(color).append(";\">")
					.append(entry.getKey()).append(" : ")
					.append(entry.getValue()).append("</p>");
				}
			}
			buff.append("</html>");
			return buff.toString();
		}
		public void setType(String type) {
			this.type = type;
		}
	}