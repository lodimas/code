package com.streamsets.engineering.codeanalyzer;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import com.streamsets.engineering.codeanalyzer.constants.AnalysisType;
import com.streamsets.engineering.codeanalyzer.exceptions.CodeAnalysisException;

import java.io.IOException;

@Command(name = "codeanalyzer", description = "StreamSets Code Analyzer")
public class CodeAnalyzer {

  @Option(name = {"--analysis", "-a"},
          title = "Analysis",
          description = "Type of analysis")
  @Required
  private String analysisTypeName;

  @Option(name = {"--input", "-i"},
          title = "Input directory",
          description = "Directory with Java code to analyze")
  @Required
  private String inputDirectoryName;

  @Option(name = {"--output", "-o"},
          title = "Output directory",
          description = "Directory to place modified analyzed Java code")
  @Required
  private String outputDirectoryName;

  public static void main(String[] arguments) {
    try {
      SingleCommand<CodeAnalyzer> parser = SingleCommand.singleCommand(CodeAnalyzer.class);
      CodeAnalyzer command = parser.parse(arguments);
      command.run();
    } catch (CodeAnalysisException | IOException exception) {
      System.err.println(exception);
    }
  }

  private void run() throws CodeAnalysisException, IOException {
    AnalysisType analysisType = AnalysisType.obtainAnalysisTypeByName(getAnalysisTypeName());
    if (analysisType == null) {
      throw new CodeAnalysisException("Analysis type not recognized");
    }

    switch (analysisType) {
      case CodeLines:
      {
        CodeLines codeLinesAnalyzer = new CodeLines(getInputDirectoryName(), getOutputDirectoryName());
        codeLinesAnalyzer.run();
        break;
      }
      default:
      {
        throw new CodeAnalysisException("Analysis type not supported");
      }
    }
  }

  public String getAnalysisTypeName() {
    return this.analysisTypeName;
  }

  public CodeAnalyzer setAnalysisTypeName(String analysisTypeName) {
    this.analysisTypeName = analysisTypeName;
    return this;
  }

  public String getInputDirectoryName() {
    return this.inputDirectoryName;
  }

  public CodeAnalyzer setInputDirectoryName(String inputDirectoryName) {
    this.inputDirectoryName = inputDirectoryName;
    return this;
  }

  public String getOutputDirectoryName() {
    return this.outputDirectoryName;
  }

  public CodeAnalyzer setOutputDirectoryName(String outputDirectoryName) {
    this.outputDirectoryName = outputDirectoryName;
    return this;
  }

}
