package ws_cotd_web;

import java.util.ArrayList;

import ws_cotd.Cotd_01_ExportImages;
import ws_cotd_v2.Cotd_WorkingFile;

public class CotdWeb_ExtrasMain {

	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		//CotdWeb_Parser.getAbilityListFromWeb("seriesFullId");
		//CotdWeb_ImageHelper.fillMissingImagesAfterRelease_Yyt("seriesId", "yytsSeriesPageId");
		//CotdWeb_ImageHelper.createImageThumbs_FullFolders();
		//CotdWeb_ImageHelper.reEnumerateImageFiles();
		//CotdWeb_ExtrasMain.completeSeriesWithWsTcg("seriesFullId");
		//CotdWeb_PageHelper.getTemporalFromCardPages();
		//CotdWeb_ExtrasMain.completeSeriesWithWsTcgData("seriesFullId");
		
		System.out.println("*** Finished ***");

	}

	public static void completeSeriesWithWsTcgImages(String seriesFullId) throws Exception{
		
		Cotd_01_ExportImages importedMainA = new Cotd_01_ExportImages();
		importedMainA.cleanImagesFolder();
		ArrayList<String> missingCards = CotdWeb_CardListHelper.listMissingCards_BoosterPack(seriesFullId);
		CotdWeb_WstcgScrapper.getCardImagesFromWstcg(missingCards);
		
	}
	
	public static void completeSeriesWithWsTcgData(String seriesFullId) throws Exception{
		ArrayList<String> missingCards = CotdWeb_CardListHelper.listMissingCards_BoosterPack(seriesFullId);
		ArrayList<CotdWeb_Card> cards = CotdWeb_WstcgScrapper.getCardsFromWstcg(missingCards);
		Cotd_WorkingFile.createTemporalFromCards(cards);
	}
}
