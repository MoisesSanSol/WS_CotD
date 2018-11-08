package ws_cotd_web;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ws_cotd.Cotd_Conf;

public class CotdWeb_WstcgScrapper {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void getCardImagesFromWstcg(ArrayList<String> cardIds) throws Exception{
		
		ArrayList<String> newCardFileIds = new ArrayList<String>();

		String seriesId = cardIds.get(0).split("/")[1].split("-")[0].toLowerCase();
		String imagesFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/images/";

		int count = 1;
		
		for(String cardId : cardIds) {
			
			String url = conf.wsJpCardUrl + cardId;
			
			System.out.println("* Get WsTcg Image From Url: " + url);
			
			Document doc = Jsoup.connect(url).maxBodySize(0).get();
			Element image = doc.select("td.graphic img").first();
			
			String imageUrl = image.attr("abs:src");
			System.out.println("Scrapping img: " + imageUrl);
			String paddedCount = String.format("%02d", count);

			String fileId = seriesId + "_" + cardId.split("-")[1];
			newCardFileIds.add(fileId);
			
			String imageName = "/jp_" + paddedCount + ".png";

			File targetImageFile = new File(imagesFolderPath + fileId + "_wstcg.jpg");
			
			System.out.println("Saving img: " + imageName);
			FileUtils.copyURLToFile(new URL(imageUrl), targetImageFile);
			
			Thread.sleep(1000);
			count++;
		}
		
		CotdWeb_IndexHelper.updateSeriesIndex_AfterRelease(seriesId, newCardFileIds, "wstcg");
	}
	
	public static ArrayList<CotdWeb_Card> getCardsFromWstcg(ArrayList<String> cardIds) throws Exception{
		
		ArrayList<CotdWeb_Card> cards = new ArrayList<CotdWeb_Card>();

		for(String cardId : cardIds) {
			
			CotdWeb_Card card = new CotdWeb_Card();
			
			String url = conf.wsJpCardUrl + cardId;
			
			Document doc = Jsoup.connect(url).maxBodySize(0).get();
			System.out.println("Scrapping URL: " + url);
			//System.out.println("Doc: " + doc.html());
			
			Element container = doc.select("table.card-detail-table tbody").first();
			
			if(container != null){
			
				System.out.println("Scrapping Card: " + cardId);
				
			Element nameTd = container.child(0).child(2);
			Element idTd = container.child(1).child(1);
			Element rarityTd = container.child(1).child(3);
			Element typeTd = container.child(4).child(1);
			Element colorTd = container.child(4).child(3);
			Element levelTd = container.child(5).child(1);
			Element costTd = container.child(5).child(3);
			Element powerTd = container.child(6).child(1);
			Element soulTd = container.child(6).child(3);
			Element triggerTd = container.child(7).child(1);
			Element traitsTd = container.child(7).child(3);
			Element abilitiesTd = container.child(8).child(1);
			
			card.name = "Pending";
			card.jpName = nameTd.ownText();
			card.idLine = idTd.ownText() + " " + rarityTd.ownText();
			
			String color = "Desconocido";
			String colorImg = colorTd.html();
			
				if(colorImg.contains("yellow")){
					color = "Amarillo";
				}
				if(colorImg.contains("green")){
					color = "Verde";
				}
				if(colorImg.contains("red")){
					color = "Rojo";
				}
				if(colorImg.contains("blue")){
					color = "Azul";
				}
				
				String trigger = "1 Soul";
				if(triggerTd.ownText().contains("−")){
					trigger = "No";
				}
				
				String type = typeTd.ownText();
				String line = "";
				
				switch(type){
			
					case ("キャラ"):
						
						line = "Personaje ";
						line = line + color;
						line = line + ", Nivel: " + levelTd.ownText();
						line = line + ", Coste: " + costTd.ownText();
						line = line + ", Poder: " + powerTd.ownText();
						line = line + ", Soul: " + soulTd.children().size();
						line = line + ", Trigger: " + trigger + ",";
						line = line + "\r\n";
						String traits = traitsTd.ownText().replace("・", ">> y <<");
						line = line + "Traits: <<" + traits + ">>.";
						
						card.statsLine = line;
						break;
					
					/*case ("Evento"):
						
						nivel = this.getImageLevel(image);
						System.out.println("* Image Level: " + nivel);
						coste = this.getImageCost(image);
						System.out.println("* Image Cost: " + coste);
						soul = this.getImageSoul(nivel);
						
						line = "Evento ";
						line = line + color;
						line = line + ", Nivel: " + nivel;
						line = line + ", Coste: " + coste;
						line = line + ", Trigger: No.";
						fileContent.add(line);
						
						break;
					*/
					case ("クライマックス"):
						
						line = "Climax ";
						line = line + color;
						line = line + ", Trigger: Manual check";
						
						card.statsLine = line;
						break;
				}
				
				String[] abilities =  abilitiesTd.html().replace("-", "{Sin Habilidades}").split("<br>");
				ArrayList<String> cardAbilities = new ArrayList<String>(Arrays.asList(abilities));
				card.abilities = cardAbilities;
				
				cards.add(card);
			}
			else{
				System.out.println("Card not found: " + cardId);
			}
			Thread.sleep(1000);
		}
		
		return cards;
	}
}
