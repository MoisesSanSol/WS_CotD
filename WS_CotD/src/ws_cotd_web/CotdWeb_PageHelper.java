package ws_cotd_web;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ws_cotd.Cotd_Conf;
import ws_cotd.Cotd_Utilities;

public class CotdWeb_PageHelper {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	public static HashMap<String,String> series;

	public static void createCardPages(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		CotdWeb_PageHelper.series = CotdWeb_Parser.getSeriesFromCurrentSeries();
		
		for(CotdWeb_Card card : cards){
			if(card.rarity.equals("SR") || card.rarity.equals("RRR") || card.rarity.equals("SP")){
				CotdWeb_PageHelper.createParallelCardPage(card);
			}
			else{
				CotdWeb_PageHelper.createCardPage(card);
			}
		}
	}
	
	public static void createParallelCardPage(CotdWeb_Card card) throws Exception{
		
		String originalFileId = card.fileId.replaceAll("SP?$", "").replaceAll("R$", "");
		String originalId = card.id.replaceAll("SP?$", "").replaceAll("R$", "");
		
		String originalPageFilePath = conf.webFolder.getAbsolutePath() + "/" + card.seriesId + "/cards/" + originalFileId + ".html";
		File originalPageFile = new File(originalPageFilePath);
		
		ArrayList<String> originalContent = new ArrayList<String>(Files.readAllLines(originalPageFile.toPath(), StandardCharsets.UTF_8));
		ArrayList<String> parallelContent = (ArrayList<String>)originalContent.clone();
		
		int titleIndex = originalContent.indexOf(originalId);
		parallelContent.set(titleIndex, card.id);
		
		int imgIndex = originalContent.indexOf("<img src='../images/" + originalFileId + ".png'></img>");
		parallelContent.set(imgIndex, "<img src='../images/" + card.fileId + ".png'></img>");
		
		int idIndex = originalContent.indexOf("<td id='idLine'>") + 1;
		String originalIdLine = originalContent.get(idIndex);
		String originalRarity = originalIdLine.split(" ")[1];

		int indexIndex = originalContent.indexOf("<a href='../index.html'>Colección Completa</a>");
		if(indexIndex == -1){
			indexIndex = originalContent.indexOf("<a href='../index.html#trialDeck'>Colección Completa</a>");
		}
		parallelContent.set(indexIndex, "<a href='../index.html#paralelas'>Colección Completa</a>");
		
		String originalUrl = "(<a href='./" + originalFileId + ".html'>" + originalRarity + "</a>)";
		String newUrl = "(<a href='./" + card.fileId + ".html'>" + card.rarity + "</a>)";
		
		originalContent.set(idIndex, originalId + " " + originalRarity + " " + newUrl);
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
		templateContent.set(templateContent.indexOf("[Series]"), CotdWeb_PageHelper.series.get(card.seriesId));
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
			String references = "<tr>\r\n<td>\r\n* Esta carta es referenciada en las habilidades de '";
			references = references + card.references.remove(0) + "'\r\n";
			while(!card.references.isEmpty()) {
				references = references + "\r\n<br>\r\n* Esta carta es referenciada en las habilidades de '" + card.references.remove(0) + "'\r\n";;
			}
			references = references + "</td>\r\n</tr>";
			templateContent.set(templateContent.indexOf("[Referencias]"), references);
		}
		else {
			templateContent.remove(templateContent.indexOf("[Referencias]"));
		}
		
		if(!card.notes.isEmpty()) {
			String notes = "<tr>\r\n<td>\r\n";
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
	
	public static String getIdLine(File cardPage) throws Exception{
		
		String idLine;
		
		Document doc = Jsoup.parse(cardPage, "UTF-8", "irrelevant");
		
		Element idDom = doc.select("#idLine").first();
		
		idLine = CotdWeb_CardListHelper.escapeFromHtml(idDom.text().trim());
		
		return idLine;
	}
}
