package com.datastax.curriculum.gradle.tasks.course

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class CourseTaskTests {
  def modules
  def courseTask

  @Before
  void setupModules() {
    modules = [
      [name: 'Introduction', vertices: 'modules/introduction.txt'],
      [name: 'Traversals', vertices: 'modules/traversals.txt']
    ]
    courseTask = new CourseTask()
  }


  @Test
  void testVertexList() {
    def vertexList = buildVertexList(modules)
    assertEquals(vertexList.size(), )
  }
}