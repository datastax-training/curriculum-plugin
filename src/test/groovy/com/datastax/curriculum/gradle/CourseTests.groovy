package com.datastax.curriculum.gradle

import org.junit.Before


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
}
