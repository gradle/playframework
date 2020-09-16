<#if playVersion == "2.7" || playVersion == "2.6">
GET     /                          @controllers.Application.index
GET     /shutdown                  @controllers.Application.shutdown
GET     /assets/*file              @controllers.Assets.at(path="/public", file)

<#else>
GET     /                          controllers.Application.index
GET     /shutdown                  controllers.Application.shutdown
GET     /assets/*file              controllers.Assets.at(path="/public", file)

</#if>
