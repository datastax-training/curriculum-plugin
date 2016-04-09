package com.datastax.curriculum.gradle

import com.bluepapa32.gradle.plugins.watch.WatchTarget
import org.gradle.api.Project


class VertexWatchTarget extends WatchTarget {
  Project project

  VertexWatchTarget(Project project, Vertex vertex) {
    super(vertex.htmlAnchor)
    tasks = [ 'vertex' ]
    this.project = project
    files(project.files(vertex.dependencies.slides))
    files(project.files(vertex.dependencies.images))
    files(project.files(vertex.dependencies.javaScript))
  }

}
