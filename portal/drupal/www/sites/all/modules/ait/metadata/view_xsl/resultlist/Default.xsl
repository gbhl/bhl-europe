<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
      xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:mods="http://www.loc.gov/mods/v3"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:dcterms="http://purl.org/dc/terms/"
      xmlns:europeana="http://www.europeana.eu/schemas/ese/"
      exclude-result-prefixes=""
      version="1.0">    
    <xsl:output method="html" indent="yes"/>



	<xsl:template match="/">
		<table><tr>
			<td style="text-align:center"><image height="150" width="105" src="sites/all/modules/ait/metadata/view_xsl/default_thumbnail.gif"/></td>
			<td><ul>
				<li>creator: <xsl:value-of select="//dc:creator"/></li>
				<li>publisher: <xsl:value-of select="//dc:publisher"/></li>
				<li>issued: <xsl:value-of select="//dcterms:issued"/></li>
			</ul></td>
		</tr></table>
	</xsl:template>
	

    
</xsl:stylesheet>
