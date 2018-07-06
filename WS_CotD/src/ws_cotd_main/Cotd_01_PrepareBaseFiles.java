package ws_cotd_main;

import ws_cotd.Cotd_01_ExportImages;
import ws_cotd.Cotd_02b_CreateTemplateFromImages;
import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;
import ws_cotd_v2.Cotd_FromGlobal;
import ws_cotd_v2.Cotd_ImageHelper;

public class Cotd_01_PrepareBaseFiles {

	boolean flowControl_DownloadImages = true;
	boolean flowControl_OpenFiles = true;
	
	String alternativeUrl = ""; 
	
	private Cotd_Conf conf;

	public Cotd_01_PrepareBaseFiles(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_01_PrepareBaseFiles main = new Cotd_01_PrepareBaseFiles();

		// Previous version used until cleanup
		Cotd_01_ExportImages importedMainA = new Cotd_01_ExportImages();
		Cotd_02b_CreateTemplateFromImages importedMainB = new Cotd_02b_CreateTemplateFromImages();
		
		if(main.flowControl_DownloadImages){
			importedMainA.cleanImagesFolder();
			if(main.alternativeUrl.isEmpty()){
				importedMainA.parseWsJpCotD();
			}
			else{
				if(main.alternativeUrl.contains("products")){
					Cotd_ImageHelper imageHelper = new Cotd_ImageHelper();
					imageHelper.targetUrl = main.alternativeUrl;
					imageHelper.parseWsJpProductPage();
				}
			}
		}
		importedMainB.createTemporalTemplateFile();
		
		// Previous version used until cleanup - End
		
		Cotd_FromGlobal.createTemplate();
		
		// Automated file and folder opening
		
		Cotd_Utilities.openFileInNotepad(main.conf.fromGlobalFile);
		Cotd_Utilities.openDefaultFolders();
		
		System.out.println("*** Finished ***");
	}
	
}
