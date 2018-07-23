package ws_cotd;

import java.awt.Desktop;
import java.io.File;
import java.text.ParseException;
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
	
	public static void openFolder(File folder) throws Exception{
		
		Desktop desktop = Desktop.getDesktop();
		desktop.open(folder);
	}
	
	public static String indexDateToNoteDate(String indexDate) throws Exception{
		
		String noteDate = indexDate;
		
		SimpleDateFormat inputFormatter = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat outputFormatter = new SimpleDateFormat("d 'de' MMMM", new Locale("es", "ES"));
		try{
			Date date = inputFormatter.parse(indexDate);
			noteDate = outputFormatter.format(date);
		}
		catch(ParseException pe){
			System.out.println("Unparseable date: " + indexDate);
		}
		
		return noteDate;
	}
}
