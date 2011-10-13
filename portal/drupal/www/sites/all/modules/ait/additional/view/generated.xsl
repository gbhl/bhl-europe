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
                    select="normalize-space($translations/terms/term[@text = $term]/translation[@language = $language]/@text)"/>
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
            <xsl:if test="dc:title">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Title'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:title">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
            <xsl:if test="dcterms:alternative">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Other Title'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dcterms:alternative">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:creator">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Author'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:creator">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
            <xsl:if test="dc:contributor">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Contributor'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:contributor">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:date">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Date'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:date">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
            <xsl:if test="dcterms:created">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Date Created'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dcterms:created">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:type">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Type'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:type">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:publisher | dcterms:issued">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Publication'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:publisher">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                     <xsl:for-each select="dcterms:issued">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:language">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Language'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:language">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:format | dcterms:extent">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Physical Description'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:format">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                     <xsl:for-each select="dcterms:extent">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:description">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Abstract'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:description">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
            <xsl:if test="dcterms:tableOfContents">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Table of Contents'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dcterms:tableOfContents">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:subject | dcterms:spatial | dcterms:temporal">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Subject'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:subject">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                     <xsl:for-each select="dcterms:spatial">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                     <xsl:for-each select="dcterms:temporal">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:identifier">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Identification'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:identifier">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="europeana:dataProvider | europeana:isShownAt">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Holding Information'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="europeana:dataProvider">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                     <xsl:for-each select="europeana:isShownAt">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:rights | europeana:rights">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Rights'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:rights">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                     <xsl:for-each select="europeana:rights">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:relation">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Related Items'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:relation">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="dc:description">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Notes'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="dc:description">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="newSection">
         <xsl:with-param name="data">
            <xsl:if test="europeana:isShownBy">
               <tr>
                  <td class="metadata-view-display">
                     <xsl:call-template name="translate">
                        <xsl:with-param name="term" select="'Digital Object'"/>
                     </xsl:call-template>
                  </td>
                  <td class="metadata-view-text">
                     <xsl:for-each select="europeana:isShownBy">
                        <p>
                           <xsl:value-of select="."/>
                        </p>
                     </xsl:for-each>
                  </td>
               </tr>
            </xsl:if>
         </xsl:with-param>
      </xsl:call-template>
   </xsl:template>
</xsl:stylesheet>