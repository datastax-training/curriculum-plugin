package com.datastax.curriculum.gradle

class Course {
  String name
  List<Module> modules = []
  def slideHeader = [:]
  def exerciseHeader = [:]


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


}
