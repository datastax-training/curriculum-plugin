
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

    curriculumRootDir = findProjectRoot(project)
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
    def asciidoctorTask = project.tasks.getByName('asciidoctor')
    project.tasks.getByName('lessc').configure {
      sourceDir "${deckjsDir}/themes/style"
      include "**/*.less"
      destinationDir = "${buildDeckjsDir}/themes/style"
      mustRunAfter asciidoctorTask
    }

    def slidesTask = project.tasks.create('slides', AsciidoctorTask)
    def docsTask = project.tasks.create('docs', AsciidoctorTask)

    slidesTask.configure {
      logDocuments = true
      sourceDir "${project.projectDir}/src"
      sources {
        include 'slides.adoc'
      }

      backends 'deckjs'

      options template_dirs : [new File(templateDir, 'haml').absolutePath ]
      options eruby: 'erubis'

      attributes 'source-highlighter': 'coderay'
      attributes idprefix: ''
      attributes idseparator: '-'

      resources {
        from (project.projectDir) {
          include 'images/**/*.svg'
          include 'images/**/*.jpg'
          include 'images/**/*.png'
          include 'js/**/*.js'
        }
        from(frameworkDir) {
          include 'deck.js/**'
        }
      }

      doLast {
        copy {
          from("${frameworkDir}/deck.ext.js/extensions")
          into project.file("${buildDeckjsDir}/extensions")
        }
        copy {
          from("${frameworkDir}/deck.split.js")
          into project.file("${buildDeckjsDir}/extensions/split/")
        }
        copy {
          from("${frameworkDir}/deck.js-notes")
          into project.file("${buildDeckjsDir}/extensions/deck.js-notes/")
        }
      }

      description = 'Builds the deck.js presentation'
      group = "Curriculum"
    }

    docsTask.configure {

      sourceDir "${project.projectDir}/src"
      sources {
        include 'exercises.adoc'
        include 'outline.adoc'
        include 'objectives.adoc'
        include 'instructor-notes.adoc'
        include 'solutions.adoc'
      }

      backends 'html5'
      resources {
        from (project.projectDir) {
          include 'images/**/*.svg'
          include 'images/**/*.jpg'
          include 'images/**/*.png'
        }
      }

      options template_dirs : [new File(templateDir, 'haml').absolutePath ]
      options eruby: 'erubis'

      attributes 'source-highlighter': 'coderay'
      attributes idprefix: ''
      attributes idseparator: '-'
      attributes stylesheet: 'styles.css',
                 stylesdir: project.file("${frameworkDir}/asciidoctor-backends/haml/html5/css")

      description = "Builds the exercises and other supporting docs"
      group = "Curriculum"
    }
  }

}
