package ws_cotd_main;

import ws_cotd.Cotd_03b_CreateTemporalFileFromImages;
import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;
import ws_cotd_v2.Cotd_ExtraFeatures;

public class Cotd_02_CreateWorkingFile {

	private final static boolean useGlobalFile = true;
	
	private Cotd_Conf conf;

	public Cotd_02_CreateWorkingFile(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
	
		System.out.println("*** Starting ***");
		
		Cotd_02_CreateWorkingFile main = new Cotd_02_CreateWorkingFile();
		
		Cotd_ExtraFeatures.insertSeparatorsInFromGlobal();
		Cotd_ExtraFeatures.transferSeriesSeparationFromGlobalToImages();
		
		Cotd_03b_CreateTemporalFileFromImages importedMain = new Cotd_03b_CreateTemporalFileFromImages();
		importedMain.generateTemporalFileFromImages(useGlobalFile);
		
		Cotd_Utilities.openFileInNotepad(main.conf.temporalFile);
		
		System.out.println("*** Finished ***");
		
	}
}
