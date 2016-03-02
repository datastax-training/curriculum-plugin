package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.Project

class SolutionBuilder {
  CourseTask courseTask

  
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
        def vertexSolutionsFile = "${curriculumRootDir}/${vertex}/src/solutions.adoc"
        if(project.file(courseTask.vertexSolutionsFile).exists()) {
          writer.println ":exercise_number: ${exerciseNumber++}"
          writer.println ":image_path: images/${vertex}"
          writer.println "include::${curriculumRootDir}/${vertex}/src/solutions.adoc[]"
          writer.println ''
        }
      }
      writer.flush()
    }
  }
}
