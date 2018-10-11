package ws_cotd_web_en;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class CotdWebEn_FileHelper {

	public static void saveMasterList(ArrayList<CotdWebEn_Series> series) throws Exception{
		
		File masterListFile = new File(CotdWebEn_LocalConf.masterserieslistfile_fullpath);
		ArrayList<String> content = new ArrayList<String>();
		
		for(CotdWebEn_Series serie : series){
			content.add(serie.name);
			content.add(serie.product);
			content.add(serie.seriesId);
			content.add(serie.wstcgId);
			content.add(String.valueOf(serie.wstcgLastPage));
		}
		
		Files.write(masterListFile.toPath(), content, StandardCharsets.UTF_8);
		
	}
	
	public static ArrayList<CotdWebEn_Series> getMasterList() throws Exception{
		
		ArrayList<CotdWebEn_Series> series = new ArrayList<CotdWebEn_Series>();
		File masterListFile = new File(CotdWebEn_LocalConf.masterserieslistfile_fullpath);
		ArrayList<String> content = new ArrayList<String>(Files.readAllLines(masterListFile.toPath(), StandardCharsets.UTF_8));
		
		while(!content.isEmpty()){
			CotdWebEn_Series serie = new CotdWebEn_Series();
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
	
	public static void saveSeriesAbilitiesList(ArrayList<CotdWebEn_Series> series) throws Exception{
		
		for(CotdWebEn_Series serie : series){
			File abilityListFile = new File(CotdWebEn_LocalConf.abilitylistfile_basepath + serie.fileName);
			if(!abilityListFile.exists()){
				ArrayList<String> content = CotdWebEn_Scrapper.getSeriesAbilities(serie);
				Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
			}
		}
	}
	
	public static void getFullAbilityList() throws Exception{
		
		ArrayList<String> content = new ArrayList<String>();
		
		ArrayList<CotdWebEn_Series> series = getMasterList();
		for(CotdWebEn_Series serie : series){
			File abilityListFile = new File(CotdWebEn_LocalConf.abilitylistfile_basepath + serie.fileName);
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
		File abilityListFile = new File(CotdWebEn_LocalConf.masterabilitylistfile_fullpath);
		Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
	}
	
	public static void getFullAbilityList_WithReferences() throws Exception{
		
		HashMap<String,String> abilities = new HashMap<String,String>();
		
		ArrayList<CotdWebEn_Series> series = getMasterList();
		for(CotdWebEn_Series serie : series){
			File abilityListFile = new File(CotdWebEn_LocalConf.abilitylistfile_basepath + serie.fileName);
			if(abilityListFile.exists()){
				ArrayList<String> auxContent = new ArrayList<String>(Files.readAllLines(abilityListFile.toPath(), StandardCharsets.UTF_8));
				String aux = "Error"; 
				while(!auxContent.isEmpty()){
					String line = auxContent.remove(0).trim();
					if(line.startsWith("#")){
						aux = line;
					}
					else if(!line.isEmpty()){
						String idList = aux;
						if(abilities.containsKey(line)){
							idList = abilities.get(line) + "; " + aux;
						}
						abilities.put(line, idList);
					}
				}
			}
		}

		ArrayList<String> content = new ArrayList<String>();
		for(String ability : abilities.keySet()){
			content.add(ability + "\t" + abilities.get(ability)); 
		}
		Collections.sort(content);
		File abilityListFile = new File(CotdWebEn_LocalConf.masterabilitylistfile_fullpath);
		Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
	}
	
	public static HashMap<String,String> getSeriesAbilityTranslations(String seriesId) throws Exception{
		
		HashMap<String,String> abilityTranslations = new HashMap<String,String>();
		
		File abilityTranslationsFile = new File(CotdWebEn_LocalConf.seriesabilitytranslationsfile_basepath + seriesId + ".txt");
		ArrayList<String> abilityTranslationsContent = new ArrayList<String>(Files.readAllLines(abilityTranslationsFile.toPath(), StandardCharsets.UTF_8));
		while(!abilityTranslationsContent.isEmpty()){
			String esAbility = abilityTranslationsContent.remove(0).replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
			esAbility = "\\Q" + esAbility.replace("@", "\\E(.+?)\\Q") + "\\E"; 
			String enAbility = abilityTranslationsContent.remove(0).replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
			abilityTranslationsContent.remove(0);
			
			abilityTranslations.put(esAbility, enAbility);
		}
		
		return abilityTranslations;
	}
	
	public static HashMap<String,String> getSeriesAbilityTranslations_Raw(String seriesId) throws Exception{
		
		HashMap<String,String> abilityTranslations = new HashMap<String,String>();
		
		File abilityTranslationsFile = new File(CotdWebEn_LocalConf.seriesabilitytranslationsfile_basepath + seriesId + ".txt");
		ArrayList<String> abilityTranslationsContent = new ArrayList<String>(Files.readAllLines(abilityTranslationsFile.toPath(), StandardCharsets.UTF_8));
		while(!abilityTranslationsContent.isEmpty()){
			String esAbility = abilityTranslationsContent.remove(0); 
			String enAbility = abilityTranslationsContent.remove(0);
			abilityTranslationsContent.remove(0);
			
			abilityTranslations.put(esAbility, enAbility);
		}
		
		return abilityTranslations;
	}
	
	public static void saveSeriesAbilityTranslations(String seriesId, HashMap<String,String> abilityTranslations) throws Exception{
		
		ArrayList<String> abilityTranslationsContent = new ArrayList<String>();
		
		ArrayList<String> translationPatterns = new ArrayList<String>(abilityTranslations.keySet());
		Collections.sort(translationPatterns);
		
		for(String translationPattern : translationPatterns){
			abilityTranslationsContent.add(translationPattern);
			abilityTranslationsContent.add(abilityTranslations.get(translationPattern));
			abilityTranslationsContent.add("");
		}
		
		File abilityTranslationsFile = new File(CotdWebEn_LocalConf.seriesabilitytranslationsfile_basepath + seriesId + ".txt");
		Files.write(abilityTranslationsFile.toPath(), abilityTranslationsContent, StandardCharsets.UTF_8);
	}
}
