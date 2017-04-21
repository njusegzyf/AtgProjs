package nju.seg.zhangyf.util;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IFunctionDeclaration;

import com.google.common.base.Preconditions;

public final class CdtUtil {

  public static final String getFunctionSinguatureOrName(final IFunctionDeclaration function) {
    Preconditions.checkNotNull(function);

    try {
      return function.getSignature();
    } catch (final CModelException ignored) {
      return function.getElementName();
    }
  }

  @Deprecated
  private CdtUtil() {}
}
