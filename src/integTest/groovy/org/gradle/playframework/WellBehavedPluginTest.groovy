package org.gradle.playframework

import static org.gradle.playframework.fixtures.Repositories.playRepositories

abstract class WellBehavedPluginTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id '${getPluginName()}'
                id 'idea'
            }
            
            ${playRepositories()}
        """
    }

    // TODO: End result should be that only the help task is realized
    def "does not realize all possible tasks"() {
        buildFile << """
            def configuredTasks = []
            tasks.configureEach {
                configuredTasks << it
            }
            
            gradle.buildFinished {
                def configuredTaskPaths = configuredTasks*.path
                
                assert configuredTaskPaths == [':compilePlayTwirlTemplates', ':help']
            }
        """

        expect:
        build("help")
    }

    abstract String getPluginName()
}
