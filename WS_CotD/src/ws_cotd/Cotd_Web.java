package ws_cotd;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Cotd_Web {

	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting Test ***");
		
		Cotd_Web.duplicateParallelCard("s51", "001", "001s", "SR");
		
		System.out.println("*** Finished ***");
	}
	
	public static void copyImagesForWeb(String seriesId, ArrayList<String> cards) throws Exception{
		
		System.out.println("** Copy Images For Web ***");
		System.out.println("* Series Id: " + seriesId);
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		int count = 0;
		
		for (String card : cards){
			
			count++;
			String paddedCount = String.format("%02d", count);
			
			File originFile = new File(conf.imagesFolder.getAbsolutePath() + "/jp_" + paddedCount + ".png");
			File targetFile = new File(conf.webFolder.getAbsolutePath() + "/" + seriesId + "/images/" + seriesId + "_" + card + ".png");
			
			System.out.println("* Copying image: " + originFile.getName() + ", to : " + targetFile.getName());
			FileUtils.copyFile(originFile, targetFile);
			
		}
	}
	
	public static void duplicateParallelCard(String seriesId, String cardId, String newCardId, String newRarity) throws Exception{
		System.out.println("** Duplicate Parallel Card");
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		File currentFile = new File(conf.webFolder.getAbsolutePath() + "/" + seriesId + "/cards/" + seriesId + "_" + cardId + ".html");
		File newFile = new File(conf.webFolder.getAbsolutePath() + "/" + seriesId + "/cards/" + seriesId + "_" + newCardId +".html");
		
		List<String> currentFileContent = new ArrayList<>(Files.readAllLines(currentFile.toPath(), StandardCharsets.UTF_8));
		List<String> newFileContent = new ArrayList<>(currentFileContent);
		
		String idLine = currentFileContent.get(23);
		String[] idLineSplit = idLine.split(" ");
		String newOldIdLine = idLine + " (<a href='./" + seriesId + "_" + newCardId + ".html'>" + newRarity + "</a>)";
		String newIdLine = idLineSplit[0] + " " + newRarity + " (<a href='./" + seriesId + "_" + cardId + ".html'>" + idLineSplit[1] + "</a>)";
		
		currentFileContent.set(23, newOldIdLine);
		newFileContent.set(23, newIdLine);
		
		newFileContent.set(13, "<img src='../images/" + seriesId + "_" + newCardId + ".png'></img>");
		
		Files.write(currentFile.toPath(), currentFileContent, StandardCharsets.UTF_8);
		Files.write(newFile.toPath(), newFileContent, StandardCharsets.UTF_8);
		
	}
	
}
