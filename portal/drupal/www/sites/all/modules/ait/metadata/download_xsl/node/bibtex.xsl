<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
      xmlns:xlink="http://www.w3.org/1999/xlink"      
      xmlns:fnc="myfunc"      
      xmlns:mods="http://www.loc.gov/mods/v3"
      xmlns:mets="http://www.loc.gov/METS/"
      xmlns:marc="http://www.loc.gov/MARC21/slim"
      xmlns:oai="http://www.openarchives.org/OAI/2.0/"
      xmlns:sru_dc="info:srw/schema/1/dc-schema" 
      xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:dcterms="http://purl.org/dc/terms/"      
      xmlns:ese="http://www.europeana.eu/schemas/ese/"
      version="1.0">

    <xsl:output method="text" media-type="application/x-bibtex" encoding="ISO-8859-1" />

    <xsl:variable name="delimiterLeft"   select="' ('"/>
    <xsl:variable name="delimiterRight"  select="') '"/>
    <xsl:variable name="separator" 	     select="'; '"/>

    <xsl:template match="/">
        <xsl:apply-templates select="mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/*" mode="metadata"/>
    </xsl:template>

    <xsl:template match="*" mode="metadata">
        <xsl:text>Metadata-format not supported.</xsl:text>
    </xsl:template>    

    <xsl:template name="writeLine">
        <xsl:param name="name"/>
        <xsl:param name="content"/>
        <xsl:if test="$content != ''">
            <xsl:text> </xsl:text>
            <xsl:value-of select="$name"/>
            <xsl:text>="</xsl:text>
            <xsl:value-of select="$content"/>
            <xsl:text>",</xsl:text>
            <xsl:text>&#xA;</xsl:text>
        </xsl:if>
    </xsl:template> 
    
    <xsl:template match="ese:record" mode="metadata">
        <xsl:text>@misc{</xsl:text>
        <xsl:value-of select="dc:identifier[1]"/>
        <xsl:text>,</xsl:text>
        <xsl:text>&#xA;</xsl:text>
        
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'author'"/>
            <xsl:with-param name="content"><xsl:call-template name="ese-author"/></xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'title'"/>
            <xsl:with-param name="content"><xsl:call-template name="ese-title"/></xsl:with-param>
        </xsl:call-template>       
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'publisher'"/>
            <xsl:with-param name="content"><xsl:call-template name="ese-publisher"/></xsl:with-param>
        </xsl:call-template>       
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'year'"/>
            <xsl:with-param name="content"><xsl:call-template name="ese-year"/></xsl:with-param>
        </xsl:call-template>       
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'isbn'"/>
            <xsl:with-param name="content"><xsl:call-template name="ese-isbn"/></xsl:with-param>
        </xsl:call-template>       
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'url'"/>
            <xsl:with-param name="content"><xsl:call-template name="ese-url"/></xsl:with-param>
        </xsl:call-template>       
        
        <xsl:text>}</xsl:text>
        <xsl:text>&#xA;</xsl:text>       
    </xsl:template>       
    
    
    <xsl:template name="ese-title">
        <xsl:for-each select="dc:title">
            <xsl:value-of select="."/>
            <xsl:if test="position()!=last()"><xsl:value-of select="$separator"/></xsl:if>
        </xsl:for-each>        
    </xsl:template>
    
    <xsl:template name="ese-author">
        <xsl:for-each select="dc:creator">
            <xsl:value-of select="."/>
            <xsl:if test="position()!=last()"><xsl:value-of select="' and '"/></xsl:if>
        </xsl:for-each>
    </xsl:template>	
    
    <xsl:template name="ese-publisher">
        <xsl:for-each select="dc:publisher">
            <xsl:value-of select="."/>		
            <xsl:if test="position()!=last()"><xsl:value-of select="$separator"/></xsl:if>            
        </xsl:for-each>        
    </xsl:template>
    
    <xsl:template name="ese-year">
        <xsl:value-of select="(dcterms:issued | 
            dcterms:created | 
            dc:date)[1]"/>
    </xsl:template>
    
    <xsl:template name="ese-isbn">
        <xsl:for-each select="dc:identifier[contains(.,'isbn')]">
            <xsl:value-of select="."/>
            <xsl:if test="position()!=last()"><xsl:value-of select="$separator"/></xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="ese-url">        
        <xsl:for-each select="ese:isShownBy | ese:isShownAt">
            <xsl:value-of select="."/>
            <xsl:if test="position()!=last()"><xsl:value-of select="$separator"/></xsl:if>
        </xsl:for-each>
    </xsl:template>
    

    <xsl:template match="mods:mods" mode="metadata">
        <xsl:text>@misc{</xsl:text>
        <xsl:value-of select="mods:identifier[1]"/>
        <xsl:text>,</xsl:text>
        <xsl:text>&#xA;</xsl:text>
          
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'author'"/>
            <xsl:with-param name="content"><xsl:call-template name="mods-author"/></xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'title'"/>
            <xsl:with-param name="content"><xsl:call-template name="mods-title"/></xsl:with-param>
        </xsl:call-template>       
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'publisher'"/>
            <xsl:with-param name="content"><xsl:call-template name="mods-publisher"/></xsl:with-param>
        </xsl:call-template>       
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'year'"/>
            <xsl:with-param name="content"><xsl:call-template name="mods-year"/></xsl:with-param>
        </xsl:call-template>       
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'isbn'"/>
            <xsl:with-param name="content"><xsl:call-template name="mods-isbn"/></xsl:with-param>
        </xsl:call-template>       
        <xsl:call-template name="writeLine">
            <xsl:with-param name="name" select="'url'"/>
            <xsl:with-param name="content"><xsl:call-template name="mods-url"/></xsl:with-param>
        </xsl:call-template>       
             
        <xsl:text>}</xsl:text>
        <xsl:text>&#xA;</xsl:text>       
    </xsl:template>         
    
    <xsl:template name="mods-title">
        <xsl:for-each select="mods:titleInfo">
            <xsl:value-of select="mods:nonSort"/>
            <xsl:if test="mods:nonSort">
                <xsl:text> </xsl:text>
            </xsl:if>
            <xsl:value-of select="mods:title"/>
            <xsl:if test="mods:subTitle">
                <xsl:text>: </xsl:text>
                <xsl:value-of select="mods:subTitle"/>
            </xsl:if>
            <xsl:if test="mods:partNumber">
                <xsl:text>, </xsl:text>
                <xsl:value-of select="mods:partNumber"/>
                <xsl:value-of select="$delimiterLeft"/>
                <xsl:text>part number</xsl:text>
                <xsl:value-of select="$delimiterRight"/>
            </xsl:if>
            <xsl:if test="mods:partName">
                <xsl:text>, </xsl:text>
                <xsl:value-of select="mods:partName"/>
                <xsl:value-of select="$delimiterLeft"/>
                <xsl:text>part name</xsl:text>
                <xsl:value-of select="$delimiterRight"/>
            </xsl:if>	  
            <xsl:if test="position()!=last()"><xsl:value-of select="$separator"/></xsl:if>
        </xsl:for-each>        
    </xsl:template>
     
    <xsl:template name="mods-author">
        <xsl:for-each select="mods:name[
            mods:role/mods:roleTerm = 'author' or 
            mods:role/mods:roleTerm = 'aut' or 
            mods:role/mods:roleTerm = 'creator' or 
            mods:role/mods:roleTerm = 'cre']">
            <xsl:for-each select="mods:namePart[not(@type)]">
                <xsl:value-of select="."/>
                <xsl:text> </xsl:text>
            </xsl:for-each>
            <xsl:value-of select="mods:namePart[@type='family']"/>
            <xsl:if test="mods:namePart[@type='given']">
                <xsl:text>, </xsl:text>
                <xsl:value-of select="mods:namePart[@type='given']"/>
            </xsl:if>
            <xsl:if test="mods:namePart[@type='date']">
                <xsl:text>, </xsl:text>
                <xsl:value-of select="mods:namePart[@type='date']"/>
                <xsl:text/>
            </xsl:if>
            <xsl:if test="mods:displayForm">
                <xsl:value-of select="$delimiterLeft"/>
                <xsl:value-of select="mods:displayForm"/>
                <xsl:value-of select="$delimiterRight"/>
            </xsl:if>		
            <xsl:if test="position()!=last()"><xsl:value-of select="' and '"/></xsl:if>
        </xsl:for-each>
    </xsl:template>	
    
    <xsl:template name="mods-publisher">
        <xsl:for-each select="mods:originInfo/mods:publisher">
            <xsl:value-of select="."/>				
            <xsl:if test="../mods:place/mods:placeTerm">
                <xsl:value-of select="$delimiterLeft"/>
                <xsl:value-of select="../mods:place/mods:placeTerm"/>
                <xsl:value-of select="$delimiterRight"/>					
            </xsl:if>
        </xsl:for-each>        
    </xsl:template>
    
    <xsl:template name="mods-year">
        <xsl:value-of select="(mods:originInfo/mods:dateIssued | 
            mods:originInfo/mods:dateCreated | 
            mods:originInfo/mods:dateCaptured | 
            mods:originInfo/mods:dateValid | 
            mods:originInfo/mods:dateModified)[1]"/>
    </xsl:template>
    
    <xsl:template name="mods-isbn">
        <xsl:for-each select="mods:identifier[@type='isbn' and not(@invalid)]">
            <xsl:value-of select="."/>
            <xsl:if test="position()!=last()"><xsl:value-of select="$separator"/></xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="mods-url">
        <xsl:for-each select="mods:location/mods:url">
            <xsl:value-of select="."/>
            <xsl:if test="position()!=last()"><xsl:value-of select="$separator"/></xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    
</xsl:stylesheet>
