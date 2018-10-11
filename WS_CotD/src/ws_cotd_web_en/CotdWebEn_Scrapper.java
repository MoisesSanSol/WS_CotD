package ws_cotd_web_en;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class CotdWebEn_Scrapper {

	public static ArrayList<CotdWebEn_Series> getSeriesList() throws Exception{
		
		ArrayList<CotdWebEn_Series> series = CotdWebEn_Scrapper.getSeriesBaseInfo();
		
		series = CotdWebEn_Scrapper.updateSeriesDetails(series);
		
		return series;
		
	}
	
	public static ArrayList<CotdWebEn_Series> getSeriesBaseInfo() throws Exception{
		
		ArrayList<CotdWebEn_Series> series = new ArrayList<CotdWebEn_Series>();
		
		Document doc = Jsoup.connect(CotdWebEn_LocalConf.wstcgen_allseries_url).maxBodySize(0).get();
		//System.out.println(doc.html());
		
		Elements headers = doc.select("h3");
		
		for(Element header : headers){
			
			//System.out.println(header.text());
			Elements seriesAnchors = header.nextElementSibling().select("li a");
			
			for(Element anchor : seriesAnchors){
			
				CotdWebEn_Series serie = new CotdWebEn_Series();
				
				serie.product = header.text();
				serie.name = anchor.text();
				//System.out.println(anchor.text());
				serie.wstcgId = anchor.attr("onclick").replaceAll(".+?'(\\d+)'.+", "$1");
				//System.out.println(anchor.attr("onclick").replaceAll(".+?'(\\d+)'.+", "$1"));
				
				series.add(serie);	
			}
			
		}
		
		return series;
	}
	
	public static ArrayList<CotdWebEn_Series> updateSeriesDetails(ArrayList<CotdWebEn_Series> series) throws Exception{
		
		for(CotdWebEn_Series serie : series){
			System.out.println(serie.name);
			System.out.println(serie.product);
			CotdWebEn_Scrapper.updateSeriesDetails(serie);
		}
		
		return series;
	}
	
	public static CotdWebEn_Series updateSeriesDetails(CotdWebEn_Series series) throws Exception{
		
		Document initialDoc = Jsoup.parse(CotdWebEn_Scrapper.getSeriesPageResponse(series.wstcgId, "1"));
		
		Element firstCardAnchor = initialDoc.select("a[href*=cardno]").first();
		String cardId = firstCardAnchor.attr("href").replaceAll(".+?=(.+-T?P?E?).+", "$1");
		System.out.println(cardId);
		series.seriesId = cardId;
		
		Elements pageAnchors = initialDoc.select("p.pageLink a");
		int lastPage = 1;
		
		for(Element pageAnchor : pageAnchors){
			if(StringUtils.isNumeric(pageAnchor.text())){
				int potentialPage = Integer.parseInt(pageAnchor.text());
				if(potentialPage > lastPage){
					lastPage = potentialPage;
				}
			}
		}
		System.out.println(lastPage);
		series.wstcgLastPage = lastPage;
		
		return series;
	}
	
	public static String getSeriesPageResponse(String seriesWstcgId, String page) throws Exception{
		
		URL urlObj = new URL(CotdWebEn_LocalConf.wstcgen_series_url);
		HttpsURLConnection con = (HttpsURLConnection) urlObj.openConnection();

		//Add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "expansion_id=" + seriesWstcgId + "&page=" + page;
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		//int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'POST' request to URL : " + urlObj.getPath());
		//System.out.println("Post parameters : " + urlParameters);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//System.out.println(response.toString());
		
		return response.toString();
	}
	
	public static ArrayList<String> getSeriesAbilities(CotdWebEn_Series series) throws Exception{
		
		System.out.println("Getting abilitites for " + series.name);
		
		ArrayList<String> content = new ArrayList<String>();
		
		for(int i = 1; i < series.wstcgLastPage; i++){
			
			Document initialDoc = Jsoup.parse(CotdWebEn_Scrapper.getSeriesPageResponse(series.wstcgId, String.valueOf(i)));
			
			Elements cardAnchors = initialDoc.select("a[href*=cardno]");
			
			for(Element cardAnchor : cardAnchors){
				
				String cardId = cardAnchor.attr("href").replaceAll(".+?=(.+)", "$1");
				
				content.addAll(CotdWebEn_Scrapper.getCardAbilities(cardId));
			}

		}
		
		return content;
	}
	
	public static ArrayList<String> getCardAbilities(String cardId) throws Exception{
		
		System.out.println("Getting abilitites for card " + cardId);
		
		ArrayList<String> content = new ArrayList<String>();
		
		String url = CotdWebEn_LocalConf.wstcgen_cardbase_url + cardId;
		
		Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").maxBodySize(0).get();
		doc.outputSettings().prettyPrint(false);
		//System.out.println(doc.html());

		Element abilitiesTh = doc.select("th:contains(text)").first();
		Element abilitiesTd = abilitiesTh.nextElementSibling();
		String abilitiesHtml = abilitiesTd.html().replace("<br>", "\n");
		String abilities = Jsoup.clean(abilitiesHtml, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
		content.add("# " + cardId);
		content.add(abilities);

		return content;
	}
}
