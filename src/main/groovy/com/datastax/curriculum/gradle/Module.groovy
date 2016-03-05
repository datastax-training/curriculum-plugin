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

//    if(!this.moduleFile.isAbsolute()) {
//      this.moduleFile = new File(curriculumRoot, this.moduleFile.path)
//    }
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

}
