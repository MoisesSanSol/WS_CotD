package ws_cotd_web_en;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import ws_cotd.Cotd_Conf;
import ws_cotd_web.CotdWeb_IndexHelper;
import ws_cotd_web.CotdWeb_Parser;

public class CotdWebEn_Web {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		System.out.println("** Generate En Web Content From Es Web");

		CotdWebEn_Web.convertIndex();
		CotdWebEn_Web.convertPages();
		
		System.out.println("*** Finished ***");
	}
	
	public static void convertIndex() throws Exception{
		
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();
		for(String seriesId : series.keySet()) {
			CotdWebEn_Web.convertIndex(seriesId, "index.html");
			CotdWebEn_Web.convertIndex(seriesId, "cartasporfecha.html");
		}
		
	}
	
	public static void convertIndex(String seriesId, String indexFileName) throws Exception{
		
		String indexFilePath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/" + indexFileName;
		File indexFile = new File(indexFilePath);

		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(indexFile.toPath(), StandardCharsets.UTF_8));
		
		for(int i = 0;i < indexContent.size(); i++){
			
			String line = indexContent.get(i);
			
			line = line.replace("Cartas del día", "Cards of the day");
			line = line.replace("por fecha", "by date");
			line = line.replace("por id", "by id");
			line = line.replace("Recuento", "Progress");
			line = line.replace("Página de producto", "Product Page");
			line = line.replace("Otras Colecciones", "Main Page");
			line = line.replace("./images/", "../../" + seriesId + "/images/");
			
			line = line.replaceAll("no_image_(.+?)\\.png", "no_image_$1.jpg");
			
			indexContent.set(i, line);
		}
		
		String enIndexFilePath = conf.webFolder.getAbsolutePath() + "/en/" + seriesId + "/" + indexFileName;
		File enIndexFile = new File(enIndexFilePath);
		Files.write(enIndexFile.toPath(), indexContent, StandardCharsets.UTF_8);
	}

	public static void convertPages() throws Exception{
		
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();
		for(String seriesId : series.keySet()) {
			
			String cardsFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/cards/";

			File cardsFolder = new File(cardsFolderPath);
			File[] cardFiles = cardsFolder.listFiles();
			
			CotdWebEn_Web.convertPages(cardFiles, seriesId, "cards");
			
			cardsFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/cardsbydate/";

			cardsFolder = new File(cardsFolderPath);
			cardFiles = cardsFolder.listFiles();
			
			CotdWebEn_Web.convertPages(cardFiles, seriesId, "cardsbydate");
		}
	}
	
	public static void convertPages(File[] cardFiles, String seriesId, String folderPath) throws Exception{
		
		HashMap<String,String> abilityTranslations = CotdWebEn_FileHelper.getSeriesAbilityTranslations(seriesId);
		String enCardsFolderPath = conf.webFolder.getAbsolutePath() + "/en/" + seriesId + "/" + folderPath + "/";
		
		for(File cardFile : cardFiles){
			
			ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(cardFile.toPath(), StandardCharsets.UTF_8));
			
			for(int i = 0;i < indexContent.size(); i++){
				
				String line = indexContent.get(i);
				
				line = line.replace("Carta del día", "Card of the day");
				line = line.replace("Colección Completa", "Full Set");
				line = line.replace(">Anterior: ", ">Previous: ");
				line = line.replace(">Siguiente: ", ">Next: ");
				
				line = line.replace("../images/", "../../../" + seriesId + "/images/");
				
				line = line.replace(", Nivel: ", ", Level: ");
				line = line.replace(", Coste: ", ", Cost: ");
				line = line.replace(", Poder: ", ", Power: ");
				line = line.replace("&gt;&gt; y &lt;&lt;", "&gt;&gt; and &lt;&lt;");
				line = line.replaceAll("Traits: &lt;&lt;(.+?)&gt;&gt;", "Traits: [$1]");
				line = line.replaceAll("Traits: (.+?)and &lt;&lt;(.+?)&gt;&gt;", "Traits: $1and [$2]");
				line = line.replaceAll("^Personaje (.+?),", "$1 Character,");
				line = line.replaceAll("^Evento (.+?),", "$1 Event,");
				line = line.replaceAll("^Climax (.+?),", "$1 Climax,");
				line = line.replaceAll("^Amarillo", "Yellow");
				line = line.replaceAll("^Verde", "Green");
				line = line.replaceAll("^Rojo", "Red");
				line = line.replaceAll("^Azul", "Blue");
				
				line = line.replace("* Esta carta es referenciada en las habilidades de", "This card is referenced by the abilities of ");
				line = line.replaceAll("# Aún no se conoce el Climax '(.+)'\\.", "# Climax '$1' has not been revealed yet.");
				
				for(String translation : abilityTranslations.keySet()){
					line = line.replaceAll(translation, abilityTranslations.get(translation));
				}
				
				indexContent.set(i, line);
			}
			
			String enCardFilePath = enCardsFolderPath + cardFile.getName();
			File enCardFile = new File(enCardFilePath);
			Files.write(enCardFile.toPath(), indexContent, StandardCharsets.UTF_8);	
		}
	}
	
}
