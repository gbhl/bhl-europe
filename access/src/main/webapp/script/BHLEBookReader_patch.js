BookReader.prototype.selectedPages = [];

BookReader.prototype.initTOC = function() {
    var self = this;
	$.getJSON('/bookreader/toc.json', function(data){
		self.updateTOC(data);
	})
}

BookReader.prototype.getToolBox = function(index) {
	var toolbox = $(
	   "<div name='BRtoolbox_" + index + "' id='BRtoolbox_" + index + "' class='BRtoolbox'>"
        +   "<span id='BRtoolboxbuttons'>"
        +     "<button class='BRicon add_page' title='Add this page to download basket'></button>"
		+     "<button class='BRicon remove_page' title='Remove this page from download basket'></button>"
		+     "<button class='BRicon ocr' title='Display OCR of this page'></button>"
        +   "</span>"
        + "</div>"
	);
	
	if (this.selectedPages.indexOf(index) == -1){
		toolbox.find('.add_page').show();
		toolbox.find('.remove_page').hide();
	} else {
		toolbox.find('.add_page').hide();
		toolbox.find('.remove_page').show();
	}
	
	var self = this;
	toolbox.find('.add_page').click( function(){
		self.updateDownloadBasketArray(index);
		self.updateDownloadDialog();
		$(this).hide();
        $(this).siblings('.remove_page').show();
	});
	
	toolbox.find('.remove_page').click( function(){
		self.updateDownloadBasketArray(index);
		self.updateDownloadDialog();
		$(this).hide();
        $(this).siblings('.add_page').show();
	});
	
	toolbox.find('.ocr').click( function(){
		self.showPageOCR(index);
	});
	
	toolbox.hide();
	
	return toolbox;
}

BookReader.prototype.addToolBox = function (index, page) {
	if (this.ui != 'embed' && index >= 0 && index <= (this.numLeafs - 1)) {

		var toolbox = this.getToolBox(index);

        toolbox.appendTo(page.parent());
		
		toolbox.mouseenter(function(){
			page.mouseenter();
		}).mouseout(function(){
		});

		var self = this;
        page.mouseenter(function () {
			if (self.selectedPages.indexOf(index) == -1){
				toolbox.find('.add_page').show();
				toolbox.find('.remove_page').hide();
			} else {
				toolbox.find('.add_page').hide();
				toolbox.find('.remove_page').show();
			}
			self.positionToolBox(toolbox, page);
			toolbox.show();
        }).mouseout(function () {
			toolbox.hide();
        });
    }
}

String.prototype.trimPx = function() {
    // remove "px" from values
    var pos = this.indexOf("px");
    if (pos != 0)
        return parseInt(this.substring(0, pos));
    else
        return 0;
}

BookReader.prototype.positionToolBox = function (toolbox, page) {
	toolbox.css('position', 'absolute');
	toolbox.css('left', page.position().left + page.width()/2 - toolbox.width()/2 );
	toolbox.css('z-index', 3);
	
	var margin = 10;
	
	var scrolltop = $('#BRcontainer').scrollTop();
	var pagetop = page.parent().css('top').trimPx();
	var toolbar = $('#BRtoolbar');
	var barheight = toolbar.css('height').trimPx() + toolbar.css('top').trimPx();
	var pageheight = page.height();
	var toolboxheight = toolbox.height();
	
	
	if (scrolltop > 0){
		if (scrolltop + barheight + margin - pagetop > 0 && scrolltop + barheight + toolboxheight + margin - pagetop - pageheight < 0) {
			toolbox.css('top', scrolltop - pagetop + barheight);
		} else {
			toolbox.css('top', margin);
		}
	} else {
		if (barheight - pagetop > margin){
			toolbox.css('top', barheight - pagetop);
		} else {
			toolbox.css('top', margin);
		}
	}
}

BookReader.prototype.removeAllToolBox = function(){
	$('div[name^="BRtoolbox"]').each(function(){
		$(this).remove();
	});
}

