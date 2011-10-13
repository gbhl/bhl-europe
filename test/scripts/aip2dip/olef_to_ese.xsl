<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" 
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
	exclude-result-prefixes="mods mets srw_dc xs xsl xlink functx fnc">

<!-- 
This stylesheet transforms MODS version 3.2 records and collections of records to simple Dublin Core (DC) records, 
based on the Library of Congress' MODS to simple DC mapping <http://www.loc.gov/standards/mods/mods-dcsimple.html> 
		
The stylesheet will transform a collection of MODS 3.2 records into simple Dublin Core (DC)
as expressed by the SRU DC schema <http://www.loc.gov/standards/sru/dc-schema.xsd>

The stylesheet will transform a single MODS 3.2 record into simple Dublin Core (DC)
as expressed by the OAI DC schema <http://www.openarchives.org/OAI/2.0/oai_dc.xsd>
		
Because MODS is more granular than DC, transforming a given MODS element or subelement to a DC element frequently results in less precise tagging, 
and local customizations of the stylesheet may be necessary to achieve desired results. 

This stylesheet makes the following decisions in its interpretation of the MODS to simple DC mapping: 
	
When the roleTerm value associated with a name is creator, then name maps to dc:creator
When there is no roleTerm value associated with name, or the roleTerm value associated with name is a value other than creator, then name maps to dc:contributor
Start and end dates are presented as span dates in dc:date and in dc:coverage
When the first subelement in a subject wrapper is topic, subject subelements are strung together in dc:subject with hyphens separating them
Some subject subelements, i.e., geographic, temporal, hierarchicalGeographic, and cartographics, are also parsed into dc:coverage
The subject subelement geographicCode is dropped in the transform

	
Revision 1.1	2007-05-18 <tmee@loc.gov>
		Added modsCollection conversion to DC SRU
		Updated introductory documentation
	
Version 1.0	2007-05-04 Tracy Meehleib <tmee@loc.gov>

