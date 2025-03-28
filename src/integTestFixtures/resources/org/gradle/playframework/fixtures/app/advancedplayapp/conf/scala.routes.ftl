<#if playVersion == "2.8" || playVersion == "2.7">
GET        /one         @controllers.scla.MixedJava.index
<#else>
GET        /one         controllers.scla.MixedJava.index
</#if>
<#if playVersion == "2.8" || playVersion == "2.7" || playVersion == "2.6">
POST       /two         @special.strangename.Application.index
<#else>
POST       /two         special.strangename.Application.index
</#if>
