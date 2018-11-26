package com.lightbend.play

abstract class WellBehavedPluginTest extends AbstractIntegrationTest {

    def "plugin does not force creation of build dir during configuration"() {
        given:
        applyPlugin()

        when:
        build('tasks')

        then:
        !file('build').exists()
    }

    def "plugin can build with empty project"() {
        given:
        applyPlugin()

        expect:
        build(mainTask)
    }

    protected applyPlugin(File target = buildFile) {
        target << """
            plugins {
                id 'com.lightbend.play-application'
                id '${getPluginName()}'
            }
        """
    }

    String getMainTask() {
        'assemble'
    }

    abstract String getPluginName()
}
