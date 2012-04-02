<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:dc="http://purl.org/dc/elements/1.1/">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:variable name="title"></xsl:variable>

    <xsl:template match="/">
        <dc-record xmlns:dc="http://purl.org/dc/elements/1.1/">
            <xsl:element name="dc:title">##title##</xsl:element>
            <xsl:apply-templates select="*//oai_dc:dc/*" />
        </dc-record>
    </xsl:template>
    
        <xsl:template match="*">
            <xsl:choose>
                <xsl:when test="local-name(.) = 'title'"><!-- SKIP --></xsl:when>
                <xsl:when test="local-name(.) = 'format'"><!-- SKIP --></xsl:when>
                <xsl:when test="local-name(.) = 'identifier'">
                    <xsl:element name="dc:isPartOf">
                        <xsl:apply-templates/>
                    </xsl:element>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:element name="dc:{local-name(.)}">
                        <xsl:apply-templates/>
                    </xsl:element>
                </xsl:otherwise>
            </xsl:choose>
            
            
    </xsl:template>
    
    <xsl:template match="@*">
        <xsl:copy/>
    </xsl:template>
    

</xsl:stylesheet>
