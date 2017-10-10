package ws_cotd;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
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
		
		//main.cleanImagesFolder();
		//main.parseWsEnCotD();
		main.parseWsJpCotD();
		/*main.parseWsJpCotDExtra();
		
		Desktop desktop = Desktop.getDesktop();
		File dirToOpen = null;
		try {
			dirToOpen = new File(CotD_Conf.imagesFolder);
			desktop.open(dirToOpen);
		} catch (IllegalArgumentException iae) {
			System.out.println("File Not Found");
		}*/
		
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
		/*UserAgent userAgent = new UserAgent();
		//userAgent.settings.showHeaders = true;
		
		userAgent.sendGET(wsJpURL, "cookie: lang_setting=en");
		System.out.println("Scrapping page: " + userAgent.getLocation());
		//System.out.println("Page content: " + userAgent.doc.innerHTML());
		
		Elements divs = userAgent.doc.findEvery("<div class=\"center\">");
		int count = 0;
		for (Element div : divs) {
			count++;
			Element img = div.getFirst("<img>");
			String src = img.getAtString("src");
			System.out.println("Scrapping img: " + src);
			String fileType = src.substring(src.lastIndexOf('.') + 1);
			String paddedNumber = String.valueOf(count).replaceAll("^(\\d)$", "0$1");
			this.downloadImage(src, CotD_Conf.imagesFolder, fileType, "jp_" + paddedNumber);
		}*/
		Document doc = Jsoup.connect(this.conf.wsJpCotdUrl).maxBodySize(0).get();
		Elements images = doc.select("div.center img");

		int count = 1;
		
		for(Element image : images){
			String imageUrl = image.attr("abs:src");
			System.out.println("Scrapping img: " + image.attr("abs:src"));
			
			
			
			//FileUtils.copyURLToFile(new URL(url), target);
		}
	}
	
	/*private void parseWsJpCotDExtra() throws Exception {
		UserAgent userAgent = new UserAgent();
		//userAgent.settings.showHeaders = true;
		
		userAgent.sendGET(wsJpURLExtra, "cookie: lang_setting=en");
		System.out.println("Scrapping page: " + userAgent.getLocation());
		//System.out.println("Page content: " + userAgent.doc.innerHTML());
		
		Elements divs = userAgent.doc.findEvery("<div class=\"center\">");
		int count = 0;
		for (Element div : divs) {
			count++;
			Element img = div.getFirst("<img>");
			String src = img.getAtString("src");
			System.out.println("Scrapping img: " + src);
			String fileType = src.substring(src.lastIndexOf('.') + 1);
			this.downloadImage(src, CotD_Conf.imagesFolder, fileType, "jp_" + String.valueOf(count) + "_2");
		}
	}
	
	private void downloadImage(String url, String where, String what, String how) throws Exception{
		
		try {
		    Thread.sleep(100);
		} catch ( java.lang.InterruptedException ie) {
		    System.out.println(ie);
		}
		
		HandlerForBinary handlerForBinary = new HandlerForBinary();
		UserAgent userAgent = new UserAgent();
		
		String handlerType = "image/";
		
		if(what.equals("jpg")){
			handlerType = handlerType + "jpeg";
		}else{
			handlerType = handlerType + what;
		}
		
		try{
			userAgent.setHandler(handlerType, handlerForBinary);
			userAgent.visit(url);
			
			System.out.println("Extracting image " + how + " from: " + url);
			
			FileOutputStream output = null;
			output = new FileOutputStream(where + "\\" + how + "." + what);
			output.write(handlerForBinary.getContent());
			output.close();
		}
		catch(Exception ex){
			System.out.println("Link for image: " + url + " is broken.");
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
*/
}
