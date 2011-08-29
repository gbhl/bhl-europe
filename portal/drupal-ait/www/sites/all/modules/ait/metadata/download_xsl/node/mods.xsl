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
    
    <xsl:import href="../dc_to_mods.xsl"/>
    
    <xsl:output method="xml" indent="yes"/>    

    <xsl:template match="/">
        <xsl:apply-templates select="mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/*" mode="record"/>
    </xsl:template>
    
    <xsl:template match="mods:mods" mode="record">     
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="ese:record" mode="record">
        <xsl:call-template name="dcStart"/>
    </xsl:template>
    
    <xsl:template match="*" mode="record">
        <xsl:text>Metadata-format not supported.</xsl:text>
    </xsl:template>

</xsl:stylesheet>
