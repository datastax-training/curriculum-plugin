package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

class SolutionBuilderTests {
  def mockedModules
  def courseTask
  def solutionBuilder
  File solutionsOutputFile

  @Before
  void setupModules() {
    mockedModules = [
      [name: 'Introduction', vertices: 'modules/introduction.txt'],
      [name: 'Traversals', vertices: 'modules/traversals.txt']
    ]

    solutionsOutputFile = File.createTempFile('solutions_','.adoc')
    Project project = ProjectBuilder.builder().withProjectDir(new File('src/test/resources')).build()
    courseTask = project.tasks.create('courseResources', CourseTask)
    courseTask.configure {
      curriculumRootDir = new File(project.projectDir, 'curriculum').absolutePath
      solutionsFile = solutionsOutputFile
      courseTask.buildVertexList(mockedModules)
    }
    solutionBuilder = new SolutionBuilder(courseTask)
  }

  @Test
  void testBuildCompleteSolutionFile() {
    solutionBuilder.build()
    def solutionsFileText = """
:exercise_number: 1
:image_path: images/graph/graph-definition/property-graph
include::${courseTask.curriculumRootDir}/graph/graph-definition/property-graph/src/solutions.adoc[]

:exercise_number: 2
:image_path: images/graph/graph-traversal/simple-traversal
include::${courseTask.curriculumRootDir}/graph/graph-traversal/simple-traversal/src/solutions.adoc[]

"""
    assertEquals(solutionsFileText as String, solutionsOutputFile.text)
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


  @Test
  void testSolutionFileName() {
    def vertex = 'monkey'
    def curriculumRootDir = 'angry'

    assertEquals('angry/monkey/src/solutions.adoc', solutionBuilder.solutionFileName(vertex, curriculumRootDir) as String)
  }
}

