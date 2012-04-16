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
			
			// dirty code to trigger event
			$(document).trigger('jumpToIndex', [index]);
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

	var jForm = $([
        '<form method="post" action="">',
                '<fieldset class="ocrFieldset">',
                '</fieldset>',
            // '<fieldset class="center">',
            // '<button type="submit"">Correct it!</button>',
            // '</fieldset>',
        '</form>'].join('\n'));
	
	jForm.find('.ocrFieldset').append('<iframe src="' + this.getPageOCRURI(index) + '"/>');
	
	jForm.appendTo($('#BRocr'));
}

BookReader.prototype.showPageOCR = function(index){
	this.updateOCRDialog(index);
	$.colorbox({inline: true, opacity: "0.5", href: "#BRocr", height: "500px"});
}

BookReader.prototype.clickDownload = function(format, quality, ranges, email, whole) 
{
	if (ranges.length == 0){
		return;
	}
	
	var method = '';
	switch(parseInt(format)){
		case 1:
			method = 'pdf';
			break;
		case 2:
			method = 'ocr';
			break;
		case 3:
			method = 'jpg';
			break;
	}
	
	var resolution = '';
	switch(quality){
		case 0:
			resolution = 'high';
			break;
		case 1:
			resolution = 'medium';
			break;
		case 2:
			resolution = 'low';
			break;
	}
	
	var url = '../download/' + br.bookInfo.guid + '/';
	url += method;
	
	var self = this;
	if (method == 'pdf' || method == 'jpg'){
		self.showProgressPopup('Your request is sending to the server');
		$.post(url, {ranges: ranges.toString(), resolution: resolution, email: email}, function(data){
			$.colorbox.close();
			$(self.popup).html('The server has received your request, please wait for our notification letter');
			setTimeout(function(){
				$(self.popup).fadeOut('slow', function() {
	                self.removeProgressPopup();
	            })        
	        },3000);
		});
	} else if (method = 'ocr'){
		url += '?ranges=' + ranges;
		window.open(url);
	}

}

BookReader.prototype.search = function(term) {
    // console.log('search called with term=' + term);
    $('#textSrch').blur(); // cause mobile safari to hide the keyboard
    
    var url = 'search/' + this.bookInfo.guid + '?query=' + escape(term);
    
    term = term.replace(/\//g, ' '); // strip slashes, since this goes in the
										// url
    this.searchTerm = term;
    
    this.removeSearchResults();
    this.showProgressPopup('<img id="searchmarker" src="'+this.imagesBaseURL + 'marker_srch-on.png'+'"> Search results will appear below...');
    // $.ajax({url:url, dataType:'jsonp', jsonpCallback:'br.BRSearchCallback'});
	$.ajax({url:url, dataType:'json', success: function(data){br.BRSearchCallback(data);}});  
}

BookReader.prototype.leafNumToIndex = function(pageNum) {
	for ( var i = 0; i < this.bookInfo.length; i++) {
		if (pageNum == this.bookInfo.pages[i].name){
			return i;
		}
	}
	return 0;
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
// '<fieldset>',
// '<input type="checkbox" id="whole_book" name="whole_book" ></input>',
// '<label for="whole_book">Select the whole literature</label>',
// '</fieldset>',
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
			'<fieldset class="format">',
				'<label for="format">Quality:</label>',
					'<select name="quality">',
					 '<option value="high">High</option>',
					  '<option value="medium" selected="selected">Medium</option>',
					  '<option value="low">Low</option>',
					'</select>',
			'</fieldset>',
			'<fieldset class="email">',
			'<label for="email">E-mail:</label>',
				'<input name="email"/>',
			'</fieldset>',
            '<fieldset class="center">',
                '<button type="button" onclick="br.clickDownload($(\'input:radio[name=format]:checked\').val(), $(\'select[name=quality]\').get(0).selectedIndex, br.selectedPages, $(\'input[name=email]\').val(), $(\'#whole_book\').is(\':checked\'));">Download</button>',
            '</fieldset>',
        '</form>'].join('\n'));
	
	jForm.find(':radio').click(function(){
		if (this.value == 2) {
			$('.email').slideUp('slow');
			$('.format').slideUp('slow');
		} else {
			$('.email').slideDown('slow');
			$('.format').slideDown('slow');
		}
		$('#cboxLoadedContent').css('height', '');
		$('#cboxContent').css('height', '');
	});
	
	if (this.selectedPages.length == 0){
		jForm.find('.thumbnailFieldset').append('Your download list is empty.');
	} else {
		jForm.find('.thumbnailFieldset').append(this.getBasketThumbnails());
	}
	
// jForm.find('#whole_book').click(function(){
// if ($(this).is(':checked')){
// $('.thumbnailFieldset').parent().slideUp('slow');
// // Update the height of the colorbox
// $('#cboxLoadedContent').css('height', '');
// $('#cboxContent').css('height', '');
// } else {
// $('.thumbnailFieldset').parent().slideDown('slow');
// }
// });
	  
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

