package com.datastax.curriculum.gradle

class Course {
  String name
  List<Module> modules = []
  def slideHeader = [:]
  def exerciseHeader = [:]
  def curriculumRoot
  File exerciseFile
  File solutionFile
  File moduleFile
  def srcDir


  Course(name) {
    this.name = name
    slideHeader = [
      backend: 'deckjs',
      deckjs_theme: 'datastax',
      icons: 'font',
      navigation: '',
      notes: '',
      split: '',
      goto: '',
      animation: ''
    ]
    exerciseHeader = [
      backend: 'html5'
    ]
  }


  void setCurriculumRoot(curriculumRoot) {
    if(curriculumRoot instanceof File) {
      this.curriculumRoot = curriculumRoot.absoluteFile
    }
    else {
      this.curriculumRoot = new File(curriculumRoot).absoluteFile
    }
  }


  Course withCurriculumRoot(cr) {
    setCurriculumRoot(cr)
    return this
  }


  void setSrcDir(srcDir) {
    if(srcDir instanceof File) {
      this.srcDir = srcDir.absoluteFile
    }
    else {
      this.srcDir = new File(srcDir).absoluteFile
    }

    solutionFile = new File(srcDir, 'solution-list.adoc').absoluteFile
    exerciseFile = new File(srcDir, 'exercise-list.adoc').absoluteFile
    moduleFile = new File(srcDir, 'module-list.adoc').absoluteFile
  }


  Course withSrcDir(srcDir) {
    setSrcDir(srcDir)
    return this
  }


  Course addModule(Module module) {
    modules << module
    return this
  }


  void build() {
    buildSolutionsFile()
    buildExercisesFile()
  }


  void buildSlides() {

  }


  void buildModuleFile() {
    moduleFile.withWriter { file ->
      modules.eachWithIndex { module, moduleNumber ->
        file.println module.vertexListAsciidoc(moduleNumber + 1)
      }
    }
  }


  void buildSolutionFile() {
    def solutionNumber = 1
    solutionFile.withWriter { file ->
      modules.each { module ->
        module.vertices.each { vertex ->
          file.println vertex.solutionIncludeAsciidoc(solutionNumber++)
        }
      }
    }
  }


  void buildExerciseFile() {
    def exerciseNumber = 1
    exerciseFile.withWriter { file ->
      modules.each { module ->
        module.vertices.each { vertex ->
          file.println vertex.exerciseIncludeAsciidoc(exerciseNumber++)
        }
      }
    }
  }
}
