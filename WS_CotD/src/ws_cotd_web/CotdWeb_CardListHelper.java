package ws_cotd_web;

import java.util.ArrayList;
import java.util.HashMap;

public class CotdWeb_CardListHelper {

	public static ArrayList<CotdWeb_Card> updateReferences(ArrayList<CotdWeb_Card> cards) throws Exception{
		
		HashMap<String, String> referenceAnchors = new HashMap<String,String>();
		HashMap<String, ArrayList<String>> references = new HashMap<String,ArrayList<String>>();
		
		for(CotdWeb_Card card : cards){
			if(card.isReferenced){
				String anchor =  "<a href='./" + card.fileId + ".html'>" + card.name + "</a>";
				referenceAnchors.put(card.name, anchor);
				references.put(card.name, new ArrayList<String>());
			}
		}
		
		for(CotdWeb_Card card : cards){
			if(card.hasReferences){
				String anchor =  "<a href='./" + card.fileId + ".html'>" + card.name + "</a>";
				for(String ability : card.abilities){
					String updatedAbility = ability;
					for(String referenced : referenceAnchors.keySet()) {
						if(updatedAbility.contains(referenced)){
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
			card.abilities = CotdWeb_CardListHelper.escapeForHtml(card.abilities);
		}
		
		return cards;
	}
	
	public static String escapeForHtml(String line) throws Exception{
		
		return line.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
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
			}
		}
		
		return cards;
	}
}
