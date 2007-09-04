/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

// Added 20/12/2000 JED. Define the package for the class


grammar JPQL;

options {
    k = 3; // This is the number of tokens to look ahead to
    superClass = 'org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser';
}


tokens {
    ABS='abs';
    ALL='all';
    AND='and';
    ANY='any';
    AS='as';
    ASC='asc';
    AVG='avg';
    BETWEEN='between';
    BOTH='both';
    BY='by';
    CONCAT='concat';
    COUNT='count';
    CURRENT_DATE='current_date';
    CURRENT_TIME='current_time';
    CURRENT_TIMESTAMP='current_timestamp';
    DESC='desc';
    DELETE='delete';
    DISTINCT='distinct';
    EMPTY='empty';
    ESCAPE='escape';
    EXISTS='exists';
    FALSE='false';
    FETCH='fetch';
    FROM='from';
    GROUP='group';
    HAVING='having';
    IN='in';
    INNER='inner';
    IS='is';
    JOIN='join';
    LEADING='leading';
    LEFT='left';
    LENGTH='length';
    LIKE='like';
    LOCATE='locate';
    LOWER='lower';
    MAX='max';
    MEMBER='member';
    MIN='min';
    MOD='mod';
    NEW='new';
    NOT='not';
    NULL='null';
    OBJECT='object';
    OF='of';
    OR='or';
    ORDER='order';
    OUTER='outer';
    SELECT='select';
    SET='set';
    SIZE='size';
    SQRT='sqrt';
    SOME='some';
    SUBSTRING='substring';
    SUM='sum';
    TRAILING='trailing';
    TRIM='trim';
    TRUE='true';
    UNKNOWN='unknown';
    UPDATE='update';
    UPPER='upper';
    WHERE='where';
}
@header {
    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import java.util.List;
    import java.util.ArrayList;

    import static org.eclipse.persistence.internal.jpa.parsing.NodeFactory.*;
    import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierException;
    import org.eclipse.persistence.exceptions.JPQLException;
}

@lexer::header {
    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierStartException;
}



