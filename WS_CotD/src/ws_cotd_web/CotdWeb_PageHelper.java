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
			CotdWeb_PageHelper.createCardPage(card);
		}
	}
	
	public static void createCardPage(CotdWeb_Card card) throws Exception{
		
		String templateFilePath = CotdWeb_PageHelper.conf.webFolder.getAbsolutePath() + "\\templates\\cardTemplate.html";
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
		
		int index = pageContent.indexOf("<td id='Stats'>");
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
		
		Element stats = doc.select("#Stats").first();
		Element abilitiesDom = stats.parent().nextElementSibling();
		
		String[] abilitiesSplit = abilitiesDom.html().replaceAll("</?td>", "").split("<br>");
		
		for(String ability : abilitiesSplit){
			ability = ability.replaceAll("<.+?>", "");
			abilities.add(CotdWeb_CardListHelper.escapeFromHtml(ability));
		}
		
		return abilities;
	}
}
