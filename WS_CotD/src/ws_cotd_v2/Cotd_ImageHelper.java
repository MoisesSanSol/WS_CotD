package ws_cotd_v2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

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
		
		//Cotd_ImageHelper main = new Cotd_ImageHelper();
		
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
	
	public void parseWsJpExtraCotdPage() throws Exception {

		System.out.println("Parsing Jp Ws-Tcg Extra Cotd Page: " + this.conf.wsJpExtraCotdUrl);

		Document doc = Jsoup.connect(this.conf.wsJpExtraCotdUrl).maxBodySize(0).get();
		
		Cotd_Out.println(doc.outerHtml());
		
		Elements images = doc.select("div.entry-content img.aligncenter");

		int count = 1;
		
		for(Element image : images){
			
			String imageUrl = image.attr("abs:src");
			System.out.println("Scrapping img: " + imageUrl);
			
			String paddedCount = String.format("%02d", count);
			String imageName = "/jp_" + paddedCount + "_extra.png";
			File targetImageFile = new File(this.conf.imagesFolder.getAbsolutePath() + imageName);
			
			System.out.println("Saving img: " + imageName);
			FileUtils.copyURLToFile(new URL(imageUrl), targetImageFile);
			
			Thread.sleep(1000);
			count++;
		}
	}
	
	
	public void createImageThumb(File originalImage) throws Exception{

		BufferedImage bufferedImage = ImageIO.read(originalImage);
		
		BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
				bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
		
		String thumbPath = originalImage.getAbsolutePath().replaceAll("\\.png$", ".jpg");
		
		File thumbFile = new File(thumbPath);
		
		ImageIO.write(newBufferedImage, "jpg", thumbFile);
		Cotd_Out.println("Image " + originalImage.getName() + " resized to " + thumbFile.getName());
	}
	
}
