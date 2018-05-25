package ws_cotd_main;

import ws_cotd.Cotd_05_ReplaceAbilities;
import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;

public class Cotd_04_MergeAbilities {
	private Cotd_Conf conf;
	
	public Cotd_04_MergeAbilities(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_04_MergeAbilities main = new Cotd_04_MergeAbilities();
		
		Cotd_05_ReplaceAbilities importedMain = new Cotd_05_ReplaceAbilities();
		
		importedMain.replaceAbility();
		Cotd_Utilities.openFileInNotepad(main.conf.temporalFile);
		
		System.out.println("*** Finished ***");
	}
}
