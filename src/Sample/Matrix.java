package Sample;
import java.lang.Math;

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
	public Matrix() {
		mat = new double[8][8];
	}
	
	// 2D array to matrix
	public Matrix(double[][] A) {
		if (A.length != 8 || A[0].length != 8) {
			throw new IllegalArgumentException("Input array is not of size 8 by 8.");
		}
		mat = A;
	}
	
	// copy a matrix
	public Matrix(Matrix M) {
		double[][] mat2 = M.to2DArray();
		mat = mat2;
	}
	
	///////////////
	// FUNCTIONS //
	///////////////
	
	// CONVERSION
	
	// returns the matrix in 2D array form
	public double[][] matrix1Dto2DArray() {
		return mat;
	}
	
	// returns the matrix in 1D array form
	public double[] matrix1Dto1DArray() {
		double[] result = new double[64];
		int x, y;
		for (int i = 0; i < 64; i++) {
			x = i % 8;
			y = i / 8;
			result[i] = mat[y][x];
		}
		
		return result;
	}
	
	// 1D array to 2D matrix
	public static Matrix[][] array1DTo2DMatrix(double[] arr, int width, int height) {
		int w = (int)Math.ceil(width / 8);
		int h = (int)Math.ceil(height / 8);
		int x_overhang = (w * 8) - width;
		int y_overhang = (h * 8) - height;
		int j_max, i_max, index;
		Matrix[][] result = new Matrix[h][w];
		for (int y = 0; y < h; y++) {
			if (y == h-1) {
				j_max = 8 - y_overhang;
			}
			else {
				j_max = 8;
			}
			for (int j = 0; j < j_max; j++) {
				for (int x = 0; x < w; x++) {
					if (x == w-1) {
						i_max = 8 - x_overhang;
					}
					else {
						i_max = 8;
					}
					for (int i = 0; i < i_max; i++) {
						index = j*8 + i;
						result[y][x].setElement(i, j, arr[index]);
					}
				}
			}
		}
		return result;
	}
	
	// returns an array with the elements of a square 2D matrix
	public static double[] matrix2DTo1DArray(Matrix[][] A) {
		int length = A[0].length;
		double[] result = new double[64 * length * length];
		int index;
		for (int y = 0; y < length; y++) {
			for (int x = 0; x < length; x++) {
				for (int j = 0; j < 8; j++) {
					for (int i = 0; i < 8; i++) {
						index = y*64*length + 64*x + j*8 + i;
						result[index] = A[y][x].getElement(i, j);
					}
				}
			}
		}
		return result;
	}
	
	
	// PRINTING
	
	// print function
	public void print() {
		for (int y = 0; y < 8; y++) {
			System.out.print("[ ");
			for (int x = 0; x < 8; x++) {
				if (x == 7) {
					System.out.println(mat[y][x] + " ]");
				}
				else {
					System.out.print(mat[y][x] + ", ");
				}
			}
		}
	}
	
	// static print function
	public static void print(Matrix A) {
		for (int y = 0; y < 8; y++) {
			System.out.print("[ ");
			for (int x = 0; x < 8; x++) {
				if (x == 7) {
					System.out.println(A.getElement(x,y) + " ]");
				}
				else {
					System.out.print(A.getElement(x,y) + ", ");
				}
			}
		}
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
	public double[] getColumn(int x) {
		double[] result = new double[8];
		for (int y = 0; y < 8; y++) {
			result[y] = mat[y][x];
		}
		return result;
	}
	
	///////////////
	// OPERATORS //
	///////////////
	
	// ADDITION
	
	// static form of addition that returns a new matrix
	public static Matrix add(Matrix A, Matrix B) {
		double[][] arr = new double[8][8];
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				arr[y][x] = A.getElement(x, y) + B.getElement(x, y);
			}
		}
		Matrix result = new Matrix(arr);
		return result;
	}
	
	// addition on a matrix
	public void add(Matrix A) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				mat[y][x] += A.getElement(x, y);
			}
		}
	}
	
	// SUBTRACTION
	
	// static form of subtraction that returns a new matrix
	public static Matrix subtract(Matrix A, Matrix B) {
		double[][] arr = new double[8][8];
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				arr[y][x] = A.getElement(x, y) - B.getElement(x, y);
			}
		}
		Matrix result = new Matrix(arr);
		return result;
	}
	
	// subtraction on a matrix
	public void subtract(Matrix A) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				mat[y][x] -= A.getElement(x, y);
			}
		}
	}
	
	// MULTIPLICATION
	
	// static form of multiplication that returns a new matrix
	public static Matrix multiply(Matrix A, Matrix B) {
		double[][] arr = new double[8][8];
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				arr[y][x] = A.getElement(x, y) * B.getElement(y, x);
			}
		}
		Matrix result = new Matrix(arr);
		return result;
	}
	
	// multiplication on a matrix
	public void multiply(Matrix A) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				mat[y][x] *= A.getElement(y, x);
			}
		}
	}
	
	// static form of element-wise multiplication that returns a new matrix
	public static Matrix elementwiseMultiply(Matrix A, Matrix B) {
		double[][] arr = new double[8][8];
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				arr[y][x] = A.getElement(x, y) * B.getElement(x, y);
			}
		}
		Matrix result = new Matrix(arr);
		return result;
	}

	// element-wise multiplication on a matrix
	public void elementwiseMultiply(Matrix A) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				mat[y][x] *= A.getElement(x, y);
			}
		}
	}
	
	// element-wise scalar multiplication
	public void scalarMultiply(double scalar) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				mat[y][x] *= scalar;
			}
		}
	}
	
	// DIVISION
	
	// static form of element-wise division that returns a new matrix
		public static Matrix elementwiseDivide(Matrix A, Matrix B) {
			double[][] arr = new double[8][8];
			for (int y = 0; y < 8; y++) {
				for (int x = 0; x < 8; x++) {
					arr[y][x] = Math.floor(A.getElement(x, y) / B.getElement(x, y));
				}
			}
			Matrix result = new Matrix(arr);
			return result;
		}
	
	// element-wise scalar integer division
		public void elementwiseDivide(Matrix A) {
			for (int y = 0; y < 8; y++) {
				for (int x = 0; x < 8; x++) {
					mat[y][x] = Math.floor(mat[y][x] / A.getElement(x, y));
				}
			}
		}
	
	// element-wise scalar integer division
	public void scalarDivide(double scalar) {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				mat[y][x] = Math.floor(mat[y][x]/scalar);
			}
		}
	}
	
	// TRANSPOSE
	
	// static form of matrix transposition, returns a new matrix
	public static Matrix transpose(Matrix A) {
		Matrix result = new Matrix();
		for (int i = 0; i < 8; i++) {
			result.setColumn(i, A.getRow(i));
		}
		return result;
	}
	
	// transpose an existing matrix
	public void transpose() {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				this.swap(x,y,y,x);
			}
		}
	}
	
	///////////////////////
	// PRIVATE FUNCTIONS //
	///////////////////////
	
	// swaps the element at (x, y) with (x2, y2)
	private void swap(int x, int y, int x2, int y2) {
		if (x != x2 || y != y2) {
			double temp = mat[y2][x2];
			mat[y2][x2] = mat[y][x];
			mat[y][x] = temp;
		}
	}
	
}
