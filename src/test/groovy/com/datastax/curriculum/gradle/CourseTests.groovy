package com.datastax.curriculum.gradle

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals


class CourseTests {
  Course emptyCourse, course
  Module internals, graphIntro, traversals
  File curriculumRoot


  @Before
  void setup() {
    curriculumRoot = new File('src/test/resources/curriculum')
    internals = new Module('Internals').withCurriculumRoot(curriculumRoot).withModuleFile('courses/test-course/modules/internals.txt')
    graphIntro = new Module('Graph Introduction').withCurriculumRoot(curriculumRoot).withModuleFile('courses/test-course/modules/introduction.txt')
    traversals = new Module('Graph Traversals').withCurriculumRoot(curriculumRoot).withModuleFile('courses/test-course/modules/traversals.txt')
    emptyCourse = new Course("Test Course").withCurriculumRoot(curriculumRoot)
    course = new Course("Test Course")
                  .withCurriculumRoot(curriculumRoot)
                  .addModule(internals)
                  .addModule(graphIntro)
                  .addModule(traversals)
  }


  @Test
  void testBuildCompleteExerciseFile() {
    def exerciseFileText = """\
:exercise_number: 1
:image_path: images/graph/graph-definition/property-graph
[[EXERCISE-1]]
include::${curriculumRoot.absolutePath}/graph/graph-definition/property-graph/src/exercises.adoc[]

:exercise_number: 2
:image_path: images/cassandra/internals/distributed-architecture/vnodes
[[EXERCISE-2]]
include::${curriculumRoot.absolutePath}/cassandra/internals/distributed-architecture/vnodes/src/exercises.adoc[]

"""
    emptyCourse.addModule(graphIntro)
    emptyCourse.addModule(internals)
    emptyCourse.srcDir = 'src/test/resources/curriculum/courses/test-course/src'
    emptyCourse.buildExercisesFile()
    assertEquals(exerciseFileText as String, emptyCourse.exerciseFile.text)
  }

}
