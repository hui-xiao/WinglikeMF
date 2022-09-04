package tool;

import java.util.*;
import datamodel.*;

public class MatrixOpr {
	/**
	 * 
	 * @param paraU
	 * @param paraV
	 * @return
	 */
	public static double[][] Matrix_Mult(double[][] paraU, double[][] paraV) {
		//paraU: m*k, paraV:n*k
		double[][] tempResultMatrix = new double[paraU.length][paraV.length];
		if (paraU[0].length == paraV[0].length) {
			for (int i = 0; i < paraU.length; i++) {
				for (int j = 0; j < paraV.length; j++) {
					for (int k = 0; k < paraU[0].length; k++) {
						tempResultMatrix[i][j] += paraU[i][k] * paraV[j][k];
					} // of for k
				} // of for j
			} // of for i
		} // of if

		return tempResultMatrix;
	}// of Matrix_Multiply

	public static double[][] ColVec_Multi_RowVec(double[] paraColVec, double[] paraRowVec) {
		double[][] tempResultMatrix = new double[paraColVec.length][paraRowVec.length];
		for (int i = 0; i < paraColVec.length; i++) {
			for (int j = 0; j < paraRowVec.length; j++) {
				tempResultMatrix[i][j] = paraColVec[i] * paraRowVec[j];
			} // of for j
		} // of for i
		return tempResultMatrix;
	}// of Matrix_Multiply

	public static double Vector_Mult(double[] paraU, double[] paraV) {
		double tempResult = 0;
		if (paraU.length == paraV.length) {
			for (int i = 0; i < paraU.length; i++) {
				tempResult += paraU[i] * paraV[i];
			} // of for i
		} // of if

		return tempResult;
	}// of dotMultiply

	/**
	 * 
	 * @param paraU
	 * @param paraV
	 * @return
	 */
	public static double[][] Matrix_DotMult(double[][] paraU, double[][] paraV) {
		double[][] tempResultMatrix = new double[paraU.length][];
		for (int i = 0; i < paraU.length; i++) {
			tempResultMatrix[i] = new double[paraU[i].length];
		} // Of for i
		if (paraU.length == paraV.length) {
			for (int i = 0; i < paraU.length; i++) {
				for (int j = 0; j < paraU[i].length; j++) {
					tempResultMatrix[i][j] = paraU[i][j] * paraV[i][j];
					if (Math.abs(tempResultMatrix[i][j]) == 0.0 // To check
																// whether
																// tempResultMatrix[i][j]
																// is equal to
																// -0.0
							&& Math.copySign(1.0, tempResultMatrix[i][j]) < 0.0) {
						tempResultMatrix[i][j] = 0.0;
					} // of if
				} // of for j
			} // of for i
		} // of if
		return tempResultMatrix;
	}// of Matrix_DotMult

	public static Triple[][] tripleMatrixMult(Triple[][] paraSampleMatrix, Triple[][] paraU, Triple[][] paraV) {
		Triple[][] tempResultMatrix = new Triple[paraU.length][];
		for(int i = 0; i < paraSampleMatrix.length; i++){
			tempResultMatrix[i] = new Triple[paraSampleMatrix[i].length];
		}//Of for i 
		
		double tempRating = 0;
		for (int i = 0; i < paraSampleMatrix.length; i++) {
			for (int j = 0; j < paraSampleMatrix[i].length; j++) {
				tempRating = 0;
				for (int k = 0; k < paraU[i].length; k++) {
					tempRating += paraU[i][k].rating * paraV[paraSampleMatrix[i][j].item][k].rating;
				} // Of for k

				tempResultMatrix[i][j] = new Triple(i, j, tempRating);
			} // Of for j
		} // Of for i
		return tempResultMatrix;
	}// Of tripleMatrixMult
	
