package ws_cotd;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cotd_03b_CreateTemporalFileFromImages {

	private Cotd_Conf conf;
	
	private static boolean useGlobalFile = true;
	
	public Cotd_03b_CreateTemporalFileFromImages(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		Cotd_03b_CreateTemporalFileFromImages main = new Cotd_03b_CreateTemporalFileFromImages();
		main.generateTemporalFileFromImages(useGlobalFile);
		
		Cotd_Utilities.openFileInNotepad(main.conf.temporalFile);
		
		System.out.println("*** Finished ***");
	}
	
	private void generateTemporalFileFromImages(boolean cardTextFromGlobal) throws Exception{
		
		System.out.println("** Generate Temporal File From Images");
		
		ArrayList<String> temporalContent = new ArrayList<String>();  
		
		temporalContent.add("****************************************");
		String dateLine = "Cartas del día " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ":";
		temporalContent.add(dateLine);
		//temporalContent.add("");
		
		List<String> currentSeriesContent = new ArrayList<>(Files.readAllLines(conf.currentSeriesFile.toPath(), StandardCharsets.UTF_8));
		String seriesHeader = "";
		String seriesId = "";
		
		seriesHeader = currentSeriesContent.remove(0);
		seriesId = currentSeriesContent.remove(0);
		ArrayList<String> cardHeader = new ArrayList<String>();
		cardHeader.add(seriesHeader);
		cardHeader.add("");
		cardHeader.add("# Name goes here");
		cardHeader.add("# Jp Name goes here");
		cardHeader.add(seriesId);
		
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
					ArrayList<String> cardText = this.parseCardTextFromGlobal(cardsText.remove(0), cardHeader);
					temporalContent.addAll(cardText);
					cardHeader.clear();
				}else{
					temporalContent.addAll(cardHeader);
					temporalContent.add("");
					temporalContent.add("* Abilities go here");
					cardHeader.clear();
				}
				//temporalContent.add("");
				if(line.startsWith("---")){
					// Do nothing
				}
				else{
					if(line.startsWith("--")){
						seriesHeader = currentSeriesContent.remove(0);
						seriesId = currentSeriesContent.remove(0);
						cardHeader.add(seriesHeader);
					}
					else{
						cardHeader.add("-");
					}
					cardHeader.add("");
					cardHeader.add("# Name goes here");
					cardHeader.add("# Jp Name goes here");
					cardHeader.add(seriesId);
				}
			}
			else{
				cardHeader.add(line);
			}

		}
		temporalContent.add("");
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
	
	private ArrayList<String> parseCardTextFromGlobal(ArrayList<String> globalCardText, ArrayList<String> header){
		
		ArrayList<String> cardText = new ArrayList<String>();
		
		String firstGlobalLine = globalCardText.get(0);
		
		if(firstGlobalLine.matches(".+\\((.+?)\\) (.+?) \\((.+?)\\/(.+)\\).*")){
			header.set(2, "# Name goes here: " + firstGlobalLine.replaceAll(".+\\((.+?)\\) (.+?) \\((.+?)\\/(.+)\\).*", "$2"));
			header.set(3, header.get(3) + " " + firstGlobalLine.replaceAll(".+\\((.+?)\\) (.+?) \\((.+?)\\/(.+)\\).*", "$1"));
			header.set(5, firstGlobalLine.replaceAll(".+\\((.+?)\\) .+? \\((.+?)\\/(.+)\\).*", "Traits: <<$2>> y <<$3>>."));
			globalCardText.remove(0);
		}
		else if(firstGlobalLine.matches(".+\\((.+?)\\) (.+?) \\((.+?)\\).*")){
			header.set(2, "# Name goes here: " + firstGlobalLine.replaceAll(".+\\((.+?)\\) (.+?) \\((.+?)\\).*", "$2"));
			header.set(3, header.get(3) + " " + firstGlobalLine.replaceAll(".+\\((.+?)\\) (.+?) \\((.+?)\\).*", "$1"));
			header.set(5, firstGlobalLine.replaceAll(".+\\((.+?)\\) .+? \\((.+?)\\).*", "Traits: <<$2>>."));
			globalCardText.remove(0);
		}
		
		cardText.addAll(header);
		cardText.add("");
		
		for(String abilityLine : globalCardText){
			cardText.add("* " + abilityLine);
		}
		return cardText;
	}
}
