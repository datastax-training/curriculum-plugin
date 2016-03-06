package com.datastax.curriculum.gradle

class Course {
  String name
  List<Module> modules = []
  def slideHeader = [:]
  def exerciseHeader = [:]
  def curriculumRoot
  File exerciseFile
  File solutionFile
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

    solutionFile = new File(srcDir, 'solutions.adoc')
    exerciseFile = new File(srcDir, 'exercises.adoc')
  }


  Course withSrcDir(srcDir) {
    this.srcDir = srcDir
    return this
  }


  Course addModule(Module module) {
    modules << module
  }


  void build() {
    buildSolutionsFile()
    buildExercisesFile()
  }


  void buildSolutionsFile() {

  }


  void buildExercisesFile() {
    def exerciseNumber = 1
    exerciseFile.withWriter { file ->
      modules.each { module ->
        module.vertices.each { vertex ->
          file.println vertex.courseExerciseInclude(exerciseNumber++)
        }
      }
    }
  }

}
