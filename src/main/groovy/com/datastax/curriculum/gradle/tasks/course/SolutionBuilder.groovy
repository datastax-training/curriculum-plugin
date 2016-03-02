package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.Project

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
    def exercisesFile = project.file(solutionsFile)
    exercisesFile.withWriter { writer ->
      writer.println ''
      vertexList.each { vertex ->
        if(project.file(courseTask.vertexSolutionsFile).exists()) {
          writer.println solutionEntry(vertex, curriculumRootDir, exerciseNumber++)
        }
      }
      writer.flush()
    }
  }


  def solutionEntry(vertex, curriculumRootDir, exerciseNumber) {
    """\
:exercise_number: ${exerciseNumber}
:image_path: images/${vertex}
include::${curriculumRootDir}/${vertex}/src/solutions.adoc[]

"""
  }
}
