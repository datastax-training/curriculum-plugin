package com.datastax.curriculum.gradle

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

/**
 * A task that configures and executes deck2pdf for exporting the slides in a
 * slide deck to images (png or jpg).
 */
class ExportSlidesTask extends JavaExec {
  @Input
  String format = 'jpg'

  @Input
  Integer width = 1600

  @Input
  Integer height = 900

  @Input
  Float quality = 95

  @Input
  String profile = 'deck2pdf/deckjs-handout'

  ExportSlidesTask() {

    // setMain('') instead of main = ''
    // This works around an odd intermittent exception where Groovy complains about
    // the right-side argument being a String. Don't ask me.
    setMain('me.champeau.deck2pdf.Main')
    configureDependencies()
    configureClasspath()
  }

  @Override
  @TaskAction
  void exec() {
    computeArgs()
    super.exec()
  }

  /**
   * Configure settings that depend on user-provided settings
   */
  void postConfigure() {
    configureIO()
  }

  void configureDependencies() {
    if (project.configurations.findByName('deck2pdf') == null) {
      project.configurations.create('deck2pdf').setVisible(false)
      project.dependencies {
        deck2pdf 'me.champeau.deck2pdf:deck2pdf:0.3.0'
      }
    }
  }

  // We need to put the curriculum-plugin on the deck2pdf classpath so it can find the deckjs profile script
  // Another approach would be to put this script in the curriculum project itself and reference it from there
  void configureClasspath() {
    this.classpath = project.configurations.deck2pdf +
        project.buildscript.configurations.classpath.filter { it.name.contains('curriculum-plugin') }
  }

  void configureIO() {
    this.inputs.file("$workingDir/slides.html")
    this.outputs.files("$workingDir/slide-001.jpg", "$workingDir/slide-001.png")
  }

  void computeArgs() {
    this.args = [
      "--profile=$profile",
      "--width=$width",
      "--height=$height",
      "--quality=$quality",
      'slides.html',
      "slide-%03d.$format"
    ]
  }
}
