package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals


class ExerciseBuilderTests {
  def mockedModules
  def courseTask
  def exerciseBuilder
  File exerciseOutputFile

  @Before
  void setupModules() {
    mockedModules = [
            [name: 'Introduction', vertices: 'modules/introduction.txt'],
            [name: 'Traversals', vertices: 'modules/traversals.txt']
    ]

    exerciseOutputFile = File.createTempFile('exercisess_','.adoc')
    Project project = ProjectBuilder.builder().withProjectDir(new File('src/test/resources')).build()
    courseTask = project.tasks.create('courseResources', CourseTask)
    courseTask.configure {
      curriculumRootDir = new File(project.projectDir, 'curriculum').absolutePath
      exercisesFile = exerciseOutputFile
      courseTask.buildVertexList(mockedModules)
    }
    exerciseBuilder = new ExerciseBuilder(courseTask)
  }

  @Test
  void testBuildCompleteExerciseFile() {
    exerciseBuilder.build()
    def exerciseFileText = """\
:exercise_number: 1
:image_path: images/graph/graph-definition/property-graph
[[EXERCISE-1]]
include::${courseTask.curriculumRootDir}/graph/graph-definition/property-graph/src/exercises.adoc[]

:exercise_number: 2
:image_path: images/graph/graph-traversal/simple-traversal
[[EXERCISE-2]]
include::${courseTask.curriculumRootDir}/graph/graph-traversal/simple-traversal/src/exercises.adoc[]

"""
    assertEquals(exerciseFileText as String, exerciseOutputFile.text)
  }


  @Test
  void testExerciseEntry() {
    def vertex = 'dev/driver/java'
    def rootDir = '/curriculum'
    def exerciseNumber = 5
    def solution = """\
:exercise_number: ${exerciseNumber}
:image_path: images/dev/driver/java
[[EXERCISE-${exerciseNumber}]]
include::/curriculum/dev/driver/java/src/exercises.adoc[]
"""
    assertEquals(solution, exerciseBuilder.exerciseEntry(vertex, rootDir, exerciseNumber))

  }


  @Test
  void testExerciseFileName() {
    def vertex = 'monkey'
    def curriculumRootDir = 'angry'

    assertEquals('angry/monkey/src/exercises.adoc', exerciseBuilder.exerciseFileName(vertex, curriculumRootDir) as String)
  }
}
