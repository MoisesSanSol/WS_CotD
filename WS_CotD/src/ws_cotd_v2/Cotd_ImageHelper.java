package ws_cotd_v2;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ws_cotd.Cotd_Conf;

public class Cotd_ImageHelper {

	public String targetUrl = "";

	private Cotd_Conf conf;
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_ImageHelper main = new Cotd_ImageHelper();
		main.parseWsJpProductPage();
		
		System.out.println("*** Finished ***");
	}
	
	public Cotd_ImageHelper(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public void parseWsJpProductPage() throws Exception {
		
		Document doc = Jsoup.connect(this.targetUrl).maxBodySize(0).get();
		
		Elements images = doc.select("div.section img");

		int count = 1;
		
		for(Element image : images){
			String imageUrl = image.attr("abs:src");
			System.out.println("Scrapping img: " + imageUrl);
			if(!imageUrl.contains("supply")){
				String paddedCount = String.format("%02d", count);
				String imageName = "/jp_" + paddedCount + ".png";
				File targetImageFile = new File(this.conf.imagesFolder.getAbsolutePath() + imageName);
				System.out.println("Saving img: " + imageName);
				FileUtils.copyURLToFile(new URL(imageUrl), targetImageFile);
				Thread.sleep(1000);
				count++;
			}
		}
	}
	
}
