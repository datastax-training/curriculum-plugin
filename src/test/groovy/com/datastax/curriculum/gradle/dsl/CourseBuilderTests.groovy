package com.datastax.curriculum.gradle.dsl

import com.datastax.curriculum.gradle.Course
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class CourseBuilderTests {
  def courseString

  @Before
  void setup() {
    courseString = """\
course {
  title 'DS201: Introduction to Apache Cassandra'
  module('Introduction') {
    vertices = [
      'courses/DS201/custom-content/course-introduction',
    ]
  }
  module('Internals') {
    vertices = [
      'cassandra/internals/node-architecture/storage-engine/write-path',
      'cassandra/internals/node-architecture/storage-engine/read-path',
      'cassandra/internals/node-architecture/storage-engine/compaction'
    ]
  }
}
"""
  }


  @Test
  void testCourseBuild() {
    def processor = new CourseDefinitionParser(new File('.'))
    Course course = processor.parse(courseString)

    assertNotNull(course)
    assertNotNull(course.name)
    assertEquals('DS201: Introduction to Apache Cassandra', course.name)
    assertNotNull(course.modules)
    assertEquals(2, course.modules.size())
    assertNotNull(course.modules[0].name)

    assertEquals('Introduction', course.modules[0].name)
    assertNotNull(course.modules[0].vertices)
    assertEquals(1, course.modules[0].vertices.size())
    assertEquals('courses/DS201/custom-content/course-introduction',
            course.modules[0].vertices[0].vertexPath)

    assertNotNull(course.modules[1].name)
    assertEquals('Internals', course.modules[1].name)
    assertNotNull(course.modules[1].vertices)
    assertEquals(3, course.modules[1].vertices.size())

    assertEquals('cassandra/internals/node-architecture/storage-engine/write-path',
            course.modules[1].vertices[0].vertexPath)
    assertEquals('cassandra/internals/node-architecture/storage-engine/read-path',
            course.modules[1].vertices[1].vertexPath)
    assertEquals('cassandra/internals/node-architecture/storage-engine/compaction',
            course.modules[1].vertices[2].vertexPath)

  }

}
