package com.atos.bookingester.rebuild;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import au.edu.apsr.mtk.base.FileGrp;
import au.edu.apsr.mtk.base.METSException;

import com.atos.bookingester.query.InformationPackage;

public class BookRebuildStrategy implements RebuildStrategy {
	private static String ID_IMAGE_PREFIX = "IMAGE.";
	private static String ID_OCR_PREFIX = "OCR.";
	private static String ID_TAXA_PREFIX = "TAXA.";

	private static String PID_PREFIX = "bhle:";

	public FedoraObject rebuildObject(MetsObject mets) {
		Book book = new Book();
		setUpBookDC(book, mets);
		setUpBookMODS(book, mets);
		setUpBookPages(book, mets);
		return book;
	}

	private void setUpBookDC(Book book, MetsObject mets) {
		book.setID(PID_PREFIX + mets.getID());
		book.setTitle(mets.getID());
	}

	private void setUpBookMODS(Book book, MetsObject mets) {
		book.setMODS(mets.getMODS());
	}

	private void setUpBookPages(Book book, MetsObject mets) {
		try {
			book.setPages(getPages(book, mets));
		} catch (METSException e) {
			e.printStackTrace();
		}
	}

	private List<Page> getPages(Book book, MetsObject mets)
			throws METSException {
		List<Page> pages = new ArrayList<Page>();

		DecimalFormat formatter = new DecimalFormat("000");

		boolean morePages = true;
		int index = 0;
		while (morePages) {
			morePages = false;
			Page page = new Page();
			index++;
			for (FileGrp fileGrp : mets.getDatastreamFileGrps()) {
				String fileGrpID = fileGrp.getID();

				if (fileGrpID.equals(ID_IMAGE_PREFIX + index)) {
					page.setImage(getHref(index, ID_IMAGE_PREFIX, fileGrp));
				}
				if (fileGrpID.equals(ID_OCR_PREFIX + index)) {
					page.setOCR(getHref(index, ID_OCR_PREFIX, fileGrp));
				}
				if (fileGrpID.equals(ID_TAXA_PREFIX + index)) {
					page.setTAXA(getHref(index, ID_TAXA_PREFIX, fileGrp));
				}
				if (fileGrpID.equals(ID_IMAGE_PREFIX + (index + 1))) {
					morePages = true;
				}
			}

			page.setID(PID_PREFIX + mets.getID() + "-"
					+ formatter.format(index));
			page.setBook(book);

			pages.add(page);
		}
		return pages;
	}

	private String getHref(int index, String prefix, FileGrp fileGrp)
			throws METSException {
		return fileGrp.getFile(prefix + index + ".FILE").getFLocats().get(0)
				.getHref();
	}
}
