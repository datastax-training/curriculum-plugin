
package com.datastax.curriculum.gradle

import com.datastax.curriculum.gradle.tasks.SlidesTask
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.asciidoctor.gradle.AsciidoctorTask
import org.gradle.api.plugins.jetty.JettyRun
import org.gradle.api.tasks.bundling.Zip

class CurriculumPlugin
  implements Plugin<Project> {
  File curriculumRootDir
  File frameworkDir
  File templateDir
  File handoutConfDir
  File deckjsDir
  File slidesOutputDir
  File pdfWorkingDir
  File buildDeckjsDir


  void apply(Project project) {
    project.plugins.apply('org.asciidoctor.convert')
    project.plugins.apply('lesscss')
    project.plugins.apply('jetty')
    project.plugins.apply('com.bluepapa32.watch')

    curriculumRootDir = findProjectRoot(project)
    frameworkDir = new File(curriculumRootDir, 'framework')
    templateDir = new File(frameworkDir, 'asciidoctor-backends')
    handoutConfDir = new File(frameworkDir, 'handout')
    deckjsDir = new File(frameworkDir, 'deck.js')
    slidesOutputDir = project.buildDir
    pdfWorkingDir = new File(project.buildDir, 'screenshots')
    buildDeckjsDir = new File(slidesOutputDir, 'deck.js')

    applyTasks(project)
  }


  void applyTasks(Project project) {
    configureLesscTask(project)
    createAndConfigureSlidesTasks(project)
    createAndConfigureDocsTasks(project)
    createAndConfigureCourseTasks(project)
    createAndConfigureVertexTask(project)
    createAndConfigureSlidesExportTask(project)
    createAndConfigureSlidesHandoutTask(project)
    createAndConfigureServerTask(project)
    createAndConfigureWatchTask(project)
    createAndConfigureOutlineTask(project)
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
      exclude "lessc/", "distributions/", "screenshots/"
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


  def createAndConfigureSlidesExportTask(project) {
    project.tasks.create('exportSlides', ExportSlidesTask).configure {

      doFirst {
        this.pdfWorkingDir.mkdirs()
      }

      // QUESTION should we depend on vertexSlides, courseSlides or neither? or can we auto-detect?
      //dependsOn << ['vertexSlides']
      description = 'Exports a screenshot of each slide in the deck to PNG'
      group = 'Curriculum'
      dependsOn << ['course']
      workingDir = this.pdfWorkingDir
      slidesFile = "${slidesOutputDir}/slides.html"

      // set configuration that depends on workingDir being set
      // QUESTION is there a way to get this method to run automatically?
      postConfigure()

      // Disable hardware acceleration due to Oracle JVM 8u51 bug on OSX
      jvmArgs = ['-Dprism.order=sw', '-Dprism.verbose=true', '-Xmx4g']
    }
  }


  def createAndConfigureSlidesHandoutTask(project) {
    project.tasks.create('pdf', AsciidoctorTask).configure {
      dependsOn << ['exportSlides']
      description = 'Creates a handout for the slide deck that includes both slides and slide notes'
      group = 'Curriculum'
      backends 'pdf'
      sourceDir "${project.projectDir}/src"
      outputDir project.buildDir
      sources {
        include 'slides.adoc'
      }
      resources {}
      attributes icons: 'font',
                 // NOTE sets image_path for images within notes
                 // FIXME setting image_path needs to be done differently for vertex & course
                 //image_path: '../images',
                 // imagesdir is prepended to image target when target is relative
                 //imagesdir: project.buildDir,
                 // NOTE sets default slide_path
                 slide_path: 'slides@',
                 noheader: true,
                 nofooter: true,
                 // NOTE pagenums enables the running header/footer
                 pagenums: true,
                 'pdf-stylesdir': this.handoutConfDir.absolutePath,
                 'pdf-style': 'handout',
                 'pdf-fontsdir': new File(this.handoutConfDir, 'fonts').absolutePath,
                 // screenshotsdir must be absolute!
                 screenshotsdir: pdfWorkingDir.absolutePath
      extensions {
        // replaces page breaks (<<<) with anonymous section titles (== !) before parsing structure
        // this version processes attribute entries so attribute references can be used within include directives
        preprocessor { doc, reader ->
          def javaEmbedUtils = Class.forName('org.jruby.javasupport.JavaEmbedUtils')
          def rubyRuntime = doc.delegate().__ruby_object().getRuntime()
          def readerRuby = reader.__ruby_object()
          def initialAttributes = doc.attributes().callMethod('dup')

          def filteredLines = []
          def prevLine = null
          while (reader.hasMoreLines()) {
            def prevLineBlank = (prevLine == null || prevLine.isEmpty())
            // FIXME um, hello AsciidoctorJ, we need a readLine() method
            def line = prevLine = readerRuby.callMethod('read_line').toString()
            if (prevLineBlank) {
              if (line.equals('<<<')) {
                filteredLines << '== !'
              }
              else {
                def matcher = null
                // handle a basic attribute entry of type `:name: value`
                if ((matcher = (line =~ /^:(?<name>\w.*?):\p{Blank}+(?<value>.*)$/))) {
                  doc.attributes().put(matcher.group('name'), matcher.group('value'))
                }
                filteredLines << line
              }
            }
            else {
              filteredLines << line
            }
          }

          doc.attributes().callMethod('replace', initialAttributes)
          // FIXME Reader needs the restoreLines method exposed
          readerRuby.callMethod('restore_lines', javaEmbedUtils.javaToRuby(readerRuby.getRuntime(), filteredLines))
          reader
        }

        // replaces page breaks (<<<) with anonymous section titles (== !) before parsing structure
        // this version does not process attribute entries
        //preprocessor { doc, reader ->
        //  def javaEmbedUtils = Class.forName('org.jruby.javasupport.JavaEmbedUtils')
        //  def prevLine = null
        //  def filteredLines = reader.readLines().collect { line ->
        //    def prevLineBlank = (prevLine == null || prevLine.isEmpty())
        //    prevLine = line
        //    (prevLineBlank && line.equals('<<<')) ? '== !' : line
        //  }

        //  // FIXME Reader needs the restoreLines method exposed
        //  def readerRuby = reader.__ruby_object()
        //  readerRuby.callMethod('restore_lines', javaEmbedUtils.javaToRuby(readerRuby.getRuntime(), filteredLines))
        //  reader
        //}

        // builds the handout document structure (screenshot + notes)
        // NOTE low-level hacks are needed until AsciidoctorJ 1.6.0 is out
        treeprocessor { doc ->
          def javaEmbedUtils = Class.forName('org.jruby.javasupport.JavaEmbedUtils')
          def rubyRuntime = doc.delegate().__ruby_object().getRuntime()
          def screenshotsDir = doc.attributes().get('screenshotsdir')
          println doc.attributes()
          println screenshotsDir
          def screenshotFormat = new File(screenshotsDir, 'slide-001.png').exists() ? 'png' : 'jpg'

          def createSlideImageBlock = { parent, slideNumber ->
            def slideNumberFormatted = String.format('%03d', slideNumber)
            def screenshot = "${screenshotsDir}/slide-${slideNumberFormatted}.${screenshotFormat}".toString()
            createBlock(parent, 'image', null, [target: screenshot, 'pdf-width': '100%'], [:])
          }

          def createNoNotesBlock = { parent ->
            def noNotesBlock = createBlock(parent, 'open', null, [:], [content_model: ':compound'])
            noNotesBlock.blocks().add(createBlock(noNotesBlock, 'paragraph', '_No notes._', [:], [subs: ':default']))
            noNotesBlock
          }

          def createPageBreakBlock = { parent ->
            createBlock(parent, 'page_break', null, [:], [:])
          }

          def notes = doc.findBy([context: ':section']).findAll { section ->
            // FIXME getLevel() not available until AsciidoctorJ 1.5.3
            section.delegate().__ruby_object().callMethod('level').toString().equals('1')
          }.collect { section ->
            // FIXME blocks() is altering nodes which breaks conversion
            def notesForSection = section.delegate().blocks().find { child ->
              // FIXME we have a non-proxied object here, so call low-level methods
              'open'.equals(child.callMethod('context').toString()) &&
                  'true'.equals(child.callMethod('has_role?', javaEmbedUtils.javaToRuby(rubyRuntime, 'notes')).toString())
            }
            if (notesForSection != null) {
              // NOTE reparent if we really want to be thorough
              //notesForSection.callMethod('parent=', javaEmbedUtils.javaToRuby(rubyRuntime, doc.delegate()))
              notesForSection
            }
            else {
              createNoNotesBlock(doc)
            }
          }
          
          def notesByPage = []

          // NOTE create page for title slide
          def notesPreamble = null
          try {
            // NOTE for now, just assume the content of the preamble is the notes
            if (doc.findBy([context: ':preamble'])[0] != null) {
              // FIXME access low-level object since findBy() alters node and breaks conversion
              // NOTE unwrap preamble so we don't get special preamble formatting
              notesPreamble = doc.delegate().blocks()[0].blocks()[0]
            }
          }
          // NOTE AsciidoctorJ 1.5.2 throws an exception if no results are found
          catch (Exception e) {}

          notesByPage << createSlideImageBlock(doc, 1)
          notesByPage << (notesPreamble == null ? createNoNotesBlock(doc) : notesPreamble)

          notes.eachWithIndex { block, idx ->
            notesByPage << createPageBreakBlock(doc)
            notesByPage << createSlideImageBlock(doc, (idx + 2))
            notesByPage << block
          }

          // FIXME AbstractBlock needs to expose a replaceBlocks() method
          doc.delegate().blocks().clear()
          doc.delegate().blocks().addAll(notesByPage)
          null
        }
      }
    }
  }


  def configureSlidesTask(task) {
    task.configure {
      imagePath = 'images'

      frameworkDir = this.frameworkDir
      buildDeckjsDir = this.buildDeckjsDir

      dependsOn << ['lessc']
      description = 'Builds the deck.js presentation slides only'
      group = 'Curriculum'

      outputDir project.buildDir
      sourceDir "${project.projectDir}/src"
      sources {
        include 'slides*.adoc'
      }

      backends 'deckjs'

      options template_dirs: [new File(templateDir, 'haml').absolutePath]

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

      // Necessary to clean up bundling of course materials
      doLast {
//        project.copy {
//          from(frameworkDir) {
//            include 'deck.js/**'
//          }
//          into project.buildDir
//        }
        project.copy {
          from "${project.buildDir}/deckjs"
          into project.buildDir
        }
        project.copy {
          from "${project.buildDir}/asciidoc/deckjs"
          into project.buildDir
        }
        project.delete "${project.buildDir}/deckjs"
        project.delete "${project.buildDir}/asciidoc"
      }

    }
  }


  def configureDocsTask(task) {
    task.configure {
      dependsOn << 'lessc'
      description = 'Builds documents that support the slides'
      group = 'Curriculum'

      outputDir project.buildDir
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

      options template_dirs : [new File(templateDir, 'haml').absolutePath ]

      attributes 'source-highlighter': 'coderay',
              idprefix: '',
              idseparator: '-',
              stylesheet: 'styles.css',
              stylesdir: project.file("${frameworkDir}/asciidoctor-backends/haml/html5/css")

      // Necessary to clean up bundling of course materials
      doLast {
        project.copy {
          from "${project.buildDir}/html5"
          into project.buildDir
        }
        project.delete "${project.buildDir}/html5"
      }
    }
  }


  def createAndConfigureOutlineTask(project) {
    project.tasks.create('outlinePdf', AsciidoctorTask) {
      description = 'Creates a PDF of the course outline'
      group = 'Curriculum'

      outputDir project.buildDir
      sourceDir "${project.projectDir}/src"
      sources {
        include 'outline.adoc'
      }

      backends 'pdf'

    }
  }


  def createAndConfigureServerTask(project) {
    def webXmlFile = project.file("${project.buildDir}/tmp/web.xml")
    project.tasks.create('server', JettyRun).configure {
      description = 'Runs a local web server on port 8080'
      group = 'Curriculum'
      webAppSourceDirectory = project.buildDir
      contextPath = '/'
      webXml = webXmlFile
      doFirst {
        project.file("${project.buildDir}/tmp").mkdir()
        webXmlFile.withWriter { writer ->
          writer.println """\
<web-app>
  <servlet>
    <servlet-name>default</servlet-name>
    <servlet-class>org.mortbay.jetty.servlet.DefaultServlet</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>default</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
</web-app>
"""
        }
      }
    }
  }


  def createAndConfigureWatchTask(project) {
    project.watch.configure {
      group = 'Curriculum'
      description = 'Watch a vertex and run the vertexSlides task when it changes'
      vertex {
        files project.tasks.findByName('vertexSlides').inputs.sourceFiles
        tasks 'vertex'
      }
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
