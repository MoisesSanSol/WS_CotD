package ws_cotd;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Cotd_01_ExportImages {

	private Cotd_Conf conf;
	
	public Cotd_01_ExportImages(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_01_ExportImages main = new Cotd_01_ExportImages();
		
		main.cleanImagesFolder();
		//main.parseWsEnCotD();
		main.parseWsJpCotD();
		main.parseWsJpExtraCotd();
		
		Cotd_Utilities.openDefaultFolders();
		
		System.out.println("*** Finished ***");
	}

	/*private void parseWsEnCotD() throws Exception {
		UserAgent userAgent = new UserAgent();
		userAgent.visit(wsEnURL);
		System.out.println("Scrapping page: " + userAgent.getLocation());
		//System.out.println("Page content: " + userAgent.doc.innerHTML());

		Elements imgs = userAgent.doc.findEvery("<img src=.+today.+>");
		int count = 0;
		for (Element img : imgs) {
			count++;
			if(img.getAtString("style").equals("")){
				String imgUrl = img.getAtString("src");
				System.out.println("Scrapping img: " + img.getAtString("src"));
				String imgType = imgUrl.replaceAll(".+?\\..+?\\.", "");
				this.downloadImage(imgUrl, CotD_Conf.imagesFolder, imgType, "en_" + String.valueOf(count));
			}
		}
	}*/

	private void parseWsJpCotD() throws Exception {
		Document doc = Jsoup.connect(this.conf.wsJpCotdUrl).maxBodySize(0).get();
		Elements images = doc.select("div.center img");

		int count = 1;
		
		for(Element image : images){
			String imageUrl = image.attr("abs:src");
			System.out.println("Scrapping img: " + image.attr("abs:src"));
			String paddedCount = String.format("%02d", count);
			String imageName = "/jp_" + paddedCount + ".png";
			File targetImageFile = new File(this.conf.imagesFolder.getAbsolutePath() + imageName);
			System.out.println("Saving img: " + imageName);
			FileUtils.copyURLToFile(new URL(imageUrl), targetImageFile);
			Thread.sleep(1000);
			count++;
		}
	}
	
	private void parseWsJpExtraCotd() throws Exception {
		Document doc = Jsoup.connect(this.conf.wsJpExtraCotdUrl).maxBodySize(0).get();
		Elements images = doc.select("div.center img");

		int count = 1;
		
		for(Element image : images){
			String imageUrl = image.attr("abs:src");
			System.out.println("Scrapping img: " + image.attr("abs:src"));
			String paddedCount = String.format("%02d", count);
			String imageName = "/jp_" + paddedCount + "_2.png";
			File targetImageFile = new File(this.conf.imagesFolder.getAbsolutePath() + imageName);
			System.out.println("Saving img: " + imageName);
			try{
				FileUtils.copyURLToFile(new URL(imageUrl), targetImageFile);
			}
			catch(Exception any){
				System.out.println("Lol: " + any.toString());
			}
			Thread.sleep(1000);
			count++;
		}
	}

	private void cleanImagesFolder() throws Exception {
		
		File[] listOfFiles = this.conf.imagesFolder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {

				boolean success = file.delete();
		
				if (success) {
					System.out.println("Deleted file " + file.getName() + ".");
				}
				else{
					System.out.println("Error deleting file " + file.getName() + ".");
				}
		    }
		}
	}
}
