package com.datastax.curriculum.gradle.tasks.course

class SolutionBuilder {
  CourseTask courseTask

  SolutionBuilder(CourseTask courseTask) {
    this.courseTask = courseTask
  }


  def build() {
    def project = courseTask.project
    def solutionsFile = courseTask.solutionsFile
    def vertexList = courseTask.vertexList
    def curriculumRootDir = courseTask.curriculumRootDir

    int exerciseNumber = 1
    def outputFile = project.file(solutionsFile)
    outputFile.withWriter { writer ->
      writer.println ''
      vertexList.each { vertex ->
        if(project.file(solutionFileName(vertex, curriculumRootDir)).exists()) {
          writer.println solutionEntry(vertex, curriculumRootDir, exerciseNumber++)
        }
      }
      writer.flush()
    }
  }


  def solutionFileName(vertex, curriculumRootDir) {
    "${curriculumRootDir}/${vertex}/src/solutions.adoc"
  }


  def solutionEntry(vertex, curriculumRootDir, exerciseNumber) {
    """\
:exercise_number: ${exerciseNumber}
:image_path: images/${vertex}
include::${solutionFileName(vertex, curriculumRootDir)}[]
"""
  }
}
