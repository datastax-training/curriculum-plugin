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
    internals = new Module('Internals').withCurriculumRoot(curriculumRoot).withModuleFile('modules/internals.txt')
    graphIntro = new Module('Graph Introduction').withCurriculumRoot(curriculumRoot).withModuleFile('modules/introduction.txt')
    traversals = new Module('Graph Traversals').withCurriculumRoot(curriculumRoot).withModuleFile('modules/traversals.txt')
  }


  @Test
  void testModuleFilenames() {
    assertEquals("${curriculumRoot.absolutePath}/modules/internals.txt" as String, internals.moduleFile.absolutePath)
    assertEquals("${curriculumRoot.absolutePath}/modules/introduction.txt" as String, graphIntro.moduleFile.absolutePath)
    assertEquals("${curriculumRoot.absolutePath}/modules/traversals.txt" as String, traversals.moduleFile.absolutePath)
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
    assertTrue(vnodes.slides.exists())
  }


  @Test
  void testModuleName() {
    assertEquals('Internals', internals.name)
    assertEquals('Graph Introduction', graphIntro.name)
    assertEquals('Graph Traversals', traversals.name)
  }


}
