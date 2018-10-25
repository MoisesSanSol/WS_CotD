package ws_cotd_v2;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import ws_cotd.Cotd_Conf;
import ws_cotd_web.CotdWeb_Card;

public class Cotd_WorkingFile {

	public static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void createTemporalFromCards(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		ArrayList<String> temporalContent = new ArrayList<String>();
		
		temporalContent.add("****************************************");
		temporalContent.add("Cartas del día Tras Salida:");
		temporalContent.add("");
		temporalContent.add("--- Manual Series, Whatever Edition");
		
		for(CotdWeb_Card card : cards) {
		
			temporalContent.add("");
			temporalContent.add(card.name);
			temporalContent.add(card.jpName);
			temporalContent.add(card.idLine);
			temporalContent.add(card.statsLine);
			temporalContent.add("");
			temporalContent.addAll(card.abilities);
			temporalContent.add("");
			temporalContent.add("-");
		}

		String cardsTemporalPath = conf.mainFolder.getAbsolutePath() + "/cardsTemporal.txt";
		File cardsTemporal = new File(cardsTemporalPath);
		Files.write(cardsTemporal.toPath(), temporalContent, StandardCharsets.UTF_8);
	}
	
}
