package com.datastax.curriculum.gradle.tasks.course

class ExerciseBuilder {
  CourseTask courseTask


  def build() {
    def curriculumRootDir = courseTask.curriculumRootDir
    def vertexList = courseTask.vertexList
    def exercisesFileName = courseTask.exerciseFile
    def project = courseTask.project

    int exerciseNumber = 1
    def exercisesFile = project.file(exercisesFileName)
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

}
