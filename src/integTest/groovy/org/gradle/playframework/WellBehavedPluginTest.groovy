package org.gradle.playframework

import groovy.transform.NotYetImplemented

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

    @NotYetImplemented
    def "does not realize all possible tasks"() {
        buildFile << """
            def configuredTasks = []
            tasks.configureEach {
                configuredTasks << it
            }
            
            gradle.buildFinished {
                def configuredTaskPaths = configuredTasks*.path
                
                assert configuredTaskPaths == [':help']
            }
        """

        expect:
        build("help")
    }

    abstract String getPluginName()
}
