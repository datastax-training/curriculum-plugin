
package com.datastax.curriculum.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {

  String title

  def vertexFile
  List<String> vertexList

  def curriculumRootDir
  def slidesFile = "${project.projectDir}/src/slides.adoc"
  def exercisesFile = "${project.projectDir}/src/exercises.adoc"
  def javaScriptFile = "${project.buildDir}/js/course.js"

  Map<String, String> slideHeader
  Map<String, String> exerciseHeader


  @TaskAction
  def courseAction() {
    vertexList = project.file(vertexFile).collect()
    copyImagesAndResources()
    writeMasterSlideAsciidoc()
    writeMasterExerciseAsciidoc()
  }


  def copyImagesAndResources() {
    combineVertexJavaScript(project.file(javaScriptFile))
    copyVertexImages()
  }


  def combineVertexJavaScript(File combinedJSFile) {
    combinedJSFile.withWriter { writer ->
      vertexList.each { vertex ->
        project.fileTree("${curriculumRootDir}/${vertex}/js").each { file ->
          file.withReader { reader ->
            writer.write(reader.text)
          }
        }
      }
      writer.flush()
    }
  }


  def copyVertexImages() {
    vertexList.each { vertex ->
      project.copy {
        from "${curriculumRootDir}/${vertex}/images"
        into "${project.buildDir}/images/${vertex}"
      }
    }
  }


  def writeMasterSlideAsciidoc() {
    project.file(slidesFile).withWriter { writer ->
      writer.println "= ${title}"
      writer.println convertHeaderMapToString(slideHeader)
      vertexList.each { vertex ->
        writer.println ":slide_path: slides"
        writer.println ":image_path: ../../images/${vertex}"
        writer.println "include::${curriculumRootDir}/${vertex}/src/includes.adoc[]"
      }
      writer.flush()
    }
  }


  def writeMasterExerciseAsciidoc() {
    int exerciseNumber = 1
    def exercisesFile = project.file(exercisesFile)
    exercisesFile.withWriter { writer ->
      writer.println "= ${title}"
      writer.println convertHeaderMapToString(exerciseHeader)
      vertexList.each { vertex ->
        def vertexExercisesFile = "${curriculumRootDir}/${vertex}/src/exercises.adoc"
        if(project.file(vertexExercisesFile).exists()) {
          writer.println ":exercise_number: ${exerciseNumber++}"
          writer.println ":image_path: ../../images/${vertex}"
          writer.println "include::${curriculumRootDir}/${vertex}/src/exercises.adoc[]"
        }
      }
      writer.flush()
    }
  }


  def convertHeaderMapToString(header) {
    header.collect { key, value -> ":${key}: ${value}" }.join('\n')
  }
}
