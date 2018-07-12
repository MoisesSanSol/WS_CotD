package ws_cotd_web;

import java.io.Serializable;
import java.util.ArrayList;

public class CotdWeb_Card implements Serializable{

	private static final long serialVersionUID = 1L;
	
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
	
	public boolean isReferenced;
	public boolean hasReferences;
	public boolean needsManualUpdate;
	public boolean isParallel;
	
	public CotdWeb_Card() {
		this.abilities = new ArrayList<String>();
		this.notes = new ArrayList<String>();
		this.references = new ArrayList<String>();
	}
}
