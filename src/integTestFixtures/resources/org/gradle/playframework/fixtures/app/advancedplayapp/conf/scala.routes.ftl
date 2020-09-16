<#if playVersion == "2.7" || playVersion == "2.6">
GET        /one         controllers.scla.MixedJava.index
POST       /two         @special.strangename.Application.index

<#else>
GET        /one         controllers.scla.MixedJava.index
POST       /two         special.strangename.Application.index

</#if>
