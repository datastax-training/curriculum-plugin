
package com.datastax.curriculum.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {

  String title
  String baseURL = 'slides.html'

  def vertexFile
  List<String> vertexList = []
  List<Map> modules

  def curriculumRootDir
  def slidesFile = "${project.projectDir}/src/slides.adoc"
  def exercisesFile = "${project.projectDir}/src/exercises.adoc"
  def solutionsFile = "${project.projectDir}/src/solutions.adoc"
  def javaScriptFile = "${project.buildDir}/js/course.js"
  def courseIndexFile = "${project.projectDir}/src/index.adoc"

  Map<String, String> slideHeader = [:]
  Map<String, String> exerciseHeader = [:]


  @TaskAction
  def courseAction() {
    vertexList = processModules(modules)
    slideHeader.customjs = 'js/course.js'
    copyImagesAndResources()
    writeMasterSlideAsciidoc()
    writeMasterExerciseAsciidoc()
    writeMasterSolutionAsciidoc()
  }


  def processModules(List<Map> modules) {
    def vertexList = []
    project.file(courseIndexFile).withWriter { writer ->
      writer.println "= ${title}"
      writer.println ':backend: html5'
      modules.each { module ->
        def name = module.name
        def moduleVertices = project.file(module.vertices).collect().findAll { it }
        writer.println ''
        writer.println "== ${name}"
        moduleVertices.each { vertex ->
          writer.println ". <<slides.adoc#${convertVertexToAnchor(vertex)},${vertex}>>"
          vertexList << vertex
        }
      }
    }
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


  def writeMasterSlideAsciidoc() {
    project.file(slidesFile).withWriter { writer ->
      writer.println "= ${title}"
      writer.println convertHeaderMapToString(slideHeader)
      writer.println ''
      vertexList.each { vertex ->
        writer.println ":slide_path: slides"
        writer.println ":image_path: images/${vertex}"
        writer.println "[[${convertVertexToAnchor(vertex)}]]"
        writer.println "include::${curriculumRootDir}/${vertex}/src/includes.adoc[]"
        writer.println ''
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
      writer.println ''
      vertexList.each { vertex ->
        def vertexExercisesFile = "${curriculumRootDir}/${vertex}/src/exercises.adoc"
        if(project.file(vertexExercisesFile).exists()) {
          writer.println ":exercise_number: ${exerciseNumber}"
          writer.println ":image_path: images/${vertex}"
          writer.println "[[EXERCISE-${exerciseNumber}]]"
          writer.println "include::${curriculumRootDir}/${vertex}/src/exercises.adoc[]"
          writer.println ''
          exerciseNumber++;
        }
      }
      writer.flush()
    }
  }


  def writeMasterSolutionAsciidoc() {
    int exerciseNumber = 1
    def exercisesFile = project.file(solutionsFile)
    exercisesFile.withWriter { writer ->
      writer.println "= ${title}"
      writer.println convertHeaderMapToString(exerciseHeader) // re-use exercise header
      writer.println ''
      vertexList.each { vertex ->
        def vertexSolutionsFile = "${curriculumRootDir}/${vertex}/src/solutions.adoc"
        if(project.file(vertexSolutionsFile).exists()) {
          writer.println ":exercise_number: ${exerciseNumber++}"
          writer.println ":image_path: images/${vertex}"
          writer.println "include::${curriculumRootDir}/${vertex}/src/solutions.adoc[]"
          writer.println ''
        }
      }
      writer.flush()
    }
  }


  def convertHeaderMapToString(header) {
    header.collect { key, value -> ":${key}: ${value}" }.join('\n')
  }


  def convertVertexToAnchor(vertex) {
    vertex.replace('/', '-')
  }
}
