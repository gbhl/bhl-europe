--- INTRODUCTION ---

To download the articles of the journal "Acta Palaeontologica Polonica" from PAS, use a dump of the metadata database provided by PAS (on request). First, create a bash script that fetches PDF files from a web server, generates metadata files with the corresponding information, and puts everything in a directory stucture. Then, run that script.

First part: the template "download-script-template.sh" already contains the necessary functions, but needs to be filled with the lines that contain web address and metadata information. Creating these lines is described here using a spreadsheet application like OpenOffice.org Calc, MS Excel, etc. (but could also be translated to SQL statements). Find a step-by-step description below.

Second part: copy the final download script to a GNU/linux environment (it requires bash, wget and some other basic commands) and run it (without further parameters) from that directory where the downloads should be stored.


--- STEPS TO DO ---


FROM THE DATABASE

- export table tl_issues_archive from the mysql database provided by PAS to a CSV file *
- import CSV file to your favourite spreadsheet application, e.g. OpenOffice.org Calc, MS Excel, etc. (and name it tl_issues_archive.*). The table should have 23 columns and hence occupy columns A through W.

- export table tl_issues from the mysql database provided by PAS to a CSV file *
- see [Note 1] below
- import CSV file to your favourite spreadsheet application, e.g. OpenOffice.org Calc, MS Excel, etc. (and name it tl_issues.*). The table should have 26 columns and hence occupy columns A through Z.

*) if you use PHPmyAdmin (and later OpenOffice.org Calc, MS Excel, etc.), then choose Fields enclosed by: " and Fields escaped by: " when exporting.


IN YOUR SPREADSHEET tl_issues_archive.*:

- sort table by "id"

- create a new column named "command" in column X and put the following command in cell X2:
	=A2 & ") issue=""" & D2 & """" &IF(F2<>"";"; issue_subtitle=""" & F2 & """";"") & ";;"
- copy that content by pulling down from cell X2 to the last row of the table

- copy that column's content into the function issueinformation() quite at the beginning of "download-script-template.sh"


IN YOUR SPREADSHEET tl_issues.*:

- sort table by "pid" ascending, then by "pagefirst" ascending

- delete all values "NULL" using "find & replace" (if not already done so on CSV export from the mysql database):
	search for "NULL",
	replace with "",
	check "Match case" and "Entire Cells" and
	replace all.

- find & replace <em>, </em>, <p>, </p>, <div>, </div>, <br /> by nothing [Note 1]
- find & replace "&nbsp;" by " "
- find & replace mail addresses using regular expressions [Note 2]
	search for: ^([^<]*)<a href=""mailto:[^>]*"">([^<]*)</a>
	replace with: $1$2
	more options: check "regular expressions"
	hit "Replace all" many times until you get "Search key not found."
- find & replace superscripts and subscripts using regular expressions [Note 3]
	in each step check "regular expressions" under "more options" and then
	hit "Replace all" many times until you get "Search key not found." (c.f. [Note 2])
	-- 1-letter superscripts:
		search for: ^([^<]*)<sup>(.)</sup>
		replace with: $1^$2
	-- all other superscripts:
		search for: ^([^<]*)<sup>([^<]*)</sup>
		replace with: $1^{$2}
	-- 1-letter subscripts:
		search for: ^([^<]*)<sub>(.)</sub>
		replace with: $1_$2
	-- all other subscripts:
		search for: ^([^<]*)<sub>([^<]*)</sub>
		replace with: $1_{$2}
- search for further occurances of "<" and replace them manually

- check if the mistakes described in the "database documentation" section below still remain and should be corrected

- copy column "enclosure" to a new column named "volume" in column AA
- apply "Text to columns" (Menu "Data" -> "Text to columns...") to entire column AA using
	"Fixed width":
	split "archive/published/app01/app01-001.pdf"
	into: "archive/published/app" | "01" | "/app01-001.pdf"
	and choose "Column type: hide" for the first and last column. So, just the volume number should remain

- create a new column named "command" in column AB and put the following command in cell AB2:
	=IF(B2<>B1;IF(AA2<>AA1;"nextvolume "&AA2&"; ";"") & "nextissue "&AA2&" "&B2&"; ";"")  & "nextarticle " & AA2 & " " & B2 & " " & N2 & " " & O2 & " """ & D2 & """ """ & I2 & """ """ & J2 & """ """ & L2 & """ """ & M2 & """ """ & S2 & """ """ & Z2 & """ """ & U2 & """ """ & P2 & """ """ & E2 & """ """ & K2 & """"
- copy that content by pulling down from cell AB2 to the last row of the table

- copy that column's content to the end of "download-script-template.sh"


[Note 1]: Removing HTML tags can be done either using "find & replace" in your spreadsheet application or already after exporting the CSV file from the database using command line tools like "sed".

[Note 2]: This replaces [<a href=""mailto:name@example.com"">name@example.com</a>] by just [name@example.com], but only the first occurance in each cell. You can try to replace <a href=""mailto:[^>]*"">([^>]*)</a> with $1 instead (which should replace all those mail links in one step) or even replace <([A-Za-z]+)[^>]*>([^<]*)</(\1)> with $2 (which should replace all HTML tags in one step), but I experienced problems with the replacement when there were more than one occurance in a cell.

[Note 3]: This replaces "10<sup>4</sup>" with "10^4" and "Ca<sup>2+</sup>" with "Ca^{2+}" and so on (using LaTeX notation for superscripts and subscripts).


--- DATABASE DOCUMENTATION ---

The fields tl_issues.pid and tl_issues_archive.id are the (continuous) number of the issue in the database; see also the "issue=" parameter in the links to the issues on http://www.app.pan.pl/archives.html .

The field tl_issues.enclosure is the relative web address of the PDF file on http://www.app.pan.pl/ .

There were some mistakes in the database dump of April 2012. Check if they have been corrected by PAS, or otherwise correct them again in your new database dump:
In the columns "pagefirst" and "pagelast", there were either zeros instead of the page numbers or the "pagefirst" entry didn't match with the first-page part of the PDF file name. This happened in the rows with the following "id"s:
843, 874 (cf. 875), 982, 1041, 1169, 1247, 1340, 1399, and 1489
Further, "enclosure" should be "archive/published/app50/app50-208.pdf" in line id=314 and "enclosure" should be "archive/published/app50/app50-364.pdf" in line id=327.
