<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
    xmlns:fo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"
    xmlns:xlink="http://www.w3.org/1999/xlink" 
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:meta="urn:oasis:names:tc:opendocument:xmlns:meta:1.0"
    xmlns:number="urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0"
    xmlns:presentation="urn:oasis:names:tc:opendocument:xmlns:presentation:1.0"
    xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0"
    xmlns:chart="urn:oasis:names:tc:opendocument:xmlns:chart:1.0"
    xmlns:dr3d="urn:oasis:names:tc:opendocument:xmlns:dr3d:1.0"
    xmlns:math="http://www.w3.org/1998/Math/MathML"
    xmlns:form="urn:oasis:names:tc:opendocument:xmlns:form:1.0"
    xmlns:script="urn:oasis:names:tc:opendocument:xmlns:script:1.0"
    xmlns:ooo="http://openoffice.org/2004/office" 
    xmlns:ooow="http://openoffice.org/2004/writer"
    xmlns:oooc="http://openoffice.org/2004/calc" 
    xmlns:dom="http://www.w3.org/2001/xml-events"
    xmlns:xforms="http://www.w3.org/2002/xforms" 
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:rpt="http://openoffice.org/2005/report"
    xmlns:of="urn:oasis:names:tc:opendocument:xmlns:of:1.2"
    xmlns:xhtml="http://www.w3.org/1999/xhtml" 
    xmlns:grddl="http://www.w3.org/2003/g/data-view#"
    xmlns:tableooo="http://openoffice.org/2009/table"
    xmlns:field="urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0"
    xmlns:formx="urn:openoffice:names:experimental:ooxml-odf-interop:xmlns:form:1.0"  
    
    xmlns:xsl_result="new_xsl"    
    
    version="2.0">
    
    <xsl:output method="xml" indent="yes"/>
    <xsl:variable name="table" select="/office:document-content/office:body[1]/office:spreadsheet[1]/table:table[1]"/>    
    
    <xsl:namespace-alias stylesheet-prefix="xsl_result" result-prefix="xsl"/>
    
    <xsl:template name="writeFromODS">
        
        <xsl:for-each-group select="$table/table:table-row[position() &gt; 2]" group-by="table:table-cell[1]/text:p[1]">  <!-- sections -->                       
             
            <xsl_result:call-template name="newSection">
                <xsl_result:with-param name="data">
            
                    <xsl:for-each-group select="current-group()" group-by="table:table-cell[2]/text:p[1]">  <!-- display fields -->
 
                        <xsl:variable name="fields" as="xs:string*">  <!-- fields -->   
                            <xsl:for-each select="current-group()">                                                                                                              
                                <xsl:sequence select="concat(table:table-cell[3]/text:p[1], table:table-cell[4]/text:p[1])"/>                                     
                            </xsl:for-each>
                        </xsl:variable>
                           
                        <xsl_result:if test="{string-join($fields, ' | ')}">
                            <tr>                            
                                <td class="metadata-view-display">
                                    <xsl_result:call-template name="translate">
                                        <xsl_result:with-param name="term" select="'{normalize-space(table:table-cell[2]/text:p[1])}'"/>
                                    </xsl_result:call-template>
                                </td>        
                                <td class="metadata-view-text">
                                    <xsl:for-each select="$fields">
                                        <xsl_result:for-each select="{.}">    
                                            <p>
                                                <xsl_result:value-of select="."/>
                                            </p>                                  
                                        </xsl_result:for-each>                                         
                                    </xsl:for-each>
                                </td>
                            </tr>
                        </xsl_result:if>
                      
                    </xsl:for-each-group>

                </xsl_result:with-param>
            </xsl_result:call-template>
            
        </xsl:for-each-group>       
        
    </xsl:template>
    
    
    
    <xsl:template match="/">

        <xsl_result:stylesheet 
            xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
            xmlns:xs="http://www.w3.org/2001/XMLSchema" 
            xmlns:mods="http://www.loc.gov/mods/v3"
            xmlns:dc="http://purl.org/dc/elements/1.1/" 
            xmlns:dcterms="http://purl.org/dc/terms/"
            xmlns:europeana="http://www.europeana.eu/schemas/ese/"
            xmlns:marc="http://www.loc.gov/MARC21/slim" 
            version="1.0">
            
            <xsl_result:import href="../utilities.xsl"/>    
            <xsl_result:output method="html"/>
            <xsl_result:param name="language"/>
            
            
            <xsl_result:variable name="translations" select="document('../Translations.xml')"/>
                        
            
            <!-- common -->
            
            <xsl_result:template match="/">
                <table cellspacing="0px" class="metadata-view">
                    <tr>
                        <td class="metadata-view-picture-cell">
                            <img src="{{$thumbnailUrl}}"/>
                        </td>
                        <td class="metadata-view-data-cell">
                            <table cellspacing="0px">
                                <xsl_result:apply-templates select="$recordToDisplay"/>
                            </table>
                        </td>
                    </tr>
                </table>
            </xsl_result:template>
            
            <xsl_result:template name="writeData">
                <xsl_result:param name="display"/>
                <!-- TODO following selects empty node-set, can this be done more elegant? -->
                <xsl_result:param name="texts" select="null"/>
                <xsl_result:param name="links" select="null"/>
                <xsl_result:for-each select="$texts">
                    <tr>
                        <xsl_result:if test=". = $texts[1]">
                            <td class="metadata-view-display" rowspan="{{count($texts | $links)}}">
                                <xsl_result:call-template name="translate">
                                    <xsl_result:with-param name="term" select="$display"/>
                                </xsl_result:call-template>
                            </td>
                        </xsl_result:if>         
                        <td class="metadata-view-text">
                            <xsl_result:value-of select="."/>
                        </td>
                    </tr>
                </xsl_result:for-each>
                <xsl_result:for-each select="$links">
                    <tr>
                        <xsl_result:if test="not($texts) and . = $links[1]">
                            <td class="metadata-view-display" rowspan="{{count($texts | $links)}}">
                                <xsl_result:call-template name="translate">
                                    <xsl_result:with-param name="term" select="$display"/>
                                </xsl_result:call-template>
                            </td>
                        </xsl_result:if>                  
                        <td class="metadata-view-text">
                            <a href="{{.}}"><xsl_result:value-of select="."/></a>
                        </td>
                    </tr>
                </xsl_result:for-each>        
            </xsl_result:template>
            
            <xsl_result:template name="translate">
                <xsl_result:param name="term"/>
                <xsl_result:variable name="translation"
                    select="normalize-space($translations/terms/term[@text = $term]/translation[@language = $language]/@text)"/>
                <xsl_result:choose>
                    <xsl_result:when test="$translation">
                        <xsl_result:value-of select="$translation"/>
                    </xsl_result:when>
                    <xsl_result:otherwise>
                        <xsl_result:value-of select="$term"/>
                    </xsl_result:otherwise>
                </xsl_result:choose>
            </xsl_result:template>
            
            <xsl_result:template name="writeLine">
                <tr>
                    <td class="metadata-view-line-1"></td>
                    <td class="metadata-view-line-2"></td>
                </tr>
                <tr>
                    <td class="metadata-view-line-3"></td>
                    <td class="metadata-view-line-4"></td>
                </tr>
            </xsl_result:template>
            
            <xsl_result:template name="newSection">
                <xsl_result:param name="data"/>
                <xsl_result:if test="$data != ''">
                    <xsl_result:copy-of select="$data"/>
                    <xsl_result:call-template name="writeLine"/>
                </xsl_result:if>
            </xsl_result:template>
            
            <!-- europeana section -->
            
            <xsl_result:template match="europeana:record">
                
                <xsl:call-template name="writeFromODS"/>
                
            </xsl_result:template>
            
        </xsl_result:stylesheet>           
                               
    </xsl:template>
    
    
</xsl:stylesheet>




