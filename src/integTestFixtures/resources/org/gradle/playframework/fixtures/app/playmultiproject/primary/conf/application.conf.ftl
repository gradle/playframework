<#if playVersion == "2.7" || playVersion == "2.6">
# https://www.playframework.com/documentation/2.6.x/ApplicationSecret
play.http.secret.key="somethingsecret"

<#else>
application.secret="changeme"
application.langs="en"

# Root logger:
logger.root=ERROR

# Logger used by the framework:
logger.play=INFO

# Logger provided to your application:
logger.application=DEBUG

</#if>
