package Sample;

public class Matrix {
	
	// Matrix class used for forming 8 by 8 blocks
	// - in the form of mat[y][x]
	// - y is the vertical component of the block (# of rows)
	// - x is the horizontal component of the block (# of columns)
	// - mat[0][0] represents the top left corner of the block
	
	private double[][] mat;
	
	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	// 8 by 8 matrix
	public Matrix () {
		mat = new double[8][8];
	}
	
	// 2D array to matrix
	public Matrix (double[][] A) {
		if (A[0].length != 8) {
			throw new IllegalArgumentException("Input array is not of size 8 by 8.");
		}
		mat = A;
	}
	
	// copy a matrix
	public Matrix (Matrix M) {
		double[][] mat2 = M.toArray();
		mat = mat2;
	}
	
	///////////////
	// FUNCTIONS //
	///////////////
	
	// returns the Matrix in array form
	public double[][] toArray() {
		return mat;
	}
	
	// CHANGING MATRIX ELEMENTS
	
	// sets position (x,y) = num
	public void setElement(int x, int y, double num) {
		mat[y][x] = num;
	}
	
	// returns the value in position (x,y)
	public double getElement(int x, int y) {
		return mat[y][x];
	}
	
	// sets row y of the matrix with array arr
	public void setRow(int y, double[] arr) {
		mat[y] = arr;
	}
	
	// returns row y in array form
	public double[] getRow(int y) {
		return mat[y];
	}
	
	// sets column x of the matrix with array arr
	public void setColumn(int x, double[] arr) {
		for (int y = 0; y < 8; y++) {
			mat[y][x] = arr[y];
		}
	}
	
	// returns column x in array form
	public double[] getColumn (int x) {
		double[] result = new double[8];
		for (int y = 0; y < 8; y++) {
			result[y] = mat[y][x];
		}
		return result;
	}
	
	// OPERATORS
	
}
