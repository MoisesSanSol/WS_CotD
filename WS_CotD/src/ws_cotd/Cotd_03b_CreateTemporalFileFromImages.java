package ws_cotd;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ws_cotd_v2.Cotd_FromGlobal;

public class Cotd_03b_CreateTemporalFileFromImages {

	private Cotd_Conf conf;
	
	private static boolean useGlobalFile = true;
	
	public Cotd_03b_CreateTemporalFileFromImages(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		Cotd_03b_CreateTemporalFileFromImages main = new Cotd_03b_CreateTemporalFileFromImages();
		main.generateTemporalFileFromImages(useGlobalFile);
		
		Cotd_Utilities.openFileInNotepad(main.conf.temporalFile);
		
		System.out.println("*** Finished ***");
	}
	
	public void generateTemporalFileFromImages(boolean cardTextFromGlobal) throws Exception{
		
		System.out.println("** Generate Temporal File From Images");
		
		ArrayList<String> temporalContent = new ArrayList<String>();  
		
		temporalContent.add("****************************************");
		String dateLine = "Cartas del día " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ":";
		temporalContent.add(dateLine);
		//temporalContent.add("");
		
		List<String> currentSeriesContent = new ArrayList<>(Files.readAllLines(conf.currentSeriesFile.toPath(), StandardCharsets.UTF_8));
		String seriesHeader = "";
		String seriesId = "";
		
		seriesHeader = currentSeriesContent.remove(0);
		seriesId = currentSeriesContent.remove(0);
		ArrayList<String> cardHeader = new ArrayList<String>();
		cardHeader.add(seriesHeader);
		cardHeader.add("");
		cardHeader.add("# Name goes here");
		cardHeader.add("# Jp Name goes here");
		cardHeader.add(seriesId);
		
		List<String> fromImagesContent = new ArrayList<>(Files.readAllLines(conf.fromImagesFile.toPath(), StandardCharsets.UTF_8));
		
		ArrayList<ArrayList<String>> cardsText = new ArrayList<ArrayList<String>>();
		if(cardTextFromGlobal){
			cardsText = this.getCardsTextFromGlobal();
		}
		
		while(fromImagesContent.size() > 0){
			
			String line = fromImagesContent.remove(0);
			
			if(line.startsWith("-")){
				temporalContent.add("");
				if(cardTextFromGlobal){
					ArrayList<String> cardText = this.parseCardTextFromGlobal(cardsText.remove(0), cardHeader);
					temporalContent.addAll(cardText);
					cardHeader.clear();
				}else{
					temporalContent.addAll(cardHeader);
					temporalContent.add("");
					temporalContent.add("* Abilities go here");
					cardHeader.clear();
				}
				//temporalContent.add("");
				if(line.startsWith("---")){
					// Do nothing
				}
				else{
					if(line.startsWith("--")){
						seriesHeader = currentSeriesContent.remove(0);
						seriesId = currentSeriesContent.remove(0);
						cardHeader.add(seriesHeader);
					}
					else{
						cardHeader.add("-");
					}
					cardHeader.add("");
					cardHeader.add("# Name goes here");
					cardHeader.add("# Jp Name goes here");
					cardHeader.add(seriesId);
				}
			}
			else{
				cardHeader.add(line);
			}

		}
		temporalContent.add("");
		temporalContent.add("-");
		
		Files.write(conf.temporalFile.toPath(), temporalContent, StandardCharsets.UTF_8);
	}
	
	private ArrayList<ArrayList<String>> getCardsTextFromGlobal() throws Exception{
		
		System.out.println("* Get Cards Text From Global");
		
		ArrayList<ArrayList<String>> cardsText = new ArrayList<ArrayList<String>>();
		
		List<String> fromGlobalContent = new ArrayList<>(Files.readAllLines(conf.fromGlobalFile.toPath(), StandardCharsets.UTF_8));
		
		fromGlobalContent.remove(0); // Delete first series header
		
		while(!fromGlobalContent.isEmpty()){
			ArrayList<String> cardText = new ArrayList<String>();
			String textLine = fromGlobalContent.remove(0);
			while(!textLine.startsWith("-")){
				cardText.add(textLine);
				textLine = fromGlobalContent.remove(0);
			}
			cardsText.add(cardText);
		}
		
		return cardsText;
	}
	
