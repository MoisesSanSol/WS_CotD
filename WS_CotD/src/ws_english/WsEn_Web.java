package ws_english;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import ws_cotd.Cotd_Conf;
import ws_cotd_web.CotdWeb_IndexHelper;
import ws_cotd_web.CotdWeb_Parser;

public class WsEn_Web {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		System.out.println("** Generate En Web Content From Es Web");

		WsEn_Web.convertIndex();
		WsEn_Web.convertPages();
		
		System.out.println("*** Finished ***");
	}
	
	public static void convertIndex() throws Exception{
		
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();
		for(String seriesId : series.keySet()) {
			
			String indexFilePath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/index.html";
			File indexFile = new File(indexFilePath);

			ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(indexFile.toPath(), StandardCharsets.UTF_8));
			
			for(int i = 0;i < indexContent.size(); i++){
				
				String line = indexContent.get(i);
				
				line = line.replace("Cartas del día", "Cards of the day");
				line = line.replace("Recuento", "Progress");
				line = line.replace("Página de producto", "Product Page");
				line = line.replace("Otras Colecciones", "Main Page");
				line = line.replace("./images/", "../../" + seriesId + "/images/");
				
				line = line.replaceAll("no_image_(.+?)\\.png", "no_image_$1.jpg");
				
				indexContent.set(i, line);
			}
			
			String enIndexFilePath = conf.webFolder.getAbsolutePath() + "/en/" + seriesId + "/index.html";
			File enIndexFile = new File(enIndexFilePath);
			Files.write(enIndexFile.toPath(), indexContent, StandardCharsets.UTF_8);
		}
	}

	public static void convertPages() throws Exception{
		
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();
		for(String seriesId : series.keySet()) {
			
			HashMap<String,String> abilityTranslations = WsEn_FileHelper.getSeriesAbilityTranslations(seriesId);
			
			String cardsFolderPath = conf.webFolder.getAbsolutePath() + "/" + seriesId + "/cards/";
			String enCardsFolderPath = conf.webFolder.getAbsolutePath() + "/en/" + seriesId + "/cards/";
			File cardsFolder = new File(cardsFolderPath);
			File[] cardFiles = cardsFolder.listFiles();
			
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
	
	
	
}
