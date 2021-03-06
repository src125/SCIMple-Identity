package edu.psu.swe.scim.spec.protocol.data;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import edu.psu.swe.scim.server.filter.FilterLexer;
import edu.psu.swe.scim.server.filter.FilterParser;
import edu.psu.swe.scim.spec.protocol.filter.FilterParseException;
import edu.psu.swe.scim.spec.protocol.filter.ValuePathExpression;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class PatchOperationPath {

  private ValuePathExpression valuePathExpression;

  public PatchOperationPath() {
    
  }

  public PatchOperationPath(String patchPath) throws FilterParseException {
    parsePatchPath(patchPath);
  }

  protected void parsePatchPath(String patchPath) throws FilterParseException {
    FilterLexer l = new FilterLexer(new ANTLRInputStream(patchPath));
    FilterParser p = new FilterParser(new CommonTokenStream(l));
    p.setBuildParseTree(true);

    p.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        throw new IllegalStateException("failed to parse at line " + line + " due to " + msg, e);
      }
    });

    try {
      ParseTree tree = p.patchPath();
      PatchPathListener patchPathListener = new PatchPathListener();
      ParseTreeWalker.DEFAULT.walk(patchPathListener, tree);

      this.valuePathExpression = patchPathListener.getValuePathExpression();
    } catch (IllegalStateException e) {
      throw new FilterParseException(e);
    }
  }

  @Override
  public String toString() {
    return valuePathExpression.toFilter();
  }

}
