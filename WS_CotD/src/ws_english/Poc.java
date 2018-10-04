package ws_english;

import java.util.ArrayList;

public class Poc {

	public static void main(String[] args) throws Exception {
		
		//ArrayList<WsEn_Series> series = WsEn_Scrapper.getSeriesList();
		//WsEn_FileHelper.saveMasterList(series);
		
		ArrayList<WsEn_Series> series = WsEn_FileHelper.getMasterList();
		WsEn_FileHelper.saveSeriesAbilitiesList(series);
		
		//WsEn_FileHelper.getFullAbilityList();

	}
	
}
