package ws_english;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class WsEn_FileHelper {

	public static void saveMasterList(ArrayList<WsEn_Series> series) throws Exception{
		
		File masterListFile = new File(WsEn_LocalConf.masterserieslistfile_fullpath);
		ArrayList<String> content = new ArrayList<String>();
		
		for(WsEn_Series serie : series){
			content.add(serie.name);
			content.add(serie.product);
			content.add(serie.seriesId);
			content.add(serie.wstcgId);
			content.add(String.valueOf(serie.wstcgLastPage));
		}
		
		Files.write(masterListFile.toPath(), content, StandardCharsets.UTF_8);
		
	}
	
	public static ArrayList<WsEn_Series> getMasterList() throws Exception{
		
		ArrayList<WsEn_Series> series = new ArrayList<WsEn_Series>();
		File masterListFile = new File(WsEn_LocalConf.masterserieslistfile_fullpath);
		ArrayList<String> content = new ArrayList<String>(Files.readAllLines(masterListFile.toPath(), StandardCharsets.UTF_8));
		
		while(!content.isEmpty()){
			WsEn_Series serie = new WsEn_Series();
			serie.name = content.remove(0);
			serie.product = content.remove(0);
			serie.fileName = serie.name + " (" + serie.product + ").txt";
			serie.fileName = serie.fileName.replace(":", "").replace("/", " ");
			serie.seriesId = content.remove(0);
			serie.wstcgId = content.remove(0);
			serie.wstcgLastPage = Integer.parseInt(content.remove(0));
			series.add(serie);
		}
		
		return series;
	}
	
	public static void saveSeriesAbilitiesList(ArrayList<WsEn_Series> series) throws Exception{
		
		for(WsEn_Series serie : series){
			File abilityListFile = new File(WsEn_LocalConf.abilitylistfile_basepath + serie.fileName);
			if(!abilityListFile.exists()){
				ArrayList<String> content = WsEn_Scrapper.getSeriesAbilities(serie);
				Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
			}
		}
	}
	
	public static void getFullAbilityList() throws Exception{
		
		ArrayList<String> content = new ArrayList<String>();
		
		ArrayList<WsEn_Series> series = getMasterList();
		for(WsEn_Series serie : series){
			File abilityListFile = new File(WsEn_LocalConf.abilitylistfile_basepath + serie.fileName);
			if(abilityListFile.exists()){
				ArrayList<String> auxContent = new ArrayList<String>(Files.readAllLines(abilityListFile.toPath(), StandardCharsets.UTF_8));
				while(!auxContent.isEmpty()){
					String line = auxContent.remove(0).trim();
					if(!line.isEmpty() && !line.startsWith("#") && !content.contains(line)){
						content.add(line);
					}
				}
			}
		}
		
		Collections.sort(content);
		File abilityListFile = new File(WsEn_LocalConf.masterabilitylistfile_fullpath);
		Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
	}
	
	public static void getFullAbilityList_WithReferences() throws Exception{
		
		ArrayList<String> content = new ArrayList<String>();
		
		ArrayList<WsEn_Series> series = getMasterList();
		for(WsEn_Series serie : series){
			File abilityListFile = new File(WsEn_LocalConf.abilitylistfile_basepath + serie.fileName);
			if(abilityListFile.exists()){
				ArrayList<String> auxContent = new ArrayList<String>(Files.readAllLines(abilityListFile.toPath(), StandardCharsets.UTF_8));
				while(!auxContent.isEmpty()){
					String line = auxContent.remove(0).trim();
					if(!line.isEmpty() && !line.startsWith("#") && !content.contains(line)){
						content.add(line);
					}
				}
			}
		}
		
		Collections.sort(content);
		File abilityListFile = new File(WsEn_LocalConf.masterabilitylistfile_fullpath);
		Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
	}
	
	public static HashMap<String,String> getSeriesAbilityTranslations(String seriesId) throws Exception{
		
		HashMap<String,String> abilityTranslations = new HashMap<String,String>();
		
		File abilityTranslationsFile = new File(WsEn_LocalConf.seriesabilitytranslationsfile_basepath + seriesId + ".txt");
		ArrayList<String> abilityTranslationsContent = new ArrayList<String>(Files.readAllLines(abilityTranslationsFile.toPath(), StandardCharsets.UTF_8));
		while(!abilityTranslationsContent.isEmpty()){
			String esAbility = abilityTranslationsContent.remove(0).replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");;
			esAbility = "\\Q" + esAbility.replace("@", "\\E(.+?)\\Q") + "\\E"; 
			String enAbility = abilityTranslationsContent.remove(0).replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");;
			abilityTranslationsContent.remove(0);
			
			abilityTranslations.put(esAbility, enAbility);
		}
		
		return abilityTranslations;
	}
}
