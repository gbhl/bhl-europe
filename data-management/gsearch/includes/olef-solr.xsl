<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exts="xalan://dk.defxws.fedoragsearch.server.GenericOperationsImpl"
    exclude-result-prefixes="exts" xmlns:foxml="info:fedora/fedora-system:def/foxml#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/">

    <xsl:template name="mods-name-DEBUG">

        <xsl:element name="field">
            <xsl:attribute name="name">mods_name</xsl:attribute>
            <xsl:for-each select="namePart">
                <xsl:value-of select="."/>
                <xsl:if test="position()!=last()">
                    <xsl:text>, </xsl:text>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>

    </xsl:template>

    <xsl:template match="name" name="mods-name">
        
        <xsl:element name="field">
            <xsl:attribute name="name">mods_name</xsl:attribute>
            <xsl:choose>
                <xsl:when test="namePart[@type='family'] or namePart[@type='given']">
                    <xsl:if test="namePart[@type='family']">
                        <xsl:value-of select="namePart[@type='family']"/>
                    </xsl:if>
                    <xsl:if test="namePart[@type='given']">
                        <xsl:if test="namePart[@type='family']">
                            <xsl:text>, </xsl:text>
                        </xsl:if>
                        <xsl:value-of select="namePart[@type='given']"/>
                    </xsl:if>
                    <xsl:if test="namePart[@type='date']">
                        <xsl:text>, </xsl:text>
                        <xsl:value-of select="namePart[@type='date']"/>
                    </xsl:if>
                </xsl:when>
                
                <!-- if only namePart no specific family or given name tags -->
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="namePart != ''">
                            <xsl:for-each select="namePart">
                                <xsl:value-of select="."/>
                                <xsl:if test="position()!=last()">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <!-- if only displayForm -->
                        <xsl:otherwise>
                            <xsl:if test="displayForm != ''">
                                <xsl:for-each select="displayForm">
                                    <xsl:value-of select="."/>
                                    <xsl:if test="position()!=last()">
                                        <xsl:text>, </xsl:text>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
            
            <!-- if there is text roleTerm -->
            <xsl:for-each select="role/roleTerm[@type='text']">
                <xsl:if test=". != ''">
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="."/>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>
        
        <xsl:element name="field">
            <xsl:attribute name="name">mods_subject_name__facet</xsl:attribute>
            <xsl:choose>
                <xsl:when test="namePart[@type='family'] or namePart[@type='given']">
                    <xsl:if test="namePart[@type='family']">
                        <xsl:value-of select="namePart[@type='family']"/>
                    </xsl:if>
                    <xsl:if test="namePart[@type='given']">
                        <xsl:if test="namePart[@type='family']">
                            <xsl:text>, </xsl:text>
                        </xsl:if>
                        <xsl:value-of select="namePart[@type='given']"/>
                    </xsl:if>
                    <xsl:if test="namePart[@type='date']">
                        <xsl:text>, </xsl:text>
                        <xsl:value-of select="namePart[@type='date']"/>
                    </xsl:if>
                </xsl:when>
                
                <!-- if only namePart no specific family or given name tags -->
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="namePart != ''">
                            <xsl:for-each select="namePart">
                                <xsl:value-of select="."/>
                                <xsl:if test="position()!=last()">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <!-- if only displayForm -->
                        <xsl:otherwise>
                            <xsl:if test="displayForm != ''">
                                <xsl:for-each select="displayForm">
                                    <xsl:value-of select="."/>
                                    <xsl:if test="position()!=last()">
                                        <xsl:text>, </xsl:text>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>
    
    
    <xsl:template match="titleInfo" name="mods-titleInfo">
        <xsl:variable name="nsort" select="nonSort"/>
        <xsl:variable name="titl" select="title"/>
        <xsl:variable name="subt" select="subTitle"/>
        <xsl:variable name="partname" select="partName"/>
        <xsl:variable name="partNumber" select="partnum"/>
        <DEBUG>mods-titleInfo</DEBUG>
        
        <xsl:choose>
            <xsl:when test="@type = 'alternative' or title/@type = 'alternative'">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_alt_title</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="title"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@type = 'uniform' or title/@type = 'uniform'">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_uni_title</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="title"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@type = 'abbreviated' or title/@type = 'abbreviated'">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_abbr_title</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="title"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@type = 'translated' or title/@type = 'translated'">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_trans_title</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="title"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="field">
                    <xsl:attribute name="name">mods_title</xsl:attribute>
                    <!-- including nsort because this is keyword search spec -->
                    <xsl:value-of select="$nsort"/>
                    <xsl:value-of select="$titl"/>
                    
                    <xsl:for-each select="subTitle">
                        <xsl:if test=". != ''">
                            <xsl:if test="position()=1 and $titl">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                            <xsl:if test="position() != last()">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>
                    
                    <xsl:for-each select="partName">
                        <xsl:if test=". != ''">
                            <xsl:if test="position()=1 and ($titl or $subt)">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                            <xsl:if test="position() != last()">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>
                    
                    <xsl:for-each select="partNumber">
                        <xsl:if test=". != ''">
                            <xsl:if test="position()=1 and ($titl or $subt or $partname)">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                            <xsl:if test="position() != last()">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    


</xsl:stylesheet>
