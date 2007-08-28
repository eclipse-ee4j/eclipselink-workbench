package org.eclipse.persistence.internal.jpa.parsing.jpql;

import org.antlr.runtime.*;
import org.eclipse.persistence.internal.jpa.parsing.jpql.antlr.JPQLLexer;

public class CaseInsensitiveJPQLLexer extends JPQLLexer {
		
	public void match(String s) throws MismatchedTokenException {
        int i = 0;
        while ( i<s.length() ) {
        	int currentChar = Character.toLowerCase(input.LA(1));
        	int stringChar = Character.toLowerCase(s.charAt(i));
            if ( currentChar != stringChar ) {
				if ( backtracking>0 ) {
					failed = true;
					return;
				}
				MismatchedTokenException mte =
					new MismatchedTokenException(s.charAt(i), input);
				recover(mte);
				throw mte;
            }
            i++;
            input.consume();
			failed = false;
        }
    }

    public void match(int c) throws MismatchedTokenException {
    	int currentChar = Character.toLowerCase(input.LA(1));
    	int stringChar = Character.toLowerCase(c);
        if ( currentChar != stringChar ) {
			if ( backtracking>0 ) {
				failed = true;
				return;
			}
			MismatchedTokenException mte =
				new MismatchedTokenException(c, input);
			recover(mte);
			throw mte;
        }
        input.consume();
		failed = false;
    }

    public void matchRange(int a, int b)
		throws MismatchedRangeException
	{
    	int currentChar = Character.toLowerCase(input.LA(1));
    	int aChar = Character.toLowerCase(a);
    	int bChar = Character.toLowerCase(b);
        if ( currentChar<aChar || currentChar>bChar ) {
			if ( backtracking>0 ) {
				failed = true;
				return;
			}
            MismatchedRangeException mre =
				new MismatchedRangeException(a,b,input);
			recover(mre);
			throw mre;
        }
        input.consume();
		failed = false;
    }
}