-->
	<xsl:include href="functx.xsl"/>
	<xsl:include href="utilities.xsl"/>

	<xsl:output method="xml" indent="yes" version="1.0" omit-xml-declaration="no" exclude-result-prefixes="#all" encoding="UTF-8"/> 

	<xsl:variable name="delimiterLeft" 		  select="' ('"/>
	<xsl:variable name="delimiterRight" 	  select="') '"/>

	<xsl:variable name="project"              select="//project"/>
	
	<xsl:variable name="rolesFile"			  select="document('roles.xml')"/>

	<xsl:template name="olef_to_ese_start">
		<xsl:apply-templates select="@*|node()"/>
	</xsl:template>
	
	<xsl:template match="mods:titleInfo">	
		<xsl:choose>
			<xsl:when test="@type">
				<dcterms:alternative><xsl:call-template name="titleInfo"/></dcterms:alternative>
			</xsl:when>
			<xsl:otherwise>
				<dc:title><xsl:call-template name="titleInfo"/></dc:title>
			</xsl:otherwise>
		</xsl:choose>			
	</xsl:template>

	<xsl:template match="mods:name">
		<xsl:variable name="role" select="fnc:getRoleInOutputFormat(.)"/>
		<xsl:choose>
			<xsl:when test="$role = 'Creator'">
				<dc:creator><xsl:call-template name="name"/></dc:creator>
			</xsl:when>
			<xsl:when test="$role = 'Publisher'">
				<dc:publisher><xsl:call-template name="name"/></dc:publisher>
			</xsl:when>
			<xsl:otherwise>
				<dc:contributor>
					<xsl:call-template name="name"><xsl:with-param name="role" select="$role"/></xsl:call-template>
				</dc:contributor>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- DEBUG: does this make sense? -->
	<xsl:template match="mods:classification">
		<dc:subject>
			<xsl:value-of select="."/>
			<xsl:if test="@authority">
				<xsl:value-of select="$delimiterLeft"/>
				<xsl:value-of select="@authority"/>
				<xsl:value-of select="$delimiterRight"/>
			</xsl:if>
		</dc:subject>
	</xsl:template>

	<xsl:template match="mods:subject">	
		
		<!-- combined elements -->
		<dc:subject>
			<xsl:for-each select="./*">
				<xsl:choose>
					<xsl:when test="local-name() = 'name'"><xsl:call-template name="name"/></xsl:when>
					<xsl:when test="local-name() = 'titleInfo'"><xsl:call-template name="titleInfo"/></xsl:when>
					<xsl:when test="local-name() = 'hierarchicalGeographic'">
						<xsl:for-each
							select="mods:continent|mods:country|mods:provence|mods:region|mods:state|mods:territory|mods:county|mods:city|mods:island|mods:area">
							<xsl:value-of select="."/>
							<xsl:if test="position()!=last()"> - </xsl:if>
						</xsl:for-each>
					</xsl:when>
					<xsl:when test="local-name() = 'temporal'">
						<xsl:choose>
							<xsl:when test="@point='start'">
								<xsl:value-of select="."/>
								<xsl:text> - </xsl:text>
								<xsl:value-of select="../mods:temporal[@point='end']"/>
							</xsl:when>
							<xsl:when test="@point='end'"/>
							<xsl:otherwise>
								<xsl:value-of select="."/>
							</xsl:otherwise>
						</xsl:choose>				
					</xsl:when>
					<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
				</xsl:choose>
				<xsl:if test="position()!=last()"> / </xsl:if>
			</xsl:for-each>
		</dc:subject>				
		
		<!-- single elements -->
		
		<!-- 
		<xsl:for-each select="mods:topic | mods:occupation | mods:genre">
			<dc:subject><xsl:value-of select="."/></dc:subject>
		</xsl:for-each>		
		
		<xsl:for-each select="mods:name">
			<dc:subject><xsl:call-template name="name"/></dc:subject>
		</xsl:for-each>

		<xsl:for-each select="mods:titleInfo">
			<dc:subject><xsl:call-template name="titleInfo"/></dc:subject>
		</xsl:for-each>
		-->

		<xsl:for-each select="mods:geographic">
			<dcterms:spatial><xsl:value-of select="."/></dcterms:spatial>
		</xsl:for-each>	

		<xsl:for-each select="mods:hierarchicalGeographic">
			<dcterms:spatial>
				<xsl:for-each
					select="mods:continent|mods:country|mods:provence|mods:region|mods:state|mods:territory|mods:county|mods:city|mods:island|mods:area">
					<xsl:value-of select="."/>
					<xsl:if test="position()!=last()"> - </xsl:if>
				</xsl:for-each>
			</dcterms:spatial>
		</xsl:for-each>
	
		<!-- 
		<xsl:for-each select="mods:cartographics/*">
			<dcterms:spatial><xsl:value-of select="."/></dcterms:spatial>
		</xsl:for-each>
		-->

		<xsl:for-each select="mods:temporal">
			<xsl:choose>
				<xsl:when test="@point='start'">
					<dcterms:termporal>
						<xsl:value-of select="."/>-<xsl:value-of select="../mods:temporal[@point='end']"/>
					</dcterms:termporal>
				</xsl:when>
				<xsl:when test="@point='end'"/>
				<xsl:otherwise>
					<dcterms:termporal>
						<xsl:value-of select="."/>
					</dcterms:termporal>
				</xsl:otherwise>
			</xsl:choose>				
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="mods:abstract">
		<dc:description><xsl:value-of select="."/></dc:description>
	</xsl:template>

	<xsl:template match="mods:tableOfContents">
		<dcterms:tableOfContents><xsl:value-of select="."/></dcterms:tableOfContents>
	</xsl:template>

	<xsl:template match="mods:originInfo">
		<xsl:apply-templates/>
		
		<xsl:for-each select="mods:issuance">
			<dc:type><xsl:value-of select="."/></dc:type>			
		</xsl:for-each>

		<xsl:for-each select="mods:publisher">
			<dc:publisher>
				<xsl:value-of select="."/>				
				<xsl:if test="../mods:place/mods:placeTerm">
					<xsl:value-of select="$delimiterLeft"/>
					<xsl:value-of select="../mods:place/mods:placeTerm"/>
					<xsl:value-of select="$delimiterRight"/>					
				</xsl:if>
			</dc:publisher>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="mods:dateIssued | mods:dateCreated | mods:dateCaptured | mods:dateOther">
		<xsl:variable name="dateNameESE">
			<xsl:choose>
				<xsl:when test="local-name() = 'dateIssued'">dcterms:issued</xsl:when>
				<xsl:when test="local-name() = 'dateCreated'">dcterms:created</xsl:when>
				<xsl:otherwise>dc:date</xsl:otherwise>
			</xsl:choose>			
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@point='start'">
				<xsl:variable name="dateName" select="local-name()"/>
				<xsl:element name="{$dateNameESE}">
					<xsl:value-of select="."/>
					<xsl:if test="../*[local-name()=$dateName][@point='end']">
						<xsl:text> - </xsl:text>
						<xsl:value-of select="../*[local-name()=$dateName][@point='end']"/>
					</xsl:if>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@point='end'"/>
			<xsl:otherwise>
				<xsl:element name="{$dateNameESE}">
					<xsl:value-of select="."/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>	
	</xsl:template>

	<xsl:template match="mods:genre">
		<xsl:choose>
			<xsl:when test="@authority='dct'">
				<dc:type><xsl:value-of select="."/></dc:type>
				<xsl:for-each select="mods:typeOfResource">
					<dc:type><xsl:value-of select="."/></dc:type>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<dc:type><xsl:value-of select="."/></dc:type>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="mods:typeOfResource">
		<xsl:if test="@collection='yes'">											<dc:type>Collection</dc:type></xsl:if>
		<xsl:if test=".='software' and ../mods:genre='database'">					<dc:type>DataSet</dc:type></xsl:if>
		<xsl:if test=".='software' and ../mods:genre='online system or service'">	<dc:type>Service</dc:type></xsl:if>
		<xsl:if test=".='software'">												<dc:type>Software</dc:type></xsl:if>
		<xsl:if test=".='cartographic material'">									<dc:type>Image</dc:type></xsl:if>
		<xsl:if test=".='multimedia'">												<dc:type>InteractiveResource</dc:type></xsl:if>
		<xsl:if test=".='moving image'">											<dc:type>MovingImage</dc:type></xsl:if>
		<xsl:if test=".='three-dimensional object'">								<dc:type>PhysicalObject</dc:type></xsl:if>
		<xsl:if test="starts-with(.,'sound recording')">							<dc:type>Sound</dc:type></xsl:if>
		<xsl:if test=".='still image'">												<dc:type>StillImage</dc:type></xsl:if>
		<xsl:if test=".='text'">													<dc:type>Text</dc:type></xsl:if>
		<xsl:if test=".='notated music'">											<dc:type>Text</dc:type></xsl:if>
	</xsl:template>

	<xsl:template match="mods:physicalDescription">
		<xsl:if test="mods:extent"><dcterms:extent><xsl:value-of select="mods:extent"/></dcterms:extent></xsl:if>
		<xsl:if test="mods:form"><dc:format><xsl:value-of select="mods:form"/></dc:format></xsl:if>
		<xsl:if test="mods:internetMediaType"><dc:format><xsl:value-of select="mods:internetMediaType"/></dc:format></xsl:if>
	</xsl:template>

	<xsl:template match="mods:mimeType">
		<dc:format>
			<xsl:value-of select="."/>
		</dc:format>
	</xsl:template>

	<xsl:template match="mods:identifier">
		<xsl:variable name="type" select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
		<dc:identifier>			
			<xsl:value-of select="."/>
			<xsl:if test="$type or @invalid = 'yes'">
				<xsl:value-of select="$delimiterLeft"/>
				<xsl:if test="@invalid = 'yes'">invalid </xsl:if>
				<xsl:if test="$type and @invalid = 'yes'"><xsl:text> </xsl:text></xsl:if>
				<xsl:if test="$type"><xsl:value-of select="$type"/></xsl:if>
				<xsl:value-of select="$delimiterRight"/>
			</xsl:if>
		</dc:identifier>
	</xsl:template>

	<xsl:template match="mods:location">
		<xsl:if test="mods:physicalLocation">
			<dcterms:provenance>
				<xsl:for-each select="mods:physicalLocation | mods:shelfLocator">
					<xsl:value-of select="."/>	
					<xsl:if test="position()!=last()"><xsl:text> - </xsl:text></xsl:if>
				</xsl:for-each>
				<xsl:for-each select="mods:holdingSimple/mods:copyInformation">
					<xsl:value-of select="$delimiterLeft"/>
					<xsl:for-each select="./*">
						<xsl:value-of select="."/>
						<xsl:if test="position()!=last()"><xsl:text> - </xsl:text></xsl:if>
					</xsl:for-each>
					<xsl:value-of select="$delimiterRight"/>
				</xsl:for-each>				
			</dcterms:provenance>
		</xsl:if>	
	</xsl:template>

	<xsl:template match="mods:language">
		<dc:language>
			<xsl:value-of select="normalize-space(.)"/>
		</dc:language>
	</xsl:template>			

	<xsl:template match="mods:relatedItem[mods:titleInfo | mods:name | mods:identifier | mods:location]">
		<xsl:param name="unstored" as="xs:boolean" select="false()"/>
		<xsl:variable name="itemNameESE">
			<xsl:choose>
				<xsl:when test="$unstored">europeana:unstored</xsl:when>
				<xsl:when test="@type='original'">dc:source</xsl:when>
				<xsl:when test="@type='host' or @type='series'">dcterms:isPartOf</xsl:when>
				<xsl:when test="@type='constituent'">dcterms:hasPart</xsl:when>
				<xsl:when test="@type='otherVersion'">dcterms:hasVersion</xsl:when>
				<xsl:when test="@type='otherFormat'">dcterms:hasFormat</xsl:when>
				<xsl:when test="@type='isReferencedBy'">dcterms:isReferencedBy</xsl:when>
				<xsl:otherwise>dc:relation</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:element name="{$itemNameESE}">
			<xsl:if test="mods:part/@type"><xsl:value-of select="functx:capitalize-first(mods:part/@type)"/>: </xsl:if>
			<xsl:for-each select="mods:titleInfo | mods:name | mods:identifier | mods:location/mods:url">				
				<xsl:if test="normalize-space(.)!= ''">
					<xsl:choose>
						<xsl:when test="local-name() = 'titleInfo'"><xsl:call-template name="titleInfo"/></xsl:when>
						<xsl:when test="local-name() = 'name'">
							<xsl:call-template name="name"><xsl:with-param name="role" select="fnc:getRoleInOutputFormat(.)"/></xsl:call-template>
						</xsl:when>
						<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
					</xsl:choose>
					<xsl:if test="position()!=last()"> -- </xsl:if>
				</xsl:if>
			</xsl:for-each>
		</xsl:element>
		<xsl:if test="@xlink:href">
			<xsl:element name="{$itemNameESE}"><xsl:value-of select="@xlink:href"/></xsl:element>
		</xsl:if>
	</xsl:template>

	<xsl:template match="mods:accessCondition">
		<dc:rights>
			<xsl:value-of select="."/>
		</dc:rights>
	</xsl:template>
	
	<xsl:template match="mods:part">
		<xsl:for-each select="mods:extent">
			<dcterms:extent>
				<xsl:value-of select="@unit"/>: <xsl:value-of select="mods:start"/> - <xsl:value-of select="mods:end"/>
			</dcterms:extent>			
		</xsl:for-each>
	</xsl:template>	
	
