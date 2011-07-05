<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
                xmlns:europeana="http://www.europeana.eu/schemas/ese/"
                xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0"
                xmlns:rpt="http://openoffice.org/2005/report"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                xmlns:marc="http://www.loc.gov/MARC21/slim"
                xmlns:ooo="http://openoffice.org/2004/office"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:of="urn:oasis:names:tc:opendocument:xmlns:of:1.2"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:ooow="http://openoffice.org/2004/writer"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0"
                xmlns:oooc="http://openoffice.org/2004/calc"
                xmlns:grddl="http://www.w3.org/2003/g/data-view#"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0"
                xmlns:dom="http://www.w3.org/2001/xml-events"
                xmlns:tableooo="http://openoffice.org/2009/table"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:presentation="urn:oasis:names:tc:opendocument:xmlns:presentation:1.0"
                xmlns:xforms="http://www.w3.org/2002/xforms"
                xmlns:field="urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0"
                xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
                xmlns:formx="urn:openoffice:names:experimental:ooxml-odf-interop:xmlns:form:1.0"
                xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
                xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0"
                xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
                xmlns:mods="http://www.loc.gov/mods/v3"
                xmlns:math="http://www.w3.org/1998/Math/MathML"
                xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0"
                version="1.0">
   <xsl:import href="../utilities.xsl"/>
   <xsl:output method="html"/>
   <xsl:param name="language"/>
   <xsl:variable name="translations" select="document('../Translations.xml')"/>
   <xsl:template match="/">
      <table cellspacing="0px" class="metadata-view">
         <tr>
            <td class="metadata-view-picture-cell">
               <img src="{$thumbnailUrl}"/>
            </td>
            <td class="metadata-view-data-cell">
               <table cellspacing="0px">
                  <xsl:apply-templates select="$recordToDisplay"/>
               </table>
            </td>
         </tr>
      </table>
   </xsl:template>
   <xsl:template name="writeData">
      <xsl:param name="display"/>
      <xsl:param name="texts" select="null"/>
      <xsl:param name="links" select="null"/>
      <xsl:for-each select="$texts">
         <tr>
            <xsl:if test=". = $texts[1]">
               <td class="metadata-view-display" rowspan="{count($texts | $links)}">
                  <xsl:call-template name="translate">
                     <xsl:with-param name="term" select="$display"/>
                  </xsl:call-template>
               </td>
            </xsl:if>
            <td class="metadata-view-text">
               <xsl:value-of select="."/>
            </td>
         </tr>
      </xsl:for-each>
      <xsl:for-each select="$links">
         <tr>
            <xsl:if test="not($texts) and . = $links[1]">
               <td class="metadata-view-display" rowspan="{count($texts | $links)}">
                  <xsl:call-template name="translate">
                     <xsl:with-param name="term" select="$display"/>
                  </xsl:call-template>
               </td>
            </xsl:if>
            <td class="metadata-view-text">
               <a href="{.}">
                  <xsl:value-of select="."/>
               </a>
            </td>
         </tr>
      </xsl:for-each>
   </xsl:template>
   <xsl:template name="translate">
      <xsl:param name="term"/>
      <xsl:variable name="translation"
                    select="$translations/terms/term[@text = $term]/translation[@language = $language]/@text"/>
      <xsl:choose>
         <xsl:when test="$translation">
            <xsl:value-of select="$translation"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$term"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <xsl:template name="writeLine">
      <tr>
         <td class="metadata-view-line-1"/>
         <td class="metadata-view-line-2"/>
      </tr>
      <tr>
         <td class="metadata-view-line-3"/>
         <td class="metadata-view-line-4"/>
      </tr>
   </xsl:template>
   <xsl:template name="newSection">
      <xsl:param name="data"/>
      <xsl:if test="$data != ''">
         <xsl:copy-of select="$data"/>
         <xsl:call-template name="writeLine"/>
      </xsl:if>
   </xsl:template>
   <xsl:template match="europeana:record">
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Title'"/>
               <xsl:with-param name="texts" select="dc:title"/>
            </xsl:call-template>
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Other Title'"/>
               <xsl:with-param name="texts" select="dcterms:alternative"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Author'"/>
               <xsl:with-param name="texts" select="dc:creator"/>
            </xsl:call-template>
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Contributor'"/>
               <xsl:with-param name="texts" select="dc:contributor"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Date'"/>
               <xsl:with-param name="texts" select="dc:date"/>
            </xsl:call-template>
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Date Created'"/>
               <xsl:with-param name="texts" select="dcterms:created"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Type '"/>
               <xsl:with-param name="texts" select="dc:type"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Publication'"/>
               <xsl:with-param name="texts" select="dc:publisher | dcterms:issued"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Language'"/>
               <xsl:with-param name="texts" select="dc:language"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Physical Description'"/>
               <xsl:with-param name="texts" select="dc:format | dcterms:extent"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Abstract'"/>
               <xsl:with-param name="texts" select="dc:description"/>
            </xsl:call-template>
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Table of Contents'"/>
               <xsl:with-param name="texts" select="dcterms:tableOfContents"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Subject'"/>
               <xsl:with-param name="texts" select="dc:subject | dcterms:spatial | dcterms:temporal"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Identification'"/>
               <xsl:with-param name="texts" select="dc:identifier"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Holding Information'"/>
               <xsl:with-param name="texts" select="europeana:dataProvider | europeana:isShownAt"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Rights'"/>
               <xsl:with-param name="texts" select="dc:rights | europeana:rights"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Related Items'"/>
               <xsl:with-param name="texts" select="dc:relation"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Notes'"/>
               <xsl:with-param name="texts" select="dc:description"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:call-template name="writeData">
               <xsl:with-param name="display" select="'Digital Object'"/>
               <xsl:with-param name="texts" select="europeana:isShownBy"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
   </xsl:template>
</xsl:stylesheet>