package ws_cotd_v2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import ws_cotd.Cotd_Conf;

public class Cotd_FromGlobal {

	private static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static void createTemplate() throws Exception{

		ArrayList<String> templateContent = new ArrayList<String>();
		ArrayList<String> series = new ArrayList<String>(Cotd_Parser.getSeriesFromCurrentSeries_Raw().keySet());
		
		for(String serie : series) {
			
			templateContent.add(serie);
			templateContent.add("");
		}
		
		templateContent.add("--- Cierre. lòl");
		
		Files.write(conf.fromGlobalFile.toPath(), templateContent, StandardCharsets.UTF_8);
	}
	
}
