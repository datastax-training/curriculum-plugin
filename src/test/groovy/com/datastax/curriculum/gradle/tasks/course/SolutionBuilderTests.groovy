package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertSame

class SolutionBuilderTests {
  def modules
  def courseTask
  def solutionBuilder

  @Before
  void setupModules() {
    modules = [
            [name: 'Introduction', vertices: 'src/test/resources/modules/introduction.txt'],
            [name: 'Traversals', vertices: 'src/test/resources/modules/traversals.txt']
    ]

    Project project = ProjectBuilder.builder().withProjectDir(new File('.')).build()
    courseTask = project.tasks.create('courseResources', CourseTask)
    courseTask.configure {
      curriculumRootDir = new File('src/test/resources').absoluteFile
    }
    solutionBuilder = new SolutionBuilder(courseTask)
  }


  @Test
  void testSolutionEntry() {
    def vertex = 'dev/driver/java'
    def rootDir = '/curriculum'
    def exerciseNumber = 5
    def solution = """\
:exercise_number: 5
:image_path: images/dev/driver/java
include::/curriculum/dev/driver/java/src/solutions.adoc[]

"""
    assertEquals(solution, solutionBuilder.solutionEntry(vertex, rootDir, exerciseNumber) as String)

  }
}

