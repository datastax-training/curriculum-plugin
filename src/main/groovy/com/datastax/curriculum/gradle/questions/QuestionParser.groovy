package com.datastax.curriculum.gradle.questions

import org.asciidoctor.Asciidoctor
import org.asciidoctor.ast.Document
import org.asciidoctor.ast.StructuralNode


class QuestionParser {
  Asciidoctor asciidoctor = Asciidoctor.Factory.create()
  Document document


  QuestionParser(File file) {
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


  String getQuestion(StructuralNode questionSection) {
    def nodes = questionSection.findBy([role: 'question'])
    return nodes ? nodes[0].content : null
  }


  List<String> getAnswers(StructuralNode questionSection) {
    def list = questionSection.findBy([style: 'upperalpha'])
    return list?.items.collect { item -> item.text }[0]
  }


  Map<Character, String> getAnswerMap(List<String> answers) {
    def letters = 'A'..'Z'
    def map = [:]
    answers.eachWithIndex { answer, index ->
      map[letters[index]] = answer
    }
    return map
  }


  Map<Character, String> getAnswerMap(StructuralNode questionSection) {
    getAnswerMap(getAnswers(questionSection))
  }

  boolean isQuestionMultipleChoice() {
    return false
  }


  boolean isQuestionTrueFalse() {
    return false
  }

  void dump(block) {
    println "${block.level}, ${block.title}, ${block.style}, ${block.context}, ${block.blocks?.size()}, ${block.getRoles()}"
  }



}
