































































































































// ************************************************
// * VARIOUS JS TOOLS                             *
// *                                              *
// * ALL RIGHTS RESERVED BY ANDREAS MEHRRATH (C)  *
// *                                              *
// ************************************************




// *** FENSTER SCHLIESSEN UND OEFFNER FOCUSIEREN ***
// *** NICHT MODUS 0 BESETZEN
function closeMe(modus)
{
 if (modus==1) { opener.document.body.focus();		 }
 if (modus==2) { opener.document.location.reload();	 }
 if (modus==3) { parent.window.close(); return true; }

 window.close();
 return true;
}





// *** BESTAETIGUNGSDIALOG ***

function nachfrage(text,targetUrl)
{
 if (confirm(text))
 {
  document.location.href = targetUrl;
 }
 else
	 return false;
}


// *** EMAIL ADRESSENCHECK ***

function check_email(address)
{
  if ((address == "") ||
	  (address.indexOf ('@') == -1) ||
	  (address.indexOf ('.') == -1) ||
	  (address.length < 6))              // kleinste moegl. email a@b.at
      return false;
  else
	  return true;
}


// **************
// popup standard
// **************
function popup_win(name,theUrl,xsize,ysize)
{
 xsize = xsize + 10;  // inner - height correction
 ysize = ysize + 25;
 var abstand_links = ((window.screen.width)  - (xsize+20)) / 2;
 var abstand_oben  = ((window.screen.height) - (ysize+50)) / 2;
 return window.open(theUrl,name,'toolbar=no,scrollbars=yes,location=no,directories=no,status=no,menubar=no,resizable=yes,width=' + xsize +',height=' + ysize + ',top='+ abstand_oben +',left='+ abstand_links);
}


// *************
// center object
// *************
// mode: 1... document centered, 2... screen centered
function centerIt(myObj,objWidth,objHeight,mode)
{
    if ((mode==null)||(mode==1))
    {
        myObj.style.left = '' + Math.round(((document.body.clientWidth)  - (objWidth))  / 2) + 'px';
        myObj.style.top  = '' + Math.round(((document.body.clientHeight) - (objHeight)) / 2) + 'px';
    }
    else
    {
        myObj.style.left = '' + Math.round(((window.screen.width)  - (objWidth))  / 2) + 'px';
        myObj.style.top  = '' + Math.round(((window.screen.height) - (objHeight)) / 2) + 'px';
    }
}


// ********************************
// fenster wie dialogbox ohne alles
// ********************************
function popup_dlg(name,theUrl,xsize,ysize)
{
 xsize = xsize + 10;  // inner - height correction
 ysize = ysize + 25;

 var abstand_links = ((window.screen.width)  - xsize) / 2;
 var abstand_oben  = ((window.screen.height) - ysize) / 2;
 return window.open(theUrl,name,'toolbar=no,scrollbars=no,location=no,directories=no,status=no,menubar=no,resizable=no,width=' + xsize +',height=' + ysize + ',top='+ abstand_oben +',left='+ abstand_links);
}


// ************************
// popup mit allen features
// ************************
function popup_winall(name,theUrl,xsize,ysize)
{
 xsize = xsize + 10;  // inner - height correction
 ysize = ysize + 25;

 var abstand_links = ((window.screen.width)  - xsize) / 2;
 var abstand_oben  = ((window.screen.height) - ysize) / 2;
 return window.open(theUrl,name,'toolbar=yes,scrollbars=yes,location=yes,directories=yes,status=yes,menubar=yes,resizable=yes,width=' + xsize +',height=' + ysize + ',top='+ abstand_oben +',left='+ abstand_links);
}



// ********************************
// fenster wie dialogbox ohne alles
// ********************************
function modal_dlg(name,theUrl,xsize,ysize)
{
 // xsize = xsize + 10;  // inner - height correction
 // ysize = ysize + 25;

 var abstand_links = ((window.screen.width)  - xsize) / 2;
 var abstand_oben  = ((window.screen.height) - ysize) / 2;
 return window.showModalDialog(theUrl,name,"dialogWidth:" + xsize + "px;dialogHeight:" + ysize + "px");
}








// ***********************
function colorpicker(feld)
// ***********************
{
 popup_dlg('colorpicker', "../../shared/colorpicker.php?feld="+feld,380,280);
}



// *******************
function csspicker(feld)
// *******************
{
 // popup_dlg('stylepicker', home_url + "../../shared/stylepicker.php?feld="+feld,600,400);
 alert("This function is not available right now.");
}



