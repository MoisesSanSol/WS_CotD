package ws_cotd_web;

import java.util.ArrayList;

import ws_cotd.Cotd_01_ExportImages;

public class CotdWeb_ExtrasMain {

	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		//CotdWeb_Parser.getAbilityListFromWeb("");
		//CotdWeb_ImageHelper.fillMissingImagesAfterRelease_Yyt("seriesId", "yytsSeriesPageId");
		//CotdWeb_ImageHelper.createImageThumbs_FullFolders();
		//CotdWeb_ImageHelper.reEnumerateImageFiles();
		//CotdWeb_ExtrasMain.completeSeriesWithWsTcg("seriesFullId");
		//CotdWeb_PageHelper.getTemporalFromCardPages();
		
		System.out.println("*** Finished ***");

	}

	public static void completeSeriesWithWsTcg(String seriesFullId) throws Exception{
		
		Cotd_01_ExportImages importedMainA = new Cotd_01_ExportImages();
		importedMainA.cleanImagesFolder();
		ArrayList<String> missingCards = CotdWeb_CardListHelper.listMissingCards_BoosterPack(seriesFullId);
		CotdWeb_WstcgScrapper.getCardsFromWstcg(missingCards);
		
	}
	
}
