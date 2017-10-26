package ws_cotd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Cotd_02a_ExportOutakuyaTranslation {

	private Cotd_Conf conf;
	private String outakuyaPostUrl;
	
	public Cotd_02a_ExportOutakuyaTranslation(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		Cotd_02a_ExportOutakuyaTranslation main = new Cotd_02a_ExportOutakuyaTranslation(); 

		main.getTodaysOutakuyaUrl();
		//main.outakuyaPostUrl = "";
		
		main.exportOutakuyaTranslation();
		
		System.out.println("*** Finished ***");

	}

	private String getTodaysOutakuyaUrl() throws Exception{

		String todayUrl = "";
		
		System.out.println("** Get Today's Outakuya Url");
		
		String today = new SimpleDateFormat("yyyy/MM/dd").format(new Date()); 
		
		Document doc = Jsoup.connect(this.conf.outakuyaUrl).maxBodySize(0).get();
		Element anchor = doc.select("a[href*=" + today + "]").first();
		
		todayUrl = anchor.attr("href");
		
		System.out.println("* Today's Outakuya Url: " + todayUrl);
		
		this.outakuyaPostUrl = todayUrl;
		
		return todayUrl;
	}
	
	
	private void exportOutakuyaTranslation() throws Exception{
		
		System.out.println("** Export Outakuya Translation");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.conf.fromOutakuyaFile));
		
		Document doc = Jsoup.connect(this.outakuyaPostUrl).maxBodySize(0).get();
		Element post = doc.select("div.post_content").first();
		
		Elements contents = post.select("p");
		
		for (Element p : contents ) {
			
			writer.write(p.html());
			writer.write("\r\n");
		}

		writer.close();
	}
}
