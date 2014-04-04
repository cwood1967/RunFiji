package bleach_Window;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class GaussFunction implements MultivariateDifferentiableVectorFunction {

	@Override
	public double[] value(double[] params) throws IllegalArgumentException {
		double ax = params[0];
		double sx = params[1];
		double ay = params[2];
		double sy = params[4];
		double z0 = params[5];
		
		
		return null;
	}

	@Override
	public DerivativeStructure[] value(DerivativeStructure[] arg0)
			throws MathIllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

}
