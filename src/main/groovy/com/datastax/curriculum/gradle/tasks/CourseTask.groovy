
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

  Map<String, String> slideHeader = [:]
  Map<String, String> exerciseHeader = [:]


  @TaskAction
  def courseAction() {
    vertexList = project.file(vertexFile).collect()
    slideHeader.customjs = '../../js/course.js'
    copyImagesAndResources()
    writeMasterSlideAsciidoc()
    writeMasterExerciseAsciidoc()
  }


  def copyImagesAndResources() {
    def file = project.file(javaScriptFile)
    def fullPath = file.absolutePath
    project.file(fullPath[0..(fullPath.lastIndexOf(File.separator))]).mkdirs()
    combineVertexJavaScript(file)
    copyVertexImages()
  }


  def combineVertexJavaScript(File combinedJSFile) {
    def tempDir = File.createTempDir()

    println "putting stuff in ${tempDir}"
    vertexList.each { vertex ->
      project.copy {
        println "copying from ${curriculumRootDir}/${vertex}/js"
        from("${curriculumRootDir}/${vertex}/js") {
          include '**/*.js'
        }
        println "into ${tempDir}/${vertex}/js"
        into("${tempDir}/${vertex}/js")
        expand(['image_path': "../../images/${vertex}"])
      }
    }

    combinedJSFile.withWriter { writer ->
      vertexList.each { vertex ->
        project.fileTree("${tempDir}/${vertex}/js").each { file ->
          println "merging ${file}"
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
      writer.println '\n\n'
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


  def relativize(basePath, path) {
    new File(basePath).toURI().relativize(new File(path).toURI()).getPath()
  }


  def convertHeaderMapToString(header) {
    header.collect { key, value -> ":${key}: ${value}" }.join('\n')
  }
}
