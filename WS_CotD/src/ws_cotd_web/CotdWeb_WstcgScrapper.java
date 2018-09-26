package ws_cotd_web;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ws_cotd.Cotd_Conf;

public class CotdWeb_WstcgScrapper {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static ArrayList<CotdWeb_Card> getCardsFromWstcg(ArrayList<String> cardIds) throws Exception{
		
		ArrayList<CotdWeb_Card> cards = new ArrayList<CotdWeb_Card>();
		ArrayList<String> newCardFileIds = new ArrayList<String>();

		String seriesId = cardIds.get(0).split("/")[1].split("-")[0].toLowerCase();
		String imagesFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/images/";

		int count = 1;
		
		for(String cardId : cardIds) {
			
			String url = conf.wsJpCardUrl + cardId;
			
			Document doc = Jsoup.connect(url).maxBodySize(0).get();
			Element image = doc.select("td.graphic img").first();
			
			String imageUrl = image.attr("abs:src");
			System.out.println("Scrapping img: " + imageUrl);
			String paddedCount = String.format("%02d", count);

			String fileId = seriesId + "_" + cardId.split("-")[1];
			newCardFileIds.add(fileId);
			
			String imageName = "/jp_" + paddedCount + ".png";

			File targetImageFile = new File(imagesFolderPath + fileId + "_wstcg.jpg");
			
			System.out.println("Saving img: " + imageName);
			FileUtils.copyURLToFile(new URL(imageUrl), targetImageFile);
			
			Thread.sleep(1000);
			count++;
		}
		
		CotdWeb_IndexHelper.updateSeriesIndex_AfterRelease(seriesId, newCardFileIds, "wstcg");
		
		return cards;
	}
}
