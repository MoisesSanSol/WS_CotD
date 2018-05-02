package ws_cotd_web;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ws_cotd.Cotd_Conf;

public class CotdWeb_Parser {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static ArrayList<CotdWeb_Card> getCardsFromTemporal() throws Exception{
		
		ArrayList<CotdWeb_Card> cards = new  ArrayList<CotdWeb_Card>();
		
		List<String> temporalContent = new ArrayList<>(Files.readAllLines(conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		
		temporalContent.remove(0); // Separador
		temporalContent.remove(0); // Fecha
		
		String linea = "";
		while(!linea.startsWith("-")){
			linea = temporalContent.remove(0);
		}
		
		int count = 1;
		
		while(!temporalContent.isEmpty()){
			
			CotdWeb_Card card = new CotdWeb_Card();
			
			temporalContent.remove(0); // Salto de linea
			card.name = temporalContent.remove(0);
			card.jpName = temporalContent.remove(0);
			card.idLine = temporalContent.remove(0);
			card.id = card.idLine.split(" ")[0];
			card.seriesId = card.id.split("-")[0].split("/")[1].toLowerCase();
			card.fileId = card.seriesId + "_" + card.id.split("-")[1];
			
			String stats  = temporalContent.remove(0);
			if(stats.startsWith("Personaje")){
				stats = stats + " " + temporalContent.remove(0);
				temporalContent.remove(0);
			}
			else if(stats.startsWith("Evento")){
				temporalContent.remove(0);
			}
			else{
				String next = temporalContent.remove(0);
				if(!next.equals("")){
					stats = stats + "\r\n<br>\r\n" + next;
					temporalContent.remove(0);
				}
			}
			card.statsLine = stats;
			
			ArrayList<String> abilities = new ArrayList<String>();
			ArrayList<String> notes = new ArrayList<String>();
			
			String abilityLine = temporalContent.remove(0);
			while(!abilityLine.startsWith("-")){
				if(abilityLine.startsWith("#")) {
					notes.add(abilityLine);
				}
				else if(!abilityLine.isEmpty()){
					abilities.add(abilityLine);
				}
				abilityLine = temporalContent.remove(0);
			}
			card.abilities = abilities;
			card.notes = notes;
			
			if(abilityLine.startsWith("-#")) {
				card.isReferenced = true;
			}
			else if(abilityLine.startsWith("-%")) {
				card.hasReferences = true;
			}
			
			card.imageFileId = "jp_" + String.format("%02d", count);
			count++;
			
			System.out.println("* Parsed card: " + card.id + " / " + card.name);
			
			cards.add(card);
		}
		
		return cards;
	}
	
	public static HashMap<String,String> getSeriesFromCurrentSeries() throws Exception{
		
		HashMap<String,String> series = new HashMap<String,String>();
		
		ArrayList<String> currentSeriesContent = new ArrayList<String>(Files.readAllLines(CotdWeb_Parser.conf.currentSeriesFile.toPath(), StandardCharsets.UTF_8));
		
		String seriesHeader = currentSeriesContent.remove(0);
		
		while(!seriesHeader.startsWith("*")) {
			
			String seriesFullId = currentSeriesContent.remove(0);
			String seriesId = seriesFullId.split("-")[0].split("/")[1].toLowerCase();
			seriesHeader = seriesHeader.replace("--- JP ", ""); 
			series.put(seriesId, seriesHeader);
			seriesHeader = currentSeriesContent.remove(0);
		}
		
		return series;
	}
	
	public static String getDateFromTemporal() throws Exception{
		
		List<String> fileContent = new ArrayList<>(Files.readAllLines(CotdWeb_Parser.conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		fileContent.remove(0); // Separador

		return fileContent.remove(0).replace("Cartas del d�a ", "").replace(":", "");
	}
	
}