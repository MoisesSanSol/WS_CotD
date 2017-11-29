package ws_cotd;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cotd_05_ReplaceAbilities {

	private Cotd_Conf conf;
	
	public Cotd_05_ReplaceAbilities(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_05_ReplaceAbilities main = new Cotd_05_ReplaceAbilities();
		
		main.replaceAbility();
		Cotd_Utilities.openFileInNotepad(main.conf.resultFile);
		
		System.out.println("*** Finished ***");
	}
	
	private void replaceAbility() throws Exception{
		
		System.out.println("** Replace Abilities -> Results File");
		
		List<String> abilitiesContent = new ArrayList<>(Files.readAllLines(conf.abilitiesFile.toPath(), StandardCharsets.UTF_8));
		List<String> temporalContent = new ArrayList<>(Files.readAllLines(conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		
		HashMap<String,String> abilityPairs = new HashMap<String,String>(); 
		
		while(!abilitiesContent.isEmpty()){
			
			String ability = abilitiesContent.remove(0);
			String translation = abilitiesContent.remove(0);
			abilitiesContent.remove(0); // Line jump separator
			abilityPairs.put(ability, translation);
		}
		
		for(int i = 0; i < temporalContent.size(); i++){
			
			String contentLine = temporalContent.get(i);
			String translation = abilityPairs.get(contentLine);
			if(translation != null){
				temporalContent.set(i, translation);
			}
		}
		
		Files.write(conf.resultFile.toPath(), temporalContent, StandardCharsets.UTF_8);
	}
	
}
