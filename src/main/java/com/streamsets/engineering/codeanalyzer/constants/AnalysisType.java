package com.streamsets.engineering.codeanalyzer.constants;

import java.util.HashMap;
import java.util.Map;

public enum AnalysisType {

  CodeLines("codeLines");

  private final String name;

  private static final Map<String, AnalysisType> map = new HashMap<>();

  static {
    for (AnalysisType analysisType : AnalysisType.values()) {
      map.put(analysisType.name, analysisType);
    }
  }

  AnalysisType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public static final AnalysisType obtainAnalysisTypeByName(String name) {
    return name == null ? null : map.get(name);
  }

}
