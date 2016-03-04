package com.datastax.curriculum.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class VertexTests {
  Vertex vnodes

  @Before
  void setup() {
    def curriculumRoot

    curriculumRoot = new File('src/test/resources/curriculum')
    vnodes = new Vertex(curriculumRoot, 'cassandra/internals/distributed-architecture/vnodes')
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
    assertNotNull(vnodes.vertexName)
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
    assertEquals('cassandra/internals/distributed-architecture/vnodes', vnodes.vertexName)
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



}
