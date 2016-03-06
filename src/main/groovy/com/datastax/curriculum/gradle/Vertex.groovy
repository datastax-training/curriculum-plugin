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
    def imageDir = new File(vertexDir, 'images')
    images = imageDir.listFiles()
  }


  def getSlideName() {
    def lines = slides.text.split('\n')
    def titleLine = lines.find { it.startsWith('=') }
    return titleLine[2..-1].trim()
  }


  def getHtmlAnchor() {
    vertexPath.replace('/', '-')
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


  @Override
  public String toString() {
    return "Vertex{" +
            "vertexPath='" + vertexPath + '\'' +
            ", curriculumRoot=" + curriculumRoot +
            ", includes=" + includes +
            ", slides=" + slides +
            ", objectives=" + objectives +
            ", exercises=" + exercises +
            ", solutions=" + solutions +
            ", javaScript=" + javaScript +
            ", images=" + images +
            '}';
  }
}
