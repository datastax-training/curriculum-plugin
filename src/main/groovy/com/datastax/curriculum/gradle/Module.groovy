package com.datastax.curriculum.gradle

class Module {
  String name
  List<Vertex> vertices = []
  File moduleFile
  File curriculumRoot


  Module(name) {
    this.name = name
  }


  void setModuleFile(moduleFile) {
    if(moduleFile instanceof File) {
      this.moduleFile = moduleFile
    }
    else {
      this.moduleFile = new File(moduleFile)
    }

    if(!this.moduleFile.isAbsolute()) {
      this.moduleFile = new File(curriculumRoot, this.moduleFile.path)
    }
  }


  Module withModuleFile(mf) {
    setModuleFile(mf)
    moduleFile.eachLine { vertexPath ->
      addVertex(vertexPath)
    }
    return this
  }


  void setCurriculumRoot(curriculumRoot) {
    if(curriculumRoot instanceof File) {
      this.curriculumRoot = curriculumRoot.absoluteFile
    }
    else {
      this.curriculumRoot = new File(curriculumRoot).absoluteFile
    }
  }


  Module withCurriculumRoot(cr) {
    setCurriculumRoot(cr)
    return this
  }


  Module addVertex(vertexPath) {
    Vertex v = new Vertex(vertexPath)
    v.curriculumRoot = curriculumRoot
    vertices << v

    return this
  }


  String vertexListAsciidoc(int moduleNumber) {
    Writer writer = new CharArrayWriter()

    writer.println "=== ${name}"
    vertices.each { vertex ->
      writer.println ". <<slides-${moduleNumber}.adoc#${vertex.htmlAnchor},${vertex.slideName}>>"
    }

    return writer.toString()
  }


  File combineJavaScript(File destDir, int moduleNumber) {
    File js = new File(destDir, "module-${moduleNumber}.js")
    js.withWriter { writer ->
      vertices.each { vertex ->
        if(vertex.javaScript.exists()) {
          writer.println vertex.javaScript.text
        }
      }
    }

    return js
  }

}
