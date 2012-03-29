# awk script to create structure for  de-uber
#
#
#
BEGIN {
	if( targetFolder == "" || scansFolder ==""){
		print "targetFolder and scansFolder must be specified when executing this script";
		exit;
	}
}
{if ($1 == "ID_Inventar:") {id=$2; print id >> "._id"} }
{if (index($0, "STRUCTURE")) inStructure=1} 
{if (index($0, "SEQUENCE")) inStructure=0} 
{if (inStructure==1 && ! match($0,"^--.*") && length($2) > 0 ) { 

		n=split($2, pages, "-"); 
		if(n == 2){
			fromPage=pages[1];
			toPage=pages[2];
		} else {
			fromPage=$2;
			toPage=$2;
		}

		# one line per chapter subfolder
		i=3;
		chapterLine = "";
		while($i != "") {
			isChapterNumber = match($i,"[0-9]+\x2E.*")
			if( lastToken == $i || !isChapterNumber ) {
				if(isChapterNumber) {
					chapterLine = chapterLine $i;
 			} else {
 				chapterLine = chapterLine " " $i;
 			}
			}	
			lastToken = $i;
			i++;
		}
		print fromPage ": " chapterLine;
		# avoid empty chapter lines
		gsub("\s", "", chapterLine);
		if(chapterLine != ""){	
			chapters[fromPage] = chapterLine;
			eocPage = toPage + 1;
			print "eocPage " eocPage;
			chapters[eocPage] = "[EOC]"; # end of Chapter, will potentially be overwritten by next chapter start
		}
		
		# one image filename per line
		for(p=fromPage; p<=toPage; p++) {
		#print id "_" p  "_" $1 >> "._filenames";
		filenames[p] = id "_" p  "_" $1
		}

	}
}

END {
	dirList = "ls -1 " scansFolder;
	# start output in targetFolder
	outDir = targetFolder;
 	while( dirList | getline > 0) {
 		# extract page number from tiff file
 		p=$0
 		sub("^.*_0", "", p); # remove everything up to the start of the page number
		sub("^0*", "", p); # remove all remaining leading zeros
		sub(".tif", "", p); # remove filename extension

		if(chapters[p]){ # [EOC] = end of Chapter
			if(chapters[p] == "[EOC]"){
				print "CHAPTER: " p ": " chapters[p];
				outDir = targetFolder;
			} else {
				if(currentChapter != chapters[p]){	
					chapterDir = id "_" p;
					print "CHAPTER: " p ": " chapters[p];
					system("mkdir " targetFolder "/" chapterDir);
				}
				outDir = targetFolder "/" chapterDir;
			}
		}

		if(filenames[p]){
			newFilename = filenames[p] ".tiff"
		} else {
			newFilename = $0;
		}
		# copy scan to new folder and give it a new name
		system("cp " scansFolder "/" $0 " " outDir "/" newFilename);
	}
	#for (p in filenames) {
	#	print p ": " filenames[p];
	#}
	#for (p in chapters) {
	#	print "CHAPTER: " p ": " chapters[p];
	#}
	print "DONE"
}