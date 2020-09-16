<#if playVersion == "2.7" || playVersion == "2.6">
# Routes
# Home page
GET     /                           @controllers.Application.index

GET     /shutdown                   @controllers.Application.shutdown

# Map static resources from the /public folder to the /assets URL path
GET     /assets/*file               @controllers.Assets.at(path="/public", file)

<#else>
# Routes
# Home page
GET     /                           controllers.Application.index

GET     /shutdown                   controllers.Application.shutdown

# Map static resources from the /public folder to the /assets URL path
GET     /assets/*file               controllers.Assets.at(path="/public", file)

</#if>
