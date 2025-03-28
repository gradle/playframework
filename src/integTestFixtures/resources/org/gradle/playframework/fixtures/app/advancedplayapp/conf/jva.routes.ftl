<#if playVersion == "2.8" || playVersion == "2.7">
GET        /one         @controllers.jva.PureJava.index
<#else>
GET        /one         controllers.jva.PureJava.index
</#if>
