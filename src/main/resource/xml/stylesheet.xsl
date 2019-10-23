<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tt="http://www.example.org/sipvs">
    <xsl:output method="html"/>

    <xsl:template match="/tt:Application">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
        <html>
        <head>
            <style type="text/css">
                .mainblock {
                    width: 840px;
                    background-color: #F4F4F4;
                    padding-bottom: 30px;
                }

                .title {
                    padding-left: 300px;
                }

                .field {
                    padding-left: 10px;
                    padding-right: 100px;
                    display: inline-block;
                }

                table {
                    border-collapse: collapse;
                }

                tr:nth-child(odd) {
                    background: #F4F4F4;
                }

                tr:nth-child(even) {
                    background: #FFFFFF;
                }

                th, td {
                    width: 140px;
                }

                th {
                    background: #DDDDDD;
                    font-weight: bold;
                    padding-left: 70px;
                    padding-right: 70px;
                    border: 1px black solid;
                    text-align: center;
                }

                td {
                    padding-left: 10px;
                    font-size: .8em;
                }
            </style>
        </head>
        <body>
            <div class="mainblock">
                <p class="title">Course assign</p>
                <div>
                    <div class="field">
                        <p>Course Title</p>
                        <p><xsl:value-of select="//tt:CourseTitle"/></p>
                    </div>
                    <div class="field">
                        <p>Course Room</p>
                        <p><xsl:value-of select="//tt:CourseRoom"/></p>
                    </div>
                </div>
                <div>
                    <div class="field">
                        <p>Course Date</p>
                        <p><xsl:value-of select="//tt:CourseDate"/></p>
                    </div>
                    <div class="field">
                        <p>Course Time</p>
                        <p><xsl:value-of select="//tt:CourseTime"/></p>
                    </div>
                    <div class="field">
                        <p>Course Lessons</p>
                        <p><xsl:value-of select="//tt:CourseLessons"/></p>
                    </div>
                    <div class="field">
                        <p> </p>
                        <xsl:apply-templates select="//tt:CourseNewbie"/>
                    </div>
                </div>
            </div>
            <table>
                <tr>
                    <th>Student FirstName</th>
                    <th>Student LastName</th>
                    <th>Student Mobile</th>
                </tr>
                <xsl:for-each select="//tt:Students/tt:Student">
                    <tr>
                        <td>
                            <xsl:value-of select="//tt:StudentFirstName"/>
                        </td>
                        <td>
                            <xsl:value-of select="//tt:StudentLastName"/>
                        </td>
                        <td>
                            <xsl:value-of select="//tt:StudentMobile"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </body>
        </html>
    </xsl:template>

    <xsl:template match="/tt:Application/tt:CourseNewbie">
        <p>
            <xsl:choose>
                <xsl:when test="string(.) = 'true'">✔ </xsl:when>
                <xsl:otherwise>  </xsl:otherwise>
            </xsl:choose>
            Is newbie?
        </p>
    </xsl:template>

</xsl:stylesheet>