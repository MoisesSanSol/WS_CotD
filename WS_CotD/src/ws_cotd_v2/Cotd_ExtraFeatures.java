package ws_cotd_v2;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;
import ws_cotd_web.CotdWeb_Card;
import ws_cotd_web.CotdWeb_IndexHelper;
import ws_cotd_web.CotdWeb_Parser;

public class Cotd_ExtraFeatures {

	public static int parallelRotationCards = 0;
	public static int parallelRotationExtras = 0;
	
	public static void checkCardsMissingFillInParts() throws Exception{
	
		// The parser already does this checking.
		CotdWeb_Parser.getCardsFromTemporal();
		
	}

	public static void pastCotdNotesForSps() throws Exception{
		
		ArrayList<CotdWeb_Card> cards = CotdWeb_Parser.getCardsFromTemporal();
		
		for(CotdWeb_Card card : cards){
			
		}
		
	}
	
	public static void pastCotdNotesForParallels() throws Exception{
		
		ArrayList<CotdWeb_Card> cards = CotdWeb_Parser.getCardsFromTemporal();
		
		for(CotdWeb_Card card : cards){
			
			if(card.idLine.contains("SP")){
				
				card.id = card.id.replaceAll("SS?P", "");
				
				String indexDate = CotdWeb_IndexHelper.searchIndexDate(card);
				String noteDate = Cotd_Utilities.indexDateToNoteDate(indexDate);
				
				String note = "# '"
						+ card.name
						+ "' ("
						+ card.id
						+ ") fue ya carta del día el "
						+ noteDate
						+ ", la de hoy es la versión SP.";
				System.out.println(note);
			}

			if(card.idLine.contains("RRR")){
				
				card.id = card.id.replaceAll("R$", "");
				
				String indexDate = CotdWeb_IndexHelper.searchIndexDate(card);
				String noteDate = Cotd_Utilities.indexDateToNoteDate(indexDate);
				
				String note = "# '"
						+ card.name
						+ "' ("
						+ card.id
						+ ") fue ya carta del día el "
						+ noteDate
						+ ", la de hoy es la versión RRR.";
				System.out.println(note);
			}
			
			if(card.idLine.contains("SR")){
				
				card.id = card.id.replaceAll("S$", "");
				
				String indexDate = CotdWeb_IndexHelper.searchIndexDate(card);
				String noteDate = Cotd_Utilities.indexDateToNoteDate(indexDate);
				
				String note = "# '"
						+ card.name
						+ "' ("
						+ card.id
						+ ") fue ya carta del día el "
						+ noteDate
						+ ", la de hoy es la versión SR.";
				System.out.println(note);
			}
		}
	}	

	public static void transferSeriesSeparationFromGlobalToImages() throws Exception{
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		ArrayList<Integer> cardCountPerSeries = new ArrayList<Integer>();
		
		ArrayList<String> fromGlobalContent = new ArrayList<String>(Files.readAllLines(conf.fromGlobalFile.toPath(), StandardCharsets.UTF_8));
				
		fromGlobalContent.remove(0); // Delete first series header
		
		int cardCount = 0;
		
		while(!fromGlobalContent.isEmpty()){
			String line = fromGlobalContent.remove(0);

			if(line.startsWith("-")) {
				
				cardCount++;
				
				if(line.startsWith("---")) {
					
					cardCountPerSeries.add(cardCount);
					cardCount = 0;
				}
			}
			
		}
		
		cardCountPerSeries.add(9999);
		
		
		ArrayList<String> fromImagesContent = new ArrayList<String>(Files.readAllLines(conf.fromImagesFile.toPath(), StandardCharsets.UTF_8));
		
		cardCount = cardCountPerSeries.remove(0);
		
		for(int i = 0; i < fromImagesContent.size(); i++) {
			
			String line = fromImagesContent.get(i);

			if(line.startsWith("-")) {
				
				cardCount--;
				
				if(cardCount == 0) {
					
					fromImagesContent.set(i, "--");
					cardCount = cardCountPerSeries.remove(0);
				}
				
			}
		}
		fromImagesContent.set(fromImagesContent.size() - 1, "---");
		
		Files.write(conf.fromImagesFile.toPath(), fromImagesContent, StandardCharsets.UTF_8);
	}
	
