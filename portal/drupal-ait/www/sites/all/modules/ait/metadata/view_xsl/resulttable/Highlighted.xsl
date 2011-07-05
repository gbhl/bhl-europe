<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:europeana="http://www.europeana.eu/schemas/ese/"
	xmlns:mets="http://www.loc.gov/METS/" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:prov="http://www.openarchives.org/OAI/2.0/provenance" 
	xmlns:oai="http://www.openarchives.org/OAI/2.0/"
  exclude-result-prefixes=""
	version="1.0">    
    
    <xsl:import href="../utilities.xsl"/>     
    <xsl:output method="html" indent="yes" omit-xml-declaration="yes"/>
    <xsl:param name="displayURI"/>

	<xsl:template match="/">
	<div class="advanced-search-grid-result">
	    <div class="thumbnail"><a href="{$displayURI}"><img src="{$thumbnailUrl}"/></a></div>
 	    <div class="grid-text">
	      <div class="grid-title"><a href="{$displayURI}">
		<xsl:choose>
			<xsl:when test="$recordToDisplay/dc:title/child::node()">
				<xsl:copy-of select="$recordToDisplay/dc:title/child::node()"/>
			</xsl:when>
			<xsl:when test="$recordToDisplay/dcterms:alternative/child::node()">
				<xsl:copy-of select="$recordToDisplay/dcterms:alternative/child::node()"/>
			</xsl:when>
			<xsl:otherwise>[...]</xsl:otherwise>
		</xsl:choose>
	      </a></div> 

		<div class="grid-creator">
			<xsl:choose>
				<xsl:when test="$recordToDisplay/dc:creator/child::node()">
					<xsl:for-each select="$recordToDisplay/dc:creator">
						<span class="grid-creator-element"><xsl:copy-of select="./child::node()"/></span>
						<xsl:if test="position() != last()">; </xsl:if>
		      			</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="$recordToDisplay/dc:contributor">
						<span class="grid-creator-element"><xsl:copy-of select="./child::node()"/></span>
						<xsl:if test="position() != last()">; </xsl:if>
		      			</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>

		</div>
		<div class="grid-date">
			<xsl:choose>
				<xsl:when test="$recordToDisplay/dc:date/child::node()">
					<xsl:for-each select="$recordToDisplay/dc:date">
						<span class="grid-date-element"><xsl:copy-of select="./child::node()"/></span>
						<xsl:if test="position() != last()">; </xsl:if>
		      			</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="$recordToDisplay/dcterms:created">
						<span class="grid-date-element"><xsl:copy-of select="./child::node()"/></span>
						<xsl:if test="position() != last()">; </xsl:if>
		      			</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>

		</div>
		<div class="grid-provider"> 
			<xsl:for-each select="$recordToDisplay/europeana:dataProvider">
				<span class="grid-provider-element"><xsl:copy-of select="./child::node()"/></span>
				<xsl:if test="position() != last()">; </xsl:if>
      			</xsl:for-each>
		</div>
 	        
            </div>

	    <xsl:if test="$recordToDisplay/europeana:isShownAt">
		<xsl:variable name="shownat">
		  <xsl:copy-of select="$recordToDisplay/europeana:isShownAt/child::node()"/>
		</xsl:variable>
                <div class="grid-shown">
		  <a class="shownat"><xsl:attribute name="href"><xsl:copy-of select="$shownat"/></xsl:attribute>.</a>
                </div>
            </xsl:if>

	</div>
	</xsl:template>
    
</xsl:stylesheet>
