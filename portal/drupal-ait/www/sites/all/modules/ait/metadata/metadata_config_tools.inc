<?php

/** Open an OpenOffice spreadsheet (*.ods) and read one table
 * first line must be header.
 *
 * @param string $odsFileName 
 * @param mixed $table either the name of the table of the number (starting at 1)
 * @param int $startLine first line number to be returned (starting at 1)
 */
function metadata_read_ods_table($odsFileName, $table = 1, $startLine = 2, $skipEmptyRows = true) {
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
      $notEmpty = false;
      foreach($xp->query("./table:table-cell", $rowEl) as $col) {
        $val = trim($col->textContent);
        if(!empty($val)) $notEmpty = true;
        $count = $col->getAttributeNS("urn:oasis:names:tc:opendocument:xmlns:table:1.0", "number-columns-repeated");
        if(empty($count)) $count = 1;
        while($count-- > 0) {
          $row[] = $val;
        }
      }
      if(!$skipEmptyRows || $notEmpty) {
        $ret[] = array_combine($header, $row);
      }
    }
  //  header("Content-Type: text/xml");
  //  echo $content->saveXML();
    return $ret;
  } else {
    throw new Exception("Cannot open $odsFileName. Is no OpenOffice document.");
  }
}


/**
 * @param array $data e.g. array( 
 *                          array('Field' => 'dc_title', 'Type' => 'text',
 *                             'Facet' => '1',  'Fulltext' => '1',),
 *                         );
 * @returns DOMDocument the solr schema.xml
 */
