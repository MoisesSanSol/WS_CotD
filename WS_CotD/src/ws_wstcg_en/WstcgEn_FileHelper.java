package ws_wstcg_en;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class WstcgEn_FileHelper {

	public static void saveMasterList(ArrayList<WstcgEn_Series> series) throws Exception{
		
		File masterListFile = new File(WstcgEn_LocalConf.masterserieslistfile_fullpath);
		ArrayList<String> content = new ArrayList<String>();
		
		for(WstcgEn_Series serie : series){
			content.add(serie.name);
			content.add(serie.product);
			content.add(serie.seriesId);
			content.add(serie.wstcgId);
			content.add(String.valueOf(serie.wstcgLastPage));
		}
		
		Files.write(masterListFile.toPath(), content, StandardCharsets.UTF_8);
		
	}
	
	public static ArrayList<WstcgEn_Series> getMasterList() throws Exception{
		
		ArrayList<WstcgEn_Series> series = new ArrayList<WstcgEn_Series>();
		File masterListFile = new File(WstcgEn_LocalConf.masterserieslistfile_fullpath);
		ArrayList<String> content = new ArrayList<String>(Files.readAllLines(masterListFile.toPath(), StandardCharsets.UTF_8));
		
		while(!content.isEmpty()){
			WstcgEn_Series serie = new WstcgEn_Series();
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
	
	public static void saveSeriesAbilitiesList(ArrayList<WstcgEn_Series> series) throws Exception{
		
		for(WstcgEn_Series serie : series){
			File abilityListFile = new File(WstcgEn_LocalConf.abilitylistfile_basepath + serie.fileName);
			if(!abilityListFile.exists()){
				ArrayList<String> content = WstcgEn_Scrapper.getSeriesAbilities(serie);
				Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
			}
		}
	}
	
	public static void getFullAbilityList() throws Exception{
		
		ArrayList<String> content = new ArrayList<String>();
		
		ArrayList<WstcgEn_Series> series = getMasterList();
		for(WstcgEn_Series serie : series){
			File abilityListFile = new File(WstcgEn_LocalConf.abilitylistfile_basepath + serie.fileName);
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
		File abilityListFile = new File(WstcgEn_LocalConf.masterabilitylistfile_fullpath);
		Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
	}
	
	public static void getFullAbilityList_WithReferences() throws Exception{
		
		HashMap<String,String> abilities = new HashMap<String,String>();
		
		ArrayList<WstcgEn_Series> series = getMasterList();
		for(WstcgEn_Series serie : series){
			File abilityListFile = new File(WstcgEn_LocalConf.abilitylistfile_basepath + serie.fileName);
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
		File abilityListFile = new File(WstcgEn_LocalConf.masterabilitylistfile_fullpath);
		Files.write(abilityListFile.toPath(), content, StandardCharsets.UTF_8);
	}
}
