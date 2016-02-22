package Sample;
import java.lang.Math;

public class Transform {

	private static float sq2 = 1/(float)Math.sqrt(2);
	
	public static Matrix inv_dctransform(Matrix input) {
		Matrix dct = createDCT();
		Matrix result = createInverseDCT();
	    result.multiply(input);
	    result.multiply(dct);
	    return result;
	}
	
	public static Matrix dctransform(Matrix input) {
		Matrix result = createDCT();
		Matrix idct = createInverseDCT();
		result.multiply(input);
		Matrix.print(result);
	    result.multiply(idct);
		Matrix.print(result);
	    return result;
	}
	
	// Private
	private static Matrix createDCT() {
		float[][] dct = new float[8][8];
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if (i == 0)
					dct[i][j] = sq2;
				else
					dct[i][j] = 0.5f * (float)Math.cos(((2*j+1)*i*Math.PI)/16);
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