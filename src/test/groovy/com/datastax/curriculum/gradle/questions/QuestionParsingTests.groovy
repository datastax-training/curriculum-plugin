package com.datastax.curriculum.gradle.questions

import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class QuestionParsingTests {
  String propertyGraphPath = 'src/test/resources/curriculum/graph/graph-definition/property-graph/'
  QuestionFileParser qp
  List<Question> questions


  @Before
  void setup() {
    File quizFile = new File("${propertyGraphPath}/src/quiz.adoc")
    qp = new QuestionFileParser(quizFile)
    questions = qp.parseQuestions()
  }


  @Test
  void testSectionParsing() {
    assertNotNull questions
    assertEquals 3, questions.size()
  }


  @Test
  void testQuestionText() {
    assertNotNull questions[0].question
    assertEquals 'Which statement about a property graph is correct?', questions[0].question

    assertNotNull questions[1].question
    assertEquals 'Proximity searches can find matches for phrases regardless of the order of the terms.', questions[1].question

    assertNotNull questions[2].question
    assertEquals 'What Gremlin I/O flavors are available? Select all that apply.', questions[2].question
  }


  @Test
  void testAnswerMaps() {
    Map<Character, String> answerMap

    answerMap = questions[0].getAnswerMap()
    assertNotNull answerMap
    assertEquals 4, answerMap.size()
    assertEquals 'A property graph is a Directed Acyclic Graph (DAG)', answerMap.A
    assertEquals 'A property graph is a directed, binary, attributed multi-graph', answerMap.B
    assertEquals 'A property graph is a directed, binary multi-graph with labeled properties', answerMap.C
    assertEquals 'A property graph is a collection of vertices, hyperedges, and properties', answerMap.D


    answerMap = questions[2].getAnswerMap()
    assertNotNull answerMap
    assertEquals 4, answerMap.size()
    assertEquals 'GraphSON', answerMap.A
    assertEquals 'GraphML', answerMap.B
    assertEquals 'Gryo', answerMap.C
    assertEquals 'GraphCSV', answerMap.D
  }

  @Test
  void testCorrectAnswers() {
    def correctAnswers
    
    correctAnswers = questions[0].getCorrectAnswers()
    assertNotNull correctAnswers
    assertEquals 1, correctAnswers.size()
    assertEquals 'B', correctAnswers[0]

    correctAnswers = questions[1].getCorrectAnswers()
    assertNotNull correctAnswers
    assertEquals 1, correctAnswers.size()
    assertEquals 'true', correctAnswers[0].toLowerCase()

    correctAnswers = questions[2].getCorrectAnswers()
    assertNotNull correctAnswers
    assertEquals 3, correctAnswers.size()
    assertEquals 'A', correctAnswers[0]
    assertEquals 'B', correctAnswers[1]
    assertEquals 'C', correctAnswers[2]
  }
}
