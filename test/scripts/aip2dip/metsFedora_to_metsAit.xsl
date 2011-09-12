<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:mods="http://www.loc.gov/mods/v3"
    xmlns:mets="http://www.loc.gov/METS/"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:europeana="http://www.europeana.eu/schemas/ese/"
    xmlns:srw_dc="info:srw/schema/1/dc-schema"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:functx="http://www.functx.com"
    xmlns:fnc="myfunc"
    version="2.0">

    <xsl:include href="olef_to_ese.xsl"/>
    
    <xsl:output method="xml" indent="yes" version="1.0" omit-xml-declaration="no" exclude-result-prefixes="#all" encoding="UTF-8"/>

    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="mets:mets">
        <mets:mets>
            <xsl:apply-templates mode="copy"/>
        </mets:mets>
    </xsl:template>

    <xsl:template match="@*|node()" mode="copy copymods">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="@*|node()" mode="#current"/>
        </xsl:copy>
    </xsl:template>    

    <xsl:template match="mets:dmdSecFedora" mode="copy">
        <mets:dmdSec ID="{@ID}">
            <mets:mdWrap LABEL="{.//mets:mdWrap/@LABEL}" MDTYPE="{.//mets:mdWrap/@MDTYPE}">
                <mets:xmlData>
                    <xsl:apply-templates select=".//mets:xmlData/child::node()" mode="copy"/>                  
                </mets:xmlData>
            </mets:mdWrap>
        </mets:dmdSec>
    </xsl:template>
    
    <xsl:template match="mets:dmdSecFedora[.//mets:bibliographicInformation]" mode="copy">
        <mets:dmdSec ID="{@ID}_ESE">
            <mets:mdWrap LABEL="{.//mets:mdWrap/@LABEL}" MDTYPE="ESE">
                <mets:xmlData>
                    <europeana:record>
                        <xsl:for-each select=".//mets:bibliographicInformation">
                            <xsl:call-template name="olef_to_ese_start"/>
                        </xsl:for-each>
                    </europeana:record>
                </mets:xmlData>
            </mets:mdWrap>
        </mets:dmdSec>         
        <mets:dmdSec ID="{@ID}">
            <mets:mdWrap LABEL="{.//mets:mdWrap/@LABEL}" MDTYPE="MODS">
                <mets:xmlData>
                    <mods:mods>
                        <xsl:apply-templates select=".//mets:bibliographicInformation/child::node()" mode="copymods"/>
                    </mods:mods>                     
                </mets:xmlData>
            </mets:mdWrap>
        </mets:dmdSec>       
    </xsl:template>    
      
</xsl:stylesheet>
