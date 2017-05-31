package nju.seg.zhangyf.atgwrapper.cfg;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.ui.console.MessageConsoleStream;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;

import cn.nju.seg.atg.model.Constraint;
import cn.nju.seg.atg.model.SimpleCFGNode;
import cn.nju.seg.atg.model.constraint.BinaryExpression;
import cn.nju.seg.atg.model.constraint.Expression;
import cn.nju.seg.atg.parse.PathCoverage;
import cn.nju.seg.atg.parse.TestBuilder;
import cn.nju.seg.atg.util.CFGPath;
import nju.seg.zhangyf.util.Util;

/**
 * Note: Even though `MessageConsoleStream` and `PrintStream` both have `print` and `println` methods,
 * they are not compatible, so we have different overloads for the two types.
 * 
 * @author Zhang Yifan
 */
public final class CfgPathUtil {

  /** Prints all `CFGPath`s of a function. */
  public static void printAllCfgPaths(final IFunctionDeclaration function, final PrintStream out) {
    Preconditions.checkNotNull(function);

    // create a `PathCoverage` to build CFG and get all paths
    // Note: `PathCoverage` store all paths in `TestBuilder`'s static field
    final PathCoverage cc = new PathCoverage("atg-pc");
    cc.buildCFG(function);
    PathCoverage.buildPaths();

    try {
      out.println("All paths in function: " + function.getSignature());
    } catch (CModelException ignored) {}
    out.println();

    // print all paths
    for (int i = 0; i < TestBuilder.uncheckedPaths.size(); i++) {
      final CFGPath path = TestBuilder.uncheckedPaths.get(i);

      out.println("Path index " + (i + 1) + ":"); // Note: the tool index paths from 1
      // CFGBuilder.printPath(path);
      CfgPathUtil.printCfgPath(path, out);
      out.println();
    }
    // for (final CFGPath path : TestBuilder.uncheckedPaths) {
    // // CFGBuilder.printPath(path);
    // CfgPathUtil.printCfgPath(path, out);
    // out.println();
    // }
  }

  /** Prints all `CFGPath`s of a function. */
  public static void printAllCfgPaths(final IFunctionDeclaration function, final MessageConsoleStream out) {
    Preconditions.checkNotNull(function);

    // create a `PathCoverage` to build CFG and get all paths
    // Note: `PathCoverage` store all paths in `TestBuilder`'s static field
    final PathCoverage cc = new PathCoverage("atg-pc");
    cc.buildCFG(function);
    PathCoverage.buildPaths();

    try {
      out.println("All paths in function: " + function.getSignature());
    } catch (CModelException ignored) {}
    out.println();

    // print all paths
    for (int i = 0; i < TestBuilder.uncheckedPaths.size(); i++) {
      final CFGPath path = TestBuilder.uncheckedPaths.get(i);

      out.println("Path index " + (i + 1) + ":"); // Note: the tool index paths from 1
      // CFGBuilder.printPath(path);
      CfgPathUtil.printCfgPath(path, out);
      out.println();
    }
    // for (final CFGPath path : TestBuilder.uncheckedPaths) {
    // // CFGBuilder.printPath(path);
    // CfgPathUtil.printCfgPath(path, out);
    // out.println();
    // }
  }

  /**
   * Prints the `CFGPath`.
   * 
   * @see cn.nju.seg.atg.parse.TestBuilder#printPath
   */
  public static void printCfgPath(final CFGPath cfgPath, final PrintStream out) {
    Preconditions.checkNotNull(cfgPath);

    final List<SimpleCFGNode> path = cfgPath.getPath();
    out.println("Path length: " + path.size());
    for (final SimpleCFGNode node : path) {
      out.print(node.getName());

      final Constraint constraint = node.getConstraint();
      if (constraint != null) {
        final BinaryExpression binaryExpression = Util.as(constraint.getExpression(), BinaryExpression.class);
        if (binaryExpression != null) {
          CfgPathUtil.printExpressionIds(binaryExpression, out);
        }
      }
      out.println();
    }
  }

  public static void printCfgPath(final CFGPath cfgPath, final MessageConsoleStream out) {
    Preconditions.checkNotNull(cfgPath);

    final List<SimpleCFGNode> path = cfgPath.getPath();
    out.println("Path length: " + path.size());
    for (final SimpleCFGNode node : path) {
      out.print(node.getName());

      final Constraint constraint = node.getConstraint();
      if (constraint != null) {
        final BinaryExpression binaryExpression = Util.as(constraint.getExpression(), BinaryExpression.class);
        if (binaryExpression != null) {
          CfgPathUtil.printExpressionIds(binaryExpression, out);
        }
      }
      out.println();
    }
  }

  /** Prints all expression ids with DFS. */
  static void printExpressionIds(final Expression expr, final PrintStream out) {
    assert expr != null;

    final BinaryExpression binaryExpression = Util.as(expr, BinaryExpression.class);
    if (binaryExpression != null) {
      final String id = binaryExpression.getId();
      if (id != null) {
        out.print(", " + binaryExpression.getId());
      } else {
        printExpressionIds(binaryExpression.getOperand1(), out);
        printExpressionIds(binaryExpression.getOperand2(), out);
      }
    }
  }

  /** Prints all expression ids with DFS. */
  static void printExpressionIds(final Expression expr, final MessageConsoleStream out) {
    assert expr != null;

    final BinaryExpression binaryExpression = Util.as(expr, BinaryExpression.class);
    if (binaryExpression != null) {
      final String id = binaryExpression.getId();
      if (id != null) {
        out.print(", " + binaryExpression.getId());
      } else {
        printExpressionIds(binaryExpression.getOperand1(), out);
        printExpressionIds(binaryExpression.getOperand2(), out);
      }
    }
  }

  public static Optional<CFGPath> getRelatedCfgPath(final Collection<String> pathNodeNames,
                                                    final Collection<CFGPath> searchPaths) {
    Preconditions.checkNotNull(pathNodeNames);
    Preconditions.checkNotNull(searchPaths);

    final int pathLength = pathNodeNames.size();

    return searchPaths.stream().filter(cfgPath -> {
      final ArrayList<SimpleCFGNode> path = cfgPath.getPath();
      if (pathLength == path.size()) {
        return Streams.zip(pathNodeNames.stream(), path.stream(), // zip node names and actual nodes
                           (nodeName, cfgNode) -> nodeName.equals(cfgNode.getName())) // returns a new element indicates whether their names are equal
                      .allMatch(b -> b); // the path matches the node name seqs if all node names are equal
      } else {
        return false;
      }
    }).findFirst();
  }

  /**
   * @deprecated Use {@link CFGPath#getPathNodeNames()} instead.
   */
  @Deprecated
  public static Stream<String> cfgPathNodeNames(final CFGPath cfgPath) {
    Preconditions.checkNotNull(cfgPath);

    return cfgPath.getPath().stream()
                  .map(node -> node.getName());
  }

  @Deprecated
  private CfgPathUtil() {}
}
