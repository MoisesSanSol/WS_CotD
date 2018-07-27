package ws_cotd_web;

import java.util.ArrayList;
import java.util.HashMap;

public class CotdWeb_CardListHelper {

	public static ArrayList<CotdWeb_Card> updateReferences(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		HashMap<String, String> referenceAnchors = new HashMap<String,String>();
		HashMap<String, ArrayList<String>> references = new HashMap<String,ArrayList<String>>();
		
		for(CotdWeb_Card card : cards){
			if(card.isReferenced){
				String originalFileId = card.fileId.replaceAll("SP?$", "").replaceAll("(BD)?R$", "").replaceAll("H$", "").replaceAll("SPM$", "");
				String anchor =  "<a href='./" + originalFileId + ".html'>" + card.name + "</a>";
				referenceAnchors.put(card.name, anchor);
				references.put(card.name, new ArrayList<String>());
			}
		}
		
		for(CotdWeb_Card card : cards){
			if(card.hasReferences){
				String originalFileId = card.fileId.replaceAll("SP?$", "").replaceAll("(BD)?R$", "").replaceAll("H$", "").replaceAll("SPM$", "");
				String anchor =  "<a href='./" + originalFileId + ".html'>" + card.name + "</a>";
				for(String ability : card.abilities){
					String updatedAbility = ability;
					for(String referenced : referenceAnchors.keySet()) {
						if(updatedAbility.contains("'" + referenced + "'")){
							updatedAbility = updatedAbility.replace(referenced, referenceAnchors.get(referenced));
							if(!references.get(referenced).contains(anchor)){
								references.get(referenced).add(anchor);
							}
						}
					}
					card.abilities.set(card.abilities.indexOf(ability), updatedAbility);
				}
			}
		}
		
		for(CotdWeb_Card card : cards){
			if(card.isReferenced){
				card.references = references.get(card.name);
			}
		}
		
		return cards;
	}
	
	public static ArrayList<CotdWeb_Card> escapeCardsForHtml(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		for(CotdWeb_Card card : cards){
			card.name = CotdWeb_CardListHelper.escapeForHtml(card.name);
			card.statsLine = CotdWeb_CardListHelper.escapeForHtml(card.statsLine);
			if(card.statsLine.startsWith("Climax")){
				card.statsLine = card.statsLine.replace("&lt;br&gt;", "<br>");
			}
			card.abilities = CotdWeb_CardListHelper.escapeForHtml(card.abilities);
		}
		
		return cards;
	}
	
	public static String escapeForHtml(String line) throws Exception{
		
		return line.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
	}
	
	public static String escapeFromHtml(String line) throws Exception{
		
		return line.replace("&amp;", "&").replace("&gt;", ">").replace("&lt;", "<");
	}
	
	public static ArrayList<String> escapeForHtml(ArrayList<String> lines) throws Exception{
		
		for(int i = 0; i < lines.size(); i++) {
			lines.set(i, CotdWeb_CardListHelper.escapeForHtml(lines.get(i)));
		}
		
		return lines;
	}
	
	public static ArrayList<CotdWeb_Card> updateNotes(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		for(CotdWeb_Card card : cards){
			for(int i = 0; i < card.notes.size(); i++) {
				if(card.notes.get(i).startsWith("##")){

					String referencedCardName = card.notes.get(i).replaceAll(".+?'(.+?)' .+", "$1");
					String referencedCardId = card.notes.get(i).replaceAll(".+? \\((.+?)\\) .+", "$1");
					String referencedCardFileId = referencedCardId.split("-")[0].split("/")[1].toLowerCase() + "_" + referencedCardId.split("-")[1];
					
					String updatedNote =  "<a href='./" + referencedCardFileId + ".html'>" + referencedCardName + "</a>\r\n";
					updatedNote = updatedNote + "* Esta carta es referenciada en las habilidades de '";
					updatedNote = updatedNote + "<a href='./" + card.fileId + ".html'>" + card.name + "</a>'";

					card.notes.set(i, updatedNote);
					card.needsManualUpdate = true;
				}
				if(card.notes.get(i).startsWith("#hotc")){

					String referencedCardName = card.notes.get(i).replaceAll("#hotc '(.+?)' \\(.+?\\) .+? '.+?'\\.", "$1");
					String referencedCardId = card.notes.get(i).replaceAll("#hotc '(.+?)' \\((.+?)\\) .+? '.+?'\\.", "$2");
										
					String nameWithHotcLink =  "<a target='_blank' href='http://www.heartofthecards.com/code/cardlist.html?card=WS_" + referencedCardId + "'>" + referencedCardName + "</a>";
					String updatedNote = card.notes.get(i).replace(referencedCardName, nameWithHotcLink);
					updatedNote = updatedNote.replace("hotc", "");
					card.notes.set(i, updatedNote);

					for(int j = 0; j < card.abilities.size(); j++) {
						if(card.abilities.get(j).contains(referencedCardName)){
							card.abilities.set(j, card.abilities.get(j).replace(referencedCardName, nameWithHotcLink));
						}
					}
				}
			}
		}
		
		return cards;
	}
}
