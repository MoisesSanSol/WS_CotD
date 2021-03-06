package ws_cotd_web;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

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
		
		String currentFullName = linea.replace("--- JP ", "");
		
		while(!temporalContent.isEmpty()){
			
			CotdWeb_Card card = new CotdWeb_Card();
			
			temporalContent.remove(0); // Salto de linea
			card.name = temporalContent.remove(0);
			card.jpName = temporalContent.remove(0);
			card.idLine = temporalContent.remove(0);
			card.id = card.idLine.split(" ")[0].replaceAll("a$", "");
			card.rarity = card.idLine.split(" ")[1];
			card.seriesId = card.id.split("-")[0].split("/")[1].toLowerCase();
			card.seriesFullName = currentFullName;
			String cardNumber = card.id.split("-")[1];
			card.fileId = card.seriesId + "_" + cardNumber;
			card.calculateBaseIds();
			
			if(cardNumber.length() == 1){
				System.out.println("* Parsing card: " + card.id + " / " + card.name);
				throw new Exception("Something has been overlooked in a card id.");
			}
			
			String stats = temporalContent.remove(0);
			
			ArrayList<String> sameDayParallels = new ArrayList<String>();
			while(stats.startsWith(card.id)){
				sameDayParallels.add(stats);
				stats  = temporalContent.remove(0);
			}
			if(stats.contains(" 00,")){
				System.out.println("* Parsing card: " + card.id + " / " + card.name);
				System.out.println("* Stats: " + stats);
				throw new Exception("Power has been overlooked in card stats.");
			}
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
					
					if(abilityLine.contains("<>") || abilityLine.contains("''")){
						System.out.println("* Parsing card: " + card.id + " / " + card.name);
						System.out.println("* Ability: " + abilityLine);
						throw new Exception("Something has been overlooked in a card ability.");
					}
					
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
			else if(abilityLine.startsWith("-@")) {
				card.isReferenced = true;
				card.hasReferences = true;
			}
			
			if(abilityLine.length() > 4){
				currentFullName = abilityLine.replaceAll("-.?-- JP ", "");
			}
			
			card.imageFileId = "jp_" + String.format("%02d", count);
			count++;
			
			System.out.println("* Parsed card: " + card.id + " / " + card.name);
			
			cards.add(card);
			int parallelCount = 2;
			for(String parrallelIdLine : sameDayParallels){
				CotdWeb_Card newCard = (CotdWeb_Card)SerializationUtils.clone(card);
				newCard.idLine = parrallelIdLine;
				newCard.id = parrallelIdLine.split(" ")[0];
				newCard.rarity = parrallelIdLine.split(" ")[1];
				String parrallelCardNumber = parrallelIdLine.split(" ")[0].split("-")[1];
				newCard.fileId = card.seriesId + "_" + parrallelCardNumber;
				newCard.imageFileId = card.imageFileId + "_" + parallelCount;
				parallelCount++;
				newCard.calculateBaseIds();
				cards.add(newCard);
			}
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
	
	public static HashMap<String,String> getSeriesFromCurrentSeries_Base() throws Exception{
		
		HashMap<String,String> series = new HashMap<String,String>();
		
		ArrayList<String> currentSeriesContent = new ArrayList<String>(Files.readAllLines(CotdWeb_Parser.conf.currentSeriesFile.toPath(), StandardCharsets.UTF_8));
		
		String seriesHeader = currentSeriesContent.remove(0);
		
		while(!seriesHeader.startsWith("*")) {
			
			String seriesFullId = currentSeriesContent.remove(0).split("-")[0];
			String seriesCleanHeader = seriesHeader.replace("--- JP ", ""); 
			series.put(seriesFullId, seriesCleanHeader);
			seriesHeader = currentSeriesContent.remove(0);
		}
		
		return series;
	}
	
	public static String getDateFromTemporal() throws Exception{
		
		List<String> fileContent = new ArrayList<>(Files.readAllLines(CotdWeb_Parser.conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		fileContent.remove(0); // Separador

		return fileContent.remove(0).replace("Cartas del d�a ", "").replace(":", "");
	}

	public static ArrayList<String> getAbilityListFromWeb(String seriesId) throws Exception{
		
		ArrayList<String> abilities = new ArrayList<String>();
		HashSet<String> uniqueAbilities = new HashSet<String>();
		
		File seriesWebFolder = new File(CotdWeb_Parser.conf.webFolder.getAbsolutePath() + "\\" + seriesId + "\\cards");

		for(File file : seriesWebFolder.listFiles()){
			uniqueAbilities.addAll(CotdWeb_PageHelper.getCardAbilities(file));
		}
		
		abilities.addAll(uniqueAbilities);
		Collections.sort(abilities);
		
		for(String ability : abilities){
			//System.out.println(ability);
		}
		return abilities;
	}

	public static CotdWeb_Card getCardsFromPage(File cardPage) throws Exception{
		
		CotdWeb_Card card = new CotdWeb_Card();
		
		ArrayList<String> names = CotdWeb_PageHelper.getCardName(cardPage);
		card.name = names.remove(0);
		card.jpName = names.remove(0);
		card.idLine = CotdWeb_PageHelper.getCardIdLine(cardPage);
		card.id = card.idLine.split(" ")[0].replaceAll("a$", "");
		card.rarity = card.idLine.split(" ")[1];
		card.seriesId = card.id.split("-")[0].split("/")[1].toLowerCase();
		card.seriesFullName = CotdWeb_PageHelper.getCardProduct(cardPage);
		String cardNumber = card.id.split("-")[1];
		card.fileId = card.seriesId + "_" + cardNumber;
		card.calculateBaseIds();
		ArrayList<String> statsArray = CotdWeb_PageHelper.getCardStats(cardPage);
		String stats = "";
		while(!statsArray.isEmpty()){
			stats = stats + statsArray.remove(0);
		}
		card.statsLine = stats;
		card.abilities = CotdWeb_PageHelper.getCardAbilities(cardPage);
		card.references = CotdWeb_PageHelper.getCardRefereneces(cardPage);
		card.notes = CotdWeb_PageHelper.getCardNotes(cardPage);
		
		return card;
	}
	
}
