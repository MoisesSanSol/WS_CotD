package ws_tcgresults;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import mss.MssFileAux;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ws_cotd.Cotd_Conf;

public class WsTcgResults_Scrapper {

	public static String wstcgResultsBaseUrl = "https://ws-tcg.com/deckrecipe/detail/";
	public static String hotcCardBaseUrl = "https://heartofthecards.com/code/cardlist.html?card=WS_";
	
	
	public static ArrayList<WsTcgResults_Deck> getDecklists(String locationUrl, String location, String type) throws Exception{
	
		Cotd_Conf conf = Cotd_Conf.getInstance();
		String cardNamesFilePath = conf.webFolder + "\\wstcgresults\\references\\cardnames.txt";
		File cardNamesFile = new File(cardNamesFilePath);
		
		HashMap<String,String> cardNames = MssFileAux.getTabulatedPairs(cardNamesFile);
		
		ArrayList<WsTcgResults_Deck> decklists = new ArrayList<WsTcgResults_Deck>();

		String url = WsTcgResults_Scrapper.wstcgResultsBaseUrl + locationUrl;
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();
		
		int numberOfDecks = 8;
		
		for(int i = 1; i <= numberOfDecks; i++){
		
			WsTcgResults_Deck deck = new WsTcgResults_Deck();
			deck.list = new ArrayList<WsTcgResults_Card>();
			deck.location = location;
			deck.type = type;
			deck.position = String.valueOf(i);
			
			Element anchor = doc.select("a#recipe" + i).first();
			Element decklistTable = anchor.nextElementSibling().nextElementSibling();
			Elements cards = decklistTable.select("tr[class*=color]");
			
			for(Element cardDom : cards){
				
				WsTcgResults_Card card = new WsTcgResults_Card();
				
				Element idDom = cardDom.select("td.cardno").first();
				card.id = idDom.text();
				Element jpNameDom = cardDom.select("td.cardname").first();
				card.jpName = jpNameDom.text();
				Element numberDom = cardDom.select("td.cardnum").first();
				card.quantity = numberDom.text().replace("枚", "");
				
				System.out.println("Found card: " + card.id);
				
				if(cardNames.containsKey(card.id)){
					card.name = cardNames.get(card.id);
				}
				else{
					//card.name = WsTcgResults_Scrapper.getCard(card.id);
					if(card.name == null){
						System.out.println("No card name found: " + card.id);
						card.name = card.jpName;
					}
					else{
						cardNames.put(card.id, card.name);
					}
					//Thread.sleep(10000);
				}
				deck.list.add(card);
			}
		
			decklists.add(deck);
			MssFileAux.saveTabulatedPairs(cardNamesFile, cardNames);
		}
		
		return decklists;
	}
	
	public static String getCard(String cardId) throws Exception{
		
		String name = null;
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		String cardImagesFolderPath = conf.webFolder + "\\wstcgresults\\images\\cards\\";
		
		String url = WsTcgResults_Scrapper.hotcCardBaseUrl + cardId;

		String cardFileId = cardId.replace("/", "-");
		
		Document doc = Jsoup.connect(url).maxBodySize(0).get();
		
		Element img = doc.select("img[src*=" + cardFileId + "]").first();
		if(img == null){
			img = doc.select("img[src*=00-00]").first();
		}
		if(img != null){
			File targetImageFile = new File(cardImagesFolderPath + cardFileId + ".gif");
			
			System.out.println("Checking card: " + url);
			if(!targetImageFile.exists()){
				String imageUrl = img.absUrl("src").replace("heartofthecards/", "");
				System.out.println("Saving img: " + imageUrl);
				FileUtils.copyURLToFile(new URL(imageUrl), targetImageFile);
			}
			
			Element cardTable = img.parent().parent().parent();
			Element nameDom = cardTable.child(1).child(0);
			
			name = nameDom.text();
		}
		
		return name;
	}
}
