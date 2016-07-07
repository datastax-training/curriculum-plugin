package com.datastax.curriculum.gradle.questions

import org.asciidoctor.ast.ContentPart
import org.asciidoctor.ast.Document
import org.asciidoctor.ast.StructuredDocument

import static org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class QuestionParsingTests {
  String propertyGraphPath = 'src/test/resources/curriculum/graph/graph-definition/property-graph/'

  QuestionParser qp

  @Before
  void setup() {
    File quizFile = new File("${propertyGraphPath}/src/quiz.adoc")
    qp = new QuestionParser(quizFile)
  }


  @Test
  void testStructure() {

    def sections = qp.getQuestionSections()
    assertNotNull sections
    assertEquals 3, sections.size()

    String question
    List<String> answers

    question = qp.getQuestion(sections[0])
    assertNotNull question
    assertEquals 'Which statement about a property graph is correct?', question
    answers = qp.getAnswers(sections[0])
    assertNotNull answers
    assertEquals 4, answers.size()
    assertEquals 'A property graph is a Directed Acyclic Graph (DAG)', answers[0]
    assertEquals 'A property graph is a directed, binary, attributed multi-graph', answers[1]
    assertEquals 'A property graph is a directed, binary multi-graph with labeled properties', answers[2]
    assertEquals 'A property graph is a collection of vertices, hyperedges, and properties', answers[3]

    Map<Character, String> answerMap

    answerMap = qp.getAnswerMap(sections[0])
    assertNotNull answerMap
    assertEquals 4, answerMap.size()
    assertEquals 'A property graph is a Directed Acyclic Graph (DAG)', answerMap.A
    assertEquals 'A property graph is a directed, binary, attributed multi-graph', answerMap.B
    assertEquals 'A property graph is a directed, binary multi-graph with labeled properties', answerMap.C
    assertEquals 'A property graph is a collection of vertices, hyperedges, and properties', answerMap.D

    question = qp.getQuestion(sections[1])
    assertNotNull question
    assertEquals 'A multi-property can be associated with a:', question
    question = qp.getQuestion(sections[2])
    assertNotNull question
    assertEquals 'A meta-property can be associated with a:', question



//    doc.blocks().each { block ->
//      dumpBlock(block)
//      if(block.blocks.size()) {
//        block.blocks.each { b ->
//          dumpBlock(b)
//        }
//      }
//    }

//
//    StructuredDocument structuredDoc
//    structuredDoc = asciidoctor.readDocumentStructure(quizFile, [STRUCTURE_MAX_LEVEL: 100])
//    assertNotNull structuredDoc
//    def sections = structuredDoc.getPartsByContext('section')
//    assertNotNull sections
//    assertEquals 3, sections.size()
//
//    sections.each { section ->
//      ContentPart question, answers, correct
//      StructuredDocument sectionDoc = StructuredDocument.createStructuredDocument(header, section.parts)
//
//      question = sectionDoc.getPartByRole('question')
//      answers = sectionDoc.getPartByStyle('upperalpha')
//      correct = sectionDoc.getPartByRole('correct')
//
//      assertNotNull question
//      assertNotNull answers
//      assertNotNull correct
//
//      println question.content
//      println "ANSWERS"
//      dumpPart(answers)
//      answers.parts.each { part ->
//        dumpPart(part)
//        println part.content
//      }
//
//      println "CORRECT"
//      dumpPart(correct)
//      correct.parts.each { part ->
//        dumpPart(part)
//        println part.content
//      }
//    }
  }



  void dumpPart(part) {
    println "${part.id}, ${part.level}, ${part.context}, ${part.style}, ${part.role}, ${part.title}, ${part.parts.size()}"
  }
}
