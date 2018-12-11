package ws_cotd_v2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import ws_cotd.Cotd_Conf;
import ws_cotd_web.CotdWeb_Card;

public class Cotd_FromImages {

	private static Cotd_Conf conf = Cotd_Conf.getInstance();
	
	public static ArrayList<CotdWeb_Card> getBaseCards_FromImages() throws Exception{
		
		ArrayList<CotdWeb_Card> cards = new ArrayList<CotdWeb_Card>();
		
		ArrayList<String> fromImagesContent = new ArrayList<>(Files.readAllLines(conf.fromImagesFile.toPath(), StandardCharsets.UTF_8));
		
		while (!fromImagesContent.isEmpty()){
			
			CotdWeb_Card card = new CotdWeb_Card();
			String firstLine = fromImagesContent.remove(0);
			if(firstLine.startsWith("Personaje")){
				
			}
			
		}
		
		return cards;
	}
	
}
