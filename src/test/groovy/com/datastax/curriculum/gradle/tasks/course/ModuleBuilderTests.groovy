package com.datastax.curriculum.gradle.tasks.course

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class ModuleBuilderTests {
  def courseTask
  def mockedModules
  ModuleBuilder moduleBuilder

  @Before
  void setupModules() {
    mockedModules = [
      [name: 'Introduction', vertices: 'modules/introduction.txt'],
      [name: 'Traversals', vertices: 'modules/traversals.txt']
    ]

    //exerciseOutputFile = File.createTempFile('_','.adoc')
    Project project = ProjectBuilder.builder().withProjectDir(new File('src/test/resources')).build()
    courseTask = project.tasks.create('courseResources', CourseTask)
    courseTask.configure {
      curriculumRootDir = new File(project.projectDir, 'curriculum').absolutePath
      //exercisesFile = exerciseOutputFile

      slideHeader = [backend: 'deckjs', deckjs_theme: 'datastax', notes: '']
    }
    courseTask.buildVertexList(mockedModules)
    moduleBuilder = new ModuleBuilder(courseTask)
  }


  @Test
  void testModuleAsciidoc() {
    def slidesFile = File.createTempFile('slides_', '.adoc')
    def title = "Traversals"
    def vertexList
    def content = """\
= ${title}
:backend: deckjs
:deckjs_theme: datastax
:notes:

:slide_path: slides
:image_path: images/graph/graph-traversal/gremlin-language
[[graph-graph-traversal-gremlin-language]]
include::${courseTask.curriculumRootDir}/graph/graph-traversal/gremlin-language/src/includes.adoc[]

:slide_path: slides
:image_path: images/graph/graph-traversal/simple-traversal
[[graph-graph-traversal-simple-traversal]]
include::${courseTask.curriculumRootDir}/graph/graph-traversal/simple-traversal/src/includes.adoc[]

:slide_path: slides
:image_path: images/graph/graph-traversal/mutating-traversal
[[graph-graph-traversal-mutating-traversal]]
include::${courseTask.curriculumRootDir}/graph/graph-traversal/mutating-traversal/src/includes.adoc[]

""" as String

    vertexList = courseTask.project.file(mockedModules[1].vertices).collect().findAll { it }
    println vertexList

    moduleBuilder.writeSlideAsciidoc(slidesFile, vertexList, title)

    assertEquals(content, slidesFile.text)
  }


  @Test
  void testVertexNameExtractionFromSlides() {
    def vertex = 'graph/graph-traversal/mutating-traversal'
    def name = moduleBuilder.extractVertexName(vertex)
    assertEquals('Mutating Traversal', name)
  }


  @Test
  void testVertexToAnchor() {
    def anchor = moduleBuilder.convertVertexToAnchor('graph/graph-traversal/mutating-traversal')
    assertEquals('graph-graph-traversal-mutating-traversal', anchor)
  }
}
