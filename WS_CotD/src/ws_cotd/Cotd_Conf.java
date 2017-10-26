package ws_cotd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Cotd_Conf {

	// Singleton instance.
	private static Cotd_Conf instance;
	
	// Folders:
	public File imagesFolder;
	public File mainFolder;
	public File webFolder;
	
	// Files:
	public File fromOutakuyaFile;
	public File temporalFile;
	public File currentSeriesFile;
	
	// Urls:
	public String wsJpCotdUrl;
	public String wsJpExtraCotdUrl;
	public String outakuyaUrl;

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

			input = new FileInputStream("Conf/configuration.properties");

			prop.load(input);

			// Folders
			String imagesFolderStr = prop.getProperty("imagesFolder");
			this.imagesFolder = new File(imagesFolderStr);
			String mainFolderStr = prop.getProperty("mainFolder");
			this.mainFolder = new File(mainFolderStr);
			String webFolderStr = prop.getProperty("webFolder");
			this.webFolder = new File(webFolderStr);
			
			// Files
			String temporalStr = prop.getProperty("temporalFile");
			String temporalPath = this.mainFolder.getAbsolutePath() + "\\" + temporalStr;
			this.temporalFile = new File(temporalPath);
			String fromOutakuyaFileStr = prop.getProperty("fromOutakuyaFile");
			String fromOutakuyaFilePath = this.mainFolder.getAbsolutePath() + "\\" + fromOutakuyaFileStr;
			this.fromOutakuyaFile = new File(fromOutakuyaFilePath);
			String currentSeriesFileStr = prop.getProperty("currentSeriesFile");
			String currentSeriesFilePath = this.mainFolder.getAbsolutePath() + "\\" + currentSeriesFileStr;
			this.currentSeriesFile = new File(currentSeriesFilePath);
			
			// Urls
			this.wsJpCotdUrl = prop.getProperty("wsJpCotdUrl");
			this.wsJpExtraCotdUrl = prop.getProperty("wsJpExtraCotdUrl");
			this.outakuyaUrl = prop.getProperty("outakuyaUrl");

			
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
