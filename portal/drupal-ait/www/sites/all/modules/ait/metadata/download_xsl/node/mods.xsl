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
      xmlns:ese="http://www.europeana.eu/schemas/ese/"      
      exclude-result-prefixes="xs"
      version="1.0">
       
    <xsl:template match="/">
	<xsl:apply-templates select="mets:mets/mets:dmdSec/mets:mdWrap/mets:xmlData/*" mode="metadata"/>        
    </xsl:template>
    
    <xsl:template match="mods:mods" mode="metadata">     
        <xsl:copy-of select="."/>
    </xsl:template>
  
    <xsl:template match="*" mode="metadata">        
        <error>Metadata-format not supported.</error>
    </xsl:template>

</xsl:stylesheet>
