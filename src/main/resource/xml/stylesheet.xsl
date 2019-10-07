<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <xsl:template match="/Applications/Application">
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
        <html><body>
            <div class="mainblock">
                <p class="title">Course assign</p>
                <div>
                    <div class="field">
                        <p>Course Title</p>
                        <p><xsl:value-of select="CourseTitle"/></p>
                    </div>
                    <div class="field">
                        <p>Course Room</p>
                        <p><xsl:value-of select="CourseRoom"/></p>
                    </div>
                </div>
                <div>
                    <div class="field">
                        <p>Course Date</p>
                        <p><xsl:value-of select="CourseDate"/></p>
                    </div>
                    <div class="field">
                        <p>Course Time</p>
                        <p><xsl:value-of select="CourseTime"/></p>
                    </div>
                    <div class="field">
                        <p>Course Lessons</p>
                        <p><xsl:value-of select="CourseLessons"/></p>
                    </div>
                    <div class="field">
                        <p> </p>
                        <xsl:apply-templates select="CourseNewbie"/>
                    </div>
                </div>
            </div>
            <table>
                <tr>
                    <th>Student FirstName</th>
                    <th>Student LastName</th>
                    <th>Student Mobile</th>
                </tr>
                <xsl:for-each select="Students/Student">
                    <tr>
                        <td>
                            <xsl:value-of select="StudentFirstName"/>
                        </td>
                        <td>
                            <xsl:value-of select="StudentLastName"/>
                        </td>
                        <td>
                            <xsl:value-of select="StudentMobile"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
        </body></html>
    </xsl:template>

    <xsl:template match="/Applications/Application/CourseNewbie">
        <p>
            <xsl:choose>
                <xsl:when test="string(.) = 'true'">✔ </xsl:when>
                <xsl:otherwise>  </xsl:otherwise>
            </xsl:choose>
            Is newbie?
        </p>
    </xsl:template>

</xsl:stylesheet>