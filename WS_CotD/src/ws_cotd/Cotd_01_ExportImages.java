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
	
	private String specificPage = "";
	
	public Cotd_01_ExportImages(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_01_ExportImages main = new Cotd_01_ExportImages();
		
		main.cleanImagesFolder();

		main.parseWsJpCotD();
		
		//main.parseWsEnCotD();
		//main.parseWsJpExtraCotd();
		
		Cotd_02b_CreateTemplateFromImages extended = new Cotd_02b_CreateTemplateFromImages();

		extended.createTemporalTemplateFile();
		
		Cotd_Utilities.openFileInNotepad(main.conf.fromImagesFile);
		Cotd_Utilities.openFileInNotepad(main.conf.fromGlobalFile);
		
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

	public void parseWsJpCotD() throws Exception {
		
		if(this.specificPage.equals("")){
			this.parseWsJpCotD(this.conf.wsJpCotdUrl);
		}
		else{
			this.parseWsJpCotD(this.specificPage);
		}
		
	}
	
	private void parseWsJpCotD(String url) throws Exception {
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();
		
		Elements images = doc.select("div.entry-content img.aligncenter");

		int count = 1;
		
		for(Element image : images){
			String imageUrl = image.attr("abs:src");
			System.out.println("Scrapping img: " + imageUrl);
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
		
		//Ws-tcg has updated their web
		
		/*Document doc = Jsoup.connect(this.conf.wsJpExtraCotdUrl).maxBodySize(0).get();
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
		}*/
	}

	public void cleanImagesFolder() throws Exception {
		
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
