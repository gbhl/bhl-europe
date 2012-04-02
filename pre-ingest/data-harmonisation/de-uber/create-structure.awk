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

function arrayKeyExists(arr, key) {
   return(key in arr)
}

function arrayGetValue(arr, key) {
	if (key in arr) {
   		return	arr[key];
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
	id="";

}
#
# part detection
#
{
	if ( id == "" && ( $1 == "ID_Inventar:" ||  $1 == "ID-Aleph:")  && $2 != "" ) {
		id=$2;
		gsub("[ \t\n\r]+$", "", id); #remove trailing whitespace
		gsub("[^a-zA-Z0-9_]", "-", id); 
		print id > targetFolder "/InternalIdentifier.txt"
	} 
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


		pageType = tolower($1);
		splitPagesRange($2);

		#
		# create ChapterMap
		#
		#
		if(pageType == "chapter") {
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
			if(!match(chapterLine, "^[ \t]*$")) {
				if(lastEOC < fromPage) {
					# end of Chapter, will potentially be overwritten by next chapter start	
					# the very last will EOC be set in the END section !!
					chapters[lastEOC] = "[EOC]";
				}
				chapters[fromPage] = chapterLine;
				lastEOC =  toPage + 1;
			}
		} else if(pageType == "section" || pageType == "subsection") {
			# extend the chapter to cover also the subsection
			lastEOC =  toPage + 1;
		} else {
			if(lastEOC > 0) {
				chapters[lastEOC] = "[EOC]";
				lastEOC = -1;
			}
		}

		#
		# create fileNameMap
		#
		if(pageTypeMap[pageType]) {
			pageType = pageTypeMap[pageType];
		}
		pageType = toupper(pageType);
		# one image filename per line
		for(p=fromPage; p<=toPage; p++) {
			#print id "_" p  "_" $1 >> "._filenames";
			paddedp = sprintf("%08d", p);
			# print paddedp;
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
	# set the EOC for the last chapter if it exists
	if(lastEOC > 0) {
		chapters[lastEOC] = "[EOC]";
		lastEOC = -1;
	}

	# start output in targetFolder
	outDir = targetFolder;

	# loop over scans
	dirList = "ls -1 " scansFolder;
 	while( dirList | getline > 0) {
 		# extract page number from tiff file
 		p=$0
 		sub("^.*_0", "", p); # remove everything up to the start of the page number
		sub("^0*", "", p); # remove all remaining leading zeros
		sub(".tif", "", p); # remove filename extension

		# handle chapters: create sub folder, print title file, etc
		if( arrayGetValue( chapters, p) ) { 
			if(chapters[p] == "[EOC]"){ # [EOC] = end of Chapter
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
		if( arrayGetValue(filenames, p) ){
			if(printedPage[p] != ""){
				newFilename = filenames[p] "_" printedPage[p] ".tif";
			} else {
				newFilename = filenames[p] ".tif";
			}
			
		} else {
			newFilename = $0;
		}
		print "cp " scansFolder "/" $0 " " outDir "/" newFilename;
		# copy scan to new folder and give it a new name
		system("cp " scansFolder "/" $0 " " outDir "/" newFilename);
	}
	#for (p in filenames) {
	#	print p ": " filenames[p];
	#}
	#for (p in chapters) {
	#	print("CHAPTER: " p ": " chapters[p]) > "chapters.txt";
	#}

	print "DONE"
}