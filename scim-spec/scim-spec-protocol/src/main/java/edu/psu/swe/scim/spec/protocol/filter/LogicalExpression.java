package edu.psu.swe.scim.spec.protocol.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogicalExpression implements FilterExpression, ValueFilterExpression {

  FilterExpression left;
  LogicalOperator operator;
  FilterExpression right;
  
  @Override
  public String toFilter() {
    boolean leftParens = left instanceof LogicalExpression;
    boolean rightParens = right instanceof LogicalExpression;

    String leftString = (leftParens ? "(" : "") + left.toFilter() + (leftParens ? ")" : "");
    String rightString = (rightParens ? "(" : "") + right.toFilter() + (rightParens ? ")" : "");
    
    return leftString + " " + operator + " " + rightString;
  }

  @Override
  public void setAttributePath(String urn, String parentAttributeName) {
    this.left.setAttributePath(urn, parentAttributeName);
    this.right.setAttributePath(urn, parentAttributeName);
  }

  @Override
  public String toUnqualifiedFilter() {
    boolean leftParens = this.left instanceof LogicalExpression;
    boolean rightParens = this.right instanceof LogicalExpression;

    String leftString = (leftParens ? "(" : "") + left.toUnqualifiedFilter() + (leftParens ? ")" : "");
    String rightString = (rightParens ? "(" : "") + right.toUnqualifiedFilter() + (rightParens ? ")" : "");

    return leftString + " " + operator + " " + rightString;
  }
}
