<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:php="http://php.net/xsl" exclude-result-prefixes="php">
<xsl:variable name="OBJECTSPAGE">
		 	<xsl:value-of select="$objectsPage"/>
</xsl:variable>
	<xsl:variable name="ALLOWEDNAMESPACES" select="$allowedPidNameSpaces"/>
  <xsl:variable name="FULLQUERY" select="$fullQuery"/>
<!--<xsl:variable name="PATHTOMAKEIMAGE">
		 	<xsl:value-of select="$pathToMakeImage"/>
		</xsl:variable>-->

<xsl:template match="gfindObjects">
<xsl:variable name="INDEXNAME" select="@indexName"/>
    
		<xsl:variable name="PREQUERY" select="substring-before(@query,':')"/>
		<xsl:variable name="QUERY" select="substring-after(@query,':')"/>
		<xsl:variable name="HITPAGESTART" select="@hitPageStart"/>
		<xsl:variable name="HITPAGESIZE" select="@hitPageSize"/>
		<xsl:variable name="HITTOTAL" select="@hitTotal"/>

		<xsl:variable name="SEARCHURL">
		 	<xsl:value-of select="$searchUrl"/>
		</xsl:variable>
		<xsl:variable name="TOKEN">
		 	<xsl:value-of select="$searchToken"/>
		</xsl:variable>
		<xsl:variable name="HITPAGEEND">
			<xsl:choose>
				<xsl:when test="$HITPAGESTART + $HITPAGESIZE - 1 > $HITTOTAL">
					<xsl:value-of select="$HITTOTAL"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$HITPAGESTART + $HITPAGESIZE - 1"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="HITPAGENO" select="ceiling($HITPAGESTART div $HITPAGESIZE)"/>
		<xsl:variable name="HITPAGENOLAST" select="ceiling($HITTOTAL div $HITPAGESIZE)"/>
		<xsl:variable name="HITPAGESTARTT" select="(($HITPAGENO - 1) * $HITPAGESIZE + 1)"/>
<xsl:choose>
<xsl:when test="$HITTOTAL > 0">

		Total Hits = <strong><xsl:value-of select="$HITTOTAL"/>,</strong>
		 Number of Hits/page = <xsl:value-of select="$HITPAGESIZE"/> 
		<!-- <br />You may not have sufficient privileges to view any or all of the items found.  The objects you have rights to view will be shown below.-->
		<!--Current page = <xsl:value-of select="@hitPageStart"/>-->
		 <br/>
		<!--<form action="/drupal-5.1/?q=search/fedora_collections"  method="post" id="search-form" class="search-form">-->
	 <br/>
   <xsl:if test="$HITTOTAL > $HITPAGESIZE">
   <div class="item-list">
     <ul class="pager">
                  <xsl:if test="$HITPAGENO > 1">
                    <li class="pager-previous">
                       <a>
                     <xsl:attribute name="href"><xsl:copy-of select="$OBJECTSPAGE"/>fedora/ilives_book_search/<xsl:value-of select="$FULLQUERY"/>/<xsl:value-of select="$HITPAGESTARTT - $HITPAGESIZE"/></xsl:attribute>
                  &lt; Previous
                     </a>
                     </li>
    		  </xsl:if><xsl:text>    </xsl:text>
                  <xsl:if test="$HITPAGENO &lt; $HITPAGENOLAST">
                      <li class="pager-next">
                 <a>
                     <xsl:attribute name="href"><xsl:copy-of select="$OBJECTSPAGE"/>fedora/ilives_book_search/<xsl:value-of select="$FULLQUERY"/>/<xsl:value-of select="$HITPAGESTARTT+$HITPAGESIZE"/></xsl:attribute>
                  Next >
                     </a>
                     </li>
                     </xsl:if>
                     </ul>
                     </div>
                     </xsl:if>
		<style type="text/css">

		span.highlight
		{
		background-color:yellow
		}

		span.searchtitle
		{
		font-weight: bold
		}
