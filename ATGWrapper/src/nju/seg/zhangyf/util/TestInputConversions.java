package nju.seg.zhangyf.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import nju.seg.zhangyf.atg.util.NioUtil;

/**
 * @author Zhang Yifan
 */
public final class TestInputConversions {

  public static void convertTcasInputs() {
    final Path inputFilePath = Paths.get("/root/eclipseWorkspace/ATGWrapperCpp/src/tcas/Tcas_CW_TestDriver.java");
    final Path outputFilePath = Paths.get("/root/eclipseWorkspace/ATGWrapperCpp/batchConfigs/FSE2014/Tcas/V00/Tcas_CW_Inputs.conf");

    final Pattern testMethodCallPattern = Pattern.compile("Tcas\\.start_symbolic\\((.*?)\\)\\;");
    final Matcher testMethodCallMatcher = testMethodCallPattern.matcher("");

    int inputCount = 0;
    final StringBuilder result = new StringBuilder();

    final ImmutableList<String> inputFileLines;
    try {
      inputFileLines = Files.asCharSource(inputFilePath.toFile(), Charsets.UTF_8).readLines();
    } catch (final IOException ignored) {
      System.out.println("Can not read input file: " + inputFilePath.toString());
      return;
    }

    // append result file head
    result.append("{\n");
    result.append("  Inputs: [\n");

    for (final String line : inputFileLines) {
      testMethodCallMatcher.reset(line);
      if (testMethodCallMatcher.find()) {
        final String argumentsString = testMethodCallMatcher.group(1);
        result.append("    { Input: [ " + argumentsString + " ] },\n");
        ++inputCount;
      }
    }

    // append result file end
    result.append("  ]\n");
    result.append("}\n");

    try {
      NioUtil.createFileAndNonExistParentDirectories(outputFilePath);
      Files.asCharSink(outputFilePath.toFile(), Charsets.UTF_8).write(result);
      System.out.println("Convert " + inputCount + " inputs to output file: " + outputFilePath.toString());
    } catch (final IOException ignored) {
      System.out.println("Can not write output file: " + outputFilePath.toString());
    }
  }

  public static void main(final String[] args) {
    TestInputConversions.convertTcasInputs();
  }

  private TestInputConversions() {
    throw new UnsupportedOperationException();
  }
}
