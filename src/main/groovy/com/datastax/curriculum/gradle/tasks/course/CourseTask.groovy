
package com.datastax.curriculum.gradle.tasks.course

import com.datastax.curriculum.gradle.Course
import com.datastax.curriculum.gradle.Module
import com.datastax.curriculum.gradle.dsl.CourseDefinitionParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {

  def curriculumRootDir
  def srcDir = "${project.projectDir}/src"
  def definition

  Course course


  @TaskAction
  def courseAction() {
    course.buildTo(project.buildDir)
  }


  void setDefinition(definition) {
    CourseDefinitionParser parser = new CourseDefinitionParser(curriculumRootDir)
    this.definition = definition

    // Doing all this work in a setter is filthy, and I know it.
    if(definition instanceof File) {
      course = parser.parse(definition.text)
    }
    else if(definition instanceof String ||
            definition instanceof GString) {
      course = parser.parse(definition as String)
    }
    else if(definition instanceof Closure) {
      course = parser.buildCourseFromClosure(definition)
    }
    else {
      throw new RuntimeException("definition?.getClass() is not a supported definition type")
    }

    course.withSrcDir(srcDir)

    inputs.files(course.allDependencies)
    outputs.dir project.buildDir
  }

}
