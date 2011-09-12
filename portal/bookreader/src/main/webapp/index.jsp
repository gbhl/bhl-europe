<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
<head>
    <title>BHL Europe </title>
    <% 
        String basePath = "http://localhost:8080";
    %>
    <link rel="stylesheet" type="text/css" href="<%= basePath %>/bookreader/style/BookReader.css"/>
    <!-- Custom CSS overrides -->
    <link rel="stylesheet" type="text/css" href="<%= basePath %>/bookreader/style/BookReaderBHLE.css"/>

    <script type="text/javascript" src="http://www.archive.org/includes/jquery-1.4.2.min.js"></script>
    <script type="text/javascript" src="http://www.archive.org/bookreader/jquery-ui-1.8.5.custom.min.js"></script>

    <script type="text/javascript" src="http://www.archive.org/bookreader/dragscrollable.js"></script>
    <script type="text/javascript" src="http://www.archive.org/bookreader/jquery.colorbox-min.js"></script>
    <script type="text/javascript" src="http://www.archive.org/bookreader/jquery.ui.ipad.js"></script>
    <script type="text/javascript" src="http://www.archive.org/bookreader/jquery.bt.min.js"></script>

    <script type="text/javascript" src="<%= basePath %>/bookreader/script/BookReader.js"></script>
</head>
<body style="background-color: ##939598;">

<div id="BookReader">
    Internet Archive BookReader Demo    <br/>
    
    <noscript>
    <p>
        The BookReader requires JavaScript to be enabled. Please check that your browser supports JavaScript and that it is enabled in the browser settings.  You can also try one of the <a href="http://www.archive.org/details/goodytwoshoes00newyiala"> other formats of the book</a>.
    </p>
    </noscript>
</div>

<script type="text/javascript">
function getPageCount(pid){
	var query = 'select $object from <#ri> ';
	query += 'where ($object <fedora-model:hasModel> <fedora:ilives:pageCModel> and $object <fedora-rels-ext:isMemberOf>';
	query += ' <fedora:' + pid + '>) order by $object';

	var riUrl = '<%= basePath %>/fedora/risearch';
	var options = {
			  type: 'tuples',
			  lang: 'itql',
			  format: 'count',
			  dt: 'on',
			  query: query
		  };
	
	var pageCount = $.ajax({
      url: riUrl,
      type: "POST",
      data: options,
      async:false
   }
	).responseText;

	return parseInt(pageCount);
}

function getUrlVars(index) {
 var vars = [], hash;
 var url = window.location.href;
 
 if (url.indexOf('#') == -1 ){
	var hashes = url.slice(url.indexOf('?') + 1).split('&');
 } else {
	var hashes = url.slice(url.indexOf('?') + 1, url.indexOf('#')).split('&');
 }

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

br.basepath = '<%= basePath %>';

//Extract variable pid from URL
br.pid = '<%= request.getParameter("pid") %>';

// Extract variable pid from URL
br.ui = '<%= request.getParameter("ui") %>';

// Return the width of a given page.  Here we assume all images are 800 pixels wide
br.getPageWidth = function(index) {
    return 800;
}

// Return the height of a given page.  Here we assume all images are 1200 pixels high
br.getPageHeight = function(index) {
    return 1200;
}

//We load the images from archive.org -- you can modify this function to retrieve images
//using a different URL structure
br.getPageURI = function(index, reduce, rotate) {
	//var level = Math.floor(-2 * (reduce -6) / 2);
	//console.log(level);
 // reduce and rotate are ignored in this simple implementation, but we
 // could e.g. look at reduce and load images from a different directory
 // or pass the information to an image server
 var leafStr = '000';            
 var imgStr = (index+1).toString();
 var re = new RegExp("0{"+imgStr.length+"}$");
 var url = '<%= basePath %>/fedora/objects/' + br.pid + '-' + leafStr.replace(re, imgStr) + '/methods/demo:pageSdef/jpeg';
	//url + = '?level=' + level;
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
    return index + 1;
}

// Total number of leafs
br.numLeafs = getPageCount(br.pid);

//Book title and the URL used for the book title link
br.bookTitle= br.pid.split(':')[1] + ' on BHL Europe';
br.bookUrl  = 'http://bhl-alexandria.nhm.ac.uk/datamanagement';

// Override the path used to find UI images
br.imagesBaseURL = br.basepath + '/bookreader/images/';

br.getEmbedCode = function(frameWidth, frameHeight, viewParams) {
    return "Embed code not supported in bookreader demo.";
}

// Let's go!
br.init();

// read-aloud and search need backend compenents and are not supported in the demo
$('#BRtoolbar').find('.read').hide();
$('#textSrch').hide();
$('#btnSrch').hide();
</script>

</body>
</html>
