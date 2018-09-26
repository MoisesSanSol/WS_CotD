package ws_cotd_web;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
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
					String newEntry = fecha + "<a href='./cards/" + card.fileId + ".html'><img src='./images/" + card.fileId + ".jpg' width=100% height=auto id='" + card.fileId + "'></img></a>" + card.idLine;
					for(String content : indexContent) {
						if(content.contains(card.fileId + "'")) {
							currentEntry = content;
						}
					}
					if(!currentEntry.isEmpty()){
						indexContent.set(indexContent.indexOf(currentEntry), newEntry);
					}
					else{
						System.out.println("* Card not found in index: " + card.id);
						System.out.println("* New Line for Reference: ");
						System.out.println(newEntry);
					}
				}
			}

			indexContent = CotdWeb_IndexHelper.getUpdatedIndexWithCompletionCount(indexContent, 100);
			
			System.out.println("* Updating Index Page: " + seriesId);
			
			Files.write(indexFile.toPath(), indexContent, StandardCharsets.UTF_8);
		}
	}
	
	public static void updateSeriesIndex_AfterRelease(String seriesId, ArrayList<String> cardFileIds, String suffix) throws Exception{

		String indexFilePath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/index.html";
		File indexFile = new File(indexFilePath);
		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(indexFile.toPath(), StandardCharsets.UTF_8));
			
		for(String cardFileId : cardFileIds) {
				
			String currentEntry = "";
			String newEntry = "Tras Salida" + "<img src='./images/" + cardFileId + "_" + suffix + ".jpg' width=100% height=auto id='" + cardFileId + "'  style='filter:grayscale(100%);'></img>";
			for(String content : indexContent) {
				if(content.contains(cardFileId + "'")) {
					currentEntry = content;
					newEntry = newEntry + content.replaceAll(".+>", "");
				}
			}
			if(!currentEntry.isEmpty()){
				indexContent.set(indexContent.indexOf(currentEntry), newEntry);
			}
			else{
				System.out.println("* Card not found in index: " + cardFileId);
				System.out.println("* New Line for Reference: ");
				System.out.println(newEntry);
			}
		}

		indexContent = CotdWeb_IndexHelper.getUpdatedIndexWithCompletionCount(indexContent, 100);
		
		System.out.println("* Updating Index Page: " + seriesId);
		
		Files.write(indexFile.toPath(), indexContent, StandardCharsets.UTF_8);
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
			String totalRR_Count = indexContent.get(iRR + 1).split("/")[1];
			indexContent.set(iRR + 1, String.valueOf(countRR) + "/" + totalRR_Count);
			
			//System.out.println("* Count R: " + countR);
			int iR = indexContent.indexOf("<td align=center id='R_Count'>");
			String totalR_Count = indexContent.get(iR + 1).split("/")[1];
			indexContent.set(iR + 1, String.valueOf(countR) + "/" + totalR_Count);
			
			//System.out.println("* Count U: " + countU);
			int iU = indexContent.indexOf("<td align=center id='U_Count'>");
			String totalU_Count = indexContent.get(iU + 1).split("/")[1];
			indexContent.set(iU + 1, String.valueOf(countU) + "/" + totalU_Count);
			
			//System.out.println("* Count C: " + countC);
			int iC = indexContent.indexOf("<td align=center id='C_Count'>");
			String totalC_Count = indexContent.get(iC + 1).split("/")[1];
			indexContent.set(iC + 1, String.valueOf(countC) + "/" + totalC_Count);
			
			//System.out.println("* Count CR: " + countCR);
			int iCR = indexContent.indexOf("<td align=center id='CR_Count'>");
			String totalCR_Count = indexContent.get(iCR + 1).split("/")[1];
			indexContent.set(iCR + 1, String.valueOf(countCR) + "/" + totalCR_Count);
			
			//System.out.println("* Count CC: " + countCC);
			int iCC = indexContent.indexOf("<td align=center id='CC_Count'>");
			String totalCC_Count = indexContent.get(iCC + 1).split("/")[1];
			indexContent.set(iCC + 1, String.valueOf(countCC) + "/" + totalCC_Count);
			
			int totalCount = countRR + countR + countU + countC + countCR + countCC;
			//System.out.println("* Total Count: " + totalCount);
			int iTC = indexContent.indexOf("<td align=center id='Total_Count'>");
			String total_Count = indexContent.get(iTC + 1).split("/")[1];
			indexContent.set(iTC + 1, String.valueOf(totalCount) + "/" + total_Count);
		}
		
		return indexContent;
	}
	
	public static void createEmptyIndex(String seriesFullId, String seriesName) throws Exception{
		
		String indexTemplateFilePath = conf.webTemplatesFolder.getAbsolutePath() + "\\indexTemplate.html";
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
	
	public static String searchIndexDate(CotdWeb_Card card) throws Exception{

		String fecha = "Not Found";

		String indexFilePath = conf.webFolder.getAbsolutePath() + "/" + card.seriesId + "/index.html";
		File indexFile = new File(indexFilePath);

		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(indexFile.toPath(), StandardCharsets.UTF_8));
			
		for(String line : indexContent) {
			if(line.contains(card.id)) {
				fecha = line.replaceAll("<+", "");
			}
		}
		return fecha;
	}
	
	public static String searchIndexDate(String cardId) throws Exception{

		String fecha = "Not Found";

		String seriesId = cardId.split("-")[0].split("/")[1].toLowerCase();
		String indexFilePath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/index.html";
		File indexFile = new File(indexFilePath);

		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(indexFile.toPath(), StandardCharsets.UTF_8));
			
		for(String line : indexContent) {
			if(line.contains(cardId)) {
				fecha = line.replaceAll("<+", "");
			}
		}
		return fecha;
	}
	
	public static void updateSeriesIndex_ShiftTdNumbers(String seriesId, int firstNumber) throws Exception{

		String indexFilePath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/index.html";
		File indexFile = new File(indexFilePath);
		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(indexFile.toPath(), StandardCharsets.UTF_8));
		
		int count = 1;
		String paddedCount = String.format("%02d", count);
		String newPaddedCount = String.format("%02d", firstNumber);

		for(String line : indexContent) {
			if(line.contains("T" + paddedCount)) {
				String newLine = line.replace(paddedCount, newPaddedCount);
				indexContent.set(indexContent.indexOf(line), newLine);
				count++;
				firstNumber++;
				paddedCount = String.format("%02d", count);
				newPaddedCount = String.format("%02d", firstNumber);
				
			}
		}
		
		System.out.println("* Updating Index Page: Shift Td Numbes for " + seriesId);
		
		Files.write(indexFile.toPath(), indexContent, StandardCharsets.UTF_8);
	}
	
	public static HashMap<String,ArrayList<Integer>> getCardColors(String seriesId) throws Exception{
		
		HashMap<String,ArrayList<Integer>> colorIndexes = new HashMap<String,ArrayList<Integer>>();
		
		ArrayList<Integer> amarillas = new ArrayList<Integer>();
		ArrayList<Integer> verdes = new ArrayList<Integer>();
		ArrayList<Integer> rojas = new ArrayList<Integer>();
		ArrayList<Integer> azules = new ArrayList<Integer>();

		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		String seriesWebPath = conf.webFolder.getPath() + "\\" +  seriesId + "\\";
		String indexPath = seriesWebPath + "index.html";
		
		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(new File(indexPath).toPath(), StandardCharsets.UTF_8));
		
		for (int i = 0; i < indexContent.size(); i++){
			
			String line = indexContent.get(i);
			
			if(line.contains("img") && !line.contains("TD") && !line.contains("PR") && !line.contains("SR") && !line.contains("RRR") && !line.contains("SP") && !line.contains("FXR")){
				
				//System.out.println("** Image for: " + line.substring(line.lastIndexOf(">") + 1));
				
				if(line.contains("href")){
					
					String href = line.replaceAll(".+href='\\./(.+?)'.+", "$1");
					Integer cardNumber =  Integer.parseInt(line.replaceAll(".+href='\\./cards/.+?_(\\d+?).html'.+", "$1"));
					File cardPage = new File(seriesWebPath + href);
					
					String cardColor = CotdWeb_PageHelper.getCardColor(cardPage);
					//System.out.println("* Color: " + cardColor);
					
					if(cardColor.equals("Amarillo")){
						amarillas.add(cardNumber);
					}
					else if(cardColor.equals("Verde")){
						verdes.add(cardNumber);
					}
					else if(cardColor.equals("Rojo")){
						rojas.add(cardNumber);
					}
					else if(cardColor.equals("Azul")){
						azules.add(cardNumber);
					}
				}
			}
		}
		
		colorIndexes.put("Amarillo", amarillas);
		colorIndexes.put("Verde", verdes);
		colorIndexes.put("Rojo", rojas);
		colorIndexes.put("Azul", azules);
		
		return colorIndexes;
	}
	
	public static void updateIndexPendingCardsColor() throws Exception{
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		ArrayList<String> seriesColorsContent = new ArrayList<String>(Files.readAllLines(conf.seriesColors.toPath(), StandardCharsets.UTF_8));
		
		for(String seriesColors : seriesColorsContent) {
			
			String seriesId = seriesColors.split("\t")[0];
			String colors = seriesColors.split("\t")[1];
			boolean tieneAmarillo = colors.contains("Y");
			boolean tieneVerde = colors.contains("G");
			boolean tieneRojo = colors.contains("R");
			boolean tieneAzul = colors.contains("B");
			
			CotdWeb_IndexHelper.updateIndexPendingCardsColor(seriesId, tieneAmarillo, tieneVerde, tieneRojo, tieneAzul);
		}
	}
	
	public static void updateIndexPendingCardsColor(String seriesId, boolean tieneAmarillo, boolean tieneVerde, boolean tieneRojo, boolean tieneAzul) throws Exception{
		
		System.out.println("Updating index with colors for series: " + seriesId);
		
		HashMap<String,ArrayList<Integer>> colorIndexes = CotdWeb_IndexHelper.getCardColors(seriesId);		
		
		boolean hayAmarillo = colorIndexes.get("Amarillo").size() > 0;
		boolean hayVerde = colorIndexes.get("Verde").size() > 0;
		boolean hayRojo = colorIndexes.get("Rojo").size() > 0;
		boolean hayAzul = colorIndexes.get("Azul").size() > 0;
		
		/*for(String color : colorIndexes.keySet()){
			System.out.println("Indexes for color: " + color);
			for(int index : colorIndexes.get(color)){
				System.out.println("Index: " + index);
			}
		}*/
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		String seriesWebPath = conf.webFolder.getPath() + "\\" +  seriesId + "\\";
		String indexPath = seriesWebPath + "index.html";
		
		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(new File(indexPath).toPath(), StandardCharsets.UTF_8));
		
		int maxAmarillo = 0;
		if(hayAmarillo){
			maxAmarillo = Collections.max(colorIndexes.get("Amarillo"));
			indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("y", 1, maxAmarillo, indexContent);
		}
		int maxVerde = -1;
		if(hayVerde){
			int minVerde = Collections.min(colorIndexes.get("Verde"));
			maxVerde = Collections.max(colorIndexes.get("Verde"));
			indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("g", minVerde, maxVerde, indexContent);
			indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("yg", maxAmarillo, minVerde, indexContent);
		}
		int minAzul = 100;
		if(hayAzul){
			minAzul = Collections.min(colorIndexes.get("Azul"));
			indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("b", minAzul, 100, indexContent);
		}
		int minRojo = -1;
		if(hayRojo){
			minRojo = Collections.min(colorIndexes.get("Rojo"));
			int maxRojo = Collections.max(colorIndexes.get("Rojo"));
			indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("r", minRojo, maxRojo, indexContent);
			indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("rb", maxRojo, minAzul, indexContent);
		}
		
		if(tieneVerde) {
			if(!hayVerde && hayRojo){
				indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("ygr", maxAmarillo, minRojo, indexContent);
			}
			
			if(hayVerde && hayRojo){
				indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("gr", maxVerde, minRojo, indexContent);
			}		
			
			if(hayVerde && !hayRojo){
				indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("grb", maxVerde, minAzul, indexContent);
			}
		}
		else {
			if(hayRojo) {
				indexContent = CotdWeb_IndexHelper.updateIndexPendingCardsColor("yr", maxAmarillo, minRojo, indexContent);
			}
		}
		
		Files.write(new File(indexPath).toPath(), indexContent, StandardCharsets.UTF_8);		
	}
	
	public static ArrayList<String> updateIndexPendingCardsColor(String colorRef, int min, int max, ArrayList<String> indexContent) throws Exception{
		
		for (int i = 0; i < indexContent.size(); i++){
			String line = indexContent.get(i);
			if(line.contains("no_image")){
				String mainSeriesId = ".+'pending_.+?_(\\d+?)'.+";
				if(line.matches(mainSeriesId)){
					Integer cardNumber =  Integer.parseInt(line.replaceAll(mainSeriesId, "$1"));
					if(cardNumber >= min && cardNumber <= max){
						indexContent.set(i, line.replaceAll("no_image.*?\\.", "no_image_" + colorRef + "."));
					}
				}
			}
		}
		return indexContent;
	}
}
