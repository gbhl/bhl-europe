#!/bin/bash -e

#######################################################################
#
# Template for a download script to fetch PDF files from www.app.pan.pl
# See explanations in README.txt
#  
#######################################################################

# Global metadata for the entire journal
journal_publisher='Polska Akademia Nauk, Instytut Paleobiologii'
journal_title='Acta Palaeontologica Polonica'
journal_publicationplace='Warszawa, Poland'  # Place of publication
journal_publicationplacecode='PL'            # Place of publication (iso3166)
journal_issn='0567-7920'                     # ISSN if available
journal_publicationstart='1956'              # Start of publication
journal_language='English'                   # Language of publication
journal_languagecode='eng'                   # Language of publication (iso639-2b)
journal_website='http://www.app.pan.pl/'     # Base/domain for use with $enclosure

#
# issueinformation() sets variables issue and issue_subtitle
#
# expetcs one parameter:
#     $1: issue id (field tl_issues_archive.id or tl_issues.pid in the
#         database)
# 
# The information is taken from the table tl_issues_archive of the
# database dump. See README.txt.
#
issueinformation() {

	if [ -z "$1" ];	then
		echo "ERROR: Missing parameter (issue id) in function issueinformation()."
		exit
	fi
	
	issue_subtitle=""
	case $1 in
		####
		#### INSERT CONTENTS OF THE "command" COLUMN OF tl_issues_archive.* HERE
		####
	esac
}

