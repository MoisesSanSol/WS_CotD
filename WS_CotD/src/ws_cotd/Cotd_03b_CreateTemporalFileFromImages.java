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
		main.generateTemporalFileFromImages();
		
		Cotd_Utilities.openFileInNotepad(main.conf.temporalFile);
		
		System.out.println("*** Finished ***");
	}
	
	private void generateTemporalFileFromImages() throws Exception{
		
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
		temporalContent.add("Name goes here");
		temporalContent.add(seriesId);
		
		List<String> fromImagesContent = new ArrayList<>(Files.readAllLines(conf.fromImagesFile.toPath(), StandardCharsets.UTF_8));
		
		while(fromImagesContent.size() > 0){
			
			String line = fromImagesContent.remove(0);
			
			if(line.startsWith("-")){
				temporalContent.add("");
				temporalContent.add("Abilities go here");
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
					temporalContent.add("Name goes here");
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
	
	private String translateHeaders(String headers){
		
		String newHeaders = headers.replace("Power", "Poder");
		newHeaders = newHeaders.replace("Yellow Character", "Personaje Amarillo");
		newHeaders = newHeaders.replace("Green Character", "Personaje Verde"); 
		newHeaders = newHeaders.replace("Red Character", "Personaje Rojo");
		newHeaders = newHeaders.replace("Blue Character", "Personaje Azul");
		newHeaders = newHeaders.replace("Yellow Event", "Evento Amarillo");
		newHeaders = newHeaders.replace("Green Event", "Evento Verde");
		newHeaders = newHeaders.replace("Red Event", "Evento Rojo");
		newHeaders = newHeaders.replace("Blue Event", "Evento Azul");
		newHeaders = newHeaders.replace("Yellow Climax", "Climax Amarillo");
		newHeaders = newHeaders.replace("Green Climax", "Climax Verde");
		newHeaders = newHeaders.replace("Red Climax", "Climax Rojo");
		newHeaders = newHeaders.replace("Blue Climax", "Climax Azul");
		newHeaders = newHeaders.replace("Level", "Nivel");
		newHeaders = newHeaders.replace("Cost", "Coste");
		newHeaders = newHeaders.replace("Power", "Poder");
		newHeaders = newHeaders.replace(">> and <<",">> y <<");
		newHeaders = newHeaders.replace("None","No");
		newHeaders = newHeaders.replace("Icon","Icono");		
		
		return newHeaders;
	}
}
