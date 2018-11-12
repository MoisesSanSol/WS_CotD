package ws_tcgresults;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import ws_cotd.Cotd_Conf;

public class WsTcgResults_Builder {

	public static void printDecklists(ArrayList<WsTcgResults_Deck> decklists) throws Exception{
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		for(WsTcgResults_Deck deck : decklists){
			
			ArrayList<String> content = new ArrayList<String>();
		
			content.add("<meta charset='utf-8'>");
			content.add("<head>");
			content.add("<title>WGP 2018 - Decklists</title>");
			content.add("</head>");
			content.add("<body>");
			content.add("<div style='font-size:150%'><b>");
			content.add("WGP 2018 - Decklists");
			content.add("</b></div>");
			content.add("<br>");
			content.add("<table border=2>");
			content.add("<tr>");
			content.add("<th colspan=4>");
			
			String position = deck.position;
			int positionInt = Integer.parseInt(position);
			if(deck.type.equals("Trios") && positionInt > 3){
				
				positionInt = (int) Math.ceil((double)((double)positionInt/(double)3));
			}
			else if(deck.type.equals("NeoStandard") && positionInt > 4){
				
				positionInt = (positionInt + 1) % 5;
				
			}
			
			content.add(deck.location + " - " + deck.type + " Puesto " + positionInt);
			content.add("</th>");
			content.add("</tr>");
			
			for(WsTcgResults_Card card : deck.list){
				
				String cardFileId = card.id.replace("/", "-");
				
				content.add("<tr>");
				content.add("<td>");
				content.add("<img src='..\\images\\cards\\" + cardFileId + ".gif'>");
				content.add("</td>");
				content.add("<td>");
				content.add(card.quantity);
				content.add("</td>");
				content.add("<td>");
				content.add(card.id);
				content.add("</td>");
				content.add("<td>");
				content.add("<a href='https://heartofthecards.com/code/cardlist.html?card=WS_" + card.id + "'>" + card.name + "</a>");
				content.add("</td>");
				content.add("</tr>");
			}
			
			content.add("</table>");
			content.add("</body>");
			
			/*for(String line : content){
				System.out.println(line);
			}*/
			String listFilePath = conf.webFolder + "\\wstcgresults\\lists\\" + deck.location + deck.type + deck.position + ".html";
			File listFile = new File(listFilePath);
			Files.write(listFile.toPath(), content, StandardCharsets.UTF_8);
		}
	}
}
