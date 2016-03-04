package com.datastax.curriculum.gradle

class Vertex {
  String vertexName
  File curriculumRoot
  File vertexDir
  def includes
  def slides
  def objectives
  def exercises
  def solutions
  def javaScript
  def images = []

  Vertex(curriculumRoot, vertexName) {
    if(curriculumRoot instanceof File) {
      this.curriculumRoot = curriculumRoot
    }
    else {
      this.curriculumRoot = new File(curriculumRoot)
    }

    vertexDir = new File(curriculumRoot, vertexName)
    this.vertexName = vertexName
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
    vertexName.replace('/', '-')
  }


  @Override
  public String toString() {
    return "Vertex{" +
            "vertexName='" + vertexName + '\'' +
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