	private ArrayList<String> parseCardTextFromGlobal(ArrayList<String> globalCardText, ArrayList<String> header) throws Exception{
		
		ArrayList<String> cardText = new ArrayList<String>();
		
		String firstGlobalLine = globalCardText.get(0);
		
		if(firstGlobalLine.matches(".*\\((.+?)\\) (.+?) \\((.+?)\\/(.+)\\).*")){
			header.set(2, "# Name goes here: " + firstGlobalLine.replaceAll(".*\\((.+?)\\) (.+?) \\((.+?)\\/(.+)\\).*", "$2"));
			header.set(4, header.get(4) + " " + firstGlobalLine.replaceAll(".*\\((.+?)\\) (.+?) \\((.+?)\\/(.+)\\).*", "$1"));
			header.set(6, firstGlobalLine.replaceAll(".*\\((.+?)\\) .+? \\((.+?)\\/(.+)\\).*", "Traits: <<$2>> y <<$3>>."));
			globalCardText.remove(0);
		}
		else if(firstGlobalLine.matches(".*\\((.+?)\\) (.+?) \\((.+?)\\).*")){
			header.set(2, "# Name goes here: " + firstGlobalLine.replaceAll(".*\\((.+?)\\) (.+?) \\((.+?)\\).*", "$2"));
			header.set(4, header.get(4) + " " + firstGlobalLine.replaceAll(".*\\((.+?)\\) (.+?) \\((.+?)\\).*", "$1"));
			header.set(6, firstGlobalLine.replaceAll(".*\\((.+?)\\) .+? \\((.+?)\\).*", "Traits: <<$2>>."));
			globalCardText.remove(0);
		}
		else if(firstGlobalLine.startsWith("SR")){
			ArrayList<String> auxHeader = new ArrayList<String>();
			auxHeader.add(header.get(0));
			auxHeader.add(header.get(1));
			String cardId = firstGlobalLine.split(" ")[1];
			header.clear();
			header.addAll(auxHeader);
			header.addAll(Cotd_FromGlobal.getParallelCard(cardId, "S SR"));
			globalCardText.clear();
		}
		else if(firstGlobalLine.startsWith("RRR")){
			ArrayList<String> auxHeader = new ArrayList<String>();
			auxHeader.add(header.get(0));
			auxHeader.add(header.get(1));
			String cardId = firstGlobalLine.split(" ")[1];
			header.clear();
			header.addAll(auxHeader);
			header.addAll(Cotd_FromGlobal.getParallelCard(cardId, "R RRR"));
			globalCardText.clear();
		}
		else if(firstGlobalLine.startsWith("SP")){
			ArrayList<String> auxHeader = new ArrayList<String>();
			auxHeader.add(header.get(0));
			auxHeader.add(header.get(1));
			String cardId = firstGlobalLine.split(" ")[1];
			header.clear();
			header.addAll(auxHeader);
			header.addAll(Cotd_FromGlobal.getParallelCard(cardId, "SP SP"));
			globalCardText.clear();
		}
		else if(firstGlobalLine.startsWith("SEC")){
			ArrayList<String> auxHeader = new ArrayList<String>();
			auxHeader.add(header.get(0));
			auxHeader.add(header.get(1));
			String cardId = firstGlobalLine.split(" ")[1];
			header.clear();
			header.addAll(auxHeader);
			header.addAll(Cotd_FromGlobal.getParallelCard(cardId, "SEC SEC"));
			globalCardText.clear();
		}
		else if(firstGlobalLine.startsWith("CX")){
			header.set(2, "# Name goes here: " + firstGlobalLine);
			if(firstGlobalLine.contains("Wind")){
				header.set(5, "Climax Amarillo, Trigger: 1 Soul, Return.");
				header.add("(Return: Cuando esta carta es revelada durante un Trigger Check, puedes escoger un personaje de tu oponente y devolverlo a su mano.)");
				globalCardText.clear();
				globalCardText.add("[CONT] Todos tus personajes ganan +1000 de Poder y +1 Soul.");
			}
			if(firstGlobalLine.contains("Shot")){
				header.set(5, "Climax Amarillo, Trigger: 1 Soul, Shot.");
				header.add("(Shot: Cuando el próximo daño hecho por el personaje cuyo Trigger Check reveló esta carta sea cancelado, puedes hacer un daño a tu oponente.)");
				globalCardText.clear();
				globalCardText.add("[CONT] Todos tus personajes ganan +1000 de Poder y +1 Soul.");
			}
				if(firstGlobalLine.contains("Treasure")){
				header.set(5, "Climax Verde, Trigger: Treasure.");
				header.add("(Treasure: Cuando esta carta es revelada durante un Trigger Check, pon esta carta en tu mano, y puedes poner la carta superior de tu Deck en tu Stock.)");
				globalCardText.clear();
				globalCardText.add("[CONT] Todos tus personajes ganan +1000 de Poder y +1 Soul.");
			}
			if(firstGlobalLine.contains("Bag")){
				header.set(5, "Climax Verde, Trigger: Pool.");
				header.add("(Pool: Cuando esta carta es revelada durante un Trigger Check, puedes poner la carta superior de tu Deck en tu Stock.)");
				globalCardText.clear();
				globalCardText.add("[CONT] Todos tus personajes ganan +1000 de Poder y +1 Soul.");
			}
			if(firstGlobalLine.contains("Gate")){
				header.set(5, "Climax Rojo, Trigger: Comeback.");
				header.add("(Comeback: Cuando esta carta es revelada durante un Trigger Check, puedes poner 1 personaje de tu Waiting Room en tu mano.)");
				globalCardText.clear();
				globalCardText.add("[CONT] Todos tus personajes ganan +1000 de Poder y +1 Soul.");
			}
			if(firstGlobalLine.contains("Standby")){
				header.set(5, "Climax Rojo, Trigger: Standby, 1 Soul.");
				header.add("(Standby: Cuando esta carta es revelada durante un Trigger Check, puedes escoger 1 personaje de Nivel igual o inferior a tu Nivel más 1 en tu Waiting Room y ponerlo en cualquier posición de tu Stage en [Rest].)");
				globalCardText.clear();
				globalCardText.add("[AUTO] Cuando esta carta es puesta de tu mano en tu área de Climax, realiza el efecto del icono de Trigger 'Standby'.");
			}
			if(firstGlobalLine.contains("Book")){
				header.set(5, "Climax Azul, Trigger: Book.");
				header.add("(Book: Cuando esta carta es revelada durante un Trigger Check, puedes robar una carta.)");
				globalCardText.clear();
				globalCardText.add("[CONT] Todos tus personajes ganan +1000 de Poder y +1 Soul.");
			}
			if(firstGlobalLine.contains("Pants")){
				header.set(5, "Climax Azul, Trigger: Gate, 1 Soul.");
				header.add("(Gate: Cuando esta carta es revelada durante un Trigger Check, puedes poner 1 Climax de tu Waiting Room en tu mano.)");
				globalCardText.clear();
				globalCardText.add("[CONT] Todos tus personajes ganan +1000 de Poder y +1 Soul.");
			}
		}

		header.set(4, header.get(4).replace("TD TD", "TD"));
		
		cardText.addAll(header);
		cardText.add("");
		
		for(String abilityLine : globalCardText){
			cardText.add("* " + abilityLine);
		}
		return cardText;
	}
}