function metadata_create_schema($data) {
  $ret = new DOMDocument();
  $ret->loadXML(
'<'.'?xml version="1.0" encoding="UTF-8" ?'.'>
<!-- $Id: schema.xml,v 1.16 2010/12/09 04:39:00 pwolanin Exp $ -->

<!--
 This is the Solr schema file. This file should be named "schema.xml" and
 should be in the conf directory under the solr home
 (i.e. ./solr/conf/schema.xml by default)
 or located where the classloader for the Solr webapp can find it.

 For more information, on how to customize this file, please see
 http://wiki.apache.org/solr/SchemaXml
-->

<schema name="drupal-metadata" version="0.1">
    <!-- attribute "name" is the name of this schema and is only used for display purposes.
         Applications should change this to reflect the nature of the search collection.
         version="1.2" is Solrs version number for the schema syntax and semantics.  It should
         not normally be changed by applications.
         1.0: multiValued attribute did not exist, all fields are multiValued by nature
         1.1: multiValued attribute introduced, false by default 
         1.2: omitTermFreqAndPositions attribute introduced, true by default except for text fields.
       -->
  <types>
    <!-- field type definitions. The "name" attribute is
       just a label to be used by field definitions.  The "class"
       attribute and any other attributes determine the real
       behavior of the fieldType.
         Class names starting with "solr" refer to java classes in the
       org.apache.solr.analysis package.
    -->

    <!-- The StrField type is not analyzed, but indexed/stored verbatim.
       - StrField and TextField support an optional compressThreshold which
       limits compression (if enabled in the derived fields) to values which
       exceed a certain size (in characters).
    -->
    <fieldType name="string" class="solr.StrField" sortMissingLast="true" omitNorms="true"/>

    <!-- boolean type: "true" or "false" -->
    <fieldType name="boolean" class="solr.BoolField" sortMissingLast="true" omitNorms="true"/>

    <!-- The optional sortMissingLast and sortMissingFirst attributes are
         currently supported on types that are sorted internally as strings.
       - If sortMissingLast="true", then a sort on this field will cause documents
         without the field to come after documents with the field,
         regardless of the requested sort order (asc or desc).
       - If sortMissingFirst="true", then a sort on this field will cause documents
         without the field to come before documents with the field,
         regardless of the requested sort order.
       - If sortMissingLast="false" and sortMissingFirst="false" (the default),
         then default lucene sorting will be used which places docs without the
         field first in an ascending sort and last in a descending sort.
    -->


    <!-- numeric field types that store and index the text
         value verbatim (and hence dont support range queries, since the
         lexicographic ordering isnt equal to the numeric ordering) -->
    <fieldType name="integer" class="solr.IntField" omitNorms="true"/>
    <fieldType name="long" class="solr.LongField" omitNorms="true"/>
    <fieldType name="float" class="solr.FloatField" omitNorms="true"/>
    <fieldType name="double" class="solr.DoubleField" omitNorms="true"/>


    <!--
      Note:
      These should only be used for compatibility with existing indexes (created with older Solr versions)
      or if "sortMissingFirst" or "sortMissingLast" functionality is needed. Use Trie based fields instead.

      Numeric field types that manipulate the value into
      a string value that isnt human-readable in its internal form,
      but with a lexicographic ordering the same as the numeric ordering,
      so that range queries work correctly.
    -->
    <fieldType name="sint" class="solr.SortableIntField" sortMissingLast="true" omitNorms="true"/>
    <fieldType name="slong" class="solr.SortableLongField" sortMissingLast="true" omitNorms="true"/>
    <fieldType name="sfloat" class="solr.SortableFloatField" sortMissingLast="true" omitNorms="true"/>
    <fieldType name="sdouble" class="solr.SortableDoubleField" sortMissingLast="true" omitNorms="true"/>

    <!--
     Numeric field types that index each value at various levels of precision
     to accelerate range queries when the number of values between the range
     endpoints is large. See the javadoc for NumericRangeQuery for internal
     implementation details.

     Smaller precisionStep values (specified in bits) will lead to more tokens
     indexed per value, slightly larger index size, and faster range queries.
     A precisionStep of 0 disables indexing at different precision levels.
    -->
    <fieldType name="tint" class="solr.TrieIntField" precisionStep="8" omitNorms="true" positionIncrementGap="0"/>
    <fieldType name="tfloat" class="solr.TrieFloatField" precisionStep="8" omitNorms="true" positionIncrementGap="0"/>
    <fieldType name="tlong" class="solr.TrieLongField" precisionStep="8" omitNorms="true" positionIncrementGap="0"/>
    <fieldType name="tdouble" class="solr.TrieDoubleField" precisionStep="8" omitNorms="true" positionIncrementGap="0"/>


    <!-- The format for this date field is of the form 1995-12-31T23:59:59Z, and
         is a more restricted form of the canonical representation of dateTime
         http://www.w3.org/TR/xmlschema-2/#dateTime
         The trailing "Z" designates UTC time and is mandatory.
         Optional fractional seconds are allowed: 1995-12-31T23:59:59.999Z
         All other components are mandatory.

         Expressions can also be used to denote calculations that should be
         performed relative to "NOW" to determine the value, ie...

               NOW/HOUR
                  ... Round to the start of the current hour
               NOW-1DAY
                  ... Exactly 1 day prior to now
               NOW/DAY+6MONTHS+3DAYS
                  ... 6 months and 3 days in the future from the start of
                      the current day

         Consult the DateField javadocs for more information.
      -->
    <fieldType name="date" class="solr.DateField" sortMissingLast="true" omitNorms="true"/>

    <!-- A Trie based date field for faster date range queries and date faceting. -->
    <fieldType name="tdate" class="solr.TrieDateField" omitNorms="true" precisionStep="6" positionIncrementGap="0"/>

    <!-- solr.TextField allows the specification of custom text analyzers
         specified as a tokenizer and a list of token filters. Different
         analyzers may be specified for indexing and querying.

         The optional positionIncrementGap puts space between multiple fields of
         this type on the same document, with the purpose of preventing false phrase
         matching across fields.

         For more info on customizing your analyzer chain, please see
         http://wiki.apache.org/solr/AnalyzersTokenizersTokenFilters
     -->

    <!-- One can also specify an existing Analyzer class that has a
         default constructor via the class attribute on the analyzer element
    <fieldType name="text_greek" class="solr.TextField">
      <analyzer class="org.apache.lucene.analysis.el.GreekAnalyzer"/>
    </fieldType>
    -->

    <!-- A text field that only splits on whitespace for exact matching of words -->
    <fieldType name="sorting" class="solr.TextField" positionIncrementGap="100" sortMissingLast="true">
      <analyzer>
        <charFilter class="solr.MappingCharFilterFactory" mapping="mapping-ISOLatin1Accent.txt"/>
        <tokenizer class="solr.KeywordTokenizerFactory"/>
        <filter class="solr.LowerCaseFilterFactory"/>
      </analyzer>
    </fieldType>

    <!-- A text field that only splits on whitespace for exact matching of words -->
    <fieldType name="text_ws" class="solr.TextField" positionIncrementGap="100">
      <analyzer>
        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        <filter class="solr.LowerCaseFilterFactory"/>
      </analyzer>
    </fieldType>

    <!-- A text field that uses WordDelimiterFilter to enable splitting and matching of
        words on case-change, alpha numeric boundaries, and non-alphanumeric chars,
        so that a query of "wifi" or "wi fi" could match a document containing "Wi-Fi".
        Synonyms and stopwords are customized by external files, and stemming is enabled.
        Duplicate tokens at the same position (which may result from Stemmed Synonyms or
        WordDelim parts) are removed.
        -->
    <fieldType name="text" class="solr.TextField" positionIncrementGap="100">
      <analyzer type="index">
        <charFilter class="solr.MappingCharFilterFactory" mapping="mapping-ISOLatin1Accent.txt"/>
        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        <!-- in this example, we will only use synonyms at query time
        <filter class="solr.SynonymFilterFactory" synonyms="index_synonyms.txt" ignoreCase="true" expand="false"/>
        -->
        <!-- Case insensitive stop word removal.
          add enablePositionIncrements=true in both the index and query
          analyzers to leave a "gap" for more accurate phrase queries.
        -->
        <filter class="solr.StopFilterFactory"
                ignoreCase="true"
                words="stopwords.txt"
                enablePositionIncrements="true"
                />
        <filter class="solr.WordDelimiterFilterFactory"
                protected="protwords.txt"
                generateWordParts="1"
                generateNumberParts="1"
                catenateWords="1"
                catenateNumbers="1"
                catenateAll="0"
                splitOnCaseChange="1"
                preserveOriginal="1"/>
        <filter class="solr.LowerCaseFilterFactory"/>
        <filter class="solr.SnowballPorterFilterFactory" language="English" protected="protwords.txt"/>
        <filter class="solr.RemoveDuplicatesTokenFilterFactory"/>
      </analyzer>
      <analyzer type="query">
        <charFilter class="solr.MappingCharFilterFactory" mapping="mapping-ISOLatin1Accent.txt"/>
        <tokenizer class="solr.WhitespaceTokenizerFactory"/>
        <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
        <filter class="solr.StopFilterFactory"
                ignoreCase="true"
                words="stopwords.txt"
                enablePositionIncrements="true"
                />
        <filter class="solr.WordDelimiterFilterFactory"
                protected="protwords.txt"
                generateWordParts="1"
                generateNumberParts="1"
                catenateWords="0"
                catenateNumbers="0"
                catenateAll="0"
                splitOnCaseChange="1"
                preserveOriginal="1"/>
        <filter class="solr.LowerCaseFilterFactory"/>
        <filter class="solr.SnowballPorterFilterFactory" language="English" protected="protwords.txt"/>
        <filter class="solr.RemoveDuplicatesTokenFilterFactory"/>
      </analyzer>
    </fieldType>


    <!-- Edge N gram type - for example for matching against queries with results 
        KeywordTokenizer leaves input string intact as a single term.
        see: http://www.lucidimagination.com/blog/2009/09/08/auto-suggest-from-popular-queries-using-edgengrams/
   -->
    <fieldType name="edge_n2_kw_text" class="solr.TextField" positionIncrementGap="100">
     <analyzer type="index">
       <tokenizer class="solr.KeywordTokenizerFactory"/>
       <filter class="solr.LowerCaseFilterFactory"/>
       <filter class="solr.EdgeNGramFilterFactory" minGramSize="2" maxGramSize="25" />
     </analyzer>
     <analyzer type="query">
       <tokenizer class="solr.KeywordTokenizerFactory"/>
       <filter class="solr.LowerCaseFilterFactory"/>
     </analyzer>
    </fieldType>
   <!--  Setup simple analysis for spell checking -->
    
   <fieldType name="textSpell" class="solr.TextField" positionIncrementGap="100">
     <analyzer>
       <tokenizer class="solr.StandardTokenizerFactory" />
       <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt"/>
       <filter class="solr.LengthFilterFactory" min="4" max="20" />
       <filter class="solr.LowerCaseFilterFactory" /> 
       <filter class="solr.RemoveDuplicatesTokenFilterFactory" /> 
     </analyzer>
   </fieldType>
  
    <!-- This is an example of using the KeywordTokenizer along
         With various TokenFilterFactories to produce a sortable field
         that does not include some properties of the source text
      -->
    <fieldType name="sortString" class="solr.TextField" sortMissingLast="true" omitNorms="true">
      <analyzer>
        <!-- KeywordTokenizer does no actual tokenizing, so the entire
             input string is preserved as a single token
          -->
        <tokenizer class="solr.KeywordTokenizerFactory"/>
        <!-- The LowerCase TokenFilter does what you expect, which can be
             when you want your sorting to be case insensitive
          -->
        <filter class="solr.LowerCaseFilterFactory" />
        <!-- The TrimFilter removes any leading or trailing whitespace -->
        <filter class="solr.TrimFilterFactory" />
        <!-- The PatternReplaceFilter gives you the flexibility to use
             Java Regular expression to replace any sequence of characters
             matching a pattern with an arbitrary replacement string,
             which may include back refrences to portions of the orriginal
             string matched by the pattern.

             See the Java Regular Expression documentation for more
             infomation on pattern and replacement string syntax.

             http://java.sun.com/j2se/1.5.0/docs/api/java/util/regex/package-summary.html

        <filter class="solr.PatternReplaceFilterFactory"
                pattern="(^\p{Punct}+)" replacement="" replace="all"
        />
        -->
      </analyzer>
    </fieldType>

    <!-- A random sort type -->
    <fieldType name="rand" class="solr.RandomSortField" indexed="true" />

    <!-- since fields of this type are by default not stored or indexed, any data added to
         them will be ignored outright
     -->
    <fieldtype name="ignored" stored="false" indexed="false" class="solr.StrField" />

 </types>


 <fields>
   <!-- Valid attributes for fields:
     name: mandatory - the name for the field
     type: mandatory - the name of a previously defined type from the <types> section
     indexed: true if this field should be indexed (searchable or sortable)
     stored: true if this field should be retrievable
     compressed: [false] if this field should be stored using gzip compressionadvanced_search
       (this will only apply if the field type is compressable; among
       the standard field types, only TextField and StrField are)
     multiValued: true if this field may contain multiple values per document
     omitNorms: (expert) set to true to omit the norms associated with
       this field (this disables length normalization and index-time
       boosting for the field, and saves some memory).  Only full-text
       fields or fields that need an index-time boost need norms.
   -->

<!-- The document id is derived from a site-spcific key (hash) and the entity type and ID like:
     $document->id = $hash . "/node/" . $node->nid; -->

<!-- TODO: insert  multiValued="true"  etc... -->

    <field name="id"   multiValued="false"  type="string" indexed="true" stored="true" required="true" />
    
   <field name="entity_id"  multiValued="false" type="long" indexed="true" stored="true" required="true" />
   <!-- entity_type is "node", "file", "user", or some other Drupal object type -->
   <field name="entity_type"  multiValued="false" type="string" indexed="true" stored="true" required="true" />

   <field name="label" type="text" indexed="true" termVectors="false" stored="true" multiValued="true"/>
   <field name="all" type="text" indexed="true" termVectors="false" stored="false" multiValued="true"/>

   

</fields>

 <uniqueKey>id</uniqueKey>

 <!-- field for the QueryParser to use when an explicit fieldname is absent -->
 <defaultSearchField>all</defaultSearchField>

 <!-- SolrQueryParser configuration: defaultOperator="AND|OR" -->
 <solrQueryParser defaultOperator="OR"/>

</schema>
'
  );
  $xp = new DOMXPath($ret);
  $fields = $xp->query("/*/fields", $ret)->item(0);
  foreach($data as $field) {
    $fldNd = $ret->createElement("field");
    $fldNd->setAttribute("name", $field["Field"]);
    $fldNd->setAttribute("type", "text");
    $fldNd->setAttribute("indexed", "true");
    $fldNd->setAttribute("multiValued", "true");
    $fldNd->setAttribute("termVectors", "false");
    $fldNd->setAttribute("stored", "true");
    $fldNd->setAttribute("required", "false");
    $fields->appendChild($fldNd);
    $fields->appendChild($ret->createTextNode("\n    "));
    
    if($field['Facet'] != "" && $field['Facet'] != "N") {
      $fctNd = $ret->createElement("field");
      $fctNd->setAttribute("name", "$field[Field]__facet");
      $fctNd->setAttribute("type", "string");
      $fctNd->setAttribute("indexed", "true");
      $fctNd->setAttribute("multiValued", "true");
      $fctNd->setAttribute("termVectors", "false");
      $fctNd->setAttribute("stored", "false");
      $fctNd->setAttribute("required", "false");
      $fields->appendChild($fctNd);
      $fields->appendChild($ret->createTextNode("\n    "));
      
      $cpFct = $ret->createElement("copyField");
      $cpFct->setAttribute("source", $field["Field"]);
      $cpFct->setAttribute("dest", "$field[Field]__facet");
      $fields->appendChild($cpFct);
      $fields->appendChild($ret->createTextNode("\n\n    "));
    }
    if($field['Sort'] != 0) {
      $srtNd = $ret->createElement("field");
      $srtNd->setAttribute("name", "$field[Field]__sort");
      $srtNd->setAttribute("type", "sorting");
      $srtNd->setAttribute("indexed", "true");
      $srtNd->setAttribute("multiValued", "false");
      $srtNd->setAttribute("termVectors", "false");
      $srtNd->setAttribute("stored", "false");
      $srtNd->setAttribute("required", "false");
      $fields->appendChild($srtNd);
      $fields->appendChild($ret->createTextNode("\n    "));
    }

    $cpAll = $ret->createElement("copyField");
    $cpAll->setAttribute("source", $field["Field"]);
    $cpAll->setAttribute("dest", "all");
    
    
    $fields->appendChild($cpAll);
    $fields->appendChild($ret->createTextNode("\n    "));
  }
  return $ret;
}


