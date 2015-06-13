package com.datastax.curriculum.gradle.tasks

import org.asciidoctor.gradle.AsciidoctorTask
import org.gradle.api.tasks.TaskAction

class SlidesTask extends AsciidoctorTask {
  def frameworkDir
  def buildDeckjsDir
  def imagePath


  @TaskAction
  def slidesAction() {
    project.copy {
      from(project.projectDir) {
        include 'js/**/*.js'
      }
      into("${project.buildDir}/asciidoc/deckjs")
      expand(['image_path': imagePath])
    }
    project.copy {
      from("${frameworkDir}/deck.ext.js/extensions")
      into project.file("${buildDeckjsDir}/extensions")
    }
    project.copy {
      from("${frameworkDir}/deck.split.js")
      into project.file("${buildDeckjsDir}/extensions/split/")
    }
    project.copy {
      from("${frameworkDir}/deck.js-notes")
      into project.file("${buildDeckjsDir}/extensions/deck.js-notes/")
    }
  }
}