// **************************
function invcolorvalue(myval)
// **************************
{
 if (myval=="F") return "0";
 if (myval=="E") return "1";
 if (myval=="D") return "2";
 if (myval=="C") return "3";
 if (myval=="B") return "4";
 if (myval=="A") return "5";
 if (myval=="0") return "F";
 if (myval=="1") return "E";
 if (myval=="2") return "D";
 if (myval=="3") return "C";
 if (myval=="4") return "B";
 if (myval=="5") return "A";
 if (myval=="6") return "0";
 if (myval=="7") return "0";
 if (myval=="8") return "0";
 if (myval=="9") return "0";
 return "8";
}




// ************************
function invcolor(strColor)
// ************************
{
 var invStr = "";
 for (i=1;i<strColor.length;i++)
 {
  invStr = invStr + invcolorvalue(strColor.substr(i,1));
 }
 return "#" + invStr;
}



// **************************
function IsObject(Expression)
// **************************
{
  return (typeof Expression == "object");
}



// ***************************
function IsElement(Expression)
// ***************************
{
  return (typeof Expression != "undefined");
}



// ***********************************************
// *** TESTET AUSDRUCK AUF VORKOMMEN VON REGEX ***
// ***********************************************
// Example: contains = instr('Gzip','/[g-z]/i',2);
// ***********************************************
function instr(ausdruck,regex,mode)
{
  var retVal = false;

  if (mode==1) {  // mode 1: string vergleich
   if ( ausdruck.search(regex) >= 0 ) { retVal = true; }
  }

  if (mode==2) {  // mode 2: regex vergleich
   eval("if ( " + regex + ".test(ausdruck) ) { retVal = true; } ");
  }

  return retVal;
}



// *********************************************
function SetCookie(cookieName,cookieValue,nDays)
// *********************************************
{
	var today  = new Date();
	var expire = new Date();
	if (nDays==null || nDays==0) nDays=1;
	expire.setTime(today.getTime() + 3600000*24*nDays);
	document.cookie = cookieName+"="+escape(cookieValue) + ";expires="+expire.toGMTString();
}



// ****************************
function switch_visibility(obj)
// ****************************
{
	if ( (IsObject(obj)) || (IsElement(obj)) )
	{
	 if (obj.style.visibility=='visible') { obj.style.visibility = 'hidden';  }
	 else								  { obj.style.visibility = 'visible'; }
	}
}



// *************************************
function moveLayer(layer_name,posx,posy)
// *************************************
{
 eval('document.getElementById(\'' + layer_name + '\').style.left = \'' + posx + '\';');
 eval('document.getElementById(\'' + layer_name + '\').style.top  = \'' + posy + '\';');
}



// ****************************
function hideLayer(layer_name)
// ****************************
{
 eval('document.getElementById(\'' + layer_name + '\').style.visibility = \'hidden\';');
}



// ****************************
function showLayer(layer_name)
// ****************************
{
 eval('document.getElementById(\'' + layer_name + '\').style.visibility = \'visible\';');
}


// ****************************
function disableObject(form_name, object_name)
// ****************************
{
 eval('document.forms.' + form_name + '.' + object_name + '.disabled = \'true\';');
}


// **********************
function isAvailable(obj)
// **********************
{
	if ( (IsObject(obj)) || (IsElement(obj)) )
	{
	 if ((obj.style.visibility!='hidden') && (!obj.disabled))
	 {
	     return true;
	 }
	}
    return false;
}


// *********************************************************
function uebertrage_multiselect(select_obj,textfeld,trenner)
// *********************************************************
{
    var strNormalized = "";
    var nEntries      = select_obj.options.length;
    var nFound        = 0;

    // ALLE SELEKTIERTEN IM OBJEKT ERMITTELN UND IN STRING SCHREIBEN
    for (var i=0; i<nEntries; i++)
    {
        if ((select_obj.options[i].selected)&&(select_obj.options[i].value!=trenner))
        {
            if (nFound>0) { strNormalized += trenner; }

            strNormalized += select_obj.options[i].value;

            nFound++;
        }
    }

    // AUFNAHMEFELD BEFUELLEN
    eval(textfeld + '.value = "' + strNormalized + '";');
}


// **********************
function isNumeric(value)
// **********************
{
  if (value == null || !value.toString().match(/^[-]?\d*\.?\d*$/)) return false;
  return true;
}





// **********************
function getX( oElement )
// **********************
{
    var iReturnValue = 0;
    while( oElement != null )
    {
      iReturnValue += parseInt(oElement.offsetLeft);
      oElement = oElement.offsetParent;
    }

    return parseInt(iReturnValue);
}



