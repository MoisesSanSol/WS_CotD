package ws_cotd;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

public class Cotd_02b_CreateTemplateFromImages {

	private Cotd_Conf conf;
	
	public Cotd_02b_CreateTemplateFromImages(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		Cotd_02b_CreateTemplateFromImages main = new Cotd_02b_CreateTemplateFromImages();

		main.createTemporalTemplateFile();
		
		Cotd_Utilities.openFileInNotepad(main.conf.fromImagesFile);
		
		System.out.println("*** Finished ***");

	}

	private String getImageColor(BufferedImage image, String tipo) throws Exception{

		String color = "Desconocido";
		
		int x = 0;
		int y = 0;
		
		if(tipo.equals("Personaje")){
			x = 325;
			y = 465;
		}
		else if (tipo.equals("Evento")){
			x = 325;
			y = 475;
		}
		else if (tipo.equals("Climax")){
			x = 240;
			y = 310;
		}
		else{
			return color;
		}
		
		// Getting pixel color by position x and y 
		int clr = image.getRGB(x, y); 
		int  red   = (clr & 0x00ff0000) >> 16;
		int  green = (clr & 0x0000ff00) >> 8;
		int  blue  =  clr & 0x000000ff;
		
		System.out.println("Red Color value = "+ red);
		System.out.println("Green Color value = "+ green);
		System.out.println("Blue Color value = "+ blue);
		
		if(red > 200  && green > 200 && blue < 50){
			color = "Amarillo";
		}
		if(red < 50  && green > 100 && blue < 100){
			color = "Verde";
		}
		if(red > 150  && green < 100 && blue < 100){
			color = "Rojo";
		}
		if(red < 50  && green > 100 && blue > 100){
			color = "Azul";
		}
		  
		return color;
	}
	
	private String getImageType(BufferedImage image) throws Exception{

		String tipo = "Desconocido";

		if(image.getHeight() < image.getWidth()){
			tipo = "Climax";
		}
		else{
			int clr =  image.getRGB(94, 457); 
			int  red   = (clr & 0x00ff0000) >> 16;
			int  green = (clr & 0x0000ff00) >> 8;
			int  blue  =  clr & 0x000000ff;
			
			if(red < 50  && green < 50 && blue < 50){
				tipo = "Personaje";
			}
			else{
				tipo = "Evento";
			}
		}

		return tipo;
	}
	
	private String getImageLevel(BufferedImage image) throws Exception{
		
		String level = "-1";
		
		if(this.getPixelColorBW(image, 32, 32) == -1){
			level = "1";
		}
		else if(this.getPixelColorBW(image, 28, 37) == -1){
			level = "0";
		}
		else if(this.getPixelColorBW(image, 30, 36) == -1){
			level = "3";
		}
		else if(this.getPixelColorBW(image, 37, 44) == -1){
			level = "2";
		}
		
		return level;
	}
	
	private String getImageCost(BufferedImage image) throws Exception{
		
		String coste = "-1";
		
		if(this.getPixelColorBW(image, 29, 72) < -10000000){
			coste = "2";
		}
		else if(this.getPixelColorBW(image, 32, 66) < -10000000){
			coste = "1";
		}
		else if(this.getPixelColorBW(image, 29, 66) < -10000000){
			coste = "0";
		}
		
		return coste;
	}
	
	/*private String getImageCost(String nivel){
		// TODO
		String coste = "0";
		
		if(nivel.equals("2")){
			coste = "1";
		}
		else if(nivel.equals("3")){
			coste = "2";
		}
		
		return coste;
	}*/
	
	private String getImageTrigger(String nivel){
		// TODO
		String trigger = "No";
		
		if(nivel.equals("2")){
			trigger = "1 Soul";
		}
		else if(nivel.equals("3")){
			trigger = "1 Soul";
		}
		
		return trigger;
	}
	
	private String getImageSoul(String nivel){
		// TODO
		String soul = "1";
		
		if(nivel.equals("3")){
			soul = "2";
		}
		
		return soul;
	}
	
	private Color getPixelColor(BufferedImage image, int x, int y) throws Exception{

		int clr =  image.getRGB(x, y); 
		int  red   = (clr & 0x00ff0000) >> 16;
		int  green = (clr & 0x0000ff00) >> 8;
		int  blue  =  clr & 0x000000ff;

		Color color = new Color(red, green, blue);
		
		//System.out.println(color.getRGB());
		/*System.out.println(color.getRed());
		System.out.println(color.getGreen());
		System.out.println(color.getBlue());*/
		
		return color;
	}
	