</style>
<xsl:apply-templates select="objects"/>
<xsl:if test="$HITTOTAL > $HITPAGESIZE">
    <div class="item-list">
     <ul class="pager">
                  <xsl:if test="$HITPAGENO > 1">
                    <li class="pager-previous">
                       <a>
                     <xsl:attribute name="href"><xsl:copy-of select="$OBJECTSPAGE"/>fedora/ilives_book_search/<xsl:value-of select="$FULLQUERY"/>/<xsl:value-of select="$HITPAGESTARTT - $HITPAGESIZE"/></xsl:attribute>
                  &lt; Previous
                     </a>
                     </li>
    		  </xsl:if><xsl:text>    </xsl:text>
                  <xsl:if test="$HITPAGENO &lt; $HITPAGENOLAST">
                      <li class="pager-next">
                 <a>
                     <xsl:attribute name="href"><xsl:copy-of select="$OBJECTSPAGE"/>fedora/ilives_book_search/<xsl:value-of select="$FULLQUERY"/>/<xsl:value-of select="$HITPAGESTARTT+$HITPAGESIZE"/></xsl:attribute>
                  Next >
                     </a>
                     </li>
                     </xsl:if>
                     </ul>
                     </div>
                     </xsl:if>
</xsl:when>
	<xsl:otherwise>
	<div class="box">

  <h2>Your search yielded no results</h2>

  <div class="content"><ul>
<li>Check if your spelling is correct.</li>

<li>Remove quotes around phrases to match each word individually: <em>"blue smurf"</em> will match less than <em>blue smurf</em>.</li>
<li>Consider loosening your query with <em>OR</em>: <em>blue smurf</em> will match less than <em>blue OR smurf</em>.</li>
</ul></div>

</div>

	</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="objects">

    <table cellpadding="3" cellspacing="3" width="100%"><div class="search-results">
      <tr><td colspan="2">



      </td></tr>
    <xsl:for-each select="object">
			<xsl:variable name="PIDVALUE">
                    <xsl:choose>
                        <xsl:when test="@PID">
                            <xsl:value-of select="@PID"/>
                        </xsl:when>
                        <xsl:when test="field[@name='PID' and @snippet='yes']">
                            <xsl:value-of select="field[@name='PID']/span/text()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="field[@name='PID']/text()"/>
                        </xsl:otherwise>
                    </xsl:choose>
			</xsl:variable>


    	<xsl:call-template name="splitBySpace">
    		<xsl:with-param name="PIDVALUE" select="$PIDVALUE"></xsl:with-param>
    		<xsl:with-param name="str" select="$ALLOWEDNAMESPACES"/>
       
    	</xsl:call-template>


    </xsl:for-each>
        </div></table>

</xsl:template>
	<xsl:template name="splitBySpace">
   
		<xsl:param name="str"/>
		<xsl:param name="PIDVALUE"/>	
		<xsl:choose>
			<xsl:when test="contains($str,' ')">
				<!--'DO SOMETHING WITH THE VALUE IN
				{substring-before($str,' ')}-->
				<xsl:variable name="testString" select="substring-before($str,' ')"/>
				
				 <xsl:if test="starts-with($PIDVALUE,$testString)">	
				 	
					<xsl:call-template name="showResult">
						
						<xsl:with-param name="PIDVALUE" select="$PIDVALUE"/>						
					</xsl:call-template>
				 	
				 </xsl:if>
				
				<!--<xsl:value-of	select="substring-before($str,' ')"/>-->
				<xsl:call-template name="splitBySpace">
					<xsl:with-param name="str"
					select="substring-after($str,' ')"/>
					<xsl:with-param name="PIDVALUE"
					select="$PIDVALUE"/>
					
					
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
                 <!--<xsl:if test="substring-before($str, ':') = substring-before($PIDVALUE, ':')">
                     <xsl:call-template name="showResult">
                         <xsl:with-param name="PIDVALUE" select="$PIDVALUE"/>
                        
                     </xsl:call-template>                    
                 </xsl:if>-->
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

