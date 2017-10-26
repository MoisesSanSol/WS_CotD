package ws_cotd;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

public class Cotd_03_CreateTemporalFile {

	private Cotd_Conf conf;
	
	public Cotd_03_CreateTemporalFile(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		Cotd_03_CreateTemporalFile main = new Cotd_03_CreateTemporalFile();
		main.generateTemporalFileFromOutakuya();
		
		System.out.println("*** Finished ***");
	}
	
	public void generateTemporalFileFromOutakuya() throws Exception{
		
		System.out.println("** Generate Temporal File From Outakuya");
		
		ArrayList<String> temporalContent = new ArrayList<String>();  
		
		temporalContent.add("****************************************");
		String dateLine = "Cartas del día " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ":";
		temporalContent.add(dateLine);
		temporalContent.add("");
		
		List<String> currentSeriesContent = new ArrayList<>(Files.readAllLines(conf.currentSeriesFile.toPath(), StandardCharsets.UTF_8));
		String seriesHeader = currentSeriesContent.get(0);
		String seriesId = currentSeriesContent.get(1);
		
		temporalContent.add(seriesHeader);
		temporalContent.add("");
		
		List<String> fromOutakuyaContent = new ArrayList<>(Files.readAllLines(conf.fromOutakuyaFile.toPath(), StandardCharsets.ISO_8859_1));
		
		while(fromOutakuyaContent.size() >= 3){
			
			fromOutakuyaContent.remove(0);
			String cardHeader = StringEscapeUtils.unescapeHtml4(fromOutakuyaContent.remove(0));
			
			ArrayList<String> splitHeader = new ArrayList<>(Arrays.asList(cardHeader.split("<br> ")));
			temporalContent.add(splitHeader.remove(0));
			temporalContent.add(seriesId);
			temporalContent.addAll(splitHeader);
			temporalContent.add("");
			
			String cardText = StringEscapeUtils.unescapeHtml4(fromOutakuyaContent.remove(0));
			ArrayList<String> splitText = new ArrayList<>(Arrays.asList(cardText.split("<br> ")));
			for(String text : splitText){
				temporalContent.add("*" + text);
			}
			
			temporalContent.add("");
			temporalContent.add("-");
		}
		
		Files.write(conf.temporalFile.toPath(), temporalContent, StandardCharsets.UTF_8);
	}
}
