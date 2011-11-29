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
			<!--<xsl:for-each select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/*[local-name() = 'mods']">
				<xsl:call-template name="mods"/>
			</xsl:for-each>-->
			<xsl:for-each select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/*[local-name() = 'olef']">
				<xsl:call-template name="olef"/>
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

	<xsl:template match="olef" name="olef">
		<xsl:for-each select="element/bibliographicInformation">
			<xsl:call-template name="mods" />
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="mods" name="mods">

        <xsl:variable name="form" select="physicalDescription/form"/>
        <xsl:variable name="rform" select="physicalDescription/reformattingQuality"/>
        <xsl:variable name="intm" select="physicalDescription/internetMediaType"/>
        <xsl:variable name="extent" select="physicalDescription/extent"/>
        <xsl:variable name="dorigin" select="physicalDescription/digitalOrigin"/>
        <xsl:variable name="note" select="note"/>
        <xsl:variable name="topic" select="subject/topic"/>
        <xsl:variable name="geo" select="subject/geographic"/>
        <xsl:variable name="time" select="subject/temporal"/>
        <xsl:variable name="hierarchic" select="subject/hierarchicalGeographic"/>
        <xsl:variable name="subname" select="subject/name"/>


        <!-- SetSpec is handled twice, once for regular indexing and once for faceting,
                    The setSpec will be broken up into individual elements by the java processing
                -->

        <xsl:for-each select="*">

            <!-- titleInfo -->
            <xsl:if test="local-name() = 'titleInfo'">
                <xsl:call-template name="mods-titleInfo"/>
            </xsl:if>

            <!-- name -->
            <xsl:if test="local-name() = 'name'">
                <xsl:call-template name="mods-name"/>
            </xsl:if>

            <!-- subject -->
            <xsl:if test="local-name() = 'subject'">
                <xsl:call-template name="mods-subject"/>
            </xsl:if>

            <!-- typeOfResource -->
            <xsl:if test="local-name() = 'typeOfResource'">
                <xsl:call-template name="mods-typeOfResource"/>
            </xsl:if>

            <!-- genre -->
            <xsl:if test="local-name() = 'genre'">
                <xsl:element name="field">
                    <xsl:attribute name="name">mods_genre</xsl:attribute>
                    <xsl:for-each select="*[local-name(.) = 'genre']">
                        <xsl:if test=". != ''">
                            <xsl:if test="position() != 1">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:element>

            </xsl:if>

            <!-- collection -->
            <xsl:for-each select="relatedItem/titleInfo[@authority='dlfaqcoll']/title">
                <xsl:element name="field">
                    <xsl:attribute name="name">mods_collection</xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

            <!-- originInfo -->
            <xsl:if test="local-name() = 'originInfo'">
                <xsl:call-template name="mods-originInfo"/>
            </xsl:if>


            <!-- language -->
            <xsl:if
                test="local-name() = 'language' and child::*[local-name() = 'languageTerm' and @type='text']">
                <xsl:element name="field">
                    <xsl:attribute name="name">mods_language</xsl:attribute>
                    <xsl:for-each select="languageTerm[@type='text']">
                        <xsl:if test=". != ''">
                            <xsl:if test="position() != 1">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:element>
            </xsl:if>

            <!-- physicalDescription -->
            <xsl:if test="$form or $rform or $intm or $extent or $dorigin">
                <xsl:apply-templates select="physicalDescription"/>
            </xsl:if>

            <!-- abstract -->
            <xsl:if test="local-name() = 'abstract'">
                <xsl:call-template name="abstract"/>
            </xsl:if>


            <!-- tableOfContents -->
            <xsl:apply-templates select="tableOfContents"/>

            <!-- targetAudience -->
            <xsl:apply-templates select="targetAudience"/>

            <!-- note -->
            <xsl:if test="$note">
                <xsl:for-each select="note">
                    <xsl:if test=". != ''">
                        <xsl:element name="field">
                            <xsl:attribute name="name">mods_note</xsl:attribute>
                            <xsl:value-of select="."/>
                        </xsl:element>
                    </xsl:if>
                </xsl:for-each>
            </xsl:if>

            <!-- relatedItem -->
            <xsl:apply-templates select="relatedItem"/>

            <!-- location -->
            <xsl:apply-templates select="location"> </xsl:apply-templates>

            <!-- accessCondition -->
            <xsl:apply-templates select="accessCondition"/>

            <!-- recordInfo -->
            <xsl:if
                test="local-name() = 'recordInfo' and child::*[local-name() = 'recordContentSource']">
                <xsl:element name="field">
                    <xsl:attribute name="name">mods_record_content_source</xsl:attribute>
                    <xsl:for-each select="recordContentSource">
                        <xsl:if test=". != ''">
                            <xsl:if test="position() != 1">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:element>
            </xsl:if>

        </xsl:for-each>

    </xsl:template>



    <xsl:template match="name" name="mods-name">
        <xsl:element name="field">
            <xsl:attribute name="name">mods_name</xsl:attribute>
            <xsl:choose>
                <xsl:when test="namePart[@type='family'] or namePart[@type='given']">
                    <xsl:if test="namePart[@type='family']">
                        <xsl:value-of select="namePart[@type='family']"/>
                    </xsl:if>
                    <xsl:if test="namePart[@type='given']">
                        <xsl:if test="namePart[@type='family']">
                            <xsl:text>, </xsl:text>
                        </xsl:if>
                        <xsl:value-of select="namePart[@type='given']"/>
                    </xsl:if>
                    <xsl:if test="namePart[@type='date']">
                        <xsl:text>, </xsl:text>
                        <xsl:value-of select="namePart[@type='date']"/>
                    </xsl:if>
                </xsl:when>

                <!-- if only namePart no specific family or given name tags -->
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="namePart != ''">
                            <xsl:for-each select="namePart">
                                <xsl:value-of select="."/>
                                <xsl:if test="position()!=last()">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:when>
                        <!-- if only displayForm -->
                        <xsl:otherwise>
                            <xsl:if test="displayForm != ''">
                                <xsl:for-each select="displayForm">
                                    <xsl:value-of select="."/>
                                    <xsl:if test="position()!=last()">
                                        <xsl:text>, </xsl:text>
                                    </xsl:if>
                                </xsl:for-each>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>

            <!-- if there is text roleTerm -->
            <xsl:for-each select="role/roleTerm[@type='text']">
                <xsl:if test=". != ''">
                    <xsl:text>, </xsl:text>
                    <xsl:value-of select="."/>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>


    <xsl:template match="titleInfo" name="mods-titleInfo">
        <xsl:variable name="nsort" select="nonSort"/>
        <xsl:variable name="titl" select="title"/>
        <xsl:variable name="subt" select="subTitle"/>
        <xsl:variable name="partname" select="partName"/>
        <xsl:variable name="partNumber" select="partnum"/>

        <xsl:choose>
            <xsl:when test="@type = 'alternative' or title/@type = 'alternative'">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_alt_title</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="title"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@type = 'uniform' or title/@type = 'uniform'">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_uni_title</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="title"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@type = 'abbreviated' or title/@type = 'abbreviated'">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_abbr_title</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="title"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="@type = 'translated' or title/@type = 'translated'">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_trans_title</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="title"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="field">
                    <xsl:attribute name="name">mods_title</xsl:attribute>
                    <!-- including nsort because this is keyword search spec -->
                    <xsl:value-of select="$nsort"/>
                    <xsl:value-of select="$titl"/>

                    <xsl:for-each select="subTitle">
                        <xsl:if test=". != ''">
                            <xsl:if test="position()=1 and $titl">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                            <xsl:if test="position() != last()">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>

                    <xsl:for-each select="partName">
                        <xsl:if test=". != ''">
                            <xsl:if test="position()=1 and ($titl or $subt)">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                            <xsl:if test="position() != last()">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>

                    <xsl:for-each select="partNumber">
                        <xsl:if test=". != ''">
                            <xsl:if test="position()=1 and ($titl or $subt or $partname)">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                            <xsl:value-of select="."/>
                            <xsl:if test="position() != last()">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>



    <xsl:template match="typeOfResource" name="mods-typeOfResource">
        <xsl:element name="field">
            <xsl:attribute name="name">mods_type_of_resource</xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>


    <xsl:template match="originInfo" name="mods-originInfo">
        <!--+ 
            + first look for any date with a keyDate and any attribute with the value w3cdtf
            + then for any date with a keyDate
            + then for the first dateIssued
            + then for the first dateCreated
            + then for the first copyrightDate
            + then for the first dateOther
            +-->
        <xsl:variable name="date_splat_w3c_key_date" select="*[@keyDate and @*='w3cdtf']"/>
        <xsl:variable name="date_splat_key_date" select="*[@keyDate]"/>
        <xsl:variable name="date_created" select="dateCreated[not(@keyDate)]"/>
        <xsl:variable name="date_issued" select="dateIssued[not(@keyDate)]"/>
        <xsl:variable name="date_copyrighted" select="copyrightDate[not(@keyDate)]"/>
        <xsl:variable name="date_other" select="dateOther[not(@keyDate)]"/>
        <xsl:variable name="date_captured" select="dateCaptured[not(@keyDate)]"/>
        <xsl:variable name="date_valid" select="dateValid[not(@keyDate)]"/>
        <xsl:variable name="date_modified" select="dateModified[not(@keyDate)]"/>

        <xsl:choose>
            <xsl:when test="$date_splat_w3c_key_date">
                <xsl:element name="field">
                    <xsl:attribute name="name">mods_raw_date</xsl:attribute>
                    <xsl:value-of select="$date_splat_w3c_key_date"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="$date_splat_key_date">
                <xsl:element name="field">
                    <xsl:attribute name="name">mods_raw_date</xsl:attribute>
                    <xsl:value-of select="$date_splat_key_date"/>
                </xsl:element>
            </xsl:when>
            <xsl:when test="$date_created">
                <xsl:for-each select="$date_created">
                    <xsl:element name="field">
                        <xsl:attribute name="name">mods_raw_date</xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="@point='start'">
                                <xsl:value-of select="."/>
                                <xsl:text>-</xsl:text>
                                <xsl:value-of select="following-sibling::*[1]"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="."/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="$date_issued">
                <xsl:for-each select="$date_issued">
                    <xsl:element name="field">
                        <!--
                            special field for date_issued, all other dates are accumulated in raw_date
                        -->
                        <xsl:attribute name="name">mods_date_issued</xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="@point='start'">
                                <xsl:value-of select="."/>
                                <xsl:text>-</xsl:text>
                                <xsl:value-of select="following-sibling::*[1]"/>
                            </xsl:when>
                            <xsl:when test="@point='end'">
                                <xsl:text>n.d.</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="."/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="$date_copyrighted">
                <xsl:for-each select="$date_copyrighted">
                    <xsl:element name="field">
                        <xsl:attribute name="name">mods_raw_date</xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="@point='start'">
                                <xsl:value-of select="."/>
                                <xsl:text>-</xsl:text>
                                <xsl:value-of select="following-sibling::*[1]"/>
                            </xsl:when>
                            <xsl:when test="@point='end'">
                                <xsl:text>n.d.</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="."/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="$date_other">
                <xsl:for-each select="$date_other">
                    <xsl:element name="field">
                        <xsl:attribute name="name">mods_raw_date</xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="@point='start'">
                                <xsl:value-of select="."/>
                                <xsl:text>-</xsl:text>
                                <xsl:value-of select="following-sibling::*[1]"/>
                            </xsl:when>
                            <xsl:when test="@point='end'">
                                <xsl:text>n.d.</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="."/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                </xsl:for-each>
            </xsl:when>
        </xsl:choose>

        <xsl:if test="place/placeTerm[@type='text']">
            <xsl:element name="field">
                <xsl:attribute name="name">mods_place</xsl:attribute>
                <xsl:for-each select="place/placeTerm[@type='text']">
                    <xsl:if test=". != ''">
                        <xsl:value-of select="."/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>

        <xsl:if test="publisher">
            <xsl:element name="field">
                <xsl:attribute name="name">mods_publisher</xsl:attribute>
                <xsl:for-each select="publisher">
                    <xsl:if test=". != ''">
                        <xsl:value-of select="."/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>

        <xsl:if test="edition|issuance|frequency">
            <xsl:element name="field">
                <xsl:attribute name="name">mods_origin_aspects</xsl:attribute>
                <xsl:for-each select="edition|issuance|frequency">
                    <xsl:if test=". != ''">
                        <xsl:value-of select="."/>
                        <xsl:if test="position() != last()">
                            <xsl:text>; </xsl:text>
                        </xsl:if>
                    </xsl:if>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>

        <xsl:variable name="pl" select="place"/>
        <xsl:variable name="pub" select="publisher"/>
        <!-- BUG in next line 
            <xsl:variable name="datei" select="dateIssued" separator="== "/>
            replaced by following:
        -->
        <xsl:variable name="datei" select="dateIssued"/>
        <xsl:variable name="datec" select="dateCreated"/>
        <xsl:variable name="datecr" select="copyrightDate"/>
        <xsl:variable name="edit" select="edition"/>

        <xsl:if test="$pl or $pub or $datei or $datec or $edit">
            <xsl:element name="field">
                <xsl:attribute name="name">mods_origin</xsl:attribute>
                <xsl:if test="$pl or $pub or $datei or $datec">
                    <!-- place U concatenated with publisher T into U -->
                    <xsl:if test="$pl or $pub">
                        <!-- place U -->
                        <xsl:if test="$pl">
                            <xsl:for-each select="place/placeTerm">
                                <xsl:choose>
                                    <xsl:when test="@type = 'code'"> </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="."/>
                                        <xsl:if test="position()!=last()">
                                            <xsl:text>; </xsl:text>
                                        </xsl:if>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:for-each>
                        </xsl:if>

                        <!-- concatenated by ',' -->
                        <xsl:if test="$pl and $pub">
                            <xsl:text>, </xsl:text>
                        </xsl:if>

                        <!-- publisher T -->
                        <xsl:if test="$pub">
                            <xsl:for-each select="publisher">
                                <xsl:if test=". != ''">
                                    <xsl:value-of select="."/>
                                    <xsl:if test="position()!=last()">
                                        <xsl:text>; </xsl:text>
                                    </xsl:if>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:if>
                    </xsl:if>

                    <xsl:if test="( $pl or $pub ) and ( $datei or $datec )">
                        <xsl:text>, </xsl:text>
                    </xsl:if>

                    <!-- dateIssued YR -->
                    <xsl:if test="$datei">
                        <xsl:for-each select="dateIssued">
                            <xsl:if test=". != ''">
                                <xsl:value-of select="."/>
                                <xsl:if test="position()!=last()">
                                    <xsl:text>; </xsl:text>
                                </xsl:if>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:if>

                    <xsl:if test="$datei or $datec">
                        <xsl:text>; </xsl:text>
                    </xsl:if>

                    <!-- dateCreated YR -->
                    <xsl:if test="$datec">
                        <xsl:for-each select="dateCreated">
                            <xsl:if test=". != ''">
                                <xsl:value-of select="."/>
                                <xsl:if test="position()!=last()">
                                    <xsl:text>; </xsl:text>
                                </xsl:if>
                            </xsl:if>
                        </xsl:for-each>
                    </xsl:if>
                </xsl:if>

                <xsl:if test="$pl or $pub or $datei or $datec">
                    <xsl:text>, </xsl:text>
                </xsl:if>

                <xsl:if test="$edit">
                    <xsl:for-each select="edition">
                        <xsl:if test=". != ''">
                            <xsl:value-of select="."/>
                            <xsl:if test="position()!=last()">
                                <xsl:text>; </xsl:text>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:if>
            </xsl:element>

        </xsl:if>
    </xsl:template>


    <xsl:template match="subject" name="mods-subject">

        <xsl:variable name="topic" select="topic|occupation|titleInfo"/>
        <xsl:variable name="geo" select="geographic|hierarchicalGeographic|geographicCode"/>
        <xsl:variable name="time" select="temporal"/>
        <xsl:variable name="cart" select="cartographics"/>
        <xsl:variable name="genre" select="genre"/>
        <xsl:variable name="subname" select="name"/>

        <xsl:for-each select="$topic">
            <xsl:element name="field">
                <xsl:attribute name="name">
                    <xsl:text>mods_subject_topic</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="$geo">
            <xsl:element name="field">
                <xsl:attribute name="name">
                    <xsl:text>mods_subject_geographic</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="$time">
            <xsl:element name="field">
                <xsl:attribute name="name">
                    <xsl:text>mods_subject_temporal</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="$subname">
            <xsl:element name="field">
                <xsl:attribute name="name">
                    <xsl:text>mods_subject_name</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
        <xsl:for-each select="$genre">
            <xsl:element name="field">
                <xsl:attribute name="name">
                    <xsl:text>mods_subject_genre</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>

        <!-- slurp up all sub elements of subject into one field -->
        <xsl:if test="*">
            <xsl:element name="field">
                <xsl:attribute name="name">
                    <xsl:text>mods_subject</xsl:text>
                </xsl:attribute>
                <xsl:for-each select="*">
                    <xsl:if test=". != ''">
                        <xsl:value-of select="."/>
                        <xsl:if test="position()!=last()">
                            <xsl:text> --</xsl:text>
                        </xsl:if>
                    </xsl:if>
                </xsl:for-each>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <xsl:template match="abstract" name="abstract">
        <xsl:choose>
            <xsl:when test="@xlink">
                <xsl:element name="field">
                    <xsl:attribute name="name">
                        <xsl:text>mods_abstract</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="@xlink"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="field">
                    <xsl:attribute name="name">abstract_t</xsl:attribute>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>
