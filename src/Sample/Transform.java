package Sample;
import java.lang.Math;

public class Transform {
	
	public static Matrix inv_dctransform(Matrix input) {
		dct
	    Matrix imp = multiply(dcttrans, input);
	    imp = multiply(imp, dct);
	    return imp;
	}
	
	public static Matrix dctransform(Matrix input) {
	    Matrix imp = multiply(dct, input);
	    imp = multiply(imp, dcttrans);
	    return imp;
	}
	
	// Private
	private static Matrix createDCT() {
		double[][] dct;
		double sq2 = 1/Math.sqrt(2);
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (i == 0)
					dct[i][j] = sq2;
				else
					dct[i][j] = 0.5 * Math.cos(((2*j+1)*i*Math.PI)/16);
			}
		}
		return new Matrix(dct);
	}
	
	private static Matrix createInverseDCT() {
		Matrix idct = createDCT();
		idct.transpose();
		return idct;
		
	}
    
}