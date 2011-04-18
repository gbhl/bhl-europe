// 
// This file shows the minimum you need to provide to BookReader to display a book
//
// Copyright(c)2008-2009 Internet Archive. Software license AGPL version 3.

// Getting parameters

var pid = getUrlVars("pid");
var countPages = null;
var query = 'select $object from <#ri> ';
query += 'where ($object <fedora-model:hasModel> <fedora:ilives:pageCModel> and $object <fedora-rels-ext:isMemberOf>';
query += ' <fedora:' + pid + '>) order by $object';
var riUrl = '/fedora/risearch';
var options = {
          type: 'tuples',
          lang: 'itql',
          format: 'count',
		  dt: 'on',
          query: this.query
      };
$.ajaxSetup({async:false});
jQuery.post('/fedora/risearch', options, 
	function(data, status) {
      if ('success' == status) {	
      	countPages = parseInt(data);
		$.ajaxSetup({async:true});
      }
	}, 'text');



function getUrlVars(index) {
 var vars = [], hash;
 var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
 
 for(var i = 0; i < hashes.length; i++) {
   hash = hashes[i].split('=');
   vars.push(hash[0]);
   if (hash[0] == index) {
	return hash[1];
   }
   vars[hash[0]] = hash[1];
 }
 return vars;
}

// Create the BookReader object
br = new BookReader();

// Return the width of a given page.  Here we assume all images are 800 pixels wide
br.getPageWidth = function(index) {
    return 520;
}

// Return the height of a given page.  Here we assume all images are 1200 pixels high
br.getPageHeight = function(index) {
    return 780;
}

// We load the images from archive.org -- you can modify this function to retrieve images
// using a different URL structure
br.getPageURI = function(index, reduce, rotate) {
    // reduce and rotate are ignored in this simple implementation, but we
    // could e.g. look at reduce and load images from a different directory
    // or pass the information to an image server
    var leafStr = '000';            
    var imgStr = (index+1).toString();
    var re = new RegExp("0{"+imgStr.length+"}$");
    var url = '/fedora/objects/' + pid + '-'+ leafStr.replace(re, imgStr) + '/datastreams/JP2/content';
    var url = '/adore-djatoka/resolver?url_ver=Z39.88-2004&rft_id=http://localhost/fedora/objects/' + pid + '-' + leafStr.replace(re, imgStr) + '/datastreams/JP2/content&svc_id=info:lanl-repo/svc/getRegion&svc_val_fmt=info:ofi/fmt:kev:mtx:jpeg2000&svc.format=image/jpeg&svc.level=3&svc.rotate=0&svc.region=0,0,680,520'
    return url;
}

// Return which side, left or right, that a given page should be displayed on
br.getPageSide = function(index) {
    if (0 == (index & 0x1)) {
        return 'R';
    } else {
        return 'L';
    }
}

// This function returns the left and right indices for the user-visible
// spread that contains the given index.  The return values may be
// null if there is no facing page or the index is invalid.
br.getSpreadIndices = function(pindex) {   
    var spreadIndices = [null, null]; 
    if ('rl' == this.pageProgression) {
        // Right to Left
        if (this.getPageSide(pindex) == 'R') {
            spreadIndices[1] = pindex;
            spreadIndices[0] = pindex + 1;
        } else {
            // Given index was LHS
            spreadIndices[0] = pindex;
            spreadIndices[1] = pindex - 1;
        }
    } else {
        // Left to right
        if (this.getPageSide(pindex) == 'L') {
            spreadIndices[0] = pindex;
            spreadIndices[1] = pindex + 1;
        } else {
            // Given index was RHS
            spreadIndices[1] = pindex;
            spreadIndices[0] = pindex - 1;
        }
    }
    
    return spreadIndices;
}

// For a given "accessible page index" return the page number in the book.
//
// For example, index 5 might correspond to "Page 1" if there is front matter such
// as a title page and table of contents.
br.getPageNum = function(index) {
    return index+1;
}

// Total number of leafs
br.numLeafs = countPages;

// Book title and the URL used for the book title link
br.bookTitle= pid.split(':')[1] + ' on BHL Europe';
br.bookUrl  = 'http://openlibrary.org';

// Override the path used to find UI images
br.imagesBaseURL = '/reader/images/';

br.getEmbedCode = function(frameWidth, frameHeight, viewParams) {
    return "Embed code not supported in bookreader demo.";
}

// Let's go!
$(document).ready(function () {
	br.init();
});

// read-aloud and search need backend compenents and are not supported in the demo
$('#BRtoolbar').find('.read').hide();
$('#textSrch').hide();
$('#btnSrch').hide();
