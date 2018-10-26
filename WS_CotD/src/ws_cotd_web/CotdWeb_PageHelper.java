package ws_cotd_web;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;

public class CotdWeb_PageHelper {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	//public static HashMap<String,String> series;

	public static void createCardPages(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		for(CotdWeb_Card card : cards){
			if(card.isParallel){
				CotdWeb_PageHelper.createParallelCardPage(card);
			}
			else{
				CotdWeb_PageHelper.createCardPage(card);
			}
		}
	}
	
	public static void createParallelCardPage(CotdWeb_Card card) throws Exception{
		
		String originalPageFilePath = conf.webFolder.getAbsolutePath() + "/" + card.seriesId + "/cards/" + card.baseFileId + ".html";
		File originalPageFile = new File(originalPageFilePath);
		
		ArrayList<String> originalContent = new ArrayList<String>(Files.readAllLines(originalPageFile.toPath(), StandardCharsets.UTF_8));
		ArrayList<String> parallelContent = (ArrayList<String>)originalContent.clone();
		
		int titleIndex = originalContent.indexOf(card.baseId);
		parallelContent.set(titleIndex, card.id);
		
		int imgIndex = originalContent.indexOf("<img src='../images/" + card.baseFileId + ".png'></img>");
		parallelContent.set(imgIndex, "<img src='../images/" + card.fileId + ".png'></img>");
		
		int idIndex = originalContent.indexOf("<td id='idLine'>") + 1;
		String originalIdLine = originalContent.get(idIndex);
		String originalRarity = originalIdLine.split(" ")[1];
		
		String navigation = parallelContent.get(parallelContent.size() - 2);
		navigation = navigation.replaceAll("index\\.html(#trialDeck)?", "index.html#paralelas");
		parallelContent.set(parallelContent.size() - 2, navigation);
		
		String originalUrl = "(<a href='./" + card.baseFileId + ".html'>" + originalRarity + "</a>)";
		String newUrl = "(<a href='./" + card.fileId + ".html'>" + card.rarity + "</a>)";
		if(card.fileId.matches(".+[b-z]$")) {
			originalUrl = originalUrl.replace(">" + originalRarity + "<",">a<");
			String letter = card.fileId.substring(card.fileId.length() - 1);
			newUrl = newUrl.replace(">" + card.rarity + "<",">" + letter + "<");
		}
		
		
		originalContent.set(idIndex, originalIdLine + " " + newUrl);
		parallelContent.set(idIndex, card.idLine + " " + originalUrl);
		
		System.out.println("* Updating Web Page: " + card.fileId);
		
		Files.write(originalPageFile.toPath(), originalContent, StandardCharsets.UTF_8);
		
		String newPageFilePath = conf.webFolder.getAbsolutePath() + "/" + card.seriesId + "/cards/" + card.fileId + ".html";
		File newPageFile = new File(newPageFilePath);

		System.out.println("* Creating Web Page: " + card.fileId);
		
		Files.write(newPageFile.toPath(), parallelContent, StandardCharsets.UTF_8);
	}

