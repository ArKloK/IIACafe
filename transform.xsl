<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <bebida>
      <xsl:for-each select="/bebida">
		<nombre><xsl:value-of select="nombre"/></nombre>
      </xsl:for-each>
  </bebida>
</xsl:template>
</xsl:stylesheet>
