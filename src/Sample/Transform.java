package Sample;
import java.lang.Math;

public class Transform {
    private double sq2 = 1/Math.sqrt(2);
	
	private double[][] dct;
	
	for (int i = 0; i < 8; i++){
		for (int j = 0; j < 8; j++){
			if (i = 0)
				dct[i][j] = sq2;
			else
				dct[i][j] = 0.5 * Math.cos(((2*j+1)*i*Math.PI)/16);
		}
	}
	
	private double[][] dcttrans = transpose(dct);
	
	public static Matrix inv_dctransform(Matrix input) {
	    Matrix imp = multiply(dcttrans, input);
	    imp = multiply(imp, dct);
	    return imp;
	}
	
	public static Matrix dctransform(Matrix input) {
	    Matrix imp = multiply(dct, input);
	    imp = multiply(imp, dcttrans);
	    return imp;
	}	
    
}