	public static void createCardPage(CotdWeb_Card card) throws Exception{
		
		String templateFilePath = CotdWeb_PageHelper.conf.webTemplatesFolder.getAbsolutePath() + "\\cardTemplate.html";
		File templateFile = new File(templateFilePath);
		
		ArrayList<String> templateContent = new ArrayList<String>(Files.readAllLines(templateFile.toPath(), StandardCharsets.UTF_8));

		templateContent.set(templateContent.indexOf("[Title Line]"), card.id);
		templateContent.set(templateContent.indexOf("[Series]"), card.seriesFullName);
		String imageLine = "<img src='../images/" + card.fileId + ".png'></img>";
		templateContent.set(templateContent.indexOf("[Image Line]"), imageLine);
		
		
		templateContent.set(templateContent.indexOf("[Nombre]"), card.name);
		templateContent.set(templateContent.indexOf("[Nombre Jp]"), card.jpName);
		templateContent.set(templateContent.indexOf("[Card Id Line]"), card.idLine);
		templateContent.set(templateContent.indexOf("[Caracteristicas]"), card.statsLine);
		
		String abilities = card.abilities.remove(0);
		while(!card.abilities.isEmpty()) {
			abilities = abilities + "\r\n<br>\r\n" + card.abilities.remove(0);
		}
		templateContent.set(templateContent.indexOf("[Habilidades]"), abilities);
		
		if(!card.references.isEmpty()) {
			String references = "<tr>\r\n<td id='references'>\r\n* Esta carta es referenciada en las habilidades de '";
			references = references + card.references.remove(0) + "'\r\n";
			while(!card.references.isEmpty()) {
				references = references + "<br>\r\n* Esta carta es referenciada en las habilidades de '" + card.references.remove(0) + "'\r\n";
			}
			references = references + "</td>\r\n</tr>";
			templateContent.set(templateContent.indexOf("[Referencias]"), references);
		}
		else {
			templateContent.remove(templateContent.indexOf("[Referencias]"));
		}
		
		if(!card.notes.isEmpty()) {
			String notes = "<tr>\r\n<td id='notes'>\r\n";
			notes = notes + card.notes.remove(0) ;
			while(!card.notes.isEmpty()) {
				notes = notes + "\r\n<br>\r\n" + card.notes.remove(0);
			}
			notes = notes + "\r\n</td>\r\n</tr>";
			templateContent.set(templateContent.indexOf("[Notas]"), notes);
		}
		else {
			templateContent.remove(templateContent.indexOf("[Notas]"));
		}
		
		if(card.rarity.equals("TD")){
			int indexIndex = templateContent.indexOf("<a href='../index.html'>Colección Completa</a>");
			templateContent.set(indexIndex, "<a href='../index.html#trialDeck'>Colección Completa</a>");
		}
		
		String pageFilePath = conf.webFolder.getAbsolutePath() + "/" + card.seriesId + "/cards/" + card.fileId + ".html";
		File pageFile = new File(pageFilePath);
		
		System.out.println("* Creating Web Page: " + card.fileId);
		
		Files.write(pageFile.toPath(), templateContent, StandardCharsets.UTF_8);
		
		if(card.needsManualUpdate){
			System.out.println("* Web Page for card " + card.fileId + " needs to be manually updated.");
			Cotd_Utilities.openFileInNotepad(pageFile);
			Cotd_Utilities.openFolder(pageFile.getParentFile());
		}
		
	}
	
	public static String getCardColor(File cardPage) throws Exception{
		String color = "¿No Color?";
		
		ArrayList<String> pageContent = new ArrayList<String>(Files.readAllLines(cardPage.toPath(), StandardCharsets.UTF_8));
		
		int index = pageContent.indexOf("<td id='stats'>");
		if(index != -1){
			String statsLine = pageContent.get(index + 1);
			if(statsLine.contains("Amarillo")){
				color = "Amarillo";
			}
			else if(statsLine.contains("Verde")){
				color = "Verde";
			}
			else if(statsLine.contains("Rojo")){
				color = "Rojo";
			}
			else if(statsLine.contains("Azul")){
				color = "Azul";
			}
		}
		
		return color;
	}
	
	public static ArrayList<String> getCardAbilities(File cardPage) throws Exception{
		
		ArrayList<String> abilities = new ArrayList<String>();
		
		Document doc = Jsoup.parse(cardPage, "UTF-8", "irrelevant");
		
		Element stats = doc.select("#stats").first();
		Element abilitiesDom = stats.parent().nextElementSibling();
		
		String[] abilitiesSplit = abilitiesDom.html().replaceAll("</?td>", "").split("<br>");
		
		for(String ability : abilitiesSplit){
			ability = ability.replaceAll("<.+?>", "");
			abilities.add(CotdWeb_CardListHelper.escapeFromHtml(ability).trim());
		}
		
		return abilities;
	}
	
