package com.streamsets.engineering.codeanalyzer;

import com.streamsets.engineering.codeanalyzer.exceptions.CodeAnalysisException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CodeLines {

  private static final DecimalFormat integerFormat = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.ITALIAN));

  private String inputDirectoryName;
  private String outputDirectoryName;

  private int qJava = 0;
  private int qTotalLines = 0;
  private int qEmptyLLines = 0;
  private int qCommentLines = 0;
  private int qOpeningBraceLines = 0;
  private int qClosingBraceLines = 0;
  private int qCodeLines = 0;

  public CodeLines(String inputDirectoryName, String outputDirectoryName) {
    setInputDirectoryName(inputDirectoryName);
  }

  public void run() throws CodeAnalysisException, IOException {
    StopWatch watch = new StopWatch();
    watch.start();

    File inputDirectory = new File(getInputDirectoryName());
    //File outputDirectory = new File(getInputDirectoryName(), getOutputDirectoryName());

    if (!inputDirectory.isDirectory()) {
      throw new CodeAnalysisException("Input directory is not a folder: " + getInputDirectoryName());
    }
    process(inputDirectory);

    System.out.println("qJava.............: " + integerFormat.format(qJava));
    System.out.println("qTotalLines.......: " + integerFormat.format(qTotalLines));
    System.out.println("qEmptyLLines......: " + integerFormat.format(qEmptyLLines));
    System.out.println("qCommentLines.....: " + integerFormat.format(qCommentLines));
    System.out.println("qOpeningBraceLines: " + integerFormat.format(qOpeningBraceLines));
    System.out.println("qClosingBraceLines: " + integerFormat.format(qClosingBraceLines));
    System.out.println("qCodeLines........: " + integerFormat.format(qCodeLines));

    System.out.println(integerFormat.format(qJava));
    System.out.println(integerFormat.format(qTotalLines));
    System.out.println(integerFormat.format(qEmptyLLines));
    System.out.println(integerFormat.format(qCommentLines));
    System.out.println(integerFormat.format(qOpeningBraceLines));
    System.out.println(integerFormat.format(qClosingBraceLines));
    System.out.println(integerFormat.format(qCodeLines));

    watch.stop();
    System.out.println("Time Elapsed: " + watch.getTime());
  }

  private void process(File file) throws CodeAnalysisException, IOException {
    Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (path.toFile().isFile() &&
            //path.toFile().getAbsolutePath().contains("/src/main") &&
            path.toFile().getName().endsWith(".java")) {
          qJava++;
          String line = null;
          LineIterator iterator = null;
          try {
            iterator = FileUtils.lineIterator(path.toFile(), "UTF-8");
            while (iterator.hasNext()) {
              line = iterator.nextLine();
              qTotalLines++;
              if (line.isBlank()) {
                qEmptyLLines++;
              } else  if (line.trim().equals("{")) {
                  qOpeningBraceLines++;
              } else  if (line.trim().equals("}")) {
                qClosingBraceLines++;
              } else if (line.trim().startsWith("/") || line.trim().startsWith("*")) {
                qCommentLines++;
              } else {
                qCodeLines++;
              }
            }
          } catch (IOException exception) {
            throw new RuntimeException(exception);
          } finally {
            LineIterator.closeQuietly(iterator);
          }
        }
        return FileVisitResult.CONTINUE;
      }
    });
  }

  public String getInputDirectoryName() {
    return inputDirectoryName;
  }

  public CodeLines setInputDirectoryName(String inputDirectoryName) {
    this.inputDirectoryName = inputDirectoryName;
    return this;
  }

  public String getOutputDirectoryName() {
    return outputDirectoryName;
  }

  public CodeLines setOutputDirectoryName(String outputDirectoryName) {
    this.outputDirectoryName = outputDirectoryName;
    return this;
  }

}
