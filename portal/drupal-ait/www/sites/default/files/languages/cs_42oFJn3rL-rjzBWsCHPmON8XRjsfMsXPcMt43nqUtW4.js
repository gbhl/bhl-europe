Drupal.locale = { 'pluralFormula': function ($n) { return Number((((($n%10)==1)&&(($n%100)!=11))?(0):((((($n%10)>=2)&&(($n%10)<=4))&&((($n%100)<10)||(($n%100)>=20)))?(1):2))); }, 'strings': {"An AJAX HTTP error occurred.":"Objevila se AJAX HTTP chyba.","HTTP Result Code: !status":"V\u00fdsledn\u00fd k\u00f3d HTTP je: !status","An AJAX HTTP request terminated abnormally.":"AJAX HTTP po\u017eadavek skon\u010dil neobvykle.","Debugging information follows.":"N\u00e1sleduj\u00ed informace pro lad\u011bn\u00ed.","Path: !uri":"Cesta: !uri","StatusText: !statusText":"Text stavu: !statusText","ResponseText: !responseText":"Text odpov\u011bdi:  !responseText","ReadyState: !readyState":"ReadyState: !readyState","Hide":"Skr\u00fdt","Show":"Uk\u00e1zat","Hide summary":"Skr\u00fdt souhrn","Edit summary":"Upravit souhrn","Please wait...":"Pros\u00edm \u010dekejte...","New revision":"Nov\u00e1 revize","No revision":"\u017d\u00e1dn\u00e1 revize","By @name on @date":"U\u017eivatel @name dne @date","By @name":"Dle @name","Not published":"Nevyd\u00e1no","Autocomplete popup":"Vyskakovac\u00ed okno automatick\u00e9ho dokon\u010dov\u00e1n\u00ed","Searching for matches...":"Hled\u00e1m shody...","Show more":"Zobrazit v\u00edce","Show fewer":"Zobrazit m\u00e9n\u011b","Not in menu":"Nen\u00ed v menu"} };