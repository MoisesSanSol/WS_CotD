package ws_cotd_main;

import ws_cotd_v2.Cotd_ExtraFeatures;

public class Cotd_05_ExtraFeatures {

	boolean flowControl_FillInPowers = true;
	boolean flowControl_UpdateNames = true;
	boolean flowControl_DefaultTraits = true;
	boolean flowControl_CheckCards = true;
	boolean flowControl_RenumberParallels = false;
	boolean flowControl_RenameParallels = false;
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_05_ExtraFeatures main = new Cotd_05_ExtraFeatures();
		
		if(main.flowControl_FillInPowers){
			Cotd_ExtraFeatures.fillPowerFromWsblog();
		}
		
		if(main.flowControl_UpdateNames){
			Cotd_ExtraFeatures.updateTemporalNamesFromFile();
		}
		
		if(main.flowControl_DefaultTraits){
			Cotd_ExtraFeatures.updateDefaultTraits();
		}
		
		if(main.flowControl_CheckCards){
			Cotd_ExtraFeatures.checkCardsMissingFillInParts();
		}
		
		if(main.flowControl_RenumberParallels){
			Cotd_ExtraFeatures.updateParallelImageFileNumbers();			
		}

		if(main.flowControl_RenameParallels){
			Cotd_ExtraFeatures.updateParallelImageFileNames();			
		}
		
		System.out.println("*** Finished ***");
	}
	
}