#
# nextvolume() creates directory and metadata file for the next volume
#
# expetcs one parameter:
#     $1: volume number
#
nextvolume() {

	if [ -z "$1" ];	then
		echo "ERROR: Missing parameter (volume number) in function nextvolume()."
		exit
	fi
	
	volume=$1; volume2d=`printf '%02d' $1`  # volume2d has always 2 digits, i.e. has a leading zero if necessary
	year=$((1955+$volume))
	
	volume_directory="app$volume2d"
	volume_metadatafile="$volume_directory/olef.xml"
	
	echo
	echo "CREATING VOLUME $volume ($year) (in directory ./$volume_directory)"
	
	# create directory
	mkdir $volume_directory
	
	#
	# create metadata file for volume
	#
	
	# OLEF header
	echo "<?xml version=\"1.0\" encoding=\"utf-8\"?>" >$volume_metadatafile
	echo "<olef:olef xmlns:olef=\"http://www.bhl-europe.eu/bhl-schema/v0.3/\"><olef:element>" >>$volume_metadatafile
	echo "<olef:bibliographicInformation>" >>$volume_metadatafile
	
	# MODS: title is Volume XX (YYYY)
	echo "    <mods:titleInfo xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$volume_metadatafile
	echo "        <mods:title>Volume $volume ($year)</mods:title>" >>$volume_metadatafile
	echo "    </mods:titleInfo>" >>$volume_metadatafile
	
	# MODS: publisher
	echo "    <mods:name type=\"corporate\" xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$volume_metadatafile
	echo "        <mods:namePart>$journal_publisher</mods:namePart>" >>$volume_metadatafile
	echo "        <mods:role>" >>$volume_metadatafile
	echo "            <mods:roleTerm type=\"code\" authority=\"marcrelator\">pbl</mods:roleTerm>" >>$volume_metadatafile
	echo "            <mods:roleTerm type=\"text\" lang=\"eng\" authority=\"marcrelator\">Publisher</mods:roleTerm>" >>$volume_metadatafile
	echo "        </mods:role>" >>$volume_metadatafile
	echo "    </mods:name>" >>$volume_metadatafile
	
	# MODS: typeOfResource is text
	echo "    <mods:typeOfResource xmlns:mods=\"http://www.loc.gov/mods/v3\">text</mods:typeOfResource>" >>$volume_metadatafile
	
	# MODS: genre is journal
	echo "    <mods:genre authority=\"marcgt\" xmlns:mods=\"http://www.loc.gov/mods/v3\">journal</mods:genre>" >>$volume_metadatafile
	
	# MODS: publisher, place of publication, date (year only), frequency
	echo "    <mods:originInfo xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$volume_metadatafile
	echo "        <mods:place>" >>$volume_metadatafile
	echo "            <mods:placeTerm type=\"text\" lang=\"eng\">$journal_publicationplace</mods:placeTerm>" >>$volume_metadatafile
	echo "            <mods:placeTerm type=\"code\" authority=\"iso3166\">$journal_publicationplacecode</mods:placeTerm>" >>$volume_metadatafile
	echo "        </mods:place>" >>$volume_metadatafile
	echo "        <mods:publisher>$journal_publisher</mods:publisher>" >>$volume_metadatafile
	echo "        <mods:dateIssued encoding=\"w3cdtf\" keyDate=\"yes\">$year</mods:dateIssued>" >>$volume_metadatafile
	echo "        <mods:issuance>serial</mods:issuance>" >>$volume_metadatafile  ## see http://www.loc.gov/standards/mods/v3/modsjournal.xml and <mods:relatedItem> tag below
	echo "        <mods:frequency authority=\"marcfrequency\">Annual</mods:frequency>" >>$volume_metadatafile
	echo "    </mods:originInfo>" >>$volume_metadatafile
	
	# MODS: language
	echo "    <mods:language xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$volume_metadatafile
	echo "        <mods:languageTerm type=\"code\" authority=\"iso639-2b\">$journal_languagecode</mods:languageTerm>" >>$volume_metadatafile
	echo "        <mods:languageTerm type=\"text\" lang=\"eng\">$journal_language</mods:languageTerm>" >>$volume_metadatafile
	echo "    </mods:language>" >>$volume_metadatafile
	
	# MODS: born digital in PDF format (although older volumes aren't born digitals)
	echo "    <mods:physicalDescription xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$volume_metadatafile
	echo "        <mods:internetMediaType>application/pdf</mods:internetMediaType>" >>$volume_metadatafile
	echo "        <mods:digitalOrigin>born digital</mods:digitalOrigin>" >>$volume_metadatafile
	echo "    </mods:physicalDescription>" >>$volume_metadatafile
	
	# MODS: journal name and ISSN
	echo "    <mods:relatedItem type=\"host\" xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$volume_metadatafile
	echo "        <mods:titleInfo>" >>$volume_metadatafile
	echo "            <mods:title>$journal_title</mods:title>" >>$volume_metadatafile
	echo "        </mods:titleInfo>" >>$volume_metadatafile
	echo "        <mods:originInfo>" >>$volume_metadatafile
	echo "            <mods:issuance>serial</mods:issuance>" >>$volume_metadatafile
	echo "        </mods:originInfo>" >>$volume_metadatafile
	echo "        <mods:identifier type=\"issn\" xmlns:mods=\"http://www.loc.gov/mods/v3\">$journal_issn</mods:identifier>" >>$volume_metadatafile  ## http://www.loc.gov/standards/sourcelist/standard-identifier.html
	echo "    </mods:relatedItem>" >>$volume_metadatafile
	
	# MODS: URL of journal overview page
	echo "    <mods:location xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$volume_metadatafile
	echo "        <mods:url access=\"object in context\" usage=\"primary display\">${journal_website}archives.html</mods:url>" >>$volume_metadatafile
	echo "    </mods:location>" >>$volume_metadatafile
	
	# MODS: 
	echo "    <mods:accessCondition type=\"use and reproduction\" xmlns:mods=\"http://www.loc.gov/mods/v3\">http://www.europeana.eu/rights/rr-f/</mods:accessCondition>" >>$volume_metadatafile
	# echo "    <mods:rights xmlns:mods=\"http://www.loc.gov/mods/v3\">http://creativecommons.org/licenses/by-nc-sa/3.0/</mods:rights>" >>$volume_metadatafile
	
	# MODS: information about this digital record
	echo "    <mods:recordInfo xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$volume_metadatafile
	echo "        <mods:recordCreationDate encoding=\"w3cdtf\">$(date +'%Y-%m-%d')</mods:recordCreationDate>" >>$volume_metadatafile
	echo "        <mods:recordChangeDate encoding=\"w3cdtf\">$(date +'%Y-%m-%d')</mods:recordChangeDate>" >>$volume_metadatafile
	echo "        <mods:recordContentSource>ADMIN</mods:recordContentSource>" >>$volume_metadatafile
	echo "        <mods:languageOfCataloging>" >>$volume_metadatafile
	echo "            <mods:languageTerm authority=\"iso639-2\">eng</mods:languageTerm>" >>$volume_metadatafile
	echo "        <mods:languageOfCataloging>" >>$volume_metadatafile
	echo "    </mods:recordInfo>" >>$volume_metadatafile
	
	# OLEF footer
	echo "</olef:bibliographicInformation>" >>$volume_metadatafile
	echo "<olef:level>volume</olef:level>" >>$volume_metadatafile
	echo "</olef:element></olef:olef>" >>$volume_metadatafile
}

