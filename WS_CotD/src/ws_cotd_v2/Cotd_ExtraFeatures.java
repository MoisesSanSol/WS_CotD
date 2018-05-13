package ws_cotd_v2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ws_cotd.Cotd_Utilities;
import ws_cotd_web.CotdWeb_Card;
import ws_cotd_web.CotdWeb_IndexHelper;
import ws_cotd_web.CotdWeb_Parser;

public class Cotd_ExtraFeatures {

public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		Cotd_ExtraFeatures.pastCotdNotesForSps();
		
		System.out.println("*** Finished ***");
	}
	
	public static void pastCotdNotesForSps() throws Exception{
		
		ArrayList<CotdWeb_Card> cards = CotdWeb_Parser.getCardsFromTemporal();
		
		for(CotdWeb_Card card : cards){
			if(card.idLine.contains("SP")){
				
				card.id = card.id.replaceAll("SS?P", "");
				
				String indexDate = CotdWeb_IndexHelper.searchIndexDate(card);
				String noteDate = Cotd_Utilities.indexDateToNoteDate(indexDate);
				
				String note = "# '"
						+ card.name
						+ "' ("
						+ card.id
						+ ") fue ya carta del día el "
						+ noteDate
						+ ", la de hoy es la versión SP.";
				System.out.println(note);
			}
		}
		
	}

}
