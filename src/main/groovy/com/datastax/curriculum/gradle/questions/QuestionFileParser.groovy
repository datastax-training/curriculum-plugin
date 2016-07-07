package com.datastax.curriculum.gradle.questions

import org.asciidoctor.Asciidoctor
import org.asciidoctor.ast.Document
import org.asciidoctor.ast.StructuralNode


class QuestionFileParser {
  Asciidoctor asciidoctor = Asciidoctor.Factory.create()
  Document document


  QuestionFileParser(File file) {
    document = asciidoctor.loadFile(file, [:])
  }


  List<StructuralNode> getQuestionSections() {
    def nodes = []

    document.blocks().each { block ->
      if(block.context == 'section') {
        nodes << block
      }
    }

    return nodes
  }


  List<Question> parseQuestions() {
    questionSections.collect { node -> Question.fromNode(node) }
  }


  void dump(block) {
    println "${block.level}, ${block.title}, ${block.style}, ${block.context}, ${block.blocks?.size()}, ${block.getRoles()}"
  }
}
