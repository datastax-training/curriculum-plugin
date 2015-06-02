
package com.datastax.curriculum.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {
  File vertices
  String title
  File curriculumRootDir

  @TaskAction
  def courseAction() {
    File slidesFile
    File exercisesFile

    slidesFile = project.file('src/slides.adoc')
    slidesFile.withWriter { writer ->
      writer.println """\
= ${title}
:backend: deckjs
:deckjs_theme: datastax
:deckjs_transition: fade
:navigation:
:status:
:notes:
:split:
"""
      vertices.each { vertex ->
        writer.println ":slide_path: slides"
        writer.println ":image_path: ${project.buildDir}/images/${vertex}"
        writer.println "include::${curriculumRootDir}/${vertex}/src/includes.adoc[]"
        project.copy {
          from "${curriculumRootDir}/${vertex}/images"
          into "${project.buildDir}/images/${vertex}"
        }
      }
      writer.flush()
    }


    exercisesFile = project.file('src/exercises.adoc')
    exercisesFile.withWriter { writer ->
      writer.println """\
= ${title}
:backend: html5
"""
      vertices.each { vertex ->
        def vertexExercisesFile = "${curriculumRootDir}/${vertex}/src/exercises.adoc"
        if(project.file(vertexExercisesFile).exists()) {
          writer.println ":image_path: ${project.buildDir}/images/${vertex}"
          writer.println "include::../${exercisesFile}[]"
        }
      }
      writer.flush()
    }
  }
}
