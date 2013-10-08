package org.jpc.converter.catalog.map;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map.Entry;

import org.jpc.Jpc;
import org.jpc.converter.JpcConversionException;
import org.jpc.converter.JpcConverter;
import org.jpc.converter.typesolver.MapTypeSolver;
import org.jpc.term.Compound;
import org.jpc.term.Term;
import org.minitoolbox.reflection.typewrapper.TypeWrapper;

public abstract class MapEntryConverter<K,V> extends JpcConverter<Entry<K,V>, Compound> {

	protected String entrySeparator;

	public MapEntryConverter() {
		this(MapTypeSolver.DEFAULT_MAP_ENTRY_SEPARATOR);
	}
	
	public MapEntryConverter(String entrySeparator) {
		this.entrySeparator = entrySeparator;
	}
	
	@Override
	public Entry<K,V> fromTerm(Compound term, Type type, Jpc context) {
		if(!term.hasFunctor(entrySeparator, 2)) //verify the term structure
			throw new JpcConversionException();
		TypeWrapper typeWrapper = TypeWrapper.wrap(type);
		if(!(typeWrapper.getRawClass().equals(Entry.class) || typeWrapper.getRawClass().equals(AbstractMap.SimpleEntry.class))) //verify the type
			throw new JpcConversionException();
		
		Term keyTerm = term.arg(1);
		Term valueTerm = term.arg(2);
		Type[] entryTypes = TypeWrapper.wrap(type).as(Entry.class).getActualTypeArgumentsOrUpperBounds();
		Type keyType = entryTypes[0];
		Type valueType = entryTypes[1];
		Object key = context.fromTerm(keyTerm, keyType);
		Object value = context.fromTerm(valueTerm, valueType);
		return new AbstractMap.SimpleEntry(key, value);
	}
	
	@Override
	public <T extends Compound> T toTerm(Entry<K,V> entry, Class<T> termClass, Jpc context) {
		Term key = context.toTerm(entry.getKey());
		Term value = context.toTerm(entry.getValue());
		Compound term = new Compound(entrySeparator, asList(key, value));
		return (T) term;
	}
	
	
	
	
	public static class MapEntryToTermConverter<K,V> extends MapEntryConverter<K,V> {
		
		public MapEntryToTermConverter() {
			super();
		}
		
		public MapEntryToTermConverter(String entrySeparator) {
			super(entrySeparator);
		}
		
		@Override
		public Entry<K,V> fromTerm(Compound term, Type type, Jpc context) {
			throw new UnsupportedOperationException();
		}

	}
	
	
	public static class TermToMapEntryConverter<K,V> extends MapEntryConverter<K,V> {
		
		public TermToMapEntryConverter() {
			super();
		}
		
		public TermToMapEntryConverter(String entrySeparator) {
			super(entrySeparator);
		}

		@Override
		public <T extends Compound> T toTerm(Entry<K,V> entry, Class<T> termClass, Jpc context) {
			throw new UnsupportedOperationException();
		}
		
	}

}