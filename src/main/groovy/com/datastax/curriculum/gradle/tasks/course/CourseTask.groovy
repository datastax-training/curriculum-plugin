
package com.datastax.curriculum.gradle.tasks.course

import com.datastax.curriculum.gradle.Course
import com.datastax.curriculum.gradle.Module
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {

  String title
  Map<String, String> slideHeader = [:]
  List<Map<String, String>> modules

  def curriculumRootDir
  def srcDir = "${project.projectDir}/src"

  Course course


  @TaskAction
  def courseAction() {
    course.buildTo(project.buildDir)
  }


  void setModules(List<Map<String, String>> modules) {
    this.modules = modules

    // Doing all this work in a setter is filthy, and I know it.
    course = new Course(title)
            .withCurriculumRoot(curriculumRootDir)
            .withSrcDir(srcDir)
    course.slideHeader = slideHeader

    modules.each { moduleDescription ->
      def moduleFilename = "${project.projectDir}/${moduleDescription.vertices}"
      def module = new Module(moduleDescription.name)
              .withCurriculumRoot(curriculumRootDir)
              .withModuleFile(moduleFilename)
      course.addModule(module)
    }

    inputs.files(course.allDependencies)
    outputs.dir project.buildDir
  }

}
