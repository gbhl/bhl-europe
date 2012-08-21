--- INTRODUCTION ---

To download the articles of the journal "Acta Palaeontologica Polonica" from PAS, use a dump of the metadata database provided by PAS (on request). First, create a bash script that
- fetches the article PDF files from a web server,
- takes the issue cover PDF files from a local directory,
- generates metadata files with the corresponding information, and
- puts everything in a directory stucture.
Then, run that script.

First part: the template "download-script-template.sh" already contains the necessary functions, but needs to be filled with the lines that contain web address and metadata information. Creating these lines is described here using a spreadsheet application like OpenOffice.org Calc, MS Excel, etc. (but could also be translated to SQL statements). Find a step-by-step description in the file "preparing-the-script.txt".

Second part: copy the final download script to a GNU/linux environment (it requires bash, wget and some other basic commands) and run it. There are no parameters required, but you can optionally use the first parameter to set the directory containing the issue covers (to override the default value set in the first lines of "download-script-template.sh"). The downloaded files are stored in a subdirectory ("app") of your current directory where you run your script from.


--- DATABASE DOCUMENTATION ---

The fields tl_issues.pid and tl_issues_archive.id are the (continuous) number of the issue in the database; see also the "issue=" parameter in the links to the issues on http://www.app.pan.pl/archives.html .

The field tl_issues.enclosure is the relative web address of the PDF file on http://www.app.pan.pl/ .
