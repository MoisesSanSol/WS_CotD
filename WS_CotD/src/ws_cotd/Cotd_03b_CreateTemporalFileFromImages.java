package ws_cotd;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

public class Cotd_03b_CreateTemporalFileFromImages {

	private Cotd_Conf conf;
	
	public Cotd_03b_CreateTemporalFileFromImages(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		Cotd_03b_CreateTemporalFileFromImages main = new Cotd_03b_CreateTemporalFileFromImages();
		main.generateTemporalFileFromImages(true);
		
		Cotd_Utilities.openFileInNotepad(main.conf.temporalFile);
		
		System.out.println("*** Finished ***");
	}
	
	private void generateTemporalFileFromImages(boolean cardTextFromGlobal) throws Exception{
		
		System.out.println("** Generate Temporal File From Images");
		
		ArrayList<String> temporalContent = new ArrayList<String>();  
		
		temporalContent.add("****************************************");
		String dateLine = "Cartas del día " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ":";
		temporalContent.add(dateLine);
		temporalContent.add("");
		
		List<String> currentSeriesContent = new ArrayList<>(Files.readAllLines(conf.currentSeriesFile.toPath(), StandardCharsets.UTF_8));
		String seriesHeader = "";
		String seriesId = "";
		
		seriesHeader = currentSeriesContent.remove(0);
		seriesId = currentSeriesContent.remove(0);
		temporalContent.add(seriesHeader);
		temporalContent.add("");
		temporalContent.add("# Name goes here");
		temporalContent.add(seriesId);
		
		List<String> fromImagesContent = new ArrayList<>(Files.readAllLines(conf.fromImagesFile.toPath(), StandardCharsets.UTF_8));
		
		ArrayList<ArrayList<String>> cardsText = new ArrayList<ArrayList<String>>();
		if(cardTextFromGlobal){
			cardsText = this.getCardsTextFromGlobal();
		}
		
		while(fromImagesContent.size() > 0){
			
			String line = fromImagesContent.remove(0);
			
			if(line.startsWith("-")){
				temporalContent.add("");
				if(cardTextFromGlobal){
					ArrayList<String> cardText = cardsText.remove(0);
					while(!cardText.isEmpty()){
						temporalContent.add("*" + cardText.remove(0));
					}
				}else{
					temporalContent.add("* Abilities go here");
				}
				temporalContent.add("");
				if(line.startsWith("---")){
					// Do nothing
				}
				else{
					if(line.startsWith("--")){
						seriesHeader = currentSeriesContent.remove(0);
						seriesId = currentSeriesContent.remove(0);
						temporalContent.add(seriesHeader);
					}
					else{
						temporalContent.add("-");
					}
					temporalContent.add("");
					temporalContent.add("# Name goes here");
					temporalContent.add(seriesId);
				}
			}
			else{
				temporalContent.add(line);
			}

		}
		temporalContent.add("-");
		
		Files.write(conf.temporalFile.toPath(), temporalContent, StandardCharsets.UTF_8);
	}
	
	private ArrayList<ArrayList<String>> getCardsTextFromGlobal() throws Exception{
		
		System.out.println("* Get Cards Text From Global");
		
		ArrayList<ArrayList<String>> cardsText = new ArrayList<ArrayList<String>>();
		
		List<String> fromGlobalContent = new ArrayList<>(Files.readAllLines(conf.fromGlobalFile.toPath(), StandardCharsets.UTF_8));
		
		while(!fromGlobalContent.isEmpty()){
			ArrayList<String> cardText = new ArrayList<String>();
			String textLine = fromGlobalContent.remove(0);
			while(!textLine.equals("-")){
				cardText.add(textLine);
				textLine = fromGlobalContent.remove(0);
			}
			cardsText.add(cardText);
		}
		
		return cardsText;
	}
}
