package ws_cotd_v2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import ws_cotd_web.CotdWeb_Parser;

public class Cotd_CurrentSeries {

	public static LinkedHashMap<String,String> getCurrentSeries_Raw() throws Exception{
		
		LinkedHashMap<String,String> series = new LinkedHashMap<String,String>();
		
		ArrayList<String> currentSeriesContent = new ArrayList<String>(Files.readAllLines(CotdWeb_Parser.conf.currentSeriesFile.toPath(), StandardCharsets.UTF_8));
		
		String seriesHeader = currentSeriesContent.remove(0);
		
		while(!seriesHeader.startsWith("*")) {
			String seriesFullId = currentSeriesContent.remove(0);
			series.put(seriesHeader, seriesFullId);
			seriesHeader = currentSeriesContent.remove(0);
		}
		return series;
	}
	
}
