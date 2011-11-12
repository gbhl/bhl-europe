<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: foxmlToSolr.xslt $ -->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:exts="xalan://dk.defxws.fedoragsearch.server.GenericOperationsImpl"
		xmlns:fn="http://xml.apache.org/xalan"
		exclude-result-prefixes="exts"
		xmlns:foxml="info:fedora/fedora-system:def/foxml#"
		xmlns:dc="http://purl.org/dc/elements/1.1/"
		xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
		 >
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>

<!--
	This xslt stylesheet generates the Solr doc element consisting of field elements
	from a FOXML record.
	You must specify the index field elements in solr's schema.xml file,
	including the uniqueKey element, which in this case is set to "PID".
	Options for tailoring:
		- generation of fields from other XML metadata streams than DC
		- generation of fields from other datastream types than XML
		- from datastream by ID, text fetched, if mimetype can be handled.
-->
	
	<xsl:include href="./includes/mods-solr.xsl"/>

	<xsl:param name="REPOSITORYNAME" select="repositoryName"/>
	<xsl:param name="FEDORASOAP" select="repositoryName"/>
	<xsl:param name="FEDORAUSER" select="repositoryName"/>
	<xsl:param name="FEDORAPASS" select="repositoryName"/>
	<xsl:param name="TRUSTSTOREPATH" select="repositoryName"/>
	<xsl:param name="TRUSTSTOREPASS" select="repositoryName"/>
	<xsl:variable name="PID" select="/foxml:digitalObject/@PID"/>

	<xsl:template match="/">
		<!-- The following allows only active FedoraObjects to be indexed. -->
		<xsl:if test="foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Active']">
			<xsl:if test="not(foxml:digitalObject/foxml:datastream[@ID='METHODMAP'] or foxml:digitalObject/foxml:datastream[@ID='DS-COMPOSITE-MODEL'])">
				<xsl:if test="starts-with($PID,'bhle') and foxml:digitalObject/foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/*[local-name()='hasModel' and @rdf:resource='info:fedora/islandora:bookCModel']">
					<xsl:apply-templates mode="activeFedoraObject"/>
				</xsl:if>
			</xsl:if>
		</xsl:if>
		<!-- The following allows inactive FedoraObjects to be deleted from the index. -->
		<xsl:if test="foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Inactive']">
			<xsl:if test="not(foxml:digitalObject/foxml:datastream[@ID='METHODMAP'] or foxml:digitalObject/foxml:datastream[@ID='DS-COMPOSITE-MODEL'])">
				<xsl:if test="starts-with($PID,'bhle') and foxml:digitalObject/foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/*[local-name()='hasModel' and @rdf:resource='info:fedora/islandora:bookCModel']">
					<xsl:apply-templates mode="inactiveFedoraObject"/>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="/foxml:digitalObject" mode="activeFedoraObject">
		<add>
		<doc>
			<field name="PID">
				<xsl:value-of select="$PID"/>
			</field>
			<!--  
			<field name="REPOSITORYNAME">
				<xsl:value-of select="$REPOSITORYNAME"/>
			</field>
			<field name="REPOSBASEURL">
				<xsl:value-of select="substring($FEDORASOAP, 1, string-length($FEDORASOAP)-9)"/>
			</field>
			-->
			<!-- 
				Content type			
				
				<xsl:for-each select="foxml:datastream[last()]/foxml:datastreamVersion/foxml:xmlContent/*[local-name()='RDF']/*[local-name()='Description']/*[local-name()='hasModel']" >
				
			-->
			<xsl:for-each select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/*[local-name()='RDF']/*[local-name()='Description']/*[local-name()='hasModel']" >
				<xsl:variable name="resourceUri" select="@rdf:resource" />
				<field name="contentType">
					<xsl:value-of select="substring-before( substring-after($resourceUri, 'islandora:'), 'CModel')"/>
				</field>
			</xsl:for-each>
			
			<!-- 
				fedora properties
			-->
			<xsl:for-each select="foxml:objectProperties/foxml:property">
				<field>
					<xsl:attribute name="name">
						<xsl:value-of select="concat('fgs_', substring-after(@NAME,'#'))"/>
					</xsl:attribute>
					<xsl:value-of select="@VALUE"/>
				</field>
			</xsl:for-each>
			<xsl:for-each select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/oai_dc:dc/*">
				<field>
					<xsl:attribute name="name">
						<xsl:value-of select="concat('dc_', substring-after(name(),':'))"/>
					</xsl:attribute>
					<xsl:value-of select="text()"/>
				</field>
			</xsl:for-each>
			<!-- 
				OLEF/MODS  ( templates included from olef-solr.xml )
			-->
			<!-- variant witch works with local namespace set on mods element -->
			<!-- 
			-->
			<xsl:for-each select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/*[local-name() = 'mods']">
				<xsl:call-template name="mods"/>
			</xsl:for-each>
			
			<!-- variant with works when local namespace NOT SET on mods element -->
			<!--
			<xsl:for-each select="/foxml:digitalObject/foxml:datastream/foxml:datastreamVersion/foxml:xmlContent/mods">
				<xsl:call-template name="mods-name-DEBUG"/>
				<DEBUG><xsl:value-of select="language"/></DEBUG>
			</xsl:for-each>
			-->	
			
			<!--
				Datastreams
				
				datastream is fetched, if its mimetype
			     	can be handled, the text becomes the value of the field. 
			-->
			<!--  
			<xsl:for-each select="foxml:datastream[@CONTROL_GROUP='M' or @CONTROL_GROUP='E' or @CONTROL_GROUP='R']">
				<field>
					<xsl:attribute name="name">
						<xsl:value-of select="concat('dsm_', @ID)"/>
					</xsl:attribute>
					<xsl:value-of select="exts:getDatastreamText($PID, $REPOSITORYNAME, @ID, $FEDORASOAP, $FEDORAUSER, $FEDORAPASS, $TRUSTSTOREPATH, $TRUSTSTOREPASS)"/>
				</field>
			</xsl:for-each>
			-->
			<!--
			creating an index field with all text from the foxml record and its datastreams
			-->
			<!--
			<field name="foxml_all_text">
				<xsl:for-each select="//text()">
					<xsl:value-of select="."/>
					<xsl:text>&#160;</xsl:text>
				</xsl:for-each>
				<xsl:for-each select="//foxml:datastream[@CONTROL_GROUP='M' or @CONTROL_GROUP='E' or @CONTROL_GROUP='R']">
					<xsl:value-of select="exts:getDatastreamText($PID, $REPOSITORYNAME, @ID, $FEDORASOAP, $FEDORAUSER, $FEDORAPASS, $TRUSTSTOREPATH, $TRUSTSTOREPASS)"/>
					<xsl:text>&#160;</xsl:text>
				</xsl:for-each>
			</field>
			-->
		</doc>
		</add>
	</xsl:template>


	<xsl:template match="/foxml:digitalObject" mode="inactiveFedoraObject">
		<delete>
			<id><xsl:value-of select="$PID"/></id>
		</delete>
	</xsl:template>

</xsl:stylesheet>
