
package com.datastax.curriculum.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CurriculumBaseTask extends DefaultTask {
  def command
  def options = []

  @TaskAction
  def curriculumAction() {

  }
}
