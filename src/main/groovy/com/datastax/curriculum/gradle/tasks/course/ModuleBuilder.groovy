package com.datastax.curriculum.gradle.tasks.course


class ModuleBuilder {
  CourseTask courseTask


  ModuleBuilder(CourseTask courseTask) {
    this.courseTask = courseTask
  }


  def build() {
    def project = courseTask.project
    def courseModuleFile = courseTask.courseModuleFile
    def curriculumRootDir = courseTask.curriculumRootDir
    def modules = courseTask.modules

    def vertexList = []
    project.file(courseModuleFile).withWriter { writer ->
      modules.eachWithIndex { module, index ->
        def name = module.name
        def moduleVertices = project.file(module.vertices).collect().findAll { it }
        def slideFileName = "slides-${index+1}.adoc"
        writeSlideAsciidoc("${courseTask.srcDir}/${slideFileName}", moduleVertices, name)

        writer.println ''
        writer.println "=== ${name}"
        moduleVertices.each { vertex ->
          def vertexName = extractVertexName(vertex)
          writer.println ". <<${slideFileName}#${convertVertexToAnchor(vertex)},${extractVertexName(vertex)}>>"
          vertexList << vertex
        }
      }
    }
    return vertexList
  }


  def writeSlideAsciidoc(slidesFile, vertexList, title) {
    courseTask.project.file(slidesFile).withWriter { writer ->
      writer.println "= ${title}"
      writer.println convertHeaderMapToString(courseTask.slideHeader)
      writer.println ''
      vertexList.each { vertex ->
        writer.println ":slide_path: slides"
        writer.println ":image_path: images/${vertex}"
        writer.println "[[${convertVertexToAnchor(vertex)}]]"
        writer.println "include::${courseTask.curriculumRootDir}/${vertex}/src/includes.adoc[]"
        writer.println ''
      }
      writer.flush()
    }
  }


  def extractVertexName(vertex) {
    def project = courseTask.project
    def curriculumRootDir = courseTask.curriculumRootDir

    def adocFile = project.file("${curriculumRootDir}/${vertex}/src/slides.adoc")
    def lines = adocFile.text.split('\n')
    def titleLine = lines.find { it.startsWith('=') }
    return titleLine[2..-1].trim()
  }


  def convertVertexToAnchor(vertex) {
    vertex.replace('/', '-')
  }


  def convertHeaderMapToString(header) {
    header.collect { key, value -> ":${key}: ${value}" }.join('\n')
  }
}
