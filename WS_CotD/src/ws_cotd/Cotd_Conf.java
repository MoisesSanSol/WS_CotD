package ws_cotd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Cotd_Conf {

	// Singleton instance.
	private static Cotd_Conf instance;
	
	// Something:
	public String notepadPath;
	
	// Folders:
	public File imagesFolder;
	public File mainFolder;
	public File webFolder;
	public File referencesFolder;
	public File webTemplatesFolder;
	
	// Files:
	public File abilitiesFile;
	public File fromImagesFile;
	public File fromGlobalFile;
	public File fromOutakuyaFile;
	public File temporalFile;
	public File resultFile;
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

			// Something
			this.notepadPath = prop.getProperty("notepadPath");
			
			// Folders
			String imagesFolderStr = prop.getProperty("imagesFolder");
			this.imagesFolder = new File(imagesFolderStr);
			String mainFolderStr = prop.getProperty("mainFolder");
			this.mainFolder = new File(mainFolderStr);
			String webFolderStr = prop.getProperty("webFolder");
			this.webFolder = new File(webFolderStr);
			String referencesFolderStr = prop.getProperty("referencesFolder");
			this.referencesFolder = new File(referencesFolderStr);
			String webTemplatesFolderStr = prop.getProperty("webTemplatesFolder");
			this.webTemplatesFolder = new File(webTemplatesFolderStr);
			
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
			String fromImagesFileStr = prop.getProperty("fromImagesFile");
			String fromImagesFilePath = this.mainFolder.getAbsolutePath() + "\\" + fromImagesFileStr;
			this.fromImagesFile = new File(fromImagesFilePath);
			String fromGlobalFileStr = prop.getProperty("fromGlobalFile");
			String fromGlobalFilePath = this.mainFolder.getAbsolutePath() + "\\" + fromGlobalFileStr;
			this.fromGlobalFile = new File(fromGlobalFilePath);
			String abilitiesFileStr = prop.getProperty("abilitiesFile");
			String abilitiesFilePath = this.mainFolder.getAbsolutePath() + "\\" + abilitiesFileStr;
			this.abilitiesFile = new File(abilitiesFilePath);
			String resultFileStr = prop.getProperty("resultFile");
			String resultFilePath = this.mainFolder.getAbsolutePath() + "\\" + resultFileStr;
			this.resultFile = new File(resultFilePath);
			
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
