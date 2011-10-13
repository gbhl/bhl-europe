<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:dc="http://purl.org/dc/elements/1.1/" 
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:europeana="http://www.europeana.eu/schemas/ese/"
    xmlns:marc="http://www.loc.gov/MARC21/slim" 
    exclude-result-prefixes="xs" 
    version="1.0">
    
    <xsl:variable name="recordToDisplay" select="(//mods:mods | //europeana:record)[1]"/>
    <xsl:variable name="thumbnailLocation" select="($recordToDisplay/europeana:object)[1]"/>
    <xsl:variable name="thumbnailUrl">
        <xsl:choose>
            <xsl:when test="$thumbnailLocation"><xsl:value-of select="$thumbnailLocation"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="'sites/all/modules/ait/metadata/view_xsl/default_thumbnail.gif'"/></xsl:otherwise>
        </xsl:choose>       
    </xsl:variable>
           
</xsl:stylesheet>