#
# nextissue() creates directory and metadata file for the next issue
#
# expetcs two parameters:
#     $1: volume number
#     $2: issue id (field tl_issues_archive.id or tl_issues.pid in the
#         database)
#
nextissue() {

	if [ -z "$1" ]; then
		echo "ERROR: Missing 1st parameter (volume number) in function nextissue()."
		exit
	fi
	if [ -z "$2" ]; then
		echo "ERROR: Missing 2nd parameter (issue id) in function nextissue()."
		exit
	fi

	volume=$1; volume2d=`printf '%02d' $1`  # volume2d has always 2 digits, i.e. has a leading zero if necessary
	issueid=$2
	
	issueinformation $issueid  # calls issueinformation() to set issue and issue_subtitle
	issue_directory="$volume_directory"

	echo "CREATING ISSUE $volume($issue) (in directory ./$issue_directory)"
}

#
# nextarticle() creates directory and metadata file for the next article
#               and downloads it
#
# expetcs many parameters, see variable assignment list below
#
nextarticle() {

	if [ $# -lt 10 ]; then  # the first parameters up to "enclosure" are required
		echo "ERROR: not enough parameters in function nextarticle()."
		exit
	fi
	
	# variable assignment
	volume=$1; volume2d=`printf '%02d' $volume`  # volume2d has always 2 digits, i.e. has a leading zero if necessary
	issueid=$2
	pagefirst=$3; pagefirst4d=`printf '%04d' $pagefirst`  # pagefirst4d has always 4 digits, i.e. has leading zeros if necessary
	pagelast=$4
	headline=$5
	authors=$6
	abstract=$7
	keywords=$8
	address=$9
	enclosure=${10}
	doi=${11}
	supplement=${12}
	itemtype=${13}; itemtype1=`echo $itemtype | sed 's/ //g'`  # itemtype1 doesn't contain spaces
	headlinepl=${14}
	abstractpl=${15}
	
	issueinformation $issueid  # calls issueinformation() to set issue
	article_directory="$issue_directory/app${volume2d}_$pagefirst4d"
	article_metadatafile="$article_directory/olef.xml"
	
	# create directory
	mkdir $article_directory
	
	# download article
	wget --no-verbose --output-document="$article_directory/article.pdf" http://www.app.pan.pl/$enclosure
	
	#
	# create metadata file for article
	#
	
	# OLEF header
	echo "<?xml version=\"1.0\" encoding=\"utf-8\"?>" >$article_metadatafile
	echo "<olef:olef xmlns:olef=\"http://www.bhl-europe.eu/bhl-schema/v0.3/\"><olef:element>" >>$article_metadatafile
	echo "<olef:bibliographicInformation>" >>$article_metadatafile
	
	# MODS: title
	echo "    <mods:titleInfo lang=\"eng\" xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
	echo "        <mods:title>$headline</mods:title>" >>$article_metadatafile
	echo "    </mods:titleInfo>" >>$article_metadatafile
	if [ -n "$headlinepl" ]; then
		echo "    <mods:titleInfo type=\"translated\" lang=\"pol\" xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
		echo "        <mods:title>$headlinepl</mods:title>" >>$article_metadatafile
		echo "    </mods:titleInfo>" >>$article_metadatafile
	fi
	
	# MODS: list of authors
	if [ -n "$authors" ]; then
		authorslist=`echo $authors | sed 's/, and /,/g' | sed 's/ and /,/g' | sed 's/, /,/g'`  # authorslist is strictly comma-seperated (no "and", no space)
		IFS_default=$IFS  # backup $IFS
		IFS=','  # change $IFS to our needs
		for singleauthor in $authorslist; do  # split $authorslist at commas ($IFS)
			echo "    <mods:name type=\"personal\" xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
			echo "        <mods:namePart>$singleauthor</mods:namePart>" >>$article_metadatafile
			echo "        <mods:role>" >>$article_metadatafile
			echo "            <mods:roleTerm type=\"code\" authority=\"marcrelator\">aut</mods:roleTerm>" >>$article_metadatafile
			echo "            <mods:roleTerm type=\"text\" lang=\"eng\" authority=\"marcrelator\">Author</mods:roleTerm>" >>$article_metadatafile
			echo "        </mods:role>" >>$article_metadatafile
			echo "    </mods:name>" >>$article_metadatafile
		done
		IFS=$IFS_default  # restore $IFS from backup
	fi
	
	# MODS: typeOfResource is text
	echo "    <mods:typeOfResource xmlns:mods=\"http://www.loc.gov/mods/v3\">text</mods:typeOfResource>" >>$article_metadatafile
	
	# MODS: genre / item type
	echo "    <mods:genre xmlns:mods=\"http://www.loc.gov/mods/v3\">$itemtype</mods:genre>" >>$article_metadatafile  ## http://www.loc.gov/standards/valuelist/marcgt.html for authority="marcgt"
	
	# MODS: publisher, place of publication, date (year only)
	echo "    <mods:originInfo xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
	echo "        <mods:place>" >>$article_metadatafile
	echo "            <mods:placeTerm type=\"text\" lang=\"eng\">$journal_publicationplace</mods:placeTerm>" >>$article_metadatafile
	echo "            <mods:placeTerm type=\"code\" authority=\"iso3166\">$journal_publicationplacecode</mods:placeTerm>" >>$article_metadatafile
	echo "        </mods:place>" >>$article_metadatafile
	echo "        <mods:publisher>$journal_publisher</mods:publisher>" >>$article_metadatafile
	echo "        <mods:dateIssued encoding=\"w3cdtf\" keyDate=\"yes\">$year</mods:dateIssued>" >>$article_metadatafile
	echo "        <mods:issuance>monographic</mods:issuance>" >>$article_metadatafile  ## see http://www.loc.gov/standards/mods/v3/modsjournal.xml and <mods:relatedItem> tag below
	echo "    </mods:originInfo>" >>$article_metadatafile
	
	# MODS: language
	echo "    <mods:language xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
	echo "        <mods:languageTerm type=\"code\" authority=\"iso639-2b\">$journal_languagecode</mods:languageTerm>" >>$article_metadatafile
	echo "        <mods:languageTerm type=\"text\" lang=\"eng\">$journal_language</mods:languageTerm>" >>$article_metadatafile
	echo "    </mods:language>" >>$article_metadatafile
	
	# MODS: authors' addresses in <mods:note>
	if [ -n "$address" ]; then
		echo "    <mods:note displayLabel=\"Address\" xmlns:mods=\"http://www.loc.gov/mods/v3\">$address</mods:note>" >>$article_metadatafile
	fi
	
	# MODS: born digital in PDF format (although older volumes aren't born digitals)
	echo "    <mods:physicalDescription xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
	echo "        <mods:internetMediaType>application/pdf</mods:internetMediaType>" >>$article_metadatafile
	echo "        <mods:digitalOrigin>born digital</mods:digitalOrigin>" >>$article_metadatafile
	echo "    </mods:physicalDescription>" >>$article_metadatafile
	
	# MODS: abstract
	if [ -n "$abstract" ]; then
		echo "    <mods:abstract lang=\"eng\" xmlns:mods=\"http://www.loc.gov/mods/v3\">$abstract</mods:abstract>" >>$article_metadatafile
	fi
	if [ -n "$abstractpl" ]; then
		echo "    <mods:abstract lang=\"pol\" xmlns:mods=\"http://www.loc.gov/mods/v3\">$abstractpl</mods:abstract>" >>$article_metadatafile
	fi
	
	# MODS: article keywords
	if [ -n "$keywords" ]; then
		echo "    <mods:subject xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
		echo "        <mods:topic>$keywords</mods:topic>" >>$article_metadatafile
		echo "    </mods:subject>" >>$article_metadatafile
	fi
	
	# MODS: journal name, volume and issue number, and page range in <mods:relatedItem type="host">, see example: http://www.loc.gov/standards/mods/v3/modsjournal.xml
	echo "    <mods:relatedItem type=\"host\" xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
	echo "        <mods:titleInfo>" >>$article_metadatafile
	echo "            <mods:title>$journal_title</mods:title>" >>$article_metadatafile
	echo "        </mods:titleInfo>" >>$article_metadatafile
	echo "        <mods:originInfo>" >>$article_metadatafile
	echo "            <mods:issuance>serial</mods:issuance>" >>$article_metadatafile
	echo "        </mods:originInfo>" >>$article_metadatafile
	echo "        <mods:identifier type=\"issn\" xmlns:mods=\"http://www.loc.gov/mods/v3\">$journal_issn</mods:identifier>" >>$article_metadatafile  ## http://www.loc.gov/standards/sourcelist/standard-identifier.html
	echo "        <mods:part>" >>$article_metadatafile
	echo "            <mods:detail type=\"volume\">" >>$article_metadatafile
	echo "                <mods:number>$volume</mods:number>" >>$article_metadatafile
	echo "            </mods:detail>" >>$article_metadatafile
	echo "            <mods:detail type=\"issue\">" >>$article_metadatafile
	echo "                <mods:number>$issue</mods:number>" >>$article_metadatafile
	if [ -n "$issue_subtitle" ]; then
		echo "                <mods:title>$issue_subtitle<mods:title>" >>$article_metadatafile
	fi
	echo "            </mods:detail>" >>$article_metadatafile
	echo "            <mods:extent unit=\"pages\">" >>$article_metadatafile
	echo "                <mods:start>$pagefirst</mods:start>" >>$article_metadatafile
	echo "                <mods:end>$pagelast</mods:end>" >>$article_metadatafile
	echo "            </mods:extent>" >>$article_metadatafile
	echo "            <mods:date>$year</mods:date>" >>$article_metadatafile
	echo "        </mods:part>" >>$article_metadatafile
	echo "    </mods:relatedItem>" >>$article_metadatafile
	
	# MODS: DOI
	if [ -n "$doi" ]; then
		echo "    <mods:identifier type=\"doi\" xmlns:mods=\"http://www.loc.gov/mods/v3\">doi:$doi</mods:identifier>" >>$article_metadatafile
	fi
	
	# MODS: original URL of article PDF file
	echo "    <mods:location xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
	echo "        <mods:url access=\"raw object\" usage=\"primary display\">$journal_website$enclosure</mods:url>" >>$article_metadatafile
	echo "    </mods:location>" >>$article_metadatafile
	
	# MODS: 
	echo "    <mods:accessCondition type=\"use and reproduction\" xmlns:mods=\"http://www.loc.gov/mods/v3\">http://www.europeana.eu/rights/rr-f/</mods:accessCondition>" >>$article_metadatafile
	# echo "    <mods:rights xmlns:mods=\"http://www.loc.gov/mods/v3\">http://creativecommons.org/licenses/by-nc-sa/3.0/</mods:rights>" >>$article_metadatafile
	
	# MODS: information about this digital record
	echo "    <mods:recordInfo xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$article_metadatafile
	echo "        <mods:recordCreationDate encoding=\"w3cdtf\">$(date +'%Y-%m-%d')</mods:recordCreationDate>" >>$article_metadatafile
	echo "        <mods:recordChangeDate encoding=\"w3cdtf\">$(date +'%Y-%m-%d')</mods:recordChangeDate>" >>$article_metadatafile
	echo "        <mods:recordContentSource>ADMIN</mods:recordContentSource>" >>$article_metadatafile
	echo "        <mods:recordIdentifier>$journal_website$enclosure</mods:recordIdentifier>" >>$article_metadatafile
	echo "        <mods:languageOfCataloging>" >>$article_metadatafile
	echo "            <mods:languageTerm authority=\"iso639-2\">eng</mods:languageTerm>" >>$article_metadatafile
	echo "        <mods:languageOfCataloging>" >>$article_metadatafile
	echo "    </mods:recordInfo>" >>$article_metadatafile
	
	# OLEF footer
	echo "</olef:bibliographicInformation>" >>$article_metadatafile
	echo "<olef:level>article</olef:level>" >>$article_metadatafile
	echo "</olef:element></olef:olef>" >>$article_metadatafile
}


#
# "main program"
#

journal_metadatafile=olef.xml

if [ ! -f "$journal_metadatafile" ]; then
	
	#
	# create metadata file for serial (journal)
	#
	
	# OLEF header
	echo "<?xml version=\"1.0\" encoding=\"utf-8\"?>" >$journal_metadatafile
	echo "<olef:olef xmlns:olef=\"http://www.bhl-europe.eu/bhl-schema/v0.3/\"><olef:element>" >>$journal_metadatafile
	echo "<olef:bibliographicInformation>" >>$journal_metadatafile
	
	# MODS: journal title
	echo "    <mods:titleInfo xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$journal_metadatafile
	echo "        <mods:title>$journal_title</mods:title>" >>$journal_metadatafile
	echo "    </mods:titleInfo>" >>$journal_metadatafile
	
	# MODS: publisher
	echo "    <mods:name type=\"corporate\" xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$journal_metadatafile
	echo "        <mods:namePart>$journal_publisher</mods:namePart>" >>$journal_metadatafile
	echo "        <mods:role>" >>$journal_metadatafile
	echo "            <mods:roleTerm type=\"code\" authority=\"marcrelator\">pbl</mods:roleTerm>" >>$journal_metadatafile
	echo "            <mods:roleTerm type=\"text\" lang=\"eng\" authority=\"marcrelator\">Publisher</mods:roleTerm>" >>$journal_metadatafile
	echo "        </mods:role>" >>$journal_metadatafile
	echo "    </mods:name>" >>$journal_metadatafile
	
	# MODS: typeOfResource is text
	echo "    <mods:typeOfResource xmlns:mods=\"http://www.loc.gov/mods/v3\">text</mods:typeOfResource>" >>$journal_metadatafile
	
	# MODS: genre is journal
	echo "    <mods:genre authority=\"marcgt\" xmlns:mods=\"http://www.loc.gov/mods/v3\">journal</mods:genre>" >>$journal_metadatafile
	
	# MODS: publisher, place of publication, date (year only), frequency
	echo "    <mods:originInfo xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$journal_metadatafile
	echo "        <mods:place>" >>$journal_metadatafile
	echo "            <mods:placeTerm type=\"text\" lang=\"eng\">$journal_publicationplace</mods:placeTerm>" >>$journal_metadatafile
	echo "            <mods:placeTerm type=\"code\" authority=\"iso3166\">$journal_publicationplacecode</mods:placeTerm>" >>$journal_metadatafile
	echo "        </mods:place>" >>$journal_metadatafile
	echo "        <mods:publisher>$journal_publisher</mods:publisher>" >>$journal_metadatafile
	echo "        <mods:dateIssued point=\"start\" encoding=\"marc\">1956</mods:dateIssued>" >>$journal_metadatafile
	echo "        <mods:dateIssued point=\"end\" encoding=\"marc\">9999</mods:dateIssued>" >>$journal_metadatafile
	echo "        <mods:issuance>serial</mods:issuance>" >>$journal_metadatafile  ## see http://www.loc.gov/standards/mods/v3/modsjournal.xml and <mods:relatedItem> tag below
	echo "        <mods:frequency authority=\"marcfrequency\">Annual</mods:frequency>" >>$journal_metadatafile
	echo "    </mods:originInfo>" >>$journal_metadatafile
	
	# MODS: language
	echo "    <mods:language xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$journal_metadatafile
	echo "        <mods:languageTerm type=\"code\" authority=\"iso639-2b\">$journal_languagecode</mods:languageTerm>" >>$journal_metadatafile
	echo "        <mods:languageTerm type=\"text\" lang=\"eng\">$journal_language</mods:languageTerm>" >>$journal_metadatafile
	echo "    </mods:language>" >>$journal_metadatafile
	
	# MODS: born digital in PDF format (although older volumes aren't born digitals)
	echo "    <mods:physicalDescription xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$journal_metadatafile
	echo "        <mods:internetMediaType>application/pdf</mods:internetMediaType>" >>$journal_metadatafile
	echo "        <mods:digitalOrigin>born digital</mods:digitalOrigin>" >>$journal_metadatafile
	echo "    </mods:physicalDescription>" >>$journal_metadatafile
	
	# MODS: URL of journal overview page
	echo "    <mods:location xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$journal_metadatafile
	echo "        <mods:url access=\"object in context\" usage=\"primary display\">${journal_website}archives.html</mods:url>" >>$journal_metadatafile
	echo "    </mods:location>" >>$journal_metadatafile
	
	# MODS: 
	echo "    <mods:accessCondition type=\"use and reproduction\" xmlns:mods=\"http://www.loc.gov/mods/v3\">http://www.europeana.eu/rights/rr-f/</mods:accessCondition>" >>$journal_metadatafile
	
	# MODS: information about this digital record
	echo "    <mods:recordInfo xmlns:mods=\"http://www.loc.gov/mods/v3\">" >>$journal_metadatafile
	echo "        <mods:recordCreationDate encoding=\"w3cdtf\">$(date +'%Y-%m-%d')</mods:recordCreationDate>" >>$journal_metadatafile
	echo "        <mods:recordChangeDate encoding=\"w3cdtf\">$(date +'%Y-%m-%d')</mods:recordChangeDate>" >>$journal_metadatafile
	echo "        <mods:recordContentSource>ADMIN</mods:recordContentSource>" >>$journal_metadatafile
	echo "        <mods:languageOfCataloging>" >>$journal_metadatafile
	echo "            <mods:languageTerm authority=\"iso639-2\">eng</mods:languageTerm>" >>$journal_metadatafile
	echo "        <mods:languageOfCataloging>" >>$journal_metadatafile
	echo "    </mods:recordInfo>" >>$journal_metadatafile
	
	# OLEF footer
	echo "</olef:bibliographicInformation>" >>$journal_metadatafile
	echo "<olef:level>serial</olef:level>" >>$journal_metadatafile
	echo "</olef:element></olef:olef>" >>$journal_metadatafile

fi


####
#### INSERT CONTENTS OF THE "command" COLUMN OF tl_issues.* HERE
####
