package ws_cotd_web;

import java.util.ArrayList;

public class CotdWeb_Card {

	public String seriesId;
	public String id;
	public String fileId;
	public String imageFileId;
	
	public String name;
	public String jpName;
	public String idLine;
	public String rarity;
	public String statsLine;
	public ArrayList<String> abilities;
	public ArrayList<String> notes;
	public ArrayList<String> references;
	
	boolean isReferenced;
	boolean hasReferences;
	boolean needsManualUpdate;
	
	public CotdWeb_Card() {
		this.abilities = new ArrayList<String>();
		this.notes = new ArrayList<String>();
		this.references = new ArrayList<String>();
	}
}