BookReader.prototype.updateDownloadBasketArray = function(index){
	var position = this.selectedPages.indexOf(index);

	if(position == -1){
		this.selectedPages.push(index);
	} else {
		this.selectedPages.splice(position, 1);
	}
	
	this.selectedPages.sort( function (a,b) { return a-b });
	
	$('.download').html(this.selectedPages.length);
	$('.download').effect("highlight", {}, 'medium');
}

BookReader.prototype.getBasketThumbnails = function(){
	var basketThumbnails = document.createElement("ul")
	$(basketThumbnails).addClass('BRtnlist');
	
	var self = this;
	for (i = 0; i < this.selectedPages.length; i++){
		var li = document.createElement("li");
		$(li).attr('id', 'thumbnail_' + self.selectedPages[i]);
	
		var img = document.createElement("img");
		img.src = this._getPageURI(this.selectedPages[i], this.reduce);
		$(li).append(img);
		
		var button = document.createElement("button");
		$(button).addClass('delete_tumbnail');
		$(button).click(function(event){
			event.preventDefault();
			var pageNum = parseInt($(this).parent().attr("id").split('_')[1]);
			self.updateDownloadBasketArray(pageNum);
			// $(this).parent().fadeOut('fast');
			$(this).parent().fadeOut();
			
			// Update the height of the colorbox
			$('#cboxLoadedContent').css('height', '');
			$('#cboxContent').css('height', '');
		});
		$(li).append(button);
		
		$(basketThumbnails).append(li);
	}
	
	return basketThumbnails;
}

BookReader.prototype.updateDownloadDialog = function(){
	this.buildDownloadDiv($('#BRdownload'));
}

BookReader.prototype.updateOCRDialog = function(index){
	$('#BRocr').find('form').each(function(){
		$(this).remove();
	});

	var ocrUrl = this.getPageOCRURI(index);
	var pageOCR = $.ajax({
      url: ocrUrl,
      async:false
   }
	).responseText;
	
	var jForm = $([
        '<form method="post" action="">',
                '<fieldset class="ocrFieldset">',
                '</fieldset>',
            '<fieldset class="center">',
                '<button type="submit"">Correct it!</button>',
            '</fieldset>',
        '</form>'].join('\n'));
	
	jForm.find('.ocrFieldset').append('<textarea class="ocrTextarea">' + pageOCR + '</textarea >');
	
	jForm.appendTo($('#BRocr'));
}

BookReader.prototype.startDownload = function(format, quality, ranges, whole) 
{
	var method = '';
	switch(parseInt(format)){
		case 1:
			method = 'pdf';
			break;
		case 2:
			method = 'ocr';
			break;
		case 3:
			method = 'zip';
			break;
	}
	
	var resolution = '';
	switch(parseInt(quality)){
		case 1:
			resolution = 'high';
			break;
		case 2:
			resolution = 'medium';
			break;
		case 3:
			resolution = 'low';
			break;
	}
	
	var url = '/fedora/objects/' + br.pid + '/methods/bhle-service:bookSdef/';
	url += method;
	url += '?ranges=';
	
	if (!whole && ranges.length != 0){
		url += ranges;
	} else if (!whole && ranges.length == 0){
		return;
	}
	
	if (method != 'ocr'){
		url += '&resolution=';
		url += resolution;
	} 

	window.open(url);
}

BookReader.prototype.showPageOCR = function(index){
	this.updateOCRDialog(index);
	$.colorbox({inline: true, href: '#BRocr', opacity: 0.3});
}