	private int getPixelColorBW(BufferedImage image, int x, int y) throws Exception{

		int clr =  image.getRGB(x, y); 
		
		System.out.println("* x: " + x + ", y: " + y + ", color: " + clr);
		
		return clr;
	}
	
	public void createTemporalTemplateFile() throws Exception{
		
		System.out.println("** Create Temporal Template File");
		
		ArrayList<String> fileContent = new ArrayList<String>();
		
		FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
               return !pathname.getName().endsWith("_2.png");
            }
        };
		File[] imageFiles = conf.imagesFolder.listFiles(filter);

		for(File imageFile : imageFiles){

			BufferedImage image = ImageIO.read(imageFile);
			System.out.println("* Analyzing Image: " + imageFile.getName());
			
			String line = "";
			String tipo = this.getImageType(image);
			System.out.println("* Image Type: " + tipo);
			String color = this.getImageColor(image, tipo);
			System.out.println("* Image Color: " + color);
			String nivel = "";
			String coste = "";
			String poder = "";
			String soul = "";
			String trigger = "";
			
			switch(tipo){
			
				case ("Personaje"):
					
					nivel = this.getImageLevel(image);
					System.out.println("* Image Level: " + nivel);
					coste = this.getImageCost(image);
					System.out.println("* Image Cost: " + coste);
					soul = this.getImageSoul(nivel);
					System.out.println("* Image Soul: " + soul);
					trigger = this.getImageTrigger(nivel);
					System.out.println("* Image Trigger: " + trigger);
					
					line = "Personaje ";
					line = line + color;
					line = line + ", Nivel: " + nivel;
					line = line + ", Coste: " + coste;
					line = line + ", Poder: 00";
					line = line + ", Soul: " + soul;
					line = line + ", Trigger: " + trigger + ",";
					fileContent.add(line);
					
					line = "Traits: <<>> y <<>>.";
					fileContent.add(line);
					
					break;
				
				case ("Evento"):
					
					nivel = this.getImageLevel(image);
					System.out.println("* Image Level: " + nivel);
					coste = this.getImageCost(image);
					System.out.println("* Image Cost: " + coste);
					soul = this.getImageSoul(nivel);
					
					line = "Evento ";
					line = line + color;
					line = line + ", Nivel: " + nivel;
					line = line + ", Coste: " + coste;
					line = line + ", Trigger: No.";
					fileContent.add(line);
					
					break;
					
				case ("Climax"):
					
					line = "Climax ";
					line = line + color;
					line = line + ", Trigger: 2 Soul.";
					fileContent.add(line);
				
					break;
				
				case ("Desconocido"):
				
					line = "Whatever";
					fileContent.add(line);
				
					break;
			}
			
			fileContent.add("-");
		}
		
		fileContent.set(fileContent.size()-1, "---");
		
		Files.write(conf.fromImagesFile.toPath(), fileContent, StandardCharsets.UTF_8);
	}
	
	
	private void test_CreateImageFromArea(File imageFile) throws Exception{
		
		System.out.println("** Test Function: Create Image From Area");
		
		// Soul area
		/*int x = 108;
	    int y = 458;
	    int w = 54;
	    int h = 13;*/

		// Level area
		int x = 15;
	    int y = 11;
	    int w = 38;
	    int h = 44;
		
	    BufferedImage image = ImageIO.read(imageFile);
	    BufferedImage out = image.getSubimage(x, y, w, h);

	    String areaPath = imageFile.getAbsolutePath().replace(".png", "_area.png");
	    
	    ImageIO.write(out, "png", new File(areaPath));
		
	}
	
	private void test_AreaColors(File imageFile) throws Exception{
		
		System.out.println("** Test Function: Area Colors");
		
		int x = 32;
	    int y = 32;
	    int w = x + 0;
	    int h = y + 1;
		
	    BufferedImage image = ImageIO.read(imageFile);
	    
	    for( ; x <= w; x++){
		    for( ; y <= h; y++){
		    	this.getPixelColorBW(image, x, y);
		    }
	    }
		
	}
}