	public static Triple[][] tripleMatrixSub(Triple[][] paraU, Triple[][] paraV) {
		Triple[][] tempResultMatrix = new Triple[paraU.length][];
		for (int i = 0; i < paraU.length; i++) {
			tempResultMatrix[i] =  new Triple[paraU[i].length];
		}//Of for i 
		
		for (int i = 0; i < paraU.length; i++) {
			for (int j = 0; j < paraU[i].length; j++) {
				tempResultMatrix[i][j] = new Triple(i, paraU[i][j].item, paraU[i][j].rating - paraV[i][j].rating);
			} // Of for j
		} // Of for i
		return tempResultMatrix;
	}// Of tripleMatrixSub

	public static Triple[][] Matrix_DotMult(Triple[][] paraU, Triple[][] paraV) {
		Triple[][] tempResultMatrix = new Triple[paraU.length][];

		for (int i = 0; i < paraU.length; i++) {
			tempResultMatrix[i] = new Triple[paraU[i].length];
		} // Of for i

		for (int i = 0; i < tempResultMatrix.length; i++) {
			for (int j = 0; j < tempResultMatrix[i].length; j++) {
				tempResultMatrix[i][j] = new Triple(paraU[i][j].user, paraU[i][j].item, 0);
			} // Of for j
		} // Of for i

		if (paraU.length == paraV.length) {
			for (int i = 0; i < paraU.length; i++) {
				for (int j = 0; j < paraU[i].length; j++) {
//					if(paraU[i].length > 0) {
//					System.out.printf("i %d, j %d\r\n", i,j);
					tempResultMatrix[i][j].rating = paraU[i][j].rating * paraV[i][j].rating;
					if (Math.abs(tempResultMatrix[i][j].rating) == 0.0 // To check whether tempResultMatrix[i][j] is
																		// equal to -0.0
							&& Math.copySign(1.0, tempResultMatrix[i][j].rating) < 0.0) {
						tempResultMatrix[i][j].rating = 0.0;
					} // of if
//					}//Of if
				} // of for j
			} // of for i
		} // of if
		return tempResultMatrix;
	}// of Matrix_DotMult

	public static Triple[][] Matrix_DotMult(Triple[][] paraU, double[] paraVector) {
		Triple[][] tempResultMatrix = new Triple[paraU.length][];

		for (int i = 0; i < paraU.length; i++) {
			tempResultMatrix[i] = new Triple[paraU[i].length];
		} // Of for i

		if (paraU.length == paraVector.length) {
			for (int i = 0; i < paraU.length; i++) {
				for (int j = 0; j < paraU[i].length; j++) {
					tempResultMatrix[i][j].rating = paraU[i][j].rating * paraVector[i];
					if (Math.abs(tempResultMatrix[i][j].rating) == 0.0 // To check
							// whether
							// tempResultMatrix[i][j]
							// is equal to
							// -0.0
							&& Math.copySign(1.0, tempResultMatrix[i][j].rating) < 0.0) {
						tempResultMatrix[i][j].rating = 0.0;
					} // of if
				} // of for j
			} // of for i
		} // of if
		return tempResultMatrix;
	}// of Matrix_DotMult

	public static double[] Vector_DotMult(double[] paraU, double[] paraV) {
		double[] tempResultVector = new double[paraU.length];
		if (paraU.length == paraV.length) {
			for (int i = 0; i < paraU.length; i++) {
				tempResultVector[i] = paraU[i] * paraV[i];
			} // of for i
		} // of if
		return tempResultVector;
	}// of Vector_DotMult

	public static double[][] Matrix_DotDiv(double[][] paraU, double[][] paraV) {
		double[][] tempResultVector = new double[paraU.length][];
		for (int i = 0; i < paraU.length; i++) {
			tempResultVector[i] = new double[paraU[i].length];
		} // Of for i

		for (int i = 0; i < paraU.length; i++) {
			for (int j = 0; j < paraU[i].length; j++) {
				if (paraU[i][j] == 0) {
					tempResultVector[i][j] = 0;
				} else if (paraV[i][j] != 0) {
					tempResultVector[i][j] = paraU[i][j] / paraV[i][j];
				} // of if
			} // of for j
		} // of for i

		return tempResultVector;
	}// of Vector_DotDiv

