#!/bin/bash -e

#######################################################################
#
# Template for a download script to fetch PDF files from www.app.pan.pl
# See explanations in README.txt
#  
#######################################################################

#
# issueinformation() sets variables issue and issuesubtitle
# expetcs one parameter:
#     $1: issue id (field tl_issues_archive.id or tl_issues.pid in the
#         database)
# 
# The information is taken from the table tl_issues_archive of the
# database dump. See README.txt.
#
issueinformation() {

	case $1 in
		####
		#### INSERT CONTENTS OF THE "command" COLUMN OF tl_issues_archive.* HERE
		####
	esac
	# TODO year=1995 + $volume
}

#
# nextvolume() creates directory and metadata file for the next volume
# expetcs one parameter:
#     $1: volume number
#
nextvolume() {

	if [ -z "$1" ];	then
		echo "ERROR: Missing parameter volumenumber in function nextvolume."
		exit
	fi
	
	volume=$1; volume2d=`printf '%02d' $1`  # volume2d has always 2 digits, i.e. has a leading zero if necessary
	volumedirectory="app$volume2d"
	volumemetadatafile="$volumedirectory/metadata.txt"
	
	# create directory
	mkdir $volumedirectory
	
	# create metadata file
	# TODO create volume metadata
}

#
# nextissue() creates directory and metadata file for the next issue
# expetcs two parameters:
#     $1: volume number
#     $2: issue id (field tl_issues_archive.id or tl_issues.pid in the
#         database)
#
nextissue() {

	if [ -z "$1" ]; then
		echo "ERROR: Missing parameter volumenumber in function nextissue."
		exit
	fi

	volume=$1; volume2d=`printf '%02d' $1`  # volume2d has always 2 digits, i.e. has a leading zero if necessary
	issueid=$2
	issueinformation $issueid  # calls issueinformation() to set issue and issuesubtitle
	issuedirectory="app$volume2d/app${volume2d}issue$issue"
	issuemetadatafile="$issuedirectory/metadata.txt"

	# create directory
	mkdir $issuedirectory
	
	# create metadata file
	# TODO create issue metadata
	
	# TODO get frontpage of issue
}

#
# nextarticle() creates directory and metadata file for the next article
#               and downloads it
# expetcs many parameters, see variable assignment list below
# TODO check required parameters, error messages
#
nextarticle() {
	if [ -z "$1" ]; then  # use param count instead
		echo "ERROR: Missing parameter volumenumber in function nextarticle."
		exit
	fi
	
	volume=$1; volume2d=`printf '%02d' $volume`  # volume2d has always 2 digits, i.e. has a leading zero if necessary
	issueid=$2
	pagefirst=$3; pagefirst4d=`printf '%04d' $pagefirst`  # pagefirst4d has always 4 digits, i.e. has a leading zeros if necessary
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
	articledirectory="app$volume2d/app${volume2d}issue${issue}/app$volume2d-$pagefirst4d-$itemtype1"
	articlemetadatafile="$articledirectory/metadata.txt"
	
	# create directory
	mkdir $articledirectory
	# touch $articledirectory/app$volume2d-$pagefirst4d.pdf
	
	# download article
	wget --no-verbose --output-document="$articledirectory/article.pdf" http://www.app.pan.pl/$enclosure
	# --append-output=wgetlog.txt 
	# TODO add error handling
	
	# create metadata file
	# TODO create article metadata
	# TODO delete HTML tags in metadata
	# test:
	echo "volume: $volume" >>$articlemetadatafile
	echo "issue: $issue" >>$articlemetadatafile
	echo "pagefirst: $pagefirst" >>$articlemetadatafile
	echo "pagelast: $pagelast" >>$articlemetadatafile
	echo "headline: $headline" >>$articlemetadatafile
	echo "authors: $authors" >>$articlemetadatafile
	echo "abstract: $abstract" >>$articlemetadatafile
	echo "keywords: $keywords" >>$articlemetadatafile
	echo "address: $address" >>$articlemetadatafile
	echo "enclosure: $enclosure" >>$articlemetadatafile
	echo "doi: $doi" >>$articlemetadatafile
	echo "supplement: $supplement" >>$articlemetadatafile
	echo "itemtype: $itemtype" >>$articlemetadatafile
	echo "headlinepl: $headlinepl" >>$articlemetadatafile
	echo "abstractpl: $abstractpl" >>$articlemetadatafile
}

####
#### INSERT CONTENTS OF THE "command" COLUMN OF tl_issues.* HERE
####