	public static void insertSeparatorsInFromGlobal() throws Exception{
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		ArrayList<String> fromGlobalContent = new ArrayList<String>(Files.readAllLines(conf.fromGlobalFile.toPath(), StandardCharsets.UTF_8));
				
		for(int i = 0; i < fromGlobalContent.size(); i++) {
			
			if(fromGlobalContent.get(i).isEmpty()) {
				fromGlobalContent.set(i, "-");
			}
		}
		
		Files.write(conf.fromGlobalFile.toPath(), fromGlobalContent, StandardCharsets.UTF_8);
	}
	
	
	public static void fillPowerFromWsblog() throws Exception{
		
		ArrayList<String> jpCardNames = Cotd_ExtraFeatures.getJpCardNames();
		HashMap<String,String> cardPowers = Cotd_ExtraFeatures.getCardPowers(jpCardNames);
		Cotd_ExtraFeatures.updateTemporalWithPowers(cardPowers);
	}
	
	public static ArrayList<String> getJpCardNames() throws Exception{
		
		ArrayList<String> jpCardNames = new ArrayList<String>();
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		ArrayList<String> temporalContent = new ArrayList<String>(Files.readAllLines(conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		
		temporalContent.remove(0); // Separador
		temporalContent.remove(0); // Fecha
		
		String linea = "";
		while(!linea.startsWith("-")){
			linea = temporalContent.remove(0);
		}
		
		while(!temporalContent.isEmpty()){
			
			temporalContent.remove(0); // Salto de linea
			temporalContent.remove(0); // Nombre
			String jpName = temporalContent.remove(0);
			temporalContent.remove(0); // Id
			String stats  = temporalContent.remove(0);
			while(!(stats.startsWith("Personaje") || stats.startsWith("Climax") || stats.startsWith("Evento"))){
				stats  = temporalContent.remove(0);
			}
			if(stats.startsWith("Personaje")){
				jpCardNames.add(jpName);
				System.out.println("Found card jp name: " + jpName);
			}
			String nextLines = temporalContent.remove(0);
			while(!nextLines.startsWith("-")){
				nextLines = temporalContent.remove(0);
			}
		}
		return jpCardNames;
	}
	
	public static HashMap<String,String> getCardPowers(ArrayList<String> jpCardNames) throws Exception{
		
		HashMap<String,String> cardPowers = new HashMap<String,String>();
		
		String wsblogUrl = "http://ws.blog.jp/";
		
		Document doc = Jsoup.connect(wsblogUrl).maxBodySize(0).get();
		
		//System.out.println(doc.html());
		
		for(String jpName : jpCardNames){
			
			String power = null;
			/* If img divs are broken
			Element img = doc.select("img[alt=" + jpName + "]").first();
			if(img != null){
				System.out.println("Found img for jp name: " + jpName);
				Element brokenDiv = img.parent().parent();
				Element statsDiv = brokenDiv.nextElementSibling();
				System.out.println("Found stat line: " + statsDiv.text());
				String[] stats = statsDiv.text().split("　"); 
				String power = stats[0].split("/")[2];
				System.out.println("Splitted power as: " + power);
				cardPowers.put(jpName, power);
			}*/
			Element bold = doc.select("b:contains(" + jpName + ")").first();
			if(bold != null){
				System.out.println("Found bold for jp name: " + jpName);
				//System.out.println("Bold html: " + bold.html());
				// Name grouped with stats line
				if(bold.html().matches(".+?\\d+/\\d+/\\d+　.+")){
					power = bold.html().replaceAll(".+?\\d+/\\d+/(\\d+)　.+", "$1");
					System.out.println("Found power as: " + power);
				}
				// Name grouped with img, div sibling of stats line
				else if(bold.parent().tagName().equals("div")){
					Element statsDiv = bold.parent().nextElementSibling();
					//System.out.println("statsDiv text: " + statsDiv.text());
					if(statsDiv.text().matches("\\d+/\\d+/\\d+.+")){
						String[] stats = statsDiv.text().split("　"); 
						power = stats[0].split("/")[2];
						System.out.println("Found power as: " + power);
					}
				}
			}
			
			if(power != null){
				cardPowers.put(jpName, power);
			}
			else{
				System.out.println("This has no stats line (or most probably we were not able to find it).");
			}
			
		}
		return cardPowers;
	}
	
	public static void updateTemporalWithPowers(HashMap<String,String> cardPowers) throws Exception{
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		ArrayList<String> temporalContent = new ArrayList<String>(Files.readAllLines(conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		ArrayList<String> newTemporalContent = new ArrayList<String>();
		
		newTemporalContent.add(temporalContent.remove(0)); // Separador
		newTemporalContent.add(temporalContent.remove(0)); // Fecha

		String linea = "";
		while(!linea.startsWith("-")){
			linea = temporalContent.remove(0);
			newTemporalContent.add(linea);
		}
		
		while(!temporalContent.isEmpty()){
			
			newTemporalContent.add(temporalContent.remove(0)); // Salto de linea
			newTemporalContent.add(temporalContent.remove(0)); // Nombre
			String jpName = temporalContent.remove(0);
			newTemporalContent.add(jpName);
			newTemporalContent.add(temporalContent.remove(0)); // Id
			String stats  = temporalContent.remove(0);
			while(!(stats.startsWith("Personaje") || stats.startsWith("Climax") || stats.startsWith("Evento"))){
				newTemporalContent.add(stats);
				stats  = temporalContent.remove(0);
			}
			if(cardPowers.containsKey(jpName)){
				stats = stats.replace(" 00,", " " + cardPowers.get(jpName) + ",");
			}
			
			newTemporalContent.add(stats);
			String nextLines = temporalContent.remove(0);
			newTemporalContent.add(nextLines);
			while(!nextLines.startsWith("-")){
				nextLines = temporalContent.remove(0);
				newTemporalContent.add(nextLines);
			}
		}
		
		Files.write(conf.temporalFile.toPath(), newTemporalContent, StandardCharsets.UTF_8);
	}
	
	public static void updateParallelImageFileNames() throws Exception{
		
		ArrayList<CotdWeb_Card> cards = CotdWeb_Parser.getCardsFromTemporal();
		
		int parallelCount = 1;
		
		for(CotdWeb_Card card : cards){
			if(card.isParallel){
				String paddedCount = String.format("%02d", parallelCount);
				
				File originFile = new File(Cotd_Conf.getInstance().imagesFolder.getAbsolutePath() + "/jp_" + paddedCount + "_extra.png");
				File targetFile = new File(Cotd_Conf.getInstance().imagesFolder.getAbsolutePath() + "/" + card.imageFileId + ".png");
				
				System.out.println("* Renaming image: " + originFile.getName() + ", to : " + targetFile.getName());
				
				originFile.renameTo(targetFile);
				parallelCount++;
			}
		}
	}
	
	public static void updateParallelImageFileNumbers() throws Exception{
		
		int parallelCount = 1; 
		
		for(int i = 1; i <= Cotd_ExtraFeatures.parallelRotationCards; i++){
			for(int j = 0; j < Cotd_ExtraFeatures.parallelRotationExtras; j++){

				String paddedCount = String.format("%02d", parallelCount);
				int newNumber =  i + (j * Cotd_ExtraFeatures.parallelRotationCards);
				String paddedNewNumber = String.format("%02d", newNumber);
				
				File originFile = new File(Cotd_Conf.getInstance().imagesFolder.getAbsolutePath() + "/jp_" + paddedNewNumber + "_extra.png");
				File targetFile = new File(Cotd_Conf.getInstance().imagesFolder.getAbsolutePath() + "/jp_" + paddedCount + "_aux.png");
				
				Cotd_Out.println("* Renaming image: " + originFile.getName() + ", to : " + targetFile.getName());
				
				originFile.renameTo(targetFile);
				parallelCount++;
			}
		}
		
		for(int aux = 1; aux < parallelCount; aux++){
			
			String paddedAuxCount = String.format("%02d", aux);
			
			File originFile = new File(Cotd_Conf.getInstance().imagesFolder.getAbsolutePath() + "/jp_" + paddedAuxCount + "_aux.png");
			File targetFile = new File(Cotd_Conf.getInstance().imagesFolder.getAbsolutePath() + "/jp_" + paddedAuxCount + "_extra.png");
			
			Cotd_Out.println("* Renaming image: " + originFile.getName() + ", to : " + targetFile.getName());
			
			originFile.renameTo(targetFile);
		}
	}
}
