package ws_cotd_web_en;

import java.util.ArrayList;
import java.util.HashMap;

import ws_cotd_web.CotdWeb_Parser;

public class CotdWebEn_AbilitiesHelper {

	public static void updateSeriesAbilityList() throws Exception{
		
		HashMap<String,String> series = CotdWeb_Parser.getSeriesFromCurrentSeries();
		for(String seriesId : series.keySet()) {
			CotdWebEn_AbilitiesHelper.updateSeriesAbilityList(seriesId);
		}
		
	}
	
	public static void updateSeriesAbilityList(String seriesId) throws Exception{
		
		HashMap<String,String> seriesAbilityTranslationsRaw = CotdWebEn_FileHelper.getSeriesAbilityTranslations_Raw(seriesId);
		HashMap<String,String> seriesAbilityTranslations = CotdWebEn_FileHelper.getSeriesAbilityTranslations(seriesId);
		ArrayList<String> seriesAbilities = CotdWeb_Parser.getAbilityListFromWeb(seriesId);
		
		for(String seriesAbility : seriesAbilities){
			boolean tranlationAvaiable = false;
			for(String translationPattern : seriesAbilityTranslations.keySet()){
				if(seriesAbility.matches(translationPattern)){
					tranlationAvaiable = true;
				}
			}
			if(!tranlationAvaiable){
				seriesAbilityTranslationsRaw.put(seriesAbility, "");
			}
		}
		
		CotdWebEn_FileHelper.saveSeriesAbilityTranslations(seriesId, seriesAbilityTranslationsRaw);
	}
}
