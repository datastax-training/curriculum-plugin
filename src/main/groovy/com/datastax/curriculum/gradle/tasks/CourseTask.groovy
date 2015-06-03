
package com.datastax.curriculum.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {
  File vertices
  String title
  File curriculumRootDir
  String slideHeader
  String exerciseHeader
  String slidesFilename = 'src/slides.adoc'
  String exercisesFilename = 'src/exercises.adoc'

  @TaskAction
  def courseAction() {
    vertices.each { vertex ->
      project.copy {
        from "${curriculumRootDir}/${vertex}/images"
        into "${project.buildDir}/images/${vertex}"
      }
    }

    project.file(slidesFilename).withWriter { writer ->
      writer.println slideHeader
      vertices.each { vertex ->
        writer.println ":slide_path: slides"
        writer.println ":image_path: ../../images/${vertex}"
        writer.println "include::${curriculumRootDir}/${vertex}/src/includes.adoc[]"
      }
      writer.flush()
    }

    def exercisesFile = project.file(exercisesFilename)
    exercisesFile.withWriter { writer ->
      writer.println exerciseHeader
      vertices.each { vertex ->
        def vertexExercisesFile = "${curriculumRootDir}/${vertex}/src/exercises.adoc"
        if(project.file(vertexExercisesFile).exists()) {
          writer.println ":image_path: ../../images/${vertex}"
          writer.println "include::${curriculumRootDir}/${vertex}/src/exercises.adoc[]"
        }
      }
      writer.flush()
    }
  }
}
