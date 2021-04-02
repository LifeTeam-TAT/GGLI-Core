package org.ace.insurance.util.prefix;

import java.util.List;

import org.ace.insurance.common.interfaces.IEntity;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;

/**
 * The utility class having the generic behaviors facilitated for all entity
 * objects.
 * 
 * @author Ace Plus
 * @version 1.0.0
 * @Date 2013/07/02
 */
public class PrefixMutator {
	private ICustomIDGenerator idGenerator;

	// ------------------------------ Constructors
	// ------------------------------

	public PrefixMutator(ICustomIDGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	// ------------------------------ Overriden and Utility Methods
	// ------------------------------

	public void setIdPrefix(IEntity entity, Class type) {
		setIdPrefix(idGenerator, entity, type);
	}

	public void setIdPrefix(PrefixMutant mutant) {
		setIdPrefix(idGenerator, mutant);
	}

	public void setIdPrefix(List<PrefixMutant> mutants) {
		setIdPrefix(idGenerator, mutants);
	}

	public void setIdPrefix(PrefixMutation mutation) {
		setIdPrefix(idGenerator, mutation);
	}

	// ------------------------------ Static Methods
	// ------------------------------

	public static void setIdPrefix(ICustomIDGenerator idGen, IEntity entity, Class type) {
		PrefixMutant mutant = new PrefixMutant(entity, type);
		setIdPrefix(idGen, mutant);
	}

	public static void setIdPrefix(ICustomIDGenerator idGen, PrefixMutant mutant) {
		if (idGen != null && mutant != null) {
			Class type = mutant.getType();
			IEntity entity = mutant.getEntity();

			String prefix = idGen.getPrefix(type, true);
			entity.setPrefix(prefix);
		}
	}

	public static void setIdPrefix(ICustomIDGenerator idGen, List<PrefixMutant> mutants) {
		if (mutants != null) {
			for (PrefixMutant mutant : mutants) {
				setIdPrefix(idGen, mutant);
			}
		}
	}

	public static void setIdPrefix(ICustomIDGenerator idGen, PrefixMutation mutation) {
		if (mutation != null) {
			List<PrefixMutant> mutants = mutation.getMutantList();
			if (mutants != null) {
				setIdPrefix(idGen, mutants);
			}
		}
	}
}
