package ws_cotd_web;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ws_cotd.Cotd_Conf;
import ws_cotd_v2.Cotd_ImageHelper;

public class CotdWeb_ImageHelper {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void copyImages(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		Cotd_ImageHelper imageHelper = new Cotd_ImageHelper();
		
		for(CotdWeb_Card card : cards) {
			
			File originFile = new File(conf.imagesFolder.getAbsolutePath() + "/" + card.imageFileId + ".png");
			File targetFile = new File(conf.webFolder.getAbsolutePath() + "/" + card.seriesId + "/images/" + card.fileId + ".png");
			
			System.out.println("* Copying image: " + originFile.getName() + ", to : " + targetFile.getName());
			
			FileUtils.copyFile(originFile, targetFile);
			
			imageHelper.createImageThumb(targetFile);
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
		
		CotdWeb_IndexHelper.updateSeriesIndex_AfterRelease(seriesId, newCardFileIds, "yyt");
	}
	
	public static void fillMissingImagesAfterRelease_Wstcg(String seriesFullId) throws Exception{
		
		System.out.println("** Download Wstcg Images: Fill Set Gaps");

		ArrayList<String> cardIds = CotdWeb_CardListHelper.listMissingCards_BoosterPack(seriesFullId);
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
			
			File targetImageFile = new File(imagesFolderPath + fileId + "_wstcg.jpg");
			
			System.out.println("Saving img: " + fileId);
			FileUtils.copyURLToFile(new URL(imageUrl), targetImageFile);
			
			Thread.sleep(1000);
			count++;
		}
		
		CotdWeb_IndexHelper.updateSeriesIndex_AfterRelease(seriesId, newCardFileIds, "wstcg");
	}
	
	
	public static void createImageThumbs_FullFolders() throws Exception{
		
		Cotd_ImageHelper imageHelper = new Cotd_ImageHelper();
		
		FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
               return pathname.getName().endsWith(".png");
            }
        };
        
        HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();

		for(String seriesId : series.keySet()) {

			String seriesFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/images/";
			File seriesFolder = new File(seriesFolderPath);
			
			File[] imageFiles = seriesFolder.listFiles(filter);
	
			for(File imageFile : imageFiles){
				
				imageHelper.createImageThumb(imageFile);
	
			}
		}
	}
	
	public static void reEnumerateImageFiles(){
		
		FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
               return !pathname.getName().endsWith("_extra.png");
            }
        };
		File[] imageFiles = conf.imagesFolder.listFiles(filter);

		int count = 1;
		
		for(File imageFile : imageFiles){
			String paddedCount = String.format("%02d", count);
			String imageName = "/jp_" + paddedCount + ".png";
			File targetImageFile = new File(conf.imagesFolder.getAbsolutePath() + imageName);
			System.out.println("Renaming file: " + imageFile.getName() + " to " + imageName);
			imageFile.renameTo(targetImageFile);
			count++;
		}
		
	}
	
}
