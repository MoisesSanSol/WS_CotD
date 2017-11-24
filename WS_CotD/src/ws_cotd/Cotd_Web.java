package ws_cotd;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Cotd_Web {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		Cotd_Web.generateWebContentFromTemporal();
		//Cotd_Web.createEmptyIndex();
	}
	
	public static void generateWebContentFromTemporal() throws Exception{
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		List<String> fileContent = new ArrayList<>(Files.readAllLines(conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		
		fileContent.remove(0); // Separador
		String fecha = fileContent.remove(0).replace("Cartas del día ", "").replace(":", "");
		
		String linea = "";
		while(!linea.startsWith("-")){
			linea = fileContent.remove(0);
		}
		
		int count = 1;
		
		while(!fileContent.isEmpty()){
			
			fileContent.remove(0); // Salto de linea
			String name = fileContent.remove(0);
			String fullId = fileContent.remove(0);
			String id = fullId.split(" ")[0];
			String ref = id.split("-")[1];
			String seriesRef = id.split("-")[0].split("/")[1].toLowerCase();
			String caract1 = fileContent.remove(0);
			String caract = caract1 + ", " + fileContent.remove(0);
			fileContent.remove(0);
			
			String seriesWebPath = conf.webFolder.getPath() + "\\" +  seriesRef + "\\";
			String templatePath = seriesWebPath + "cards\\template.html";
			
			List<String> templateContent = new ArrayList<>(Files.readAllLines(new File(templatePath).toPath(), StandardCharsets.UTF_8));
			
			templateContent.set(1, templateContent.get(1).replace("[Card Id]", id));
			templateContent.set(12, templateContent.get(12).replace("[Ref]", ref));
			templateContent.set(17, templateContent.get(17).replace("[Nombre]", name));
			templateContent.set(22, fullId);
			templateContent.set(27, caract.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;"));
			templateContent.remove(32);

			List<String> cardContent = new ArrayList<String>();
			
			String habLinea = fileContent.remove(0);
			while(!habLinea.startsWith("-")){
				cardContent.add(habLinea);
				habLinea = fileContent.remove(0);
			}
			cardContent.remove(cardContent.size()-1);
			Collections.reverse(cardContent);
			
			
			while(!cardContent.isEmpty()){
				String hab = cardContent.remove(0);
				templateContent.add(32, hab.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;"));
				if(!cardContent.isEmpty()){
					templateContent.add(32, "<br>");
				}
			}
			
			String file = seriesRef + "_" + ref + ".html";
			
			templateContent.add(0,"<meta charset=\"utf-8\">");
			
			String cardsPath = seriesWebPath + "cards\\"; 
			
			Files.write(new File(cardsPath + file).toPath(), templateContent, StandardCharsets.UTF_8);
		
			String indexPath = seriesWebPath + "index.html";
			
			List<String> indexContent = new ArrayList<>(Files.readAllLines(new File(indexPath).toPath(), StandardCharsets.UTF_8));
		
			for(int i = 0; i < indexContent.size(); i++){
				if(indexContent.get(i).endsWith(ref)){
					String newLine = fecha + "<a href='./cards/" + seriesRef + "_" + ref + ".html'><img src='./images/" + seriesRef + "_" + ref + ".png' width=100% height=auto></img></a>" + id;
					indexContent.set(i, newLine);
				}
			}
			
			Files.write(new File(indexPath).toPath(), indexContent, StandardCharsets.UTF_8);
			
			String paddedCount = String.format("%02d", count);
			count++;
			
			File originFile = new File(conf.imagesFolder.getAbsolutePath() + "/jp_" + paddedCount + ".png");
			File targetFile = new File(conf.webFolder.getAbsolutePath() + "/" + seriesRef + "/images/" + seriesRef + "_" + ref + ".png");
			
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
	
	public static void createEmptyIndex() throws Exception{
		
		System.out.println("* Create Empty Index");
		
		String seriesId = "we29";
		String seriesFullId = "HLL/WE29-";
		String productType = "Extra Booster";
		String seriesName = "Hina Logi ~From Luck & Logic~ Vol. 2";
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		File newFile = new File(conf.webFolder.getAbsolutePath() + "/" + seriesId + "/index.html");
		
		List<String> newFileContent = new ArrayList<>();
		
		newFileContent.add("<meta charset=\"utf-8\">");
		newFileContent.add("<head>");
		newFileContent.add("<title>Cartas del día · " + seriesName + "</title>");
		newFileContent.add("</head>");
		newFileContent.add("<body>");
		newFileContent.add("<div align=center style=\"font-size:150%\"><b>");
		newFileContent.add(productType + ": " + seriesName);
		newFileContent.add("</b></div>");
		newFileContent.add("<table border=2 width=100%>");
		
		int count = 1;
		for(int i = 1; i <= 5; i++){
			
			newFileContent.add("<tr>");
			
			for(int j = 1; j <= 10; j++){
				
				String paddedCount = String.format("%02d", count);;
				
				newFileContent.add("<td width=10%  align=center>");
				newFileContent.add("<img src='../images_default/no_image.png' width=100% height=auto id='" + seriesId + "_" + paddedCount + "'></img>" + seriesFullId + paddedCount);
				newFileContent.add("</td>");
				
				count++;
			}
			
			newFileContent.add("</tr>");
		}
		
		newFileContent.add("</table>");
		newFileContent.add("</body>");
		
		Files.write(newFile.toPath(), newFileContent, StandardCharsets.UTF_8);
	}
	
}
