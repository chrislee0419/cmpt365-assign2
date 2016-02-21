package Sample;

public class Quantization{
    
	public static Matrix quantize(Matrix input) {
		Matrix lqt = createLQT();
	    Matrix q = Matrix.elementwiseDivide(input, lqt);
	    return q;
	}
	
	public static Matrix inv_quantize(Matrix input) {
		Matrix lqt = createLQT();
	    Matrix q = Matrix.elementwiseMultiply(input, lqt);
	    return q;
	}	
	
	// Private
	private static Matrix createLQT() {
		//Luminance Quantization Table
	    double[][] lqt = {{16, 11, 10, 16, 24, 40, 51, 61},
	                {12, 12, 14, 19, 26, 58, 60, 55},
	                {14, 13, 16, 24, 40, 57, 69, 56},
	                {14, 17, 22, 29, 51, 87, 80, 62},
	                {18, 22, 37, 56, 68, 109, 103, 77},
	                {24, 35, 55, 64, 81, 104, 113, 92},
	                {49, 64, 78, 87, 103, 121, 120, 101},
	                {72, 92, 95, 98, 112, 100, 103, 99}};
	    Matrix result = new Matrix(lqt);
	    return result;
	}
   
}