package org.terasology.codecity.world.structure.metric;

import org.terasology.codecity.world.structure.CodeClass;

/**
 * Gets number of comments in class (using ast).
 * @author paula
 *
 */
public class NumberOfCommentsMetric extends CodeMetric {

	@Override
	public String getMetricVal(CodeClass code) {
		return Integer.toString(code.getAst().getComments().size());
	}

}
