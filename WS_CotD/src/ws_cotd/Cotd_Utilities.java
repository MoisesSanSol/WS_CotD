package ws_cotd;

import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Cotd_Utilities{

	public static void openFileInNotepad(File file) throws Exception{
		
		System.out.println("** Opening file in notepad: " + file.getName());
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		String cmdLine = conf.notepadPath + " \"" + file.getAbsolutePath() + "\"";

		Runtime runtime = Runtime.getRuntime();
		runtime.exec(cmdLine);
	}
	
	public static void openDefaultFolders() throws Exception{
		
		System.out.println("** Open Default Folders");
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		Desktop desktop = Desktop.getDesktop();
		desktop.open(conf.imagesFolder);
		desktop.open(conf.mainFolder);
		desktop.open(conf.referencesFolder);
	}
	
	public static String indexDateToNoteDate(String indexDate) throws Exception{
		
		SimpleDateFormat inputFormatter = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat outputFormatter = new SimpleDateFormat("d 'de' MMMM", new Locale("es", "ES"));
		
		Date date = inputFormatter.parse(indexDate);
		String noteDate = outputFormatter.format(date);
		
		return noteDate;
	}
}
