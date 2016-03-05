package com.datastax.curriculum.gradle

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class ModuleTests {
  Module internals, graphIntro, traversals
  File curriculumRoot


  @Before
  void setup() {
    curriculumRoot = new File('src/test/resources/curriculum')
    internals = new Module('Internals').withCurriculumRoot(curriculumRoot)
    graphIntro = new Module('Graph Introduction').withCurriculumRoot(curriculumRoot).withModuleFile('courses/test-course/modules/introduction.txt')
    traversals = new Module('Graph Traversals').withCurriculumRoot(curriculumRoot).withModuleFile('courses/test-course/modules/traversals.txt')
  }


  @Test
  void testModuleFilenames() {
    assertNotNull(graphIntro.moduleFile.absolutePath)
    assertNotNull(traversals.moduleFile.absolutePath)
    assertTrue(graphIntro.moduleFile.exists())
    assertTrue(traversals.moduleFile.exists())

    // Rough test to establish that we're probably pointing to the right files
    assertEquals(1, graphIntro.moduleFile.readLines().size())
    assertEquals(3, traversals.moduleFile.readLines().size())
  }


  @Test
  void testModuleAdding() {
    internals.addVertex('cassandra/internals/distributed-architecture/vnodes')
    assertNotNull(internals.vertices)
    assertEquals(1, internals.vertices.size())

    Vertex vnodes = internals.vertices[0]
    assertNotNull(vnodes)
    assertEquals('cassandra/internals/distributed-architecture/vnodes', vnodes.vertexPath)
    assertNotNull(vnodes.slides)
    println vnodes.slides.absolutePath
    assertTrue(vnodes.slides.exists())
  }


  @Test
  void testModuleFile() {
    assertNotNull(traversals.vertices)
    assertEquals(3, traversals.vertices.size())
    assertEquals('graph/graph-traversal/gremlin-language', traversals.vertices[0].vertexPath)
    assertEquals('graph/graph-traversal/simple-traversal', traversals.vertices[1].vertexPath)
    assertEquals('graph/graph-traversal/mutating-traversal', traversals.vertices[2].vertexPath)
  }


  @Test
  void testModuleName() {
    assertEquals('Internals', internals.name)
    assertEquals('Graph Introduction', graphIntro.name)
    assertEquals('Graph Traversals', traversals.name)
  }
}