<!--	<xsl:template match="mods:temporal[@point='start']  ">
		<xsl:value-of select="."/>-<xsl:value-of select="../mods:temporal[@point='end']"/>
	</xsl:template>
	
	<xsl:template match="mods:temporal[@point!='start' and @point!='end']  ">
		<xsl:value-of select="."/>
	</xsl:template>-->
	
	
	
	<!-- named templates for repeated use -->
	
	<xsl:template name="titleInfo">
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
	</xsl:template>	
	
	<xsl:template name="name">
		<xsl:param name="role" as="xs:string" required="no" select="''"/>
		<xsl:variable name="nameOutput">
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
			<xsl:if test="$role">
				<xsl:value-of select="$delimiterLeft"/>
				<xsl:value-of select="$role"/>
				<xsl:value-of select="$delimiterRight"/>
			</xsl:if>			
<!--			<xsl:variable name="roles">
				<xsl:choose>
					<xsl:when test="$writeRole"><xsl:value-of select="mods:role/mods:roleTerm"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="fnc:filterOutCreator(mods:role/mods:roleTerm)"/></xsl:otherwise>
				</xsl:choose>								
			</xsl:variable>-->
<!--			<xsl:for-each select="mods:role/mods:roleTerm">		<!-\- TODO: Thesaurus matching -\->
				<xsl:value-of select="$delimiterLeft"/>
				<xsl:value-of select="normalize-space(.)"/>		
				<xsl:value-of select="$delimiterRight"/>
			</xsl:for-each>-->
		</xsl:variable>
		<xsl:value-of select="normalize-space($nameOutput)"/>
	</xsl:template>	
	
	
	<!-- functions -->
	
	<xsl:function name="fnc:getEuropeanaUnstored">
		<xsl:param name="context"/>
		<xsl:for-each select="$context/mods:note">
			<europeana:unstored><xsl:value-of select="."/></europeana:unstored>
		</xsl:for-each>
		<xsl:apply-templates select="$context/mods:relatedItem//mods:relatedItem"><xsl:with-param name="unstored" select="true()"/></xsl:apply-templates>
	</xsl:function>
	
	<xsl:function name="fnc:getEuropeanaType">
		<xsl:param name="context"/>
		<europeana:type>	
			<xsl:choose>
				<xsl:when test="$context = 'text'">TEXT</xsl:when>
				<xsl:when test="$context = 'still image'">IMAGE</xsl:when>					
				<xsl:when test="$context = 'moving image'">VIDEO</xsl:when>
				<xsl:when test="$context = 'sound recording' or 
					$context = 'sound recording-musical' or 
					$context = 'sound recording-nonmusical'">SOUND</xsl:when>
				<xsl:otherwise>TEXT</xsl:otherwise>		<!-- DEBUG: should standard type be TEXT? -->
			</xsl:choose>	
		</europeana:type>			
	</xsl:function>
	
	<xsl:function name="fnc:getEuropeanaRights">
		<xsl:param name="context"/>
		<xsl:variable name="write" as="xs:boolean">
			<xsl:choose>
				<xsl:when test="$context = 'http://www.europeana.eu/rights/rr-f/'"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="$context = 'http://www.europeana.eu/rights/rr-p/'"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="$context = 'http://www.europeana.eu/rights/rr-r/'"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="$context = 'http://www.europeana.eu/rights/unknown/'"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="fnc:isCreativeCommons($context,'http://creativecommons.org/publicdomain/zero/')"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="fnc:isCreativeCommons($context,'http://creativecommons.org/licenses/by/')"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="fnc:isCreativeCommons($context,'http://creativecommons.org/licenses/by-sa/')"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="fnc:isCreativeCommons($context,'http://creativecommons.org/licenses/by-nc/')"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="fnc:isCreativeCommons($context,'http://creativecommons.org/licenses/by-nc-sa/')"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="fnc:isCreativeCommons($context,'http://creativecommons.org/licenses/by-nd/')"><xsl:value-of select="true()"/></xsl:when>
				<xsl:when test="fnc:isCreativeCommons($context,'http://creativecommons.org/licenses/by-nc-nd/')"><xsl:value-of select="true()"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="false()"/></xsl:otherwise>				
			</xsl:choose>			
		</xsl:variable>
		<xsl:if test="$write"><europeana:rights><xsl:value-of select="$write"/></europeana:rights></xsl:if>
	</xsl:function>
	
	<xsl:function name="fnc:getEuropeanaIsShown">
		<xsl:param name="context"/>
		<xsl:for-each select="$context">
			<xsl:choose>
				<xsl:when test="@note = 'isShownBy'"><europeana:isShownBy><xsl:value-of select="."/></europeana:isShownBy></xsl:when>
				<xsl:when test="@note = 'isShownAt'"><europeana:isShownAt><xsl:value-of select="."/></europeana:isShownAt></xsl:when>
			</xsl:choose>			
		</xsl:for-each>
	</xsl:function>
	
	<xsl:function name="fnc:isCreativeCommons" as="xs:boolean">
		<xsl:param name="context"/>			
		<xsl:param name="cc"/>
		<xsl:value-of select="starts-with($context,$cc)"/>
	</xsl:function>
		
