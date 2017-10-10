package ws_cotd;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main3 {

	public static String path = "D:\\Workshop\\WeissSchwarz\\FB\\CotD_Web\\current.txt";
	public static String path2 = "D:\\Workshop\\WeissSchwarz\\FB\\CotD_Web\\we28\\cards\\template.html";
	public static String path3 = "D:\\Workshop\\WeissSchwarz\\FB\\CotD_Web\\we28\\cards\\";
	public static String path4 = "D:\\Workshop\\WeissSchwarz\\FB\\CotD_Web\\we28\\index.html";
	public static String path5 = "D:\\Workshop\\WeissSchwarz\\FB\\CotD_Web\\we28\\index2.html";
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub

		List<String> fileContent = new ArrayList<>(Files.readAllLines(new File(path).toPath(), StandardCharsets.UTF_8));
		
		String fecha = fileContent.get(1).replace("Cartas del día ", "").replace(":", "");
		
		boolean keep = true;
		int end = fileContent.indexOf("-") + 1;
		fileContent.subList(0, 5).clear();
		List<String> cardContent = fileContent.subList(0, fileContent.indexOf("-") - 1);
		
		ArrayList<String> newCards = new ArrayList<String>();  
		
		while(keep){
			
			String name = cardContent.remove(0);
			String fullId = cardContent.remove(0);
			String id = fullId.split(" ")[0];
			String ref = id.split("-")[1];
			newCards.add(ref);
			String caract1 = cardContent.remove(0);
			String caract = caract1 + ", " + cardContent.remove(0);
			cardContent.remove(0);
			
			List<String> templateContent = new ArrayList<>(Files.readAllLines(new File(path2).toPath(), StandardCharsets.UTF_8));
			
			templateContent.set(1, templateContent.get(1).replace("[Card Id]", id));
			templateContent.set(12, templateContent.get(12).replace("[Ref]", ref));
			templateContent.set(17, templateContent.get(17).replace("[Nombre]", name));
			templateContent.set(22, fullId);
			templateContent.set(27, caract.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;"));
			templateContent.remove(32);
			
			Collections.reverse(cardContent);
			
			while(!cardContent.isEmpty()){
				String hab = cardContent.remove(0);
				templateContent.add(32, hab.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;"));
				if(!cardContent.isEmpty()){
					templateContent.add(32, "<br>");
				}
			}
			
			String file = "we28_" + ref + ".html";
			
			templateContent.add(0,"<meta charset=\"utf-8\">");
			
			Files.write(new File(path3 + file).toPath(), templateContent, StandardCharsets.UTF_8);
			
			fileContent.remove(0);
			fileContent.remove(0);
			
			end = fileContent.indexOf("-");

			if(end == -1){
				keep = false;
			}
			else{
				fileContent.remove(0);
				cardContent = fileContent.subList(0, fileContent.indexOf("-") - 1);
			}
			
		}
		
		List<String> indexContent = new ArrayList<>(Files.readAllLines(new File(path4).toPath(), StandardCharsets.UTF_8));
		
		for(int i = 0; i < indexContent.size(); i++){
			for(String ref : newCards){
				if(indexContent.get(i).endsWith(ref)){
					String newLine = fecha + "<a href='./cards/we28_" + ref + ".html'><img src='./images/we28_" + ref + ".png' width=100% height=auto></img></a>HLL/WE28-" + ref;
					indexContent.set(i, newLine);
				}
			}
		}
		
		Files.write(new File(path4).toPath(), indexContent, StandardCharsets.UTF_8);
		
	}

}