	public static ArrayList<String> getCardStats(File cardPage) throws Exception{
		
		ArrayList<String> stats = new ArrayList<String>();
		
		Document doc = Jsoup.parse(cardPage, "UTF-8", "irrelevant");
		
		Element statsDom = doc.select("#stats").first();
		
		String[] statsSplit = statsDom.html().replaceAll("</?td>", "").split("<br>");
		
		for(String statLine : statsSplit){
			statLine = statLine.replaceAll("<.+?>", "");
			statLine = statLine.replaceAll(", Traits", ",\r\nTraits");
			stats.add(CotdWeb_CardListHelper.escapeFromHtml(statLine).trim());
		}
		
		return stats;
	}
	
	public static ArrayList<String> getCardRefereneces(File cardPage) throws Exception{
		
		ArrayList<String> references = new ArrayList<String>();
		
		Document doc = Jsoup.parse(cardPage, "UTF-8", "irrelevant");
		
		Element statsDom = doc.select("#references").first();
		
		String[] statsSplit = statsDom.html().split("<br>");
		
		for(String statLine : statsSplit){
			references.add(CotdWeb_CardListHelper.escapeFromHtml(statLine).trim());
		}
		
		return references;
	}
	
	public static ArrayList<String> getCardNotes(File cardPage) throws Exception{
		
		ArrayList<String> notes = new ArrayList<String>();
		
		Document doc = Jsoup.parse(cardPage, "UTF-8", "irrelevant");
		
		Element statsDom = doc.select("#notes").first();
		
		String[] statsSplit = statsDom.html().split("<br>");
		
		for(String statLine : statsSplit){
			notes.add(CotdWeb_CardListHelper.escapeFromHtml(statLine).trim());
		}
		
		return notes;
	}
	
	public static ArrayList<String> getCardName(File cardPage) throws Exception{
		
		ArrayList<String> names = new ArrayList<String>();
		
		Document doc = Jsoup.parse(cardPage, "UTF-8", "irrelevant");
		
		Element namesDom = doc.select("#name").first();
		
		String[] namesSplit = namesDom.html().replaceAll("</?td>", "").split("<br>");
		
		for(String name : namesSplit){
			name = name.replaceAll("<.+?>", "");
			names.add(CotdWeb_CardListHelper.escapeFromHtml(name).trim());
		}
		
		return names;
	}
	
	public static String getCardIdLine(File cardPage) throws Exception{
		
		String idLine;
		
		Document doc = Jsoup.parse(cardPage, "UTF-8", "irrelevant");
		
		Element idDom = doc.select("#idLine").first();
		
		idLine = CotdWeb_CardListHelper.escapeFromHtml(idDom.text().trim());
		
		return idLine;
	}
	
	public static String getCardProduct(File cardPage) throws Exception{
		
		String product;
		
		Document doc = Jsoup.parse(cardPage, "UTF-8", "irrelevant");
		
		Element idDom = doc.select("#product").first();
		
		product = CotdWeb_CardListHelper.escapeFromHtml(idDom.text().trim());
		
		return product;
	}
	