BookReader.prototype.search = function(term) {
    //console.log('search called with term=' + term);
    
    $('#textSrch').blur(); //cause mobile safari to hide the keyboard     
    
    var url = 'http://'+this.server.replace(/:.+/, ''); //remove the port and userdir
    url    += '/fulltext/inside.php?item_id='+this.bookId;
    url    += '&doc='+this.subPrefix;   //TODO: test with subitem
    url    += '&path='+this.bookPath.replace(new RegExp('/'+this.subPrefix+'$'), ''); //remove subPrefix from end of path
    url    += '&q='+escape(term);
    //console.log('search url='+url);
    
    term = term.replace(/\//g, ' '); // strip slashes, since this goes in the url
    this.searchTerm = term;
    
    this.removeSearchResults();
    this.showProgressPopup('<img id="searchmarker" src="'+this.imagesBaseURL + 'marker_srch-on.png'+'"> Search results will appear below...');
    $.ajax({url:url, dataType:'jsonp', jsonpCallback:'br.BRSearchCallback'});    
}

BookReader.prototype.leafNumToIndex = function(pageNum) {
    return pageNum - 1;
}

BookReader.prototype.blankDownloadDiv = function() {
    return $([
        '<div class="BRfloat" id="BRdownload">',
            '<div class="BRfloatHead">',
                'Download',
                '<a class="floatShut" href="javascript:;" onclick="$.fn.colorbox.close();"><span class="shift">Close</span></a>',
            '</div>',
        '</div>'].join('\n')
    );
}

BookReader.prototype.blankOCRDiv = function() {
    return $([
        '<div class="BRfloat" id="BRocr">',
            '<div class="BRfloatHead">',
                'OCR',
                '<a class="floatShut" href="javascript:;" onclick="$.fn.colorbox.close();"><span class="shift">Close</span></a>',
            '</div>',
        '</div>'].join('\n')
    );
}

BookReader.prototype.buildDownloadDiv = function(jDownloadDiv) 
{
	// Remove all fieldsets
	$(jDownloadDiv).find('fieldset').each(function(){
		$(this).remove();
	});
	
    var jForm = $([
        '<form method="post" action="">',
			'<fieldset>',
				'<label>Your Basket:</label>',
                '<fieldset class="thumbnailFieldset">',
                '</fieldset>',
            '</fieldset>',
//			'<fieldset>',
//                '<input type="checkbox" id="whole_book" name="whole_book" ></input>',
//				'<label for="whole_book">Select the whole literature</label>',
//			'</fieldset>',
			'<fieldset class="format_selection">',
				'<label for="format">Format:</label>',
                '<fieldset class="format_selection">',
                        '<input type="radio" name="format" value="1" checked="checked"/>',
                        '<img src="' + this.imagesBaseURL + 'PDF.png"/>',
                        '<input type="radio" name="format" value="2"/>',
                        '<img src="' + this.imagesBaseURL + 'OCR.png"/>',
                        '<input type="radio" name="format" value="3"/>',
                        '<img src="' + this.imagesBaseURL + 'JPEG.png"/>',
				'</fieldset>',
            '</fieldset>',
			'<fieldset>',
				'<label for="format">Quality:</label>',
					'<select name="quality">',
					 '<option value="high">High</option>',
					  '<option value="medium" selected="selected">Medium</option>',
					  '<option value="low">Low</option>',
					'</select>',
				'</fieldset>',
            '<fieldset class="center">',
                '<button type="button" onclick="$.fn.colorbox.close(); br.startDownload($(\'input:radio[name=format]:checked\').val(), $(\'input:radio[name=quality]:checked\').val() ,br.downloadPages, $(\'#whole_book\').is(\':checked\'));">Download</button>',
            '</fieldset>',
        '</form>'].join('\n'));
	
	var tns = this.getBasketThumbnails();
	
	if (this.selectedPages.length == 0){
		jForm.find('.thumbnailFieldset').append('(Empty)');
	} else {
		jForm.find('.thumbnailFieldset').append(tns);
	}
	
//	jForm.find('#whole_book').click(function(){
//		if ($(this).is(':checked')){
//			$('.thumbnailFieldset').parent().slideUp('slow');
//			// Update the height of the colorbox
//			$('#cboxLoadedContent').css('height', '');
//			$('#cboxContent').css('height', '');
//		} else {
//			$('.thumbnailFieldset').parent().slideDown('slow');
//		}
//	});
	  
    jForm.appendTo(jDownloadDiv);
    jForm = ''; // closure
}

BookReader.prototype.updatePageBoxPageNum = function(index) {
    var pageNum = this.getPageNum(index);
    var pageStr;
    if (pageNum[0] == 'n') { // funny index
        pageStr = index + ' / ' + this.numLeafs;
    } else {
        pageStr = 'Page ' + pageNum;
    }
    
    $('#pagenum .currentpage').text(pageStr);
}