BookReader.prototype.buildCollapsableBox = function(){
	var box = $(['<div id="BRbox">',
	                	'<div id="BRboxhead" class="BRexpand" />',
	                	'<div id="BRboxcontent" />',
	                '</div>'
	                ].join('\n'));
	
	box.find('#BRboxcontent').append(this.buildPageList());
	box.find('#BRboxcontent').append(this.buildScientificNameList());
	
	var self = this;
	box.find('#BRboxhead').click(function() {
		if ($(this).hasClass('BRexpand')) {
			$(this).animate({
				opacity : 1
			});
			$(this).parent().animate({
				left : "0px",
			});
			$('#BookReader').animate({
				left : "210px",
			}, function(){
				self.prepareView();
			});
			$(this).addClass('BRcollapse').removeClass('BRexpand');
		} else {
			$(this).animate({
				opacity : .25
			});
			$(this).parent().animate({
				left : "-200px",
			});
			$('#BookReader').animate({
				left : "0px",
			}, function(){
				self.prepareView();
			});
			$(this).addClass('BRexpand').removeClass('BRcollapse');
		}

	})
	
    box.find('#BRboxhead').mouseover(function(){
        if ($(this).hasClass('BRexpand')) {
            $('#BRboxhead').animate({opacity:1},250);
        };
    });
	 box.find('#BRboxhead').mouseleave(function(){
        if ($(this).hasClass('BRexpand')) {
            $('#BRboxhead').animate({opacity:.25},250);
        };
    });
	
	var self = this;
	$(document).bind('jumpToIndex', function(event, index){
		self.updatePageList(index);
		self.updateScientificNameList(index);
	})
	
	return box;
}

BookReader.prototype.buildPageList = function(){
	var pageListDiv = $('<div id="BRpagination" />');
	
	if (this.bookInfo.pages == undefined){
		pageListDiv.text("Page information not found");
		return;
	}
	
	var fieldset = $('<fieldset />');
	var legend = $('<legend>Pages</legend>');
	fieldset.append(legend);
	
	var pageList = $('<select size="20" />');
	pageList.appendTo(fieldset);
	
	fieldset.appendTo(pageListDiv);
	
	var self = this;
	for (i = 0; i < this.bookInfo.pages.length; i++){
		var pageEntry = $('<option />');
		pageEntry.text(this.bookInfo.pages[i].name);
		pageList.append(pageEntry);
		(function() {
			var index = i;
			pageEntry.click(function(){
				self.jumpToIndex(index);
			});
		})();
	}
	
	return pageListDiv;
}

BookReader.prototype.updatePageList = function(index){
	$('#BRpagination').find('select').get(0).selectedIndex = index;
}

BookReader.prototype.buildScientificNameList = function(){
	var scientificNamesDiv = $('<div id="BRscientificNames" />');
	
	if (this.bookInfo.pages == undefined){
		scientificNamesDiv.text("Page information not found");
		return;
	}
	
	var fieldset = $('<fieldset />');
	var legend = $('<legend>Names on this page</legend>');
	fieldset.append(legend);
	
	var nameList = $('<ol />');
	nameList.appendTo(fieldset);
	
	var ubioSubtitle = $('<div id="ubioLinkDiv"><p>Powered by <a href="http://www.uBio.org" target="_blank" title="uBio">uBio</a></p></div>')
	fieldset.append(ubioSubtitle);

	scientificNamesDiv.append(fieldset);
	
	return scientificNamesDiv;
}

BookReader.prototype.updateScientificNameList = function(index){
	var nameList = $('#BRscientificNames').find('ol');
	nameList.empty();
	
	if(undefined == this.bookInfo.pages[index]){
		return;
	}
	
	if(this.bookInfo.pages[index].scientificNames.length == 0){
		var msgEntry = $('<li />');
		msgEntry.text('No Names Found');
		nameList.append(msgEntry);
	} else {
		for (i = 0; i < this.bookInfo.pages[index].scientificNames.length; i++){
			var nameEntry = $('<li />');
			nameEntry.text(this.bookInfo.pages[index].scientificNames[i]);
			nameList.append(nameEntry);
		}
	}
}

BookReader.prototype.appendPageBox = function(){
	$('#BRpage').after('<div id="BRpagebox">Go to:<input type="text"/></div>');
	$('#BRpagebox').find('input').val(br.getPageNum(br.currentIndex()));
	$('#BRpagebox').find('input').change(function(){
		br.jumpToIndex(br.getPageIndex($(this).val()));
	});
}

BookReader.prototype.appendDownloadButtonAndDialog = function(){
	var hiddenDiv = $('<div style="display: none;" />');
	$('body').append(hiddenDiv);
	
	$(".BRicon.info").before('<button class="BRicon download"></button>');
	$('#BRtoolbar').find('.download').colorbox({inline: true, opacity: "0.5", href: "#BRdownload", onLoad: function() { br.autoStop(); br.ttsStop(); } });
	hiddenDiv.append(br.blankDownloadDiv());
	this.updateDownloadDialog();
}

BookReader.prototype.appendOCRDialog = function(){
	var hiddenDiv = $('<div style="display: none;" />');
	$('body').append(hiddenDiv);
	
	hiddenDiv.append(br.blankOCRDiv());
}

BookReader.prototype.appendCollapsableBox = function(){
	var mainDiv = $('<div id="main" />');
	$('#BookReader').wrap(mainDiv);
	$('#BookReader').before(br.buildCollapsableBox());
}

BookReader.prototype.prepareView = function(){
    if (this.constMode1up == this.mode) {
        this.prepareOnePageView();
    } else if (this.constModeThumb == this.mode) {
        this.prepareThumbnailView();
    } else {
        this.prepareTwoPageView();
    }
}