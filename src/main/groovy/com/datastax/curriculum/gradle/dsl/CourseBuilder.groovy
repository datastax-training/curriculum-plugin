package com.datastax.curriculum.gradle.dsl

import com.datastax.curriculum.gradle.Course


class CourseBuilder {
  Course course
  def curriculumRoot
  def title


  CourseBuilder(curriculumRoot) {
    this.curriculumRoot = curriculumRoot
  }


  def title(String title) {
    course = new Course().withCurriculumRoot(curriculumRoot)
    course.name = title
    this.title = title
  }


  def module(String name, Closure c) {
    if(title == null) {
      throw new RuntimeException('A course must define a title before defining modules')
    }
    ModuleBuilder moduleBuilder = new ModuleBuilder(name, curriculumRoot)
    c.delegate = moduleBuilder
    c.call()
    course.addModule(moduleBuilder.module)

    return moduleBuilder.module
  }
}
