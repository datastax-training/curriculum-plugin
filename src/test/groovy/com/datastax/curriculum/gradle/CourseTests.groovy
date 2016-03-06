package com.datastax.curriculum.gradle

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals


class CourseTests {
  Course emptyCourse, course
  Module internals, graphIntro, traversals
  File curriculumRoot
  File srcDir

  @Before
  void setup() {
    curriculumRoot = new File('src/test/resources/curriculum')
    srcDir = new File(curriculumRoot, 'courses/test-course/src')
    internals = new Module('Internals').withCurriculumRoot(curriculumRoot).withModuleFile('courses/test-course/modules/internals.txt')
    graphIntro = new Module('Graph Introduction').withCurriculumRoot(curriculumRoot).withModuleFile('courses/test-course/modules/introduction.txt')
    traversals = new Module('Graph Traversals').withCurriculumRoot(curriculumRoot).withModuleFile('courses/test-course/modules/traversals.txt')
    emptyCourse = new Course("Test Course").withCurriculumRoot(curriculumRoot)
    course = new Course("Test Course")
                  .withCurriculumRoot(curriculumRoot)
                  .withSrcDir(srcDir)
                  .addModule(internals)
                  .addModule(graphIntro)
                  .addModule(traversals)
  }


  @Test
  void testBuildCompleteSolutionFile() {
    def solutionFileText = """\
:solution_number: 1
:image_path: images/graph/graph-definition/property-graph
[[SOLUTION-1]]
include::${curriculumRoot.absolutePath}/graph/graph-definition/property-graph/src/solutions.adoc[]

:solution_number: 2
:image_path: images/cassandra/internals/distributed-architecture/vnodes
[[SOLUTION-2]]
include::${curriculumRoot.absolutePath}/cassandra/internals/distributed-architecture/vnodes/src/solutions.adoc[]

"""
    emptyCourse.addModule(graphIntro)
    emptyCourse.addModule(internals)
    emptyCourse.srcDir = 'src/test/resources/curriculum/courses/test-course/src'
    emptyCourse.buildSolutionFile()
    assertEquals(solutionFileText as String, emptyCourse.solutionFile.text)
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
    emptyCourse.buildExerciseFile()
    assertEquals(exerciseFileText as String, emptyCourse.exerciseFile.text)
  }


  @Test
  void testBuildCompleteModuleListFile() {
    def content = """\
=== Internals
. <<slides-1.adoc#cassandra-internals-distributed-architecture-vnodes,VNodes>>

=== Graph Introduction
. <<slides-2.adoc#graph-graph-definition-property-graph,Property Graph>>

=== Graph Traversals
. <<slides-3.adoc#graph-graph-traversal-gremlin-language,The Gremlin Graph Traversal Language>>
. <<slides-3.adoc#graph-graph-traversal-simple-traversal,Simple Traversal>>
. <<slides-3.adoc#graph-graph-traversal-mutating-traversal,Mutating Traversal>>

"""

    course.buildModuleFile()
    assertEquals(content as String, course.moduleFile.text)
  }
}
