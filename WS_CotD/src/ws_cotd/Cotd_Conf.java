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
	
	// Urls:
	public String wsJpCotdUrl;
	public String wsJpExtraCotdUrl;

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

			String imagesFolder = prop.getProperty("imagesFolder");
			this.imagesFolder = new File(imagesFolder);
			
			this.wsJpCotdUrl = prop.getProperty("wsJpCotdUrl");
			this.wsJpExtraCotdUrl = prop.getProperty("wsJpExtraCotdUrl");

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
