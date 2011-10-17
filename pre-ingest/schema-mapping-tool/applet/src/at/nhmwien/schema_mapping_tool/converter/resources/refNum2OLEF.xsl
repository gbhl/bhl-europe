<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:refNum="http://bibnum.bnf.fr/ns/refNum" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="var_refNum" select="refNum:refNum"/>
		<olef xmlns="http://www.bhl-europe.eu/bhl-schema/v0.3/" xmlns:o-ex="http://odrl.net/1.1/ODRL-EX" xmlns:dcelem="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dwctype="http://rs.tdwg.org/dwc/dwctype/" xmlns:dwc="http://rs.tdwg.org/dwc/terms/" xmlns:mix="http://www.loc.gov/mix/v20" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:enc="http://www.w3.org/2001/04/xmlenc#">
			<xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">http://www.bhl-europe.eu/bhl-schema/v0.3/ http://www.bhl-europe.eu/bhl-schema/v0.3/OLEF_v0.3.xsd</xsl:attribute>
			<element xmlns="">
				<bibliographicInformation>
					<xsl:for-each select="$var_refNum/refNum:document/refNum:bibliographie/refNum:description">
						<mods:abstract>
							<xsl:value-of select="."/>
						</mods:abstract>
					</xsl:for-each>
					<mods:identifier>
						<xsl:value-of select="$var_refNum/refNum:document/@identifiant"/>
					</mods:identifier>
					<xsl:for-each select="$var_refNum/refNum:document/refNum:bibliographie/refNum:auteur">
						<mods:name>
							<mods:namePart>
								<xsl:value-of select="."/>
							</mods:namePart>
							<mods:role>
							  <mods:roleTerm type="code">aut</mods:roleTerm>
							</mods:role>
						</mods:name>
					</xsl:for-each>
					<xsl:for-each select="$var_refNum/refNum:document/refNum:bibliographie/refNum:editeur">
						<mods:name>
							<mods:namePart>
								<xsl:value-of select="."/>
							</mods:namePart>
							<mods:role>
							  <mods:roleTerm type="code">edt</mods:roleTerm>
							</mods:role>
						</mods:name>
					</xsl:for-each>
					<mods:originInfo>
						<xsl:for-each select="$var_refNum/refNum:document/refNum:bibliographie/refNum:dateEdition">
							<mods:dateIssued>
								<xsl:value-of select="."/>
							</mods:dateIssued>
						</xsl:for-each>
					</mods:originInfo>
					<mods:titleInfo>
						<mods:title>
							<xsl:value-of select="$var_refNum/refNum:document/refNum:bibliographie/refNum:titre"/>
						</mods:title>
						<xsl:for-each select="$var_refNum/refNum:document/refNum:bibliographie/refNum:tomaison">
							<mods:partNumber>
								<xsl:value-of select="refNum:valeur"/>
							</mods:partNumber>
						</xsl:for-each>
					</mods:titleInfo>
					<xsl:for-each select="$var_refNum/refNum:document/refNum:bibliographie/refNum:nombrePages">
						<mods:physicalDescription>
							<mods:extent>
								<xsl:value-of select="."/>
							</mods:extent>
						</mods:physicalDescription>
					</xsl:for-each>
				</bibliographicInformation>
				<itemInformation>
					<xsl:for-each select="$var_refNum/refNum:document/refNum:production">
						<dateCreated>
							<xsl:value-of select="refNum:dateNumerisation"/>
						</dateCreated>
					</xsl:for-each>
					<files>
						<xsl:for-each select="$var_refNum/refNum:document/refNum:structure/refNum:vueObjet">
							<xsl:variable name="var_typePage" select="@typePage"/>
							<file>
								<pages>
									<page>
										<xsl:if test="$var_typePage = 'T'">
											<xsl:attribute name="pageType">page</xsl:attribute>
										</xsl:if>
										<xsl:if test="$var_typePage = 'I'">
											<xsl:attribute name="pageType">page</xsl:attribute>
										</xsl:if>
										<xsl:if test="$var_typePage = 'L'">
											<xsl:attribute name="pageType">page</xsl:attribute>
										</xsl:if>
										<xsl:if test="$var_typePage = 'P'">
											<xsl:attribute name="pageType">title</xsl:attribute>
										</xsl:if>
										<xsl:if test="$var_typePage = 'E'">
											<xsl:attribute name="pageType">page</xsl:attribute>
										</xsl:if>
										<xsl:if test="$var_typePage = 'N'">
											<xsl:attribute name="pageType">page</xsl:attribute>
										</xsl:if>
										<xsl:if test="$var_typePage = 'A'">
											<xsl:attribute name="pageType">page</xsl:attribute>
										</xsl:if>
										<xsl:if test="$var_typePage = 'D'">
											<xsl:attribute name="pageType">page</xsl:attribute>
										</xsl:if>
										<xsl:if test="$var_typePage = 'R'">
											<xsl:attribute name="pageType">page</xsl:attribute>
										</xsl:if>
										<name>
											<xsl:value-of select="@ordre"/>
										</name>
									</page>
								</pages>
								<imageInformation>
									<mix:BasicImageCharacteristics>
										<mix:imageWidth><xsl:value-of select="substring-before(refNum:image/@dimension, ',')" /></mix:imageWidth>
										<mix:imageHeight><xsl:value-of select="substring-after(refNum:image/@dimension, ',')" /></mix:imageHeight>
									</mix:BasicImageCharacteristics>
								</imageInformation>
								<reference type="path">T/<xsl:value-of select="refNum:image/@nomImage" />.<xsl:value-of select="refNum:image/@typeFichier" /></reference>
							</file>
						</xsl:for-each>
					</files>
				</itemInformation>
				<xsl:for-each select="$var_refNum/refNum:document/refNum:bibliographie">
					<level>
						<xsl:if test="refNum:genre = 'PERIODIQUE'">serial</xsl:if>
						<xsl:if test="refNum:genre = 'MONOGRAPH'">monograph</xsl:if>
					</level>
				</xsl:for-each>
			</element>
		</olef>
	</xsl:template>
</xsl:stylesheet>
