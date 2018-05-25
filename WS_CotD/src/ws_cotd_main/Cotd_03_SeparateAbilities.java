package ws_cotd_main;

import ws_cotd.Cotd_04_CreateAbilityList;
import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;

public class Cotd_03_SeparateAbilities {
	
	private Cotd_Conf conf;
	
	public Cotd_03_SeparateAbilities(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_03_SeparateAbilities main = new Cotd_03_SeparateAbilities();
		
		Cotd_04_CreateAbilityList importedMain = new Cotd_04_CreateAbilityList();
		
		importedMain.createAbilityList();
		Cotd_Utilities.openFileInNotepad(main.conf.abilitiesFile);
		
		System.out.println("*** Finished ***");
	}
}
