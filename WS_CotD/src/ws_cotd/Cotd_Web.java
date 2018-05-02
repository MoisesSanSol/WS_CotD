package ws_cotd;

import java.awt.Desktop;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Cotd_Web {

	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");

		//Cotd_Web.createEmptyIndex();
		
		Cotd_Web.generateWebContentFromTemporal();
		//Cotd_Web.updateNoImageColors("s53", null);
		
		Desktop.getDesktop().open(Cotd_Conf.getInstance().webFolder);
		
		System.out.println("*** Finished ***");
	}
	
	public static void generateWebContentFromTemporal() throws Exception{
		
		System.out.println("** Generate Web Content From Temporal");
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		
		List<String> fileContent = new ArrayList<>(Files.readAllLines(conf.temporalFile.toPath(), StandardCharsets.UTF_8));
		
		fileContent.remove(0); // Separador
		String fecha = fileContent.remove(0).replace("Cartas del día ", "").replace(":", "");
		
		String linea = "";
		while(!linea.startsWith("-")){
			linea = fileContent.remove(0);
		}
		
		int count = 1;
		int size = 100;
		
		while(!fileContent.isEmpty()){
			
			fileContent.remove(0); // Salto de linea
			String name = fileContent.remove(0);
			String jpName = fileContent.remove(0);
			String fullId = fileContent.remove(0);
			String id = fullId.split(" ")[0];
			String ref = id.split("-")[1];
			String seriesRef = id.split("-")[0].split("/")[1].toLowerCase();
			String caract = fileContent.remove(0);
			if(caract.startsWith("Personaje")){
				caract = caract + " " + fileContent.remove(0);
				caract = caract.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
				fileContent.remove(0);
			}
			else if(caract.startsWith("Evento")){
				fileContent.remove(0);
			}
			else{
				String next = fileContent.remove(0);
				if(!next.equals("")){
					caract = caract + "\r\n<br>\r\n" + next;
					fileContent.remove(0);
				}
			}
			
			System.out.println("* Parsed card: " + name + " " + id);
			
			String seriesWebPath = conf.webFolder.getPath() + "\\" +  seriesRef + "\\";
			String templatePath = seriesWebPath + "cards\\template.html";
			
			List<String> templateContent = new ArrayList<>(Files.readAllLines(new File(templatePath).toPath(), StandardCharsets.UTF_8));
			
			templateContent.set(1, templateContent.get(1).replace("[Card Id]", id));
			templateContent.set(12, templateContent.get(12).replace("[Ref]", ref));
			templateContent.set(17, templateContent.get(17).replace("[Nombre]", name + "\r\n<br>" + jpName));
			templateContent.set(22, fullId);
			templateContent.set(27, caract);
			
			String producto = templateContent.get(7);
			if(ref.startsWith("T")){
				producto.replace("Booster Pack", "Trial Deck Plus");
				templateContent.set(7, producto.replace("Booster Pack", "Trial Deck Plus"));
			}
			
			templateContent.remove(32);

			List<String> cardContent = new ArrayList<String>();
			
			boolean referenciado = false;
			boolean referenciador = false;
			
			String habLinea = fileContent.remove(0);
			while(!habLinea.startsWith("-")){
				cardContent.add(habLinea);
				habLinea = fileContent.remove(0);
			}
			
			if(habLinea.startsWith("-#")) {
				referenciado = true;
			}
			else if(habLinea.startsWith("-%")) {
				referenciador = true;
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
			
			if(caract.startsWith("Climax") || referenciado) {
				String referencias = "<tr>\r\n<td>\r\n* Esta carta es referenciada en las habilidades de ''\r\n";
				referencias = referencias + "<a href='./" + seriesRef + "_" + ref + ".html'>" + name + "</a>\r\n";
				referencias = referencias + "</td>\r\n</tr>";
				templateContent.set(templateContent.indexOf("[Referencias]"), referencias);
			}
			else if (referenciador) {
				String referencias = "<a href='./" + seriesRef + "_" + ref + ".html'>" + name + "</a>";
				templateContent.set(templateContent.indexOf("[Referencias]"), referencias);
			}
			else {
				templateContent.remove(templateContent.indexOf("[Referencias]"));
			}
			
			String cardsPath = seriesWebPath + "cards\\"; 
			
			Files.write(new File(cardsPath + file).toPath(), templateContent, StandardCharsets.UTF_8);
		
			String indexPath = seriesWebPath + "index.html";
			
			List<String> indexContent = new ArrayList<>(Files.readAllLines(new File(indexPath).toPath(), StandardCharsets.UTF_8));
		
			for(int i = 0; i < indexContent.size(); i++){
				if(indexContent.get(i).endsWith(ref)){
					String newLine = fecha + "<a href='./cards/" + seriesRef + "_" + ref + ".html'><img src='./images/" + seriesRef + "_" + ref + ".png' width=100% height=auto id='" + seriesRef + "_" + ref + "'></img></a>" + fullId;
					indexContent.set(i, newLine);
				}
			}
			
			indexContent = Cotd_Web.getUpdatedIndexWithCompletionCount(indexContent, size);
			
			Files.write(new File(indexPath).toPath(), indexContent, StandardCharsets.UTF_8);
			
			String paddedCount = String.format("%02d", count);
			count++;
			
			File originFile = new File(conf.imagesFolder.getAbsolutePath() + "/jp_" + paddedCount + ".png");
			File targetFile = new File(conf.webFolder.getAbsolutePath() + "/" + seriesRef + "/images/" + seriesRef + "_" + ref + ".png");
			
			System.out.println("* Copying image: " + originFile.getName() + ", to : " + targetFile.getName());
			FileUtils.copyFile(originFile, targetFile);
		}
	}
	
	public static void createEmptyIndex() throws Exception{
		
		System.out.println("* Create Empty Index");

		String seriesId = "w56";
		String seriesFullId = "SHS/W56-";
		String productType = "Booster Pack";
		String seriesName = "[Saekano] How to Raise a Boring Girlfriend";
		
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
		for(int i = 1; i <= 10; i++){
			
			newFileContent.add("<tr>");
			
			for(int j = 1; j <= 10; j++){
				
				String paddedCount = String.format("%03d", count);;
				
				newFileContent.add("<td width=10%  align=center>");
				newFileContent.add("<img src='../images_default/no_image.png' width=100% height=auto id='pending_" + seriesId + "_" + paddedCount + "'></img>" + seriesFullId + paddedCount);
				newFileContent.add("</td>");
				
				count++;
			}
			
			newFileContent.add("</tr>");
		}
		
		newFileContent.add("</table>");
		newFileContent.add("</body>");
		
		Files.write(newFile.toPath(), newFileContent, StandardCharsets.UTF_8);
	}
	
	private static void updateNoImageColors(String seriesRef, boolean[] colors) throws Exception{
		
		System.out.println("** Update No Image Colors");
		
		Cotd_Conf conf = Cotd_Conf.getInstance();
		String seriesWebPath = conf.webFolder.getPath() + "\\" +  seriesRef + "\\";
		String indexPath = seriesWebPath + "index.html";
		
		ArrayList<String> indexContent = new ArrayList<String>(Files.readAllLines(new File(indexPath).toPath(), StandardCharsets.UTF_8));
	
		String currentColor = "Amarillo";
		ArrayList<Integer> currentIndexes = new ArrayList<Integer>();
		ArrayList<Integer> amarillo = new ArrayList<Integer>();
		/*ArrayList<Integer> amarilloVerde = new ArrayList<Integer>();
		ArrayList<Integer> verde = new ArrayList<Integer>();
		ArrayList<Integer> verdeRojo = new ArrayList<Integer>();*/
		ArrayList<Integer> amarilloRojo = new ArrayList<Integer>();
		ArrayList<Integer> rojo = new ArrayList<Integer>();
		ArrayList<Integer> rojoAzul = new ArrayList<Integer>();
		ArrayList<Integer> azul = new ArrayList<Integer>();
		
		for (int i = 0; i < indexContent.size(); i++){
			String line = indexContent.get(i);
			if(line.contains("img") && !line.contains("TD") && !line.contains("PR")){
				System.out.println("** Image for: " + line.substring(line.lastIndexOf(">") + 1));
				if(line.contains("href")){
					String href = line.replaceAll(".+href='\\./(.+?)'.+", "$1");
					File cardPage = new File(seriesWebPath + href);
					String cardColor = Cotd_Web.getCardColor(cardPage);
					System.out.println("* Color: " + cardColor);
					if(cardColor.equals("Amarillo") && currentColor.equals("Amarillo")){
						//amarillo.addAll(currentIndexes);
						currentIndexes.clear();
					}
					else if(cardColor.equals("Rojo") && currentColor.equals("Amarillo")){
						//amarilloRojo.addAll(currentIndexes);
						currentColor = "Rojo";
						currentIndexes.clear();
					}
					else if(cardColor.equals("Rojo") && currentColor.equals("Rojo")){
						rojo.addAll(currentIndexes);
						currentIndexes.clear();
					}
					else if(cardColor.equals("Azul") && currentColor.equals("Rojo")){
						rojoAzul.addAll(currentIndexes);
						currentColor = "Azul";
						currentIndexes.clear();
					}
				}
				else{
					System.out.println("* No Color.");
					currentIndexes.add(i);
				}
			}
		}
		azul.addAll(currentIndexes);
		for (int i = 0; i < indexContent.size(); i++){
			String line = indexContent.get(i);
			if(line.contains("img")){
				if(amarillo.contains(i)){
					indexContent.set(i, line.replaceAll("no_image.*?\\.", "no_image_y."));
				}
				else if(amarilloRojo.contains(i)){
					indexContent.set(i, line.replaceAll("no_image.*?\\.", "no_image_yr."));
				}
				else if(rojo.contains(i)){
					indexContent.set(i, line.replaceAll("no_image.*?\\.", "no_image_r."));
				}
				else if(rojoAzul.contains(i)){
					indexContent.set(i, line.replaceAll("no_image.*?\\.", "no_image_rb."));
				}
				else if(azul.contains(i)){
					indexContent.set(i, line.replaceAll("no_image.*?\\.", "no_image_b."));
				}
			}
		}
		/*if(currentColor.equals("Start")){
		indexContent.set(i, line.replaceAll("no_image.+?\\.", "no_image_y."));
	}
	if(currentColor.equals("Azul")){
		indexContent.set(i, line.replace("no_image.", "no_image_b."));
	}*/
		
		Files.write(new File(indexPath).toPath(), indexContent, StandardCharsets.UTF_8);
	}
	
	private static String getCardColor(File cardPage) throws Exception{
		String color = "¿No Color?";
		
		ArrayList<String> pageContent = new ArrayList<String>(Files.readAllLines(cardPage.toPath(), StandardCharsets.UTF_8));
		
		String descLine = pageContent.get(29);
		if(descLine.contains("Amarillo")){
			color = "Amarillo";
		}
		else if(descLine.contains("Verde")){
			color = "Verde";
		}
		else if(descLine.contains("Rojo")){
			color = "Rojo";
		}
		else if(descLine.contains("Azul")){
			color = "Azul";
		}
		
		return color;
	}
	
	private static List<String> getUpdatedIndexWithCompletionCount(List<String> indexContent, int size) throws Exception{
		
		//System.out.println("** Get Updated Index Completion Count");
		
		if(indexContent.indexOf("Recuento") > 0) {
		
			int countC = 0;
			int countU = 0;
			int countR = 0;
			int countRR = 0;
			int countCC = 0;
			int countCR = 0;
			
			for(int i = 0; i < indexContent.size(); i++){
				if(indexContent.get(i).endsWith(" C")){
					countC++;
				}
				if(indexContent.get(i).endsWith(" U")){
					countU++;
				}
				if(indexContent.get(i).endsWith(" R")){
					countR++;
				}
				if(indexContent.get(i).endsWith(" RR")){
					countRR++;
				}
				if(indexContent.get(i).endsWith(" CC")){
					countCC++;
				}
				if(indexContent.get(i).endsWith(" CR")){
					countCR++;
				}
			}
			
			//System.out.println("* Count RR: " + countRR);
			int iRR = indexContent.indexOf("<td align=center id='RR_Count'>");
			indexContent.set(iRR + 1, String.valueOf(countRR));
			
			//System.out.println("* Count R: " + countR);
			int iR = indexContent.indexOf("<td align=center id='R_Count'>");
			indexContent.set(iR + 1, String.valueOf(countR));
			
			//System.out.println("* Count U: " + countU);
			int iU = indexContent.indexOf("<td align=center id='U_Count'>");
			indexContent.set(iU + 1, String.valueOf(countU));
			
			//System.out.println("* Count C: " + countC);
			int iC = indexContent.indexOf("<td align=center id='C_Count'>");
			indexContent.set(iC + 1, String.valueOf(countC));
			
			//System.out.println("* Count CR: " + countCR);
			int iCR = indexContent.indexOf("<td align=center id='CR_Count'>");
			indexContent.set(iCR + 1, String.valueOf(countCR));
			
			//System.out.println("* Count CC: " + countCC);
			int iCC = indexContent.indexOf("<td align=center id='CC_Count'>");
			indexContent.set(iCC + 1, String.valueOf(countCC));
			
			int totalCount = countRR + countR + countU + countC + countCR + countCC;
			//System.out.println("* Total Count: " + totalCount);
			int iTC = indexContent.indexOf("<td align=center id='Total_Count'>");
			indexContent.set(iTC + 1, String.valueOf(totalCount) + "/" + String.valueOf(size));
		}
		
		return indexContent;
	}
}
