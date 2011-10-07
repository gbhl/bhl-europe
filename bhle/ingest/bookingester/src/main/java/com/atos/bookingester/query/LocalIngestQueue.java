package com.atos.bookingester.query;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;


public class LocalIngestQueue implements IngestQueue {

	private String metsDirLoc;
	private List<InformationPackage> packs = new ArrayList<InformationPackage>();
	
	public LocalIngestQueue(String metsDirLoc) {
		super();
		this.metsDirLoc = metsDirLoc;
	}

	@Override
	public void query() {
		File metsDir = new File(metsDirLoc);
		if (metsDir.isDirectory()){
			File[] metsFiles = metsDir.listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".mets.xml");
				}
			});
			
			for (File metsFile : metsFiles){
				InformationPackage pack = new InformationPackage();
				pack.setMetsfile(metsFile.toString());
				pack.setType(InformationPackageType.LOCAL);
				packs.add(pack);
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return packs.isEmpty();
	}

	@Override
	public List<InformationPackage> getInformationPackages() {
		return packs;
	}

	@Override
	public void reset() {
		packs.clear();
	}
}
