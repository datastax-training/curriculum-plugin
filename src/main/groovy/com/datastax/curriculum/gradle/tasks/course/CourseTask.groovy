
package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {

  String title
  String baseURL = 'slides.html'

  def vertexFile
  List<String> vertexList = []
  List<Map> modules

  def curriculumRootDir
  def srcDir = "${project.projectDir}/src"
  def exercisesFile = "${srcDir}/exercise-list.adoc"
  def solutionsFile = "${srcDir}/solutions-list.adoc"
  def courseModuleFile = "${srcDir}/module-list.adoc"
  def javaScriptFile = "${project.buildDir}/js/course.js"

  Map<String, String> slideHeader = [:]

  def builders = []


  CourseTask() {
    builders << new SolutionBuilder(this)
    builders << new ModuleBuilder(courseTask: this)
    builders << new ExerciseBuilder(courseTask: this)
  }


  @TaskAction
  def courseAction() {
    slideHeader.customjs = 'js/course.js'
    vertexList = buildVertexList(modules)

    builders.each { it.build() }

    copyImagesAndResources()
  }


  def buildVertexList(modules) {
    def vertexList = []
    modules.eachWithIndex { module, index ->
      def name = module.name
      def moduleVertices = project.file(module.vertices).collect().findAll { it }
      vertexList.addAll(moduleVertices)
    }d

    return vertexList
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
