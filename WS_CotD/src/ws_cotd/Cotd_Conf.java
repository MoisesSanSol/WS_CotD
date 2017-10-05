package ws_cotd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Cotd_Conf {
	
	public File ongoingSeriesFolder;
	public File downloadTargetFolder;
	public static String animeFlvBaseUrl = "https://animeflv.net";
	public static String animeFlvSeriesMainPageBaseUrl = "https://animeflv.net/anime/";
	
	public String setId;
	public String 
	
	private static Cotd_Conf instance;
	
	private Cotd_Conf(){
		this.loadLocalConfiguration();
	}
	
	public static Cotd_Conf getInstance(){
      if(instance == null) {
          instance = new Cotd_Conf();
       }
       return instance;
	}
	
	public void loadLocalConfiguration(){
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("Conf/LocalConfiguration.properties");

			prop.load(input);

			String ongoingSeriesFolderPath = prop.getProperty("ongoingSeriesFolder");
			this.ongoingSeriesFolder = new File(ongoingSeriesFolderPath);
			String downloadTargetFolderPath = prop.getProperty("downloadTargetFolder");
			this.downloadTargetFolder = new File(downloadTargetFolderPath);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
