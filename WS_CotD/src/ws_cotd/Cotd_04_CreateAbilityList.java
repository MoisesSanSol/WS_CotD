package ws_cotd;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cotd_04_CreateAbilityList {
	
	private Cotd_Conf conf;
	
	public Cotd_04_CreateAbilityList(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_04_CreateAbilityList main = new Cotd_04_CreateAbilityList();
		
		main.createAbilityList();
		Cotd_Utilities.openFileInNotepad(main.conf.abilitiesFile);
		
		System.out.println("*** Finished ***");
	}
	
	public void createAbilityList() throws Exception{
	
		System.out.println("** Generate Abilities File");
		
		ArrayList<String> abilities = new ArrayList<String>();
		ArrayList<String> abilitiesContent = new ArrayList<String>();
		abilitiesContent.add("LÚL"); //Forcing notepad++ to use actual UTF-8 encoding
		
		List<String> temporalContent = new ArrayList<>(Files.readAllLines(conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		
		temporalContent.remove(0);
		
		for (String contentLine : temporalContent){
			
			if(contentLine.startsWith("*")){
				abilities.add(contentLine);
			}
			else{
				// Do nothing
			}
		}
		
		Collections.sort(abilities);
		
		for (String ability : abilities){
			abilitiesContent.add(ability);
			abilitiesContent.add("");
			abilitiesContent.add("");
		}
		
		Files.write(conf.abilitiesFile.toPath(), abilitiesContent, StandardCharsets.UTF_8);
	}
}
