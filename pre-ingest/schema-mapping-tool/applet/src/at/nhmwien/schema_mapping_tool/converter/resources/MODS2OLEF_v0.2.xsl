<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mods="http://www.loc.gov/mods/v3">
  <xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"/>

  <!-- XSL MODS to OLEF v0.4 -->

  <!-- Rename all elements and add the mods: namespace prefix -->
  <xsl:template match="*" priority="0">
    <xsl:element name="mods:{local-name()}" namespace="http://www.loc.gov/mods/v3">
      <xsl:apply-templates />
    </xsl:element>
  </xsl:template>

  <!-- The modsCollection item is equivalent to our bhl top-level element -->
  <xsl:template match="mods:modsCollection" priority="2">
      <xsl:element name="olef">
        <xsl:apply-templates />
      </xsl:element>
  </xsl:template>

  <!-- rename the mods element -->
  <xsl:template match="mods:modsCollection/mods:mods" priority="3">
    <!--<xsl:attribute name="xmlns:xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:attribute>-->
    <!--<xsl:attribute name="xmlns:mods">http://www.loc.gov/mods/v3</xsl:attribute>
    <xsl:attribute name="xsi:noNamespaceSchemaLocation">http://bhl.nhm-wien.ac.at/schemas/olef/0.2/olef_v0.2.xsd</xsl:attribute>-->
    <xsl:element name="element">
      <xsl:element name="bibliographicInformation">
        <xsl:apply-templates />
      </xsl:element>
        
      <xsl:element name="level">
          <xsl:if test="mods:originInfo/mods:issuance = 'monographic'">monograph</xsl:if>
          <xsl:if test="mods:originInfo/mods:issuance = 'continuing'">serial</xsl:if>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <!-- If we have a root-mods entry: rename the mods element and add all required upper elements -->
  <xsl:template match="/mods:mods" priority="1">
    <xsl:element name="olef">
      <xsl:attribute name="xmlns">http://www.bhl-europe.eu/bhl-schema/v0.3/</xsl:attribute>
      <!--<xsl:attribute name="xmlns:xsi">http://www.w3.org/2001/XMLSchema-instance</xsl:attribute>-->
      <!--<xsl:attribute name="xmlns:mods">http://www.loc.gov/mods/v3</xsl:attribute>
      <xsl:attribute name="xsi:noNamespaceSchemaLocation">http://bhl.nhm-wien.ac.at/schemas/olef/0.2/olef_v0.2.xsd</xsl:attribute>-->
      <xsl:element name="element">
        <xsl:element name="bibliographicInformation">
          <xsl:apply-templates />
        </xsl:element>
        
        <xsl:element name="level">
            <xsl:if test="mods:originInfo/mods:issuance = 'monographic'">monograph</xsl:if>
            <xsl:if test="mods:originInfo/mods:issuance = 'continuing'">serial</xsl:if>
        </xsl:element>
      </xsl:element>
    </xsl:element>
  </xsl:template>
  
</xsl:stylesheet>
