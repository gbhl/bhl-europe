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
    <!-- ignore everything by default -->
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
        <xsl:variable name="ind1" select="@ind1" />
        
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
                    <xsl:with-param name="src_code" select="'abcdefghivz'" />
                    <xsl:with-param name="dest_code" select="'ahabbccnph'" />
                </xsl:call-template>
            </xsl:when>
            <!-- publication / 210 => 260 -->
            <xsl:when test="$tag = '210'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'260'" />
                    <xsl:with-param name="src_code" select="'acdegh'" />
                    <xsl:with-param name="dest_code" select="'abcefg'" />
                </xsl:call-template>
            </xsl:when>
            <!-- continues / 430 => 780 -->
            <xsl:when test="$tag = '430'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'780'" />
                    <xsl:with-param name="src_code" select="'3tvxy'" />
                    <xsl:with-param name="dest_code" select="'wtgxz'" />
                </xsl:call-template>
            </xsl:when>
            <!-- merged with .. to form / 447 => 785 -->
            <xsl:when test="$tag = '447'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'785'" />
                    <xsl:with-param name="src_code" select="'13tvxy'" />
                    <xsl:with-param name="dest_code" select="'wtgxz'" />
                </xsl:call-template>
            </xsl:when>
            <!-- key title / 530 => 222 -->
            <xsl:when test="$tag = '530'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'222'" />
                    <xsl:with-param name="src_code" select="'abj'" />
                    <xsl:with-param name="dest_code" select="'abb'" />
                </xsl:call-template>
            </xsl:when>
            <!-- abbreviated title / 531 => 210 -->
            <xsl:when test="$tag = '531'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'210'" />
                    <xsl:with-param name="src_code" select="'ab'" />
                </xsl:call-template>
            </xsl:when>
            <!-- universal decimal classification number / 675 => 080 -->
            <xsl:when test="$tag = '675'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'080'" />
                    <xsl:with-param name="src_code" select="'a'" />
                </xsl:call-template>
            </xsl:when>
            <!-- corporate name / 710 => 110/111 -->
            <xsl:when test="$tag = '710'">
                <xsl:choose>
                    <xsl:when test="$ind1 = '1'">
                        <xsl:call-template name="translate_datafield">
                            <xsl:with-param name="dest_tag" select="'111'" />
                            <xsl:with-param name="src_code" select="'abcdefp4'" />
                            <xsl:with-param name="dest_code" select="'abancdu4'" />
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="translate_datafield">
                            <xsl:with-param name="dest_tag" select="'110'" />
                            <xsl:with-param name="src_code" select="'abcdefp4'" />
                            <xsl:with-param name="dest_code" select="'abancdu4'" />
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <!-- corporate name / 711 => 710/711 -->
            <xsl:when test="$tag = '711'">
                <xsl:choose>
                    <xsl:when test="$ind1 = '1'">
                        <xsl:call-template name="translate_datafield">
                            <xsl:with-param name="dest_tag" select="'711'" />
                            <xsl:with-param name="src_code" select="'abcdefp4'" />
                            <xsl:with-param name="dest_code" select="'abancdu4'" />
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="translate_datafield">
                            <xsl:with-param name="dest_tag" select="'710'" />
                            <xsl:with-param name="src_code" select="'abcdefp4'" />
                            <xsl:with-param name="dest_code" select="'abancdu4'" />
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <!-- originating source / 801 => 040 -->
            <xsl:when test="$tag = '801'">
                <xsl:call-template name="translate_datafield">
                    <xsl:with-param name="dest_tag" select="'040'" />
                    <xsl:with-param name="src_code" select="'abcg'" />
                    <xsl:with-param name="dest_code" select="'aaae'" />
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
            <xsl:with-param name="src_code" select="$src_code" />
            <xsl:with-param name="dest_code" select="$dest_code" />
        </xsl:call-template>
    </xsl:template>
    
    <!-- create a subfield -->
    <xsl:template name="create_subfield">
        <xsl:param name="code" />
        <xsl:param name="content" />
        
        <!-- subfield element -->
        <xsl:element name="marc:subfield">
            <xsl:attribute name="marc:code">
                <xsl:value-of select="$code" />
            </xsl:attribute>
            <xsl:value-of select="$content" />
        </xsl:element>
    </xsl:template>
    
    <!-- helper template for creating arbitrary datafields -->
    <xsl:template name="create_datafield">
        <xsl:param name="tag" />
        <xsl:param name="src_code" />
        <xsl:param name="dest_code" select="$src_code" />
        
        <!-- create wrapping datafield -->
        <xsl:element name="marc:datafield">
            <xsl:attribute name="marc:tag">
                <xsl:value-of select="$tag" />
            </xsl:attribute>
            <!-- create subfield entries -->
            <xsl:for-each select="marc:subfield">
                <xsl:if test="contains($src_code, @code)" >
                    <xsl:call-template name="create_subfield">
                        <xsl:with-param name="code" select="translate(@code, $src_code, $dest_code)" />
                        <xsl:with-param name="content" select="." />
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>
