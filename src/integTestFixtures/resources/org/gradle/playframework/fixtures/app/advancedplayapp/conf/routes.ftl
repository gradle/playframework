<#if playVersion == "2.7" || playVersion == "2.6">
# Routes
GET /          @controllers.Application.index
GET /root      @controllers.Application.root
GET /shutdown  @controllers.Application.shutdown

->  /scala     scala.Routes
->  /java      jva.Routes

# Map static resources from the /public folder to the /assets URL path
GET     /assets/*file               @controllers.Assets.at(path="/public", file)

<#else>
# Routes
GET /          controllers.Application.index
GET /root      controllers.Application.root
GET /shutdown  controllers.Application.shutdown

->  /scala     scala.Routes
->  /java      jva.Routes

# Map static resources from the /public folder to the /assets URL path
GET     /assets/*file               controllers.Assets.at(path="/public", file)

</#if>
