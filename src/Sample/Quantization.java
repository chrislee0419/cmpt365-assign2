package Sample;

public class Quantization{

	// Luminance Quantization Tables
	private static float[][] lqt_recommended_luminance = {
			{16, 11, 10, 16, 24,  40,  51,  61},
			{12, 12, 14, 19, 26,  58,  60,  55},
			{14, 13, 16, 24, 40,  57,  69,  56},
			{14, 17, 22, 29, 51,  87,  80,  62},
			{18, 22, 37, 56, 68,  109, 103, 77},
			{24, 35, 55, 64, 81,  104, 113, 92},
			{49, 64, 78, 87, 103, 121, 120, 101},
			{72, 92, 95, 98, 112, 100, 103, 99}};
	
	private static float[][] lqt_recommended_chrominance = {
			{17, 18, 24, 47, 99, 99, 99, 99},
			{18, 21, 26, 66, 99, 99, 99, 99},
			{24, 26, 56, 99, 99, 99, 99, 99},
			{47, 66, 99, 99, 99, 99, 99, 99},
			{99, 99, 99, 99, 99, 99, 99, 99},
			{99, 99, 99, 99, 99, 99, 99, 99},
			{99, 99, 99, 99, 99, 99, 99, 99},
			{99, 99, 99, 99, 99, 99, 99, 99}};
	
	private static float[][] lqt_high_quality = {
			{1, 1, 1, 1, 1, 2, 3, 3},
			{1, 1, 1, 1, 1, 3, 3, 3},
			{1, 1, 1, 1, 2, 3, 3, 3},
			{1, 1, 1, 1, 3, 4, 4, 3},
			{1, 1, 2, 3, 3, 5, 5, 4},
			{1, 2, 3, 3, 4, 5, 6, 5},
			{2, 3, 4, 4, 5, 6, 6, 5},
			{4, 5, 5, 5, 6, 5, 5, 5}};
	
	private static float[][] lqt_low_quality = {
			{40, 50, 60, 70, 80, 90, 100, 100},
			{50, 60, 70, 80, 90, 100, 100, 100},
			{60, 70, 80, 90, 100, 100, 100, 100},
			{70, 80, 90, 100, 100, 100, 100, 125},
			{80, 90, 100, 100, 100, 100, 125, 150},
			{90, 100, 100, 100, 100, 125, 150, 175},
			{100, 100, 100, 100, 125, 150, 175, 200},
			{100, 100, 100, 125, 150, 175, 200, 200}};
	
	private static float[][] lqt_default = {
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1}};
	
	// Quantize Functions
    
	public static Matrix quantize(Matrix input, int table) {
		Matrix lqt = createLQT(table);
	    Matrix q = Matrix.elementwiseDivide(input, lqt);
	    return q;
	}
	
	public static Matrix inv_quantize(Matrix input, int table) {
		Matrix lqt = createLQT(table);
	    Matrix q = Matrix.elementwiseMultiply(input, lqt);
	    return q;
	}	
	
	// Private
	private static Matrix createLQT(int table) {
	    float[][] lqt;
	    switch (table) {
	    	case 1:
	    		lqt = lqt_recommended_luminance;
	    		break;
	    	case 2:
		    	lqt = lqt_recommended_chrominance;
		    	break;
	    	case 3:
	    		lqt = lqt_high_quality;
	    		break;
	    	case 4:
	    		lqt = lqt_low_quality;
	    		break;
	    	default:
		    	lqt = lqt_default;
	    		
	    }
	    Matrix result = new Matrix(lqt);
	    return result;
	}
   
}