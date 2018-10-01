package ws_english;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import ws_cotd.Cotd_Conf;

public class Poc {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void main(String[] args) throws Exception {
		
		String url = "https://en.ws-tcg.com/cardlist/list/?cardno=MM/W17-E";
		ArrayList<String> temporalContent = new ArrayList<String>();
		
		for(int i = 1; i <= 100; i++){
		
			String paddedCount = String.format("%03d", i);
			
			Document doc = Jsoup.connect(url + paddedCount).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").maxBodySize(0).get();
			doc.outputSettings().prettyPrint(false);
			//System.out.println(doc.html());

			Element abilitiesTh = doc.select("th:contains(text)").first();
			Element abilitiesTd = abilitiesTh.nextElementSibling();
			String abilities = Jsoup.clean(abilitiesTd.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
			temporalContent.add("# MM/W17-E" + paddedCount);
			temporalContent.add(abilities);
			
		}
		
		String reverseTemporalPath = conf.mainFolder.getAbsolutePath() + "/POC.txt";
		File reverseTemporal = new File(reverseTemporalPath);
		Files.write(reverseTemporal.toPath(), temporalContent, StandardCharsets.UTF_8);
	}
	
}
