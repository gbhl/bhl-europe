# awk script to create structure for  de-uber
#
#
#

{if (index($0, "STRUCTURE")) doPrint=1} 
{if (index($0, "SEQUENCE")) doPrint=0} 
{if ($1 == "ID_Inventar:") {id=$2; print id >> "._id"} }
{if (doPrint==1 && ! match($0,"^--.*") && length($2) > 0 ) { 

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
			isPageNumber = match($i,"[0-9]+\x2E.*")
			if( lastToken == $i || !isPageNumber ) {
				if(isPageNumber) {
					chapterLine = chapterLine $i;
 			} else {
 				chapterLine = chapterLine " " $i;
 			}
			}	
			lastToken = $i;
			i++;
		}
		print p, chapterLine >> "._chapters";
		
		# one image filename per line
		for(p=fromPage; p<=toPage; p++) {
		print id "_" p  "_" $1 >> "._filenames";
		}

	}
}