# awk script to create structure for  de-uber
#
#
#

function splitPagesRange(string) {
	pages[1] = "";
	pages[2] = "";
	fromPage = "";
	toPage = "";
	
	n=split(string, pages, "-"); 
	if(n == 2){
		fromPage=pages[1];
		toPage=pages[2];
	} else {
		fromPage=$2;
		toPage=$2;
	}
}

BEGIN {
	if( targetFolder == "" || scansFolder ==""){
		print "targetFolder and scansFolder must be specified when executing this script";
		exit;
	}
	# mapping of non standart uber page types to BHL-E page types:
	pageTypeMap["chapter"] = "page";
	pageTypeMap["titlepage"] = "title";
	pageTypeMap["foreword"] = "preface";
	pageTypeMap["errata"] = "page";
	pageTypeMap["toc"] = "index";
	pageTypeMap["tocimage"] = "index";
	pageTypeMap["tocplates"] = "index";
	pageTypeMap["part"] = "page";
	pageTypeMap["tocmap"] = "index";

	inSequence=0;
	inStructure=0;

}
#
# part detection
#
{
	if ($1 == "ID_Inventar:") {id=$2;} 
}
{
	if ( index($0, "STRUCTURE") ) {
		inStructure=1;
		inSequence=0;
	}
} 
{
	if ( index($0, "SEQUENCE")) {
		inStructure=0; 
		inSequence=1;
	}
}
#
# parsing
# 
{
	if (inStructure==1 && ! match($0,"^--.*") && length($2) > 0 ) { 

		splitPagesRange($2);

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
		
		# avoid empty chapter lines
		gsub("\s", "", chapterLine);
		if(chapterLine != ""){	
			chapters[fromPage] = chapterLine;
			eocPage = toPage + 1;
			chapters[eocPage] = "[EOC]"; # end of Chapter, will potentially be overwritten by next chapter start
		}
		
		pageType = $1;
		if(pageTypeMap[pageType]) {
			pageType = pageTypeMap[pageType];
		}
		pageType = toupper(pageType);
		# one image filename per line
		for(p=fromPage; p<=toPage; p++) {
			#print id "_" p  "_" $1 >> "._filenames";
			paddedp = sprintf("%08d", p);
			print paddedp;
			filenames[p] = id "_" paddedp  "_" pageType
		}

	}
}
{	
	if (inSequence==1 && ! match($0,"^--.*") && length($1) > 0 ) { 

		# parse page sequence number to printed number information
		splitPagesRange($1);
		indexFrom = fromPage;
		indexTo= toPage;

		#print $1, indexFrom, indexTo;
		
		if($3) {
			splitPagesRange($3);
			printedfromPage = fromPage;
			#print "   printed: ", $3, printedfromPage;
		} else {
			printedfromPage = "";
		}

		if(printedfromPage != "") {	
			currentIndex = indexFrom;
			i = 0;
			while(currentIndex <= indexTo){
				print currentIndex, printedfromPage + i;
				printedPage[currentIndex] = printedfromPage + i;
				# increase counter
				i++;
				currentIndex = indexFrom + i;
			}
		}


	}
}
#
# processing
#
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

		# handle chapters: create sub folder, print title file, etc
		if(chapters[p]){ # [EOC] = end of Chapter
			if(chapters[p] == "[EOC]"){
				print "CHAPTER: " p ": " chapters[p];
				outDir = targetFolder;
			} else {
				if(currentChapter != chapters[p]){	
					chapterDir = id "_" p;
					print "CHAPTER: " p ": " chapters[p];
					system("mkdir " targetFolder "/" chapterDir);
					print chapters[p] > targetFolder "/" chapterDir "/chapter-title.txt"; 
				}
				outDir = targetFolder "/" chapterDir;
			}
		}

		# process image files
		if(filenames[p]){
			if(printedPage[p] != ""){
				newFilename = filenames[p] "_" printedPage[p] ".tif";
			} else {
				newFilename = filenames[p] ".tif";
			}
			
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