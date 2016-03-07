
package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {

  String title
  String baseURL = 'slides.html'

  def vertexFile
  List<String> vertexList
  List<Map> modules

  def curriculumRootDir
  def srcDir = "${project.projectDir}/src"
  def exercisesFile = "${srcDir}/exercise-list.adoc"
  def solutionsFile = "${srcDir}/solution-list.adoc"
  def vertexSolutionsFile = "${srcDir}/solutions.adoc"
  def courseModuleFile = "${srcDir}/module-list.adoc"
  def javaScriptFile = "${project.buildDir}/js/course.js"

  Map<String, String> slideHeader = [:]

  def builders = []


  @TaskAction
  def courseAction() {
    slideHeader.customjs = 'js/course.js'
    buildVertexList(modules)
    builders.each { it.build() }

    copyImagesAndResources()
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

    // Copy vertex JS files to temp dir, expanding image_path macros
    vertexList.each { vertex ->
      project.copy {
        from("${curriculumRootDir}/${vertex}/js") {
          include '**/*.js'
        }
        into("${tempDir}/${vertex}/js")
        expand(['image_path': "images/${vertex}"])
      }
    }

    // Munge all vertex JS files into one big JS file
    combinedJSFile.withWriter { writer ->
      vertexList.each { vertex ->
        project.fileTree("${tempDir}/${vertex}/js").each { file ->
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
}