// **********************
function getY( oElement )
// **********************
{
    var iReturnValue = 0;
    while( oElement != null )
    {
      iReturnValue += parseInt(oElement.offsetTop);
      oElement = oElement.offsetParent;
    }

    return parseInt(iReturnValue);
}


// ********************
function checkBrowser()
// ********************
{
 if (window.opera)     { return 'opera';  }

 if (navigator.userAgent)
 {
  var agent = navigator.userAgent;

  if      (instr(agent,'MSIE',1))   { return 'ie';      }
  else if (instr(agent,'irefox',1)) { return 'firefox'; }
  else if (instr(agent,'hrome',1))  { return 'chrome';  }
  return 'other';
 }

 else if (navigator.vendor) { return 'apple';  } // and other new ones

 return 'other';
}


// ******************************************************
function setSelectOptions(the_form, the_select, do_check)
// ******************************************************
{
    var selectObject = document.forms[the_form].elements[the_select];
    var selectCount  = selectObject.length;

    for (var i = 0; i < selectCount; i++)
    {
        if (selectObject.options[i].value!="") { selectObject.options[i].selected = do_check; }
    }

    return true;
}



// ******************************************
function getElementByValue(ElementValue,Node)
// ******************************************
{
    var search = Node.document.getElementsByTagName('*');       // input, ...

	for (var i=0; i<search.length; i++)
	{
	    if (search[i].value == ElementValue) { return search[i]; }
	}

    return false;
}



// ********************************************
function getXMLHttpRequest()
// ********************************************
{
	var httpReq = null;
	if (window.XMLHttpRequest)
	{
		httpReq = new XMLHttpRequest();
	}
	else if (typeof ActiveXObject != "undefined")
	{
		httpReq = new ActiveXObject("Microsoft.XMLHTTP");
	}
	return httpReq;
}




// ********************************************
function sendRequestPOST(url, handler, params)
// ********************************************
{
	req = getXMLHttpRequest();

	if (req)
	{
		req.onreadystatechange = handler;

		req.open("POST", url, true);

	    // SEND THE PROPER HEADER INFORMATION ALONG WITH THE REQUEST

		req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		req.setRequestHeader("Content-length", params.length);
		req.setRequestHeader("Connection", "close");

		req.send(params);
	}
}


// ********************************************
function sendRequestGET(url, handler, params)
// ********************************************
{
	req = getXMLHttpRequest();

	if (req)
	{
		req.onreadystatechange = handler;

		req.open("GET", url+params, true);

		req.send(null);
	}
}


// ********************************************
if (typeof String.prototype.trim != 'function')
// ********************************************
{
  String.prototype.trim = function()
  {
    return this.replace(/^\s+/, '').replace(/\s+$/, '');
  };
}



// ******************************
function html_entity_decode(str)
// ******************************
{
	  var myTextarea=document.createElement('textarea');
	  myTextarea.innerHTML = str;
	  return myTextarea.value;
}



// *****************************************
function relocateLayer(layer_name,x,y,w,h,z)
// *****************************************
{
    container = document.getElementById(layer_name);

    container.style.left   = parseInt(x) + 'px';
    container.style.top    = parseInt(y) + 'px';
    container.style.width  = parseInt(w) + 'px';
    container.style.height = parseInt(h) + 'px';
    container.style.zIndex = parseInt(z);
}



// ************************
function setMyHome(sel,url)
// ************************
{
 if (sel==null) { sel = document.body; }
 
 if (document.all) 
 {
   sel.style.behavior = 'url(#default#homePage)';
   sel.setHomePage(url);
   return false;
 }
 else if (window.sidebar)
 {
    if(window.netscape)
    {
         try
         {  
            netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
         }  
         catch(e)
         {  
            alert("This action was avoided by your browser. If you want to enable \n\
please enter about:config in your address line and change the value of\n\
\n\
signed.applets.codebase_principal_support\n\
\n\
to true.");  
         }
    } 
    var prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components. interfaces.nsIPrefBranch);
    prefs.setCharPref('browser.startup.homepage',url);
 }
}



// *******************************
function createBookmark(url,title)
// *******************************
{
 if (window.sidebar)
 {                                               // Mozilla Firefox Bookmark
  window.sidebar.addPanel(title, url,"");
 }
 else if( window.external )
 {                                               // IE Favorite
  window.external.AddFavorite( url, title); }
  else if(window.opera && window.print) {        // Opera Hotlist
  return true;
 }
}


var userClient = checkBrowser();

