package com.datastax.curriculum.gradle.tasks.course

class ExerciseBuilder {
  CourseTask courseTask


  ExerciseBuilder(CourseTask courseTask) {
    this.courseTask = courseTask
  }


  def build() {
    def curriculumRootDir = courseTask.curriculumRootDir
    def vertexList = courseTask.vertexList
    def exercisesFileName = courseTask.exercisesFile
    def project = courseTask.project

    int exerciseNumber = 1
    def exercisesFile = project.file(exercisesFileName)
    exercisesFile.withWriter { writer ->
      vertexList.each { vertex ->
        def vertexExercisesFile = exerciseFileName(vertex, curriculumRootDir)
        if(project.file(vertexExercisesFile).exists()) {
          writer.println exerciseEntry(vertex, curriculumRootDir, exerciseNumber++)
        }
      }
      writer.flush()
    }
  }


  def exerciseEntry(vertex, curriculumRootDir, exerciseNumber) {
    """\
:exercise_number: ${exerciseNumber}
:image_path: images/${vertex}
[[EXERCISE-${exerciseNumber}]]
include::${exerciseFileName(vertex, curriculumRootDir)}[]
"""
  }


  def exerciseFileName(vertex, curriculumRootDir) {
    "${curriculumRootDir}/${vertex}/src/exercises.adoc"
  }

}
