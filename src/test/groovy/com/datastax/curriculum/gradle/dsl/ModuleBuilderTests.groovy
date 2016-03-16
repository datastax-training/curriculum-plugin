package com.datastax.curriculum.gradle.dsl

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class ModuleBuilderTests {
  def introduction, internals

  @Before
  void setup() {
    introduction = {
      vertices = [
        'courses/DS201/custom-content/course-introduction',
        'courses/DS201/transitions/killrvideo-transition',
        'courses/DS220/data-modeling/overview/killrvideo-story'
      ]
    }
    internals = {
      vertices = [
        'cassandra/internals/node-architecture/storage-engine/write-path',
        'cassandra/internals/node-architecture/storage-engine/read-path',
        'cassandra/internals/node-architecture/storage-engine/compaction'
      ]
    }
  }


  @Test
  void testModuleBuilding() {
    ModuleBuilder builder = new ModuleBuilder('Internals', new File('.'))

    internals.delegate = builder
    internals.call()

    assertNotNull(builder.module)
    assertNotNull(builder.module.vertices)
    assertEquals(3, builder.module.vertices.size())
    assertNotNull(builder.module.name)
    assertEquals('Internals', builder.module.name)
  }
}
