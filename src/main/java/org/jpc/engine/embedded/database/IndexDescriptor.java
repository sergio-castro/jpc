package org.jpc.engine.embedded.database;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.jpc.term.Compound;
import org.jpc.term.Term;

/**
 * An index descriptor is modelled by an index function and a list of next Index Descriptors.
 * @author sergioc
 *
 */
public class IndexDescriptor {
	
	/**
	 * @param indexDescriptor an index descriptor of a subterm of the desired term to index.
	 * @param adapterFunction the function obtaining the subterm.
	 * @return a new index descriptor composing the subterm (adapter) function and the index descriptor sent as parameter.
	 */
	static IndexDescriptor indexDescriptorAdapter(IndexDescriptor indexDescriptor, Function<Term, Term> adapterFunction) {
		Function<Term, ?> indexFunction = indexDescriptor.getIndexFunction().compose(adapterFunction);
		Function<Term, List<IndexDescriptor>> indexDescriptorFunction = indexDescriptor.getNextIndexDescriptorsFunction().compose(adapterFunction);
		return new IndexDescriptor(indexFunction, indexDescriptorFunction);
	}
	
	static IndexDescriptor defaultRootIndexDescriptor(final IndexManager indexManager) {
		return new IndexDescriptor(
				new FunctorIndexFunction(), //the index function maps a term to its functor name.
				/**
				 * The next indexes function makes use of the index manager to find the user-defined indexes for a given term.
				 * This function is invoked when instantiating an indexed IndexedClauses associated with the index of a term.
				 */
				term -> {
					if(term instanceof Compound) { //indexes can be defined only for compounds.
						Compound compound = (Compound) term;
						return indexManager.getOrCreateIndexDescriptors(compound.getFunctor()); //functor should be ground (otherwise an exception will be thrown here).
					} else {
						return Collections.<IndexDescriptor>emptyList();
					}
				});
	}
	
	private static Function<Term, Term> termArgumentFunction(int argPos) {
		return term -> term.arg(argPos);
	}
	
	/**
	 * 
	 * @param argPos the term argument position.
	 * @return an IndexDescriptor based on the functor of a term argument in the given position.
	 */
	public static IndexDescriptor forArgumentFunctor(int argPos) {
		return indexDescriptorAdapter(new IndexDescriptor(new FunctorIndexFunction()), termArgumentFunction(argPos));
	}
	
	/**
	 * 
	 * @param argPos the term argument position.
	 * @param indexManager an index manager.
	 * @return an IndexDescriptor based on the defined index of a term argument in the given position.
	 */
	public static IndexDescriptor forIndexedArgument(int argPos, MutableIndexManager indexManager) {
		return indexDescriptorAdapter(defaultRootIndexDescriptor(indexManager), termArgumentFunction(argPos));
	}
	
	/**
	 * 
	 * @param argPos the term argument position.
	 * @return an IndexDescriptor based on the term argument in the given position.
	 */
	public static IndexDescriptor forArgument(int argPos) {
		return new IndexDescriptor(termArgumentFunction(argPos));
	}
	
	public static IndexDescriptor forFunctions(final List<Function<Term, Object>> indexFunctions) {
		UpdatableIndexFunction<Term, Object> indexFunction = new UpdatableIndexFunction<>(indexFunctions.get(0));
		if(indexFunctions.size() == 1) {
			return new IndexDescriptor(indexFunction);
		} else {
			return new IndexDescriptor(indexFunction,
					term -> asList(forFunctions(indexFunctions.subList(1, indexFunctions.size()))));
		}
	}
	
	
	private final UpdatableIndexFunction<Term, ?> indexFunction;
	private final Function<Term, List<IndexDescriptor>> nextIndexDescriptorsFunction;
	
	public IndexDescriptor(Function<Term, ?> indexFunction) {
		this(new UpdatableIndexFunction<>(indexFunction));
	}
	
	public IndexDescriptor(UpdatableIndexFunction<Term, ?> indexFunction) {
		this(indexFunction, term -> emptyList());
	}
	
	public IndexDescriptor(Function<Term, ?> indexFunction, Function<Term, List<IndexDescriptor>> nextIndexDescriptorsFunction) {
		this(new UpdatableIndexFunction<>(indexFunction), nextIndexDescriptorsFunction);
	}
	
	public IndexDescriptor(UpdatableIndexFunction<Term, ?> indexFunction, Function<Term, List<IndexDescriptor>> nextIndexDescriptorsFunction) {
		this.indexFunction = indexFunction;
		this.nextIndexDescriptorsFunction = nextIndexDescriptorsFunction;
	}

	public UpdatableIndexFunction<Term, ?> getIndexFunction() {
		return indexFunction;
	}

	public Function<Term, List<IndexDescriptor>> getNextIndexDescriptorsFunction() {
		return nextIndexDescriptorsFunction;
	}

}
