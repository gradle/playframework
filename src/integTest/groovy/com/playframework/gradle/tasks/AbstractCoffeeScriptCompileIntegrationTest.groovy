package com.playframework.gradle.tasks

abstract class AbstractCoffeeScriptCompileIntegrationTest extends AbstractJavaScriptMinifyIntegrationTest {

    File getProcessedJavaScriptDir() {
        file("build/src/play/javaScript")
    }

    File getCompiledCoffeeScriptDir() {
        file("build/src/play/coffeeScript")
    }

    File compiledCoffeeScript(String fileName) {
        new File(getCompiledCoffeeScriptDir(), fileName)
    }

    void hasProcessedCoffeeScript(file) {
        hasExpectedJavaScript(compiledCoffeeScript("${file}.js" ))
        hasExpectedJavaScript(processedJavaScript("${file}.js" ))
        hasMinifiedJavaScript(processedJavaScript("${file}.min.js"))
    }

    def withCoffeeScriptSource(String path) {
        withCoffeeScriptSource(file(path))
    }

    def withCoffeeScriptSource(File file) {
        file.text = coffeeScriptSource()
        file
    }

    def coffeeScriptSource() {
        return """
# Assignment:
number   = 42
opposite = true

# Conditions:
number = -42 if opposite

# Functions:
square = (x) -> x * x

# Arrays:
list = [1, 2, 3, 4, 5]

# Objects:
math =
  root:   Math.sqrt
  square: square
  cube:   (x) -> x * square x

# Splats:
race = (winner, runners...) ->
  print winner, runners

# Existence:
alert "I knew it!" if elvis?

# Array comprehensions:
cubes = (math.cube num for num in list)"""
    }
}
