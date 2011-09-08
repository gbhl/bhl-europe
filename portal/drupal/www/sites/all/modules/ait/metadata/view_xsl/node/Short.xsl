<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
      xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:mods="http://www.loc.gov/mods/v3"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:dcterms="http://purl.org/dc/terms/"
      xmlns:europeana="http://www.europeana.eu/schemas/ese/"
      exclude-result-prefixes="xs"
      version="1.0">    
    <xsl:output method="html" indent="yes"/>
    
    <xsl:variable name="maxTextCharacters" select="'100'"/>
    <xsl:variable name="maxRelatedItemTextCharacters" select="'100'"/>
    
    <xsl:template match="/">  
        <html>
            <head>
                <!-- <link rel="stylesheet" type="text/css" href="../../../../utilities/view/view.css"/> -->
            </head>
            <body>
				<table><tr>
					<!-- TODO make path configurable -->
					<td style="text-align:center"><image height="200" width="140" src="sites/all/modules/ait/metadata/view_xsl/default_thumbnail.gif"/></td>
					<td>
		                <xsl:apply-templates select="//mods:mods"/>
		                <xsl:apply-templates select="//europeana:record"/>					
					</td>
				</tr></table>            
            </body>
        </html>                
    </xsl:template>

    <xsl:template match="mods:mods">    
        <table class="mods" style="background-color:#FFFFFF">
            <tbody><xsl:apply-templates/></tbody>
        </table>
    </xsl:template>  
  

   <xsl:template match="mods:mods//*">              
        <tr><td class="textLine">
            <xsl:value-of select="local-name()"/>: <xsl:value-of select="."/>
        </td></tr>         
    </xsl:template>       




    <xsl:template match="europeana:record">    
        <table class="ese" style="background-color:#FFFFFF">
            <tbody><xsl:apply-templates/></tbody>
        </table>
    </xsl:template>  
    
    <xsl:template match="europeana:record//*">
	<xsl:if test="namespace-uri() = 'http://purl.org/dc/elements/1.1/'">

		<tr>
		    <td class="eseNode"><xsl:value-of select="local-name()"/>:</td>
		    <td><xsl:copy-of select="."/></td>
		</tr>
	</xsl:if>
    </xsl:template>
  
    

</xsl:stylesheet>
