package com.datastax.curriculum.gradle.questions

import org.asciidoctor.ast.StructuralNode


class Question {
  String question
  List<String> answers
  Map<Character, String> answerMap
  List<Character> correctAnswers
  Boolean correctAnswer


  private Question(StructuralNode node) {
    question = parseQuestion(node)
    answers = parseAnswers(node)
    answerMap = parseAnswerMap(node)
    correctAnswers = parseCorrectAnswers(node)
  }


  static Question fromNode(StructuralNode node) {
    return new Question(node)
  }


  private String parseQuestion(StructuralNode questionSection) {
    def nodes = questionSection.findBy([role: 'question'])
    return nodes ? nodes[0].content : null
  }


  private List<String> parseAnswers(StructuralNode questionSection) {
    def list = questionSection.findBy([style: 'upperalpha'])
    return list?.items.collect { item -> item.text }[0]
  }


  private Map<Character, String> parseAnswerMap(List<String> answers) {
    def letters = 'A'..'Z'
    def map = [:]
    answers.eachWithIndex { answer, index ->
      map[letters[index]] = answer
    }
    return map
  }


  private Map<Character, String> parseAnswerMap(StructuralNode questionSection) {
    parseAnswerMap((List<String>)parseAnswers(questionSection))
  }


  private List<String> parseCorrectAnswers(StructuralNode questionSection) {
    def list = questionSection.findBy([role: 'correct'])
    return list?.items.collect { item -> item.text }[0]
  }


  boolean isMultipleChoice() {
    return false
  }


  boolean isTrueFalse() {
    return false
  }


}
