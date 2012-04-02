<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:dc="http://purl.org/dc/elements/1.1/">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/">
        <dc-record xmlns:dc="http://purl.org/dc/elements/1.1/">         
        <xsl:apply-templates select="*//oai_dc:dc/*" />
        </dc-record>
    </xsl:template>
    
    <xsl:template match="*">
        <xsl:element name="dc:{local-name(.)}">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="@*">
        <xsl:copy/>
    </xsl:template>
    

</xsl:stylesheet>
