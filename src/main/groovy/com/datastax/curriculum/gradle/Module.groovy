package com.datastax.curriculum.gradle

import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovy.text.TemplateEngine

class Module {
  String name
  List<Vertex> vertices = []
  File moduleFile
  File curriculumRoot
  TemplateEngine templateEngine = new GStringTemplateEngine()


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
    File js = new File(destDir, moduleJavaScriptFilename(moduleNumber))
    js.withWriter { writer ->
      vertices.each { vertex ->
        def parms = [ image_path: vertex.imageDir.absolutePath ]
        vertex.javaScript.each { jsFile ->
          if(jsFile.exists()) {
            writer.println expandJavaScriptMacros(jsFile.text, parms)
          }
        }
      }
    }

    return js
  }


  String expandJavaScriptMacros(String js, Map parms) {
    Template template = new GStringTemplateEngine().createTemplate(js)
    Writable writable = template.make(parms)
    CharArrayWriter writer = new CharArrayWriter()
    writable.writeTo(writer)
    return writer.toString()
  }


  String moduleJavaScriptFilename(int moduleNumber) {
    "module-${moduleNumber}.js"
  }

}

