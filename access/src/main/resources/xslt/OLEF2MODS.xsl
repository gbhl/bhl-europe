<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" indent="yes" />

	<xsl:template match="/">
		<xsl:for-each select="//bibliographicInformation">
			<mods:mods xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				version="3.0"
				xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-0.xsd">
				<xsl:copy-of select="*" />
			</mods:mods>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>