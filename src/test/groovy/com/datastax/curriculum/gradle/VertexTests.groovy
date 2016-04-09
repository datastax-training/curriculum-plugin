package com.datastax.curriculum.gradle

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class VertexTests {
  Vertex vnodes, simpleTraversal
  File curriculumRoot
  String vnodeVertexPath = 'cassandra/internals/distributed-architecture/vnodes'
  String simpleTraversalPath = 'graph/graph-traversal/simple-traversal'

  @Before
  void setup() {
    curriculumRoot = new File('src/test/resources/curriculum')
    vnodes = new Vertex(vnodeVertexPath)
    vnodes.curriculumRoot = curriculumRoot
    simpleTraversal = new Vertex(simpleTraversalPath).withCurriculumRoot(curriculumRoot)
  }


  @Test
  void testVertexComponents() {
    def imageFilenames = ['datum-blue.svg',
                          'datum-yellow.svg',
                          'node3.svg',
                          'ring2.svg',
                          'datum-bright-green.svg',
                          'node1.svg',
                          'node4.svg',
                          'vnode-ring-1.svg',
                          'datum-green.svg',
                          'node2.svg',
                          'ring1.svg',
                          'vnode-ring-2.svg']

    assertNotNull(vnodes.includes)
    assertNotNull(vnodes.images)
    assertNotNull(vnodes.solutions)
    assertNotNull(vnodes.exercises)
    assertNotNull(vnodes.javaScript)
    assertNotNull(vnodes.slides)
    assertNotNull(vnodes.vertexPath)
    assertEquals(12, vnodes.images.size())
    assertTrue(vnodes.includes instanceof File)
    assertTrue(vnodes.javaScript instanceof List<File>)
    assertTrue(vnodes.exercises instanceof File)
    assertTrue(vnodes.solutions instanceof File)
    assertTrue(vnodes.slides instanceof File)
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/src/slides.adoc', vnodes.slides.absolutePath[-78..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/src/exercises.adoc', vnodes.exercises.absolutePath[-81..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/src/solutions.adoc', vnodes.solutions.absolutePath[-81..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/src/includes.adoc', vnodes.includes.absolutePath[-80..-1])
    def jsFiles = vnodes.javaScript.collect { it.absolutePath }.sort()
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/js/animation.js', jsFiles[0][-78..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/js/other-1.js', jsFiles[1][-76..-1])
    assertEquals('curriculum/cassandra/internals/distributed-architecture/vnodes/js/other-2.js', jsFiles[2][-76..-1])
    assertEquals('cassandra/internals/distributed-architecture/vnodes', vnodes.vertexPath)
    def vertexImages = vnodes.images.collect { it.name }.sort()
    imageFilenames.sort().eachWithIndex { filename, index ->
      assertEquals(filename, vertexImages[index])
    }
  }


  @Test
  void testVertexNameExtractionFromSlides() {
    assertEquals('VNodes', vnodes.slideName)
  }


  @Test
  void testVertexToAnchor() {
    assertEquals('cassandra-internals-distributed-architecture-vnodes', vnodes.htmlAnchor)
  }


  @Test
  void testCourseExerciseInclude() {
    def exerciseNumber = 5
    def exercise = """\
:exercise_number: ${exerciseNumber}
:image_path: images/${vnodeVertexPath}
[[EXERCISE-${exerciseNumber}]]
include::${curriculumRoot.absolutePath}/${vnodeVertexPath}/src/exercises.adoc[]
"""
    assertEquals(exercise, vnodes.exerciseIncludeAsciidoc(exerciseNumber))
  }


  @Test
  void testCourseSolutionInclude() {
    def solutionNumber = 5
    def solution = """\
:solution_number: ${solutionNumber}
:image_path: images/${vnodeVertexPath}
[[SOLUTION-${solutionNumber}]]
include::${curriculumRoot.absolutePath}/${vnodeVertexPath}/src/solutions.adoc[]
"""
    assertEquals(solution, vnodes.solutionIncludeAsciidoc(solutionNumber))
  }


  @Test
  void testSlideIncludesAsciidoc() {
    def content = """\
:slide_path: slides
:image_path: images/${vnodeVertexPath}
[[cassandra-internals-distributed-architecture-vnodes]]
include::${curriculumRoot.absolutePath}/${vnodeVertexPath}/src/includes.adoc[]
"""
    assertEquals(content, vnodes.slideIncludeAsciidoc())
  }


  @Test
  void testExerciseFileName() {
    assertNotNull(vnodes.exerciseFile)
    assertEquals("${curriculumRoot.absolutePath}/${vnodeVertexPath}/src/exercises.adoc" as String,
                 vnodes.exerciseFile.absolutePath)
  }


  @Test
  void testSolutionFileName() {
    assertNotNull(vnodes.exerciseFile)
    assertEquals("${curriculumRoot.absolutePath}/${vnodeVertexPath}/src/solutions.adoc" as String,
                 vnodes.solutionFile.absolutePath)
  }


  @Test
  void testAsciidocSlideFiles() {
    def files = simpleTraversal.slideAsciidocFiles
    assertNotNull(files)
    assertEquals(5, files.size())
  }


  @Test
  void testVertexDependencies() {
    def deps

    deps = vnodes.dependencies
    assertNotNull(deps)
    assertNotNull(deps.javaScript)
    assertNotNull(deps.images)
    assertNotNull(deps.slides)
    assertNotNull(deps.docs)
    assertEquals(1, deps.docs.size())
    assertEquals(2, deps.slides.size())
    assertEquals(12, deps.images.size())
  }


  @Test
  void testSlideImageFileListing() {
    def files

    files = vnodes.slideImageFiles
    assertNotNull(files)
    assertEquals(12, files.size())
    assertNull(files.find { it.name.endsWith('.txt')})

    files = simpleTraversal.slideImageFiles
    assertNotNull(files)
    assertEquals(7, files.size())
  }


  @Test
  void testJavaScriptFileListing() {
    def files
    files = vnodes.javaScript
    assertNotNull(files)
    assertEquals(3, files.size())
  }


  @Test
  void testImageCopying() {
    def destDir = vnodes.copyImagesTo(File.createTempDir())
    assertNotNull(destDir)
    assertTrue(destDir instanceof File)
    assertEquals(vnodes.images.size(), destDir.listFiles().size())
  }
}
