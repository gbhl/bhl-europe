<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:mods="http://www.loc.gov/mods/v3"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:dcterms="http://purl.org/dc/terms/"
      xmlns:europeana="http://www.europeana.eu/schemas/ese/"
      exclude-result-prefixes="xs"
      version="1.0">    
    <xsl:output method="html" indent="yes"/>
    
    <xsl:variable name="maxTextCharacters" select="'100'"/>
    <xsl:variable name="maxRelatedItemTextCharacters" select="'100'"/>
    
    <xsl:template match="/">  
        <html>
            <head>
                <link rel="stylesheet" type="text/css" href="../../../../utilities/view/view.css"/>
                <!-- <script type="text/javascript" src="../../../../utilities/view/view.js"/> -->
            </head>
            <body>
                <xsl:apply-templates select="//mods:mods"/>
                <xsl:apply-templates select="//europeana:record"/>
            </body>
        </html>                
    </xsl:template>

    <xsl:template match="mods:mods">    
        <table class="mods">
            <thead><tr><th><xsl:value-of select="local-name()"/></th></tr></thead>
            <tbody><xsl:apply-templates/></tbody>
        </table>
    </xsl:template>  
    
    <xsl:template match="mods:mods//mods:relatedItem">   
        <xsl:variable name="relatedItemID" select="generate-id(.)"/>
        <xsl:variable name="relatedItemType">
            <xsl:choose>
                <xsl:when test="@type = 'constituent'"><xsl:value-of select="'relatedItemConstituent'"/></xsl:when>
                <xsl:when test="(@type = 'host') or (@type = 'series')"><xsl:value-of select="'relatedItemParent'"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="'relatedItemGeneral'"/></xsl:otherwise>
            </xsl:choose>            
        </xsl:variable>
        <tr><td>
            <xsl:choose>
                <xsl:when test="count(child::node()) = 0">
                    <table class="{$relatedItemType} relatedItem" id="{$relatedItemID}" status="show">
                        <thead><tr><th>
                            <table width="100%" cellpadding="0px" cellspacing="0px">
                                <tr><td>
                                    <span class="button">&#160;</span>                                    
                                    <span class="superElementText"><xsl:value-of select="local-name()"/></span>
                                    <xsl:apply-templates select="." mode="writeAttributes"/>
                                </td></tr>
                            </table>
                        </th></tr></thead>
                    </table>                    
                </xsl:when>
                <xsl:otherwise>
                    <table name="relatedItemTable" class="{$relatedItemType} relatedItem" id="{$relatedItemID}" status="show">
                        <thead><tr><th>
                            <table width="100%" cellpadding="0px" cellspacing="0px">
                                <tr><td>
                                    <span class="button"><a href="javascript:switchItem('{$relatedItemID}')">+</a></span>
                                    <span class="superElementText"><xsl:value-of select="local-name()"/></span>
                                    <xsl:apply-templates select="." mode="writeAttributes"/>
                                </td><td style="text-align:right">
                                    <xsl:apply-templates mode="writeRelatedItemInfo" select="."/>
                                </td></tr>
                            </table>
                        </th></tr></thead>
                        <tbody>
                            <xsl:apply-templates/>
                        </tbody>
                    </table>                    
                </xsl:otherwise>
            </xsl:choose>            
        </td></tr>
    </xsl:template>  

   <xsl:template match="mods:mods//*">
        <xsl:choose>
            <xsl:when test="child::*">
                <tr><td>
                    <table class="subNode">
                        <thead><tr><th><xsl:value-of select="local-name()"/></th></tr></thead>                        
                        <tbody><xsl:apply-templates/></tbody>
                    </table>
                </td></tr>
            </xsl:when>
            <xsl:otherwise>                
                <tr><td class="textLine">
                    <xsl:value-of select="local-name()"/>: <xsl:value-of select="."/>
                </td></tr>
            </xsl:otherwise>
        </xsl:choose>         
    </xsl:template>       
    
    <xsl:template match="mods:mods//*" mode="writeRelatedItemInfo">
        <span class="relatedItemTitle">
            <xsl:choose>
                <xsl:when test="string-length(mods:titleInfo/mods:title) &lt; $maxRelatedItemTextCharacters">                
                    <xsl:value-of select="mods:titleInfo/mods:title"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="substring(mods:titleInfo/mods:title,0,$maxRelatedItemTextCharacters)"/>
                    <xsl:text>...</xsl:text> 
                </xsl:otherwise>
            </xsl:choose>
        </span>
        <span class="relatedItemPartType">
            <xsl:text> - </xsl:text>
            <xsl:value-of select="mods:part/@type"/>
        </span>
    </xsl:template>




    <xsl:template match="europeana:record">    
        <table class="ese">
            <thead><tr><th>Namespace</th><th>Field</th><th>Value</th></tr></thead>
            <tbody><xsl:apply-templates/></tbody>
        </table>
    </xsl:template>  
    
    <xsl:template match="europeana:record//*">
        <tr>
            <td class="eseNode"><xsl:value-of select="substring-before(name(),':')"/>:</td>
            <td class="eseNode"><xsl:value-of select="local-name()"/></td>
            <td><xsl:copy-of select="."/></td>
        </tr>
    </xsl:template>
  
    

</xsl:stylesheet>