	public static double[] Vector_DotDiv(double[] paraU, double[] paraV) {
		double[] tempResultVector = new double[paraU.length];
		if (paraU.length == paraV.length) {
			for (int i = 0; i < paraU.length; i++) {
				if (paraV[i] != 0) {
					tempResultVector[i] = paraU[i] / paraV[i];
				} // of if
			} // of for i
		} // of if
		return tempResultVector;
	}// of Vector_DotDiv

	/**
	 * 
	 * @param paraU
	 * @param paraV
	 * @return
	 */
	public static double[][] Matrix_Add(double[][] paraU, double[][] paraV) {
		double[][] tempResultMatrix = new double[paraU.length][paraU[0].length];
		if (paraU.length == paraV.length && paraU[0].length == paraV[0].length) {
			for (int i = 0; i < paraU.length; i++) {
				for (int j = 0; j < paraU[0].length; j++) {
					tempResultMatrix[i][j] = paraU[i][j] + paraV[i][j];
				} // of for j
			} // of for i
		} // Of if
		return tempResultMatrix;
	}// of add

	public static double[][] Matrix_Sub(double[][] paraU, double[][] paraV) {
		double[][] tempResultMatrix = new double[paraU.length][paraV[0].length];
		if (paraU.length == paraV.length && paraU[0].length == paraV[0].length) {
			for (int i = 0; i < paraU.length; i++) {
				for (int j = 0; j < paraU[0].length; j++) {
					tempResultMatrix[i][j] = paraU[i][j] - paraV[i][j];
				} // of for j
			} // of for i
		} // of if
		return tempResultMatrix;
	}// of Matrix_Sub

	public static double[] Vector_Sub(double[] paraU, double[] paraV) {
		double[] tempResultVector = new double[paraU.length];
		if (paraU.length == paraV.length) {
			for (int i = 0; i < paraU.length; i++) {
				tempResultVector[i] = paraU[i] - paraV[i];
			} // of for i
		} // of if

		return tempResultVector;
	}// of Vector_Sub

	public static double[] Vector_Add(double[] paraU, double[] paraV) {
		double[] tempResultVector = new double[paraU.length];
		if (paraU.length == paraV.length) {
			for (int i = 0; i < paraU.length; i++) {
				tempResultVector[i] = paraU[i] + paraV[i];
			} // of for i
		} // of if
		return tempResultVector;
	}// of Vector_Sub

	/*
	 * paraVector is considered as a column vector, and is expanded to a matrix.
	 * Each column of the matrix is paraVector. And the column number of the matrix
	 * is paraColNum.
	 */
	public static double[][] Vector2Matrix(double[] paraVector, int paraColNum) {
		double[][] tempResultMatrix = new double[paraVector.length][paraColNum];
		for (int j = 0; j < paraColNum; j++) {
			for (int i = 0; i < paraVector.length; i++) {
				tempResultMatrix[i][j] = paraVector[i];
			} // of for i
		} // of for j
		return tempResultMatrix;
	}// of Vector2Matrix

	public static double[] SumbyCol(double[][] paraMatrix) {
		double[] tempResult = new double[paraMatrix[0].length];
		for (int j = 0; j < paraMatrix[0].length; j++) {
			for (int i = 0; i < paraMatrix.length; i++) {
				tempResult[j] += paraMatrix[i][j];
			} // of for i
		} // of for j
		return tempResult;
	}// of SumCol

	public static double Matrix_Sum(double[][] paraMatrix) {
		double tempResult = 0;

		for (int i = 0; i < paraMatrix.length; i++) {
			for (int j = 0; j < paraMatrix[0].length; j++) {
				tempResult += paraMatrix[i][j];
			} // of for j
		} // of for i

		return tempResult;
	}// of Matrix_Sum

