package com.datastax.curriculum.gradle


class Vertex {
  String vertexPath
  File curriculumRoot
  File vertexDir
  File includes
  File slides
  File objectives
  File exercises
  File solutions
  File javaScript
  File imageDir
  List<File> images = []


  Vertex(vertexPath) {
    this.vertexPath = vertexPath
  }


  void setCurriculumRoot(curriculumRoot) {
    if(curriculumRoot instanceof File) {
      this.curriculumRoot = curriculumRoot.absoluteFile
    }
    else {
      this.curriculumRoot = new File(curriculumRoot).absoluteFile
    }

    vertexDir = new File(curriculumRoot, vertexPath)
    includes = new File(vertexDir, 'src/includes.adoc')
    slides = new File(vertexDir, 'src/slides.adoc')
    objectives = new File(vertexDir, 'src/objectives.adoc')
    exercises = new File(vertexDir, 'src/exercises.adoc')
    solutions = new File(vertexDir, 'src/solutions.adoc')
    javaScript = new File(vertexDir, 'js/animation.js')
    imageDir = new File(vertexDir, 'images')
    images = imageDir.listFiles()
  }


  Vertex withCurriculumRoot(curriculumRoot) {
    setCurriculumRoot(curriculumRoot)
    return this
  }


  def getSlideName() {
    def lines = slides.text.split('\n')
    def titleLine = lines.find { it.startsWith('=') }
    return titleLine[2..-1].trim()
  }


  def getHtmlAnchor() {
    vertexPath.replace('/', '-')
  }


  def slideIncludeAsciidoc() {
    """\
:slide_path: slides
:image_path: images/${vertexPath}
[[${htmlAnchor}]]
include::${includes.absolutePath}[]
"""
  }


  def exerciseIncludeAsciidoc(exerciseNumber) {
    """\
:exercise_number: ${exerciseNumber}
:image_path: images/${vertexPath}
[[EXERCISE-${exerciseNumber}]]
include::${exerciseFile.absolutePath}[]
"""
  }


  def solutionIncludeAsciidoc(solutionNumber) {
    """\
:solution_number: ${solutionNumber}
:image_path: images/${vertexPath}
[[SOLUTION-${solutionNumber}]]
include::${solutionFile.absolutePath}[]
"""
  }


  File getExerciseFile() {
    new File("${vertexRoot}/src/exercises.adoc")
  }


  def getSolutionFile() {
    new File("${vertexRoot}/src/solutions.adoc")
  }


  String getVertexRoot() {
    "${curriculumRoot.absolutePath}/${vertexPath}"
  }


  def getSlideAsciidocFiles() {
    def files = []
    File slidesDir = new File(vertexDir.absoluteFile, 'src/slides')

    files.addAll slidesDir.listFiles(new FilenameFilter() {
      boolean accept(File dir, String name) {
        name.endsWith('.adoc')
      }
    })
    files << slides
    files << includes
  }


  def getSlideImageFiles() {
    imageDir.listFiles(new FilenameFilter() {
      boolean accept(File dir, String name) {
        name[-4..-1] in ['.jpg', '.png', '.svg']
      }
    })
  }


  Map getDependencies() {
    def deps = [:]
    deps.javaScript = javaScript
    deps.slides = slideAsciidocFiles
    deps.docs = [ exercises, solutions, objectives ]
    deps.images = images
  }
}
