<#if playVersion == "2.8" || playVersion == "2.7" || playVersion == "2.6">
play.http.secret.key="TY9[b`xw2MeXUt;M<i_B0kUKm8/?PD1cS1WhFYyZ[1^6`Apew34q6DyNL=UqG/1l"

<#else>
application.secret="TY9[b`xw2MeXUt;M<i_B0kUKm8/?PD1cS1WhFYyZ[1^6`Apew34q6DyNL=UqG/1l"
application.langs="en"

# Root logger:
logger.root=ERROR

# Logger used by the framework:
logger.play=INFO

# Logger provided to your application:
logger.application=DEBUG

</#if>