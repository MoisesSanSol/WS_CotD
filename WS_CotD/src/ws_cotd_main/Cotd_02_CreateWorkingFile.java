package ws_cotd_main;

import ws_cotd.Cotd_03b_CreateTemporalFileFromImages;
import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;
import ws_cotd_v2.Cotd_ExtraFeatures;
import ws_cotd_v2.Cotd_FromFreedom;

public class Cotd_02_CreateWorkingFile {

	private final static boolean useGlobalFile = true;
	private final static boolean useFreedomFile = false;
	
	private static Cotd_Conf conf = Cotd_Conf.getInstance();;

	public static void main(String[] args) throws Exception {
	
		System.out.println("*** Starting ***");
		
		if(!useFreedomFile){
			Cotd_02_CreateWorkingFile main = new Cotd_02_CreateWorkingFile();
			
			Cotd_ExtraFeatures.insertSeparatorsInFromGlobal();
			Cotd_ExtraFeatures.transferSeriesSeparationFromGlobalToImages();
			
			Cotd_03b_CreateTemporalFileFromImages importedMain = new Cotd_03b_CreateTemporalFileFromImages();
			importedMain.generateTemporalFileFromImages(useGlobalFile);
		}	
		else{
			Cotd_FromFreedom.generateTemporalFile_FromFreedom();
		}
		Cotd_Utilities.openFileInNotepad(conf.temporalFile);
		
		System.out.println("*** Finished ***");
		
	}
}
