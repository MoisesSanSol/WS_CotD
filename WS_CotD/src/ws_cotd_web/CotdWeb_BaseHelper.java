package ws_cotd_web;

import java.io.File;
import java.util.HashMap;

import ws_cotd.Cotd_Conf;

public class CotdWeb_BaseHelper {
	
	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void prepareNewSeries() throws Exception{
		
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries_Base();
		
		for(String seriesFullId : series.keySet()){

			String seriesId = seriesFullId.split("/")[1].toLowerCase();
			
			String seriesFolderPath = conf.webFolder.getAbsolutePath() + "\\" + seriesId + "\\";
			File seriesFolder = new File(seriesFolderPath);
			if(!seriesFolder.exists()){
				System.out.println("* New Series: " + seriesId);
				seriesFolder.mkdir();
				String cardsFolderPath = seriesFolderPath + "cards";
				File cardsFolder = new File(cardsFolderPath);
				cardsFolder.mkdir();
				String cardsFolderPath = seriesFolderPath + "cardsbydate";
				File cardsFolder = new File(cardsFolderPath);
				cardsFolder.mkdir();
				String imagesFolderPath = seriesFolderPath + "images";
				File imagesFolder = new File(imagesFolderPath);
				imagesFolder.mkdir();
				
				CotdWeb_IndexHelper.createEmptyIndex(seriesFullId, series.get(seriesFullId));
			}
		}
		
	}

}
