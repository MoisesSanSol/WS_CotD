package ws_cotd_v2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;
import ws_cotd_web.CotdWeb_Card;
import ws_cotd_web.CotdWeb_IndexHelper;
import ws_cotd_web.CotdWeb_Parser;

public class Cotd_ExtraFeatures {

public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		//Cotd_ExtraFeatures.pastCotdNotesForSps();
		Cotd_ExtraFeatures.transferSeriesSeparationFromGlobalToImages();
		//Cotd_ExtraFeatures.insertSeparatorsInFromGlobal();
		
		System.out.println("*** Finished ***");
	}
	
	public static void pastCotdNotesForSps() throws Exception{
		
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
}