<!--	<xsl:function name="fnc:filterOutCreator">
		<xsl:param name="roles"/>		
		<xsl:value-of select="$roles[fnc:isCreator()]"/>
	</xsl:function>-->
	
<!--	<xsl:function name="fnc:isCreator" as="xs:boolean">
		<xsl:param name="context"/>
		<xsl:choose>
			<xsl:when test="fnc:normalize($context/mods:role/mods:roleTerm) = 'cre' or fnc:normalize($context/mods:role/mods:roleTerm) = 'creator'">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="false()"/></xsl:otherwise>
		</xsl:choose>				
	</xsl:function>
	
	<xsl:function name="fnc:isPublisher" as="xs:boolean">
		<xsl:param name="context"/>
		<xsl:choose>
			<xsl:when test="fnc:normalize($context/mods:role/mods:roleTerm) = 'pbl' or fnc:normalize($context/mods:role/mods:roleTerm) = 'publisher'">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="false()"/></xsl:otherwise>
		</xsl:choose>				
	</xsl:function>	-->
	
	<xsl:function name="fnc:getRoleInOutputFormat">
		<xsl:param name="context"/>
		<xsl:variable name="role" select="normalize-space($context/mods:role/mods:roleTerm[1])"/>
		<xsl:variable name="textFromCode" select="$rolesFile/records/item[@code = $role]/@text"/>
		<xsl:choose>
			<xsl:when test="empty($textFromCode)"><xsl:value-of select="functx:capitalize-first($role)"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="$textFromCode"/></xsl:otherwise>
<!--			<xsl:when test="empty($textFromCode)"><xsl:value-of select="'AAA'"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="$textFromCode"/></xsl:otherwise>-->
		</xsl:choose>
	</xsl:function>
<!--	
	<xsl:function name="fnc:normalize">
		<xsl:param name="context"/>
		<xsl:value-of select="normalize-space(lower-case($context))"/>
	</xsl:function>-->
	
	<!-- suppress all else:-->
	<xsl:template match="*"/>		
	
	
</xsl:stylesheet>
