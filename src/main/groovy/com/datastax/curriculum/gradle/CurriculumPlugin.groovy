
package com.datastax.curriculum.gradle

import com.datastax.curriculum.gradle.tasks.SlidesTask
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.asciidoctor.gradle.AsciidoctorTask
import org.gradle.api.tasks.Delete
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
    createAndConfigureSlidesTasks(project)
    createAndConfigureDocsTasks(project)
    createAndConfigureCourseTasks(project)
    createAndConfigureVertexTask(project)
  }


  def createAndConfigureVertexTask(project) {
    project.tasks.create('vertex').configure {
      dependsOn << ['vertexSlides', 'vertexDocs']
      description = 'Builds all vertex materials'
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


  def createAndConfigureCourseTasks(project) {
    project.tasks.create('courseResources', CourseTask).configure {
      curriculumRootDir = this.curriculumRootDir
      description = 'Combines vertices into course sources'
      group = 'Curriculum'
    }

    project.tasks.create('course').configure {
      dependsOn << ['courseSlides', 'courseDocs']
      description = 'Builds a course'
      group = 'Curriculum'
    }

    project.tasks.create('bundle', Zip).configure {
      dependsOn << ['course']
      from project.buildDir
      exclude "lessc/", "distributions/"
      description = 'Bundles all course outputs into a distributable ZIP file'
      group = 'Curriculum'
    }
  }


  def createAndConfigureSlidesTasks(project) {
    def task

    task = project.tasks.create('vertexSlides', SlidesTask)
    configureSlidesTask(task)

    task = project.tasks.create('courseSlides', SlidesTask)
    configureSlidesTask(task)
    task.dependsOn << ['courseResources']
  }


  def createAndConfigureDocsTasks(project) {
    def task

    task = project.tasks.create('vertexDocs', AsciidoctorTask)
    configureDocsTask(task)
    task.attributes image_path: 'images',
                    exercise_number: 1

    task = project.tasks.create('courseDocs', AsciidoctorTask)
    configureDocsTask(task)
    task.dependsOn << ['courseResources']
  }


  def configureSlidesTask(task) {
    task.configure {
      imagePath = 'images'

      frameworkDir = this.frameworkDir
      buildDeckjsDir = this.buildDeckjsDir

      sourceDir "${project.projectDir}/src"
      sources {
        include 'slides.adoc'
      }

      backends 'deckjs'

      options template_dirs: [new File(templateDir, 'haml').absolutePath],
              eruby: 'erubis'

      attributes 'source-highlighter': 'coderay',
              idprefix: '',
              idseparator: '-'

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

      dependsOn << ['lessc']
      description = 'Builds the deck.js presentation slides only'
      group = 'Curriculum'
    }
  }


  def configureDocsTask(task) {
    task.configure {
      sourceDir "${project.projectDir}/src"
      sources {
        exclude 'slides.adoc'
        exclude 'includes.adoc'
        exclude 'slides/**/*'
      }

      backends 'html5'

      resources {
        from (project.projectDir) {
          include 'images/**/*.svg'
          include 'images/**/*.jpg'
          include 'images/**/*.png'
        }
      }

      options template_dirs : [new File(templateDir, 'haml').absolutePath ],
              eruby: 'erubis'

      attributes 'source-highlighter': 'coderay',
              idprefix: '',
              idseparator: '-',
              stylesheet: 'styles.css',
              stylesdir: project.file("${frameworkDir}/asciidoctor-backends/haml/html5/css")

      dependsOn << 'lessc'
      description = 'Builds documents that support the slides'
      group = 'Curriculum'
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
