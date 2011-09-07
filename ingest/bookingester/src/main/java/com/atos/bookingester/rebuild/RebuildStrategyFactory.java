package com.atos.bookingester.rebuild;

public class RebuildStrategyFactory {

	public static RebuildStrategy getStrategy(FedoraObjectType type) {
		switch (type){
		case BOOK_COLLECTION:
			return new BookCollectionRebuildStrategy();
		case BOOK:
			return new BookRebuildStrategy();
		case ARTICLE:
			return new ArticalRebuildStrategy();
		}
		return null;
	}

}
