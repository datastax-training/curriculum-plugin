
package com.datastax.curriculum.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.asciidoctor.gradle.AsciidoctorTask


class CurriculumPlugin
  implements Plugin<Project> {
  File curriculumRootDir
  File frameworkDir
  File templateDir
  File deckjsDir
  File buildDeckjsDir


  void apply(Project project) {
    project.plugins.apply('org.asciidoctor.gradle.asciidoctor')
    project.plugins.apply('lesscss')
    curriculumRootDir = findProjectRoot()
    frameworkDir = new File(curriculumRootDir, 'framework')
    templateDir  = new File(frameworkDir, 'asciidoctor-backends')
    deckjsDir    = new File(frameworkDir, 'deck.js')
    buildDeckjsDir = new File(project.buildDir, 'asciidoc/deckjs/deck.js')
    applyTasks(project)
  }

  File findProjectRoot(project) {
    def projectRoot = project.projectDir.absolutePath
    def parts = [''] + projectRoot.tokenize(File.separator)
    def paths = (parts.size()..1).collect { depth -> parts[0..depth-1].join(File.separator) }
    paths.each { path ->
      if(project.file([path,'.projectroot'].join(File.separator)).exists()) {
        projectRoot = path
      }
    }
    return project.file(projectRoot)
  }


  void applyTasks(Project project) {
    project.tasks.lessc.configure {
      sourceDir "${deckjsDir}/themes/style"
      include "**/*.less"
      destinationDir = "${buildDeckjsDir}/themes/style"
      mustRunAfter asciidoctor
    }

    project.tasks.create('slides', AsciidoctorTask)
    project.tasks.create('docs', AsciidoctorTask)

    project.tasks.slides.configure {
      sourceDir "${project.projectDir}/src"
      sources {
        include 'slides.adoc'
      }

      backends 'deckjs'
    }

    project.task.slides.configure {

      sourceDir "${project.projectDir}/src"
      sources {
        include 'exercises.adoc'
        include 'outline.adoc'
        include 'objectives.adoc'
        include 'instructor-notes.adoc'
        include 'solutions.adoc'
      }

      backends 'html5'
    }
  }

}
