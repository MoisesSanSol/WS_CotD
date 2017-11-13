package ws_cotd;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

public class Cotd_03a_CreateTemporalFileFromOutakuya {

	private Cotd_Conf conf;
	
	public Cotd_03a_CreateTemporalFileFromOutakuya(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		Cotd_03a_CreateTemporalFileFromOutakuya main = new Cotd_03a_CreateTemporalFileFromOutakuya();
		main.generateTemporalFileFromOutakuya();
		
		Cotd_Utilities.openFileInNotepad(main.conf.temporalFile);
		
		System.out.println("*** Finished ***");
	}
	
	private void generateTemporalFileFromOutakuya() throws Exception{
		
		System.out.println("** Generate Temporal File From Outakuya");
		
		ArrayList<String> temporalContent = new ArrayList<String>();  
		
		temporalContent.add("****************************************");
		String dateLine = "Cartas del día " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ":";
		temporalContent.add(dateLine);
		temporalContent.add("");
		
		List<String> currentSeriesContent = new ArrayList<>(Files.readAllLines(conf.currentSeriesFile.toPath(), StandardCharsets.UTF_8));
		String seriesHeader = "";
		String seriesId = "";
		
		List<String> fromOutakuyaContent = new ArrayList<>(Files.readAllLines(conf.fromOutakuyaFile.toPath(), StandardCharsets.UTF_8));
		
		while(fromOutakuyaContent.size() >= 3){
			
			String imageLine = fromOutakuyaContent.remove(0);
			
			if(imageLine.startsWith("-")){
				seriesHeader = currentSeriesContent.remove(0);
				seriesId = currentSeriesContent.remove(0);
				temporalContent.add(seriesHeader);
			}
			else{
				temporalContent.add("-");
			}
			
			String cardHeader = this.translateHeaders(fromOutakuyaContent.remove(0));
			
			ArrayList<String> splitHeader = new ArrayList<>(Arrays.asList(cardHeader.split("<br> ")));
			temporalContent.add("");
			temporalContent.add(splitHeader.remove(0));
			temporalContent.add(seriesId);
			temporalContent.addAll(splitHeader);
			temporalContent.add("");
			
			String cardText = fromOutakuyaContent.remove(0);
			ArrayList<String> splitText = new ArrayList<>(Arrays.asList(cardText.split("<br> ")));
			for(String text : splitText){
				temporalContent.add("*" + text);
			}
			
			temporalContent.add("");
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
