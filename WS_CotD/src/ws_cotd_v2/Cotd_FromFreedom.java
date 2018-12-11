package ws_cotd_v2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ws_cotd.Cotd_Conf;
import ws_cotd_web.CotdWeb_Parser;

public class Cotd_FromFreedom {

	private static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	// Parallel parsing of FromImages and FromFreedom
	public static void generateTemporalFile_FromFreedom() throws Exception{
		
		System.out.println("** Generate Temporal File : From Images + From Freedom");
		
		ArrayList<String> temporalContent = new ArrayList<String>();  
		
		LinkedHashMap<String,String> series = Cotd_CurrentSeries.getCurrentSeries_Raw();
		ArrayList<String> fromFreedomContent = new ArrayList<>(Files.readAllLines(conf.fromFreedomFile.toPath(), StandardCharsets.UTF_8));
		ArrayList<String> fromImagesContent = new ArrayList<>(Files.readAllLines(conf.fromImagesFile.toPath(), StandardCharsets.UTF_8));
		
		fromFreedomContent.remove(0);
		fromFreedomContent.remove(0);
		fromFreedomContent.remove(0);
		
		temporalContent.add("****************************************");
		String dateLine = "Cartas del día " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ":";
		temporalContent.add(dateLine);
		temporalContent.add("");
		
		String seriesHeader = series.keySet().iterator().next();
		String seriesId = series.remove(seriesHeader);	

		temporalContent.add(seriesHeader);
		
		boolean blockLoop = true;
		while(blockLoop){
			
			temporalContent.add("");
			
			String freedomName = fromFreedomContent.remove(0);
			
			temporalContent.add("# Name goes here");
			temporalContent.add(freedomName);
			temporalContent.add("# Jp Name goes here");
			temporalContent.add(seriesId);
			
			String imagesStatsLine = fromImagesContent.remove(0);
			String freedomStatsLine = fromFreedomContent.remove(0);
			
			if(imagesStatsLine.startsWith("Personaje")){
				String freedomPower = freedomStatsLine.replaceAll("^.+POWER: (\\d+)$", "$1");
				imagesStatsLine = imagesStatsLine.replace("Poder: 00", "Poder: " + freedomPower);
				temporalContent.add(imagesStatsLine);
				
				String freedomTraitsLine = fromFreedomContent.remove(0);
				fromImagesContent.remove(0);
				
				String traitsLine = freedomTraitsLine.replaceAll("\\((.+?)\\)\\((.+?)\\)", "Traits: <<$1>> y <<$2>>.");
				temporalContent.add(traitsLine);
			}
			else{
				temporalContent.add(imagesStatsLine);
			}
			
			temporalContent.add("");
			fromImagesContent.remove(0);
			
			String text = fromFreedomContent.remove(0);
			boolean textLoop = true;
			while(textLoop && !fromFreedomContent.isEmpty()){
				String line = fromFreedomContent.remove(0);
				if(line.startsWith("[")){
					temporalContent.add("*" + text);
					text = line;
				}
				else if(line.startsWith("-")){
					temporalContent.add("*" + text);
					temporalContent.add("");
					temporalContent.add("-");
					textLoop = false;
				}
				else if(line.startsWith("+++")){
					temporalContent.add("*" + text);
					temporalContent.add("");
					seriesHeader = series.keySet().iterator().next();
					seriesId = series.remove(seriesHeader);
					temporalContent.add(seriesHeader);
					fromFreedomContent.remove(0);
					fromFreedomContent.remove(0);
					textLoop = false;
				}
				else{
					text = text + " " + line;
				}
			}
			if(fromFreedomContent.isEmpty()){
				temporalContent.add("*" + text);
				temporalContent.add("");
				temporalContent.add("-");
				blockLoop = false;
			}
		}
		
		Files.write(conf.temporalFile.toPath(), temporalContent, StandardCharsets.UTF_8);
	}
}
