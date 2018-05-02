package ws_cotd_web;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import ws_cotd.Cotd_Conf;

public class CotdWeb_ImageHelper {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void copyImages(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		for(CotdWeb_Card card : cards) {
			
			File originFile = new File(conf.imagesFolder.getAbsolutePath() + "/" + card.imageFileId + ".png");
			File targetFile = new File(conf.webFolder.getAbsolutePath() + "/" + card.seriesId + "/images/" + card.fileId + ".png");
			
			System.out.println("* Copying image: " + originFile.getName() + ", to : " + targetFile.getName());
			
			FileUtils.copyFile(originFile, targetFile);
		}
	}
}
