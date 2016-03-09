
package com.datastax.curriculum.gradle.tasks.course

import com.datastax.curriculum.gradle.Course
import com.datastax.curriculum.gradle.Module
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CourseTask extends DefaultTask {

  String title
  String baseURL = 'slides.html'

  List<String> vertexList
  List<Map> modules

  def curriculumRootDir
  def srcDir = "${project.projectDir}/src"

  Map<String, String> slideHeader = [:]

  private Course course


  @TaskAction
  def courseAction() {
    slideHeader.customjs = 'js/course.js'

    course = new Course(title)
                  .withCurriculumRoot(curriculumRoot)
                  .withSrcDir(srcDir)
    modules.each { moduleDescription ->
      def module = new Module(moduleDescription.name)
                        .withCurriculumRoot(curriculumRoot)
                        .withModuleFile(moduleDescription.vertices)
      course.addModule(module)
    }

    course.buildTo(project.buildDir)
  }


  def combineVertexJavaScript(File combinedJSFile) {
    def tempDir = File.createTempDir()

    // Copy vertex JS files to temp dir, expanding image_path macros
    vertexList.each { vertex ->
      project.copy {
        from("${curriculumRootDir}/${vertex}/js") {
          include '**/*.js'
        }
        into("${tempDir}/${vertex}/js")
        expand(['image_path': "images/${vertex}"])
      }
    }

    // Munge all vertex JS files into one big JS file
    combinedJSFile.withWriter { writer ->
      vertexList.each { vertex ->
        project.fileTree("${tempDir}/${vertex}/js").each { file ->
          file.withReader { reader ->
            writer.write(reader.text)
          }
        }
      }
      writer.flush()
    }
  }

//project.file("${project.buildDir}/images/${vertex}")
  def copyVertexImages(File destinationDir) {
    course.modules.each { module ->
      module.vertices.each { vertex ->
        vertex.copyImagesTo(destinationDir)
      }
    }
  }
}
