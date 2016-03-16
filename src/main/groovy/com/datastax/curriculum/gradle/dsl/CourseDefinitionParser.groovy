package com.datastax.curriculum.gradle.dsl

import com.datastax.curriculum.gradle.Course


class CourseDefinitionParser {
  def curriculumRoot


  CourseDefinitionParser(curriculumRoot) {
    this.curriculumRoot = curriculumRoot
  }


  Course parse(String scriptText) {
    def binding = new Binding()
    def shell = new GroovyShell(binding)

    def script = shell.parse(scriptText)
    script.metaClass.methodMissing = scriptMethodMissing
    script.run()
  }


  def getScriptMethodMissing() {
    { name, args ->
      if(name == 'course') {
        processScriptRootElement(args)
      }
      else {
        throw new RuntimeException("Unrecognized root element ${name}")
      }
    }
  }

  def buildCourseFromClosure(Closure closure) {
    def delegate = new CourseBuilder(curriculumRoot)
    closure.delegate = delegate
    closure.resolveStrategy = Closure.DELEGATE_ONLY
    closure.call()
    return delegate.course
  }


  def processScriptRootElement(args) {
    switch(args.size()) {
      case 0:
        throw new RuntimeException("Course description element cannot be empty")

      case 1:
        def closure = args[0]
        if(!(closure instanceof Closure)) {
          throw new RuntimeException("course element must be followed by a closure (course { ... })")
        }
        return buildCourseFromClosure(closure)

      case 2:
        def params = args[0]
        def closure = args[1]
        if(!(params instanceof Map)) {
          throw new RuntimeException("course element must take parameters followed by a closure (course(key: value) { ... })")
        }
        if(!(closure instanceof Closure)) {
          throw new RuntimeException("course element must take parameters followed by a closure (course(key: value) { ... })")
        }
        return buildCourseFromClosure(closure)

      default:
        throw new RuntimeException("course element has too many parameters: ${args}")
    }
  }

}
