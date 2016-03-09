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
  File srcDir
  File buildDir


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


  void buildTo(File buildDir) {
    buildSolutionsFile()
    buildExercisesFile()
    buildSlides()
    copyVertexImagesTo(buildDir)
  }


  void buildSlides() {
    buildModuleListFile()
    modules.eachWithIndex { module, moduleNumber ->
      buildModuleSlideFile(module, moduleNumber)
    }
  }


  File buildModuleSlideFile(Module module, int moduleNumber) {
    File slides = new File("${srcDir.absolutePath}/slides-${moduleNumber}.adoc")
    slides.withWriter { writer ->
      writer.println "= ${module.name}"
      writer.println convertSlideHeaderToAsciidoc()
      writer.println ''
      module.vertices.each { vertex ->
        writer.println vertex.slideIncludeAsciidoc()
      }
    }
    return slides
  }


  void buildModuleListFile() {
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


  def convertSlideHeaderToAsciidoc() {
    convertHeaderMapToAsciidoc(slideHeader)
  }


  def convertExerciseHeaderToAsciidoc() {
    convertHeaderMapToAsciidoc(exerciseHeader)
  }


  def convertHeaderMapToAsciidoc(header) {
    SortedMap map = new TreeMap(header)
    map.collect { key, value -> ":${key}: ${value}".trim() }.join('\n')
  }


  void copyVertexImagesTo(File destinationRoot) {
    modules.each { module ->
      module.vertices.each { vertex ->
        vertex.copyImagesTo(destinationRoot)
      }
    }
  }
}
