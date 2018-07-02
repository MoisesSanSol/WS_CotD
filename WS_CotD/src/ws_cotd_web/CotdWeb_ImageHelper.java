package ws_cotd_web;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	
	public static void fillMissingImagesAfterRelease_Yyt(String seriesId, String yytSetName) throws Exception{
		
		System.out.println("** Download Yuyutei Images: Fill Set Gaps");

		ArrayList<String> newCardFileIds = new ArrayList<String>();
		
		String yuyuteiBaseUrl = "http://yuyu-tei.jp/";
		String setUrl = yuyuteiBaseUrl + "game_ws/sell/sell_price.php?ver=" + yytSetName;
		
		Document doc = Jsoup.connect(setUrl).maxBodySize(0).get();
		
		Elements cards = doc.select("[class^=card_unit]");

		for(Element card : cards){
			
			String rarity = card.className().replace("card_unit rarity_", "");
			
			Element name = card.select(".id").first();
			String cardId = name.text().replace("/", "_");

			if(rarity.startsWith("S-")){
				cardId = cardId + "-S";
			}
			
			Element img = card.select("img").first();
			String imgSrc = img.attr("src");
			
			String imgThumbUrl = yuyuteiBaseUrl + imgSrc;
			String imgUrl = imgThumbUrl.replace("90_126", "front");
			
			/*System.out.println(rarity);
			System.out.println(cardId);
			System.out.println(imgSrc);*/
			
			String fileId = seriesId + "_" + cardId.split("-")[1];
			
			
			String imagesFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/images/";
			File currentImageFile = new File(imagesFolderPath + fileId + ".png");
			
			if(currentImageFile.exists()){
				System.out.println("Image already exists: " + cardId);
			}
			else{
				System.out.println("Downloading image for: " + cardId);
				newCardFileIds.add(fileId);
				File imageFile = new File(imagesFolderPath + fileId + "_yyt.jpg");
				FileUtils.copyURLToFile(new URL(imgUrl), imageFile);
				Thread.sleep(1000);
			}
		}
		
		CotdWeb_IndexHelper.updateSeriesIndex_AfterRelease(seriesId, newCardFileIds);
	}
}
