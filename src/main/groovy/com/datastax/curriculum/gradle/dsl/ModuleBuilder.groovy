package com.datastax.curriculum.gradle.dsl

import com.datastax.curriculum.gradle.Module


class ModuleBuilder {
  Module module

  ModuleBuilder(String name, curriculumRoot) {
    module = new Module(name).withCurriculumRoot(curriculumRoot)
  }

  void setVertices(List<String> vertices) {
    vertices.each { vertexPath -> module.addVertex(vertexPath) }
  }
}