	public static double[][] Matrix_Transpose(double[][] paraMatrix) {
		double[][] tempResult = new double[paraMatrix[0].length][paraMatrix.length];

		for (int i = 0; i < tempResult.length; i++) {
			for (int j = 0; j < tempResult[0].length; j++) {
				tempResult[i][j] = paraMatrix[j][i];
			} // of for j
		} // of for i

		return tempResult;
	}// of Matrix_Transpose

	public static Triple[][] TripleMatrixTranspose(Triple[][] paraMatrix){
		Triple[][] result = new Triple[paraMatrix[0].length][paraMatrix.length];
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				result[i][j] = new Triple(j, i, paraMatrix[j][i].rating);
			} // Of for j
		} // Of for i
return result;
	}//Of TripleMatrixTranspose
	
	/**
	 * 
	 * @param paraMatrix
	 * @param paraNumOfRowOfTrans
	 * @param paraFlag
	 * @return
	 */
	public static Triple[][] compressedMatrixTranspose(Triple[][] paraMatrix, int paraNumOfRowOfTrans,
			boolean paraFlag) {//
		// paraFlag = true, the rows of original matrix are users.
		// paraFlag = false, the rows of original matrix are items.
		Triple[][] tempResult = new Triple[paraNumOfRowOfTrans][];
		int[] tempNumOfRatingForEachRowOfTrans = new int[paraNumOfRowOfTrans];

		if (paraFlag) {
			// compute the number of ratings for each item
			for (int i = 0; i < paraMatrix.length; i++) {
				for (int j = 0; j < paraMatrix[i].length; j++) {
					tempNumOfRatingForEachRowOfTrans[paraMatrix[i][j].item]++;
//					System.out.printf("tempNumOfRatingForEachCol[%d]:%d\r\n", paraMatrix[i][j].item,tempNumOfRatingForEachCol[paraMatrix[i][j].item]);
				} // Of for j
			} // Of for i

//			System.out.println("tempNumOfRatingForEachCol");
//			SimpleTool.printIntArray(tempNumOfRatingForEachCol);
			for (int i = 0; i < paraNumOfRowOfTrans; i++) {
				tempResult[i] = new Triple[tempNumOfRatingForEachRowOfTrans[i]];
			} // Of for i

			int[] tempRecordIndex = new int[paraNumOfRowOfTrans];
			int tempIndex;
			for (int i = 0; i < paraMatrix.length; i++) {
				for (int j = 0; j < paraMatrix[i].length; j++) {
					tempIndex = tempRecordIndex[paraMatrix[i][j].item];
					tempResult[paraMatrix[i][j].item][tempIndex] = paraMatrix[i][j];
					tempRecordIndex[paraMatrix[i][j].item]++;
				} // of for j
			} // of for i
		} // Of if

		else {
			int cnt = 0;
			for (int i = 0; i < paraMatrix.length; i++) {
				for (int j = 0; j < paraMatrix[i].length; j++) {
//					System.out.printf("cnt %d, i %d, j %d, paraMatrix[i][j].user:%d\r\n",cnt,i,j,paraMatrix[i][j].user);
					tempNumOfRatingForEachRowOfTrans[paraMatrix[i][j].user]++;
					cnt++;
				} // Of for j
			} // Of for i

			for (int i = 0; i < paraNumOfRowOfTrans; i++) {
				tempResult[i] = new Triple[tempNumOfRatingForEachRowOfTrans[i]];
			} // Of for i

			int[] tempRecordIndex = new int[paraNumOfRowOfTrans];
			int tempIndex;
			for (int i = 0; i < paraMatrix.length; i++) {
				for (int j = 0; j < paraMatrix[i].length; j++) {
					tempIndex = tempRecordIndex[paraMatrix[i][j].user];
					tempResult[paraMatrix[i][j].user][tempIndex] = paraMatrix[i][j];
					tempRecordIndex[paraMatrix[i][j].user]++;
				} // of for j
			} // of for i
		} // Of if

		return tempResult;
	}// of compressedMatrixTranspose

	public static double[][] compressedMatrixTranspose(double[][] paraMatrix, int paraNumOfCol) {
		double[][] tempResult = new double[paraNumOfCol][];
		int[] tempRecordIndex = new int[paraNumOfCol];
		int tempIndex;
		for (int i = 0; i < paraMatrix.length; i++) {
			for (int j = 0; j < paraMatrix[i].length; j++) {
				tempIndex = tempRecordIndex[i];
				tempResult[j][tempIndex] = paraMatrix[i][j];
				tempRecordIndex[j]++;
			} // of for j
		} // of for i

		return tempResult;
	}// of Matrix_Transpose

	public static double[][] Add_MatrixandNumber(double[][] paraMatrix, double paraNum) {
		double[][] tempResult = new double[paraMatrix.length][paraMatrix[0].length];

		for (int i = 0; i < tempResult.length; i++) {
			for (int j = 0; j < tempResult[0].length; j++) {
				tempResult[i][j] = paraMatrix[i][j] + paraNum;
			} // of for j
		} // of for i

		return tempResult;
	}// of Add_MatrixandNumber

	public static double[][] Matrix_Subspace(double[][] paraMatrix1, double[][] paraMatrix2) {
		double[][] tempResult;
		double[][] tempMulti;
		// double tempMax = 0;
		// if (paraMatrix1.length == paraMatrix2.length && paraMatrix1[0].length
		// == paraMatrix2[0].length) {

		tempMulti = new double[paraMatrix1[0].length][paraMatrix1[0].length];
		tempResult = new double[tempMulti.length][tempMulti[0].length];

		tempMulti = Matrix_Mult(paraMatrix1, paraMatrix2);

		for (int i = 0; i < tempMulti.length; i++) {
			for (int j = 0; j < tempMulti[0].length; j++) {
				tempMulti[i][j] = Math.acos(Math.abs(tempMulti[i][j]));
			} // of for j
		} // of for i

		tempResult = tempMulti;
		return tempResult;
		// }//of if

		// return tempMulti;
	}// of Matrix_Subspace

	public static double getMedian(double[] arr) {
		double[] tempArr = Arrays.copyOf(arr, arr.length);
		Arrays.sort(tempArr);
		if (tempArr.length % 2 == 0) {
			return (tempArr[tempArr.length >> 1] + tempArr[(tempArr.length >> 1) - 1]) / 2;
		} else {
			return tempArr[(tempArr.length >> 1)];
		}
	}// of getMedian

	public  static double[][] inverseMatrix(double[][] paraMatrix){
		if(paraMatrix.length != paraMatrix[0].length) {
			System.out.println("Error in inverseMatrix");
		}//Of if 
		int N = paraMatrix.length;
		
		double[][] result = new double [N][N];
		double[][] L = new double[N][N];
		double[][] U = new double[N][N];
		double[][] L_n = new double[N][N];
		double[][] U_n = new double[N][N];
		int i, j, k, d;
		double s;
		
		// initialize
		for(i=0;i<N;i++){
			for(j=0;j<N;j++){
				L[i][j] = 0;
				U[i][j] = 0;
				L_n[i][j] = 0;
				U_n[i][j] = 0;
				result[i][j] = 0;
			}
		}
	 
		for(i=0;i<N;i++)  // the diagonal
		{
			L[i][i] = 1.0;
		}
	 
		for(j=0;j<N;j++)  
		{
			U[0][j] = paraMatrix[0][j];
		}
	 
		for(i=1;i<N;i++)
		{
			L[i][0] = paraMatrix[i][0] / U[0][0];
		}
	 
		for(i=1;i<N;i++)
		{
			for(j=i;j<N;j++) // compute U
			{
				s = 0;
				for(k=0;k<i;k++)
				{
					s += L[i][k] * U[k][j];
				}
				U[i][j] = paraMatrix[i][j] - s;
			}
	 
			for(d=i;d<N;d++) // compute L
			{
				s = 0;
				for(k=0;k<i;k++)
				{
					s += L[d][k] * U[k][i];
				}
				L[d][i] = (paraMatrix[d][i] - s) / U[i][i];
			}
		}
	 
		for(j=0;j<N;j++)  //the inverse of L
		{
			for(i=j;i<N;i++)
			{
				if(i==j) 
					L_n[i][j] = 1 / L[i][j];
				else if(i<j) 
					L_n[i][j] = 0;
				else
				{
					s = 0.;
					for(k=j;k<i;k++)
					{
						s += L[i][k] * L_n[k][j];
					}
					L_n[i][j] = -L_n[j][j] * s;
				}
			}
		}
	 
		for(i=0;i<N;i++)  //the inverse of U
		{
			for(j=i;j>=0;j--)
			{
				if(i==j)
					U_n[j][i] = 1 / U[j][i];
				else if(j>i) 
					U_n[j][i] = 0;
				else
				{
					s = 0.;
					for(k=j+1;k<=i;k++)
					{
						s += U[j][k] * U_n[k][i];
					}
					U_n[j][i] = -1 / U[j][j] * s;
				}
			}
		}
	 
	 
		for(i=0;i<N;i++)
		{
			for(j=0;j<N;j++)
			{
				for(k=0;k<N;k++)
				{
					result[i][j] += U_n[i][k] * L_n[k][j];
				}
			}
		}
		return result;
	}//Of inverseMatrix
	
	
	/**
	 * 
	 * @param paraU
	 * @param paraV
	 * @return
	 */
	// public static double[][] Matrix_Col_Mult(double[][] paraU, double[][] paraV,
	// int paraFirstCol, int paraSecond) {
	// for (int i = 0; i < paraU.length; i++) {
	// paraU[i][paraFirstCol]
	// }
	// return null;
	// }
	// }
	/**
	 * 
	 * @param paraMatrix
	 * @param paraV
	 * @return
	 */
	public static double[] Matrix_Mult_ColVector(double[][] paraMatrix, double[] paraColVector) {
		//paraU: m*k, paraV:n*k
		double[] tempResult = new double[paraMatrix.length];
		if (paraMatrix[0].length == paraColVector.length) {
			for (int i = 0; i < paraMatrix.length; i++) {
				for (int k = 0; k < paraMatrix[0].length; k++) {
						tempResult[i] += paraMatrix[i][k] * paraColVector[k];
					} // of for k
				} // of for i
		} // of if

		return tempResult;
	}//Of Matrix_Mult_ColVector
	
	/**
	 * 
	 * @param paraTriple
	 * @param paraNumRow
	 * @param paraNumCol
	 * @return
	 */
	public static double[][] TripleToDouble(Triple paraTriple[][], int paraNumRow, int paraNumCol, boolean paraTranspose){
		double[][] tempResult = new double[paraNumRow][paraNumCol];
//		for(int i = 0; i < paraNumRow; i++) {
//			tempResult[i] = new double[paraTriple[i].length];
//		}
		int tempRow, tempCol;
		for(int i = 0; i < paraTriple.length; i++) {
			for(int j = 0; j < paraTriple[i].length; j++) {
				tempRow = paraTriple[i][j].user;
				tempCol = paraTriple[i][j].item;
				if(paraTranspose) {
					tempRow = paraTriple[i][j].item;
					tempCol = paraTriple[i][j].user;
				}//Of 
				tempResult[tempRow][tempCol] = paraTriple[i][j].rating;
//				tempResult[i][j] = paraTriple[i][j].rating;
			}//Of for j 
		}//Of for i
		return tempResult;
	}//Of tripleConvertToDouble

}// of MatrixOpr
