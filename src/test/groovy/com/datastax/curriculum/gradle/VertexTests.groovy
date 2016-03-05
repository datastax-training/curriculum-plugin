package com.datastax.curriculum.gradle

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class VertexTests {
  Vertex vnodes
  File curriculumRoot
  String vertexPath = 'cassandra/internals/distributed-architecture/vnodes'


  @Before
  void setup() {
    curriculumRoot = new File('src/test/resources/curriculum')
    vnodes = new Vertex(vertexPath)
    vnodes.curriculumRoot = curriculumRoot
  }


  @Test
  void testVertexComponents() {
    def imageFilenames = ['datum-blue.svg',
                          'datum-yellow.svg',
                          'node3.svg',
                          'ring2.svg',
                          'datum-bright-green.svg',
                          'node1.svg',
                          'node4.svg',
                          'vnode-ring-1.svg',
                          'datum-green.svg',
                          'node2.svg',
                          'ring1.svg',
                          'vnode-ring-2.svg']

    assertNotNull(vnodes.includes)
    assertNotNull(vnodes.images)
    assertNotNull(vnodes.solutions)
    assertNotNull(vnodes.exercises)
    assertNotNull(vnodes.javaScript)
    assertNotNull(vnodes.slides)
    assertNotNull(vnodes.vertexPath)
    assertEquals(12, vnodes.images.size())
    assertTrue(vnodes.includes instanceof File)
    assertTrue(vnodes.javaScript instanceof File)
    assertTrue(vnodes.exercises instanceof File)
    assertTrue(vnodes.solutions instanceof File)
    assertTrue(vnodes.slides instanceof File)
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/src/slides.adoc', vnodes.slides.absolutePath[-78..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/src/exercises.adoc', vnodes.exercises.absolutePath[-81..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/src/solutions.adoc', vnodes.solutions.absolutePath[-81..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/src/includes.adoc', vnodes.includes.absolutePath[-80..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/js/animation.js', vnodes.javaScript.absolutePath[-78..-1])
    assertEquals('cassandra/internals/distributed-architecture/vnodes', vnodes.vertexPath)
    def vertexImages = vnodes.images.collect { it.name }.sort()
    imageFilenames.sort().eachWithIndex { filename, index ->
      assertEquals(filename, vertexImages[index])
    }
  }


  @Test
  void testVertexNameExtractionFromSlides() {
    assertEquals('VNodes', vnodes.slideName)
  }


  @Test
  void testVertexToAnchor() {
    assertEquals('cassandra-internals-distributed-architecture-vnodes', vnodes.htmlAnchor)
  }


  @Test
  void testCourseExerciseInclude() {
    def exerciseNumber = 5
    def solution = """\
:exercise_number: ${exerciseNumber}
:image_path: images/${vertexPath}
[[EXERCISE-${exerciseNumber}]]
include::${curriculumRoot.absolutePath}/${vertexPath}/src/exercises.adoc[]
"""
    assertEquals(solution, vnodes.courseExerciseInclude(exerciseNumber))
  }


  @Test
  void testExerciseFileName() {
    assertNotNull(vnodes.exerciseFile)
    assertEquals("${curriculumRoot.absolutePath}/${vertexPath}/src/exercises.adoc" as String,
                 vnodes.exerciseFile.absolutePath)
  }

  @Test
  void testSolutionFileName() {
    assertNotNull(vnodes.exerciseFile)
    assertEquals("${curriculumRoot.absolutePath}/${vertexPath}/src/solutions.adoc" as String,
                 vnodes.solutionFile.absolutePath)
  }



  @Test
  void testVertexDependencies() {
//    def deps = vnodes.dependencies
//    assertNotNull(deps)
//    assertNotNull(deps.javaScript)
//    assertNotNull(deps.images)
//    assertNotNull(deps.slides)
//    assertNotNull(deps.docs)
  }
}
