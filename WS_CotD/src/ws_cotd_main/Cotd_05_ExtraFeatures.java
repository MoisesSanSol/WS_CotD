package ws_cotd_main;

import ws_cotd.Cotd_Conf;
import ws_cotd_v2.Cotd_ExtraFeatures;

public class Cotd_05_ExtraFeatures {

	private Cotd_Conf conf;
	
	public Cotd_05_ExtraFeatures(){
		this.conf = Cotd_Conf.getInstance();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		Cotd_05_ExtraFeatures main = new Cotd_05_ExtraFeatures();
		
		Cotd_ExtraFeatures.fillPowerFromWsblog();
		Cotd_ExtraFeatures.checkCardsMissingFillInParts();
		
		System.out.println("*** Finished ***");
	}
	
}
