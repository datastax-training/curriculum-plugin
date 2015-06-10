
package com.datastax.curriculum.gradle

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.asciidoctor.gradle.AsciidoctorTask
import org.gradle.api.tasks.bundling.Zip


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


  void applyTasks(Project project) {
    configureLesscTask(project)
    createAndConfigureSlidesTask(project)
    createAndConfigureDocsTask(project)
    createAndConfigurePresentationTask(project)
    createAndConfigureCourseTask(project)
    createAndConfigureBundleTask(project)
    createAndConfigureVertexTask(project)
  }


  def createAndConfigureVertexTask(project) {
    project.tasks.create('vertex').configure {
      dependsOn = ['presentation', 'docs']
      description = 'Builds all vertex materials'
      group = 'Curriculum'
    }
  }


  def createAndConfigureBundleTask(project) {
    project.tasks.create('bundle', Zip).configure {
      dependsOn = ['presentation', 'docs'].collect { project.tasks.getByName(it) }
      from project.buildDir
      exclude "lessc/", "distributions/"
      description = 'Bundles all course outputs into a distributable ZIP file'
      group = 'Curriculum'
    }
  }


  def configureLesscTask(project) {
    project.tasks.getByName('lessc').configure {
      sourceDir "${deckjsDir}/themes/style"
      include "**/*.less"
      destinationDir = "${buildDeckjsDir}/themes/style"
      mustRunAfter project.tasks.getByName('asciidoctor')
      description = 'Compiles less files into CSS'
      group = 'Curriculum'
    }
  }


  def createAndConfigurePresentationTask(project) {
    project.tasks.create('presentation').configure {
      dependsOn = ['slides', 'lessc']
      description = 'Builds the deck.js presentation and dependencies'
      group = "Curriculum"
    }
  }


  def createAndConfigureCourseTask(project) {
    def task = project.tasks.create('course', CourseTask).configure {
      dependsOn = ['presentation', 'docs']
      curriculumRootDir = this.curriculumRootDir
      description = 'Builds a course out of vertices'
      group = "Curriculum"
    }
  }


  def createAndConfigureSlidesTask(project) {
    project.tasks.create('slides', AsciidoctorTask).configure {
      logDocuments = false
      sourceDir "${project.projectDir}/src"
      sources {
        include 'slides.adoc'
      }

      backends 'deckjs'

      options template_dirs: [new File(templateDir, 'haml').absolutePath]
      options eruby: 'erubis'

      attributes 'source-highlighter': 'coderay'
      attributes idprefix: ''
      attributes idseparator: '-'

      resources {
        from(project.projectDir) {
          include 'images/**/*.svg'
          include 'images/**/*.jpg'
          include 'images/**/*.png'
        }
        from(frameworkDir) {
          include 'deck.js/**'
        }
      }

      doLast {
        project.copy {
          from(project.projectDir) {
            include 'js/**/*.js'
          }
          into("${project.buildDir}/asciidoc/deckjs")
          expand(['image_path': 'images'])
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

      description = 'Builds the deck.js presentation slides only'
      group = "Curriculum"
    }
  }


  def createAndConfigureDocsTask(project) {
    project.tasks.create('docs', AsciidoctorTask).configure {
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
      attributes image_path: 'docs'
      attributes idprefix: '', idseparator: '-'
      attributes stylesheet: 'styles.css',
                 stylesdir: project.file("${frameworkDir}/asciidoctor-backends/haml/html5/css")

      description = "Builds the exercises and other supporting docs"
      group = "Curriculum"
    }
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
}
