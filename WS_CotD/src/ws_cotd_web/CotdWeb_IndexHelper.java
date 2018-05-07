package ws_cotd_web;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import ws_cotd.Cotd_Conf;

public class CotdWeb_IndexHelper {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void updateSeriesIndex(ArrayList<CotdWeb_Card> cards) throws Exception{

		String fecha = CotdWeb_Parser.getDateFromTemporal();
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();

		for(String seriesId : series.keySet()) {

			String indexFilePath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/index.html";
			File indexFile = new File(indexFilePath);
			ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(indexFile.toPath(), StandardCharsets.UTF_8));
			
			for(CotdWeb_Card card : cards) {
				
				if(card.seriesId.equals(seriesId)) {
					
					String currentEntry = "";
					for(String content : indexContent) {
						if(content.contains(card.fileId)) {
							currentEntry = content;
						}
					}
					String newEntry = fecha + "<a href='./cards/" + card.fileId + ".html'><img src='./images/" + card.fileId + ".png' width=100% height=auto id='" + card.fileId + "'></img></a>" + card.idLine;
					indexContent.set(indexContent.indexOf(currentEntry), newEntry);
				}
			}

			indexContent = CotdWeb_IndexHelper.getUpdatedIndexWithCompletionCount(indexContent, 100);
			
			System.out.println("* Updating Index Page: " + seriesId);
			
			Files.write(indexFile.toPath(), indexContent, StandardCharsets.UTF_8);
		}
	}
	
	public static ArrayList<String> getUpdatedIndexWithCompletionCount(ArrayList<String> indexContent, int size) throws Exception{
		
		//System.out.println("** Get Updated Index Completion Count");
		
		if(indexContent.indexOf("Recuento") > 0) {
		
			int countC = 0;
			int countU = 0;
			int countR = 0;
			int countRR = 0;
			int countCC = 0;
			int countCR = 0;
			
			for(int i = 0; i < indexContent.size(); i++){
				if(indexContent.get(i).endsWith(" C")){
					countC++;
				}
				if(indexContent.get(i).endsWith(" U")){
					countU++;
				}
				if(indexContent.get(i).endsWith(" R")){
					countR++;
				}
				if(indexContent.get(i).endsWith(" RR")){
					countRR++;
				}
				if(indexContent.get(i).endsWith(" CC")){
					countCC++;
				}
				if(indexContent.get(i).endsWith(" CR")){
					countCR++;
				}
			}
			
			//System.out.println("* Count RR: " + countRR);
			int iRR = indexContent.indexOf("<td align=center id='RR_Count'>");
			indexContent.set(iRR + 1, String.valueOf(countRR));
			
			//System.out.println("* Count R: " + countR);
			int iR = indexContent.indexOf("<td align=center id='R_Count'>");
			indexContent.set(iR + 1, String.valueOf(countR));
			
			//System.out.println("* Count U: " + countU);
			int iU = indexContent.indexOf("<td align=center id='U_Count'>");
			indexContent.set(iU + 1, String.valueOf(countU));
			
			//System.out.println("* Count C: " + countC);
			int iC = indexContent.indexOf("<td align=center id='C_Count'>");
			indexContent.set(iC + 1, String.valueOf(countC));
			
			//System.out.println("* Count CR: " + countCR);
			int iCR = indexContent.indexOf("<td align=center id='CR_Count'>");
			indexContent.set(iCR + 1, String.valueOf(countCR));
			
			//System.out.println("* Count CC: " + countCC);
			int iCC = indexContent.indexOf("<td align=center id='CC_Count'>");
			indexContent.set(iCC + 1, String.valueOf(countCC));
			
			int totalCount = countRR + countR + countU + countC + countCR + countCC;
			//System.out.println("* Total Count: " + totalCount);
			int iTC = indexContent.indexOf("<td align=center id='Total_Count'>");
			indexContent.set(iTC + 1, String.valueOf(totalCount) + "/" + String.valueOf(size));
		}
		
		return indexContent;
	}
	
	public static void createEmptyIndex(String seriesFullId, String seriesName) throws Exception{
		
		String indexTemplateFilePath = conf.webFolder.getAbsolutePath() + "\\templates\\indexTemplate.html";
		File indexTemplateFile = new File(indexTemplateFilePath);
		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(indexTemplateFile.toPath(), StandardCharsets.UTF_8));

		String seriesId = seriesFullId.split("/")[1].toLowerCase();
		
		for(int i = 0; i < indexContent.size(); i++){
			
			String line = indexContent.get(i);
			
			line = line.replace("SeriesIdRef", seriesId);
			line = line.replace("SeriesIdFull", seriesFullId);
			line = line.replace("{Series Name}", seriesName);
			
			indexContent.set(i, line);
		}
		
		System.out.println("* Creating Index Page: " + seriesId);
		
		String indexFilePath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/index.html";
		File indexFile = new File(indexFilePath);
		Files.write(indexFile.toPath(), indexContent, StandardCharsets.UTF_8);	
		
	}
}
