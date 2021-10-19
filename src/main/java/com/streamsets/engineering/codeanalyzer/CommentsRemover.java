package com.streamsets.engineering.codeanalyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentsRemover {

  private CommentsRemover() {
  }

  public static String doAction(String content) {
    JavaParser javaParser = createJavaParser();
    ParseResult<CompilationUnit> result = javaParser.parse(content);
    Optional<CompilationUnit> optionalCompilationUnit = result.getResult();
    if (!optionalCompilationUnit.isPresent()) {
      return "";
    }
    CompilationUnit compilationUnit = optionalCompilationUnit.get();
    removeComments(compilationUnit);
    return LexicalPreservingPrinter.print(compilationUnit);
  }

  private static void removeComments(CompilationUnit compilationUnit) {
    List<Comment> comments = compilationUnit.getAllContainedComments();
    List<Comment> unwantedComments = comments.stream()
        .filter(CommentsRemover::isValidCommentType)
        .collect(Collectors.toList());
    unwantedComments.forEach(Node::remove);
  }

  private static JavaParser createJavaParser() {
    ParserConfiguration parserConfiguration = new ParserConfiguration();
    parserConfiguration.setLexicalPreservationEnabled(true);
    return new JavaParser(parserConfiguration);
  }

  private static boolean isValidCommentType(Comment comment) {
    return comment instanceof LineComment || comment instanceof BlockComment;
  }

}
