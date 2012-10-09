<?xml version="1.0" encoding="UTF-8"?>
<!--
    Document   : OLEFUpdate.xsl
    Created on : 09. Oktober 2012, 09:09
    Author     : wkoller
    Description:
        Transform old OLEF documents to latest versions
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:olef03="http://www.bhl-europe.eu/bhl-schema/v0.3/" xmlns:olef="http://www.bhl-europe.eu/bhl-schema/v1/" xmlns:dwc="http://rs.tdwg.org/dwc/terms/" xmlns:mix="http://www.loc.gov/mix/v20" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:o-ex="http://odrl.net/1.1/ODRL-EX">
    <!-- upgrade old olef namespace entries (since structure didn't change significantly) -->
    <xsl:template match="olef03:*" priority="0">
        <xsl:element name="olef:{local-name()}">
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <!-- copy all elements by default -->
    <xsl:template match="*">
        <xsl:copy>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
