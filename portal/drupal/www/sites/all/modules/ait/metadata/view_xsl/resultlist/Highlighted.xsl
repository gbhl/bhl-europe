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
	<xsl:output method="xml" indent="yes"/>
	<xsl:param name="displayURI"/>

	<xsl:template match="/">
		<div class="advanced-search-list-result">
	  	  <div class="advanced-search-list-result-picture-cell">
		    <a href="{$displayURI}"><img src="{$thumbnailUrl}"/></a>
		  </div>
			<div class="advanced-search-list-result-metadata">		 
			  <ul>
			  <!-- TODO modify this in order to work with MODS etc.. -->
				<li class="list-title">
					<a href="{$displayURI}">
						<xsl:choose>
							<xsl:when test="$recordToDisplay/dc:title/child::node()">
								<xsl:copy-of select="$recordToDisplay/dc:title/child::node()"/>
							</xsl:when>
							<xsl:when test="$recordToDisplay/dcterms:alternative/child::node()">
								<xsl:copy-of select="$recordToDisplay/dcterms:alternative/child::node()"/>
							</xsl:when>
							<xsl:otherwise>[...]</xsl:otherwise>
						</xsl:choose>
					</a>
				</li>			
				<li class="list-creator">
					<xsl:choose>
						<xsl:when test="$recordToDisplay/dc:creator/child::node()">
							<xsl:for-each select="$recordToDisplay/dc:creator">
								<span class="list-creator-element"><xsl:copy-of select="./child::node()"/></span>
								<xsl:if test="position() != last()">; </xsl:if>
				      			</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:for-each select="$recordToDisplay/dc:contributor">
								<span class="list-creator-element"><xsl:copy-of select="./child::node()"/></span>
								<xsl:if test="position() != last()">; </xsl:if>
				      			</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>

				</li>
				<li class="list-date">
					<xsl:choose>
						<xsl:when test="$recordToDisplay/dc:date/child::node()">
							<xsl:for-each select="$recordToDisplay/dc:date">
								<span class="list-date-element"><xsl:copy-of select="./child::node()"/></span>
								<xsl:if test="position() != last()">; </xsl:if>
				      			</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:for-each select="$recordToDisplay/dcterms:created">
								<span class="list-date-element"><xsl:copy-of select="./child::node()"/></span>
								<xsl:if test="position() != last()">; </xsl:if>
				      			</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>

				</li>
				<li class="list-provider"> 
					<xsl:for-each select="$recordToDisplay/europeana:dataProvider">
						<span class="list-provider-element"><xsl:copy-of select="./child::node()"/></span>
						<xsl:if test="position() != last()">; </xsl:if>
		      			</xsl:for-each>
				</li>
			  </ul>
			</div>
		</div>
	</xsl:template>



</xsl:stylesheet>
