<?xml version="1.0" encoding="UTF-8"?>
<!--Converts Element Rows into Attribute Rows-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/*">
		<xsl:element name="{local-name(.)}">
			<xsl:apply-templates select="Row"/>		
		</xsl:element>
	</xsl:template>
		<!--xsl:apply-templates select="*"  didn't work: Attributes can only be added within the same Template /-->
	<xsl:template match="Row">
		<Row>
			<xsl:for-each select="*">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>
		</Row>
	</xsl:template>
</xsl:stylesheet>
