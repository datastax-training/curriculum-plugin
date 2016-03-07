package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class CourseTaskTests {
  def modules
  def courseTask


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
      vertexSolutionsFile = 'src/test/resources/src'
    }
  }


  @Test
  void testCourseDependencies() {

  }
}