package ws_cotd_v2;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import ws_cotd.Cotd_Conf;
import ws_cotd_web.CotdWeb_PageHelper;

public class Cotd_FromGlobal {

	private static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void createTemplate() throws Exception{

		ArrayList<String> templateContent = new ArrayList<String>();
		ArrayList<String> series = new ArrayList<String>(Cotd_CurrentSeries.getCurrentSeries_Raw().keySet());
		
		for(String serie : series) {
			
			templateContent.add(serie);
			templateContent.add("");
		}
		
		templateContent.add("--- Cierre. lòl");
		
		Files.write(conf.fromGlobalFile.toPath(), templateContent, StandardCharsets.UTF_8);
	}
	
	public static ArrayList<String> getParallelCard(String cardId) throws Exception{
		
		ArrayList<String> cardData = new ArrayList<String>();
		
		String seriesId = cardId.split("-")[0].split("/")[1].toLowerCase();
		String cardNumber = cardId.split("-")[1];
		String fileId = seriesId + "_" + cardNumber;
		
		String cardPageFilePath = Cotd_FromGlobal.conf.webFolder.getAbsolutePath() + "\\" + seriesId + "\\cards\\" + fileId + ".html";
		
		File cardPageFile = new File(cardPageFilePath);
		
		cardData.addAll(CotdWeb_PageHelper.getCardName(cardPageFile));
		cardData.add(CotdWeb_PageHelper.getIdLine(cardPageFile).replaceAll(" .+", "S SR"));
		cardData.addAll(CotdWeb_PageHelper.getCardStats(cardPageFile));
		cardData.add("");
		cardData.addAll(CotdWeb_PageHelper.getCardAbilities(cardPageFile));
		
		return cardData;
	}
}