<xsl:template name="showResult">
	<xsl:param name="PIDVALUE"/>
  
    <xsl:variable name="DCTITLE">
        <xsl:value-of select="field[@name='dc.title']/node()"/>
    </xsl:variable>

    <xsl:variable name="CLEANTITLE">
         <xsl:value-of select="php:functionString('fedora_repository_urlencode_string', $DCTITLE)"/>
        </xsl:variable>
	<tr><td valign="top">
		<a>
			<xsl:attribute name="href"><xsl:copy-of select="$OBJECTSPAGE"/>fedora/ilives/<xsl:copy-of select="$PIDVALUE"/>/<xsl:value-of select="$FULLQUERY"/>
			</xsl:attribute>
			<!--<xsl:attribute name="href"><xsl:copy-of select="$OBJECTSPAGE"/><![CDATA[&pid=]]><xsl:value-of select="$PIDVALUE"/><![CDATA[&collection=object]]>

				</xsl:attribute>-->
			<img>
				<xsl:attribute name="src"><xsl:copy-of select="$OBJECTSPAGE"/>fedora/repository/<xsl:copy-of select="$PIDVALUE"/>/TN
				</xsl:attribute>
                <xsl:attribute name="height">100</xsl:attribute>
			</img>
           
		</a>

	</td  >

		<td  valign="top">
			


				
					
                          <span class="searchtitle">
						<a>
							<xsl:attribute name="href"><xsl:copy-of select="$OBJECTSPAGE"/>fedora/ilives/<xsl:value-of select="$PIDVALUE"/>/<xsl:value-of select="$FULLQUERY"/>

							</xsl:attribute>
                          
							<xsl:for-each select="field[@name='mods.title']">
                               <!-- <xsl:value-of select="substring-before(text(),'/')"/>-->
                                <xsl:value-of select="node()"/><xsl:text> </xsl:text>
                                </xsl:for-each>
                            <xsl:for-each select="field[@name='mods.subTitle']">
                                <span class="searchsubtitle">
                               <xsl:text> : </xsl:text><xsl:value-of select="node()"/>
                                </span>
                                </xsl:for-each>                            
							
                        
						</a>
                       </span>
                         
                         
							<xsl:for-each select="field[@name='mods.sor']">
                                <span class="searchcreator">
                               <xsl:text> / </xsl:text> <xsl:value-of select="node()"/>
                               </span>
                                </xsl:for-each>
                            
                           
							<xsl:for-each select="field[@name='mods.place_of_publication']">
                                 <span class="searchpop">
                               <xsl:text> </xsl:text> <xsl:value-of select="node()"/>
                                </span>
                                </xsl:for-each>
                           
                           
							<xsl:for-each select="field[@name='mods.publisher']">
                                 <span class="searchpop">
                               <xsl:text> : </xsl:text> <xsl:value-of select="node()"/>
                                 </span>
                                </xsl:for-each>
                          
                            
							<xsl:for-each select="field[@name='mods.dateIssued']">
                                 <span class="searchdateIssued">
                               <xsl:text>, </xsl:text> <xsl:value-of select="node()"/>.
                               </span>
                                </xsl:for-each>
                            
                            
							<xsl:for-each select="field[@name='mods.edition']">
                                  <span class="searchedition">
                               <xsl:text> </xsl:text> <xsl:value-of select="node()"/>
                                </span>
                                </xsl:for-each>
                           



					</td>
                    </tr>


				<!--
                     <xsl:for-each select="field">
					<xsl:choose>
						<xsl:when test="(@name='dc.titledsm.OBJ')">

							<tr>
								<td  valign="top">
									<span class="searchtitle">
										Text Stream
									</span>
								</td>
								<td>
									<span class="text">
										<xsl:copy-of select="node()"/>
									</span>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="(@name='dc.title')">

						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td  valign="top">
									<span class="searchtitle">
										<xsl:value-of select="@name"/>
									</span>
								</td>
								<td>
									<span class="text">
										<xsl:copy-of select="node()"/>
									</span>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
						-->



				
			

		
</xsl:template>

</xsl:stylesheet>
