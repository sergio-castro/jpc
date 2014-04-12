package org.jpc.converter.catalog.jrefterm;

import static org.jpc.engine.prolog.ReturnSpecifierConstants.JREF_FLAG_IDENTIFIER;
import static org.jpc.engine.prolog.ReturnSpecifierConstants.JREF_TERM_FLAG_IDENTIFIER;
import static org.jpc.engine.prolog.ReturnSpecifierConstants.SOFT_JREF_FLAG_IDENTIFIER;
import static org.jpc.engine.prolog.ReturnSpecifierConstants.STRONG_JREF_FLAG_IDENTIFIER;
import static org.jpc.engine.prolog.ReturnSpecifierConstants.WEAK_JREF_FLAG_IDENTIFIER;

import java.lang.reflect.Type;

import org.jpc.Jpc;
import org.jpc.JpcException;
import org.jpc.converter.FromTermConverter;
import org.jpc.term.Compound;
import org.jpc.term.jrefterm.JRefTermType;
import org.jpc.term.jrefterm.JRefTermType.Opacity;
import org.minitoolbox.gc.ReferenceType;

public class TermToJRefTermTypeConverter implements FromTermConverter<Compound, JRefTermType> {

	@Override
	public JRefTermType fromTerm(Compound term, Type targetType, Jpc context) {
		ReferenceType javaReferenceType;
		Opacity opacity;
		
		if(term.getNameString().equals(JREF_FLAG_IDENTIFIER) || 
				(term.arg(1) instanceof Compound && ((Compound)term.arg(1)).getNameString().equals(JREF_FLAG_IDENTIFIER)) )
			opacity = Opacity.BLACK_BOX;
		else if(term.arg(1) instanceof Compound && ((Compound)term.arg(1)).getNameString().equals(JREF_TERM_FLAG_IDENTIFIER))
			opacity = Opacity.WHITE_BOX;
		else
			throw new JpcException("Unrecognized reference opacity for term: " + term + ".");
		
		if(term.getNameString().equals(STRONG_JREF_FLAG_IDENTIFIER) || term.getNameString().equals(JREF_FLAG_IDENTIFIER))
			javaReferenceType = ReferenceType.STRONG;
		else if (term.getNameString().equals(SOFT_JREF_FLAG_IDENTIFIER))
			javaReferenceType = ReferenceType.SOFT;
		else if (term.getNameString().equals(WEAK_JREF_FLAG_IDENTIFIER))
			javaReferenceType = ReferenceType.WEAK;
		else
			throw new JpcException("Unrecognized reference type for term: " + term + ".");
		
		return new JRefTermType(javaReferenceType, opacity);
	}

}