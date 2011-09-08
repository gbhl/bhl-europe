<?php
$data = read_ods_table('schema.ods', 'Schema', 3);
echo "<pre>".var_export($data, true);

/** Open an OpenOffice spreadsheet (*.ods) and read one table
 * first line must be header.
 *
 * @param string $odsFileName 
 * @param mixed $table either the name of the table of the number (starting at 1)
 * @param int $startLine first line number to be returned (starting at 1)
 */
function read_ods_table($odsFileName, $table = 1, $startLine = 2) {
	$zip = new ZipArchive();
	if ($zip->open($odsFileName)) {
		$fp = $zip->getStream('mimetype');
		if(!$fp) {
			throw new Exception("$odsFileName is not an ODS file.");
		}
		$mime = "";
		while (!feof($fp)) {
		  $mime .= fread($fp, 2048); 
		}
		fclose($fp);
		if($mime != "application/vnd.oasis.opendocument.spreadsheet") {
			throw new Exception("$odsFileName is not an ODS file (but $mime).");
		}
		$fp = $zip->getStream('content.xml');
		if(!$fp) {
			throw new Exception("$odsFileName is not an ODS file.");
		}
		$contentXML = "";
		while (!feof($fp)) {
		  $contentXML .= fread($fp, 2048); 
		}
		fclose($fp);
		$zip->close();
		$content = new DOMDocument();
		if(!$content->loadXML($contentXML)) {
			throw new Exception("content.xml of $odsFileName is damaged.");
		}
		$xp = new DOMXPath($content);
		//xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0" xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0" xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0" xmlns:presentation="urn:oasis:names:tc:opendocument:xmlns:presentation:1.0" xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0" xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0" xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0" xmlns:ooo="http://openoffice.org/2004/office" xmlns:ooow="http://openoffice.org/2004/writer" xmlns:oooc="http://openoffice.org/2004/calc" xmlns:dom="http://www.w3.org/2001/xml-events" xmlns:xforms="http://www.w3.org/2002/xforms" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:rpt="http://openoffice.org/2005/report" xmlns:of="urn:oasis:names:tc:opendocument:xmlns:of:1.2" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:grddl="http://www.w3.org/2003/g/data-view#" xmlns:tableooo="http://openoffice.org/2009/table" xmlns:field="urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0" xmlns:formx="urn:openoffice:names:experimental:ooxml-odf-interop:xmlns:form:1.0" office:version="1.2" grddl:transformation="http://docs.oasis-open.org/office/1.2/xslt/odf2rdf.xsl">
		$xp->registerNamespace("office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
		$xp->registerNamespace("table", "urn:oasis:names:tc:opendocument:xmlns:table:1.0");
		$xp->registerNamespace("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0");
		//$xp->registerNamespace("", "");
		$idx = 0;
		if(is_numeric($table)) {
			$tables = $xp->query("/*/office:body/office:spreadsheet/table:table[$table]");
		} else {
			$tn = addcslashes($table, "'");
			$tables = $xp->query("/*/office:body/office:spreadsheet/table:table[@table:name='$tn']");
		}
		if($tables->length == 0) {
			throw new Exception("There is no table '$table' in the ODS file.");
		}
		
		$table = $tables->item(0);
		foreach($xp->query("./table:table-row[1]/table:table-cell", $table) as $headCell) {
			$name = trim($headCell->textContent);
			$count = $headCell->getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "number-columns-repeated");
			if(empty($count)) $count = 1;
			while($count-- > 0) {
				$header[$idx++] = $name;
			}
			
		}
		
		$ret = array();
		$startLine = intval($startLine);
		foreach($xp->query("./table:table-row[position()>=$startLine]", $table) as $rowEl) {
			$row = array();
			foreach($xp->query("./table:table-cell", $rowEl) as $col) {
				$val = trim($col->textContent);
				$count = $col->getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "number-columns-repeated");
				if(empty($count)) $count = 1;
				while($count-- > 0) {
					$row[] = $val;
				}
			}
			$ret[] = array_combine($header, $row);
		}
	//  header("Content-Type: text/xml");
	//  echo $content->saveXML();
		return $ret;
	} else {
		throw new Exception("Cannot open $odsFileName. Is no OpenOffice document.");
	}
}