function metadata_create_dataimport_config($data) {
  $ret = new DOMDocument();
  $ret->loadXML(
'<'.'?xml version="1.0" encoding="UTF-8" ?'.'>
<dataConfig>
        <dataSource type="ContentStreamDataSource" connectionTimeout="300000" readTimeout="400000" />
        <document>                      
                <entity name="mets-ese"
                
                      processor="XPathEntityProcessor"
                      stream="false"
                      forEach="/mets"
                      onError ="continue">
                      
      <field column="id" xpath="/mets/@_id"/>                  
      <field column="entity_id" xpath="/mets/@_entity_id"/>                  
      <field column="entity_type" xpath="/mets/@_entity_type"/>                  
      <field column="label" xpath="/mets/@_label"/>                  
      

                </entity>
        </document>
</dataConfig>
'  );
  $xp = new DOMXPath($ret);
  $entity = $xp->query("/dataConfig/document/entity", $ret)->item(0);
  foreach($data as $field) {
    $fldNd = $ret->createElement("field");
    $fldNd->setAttribute("column", $field["Field"]);
    $fldNd->setAttribute("xpath", $field["Field-XPath"]);
    $entity->appendChild($fldNd);
    $entity->appendChild($ret->createTextNode("\n      "));
  }
  return $ret;
}


/**
 * The XSL takes a drupal metadata node and transforms it
 * into something like:
 * &lt;doc>
 *   &lt;field name="title"> My special thingy &lt;/field>
 *   ...
 * &lt;/doc>
 *
 * @return DOMDocument of an XSL transforming a drupal node into Solr fields
 */
function metadata_create_data_transformer($data) {
  $ret = new DOMDocument();
  $ret->loadXML(
'<'.'?xml version="1.0" encoding="UTF-8" ?'.'>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      version="1.0"
      exclude-result-prefixes="xs">    
    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
    <xsl:template match="/">  
<doc>
  <field name="id"><xsl:value-of select="/*/@_id"/></field>
  <field name="entity_id"><xsl:value-of select="/*/@_entity_id"/></field>                
  <field name="entity_type"><xsl:value-of select="/*/@_entity_type"/></field>
  <field name="label"><xsl:value-of select="/*/@_label"/></field>
</doc>
    </xsl:template>
</xsl:stylesheet>
' );
  $nsxsl = 'http://www.w3.org/1999/XSL/Transform';
  $xp = new DOMXPath($ret);
  $xp->registerNamespace('xsl', $nsxsl);
  $doc = $xp->query("/xsl:stylesheet/xsl:template[@match='/']/doc", $ret)->item(0);
  $exclAlia = $ret->documentElement->getAttribute("exclude-result-prefixes");
  foreach($data as $field) {
    $nsdecl = $field["Namespaces"];
    if(!empty($nsdecl)) {
      list($alias, $value) = array_map('trim', explode('=', $nsdecl, 2));
      $ret->documentElement->setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:$alias", $value);
      $exclAlia .= " $alias";
    }
  }
  $ret->documentElement->setAttribute("exclude-result-prefixes", $exclAlia);
  
  foreach($data as $field) {
    $foreach = $ret->createElementNS($nsxsl, "xsl:for-each");
    $foreach->setAttribute("select", $field["Field-XPath"]);
    $doc->appendChild($foreach);
    $fldNd =  $ret->createElement("field");
    $fldNd->setAttribute("name", $field["Field"]);
    $foreach->appendChild($fldNd);
    $valueof = $ret->createElementNS($nsxsl, "xsl:value-of");
    $valueof->setAttribute("select", '.');
    $fldNd->appendChild($valueof);
    $doc->appendChild($ret->createTextNode("\n  "));
    if(is_numeric($field['Sort']) && $field['Sort'] > 0) {
      $fldNd =  $ret->createElement("field");
      $fldNd->setAttribute("name", "$field[Field]__sort");
      $doc->appendChild($fldNd);
      $valueof = $ret->createElementNS($nsxsl, "xsl:value-of");
      $valueof->setAttribute("select", $field["Field-XPath"]."[.!=''][position()=$field[Sort]]");
      $fldNd->appendChild($valueof);
      $doc->appendChild($ret->createTextNode("\n  "));
    }
  }
  return $ret; 
}
