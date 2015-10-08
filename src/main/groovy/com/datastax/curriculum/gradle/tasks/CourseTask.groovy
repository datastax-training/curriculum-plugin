
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
  def srcDir = "${project.projectDir}/src"
  def slidesFile = "${srcDir}/slides.adoc"
  def exercisesFile = "${srcDir}/exercise-list.adoc"
  def solutionsFile = "${srcDir}/solutions.adoc"
  def courseModuleFile = "${srcDir}/module-list.adoc"
  def javaScriptFile = "${project.buildDir}/js/course.js"

  Map<String, String> slideHeader = [:]
  Map<String, String> exerciseHeader = [:]


  @TaskAction
  def courseAction() {
    slideHeader.customjs = 'js/course.js'
    vertexList = writeCourseModuleAsciidoc(modules)
    copyImagesAndResources()
    writeSlideAsciidoc(slidesFile, vertexList, title)
    writeMasterExerciseAsciidoc()
    writeMasterSolutionAsciidoc()
  }


  def writeCourseModuleAsciidoc(List<Map> modules) {
    def vertexList = []
    project.file(courseModuleFile).withWriter { writer ->
      modules.eachWithIndex { module, index ->
        def name = module.name
        def moduleVertices = project.file(module.vertices).collect().findAll { it }
        def slideFileName = "slides-${index+1}.adoc"
        writeSlideAsciidoc("${srcDir}/${slideFileName}", moduleVertices, name)

        writer.println ''
        writer.println "=== ${name}"
        moduleVertices.each { vertex ->
          def vertexName = extractVertexName(vertex)
          writer.println ". <<${slideFileName}#${convertVertexToAnchor(vertex)},${extractVertexName(vertex)}>>"
          vertexList << vertex
        }
      }
    }
    return vertexList
  }


  def extractVertexName(vertex) {
    def adocFile = project.file("${curriculumRootDir}/${vertex}/src/slides.adoc")
    def lines = adocFile.text.split('\n')
    def titleLine = lines.find { it.startsWith('=') }
    return titleLine[2..-1]
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


  def writeSlideAsciidoc(slidesFile, vertexList, title) {
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
