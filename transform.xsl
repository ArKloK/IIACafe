<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <drink>
      <xsl:for-each select="/drink">
		<name><xsl:value-of select="name"/></name>
      </xsl:for-each>
  </drink>
</xsl:template>
</xsl:stylesheet>
