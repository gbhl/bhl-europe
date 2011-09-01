<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform 
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
      xmlns:xs="http://www.w3.org/2001/XMLSchema"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
      xmlns:xlink="http://www.w3.org/1999/xlink"      
      xmlns:fnc="myfunc"      
      exclude-result-prefixes="#all"
      version="2.0">      
      
      <xsl:function name="fnc:openDoc" as="document-node()">
            <xsl:param name="base"/>
            <xsl:param name="file"/>
            <xsl:sequence select="doc(concat($base, $file))"/>
      </xsl:function>
      
      <!-- Functions for parsing -->
      
      <xsl:function name="fnc:parseSequence">
            <xsl:param name="string" as="xs:string"/>
            <xsl:param name="ending" as="xs:string"/>
            <xsl:value-of select="replace(replace($string, $ending, ''),'^0+','')"/>
      </xsl:function>      
      <xsl:function name="fnc:parseIssn">
            <xsl:param name="string" as="xs:string"/>
            <xsl:value-of select="replace(replace($string,'[^0-9]+$',''),'^[^0-9]+','')"/>
      </xsl:function>
      <xsl:function name="fnc:recodeForFilename">
            <xsl:param name="string" as="xs:string"/>
            <xsl:value-of select="substring(translate($string, '!§$%/()=?,.-;:_#+*{}[]\\&amp; ','_____________________________________'), string-length($string)-64)"/>
      </xsl:function>
      <xsl:function name="fnc:recodeIdentifier">
            <xsl:param name="string" as="xs:string"/>
            <xsl:value-of select="translate($string, '&lt;&gt;&quot;/|?*','___________________')"></xsl:value-of>
      </xsl:function>
   
           
</xsl:transform>
