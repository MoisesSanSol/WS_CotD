package ws_cotd_main;

import ws_cotd.Cotd_01_ExportImages;
import ws_cotd.Cotd_02b_CreateTemplateFromImages;
import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;
import ws_cotd_v2.Cotd_FromGlobal;

public class Cotd_01_PrepareFiles {

	private Cotd_Conf conf;

	public Cotd_01_PrepareFiles(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_01_PrepareFiles main = new Cotd_01_PrepareFiles();
		
		Cotd_FromGlobal.createTemplate();
		
		//Cotd_Utilities.openFileInNotepad(main.conf.fromImagesFile);
		Cotd_Utilities.openFileInNotepad(main.conf.fromGlobalFile);
		
		Cotd_Utilities.openDefaultFolders();
		
		System.out.println("*** Finished ***");
	}
	
}
