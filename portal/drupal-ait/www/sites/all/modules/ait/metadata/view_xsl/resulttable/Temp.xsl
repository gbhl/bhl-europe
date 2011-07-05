<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:mods="http://www.loc.gov/mods/v3"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:dcterms="http://purl.org/dc/terms/"
      xmlns:europeana="http://www.europeana.eu/schemas/ese/"
      exclude-result-prefixes="xs"
      version="1.0">    
    
    <xsl:import href="../utilities.xsl"/>     
    <xsl:output method="html" indent="yes"/>



	<xsl:template match="/">
		<table class="advanced-search-table-result">
		    <tr><td class="advanced-search-table-result-picture-cell"><image src="{$thumbnailUrl}"/></td></tr>
		</table>
	    <div id="title"><xsl:copy-of select="$recordToDisplay/dc:title/child::node()"/></div> 
	    <div id="provider">><xsl:copy-of select="$recordToDisplay/europeana:dataProvider/child::node()"/></div>	        
        <xsl:if test="$recordToDisplay/europeana:isShownAt">
            <div id="shown"><xsl:copy-of select="$recordToDisplay/europeana:isShownAt/child::node()"/></div>
        </xsl:if>    

	</xsl:template>
	

    
</xsl:stylesheet>