	public static void updatePreviousNextLinks() throws Exception{
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();

		for(String seriesId : series.keySet()) {

			String cardsFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/cards/";
			File cardsFolder = new File(cardsFolderPath);
			
			File[] cardFiles = cardsFolder.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return !name.contains("_T") && !name.contains("_P") && name.matches(".+\\d\\.html$");
			    }
			});
			
			if(cardFiles.length > 1){
				Arrays.sort(cardFiles);
				CotdWeb_PageHelper.updatePreviousNextLinks(cardFiles);
			}
			
			File[] tdCardFiles = cardsFolder.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.contains("_T") && name.matches(".+\\d\\.html$");
			    }
			});
			
			if(tdCardFiles.length > 1){
				Arrays.sort(tdCardFiles);
				CotdWeb_PageHelper.updatePreviousNextLinks(tdCardFiles);
			}
			
			File[] prCardFiles = cardsFolder.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.contains("_P") && name.matches(".+\\d\\.html$");
			    }
			});
			
			if(prCardFiles.length > 1){
				Arrays.sort(prCardFiles);
				CotdWeb_PageHelper.updatePreviousNextLinks(prCardFiles);
			}
		}
	}
	
	public static void updatePreviousNextLinks(File[] cardFiles) throws Exception{
		
		String previousId = cardFiles[0].getName();
		String previousName = CotdWeb_PageHelper.getCardName(cardFiles[0]).get(0);
		String nextId = cardFiles[1].getName();
		String nextName = CotdWeb_PageHelper.getCardName(cardFiles[1]).get(0);
		
		CotdWeb_PageHelper.updatePreviousNextLinks(cardFiles[0], null, null, nextId, nextName);
		
		for(int i = 1; i < (cardFiles.length-1); i++) {
			
			String currentId = nextId;
			String currentName = nextName;
			nextId = cardFiles[i+1].getName();
			nextName =CotdWeb_PageHelper.getCardName(cardFiles[i+1]).get(0);
			
			CotdWeb_PageHelper.updatePreviousNextLinks(cardFiles[i], previousId, previousName, nextId, nextName);
			
			previousId = currentId;
			previousName = currentName;

		}
		
		CotdWeb_PageHelper.updatePreviousNextLinks(cardFiles[cardFiles.length-1], previousId, previousName, null, null);
	}
	
	public static void updatePreviousNextLinks(File cardFile, String previousId, String previousName, String nextId, String nextName) throws Exception{
		ArrayList<String> cardContent = new ArrayList<String>(Files.readAllLines(cardFile.toPath(), StandardCharsets.UTF_8));
		
		String navigation = "<a href='../index.html'>Colección Completa</a>";
		
		if(previousId != null){
			navigation = navigation + "<br><a href='./" + previousId +"'>Anterior: " + previousName + "</a>";
			if(previousId.contains("_T")){
				navigation = navigation.replace("index.html'", "index.html#trialDeck'");
			}
		}
		if(nextId != null){
			navigation = navigation + "<br><a href='./" + nextId +"'>Siguiente: " + nextName + "</a>";
			if(nextId.contains("_T")){
				navigation = navigation.replace("index.html'", "index.html#trialDeck'");
			}
		}
		
		cardContent.set(cardContent.size() - 2, navigation);
		
		Files.write(cardFile.toPath(), cardContent, StandardCharsets.UTF_8);
	}
	
	public static void getTemporalFromCardPages() throws Exception{
		
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();

		for(String seriesId : series.keySet()) {

			ArrayList<String> temporalContent = new ArrayList<String>();
			
			temporalContent.add("****************************************");
			temporalContent.add("Cartas del día Reversed:");
			temporalContent.add("");
			temporalContent.add(series.get(seriesId));
			
			String cardsFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/cards/";
			File cardsFolder = new File(cardsFolderPath);
			File[] cardFiles = cardsFolder.listFiles();
			Arrays.sort(cardFiles);
			
			
			for(File cardPage : cardFiles) {
			
				temporalContent.add("");
				temporalContent.add(CotdWeb_PageHelper.getCardName(cardPage).get(0));
				temporalContent.add(CotdWeb_PageHelper.getCardName(cardPage).get(1));
				temporalContent.add(CotdWeb_PageHelper.getCardIdLine(cardPage));
				temporalContent.addAll(CotdWeb_PageHelper.getCardStats(cardPage));
				temporalContent.add("");
				temporalContent.addAll(CotdWeb_PageHelper.getCardAbilities(cardPage));
				temporalContent.add("");
				temporalContent.add("-");
				
				
				String reverseTemporalPath = conf.mainFolder.getAbsolutePath() + "/ReverseTemporal.txt";
				File reverseTemporal = new File(reverseTemporalPath);
				Files.write(reverseTemporal.toPath(), temporalContent, StandardCharsets.UTF_8);

			}
		}
	}
}
