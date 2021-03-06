package ws_cotd_web;

import java.util.ArrayList;

public class CotdWeb_Main {

	public static void main(String[] args) throws Exception{
		
		System.out.println("*** Starting ***");
		
		System.out.println("** Generate Web Content From Temporal");

		ArrayList<CotdWeb_Card> cards = CotdWeb_Parser.getCardsFromTemporal();
		
		cards = CotdWeb_CardListHelper.escapeCardsForHtml(cards);
		cards = CotdWeb_CardListHelper.updateReferences(cards);
		cards = CotdWeb_CardListHelper.updateNotes(cards);
		
		CotdWeb_ImageHelper.copyImages(cards);
		
		CotdWeb_PageHelper.createCardPages(cards);
		CotdWeb_IndexHelper.updateSeriesIndex(cards);
		
		CotdWeb_PageHelper.updatePreviousNextLinks();
		CotdWeb_IndexHelper.updateIndexPendingCardsColor();
		CotdWeb_IndexHelper.createDateBasedIndex();
		
		System.out.println("*** Finished ***");
	}
}
