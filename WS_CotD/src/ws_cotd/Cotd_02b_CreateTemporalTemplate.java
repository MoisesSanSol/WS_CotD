package ws_cotd;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class Cotd_02b_CreateTemporalTemplate {

	private Cotd_Conf conf;
	
	public Cotd_02b_CreateTemporalTemplate(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	
	public static void main(String[] args) throws Exception{
		System.out.println("*** Starting ***");
		
		Cotd_02b_CreateTemporalTemplate main = new Cotd_02b_CreateTemporalTemplate();

		main.createTemporalTemplateFile();
		
		/*Desktop desktop = Desktop.getDesktop();
		File dirToOpen = null;
		try {
			dirToOpen = new File(main.conf.resultsFolder);
			desktop.open(dirToOpen);

			dirToOpen = new File(CotD_Conf.referencesFolder);
			desktop.open(dirToOpen);
			
			String fullPathFile = CotD_Conf.resultsFolder + CotD_Conf.temporalFile;
			
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("C:\\Program Files (x86)\\Notepad++\\notepad++.exe " + fullPathFile);
			
		} catch (IllegalArgumentException iae) {
			System.out.println("File Not Found");
		}*/
		
		System.out.println("*** Finished ***");

	}

	private static String getImageColor(File imageFile) throws Exception{
		String color = "Desconocido";
		//System.out.println("Image = "+ imageFile.getName());
		
		BufferedImage image = ImageIO.read(imageFile);
		int x = 0;
		int y = 0;
		if(image.getHeight() > image.getWidth()){
			x = 325;
			y = 465;
		}
		else{
			x = 240;
			y = 310;
		}
		// Getting pixel color by position x and y 
		int clr=  image.getRGB(x, y); 
		int  red   = (clr & 0x00ff0000) >> 16;
		int  green = (clr & 0x0000ff00) >> 8;
		int  blue  =  clr & 0x000000ff;
		
		/*System.out.println("Red Color value = "+ red);
		System.out.println("Green Color value = "+ green);
		System.out.println("Blue Color value = "+ blue);*/
		
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
	
	private String getImageType(File imageFile) throws Exception{
		String tipo = "Desconocido";
		//System.out.println("Image = "+ imageFile.getName());
		
		BufferedImage image = ImageIO.read(imageFile);

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
	
	private int getImageLevel(BufferedImage image) throws Exception{
		int level = -1;
		
		Color color0 = this.getPixelColor(image, 27, 36);
		Color color1 = this.getPixelColor(image, 33, 41);
		Color color2 = this.getPixelColor(image, 37, 44);
		
		if(color0.getRGB() == -1){
			level = 0;
		}
		else if(color1.getRGB() == -1){
			level = 1;
		}
		else if(color2.getRGB() == -1){
			level = 2;
		}
		else{
			level = 3;
		}

		
		return level;
	}
	
	private String getImageCost(){
		String color = "";
		return color;
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
	
	private void createTemporalTemplateFile() throws Exception{
		
		String fullPathWrite = CotD_Conf.resultsFolder + CotD_Conf.temporalFile;
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPathWrite), "UTF-8"));

		File imagesFolder = new File(CotD_Conf.imagesFolder);
		File[] imageFiles = imagesFolder.listFiles();
	
		writer.write("****************************************\r\n");
		writer.write("Cartas del día ");
		writer.write(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		writer.write(":\r\n");
		writer.write("\r\n");
		
		for(File imageFile : imageFiles){
			if(imageFile.getName().startsWith("jp")){
				
				System.out.println("Image = "+ imageFile.getName());
				
				writer.write("\r\n");
				writer.write("\r\n");
				writer.write("\r\n");
				writer.write("-");
				writer.write("\r\n");
				writer.write("\r\n");
				writer.write(imageFile.getName());
				writer.write("\r\n");
				writer.write("\r\n");
				
				String color = Cotd_02b_CreateTemporalTemplate.getImageColor(imageFile);
				String type = Cotd_02b_CreateTemporalTemplate.getImageType(imageFile);
				
				switch(type){
				case "Personaje":
					int nivel = Cotd_02b_CreateTemporalTemplate.getImageLevel(ImageIO.read(imageFile));
					int coste = nivel - 1;
					if(coste < 0) coste = 0;
					int soul = 1;
					if(nivel > 2) soul = 2;
					String trigger = "No";
					if(nivel > 1) trigger = "1 Soul";
					writer.write("Personaje ");
					writer.write(color);
					writer.write(", Nivel: " + nivel + ", Coste: " + coste + ", Poder: 00, Soul: " + soul + ", Trigger: " + trigger);
					writer.write("\r\n");
					writer.write("Traits: <<>> y <<>>.");
					writer.write("\r\n");
					break;
				case "Climax":
					writer.write("Climax ");
					writer.write(color);
					writer.write(", Trigger: 2 Soul.");
					writer.write("\r\n");
					break;
				case "Evento":
					int nivelE = Cotd_02b_CreateTemporalTemplate.getImageLevel(ImageIO.read(imageFile));
					writer.write("Evento ");
					writer.write(color);
					writer.write(", Nivel: " + nivelE + ", Coste: , Trigger: No.");
					writer.write("\r\n");
					break;
				}
			}
		}

		writer.close();
	}
}

