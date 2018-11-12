package ws_tcgresults;

import java.util.ArrayList;

public class WsTcgResults_Main {

	public static void main(String[] args) throws Exception {
		
		System.out.println("*** Starting ***");
		
		ArrayList<WsTcgResults_Deck> decklists = WsTcgResults_Scrapper.getDecklists("recipe_osaka_wgp2018_01", "Osaka", "NeoStandard");
		WsTcgResults_Builder.printDecklists(decklists);
		
		System.out.println("*** Finished ***");
	}
}
