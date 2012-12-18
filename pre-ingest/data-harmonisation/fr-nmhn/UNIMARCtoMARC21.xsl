<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : UNIMARCtoMARC21.xsl
    Created on : 14. Dezember 2012, 16:05
    Author     : wkoller
    Description:
        Transforming UNIMARC-XML to MARC21-XML
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:marc="http://www.loc.gov/MARC21/slim">
    <xsl:output method="xml" encoding="utf-8" indent="yes" />
    <!--<xsl:template match="marc:*">
        <xsl:copy>
            <xsl:copy-of select="@*" />
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>-->
    
    <xsl:template match="*"></xsl:template>
    
    <xsl:template match="marc:collection">
        <xsl:element name="marc:collection">
            <xsl:for-each select="marc:record">
                <xsl:element name="marc:record">
                    <xsl:for-each select="marc:datafield">
                        <xsl:call-template name="datafield" />
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
            <xsl:apply-templates />
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="datafield">
        <xsl:variable name="tag" select="@tag" />
        
        <!--
        Tag debugging
        <xsl:element name="tag">
            <xsl:value-of select="$tag" />
        </xsl:element>
        -->
        
        <xsl:choose>
            <!-- issn / 011 => 022 -->
            <xsl:when test="$tag = '011'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'022'" />
                    <xsl:with-param name="src_code" select="'a'" />
                </xsl:call-template>
            </xsl:when>
            <!-- title / 200 => 245 -->
            <xsl:when test="$tag = '200'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'245'" />
                    <xsl:with-param name="src_code" select="'a'" />
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <!-- translate a given datafield into a new one -->
    <xsl:template name="translate_datafield">
        <xsl:param name="dest_tag" />
        <xsl:param name="src_code" />
        <xsl:param name="dest_code" select="$src_code" />

        <!-- create translated datafield entry -->        
        <xsl:call-template name="create_datafield">
            <xsl:with-param name="tag" select="$dest_tag" />
            <xsl:with-param name="code" select="$dest_code" />
            <xsl:with-param name="content" select="marc:subfield[@code=$src_code]" />
        </xsl:call-template>
    </xsl:template>
    
    <!-- helper template for creating arbitrary datafields -->
    <xsl:template name="create_datafield">
        <xsl:param name="tag" />
        <xsl:param name="code" />
        <xsl:param name="content" />
        
        <!-- create wrapping datafield -->
        <xsl:element name="marc:datafield">
            <xsl:attribute name="marc:tag">
                <xsl:value-of select="$tag" />
            </xsl:attribute>
            <!-- create subfield entry -->
            <xsl:element name="marc:subfield">
                <xsl:attribute name="marc:code">
                    <xsl:value-of select="$code" />
                </xsl:attribute>
                <!-- assign content to subfield -->
                <xsl:value-of select="$content" />
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>