@members{
    /** The root node of the parsed EJBQL query. */
    private Object queryRoot;

    /** Flag indicating whether aggregates are allowed. */
    private boolean aggregatesAllowed = false;

    /** */
    protected void setAggregatesAllowed(boolean allowed) {
        this.aggregatesAllowed = allowed;
    }

    /** */
    protected boolean aggregatesAllowed() {
        return aggregatesAllowed;
    }

    /** */
    protected void validateAbstractSchemaName(Token token) 
        throws RecognitionException {
        String text = token.getText();
        if (!isValidJavaIdentifier(token.getText())) {
            throw new InvalidIdentifierException(token);
        }
    }

    /** */
    protected void validateAttributeName(Token token) 
        throws RecognitionException {
        String text = token.getText();
        if (!isValidJavaIdentifier(token.getText())) {
            throw new InvalidIdentifierException(token);
        }
    }

    /** */
    protected boolean isValidJavaIdentifier(String text) {
        if ((text == null) || text.equals(""))
            return false;

        // check first char
        if (!Character.isJavaIdentifierStart(text.charAt(0)))
            return false;

        // check remaining characters
        for (int i = 1; i < text.length(); i++) {
            if (!Character.isJavaIdentifierPart(text.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }

    protected String convertStringLiteral(String text) {
        // skip leading and trailing quotes
        String literal = text.substring(1, text.length() - 1);
        
        // convert ''s to 's
        while (true) {
            int index = literal.indexOf("''");
            if (index == -1) {
                break;
            }
            literal = literal.substring(0, index) + 
                      literal.substring(index + 1, literal.length());
        }

        return literal;
    }

    /** */
    public Object getRootNode() {
        return queryRoot;
    }
}

document
    : root = selectStatement {queryRoot = $root.node;}
    | root = updateStatement {queryRoot = $root.node;}
    | root = deleteStatement {queryRoot = $root.node;}
    ;

selectStatement returns [Object node]
@init { 
    node = null;
}
    : select  = selectClause
      from    = fromClause
      (where   = whereClause)?
      (groupBy = groupByClause)?
      (having  = havingClause)?
      (orderBy = orderByClause)?
      EOF 
        { 
            $node = factory.newSelectStatement(0, 0, $select.node, $from.node, $where.node, 
                                              $groupBy.node, $having.node, $orderBy.node); 
        }
    ;

//================================================

updateStatement returns [Object node]
@init { 
    node = null; 
}
    : update = updateClause
      set    = setClause
      (where  = whereClause)?
      EOF { $node = factory.newUpdateStatement(0, 0, $update.node, $set.node, $where.node); }
    ;

updateClause returns [Object node]
@init { 
    node = null; 
}
    : u = UPDATE schema = abstractSchemaName 
        ((AS)? ident = IDENT )?
        { 
            String schemaName = null;
            if ($ident != null){
                schemaName = $ident.getText();
            }
            $node = factory.newUpdateClause($u.getLine(), $u.getCharPositionInLine(), 
                                           $schema.schema, schemaName); 
        } 
    ;  

setClause returns [Object node]
scope{
	List assignments;
}
@init { 
    node = null; 
    $setClause::assignments = new ArrayList();
}
    : t = SET n = setAssignmentClause { $setClause::assignments.add($n.node); }
        (COMMA n = setAssignmentClause { $setClause::assignments.add($n.node); } )*
        { $node = factory.newSetClause($t.getLine(), $t.getCharPositionInLine(), $setClause::assignments); }
     ;

setAssignmentClause returns [Object node]
@init { 
    node = null;
}
        @after{ 
            $node = factory.newSetAssignmentClause($t.getLine(), $t.getCharPositionInLine(), 
                                                  $target.node, $value.node); 
        }
    : target = setAssignmentTarget t=EQUALS value = newValue
    ;

setAssignmentTarget returns [Object node]
@init { 
    node = null;
}
    : n = attribute { $node = $n.node;}
    | n =  pathExpression {$node = $n.node;}
    ;

newValue returns [Object node]
@init { node = null; }
    : n = simpleArithmeticExpression {$node = $n.node;}
    | n1 = NULL 
        { $node = factory.newNullLiteral($n1.getLine(), $n1.getCharPositionInLine()); } 
    ;

//================================================

deleteStatement returns [Object node]
@init { 
    node = null; 
}
    : delete = deleteClause
      (where = whereClause)?
      EOF { $node = factory.newDeleteStatement(0, 0, $delete.node, $where.node); }
    ;

deleteClause returns [Object node]
scope{
	String variable;
}
@init { 
    node = null; 
    $deleteClause::variable = null;
}
    : t=DELETE FROM schema = abstractSchemaName 
        ((AS)? ident=IDENT { $deleteClause::variable = $ident.getText(); })?
        { 
            $node = factory.newDeleteClause($t.getLine(), $t.getCharPositionInLine(), 
                                           $schema.schema, $deleteClause::variable); 
        }
    ;

//================================================

selectClause returns [Object node]
scope{
    boolean distinct;
    List exprs;
}
@init { 
    node = null;
    $selectClause::distinct = false;
    $selectClause::exprs = new ArrayList();
}
    : t=SELECT (DISTINCT { $selectClause::distinct = true; })?
      n = selectExpression {$selectClause::exprs.add($n.node); }
      ( COMMA n = selectExpression  { $selectClause::exprs.add($n.node); } )*
        { 
            $node = factory.newSelectClause($t.getLine(), $t.getCharPositionInLine(), 
                                           $selectClause::distinct, $selectClause::exprs); 
        }
    ;

selectExpression returns [Object node]
@init { node = null; }
    : n = pathExprOrVariableAccess {$node = $n.node;}
    | n = aggregateExpression {$node = $n.node;}
    | OBJECT LEFT_ROUND_BRACKET n = variableAccess RIGHT_ROUND_BRACKET  {$node = $n.node;}
    | n = constructorExpression  {$node = $n.node;}
    ;

pathExprOrVariableAccess returns [Object node]
@init {
    node = null;
}
    : n = variableAccess {$node = $n.node;}
        (d=DOT right = attribute
            { $node = factory.newDot($d.getLine(), $d.getCharPositionInLine(), $node, $right.node); }
        )* 
    ;

aggregateExpression returns [Object node]
scope{
    boolean distinct;
}
@init { 
    node = null; 
    $aggregateExpression::distinct = false;
}
    : t1=AVG LEFT_ROUND_BRACKET (DISTINCT { $aggregateExpression::distinct = true; })?
        n = stateFieldPathExpression RIGHT_ROUND_BRACKET 
        { $node = factory.newAvg($t1.getLine(), $t1.getCharPositionInLine(), $aggregateExpression::distinct, $n.node); }
    | t2=MAX LEFT_ROUND_BRACKET (DISTINCT { $aggregateExpression::distinct = true; })? 
        n = stateFieldPathExpression RIGHT_ROUND_BRACKET
        { $node = factory.newMax($t2.getLine(), $t2.getCharPositionInLine(), $aggregateExpression::distinct, $n.node); }
    | t3=MIN LEFT_ROUND_BRACKET (DISTINCT { $aggregateExpression::distinct = true; })?
        n = stateFieldPathExpression RIGHT_ROUND_BRACKET
        { $node = factory.newMin($t3.getLine(), $t3.getCharPositionInLine(), $aggregateExpression::distinct, $n.node); }
    | t4=SUM LEFT_ROUND_BRACKET (DISTINCT { $aggregateExpression::distinct = true; })?
        n = stateFieldPathExpression RIGHT_ROUND_BRACKET
        { $node = factory.newSum($t4.getLine(), $t4.getCharPositionInLine(), $aggregateExpression::distinct, $n.node); }
    | t5=COUNT LEFT_ROUND_BRACKET (DISTINCT { $aggregateExpression::distinct = true; })?
        n = pathExprOrVariableAccess RIGHT_ROUND_BRACKET
        { $node = factory.newCount($t5.getLine(), $t5.getCharPositionInLine(), $aggregateExpression::distinct, $n.node); }
    ;

constructorExpression returns [Object node]
scope{
    List args;
}
@init { 
    node = null;
    $constructorExpression::args = new ArrayList();
}
    : t = NEW className = constructorName
        LEFT_ROUND_BRACKET 
        n = constructorItem {$constructorExpression::args.add($n.node); } 
        ( COMMA n = constructorItem { $constructorExpression::args.add($n.node); } )*
        RIGHT_ROUND_BRACKET
        { 
            $node = factory.newConstructor($t.getLine(), $t.getCharPositionInLine(), 
                                          $className.className, $constructorExpression::args); 
        }
    ;

constructorName returns [String className]
scope{
    StringBuffer buf;
}
@init { 
    className = null;
    $constructorName::buf = new StringBuffer(); 
}
    : i1=IDENT { $constructorName::buf.append($i1.getText()); }
        ( DOT i2=IDENT { $constructorName::buf.append('.').append($i2.getText()); })*
        { $className = $constructorName::buf.toString(); }
    ;

constructorItem returns [Object node]
@init { node = null; }
    : n = pathExprOrVariableAccess { $node = $n.node;}
    | n = aggregateExpression {$node = $n.node;}
    ;

fromClause returns [Object node]
scope{
    List varDecls;
}
@init { 
    node = null; 
    $fromClause::varDecls = new ArrayList();
}
    : t=FROM identificationVariableDeclaration[$fromClause::varDecls]
        (COMMA  ( identificationVariableDeclaration[$fromClause::varDecls]
                | n = collectionMemberDeclaration  {$fromClause::varDecls.add($n.node); }
                ) 
        )*
        { $node = factory.newFromClause($t.getLine(), $t.getCharPositionInLine(), $fromClause::varDecls); }
    ;

identificationVariableDeclaration [List varDecls]
    : node = rangeVariableDeclaration { varDecls.add($node.node); } 
        ( node = join { $varDecls.add($node.node); } )*
    ;

rangeVariableDeclaration returns [Object node]
@init { 
    node = null; 
}
    : schema = abstractSchemaName (AS)? i=IDENT
        { 
            $node = factory.newRangeVariableDecl($i.getLine(), $i.getCharPositionInLine(), 
                                                $schema.schema, $i.getText()); 
        }
    ;

// Non-terminal abstractSchemaName first matches any token to allow abstract 
// schema names that are keywords (such as order, etc.). 
// Method validateAbstractSchemaName throws an exception if the text of the 
// token is not a valid Java identifier.
abstractSchemaName returns [String schema]
@init { schema = null; }
    : ident=. 
        {
            $schema = $ident.getText();
            validateAbstractSchemaName($ident); 
        }
    ;

join returns [Object node]
@init { 
    node = null;
}
    : outerJoin = joinSpec
      ( n = joinAssociationPathExpression (AS)? i=IDENT
        {
            $node = factory.newJoinVariableDecl($i.getLine(), $i.getCharPositionInLine(), 
                                               $outerJoin.outer, $n.node, $i.getText()); 
        }
      | t=FETCH n = joinAssociationPathExpression 
        { 
            $node = factory.newFetchJoin($t.getLine(), $t.getCharPositionInLine(), 
                                        $outerJoin.outer, $n.node); }
      )
    ;

joinSpec returns [boolean outer]
@init { outer = false; }
    : (LEFT (OUTER)? { $outer = true; }  | INNER  )? JOIN
    ;

collectionMemberDeclaration returns [Object node]
@init { node = null; }
    : t=IN LEFT_ROUND_BRACKET n = collectionValuedPathExpression RIGHT_ROUND_BRACKET 
      (AS)? i=IDENT
      { 
          $node = factory.newCollectionMemberVariableDecl(
                $t.getLine(), $t.getCharPositionInLine(), $n.node, $i.getText()); 
        }
    ;

collectionValuedPathExpression returns [Object node]
@init { node = null; }
    : n = pathExpression {$node = $n.node;}
    ;

associationPathExpression returns [Object node]
@init { node = null; }
    : n = pathExpression {$node = $n.node;}
    ;

joinAssociationPathExpression returns [Object node]
@init {
    node = null; 
}
    : left = variableAccess d=DOT right = attribute
        { $node = factory.newDot($d.getLine(), $d.getCharPositionInLine(), $left.node, $right.node); }
    ;

singleValuedPathExpression returns [Object node]
@init { node = null; }
    : n = pathExpression {$node = $n.node;}
    ;

stateFieldPathExpression returns [Object node]
@init { node = null; }
    : n = pathExpression {$node = $n.node;}
    ;

pathExpression returns [Object node]
@init { 
    node = null; 
}
    : n = variableAccess {$node = $n.node;}
        (d=DOT right = attribute
            {
                $node = factory.newDot($d.getLine(), $d.getCharPositionInLine(), $node, $right.node); 
            }
        )+
    ;

// Non-terminal attribute first matches any token to allow abstract 
// schema names that are keywords (such as order, etc.). 
// Method validateAttributeName throws an exception if the text of the 
// token is not a valid Java identifier.
attribute returns [Object node]
@init { node = null; }

    : i=.
        { 
            validateAttributeName($i);
            $node = factory.newAttribute($i.getLine(), $i.getCharPositionInLine(), $i.getText()); 
        }
    ;

variableAccess returns [Object node]
@init { node = null; }
    : i=IDENT
        { $node = factory.newVariableAccess($i.getLine(), $i.getCharPositionInLine(), $i.getText()); }
    ;

whereClause returns [Object node]
@init { node = null; }
    : t=WHERE n = conditionalExpression 
        {
            $node = factory.newWhereClause($t.getLine(), $t.getCharPositionInLine(), $n.node); 
        } 
    ;

conditionalExpression returns [Object node]
@init { 
    node = null; 
}
    : n = conditionalTerm {$node = $n.node;}
        (t=OR right = conditionalTerm
            { $node = factory.newOr($t.getLine(), $t.getCharPositionInLine(), $node, $right.node); }
        )*
    ;

conditionalTerm returns [Object node]
@init { 
    node = null; 
}
    : n = conditionalFactor {$node = $n.node;}
        (t=AND right = conditionalFactor
            { $node = factory.newAnd($t.getLine(), $t.getCharPositionInLine(), $node, $right.node); }
        )* 
    ;

conditionalFactor returns [Object node]
@init { node = null; }
    : (n=NOT)? 
        ( n1 = conditionalPrimary 
          {
              $node = $n1.node; 
              if ($n != null) {
                  $node = factory.newNot($n.getLine(), $n.getCharPositionInLine(), $n1.node); 
              }
          }
        | n1 = existsExpression[(n!=null)]  {$node = $n1.node;}
        )
    ;

conditionalPrimary  returns [Object node]
@init { node = null; }
    : (LEFT_ROUND_BRACKET conditionalExpression) =>
        LEFT_ROUND_BRACKET n = conditionalExpression RIGHT_ROUND_BRACKET {$node = $n.node;}
    | n = simpleConditionalExpression {$node = $n.node;}
    ;

simpleConditionalExpression returns [Object node]
@init { 
    node = null; 
}
    : left = arithmeticExpression 
        n = simpleConditionalExpressionRemainder[$left.node] {$node = $n.node;}
    ;

simpleConditionalExpressionRemainder [Object left] returns [Object node]
@init { node = null; }
    : n = comparisonExpression[left] {$node = $n.node;}
    | (n1=NOT)? n = conditionWithNotExpression[(n1!=null), left] {$node = $n.node;}
    | IS (n2=NOT)? n = isExpression[(n2!=null), left] {$node = $n.node;}
    ;

conditionWithNotExpression [boolean not, Object left] returns [Object node]
@init { node = null; }
    : n = betweenExpression[not, left] {$node = $n.node;}
    | n = likeExpression[not, left] {$node = $n.node;}
    | n= inExpression[not, left] {$node = $n.node;}
    | n= collectionMemberExpression[not, left] {$node = $n.node;}
    ;

isExpression [boolean not, Object left] returns [Object node]
@init { node = null; }
    : n = nullComparisonExpression[not, left] {$node = $n.node;}
    | n = emptyCollectionComparisonExpression[not, left] {$node = $n.node;}
    ;

betweenExpression [boolean not, Object left] returns [Object node]
@init {
    node = null;
}
    : t=BETWEEN
        lowerBound = arithmeticExpression AND upperBound = arithmeticExpression
        {
            $node = factory.newBetween($t.getLine(), $t.getCharPositionInLine(),
                                      $not, $left, $lowerBound.node, $upperBound.node);
        }
    ;

inExpression [boolean not, Object left] returns [Object node]
scope{
    List items; 
}
@init {
    node = null;
    $inExpression::items = new ArrayList();
}
    : t=IN
        LEFT_ROUND_BRACKET
        ( itemNode = inItem { $inExpression::items.add($itemNode.node); }
            ( COMMA itemNode = inItem { $inExpression::items.add($itemNode.node); } )*
            {
                $node = factory.newIn($t.getLine(), $t.getCharPositionInLine(),
                                     $not, $left, $inExpression::items);
            }
        | subqueryNode = subquery
            {
                $node = factory.newIn($t.getLine(), $t.getCharPositionInLine(),
                                     $not, $left, $subqueryNode.node);
            }
        )
        RIGHT_ROUND_BRACKET
    ;

inItem returns [Object node]
@init { node = null; }
    : n = literalString {$node = $n.node;}
    | n = literalNumeric {$node = $n.node;}
    | n = inputParameter {$node = $n.node;}
    ;

likeExpression [boolean not, Object left] returns [Object node]
@init {
    node = null;
}
    : t=LIKE pattern = likeValue
        (escapeChars = escape)?
        {
            $node = factory.newLike($t.getLine(), $t.getCharPositionInLine(), $not,
                                   $left, $pattern.node, $escapeChars.node);
        }
    ;

escape returns [Object node]
@init { 
    node = null; 
}
    : t=ESCAPE escapeClause = likeValue
        { $node = factory.newEscape($t.getLine(), $t.getCharPositionInLine(), $escapeClause.node); }
    ;

likeValue returns [Object node]
@init { node = null; }
    : n = literalString {$node = $n.node;}
    | n = inputParameter {$node = $n.node;}
    ;

nullComparisonExpression [boolean not, Object left] returns [Object node]
@init { node = null; }
    : t= NULL
        { $node = factory.newIsNull($t.getLine(), $t.getCharPositionInLine(), $not, $left); }
    ;

emptyCollectionComparisonExpression [boolean not, Object left] returns [Object node]
@init { node = null; }
    : t= EMPTY
        { $node = factory.newIsEmpty($t.getLine(), $t.getCharPositionInLine(), $not, $left); }
    ;

collectionMemberExpression [boolean not, Object left] returns [Object node]
@init { node = null; }
    : t= MEMBER (OF)? n = collectionValuedPathExpression
        { 
            $node = factory.newMemberOf($t.getLine(), $t.getCharPositionInLine(), 
                                       $not, $left, $n.node); 
        }
    ;

existsExpression [boolean not] returns [Object node]
@init { 
    node = null;
}
    : t=EXISTS LEFT_ROUND_BRACKET subqueryNode = subquery RIGHT_ROUND_BRACKET
        { 
            $node = factory.newExists($t.getLine(), $t.getCharPositionInLine(), 
                                     $not, $subqueryNode.node); 
        }
    ;

comparisonExpression [Object left] returns [Object node]
@init { node = null; }
    : t1=EQUALS n = comparisonExpressionRightOperand 
        { $node = factory.newEquals($t1.getLine(), $t1.getCharPositionInLine(), $left, $n.node); }
    | t2=NOT_EQUAL_TO n = comparisonExpressionRightOperand 
        { $node = factory.newNotEquals($t2.getLine(), $t2.getCharPositionInLine(), $left, $n.node); }
    | t3=GREATER_THAN n = comparisonExpressionRightOperand 
        { $node = factory.newGreaterThan($t3.getLine(), $t3.getCharPositionInLine(), $left, $n.node); }
    | t4=GREATER_THAN_EQUAL_TO n = comparisonExpressionRightOperand 
        { $node = factory.newGreaterThanEqual($t4.getLine(), $t4.getCharPositionInLine(), $left, $n.node); }
    | t5=LESS_THAN n = comparisonExpressionRightOperand 
        { $node = factory.newLessThan($t5.getLine(), $t5.getCharPositionInLine(), $left, $n.node); }
    | t6=LESS_THAN_EQUAL_TO n = comparisonExpressionRightOperand 
        { $node = factory.newLessThanEqual($t6.getLine(), $t6.getCharPositionInLine(), $left, $n.node); }
    ;

comparisonExpressionRightOperand returns [Object node]
@init { node = null; }
    : n = arithmeticExpression {$node = $n.node;}
    | n = anyOrAllExpression {$node = $n.node;}
    ;

arithmeticExpression returns [Object node]
@init { node = null; }
    : n = simpleArithmeticExpression {$node = $n.node;}
    | LEFT_ROUND_BRACKET n = subquery RIGHT_ROUND_BRACKET {$node = $n.node;}
    ;

simpleArithmeticExpression returns [Object node]
@init { 
    node = null; 
}
    : n = arithmeticTerm {$node = $n.node;}
        ( p=PLUS right = arithmeticTerm 
            { $node = factory.newPlus($p.getLine(), $p.getCharPositionInLine(), $node, $right.node); }
        | m=MINUS right = arithmeticTerm
            { $node = factory.newMinus($m.getLine(), $m.getCharPositionInLine(), $node, $right.node); }
        )* 
    ;

arithmeticTerm  returns [Object node]
@init { 
    node = null; 
}
    : n = arithmeticFactor {$node = $n.node;}
        ( m=MULTIPLY right = arithmeticFactor 
            { $node = factory.newMultiply($m.getLine(), $m.getCharPositionInLine(), $node, $right.node); }
        | d=DIVIDE right = arithmeticFactor
            { $node = factory.newDivide($d.getLine(), $d.getCharPositionInLine(), $node, $right.node); }
        )* 
    ;

arithmeticFactor returns [Object node]
@init { node = null; }
    : p=PLUS  n = arithmeticPrimary 
        {$node = factory.newUnaryPlus($p.getLine(), $p.getCharPositionInLine(), $n.node); } 
    | m=MINUS n = arithmeticPrimary  
        { $node = factory.newUnaryMinus($m.getLine(), $m.getCharPositionInLine(), $n.node); }
    | n = arithmeticPrimary {$node = $n.node;}
    ;

arithmeticPrimary returns [Object node]
@init { node = null; }
    : { aggregatesAllowed() }? n = aggregateExpression {$node = $n.node;}
    | n = variableAccess {$node = $n.node;}
    | n = stateFieldPathExpression {$node = $n.node;}
    | n = functionsReturningNumerics {$node = $n.node;}
    | n = functionsReturningDatetime {$node = $n.node;}
    | n = functionsReturningStrings {$node = $n.node;}
    | n = inputParameter {$node = $n.node;}
    | n = literalNumeric {$node = $n.node;}
    | n = literalString {$node = $n.node;}
    | n = literalBoolean {$node = $n.node;}
    | LEFT_ROUND_BRACKET n = simpleArithmeticExpression RIGHT_ROUND_BRACKET {$node = $n.node;}
    ;

anyOrAllExpression returns [Object node]
@init { node = null; }
    : a=ALL LEFT_ROUND_BRACKET n = subquery RIGHT_ROUND_BRACKET
        { $node = factory.newAll($a.getLine(), $a.getCharPositionInLine(), $n.node); }
    | y=ANY LEFT_ROUND_BRACKET n = subquery RIGHT_ROUND_BRACKET
        { $node = factory.newAny($y.getLine(), $y.getCharPositionInLine(), $n.node); }
    | s=SOME LEFT_ROUND_BRACKET n = subquery RIGHT_ROUND_BRACKET
        { $node = factory.newSome($s.getLine(), $s.getCharPositionInLine(), $n.node); }
    ;

stringPrimary returns [Object node]
@init { node = null; }
    : n = literalString {$node = $n.node;}
    | n = functionsReturningStrings {$node = $n.node;}
    | n = inputParameter {$node = $n.node;}
    | n = stateFieldPathExpression {$node = $n.node;}
    ;

// Literals and Low level stuff

literal returns [Object node]
@init { node = null; }
    : n = literalNumeric {$node = $n.node;}
    | n = literalBoolean {$node = $n.node;}
    | n = literalString {$node = $n.node;}
    ;

literalNumeric returns [Object node]
@init { node = null; }
    : i=INTEGER_LITERAL
    { 
            $node = factory.newIntegerLiteral($i.getLine(), $i.getCharPositionInLine(), 
                                             Integer.valueOf($i.getText())); 
        }
    | l=LONG_LITERAL 
        { 
            String text = l.getText();
            // skip the tailing 'l'
            text = text.substring(0, text.length() - 1);
            $node = factory.newLongLiteral($l.getLine(), $l.getCharPositionInLine(), 
                                          Long.valueOf(text)); 
        }
    | f=FLOAT_LITERAL
        { 
            $node = factory.newFloatLiteral($f.getLine(), $f.getCharPositionInLine(),
                                           Float.valueOf($f.getText()));
        }
    | d=DOUBLE_LITERAL
        { 
            $node = factory.newDoubleLiteral($d.getLine(), $d.getCharPositionInLine(),
                                            Double.valueOf($d.getText()));
        }
    ;

literalBoolean returns [Object node]
@init { node = null; }
    : t=TRUE  
        { $node = factory.newBooleanLiteral($t.getLine(), $t.getCharPositionInLine(), Boolean.TRUE); }
    | f=FALSE 
        { $node = factory.newBooleanLiteral($f.getLine(), $f.getCharPositionInLine(), Boolean.FALSE); }
    ;

literalString returns [Object node]
@init { node = null; }
    : d=STRING_LITERAL_DOUBLE_QUOTED 
        { 
            $node = factory.newStringLiteral($d.getLine(), $d.getCharPositionInLine(), 
                                            convertStringLiteral($d.getText())); 
        }
    | s=STRING_LITERAL_SINGLE_QUOTED
        { 
            $node = factory.newStringLiteral($s.getLine(), $s.getCharPositionInLine(), 
                                            convertStringLiteral($s.getText())); 
        }
    ;

inputParameter returns [Object node]
@init { node = null; }
    : p=POSITIONAL_PARAM
        { 
            // skip the leading ?
            String text = $p.getText().substring(1);
            $node = factory.newPositionalParameter($p.getLine(), $p.getCharPositionInLine(), text); 
        }
    | n=NAMED_PARAM
        { 
            // skip the leading :
            String text = $n.getText().substring(1);
            $node = factory.newNamedParameter($n.getLine(), $n.getCharPositionInLine(), text); 
        }
    ;

functionsReturningNumerics returns [Object node]
@init { node = null; }
    : n = abs {$node = $n.node;}
    | n = length {$node = $n.node;}
    | n = mod {$node = $n.node;}
    | n = sqrt {$node = $n.node;}
    | n = locate {$node = $n.node;}
    | n = size {$node = $n.node;}
    ;

functionsReturningDatetime returns [Object node]
@init { node = null; }
    : d=CURRENT_DATE 
        { $node = factory.newCurrentDate($d.getLine(), $d.getCharPositionInLine()); }
    | t=CURRENT_TIME
        { $node = factory.newCurrentTime($t.getLine(), $t.getCharPositionInLine()); }
    | ts=CURRENT_TIMESTAMP
        { $node = factory.newCurrentTimestamp($ts.getLine(), $ts.getCharPositionInLine()); }
    ;

functionsReturningStrings returns [Object node]
@init { node = null; }
    : n = concat {$node = $n.node;}
    | n = substring {$node = $n.node;}
    | n = trim {$node = $n.node;}
    | n = upper {$node = $n.node;}
    | n = lower {$node = $n.node;}
    ;

// Functions returning strings
concat returns [Object node]
@init { 
    node = null;
}
    : c=CONCAT 
        LEFT_ROUND_BRACKET 
        firstArg = stringPrimary COMMA secondArg = stringPrimary
        RIGHT_ROUND_BRACKET
        { $node = factory.newConcat($c.getLine(), $c.getCharPositionInLine(), $firstArg.node, $secondArg.node); }
    ;

substring returns [Object node]
@init { 
    node = null;
}
    : s=SUBSTRING   
        LEFT_ROUND_BRACKET
        string = stringPrimary COMMA
        start = simpleArithmeticExpression COMMA
        lengthNode = simpleArithmeticExpression
        RIGHT_ROUND_BRACKET
        { 
            $node = factory.newSubstring($s.getLine(), $s.getCharPositionInLine(), 
                                        $string.node, $start.node, $lengthNode.node); 
        }
    ;

trim returns [Object node]
@init { 
    node = null; 
}
    : t=TRIM
        LEFT_ROUND_BRACKET 
        ( ( trimSpec trimChar FROM )=> trimSpecIndicator = trimSpec trimCharNode = trimChar FROM )? 
        n = stringPrimary
        RIGHT_ROUND_BRACKET
        {
            $node = factory.newTrim($t.getLine(), $t.getCharPositionInLine(), 
                                   $trimSpecIndicator.trimSpec, $trimCharNode.node, $n.node); 
        }
    ;

trimSpec returns [TrimSpecification trimSpec]
@init { trimSpec = TrimSpecification.BOTH; }
    : LEADING
        { $trimSpec = TrimSpecification.LEADING; }
    | TRAILING
        { $trimSpec = TrimSpecification.TRAILING; }
    | BOTH
        { $trimSpec = TrimSpecification.BOTH; }
    | // empty rule
    ;

trimChar returns [Object node]
@init { node = null; }
    : n = literalString {$node = $n.node;}
    | n = inputParameter {$node = $n.node;}
    | // empty rule
    ;

upper returns [Object node]
@init { node = null; }
    : u=UPPER LEFT_ROUND_BRACKET n = stringPrimary RIGHT_ROUND_BRACKET
        { $node = factory.newUpper($u.getLine(), $u.getCharPositionInLine(), $n.node); }
    ;

lower returns [Object node]
@init { node = null; }
    : l=LOWER LEFT_ROUND_BRACKET n = stringPrimary RIGHT_ROUND_BRACKET
        { $node = factory.newLower($l.getLine(), $l.getCharPositionInLine(), $n.node); }
    ;

// Functions returning numerics
abs returns [Object node]
@init { node = null; }
    : a=ABS LEFT_ROUND_BRACKET n = simpleArithmeticExpression RIGHT_ROUND_BRACKET
        { $node = factory.newAbs($a.getLine(), $a.getCharPositionInLine(), $n.node); }
    ;

length returns [Object node]
@init { node = null; }
    : l=LENGTH LEFT_ROUND_BRACKET n = stringPrimary RIGHT_ROUND_BRACKET
        { $node = factory.newLength($l.getLine(), $l.getCharPositionInLine(), $n.node); }
    ;

locate returns [Object node]
@init { 
    node = null; 
}
    : l=LOCATE
        LEFT_ROUND_BRACKET 
        pattern = stringPrimary COMMA n = stringPrimary
        ( COMMA startPos = simpleArithmeticExpression )?
        RIGHT_ROUND_BRACKET
        { 
            $node = factory.newLocate($l.getLine(), $l.getCharPositionInLine(), 
                                     $pattern.node, $n.node, $startPos.node); 
        }
    ;

size returns [Object node]
@init { node = null; }
    : s=SIZE 
        LEFT_ROUND_BRACKET n = collectionValuedPathExpression RIGHT_ROUND_BRACKET
        { $node = factory.newSize($s.getLine(), $s.getCharPositionInLine(), $n.node);}
    ;

mod returns [Object node]
@init { 
    node = null; 
}
    : m=MOD LEFT_ROUND_BRACKET
        left = simpleArithmeticExpression COMMA 
        right = simpleArithmeticExpression
        RIGHT_ROUND_BRACKET
        { $node = factory.newMod($m.getLine(), $m.getCharPositionInLine(), $left.node, $right.node); }
    ;

sqrt returns [Object node]
@init { node = null; }
    : s=SQRT 
        LEFT_ROUND_BRACKET n = simpleArithmeticExpression RIGHT_ROUND_BRACKET
        { $node = factory.newSqrt($s.getLine(), $s.getCharPositionInLine(), $n.node); }
    ;

subquery returns [Object node]
@init { 
    node = null; 
}
    : select  = simpleSelectClause
      from    = subqueryFromClause
      (where   = whereClause)?
      (groupBy = groupByClause)?
      (having  = havingClause)?
        { 
            $node = factory.newSubquery(0, 0, $select.node, $from.node, 
                                       $where.node, $groupBy.node, $having.node); 
        }
    ;

simpleSelectClause returns [Object node]
scope{
    boolean distinct;
}
@init { 
    node = null; 
    $simpleSelectClause::distinct = false;
}
    : s=SELECT (DISTINCT { $simpleSelectClause::distinct = true; })?
      n = simpleSelectExpression
        {
            List exprs = new ArrayList();
            exprs.add($n.node);
            $node = factory.newSelectClause($s.getLine(), $s.getCharPositionInLine(), 
                                           $simpleSelectClause::distinct, exprs);
        }
    ;

simpleSelectExpression returns [Object node]
@init { node = null; }
    : n = singleValuedPathExpression  {$node = $n.node;}
    | n = aggregateExpression  {$node = $n.node;}
    | n = variableAccess  {$node = $n.node;}
    ;


subqueryFromClause returns [Object node]
scope{
    List varDecls;
}
@init { 
    node = null; 
    $subqueryFromClause::varDecls = new ArrayList();
}
    : f=FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] 
        ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )*
        { $node = factory.newFromClause($f.getLine(), $f.getCharPositionInLine(), $subqueryFromClause::varDecls); }
    ;

subselectIdentificationVariableDeclaration [List varDecls]
@init { Object node; }
    : identificationVariableDeclaration[varDecls]
    | n = associationPathExpression (AS)? i=IDENT
        { 
            $varDecls.add(factory.newVariableDecl($i.getLine(), $i.getCharPositionInLine(), 
                                                 $n.node, $i.getText())); 
        }
    | n = collectionMemberDeclaration { $varDecls.add($n.node); }
    ;

orderByClause returns [Object node]
scope{
    List items;
}
@init { 
    node = null; 
    $orderByClause::items = new ArrayList();
}
    : o=ORDER BY
        n = orderByItem  { $orderByClause::items.add($n.node); } 
        (COMMA n = orderByItem  { $orderByClause::items.add($n.node); })*
        { $node = factory.newOrderByClause($o.getLine(), $o.getCharPositionInLine(), $orderByClause::items); }
    ; 

orderByItem returns [Object node]
@init { node = null; }
    : n = stateFieldPathExpression
        ( a=ASC 
            { $node = factory.newAscOrdering($a.getLine(), $a.getCharPositionInLine(), $n.node); }
        | d=DESC
            { $node = factory.newDescOrdering($d.getLine(), $d.getCharPositionInLine(), $n.node); }
        | // empty rule
            { $node = factory.newAscOrdering(0, 0, $n.node); }
        )
    ;

groupByClause returns [Object node]
scope{
    List items;
}
@init { 
    node = null; 
    $groupByClause::items = new ArrayList();
}
    : g=GROUP BY
        n = groupByItem { $groupByClause::items.add($n.node); }
        (COMMA n = groupByItem  { $groupByClause::items.add($n.node); } )*
        { $node = factory.newGroupByClause($g.getLine(), $g.getCharPositionInLine(), $groupByClause::items); }
    ;

groupByItem returns [Object node]
@init { node = null; }
    : n = stateFieldPathExpression {$node = $n.node;}
    | n = variableAccess {$node = $n.node;}
    ;

havingClause returns [Object node]
@init { node = null; }
    : h=HAVING { setAggregatesAllowed(true); } 
        n = conditionalExpression 
        { 
            setAggregatesAllowed(false); 
            $node = factory.newHavingClause($h.getLine(), $h.getCharPositionInLine(), $n.node);
        }
    ;


DOT
    : '.'
    ;

WS  : (' ' | '\t' | '\n' | '\r')+
    { skip(); } ;

LEFT_ROUND_BRACKET
    : '('
    ;

RIGHT_ROUND_BRACKET
    : ')'
    ;

COMMA
    : ','
    ;

IDENT 
    : TEXTCHAR
    ;

fragment
TEXTCHAR
    : ('a'..'z' | 'A'..'Z' | '_' | '$' | 
       c1='\u0080'..'\uFFFE' 
       {
           if (!Character.isJavaIdentifierStart(c1)) {
                throw new InvalidIdentifierStartException(c1, getLine(), getCharPositionInLine());
           }
       }
      )
      ('a'..'z' | '_' | '$' | '0'..'9' | 
       c2='\u0080'..'\uFFFE'
       {
           if (!Character.isJavaIdentifierPart(c2)) {
                throw new InvalidIdentifierStartException(c2, getLine(), getCharPositionInLine());
           }
       }
      )*
    ;


HEX_LITERAL : '0' ('x'|'X') HEX_DIGIT+ ;

INTEGER_LITERAL : ('0' | '1'..'9' '0'..'9'*) ;

LONG_LITERAL : INTEGER_LITERAL INTEGER_SUFFIX;

OCTAL_LITERAL : '0' ('0'..'7')+ ;

// hexadecimal digit 
fragment
HEX_DIGIT
    :   ('0'..'9'|'a'..'f' | 'A'..'F')
    ;

fragment
INTEGER_SUFFIX : ('l'|'L') ;

fragment
NUMERIC_DIGITS
    :   ('0'..'9')+ '.' ('0'..'9')*
    |   '.' ('0'..'9')+
    |   ('0'..'9')+
    ;

DOUBLE_LITERAL
    :   NUMERIC_DIGITS DOUBLE_SUFFIX?
	;

FLOAT_LITERAL
    :   NUMERIC_DIGITS EXPONENT FLOAT_SUFFIX?
    |   NUMERIC_DIGITS FLOAT_SUFFIX
	;

// a couple protected methods to assist in matching floating point numbers
fragment
EXPONENT
    :   ('e' | 'E') ('+'|'-')? ('0'..'9')+
    ;


fragment
FLOAT_SUFFIX 
    :   'f'
    ;

fragment
DOUBLE_SUFFIX
    : 'd'
    ;

EQUALS
    : '='
    ;

GREATER_THAN
    : '>'
    ;

GREATER_THAN_EQUAL_TO
    : '>='
    ;

LESS_THAN
    : '<'
    ;

LESS_THAN_EQUAL_TO
    : '<='
    ;

NOT_EQUAL_TO
    : '<>'
    ;

MULTIPLY
    : '*'
    ;

DIVIDE
    : '/'
    ;

PLUS
    : '+'
    ;

MINUS
    : '-'
    ;


POSITIONAL_PARAM
    : '?' ('1'..'9') ('0'..'9')*
    ;

NAMED_PARAM
    : ':' TEXTCHAR
    ;

// Added Jan 9, 2001 JED
// string literals
STRING_LITERAL_DOUBLE_QUOTED
    : '"' (~ ('"'))* '"'
    ;

STRING_LITERAL_SINGLE_QUOTED
    : '\'' (~ ('\'') | ('\'\''))* '\'' 
    ;

 
