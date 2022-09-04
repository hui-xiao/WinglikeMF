package algorithm;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

import javax.naming.ldap.SortControl;
import javax.swing.text.Position;

import datamodel.Triple;
import tool.Metrics;
import datamodel.RatingSystem;
//import util.SimpleTool;

/*
 * Matrix factorization for recommender systems. This is the super class of other matrix factorization algorithms.
 * 
 * @author Fan Min minfanphd@163.com.
 */
public class SimpleMatrixFactorization {
	/**
	 * Used to generate random numbers.
	 */
	Random rand = new Random();

	RatingSystem data;

	/**
	 * Define as a constant to save runtime..
	 */
	public static double log2 = Math.log(2);

	/**
	 * Number of users.
	 */
	int numUsers;

	/**
	 * Number of items.
	 */
	int numItems;

	/**
	 * Training data.
	 */
	Triple[][] trainingSet;

	/**
	 * Validation data.
	 */
	Triple[][] validationSet;

	/**
	 * Testing data.
	 */
	Triple[][] testingSet;

	/**
	 * A parameter for controlling learning speed.
	 */
	double alpha;

	/**
	 * A parameter for controlling the learning regular.
	 */
	double lambda;

	/**
	 * The low rank of the small matrices.
	 */
	int rank;

	/**
	 * The user matrix U.
	 */
	double[][] userSubspace;

	/**
	 * The item matrix V.
	 */
	double[][] itemSubspace;

	/**
	 * The lower bound of the rating value.
	 */
	double ratingLowerBound;

	/**
	 * The upper bound of the rating value.
	 */
	double ratingUpperBound;

	/**
	 * The default mean rating.
	 */
	public static final double DEFAULT_MEAN_RATING = 3.5;

	/**
	 * Determine whether the regularization items are needed.
	 */
	boolean regular;

	/**
	 * Determine whether the validation set is required
	 */
	boolean validation;

	RatingSystem wholeD;


	/**
	 ************************ 
	 * The first constructor.
	 * 
	 * @param paraDataset    The given dataset.
	 * @param paraFilename   The data filename.
	 * @param paraNumUsers   The number of users.
	 * @param paraNumItems   The number of items.
	 * @param paraNumRatings The number of ratings.
	 ************************ 
	 */
	public SimpleMatrixFactorization(RatingSystem paraData) {
		data = paraData;
		trainingSet = paraData.getTrainingMatrix();
		validationSet = paraData.getValidationMatrix();
		testingSet = paraData.getTestingMatrix();
		numUsers = paraData.getNumUsers();
		numItems = paraData.getNumItems();
		ratingLowerBound = paraData.getRatingLowerBound();
		ratingUpperBound = paraData.getRatingUpperBound();
		regular = paraData.getRegular();
		System.out.println("regular\t" + regular);
	}// Of the first constructor
//	public SimpleMatrixFactorization(Triple[][] paraTrainingSet, Triple[][] paraValidationSet, 
//			int paraNumUsers, int paraNumItems, double paraRatingLowerBound, 
//			double paraRatingUpperBound, boolean paraRegular) {
//		trainingSet = paraTrainingSet;
//		validationSet = paraValidationSet;
//		numUsers = paraNumUsers;
//		numItems = paraNumItems;
//		ratingLowerBound = paraRatingLowerBound;
//		ratingUpperBound = paraRatingUpperBound;
//		regular = paraRegular;
//	}// Of the first constructor

	/**
	 ************************ 
	 * Set parameters for basic update.
	 * 
	 * @param paraRank The given rank.
	 * @throws IOException
	 ************************ 
	 */
	public void setParameters(int paraRank, double paraAlpha, double paraLambda) {
		rank = paraRank;
		alpha = paraAlpha;
		lambda = paraLambda;
	}// Of setParameters

	/**
	 ************************ 
	 * Set validation.
	 * 
	 * @param paraValidation The given validation.
	 * @throws IOException
	 ************************ 
	 */

	public void setValidation(boolean paraValidation) {
		validation = paraValidation;
	}// Of setRegular

	/**
	 ************************ 
	 * Set regular.
	 * 
	 * @param paraRank The given rank.
	 * @throws IOException
	 ************************ 
	 */

	public void setRegular(boolean paraRegular) {
		regular = paraRegular;
	}// Of setRegular

	/**
	 ************************ 
	 * Initialize subspaces. Each value is in [0, 1].
	 ************************ 
	 */
	void initializeSubspaces() {
		userSubspace = new double[numUsers][rank];

		for (int i = 0; i < numUsers; i++) {
//			System.out.println("user id"+i);
			for (int j = 0; j < rank; j++) {
				userSubspace[i][j] = (rand.nextDouble() - 0.5) / 10;
			} // Of for j
		} // Of for i

		itemSubspace = new double[numItems][rank];
		for (int i = 0; i < numItems; i++) {
			for (int j = 0; j < rank; j++) {
				itemSubspace[i][j] = (rand.nextDouble() - 0.5) / 10;
			} // Of for j
		} // Of for i
	}// Of initializeSubspaces

	/**
	 ************************ 
	 * Predict the rating of the user to the item
	 * 
	 * @param paraUser The user index.
	 ************************ 
	 */
	public double predict(int paraUser, int paraItem) {
		double resultValue = 0;
		for (int i = 0; i < rank; i++) {
			// The row vector of an user and the column vector of an item
			resultValue += userSubspace[paraUser][i] * itemSubspace[paraItem][i];
		} // Of for i

		if (resultValue > ratingUpperBound) {
			resultValue = ratingUpperBound;
		} // Of if
		if (resultValue < ratingLowerBound) {
			resultValue = ratingLowerBound;
		} // Of if
		return resultValue;
	}// Of predict

	/**
	 ************************ 
	 * Predict the rating of the user to the item
	 * 
	 * @param paraUser The user index.
	 ************************ 
	 */
	public Triple[][] predictions(Triple[][] paraDataset) {
		Triple[][] resultPredictions = new Triple[paraDataset.length][];
		for (int i = 0; i < paraDataset.length; i++) {
			resultPredictions[i] = new Triple[paraDataset[i].length];
			for (int j = 0; j < paraDataset[i].length; j++) {
				int tempItem = paraDataset[i][j].item;
				double tempPrediction = predict(i, tempItem);
				double tempRating = paraDataset[i][j].rating;
				resultPredictions[i][j] = new Triple(i, tempItem, tempPrediction);
//				System.out.printf("Train: %d,%d,%f\r\n", i, tempItem, tempRating);
//				System.out.printf("Prediction: %d,%d,%f\r\n", i, tempItem, tempPrediction);
//				System.out.printf("Deviation: %d,%d,%f\r\n", i, tempItem, Math.abs(tempPrediction - tempRating));
			} // of for j
		} // of for i

		return resultPredictions;
	}// Of predict

	/**
	 ************************ 
	 * Train.
	 * 
	 * @param paraMinimalRounds The minimal number of rounds.
	 * @param paraValidation    Use validation set or not.
	 * @return The training rounds.
	 ************************ 
	 */
	public int train(int paraMinimalRounds, boolean paraValidation, int paraScheme,
			int paraWinglike) {
		System.out.println("validation\t" + paraValidation);
		initializeSubspaces();
		int updateRoundInterval = 10;
		double tempCurrentValidationMae = 100;
		double tempLastValidationMae = 100;
		double tempTrainingMae = 0;
		double tempTestingMae = 0;

		int i = 0;
		if (paraScheme == paraWinglike) {
			for (i = 0; i < paraMinimalRounds; i++) {
				update();
			} // Of for i
		} // Of if
		
		
//		for (i = 0; i < paraMinimalRounds; i++) {
		if (paraValidation) {
			// Terminate when the performance on the validation set gets worse.
			for (;; i++) {
				update();
				if (i % updateRoundInterval == 0) {
					// Use validation set to terminate
					Triple[][] tempPredictions = predictions(validationSet);
//					System.out.println("train validationSet 0:" + validationSet[1][0].rating);
					tempCurrentValidationMae = Metrics.mae(tempPredictions, validationSet);
					tempPredictions = predictions(trainingSet);
					tempTrainingMae = Metrics.mae(tempPredictions, trainingSet);

					tempPredictions = predictions(testingSet);
					tempTestingMae = Metrics.mae(tempPredictions, testingSet);
					// Show the process
//						if (i % 10 == 0) {
					System.out.println("Round " + i);
					System.out.println("Training MAE = " + tempTrainingMae);
					System.out.println("Validation MAE = " + tempCurrentValidationMae);
					System.out.println("Testing MAE = " + tempTestingMae);
					if (paraScheme == paraWinglike) {
						if ((i > paraMinimalRounds / 2 && tempCurrentValidationMae > tempLastValidationMae)) {
							break;
						} // Of if
					}else { // Of if
						if (tempCurrentValidationMae > tempLastValidationMae) {
							break;
						} // Of if
					}//Of if 
					tempLastValidationMae = tempCurrentValidationMae;
//						}//Of if
				} // Of if
			} // Of for i
		} else {
			double tempCurrentTrainingMae = 100;
			double tempLastTrainingMae = 100;
//			 double tempDifference = 0;
			// Terminate if converge
			for (;; i++) {
//				System.out.println("Round " + i);
				update();
				if (paraMinimalRounds > 100) {
					updateRoundInterval = 1;
				}
				if (i % updateRoundInterval == 0) {
					Triple[][] tempPredictions = predictions(trainingSet);
					tempCurrentTrainingMae = Metrics.mae(tempPredictions, trainingSet);

					// Show the process
					System.out.println("Round " + i);
					System.out.println("Training MAE = " + mae());
					tempLastTrainingMae = tempCurrentTrainingMae;
				} // Of if
			} // Of for i
		} // Of if paraValidation
//		} // Of for i
		return i;
	}// Of train

	/**
	 * 
	 * @return
	 */
	public double mae() {
		return mae(trainingSet);
	}// Of mae

	/**
	 * 
	 * @param paraDataSet
	 * @return
	 */
	public double mae(Triple[][] paraDataSet) {
		double resultMae = 0;
		int tempCnt = 0;
		for (int i = 0; i < paraDataSet.length; i++) {
			for (int j = 0; j < paraDataSet[i].length; j++) {
				double tempRating = paraDataSet[i][j].rating;
				double tempPrediction = predict(i, paraDataSet[i][j].item);
				resultMae += Math.abs(tempRating - tempPrediction);
				tempCnt++;
			} // Of for j
		} // Of for i
		resultMae /= tempCnt;
		return resultMae;
	}// Of mae

	/**
	 ************************ 
	 * Update sub-spaces using the training data. No regular term is considered.
	 * This method should be overwritten in subclasses.
	 ************************ 
	 */
	public void update() {
		int tempUserId, tempItemId;
		double tempRating, tempResidual, tempValue;
		for (int i = 0; i < numUsers; i++) {
			for (int j = 0; j < trainingSet[i].length; j++) {
				tempUserId = trainingSet[i][j].user;
				tempItemId = trainingSet[i][j].item;
				tempRating = trainingSet[i][j].rating;

				tempResidual = tempRating - predict(tempUserId, tempItemId); // Residual

				// Update user subspace
				tempValue = 0;
				for (int k = 0; k < rank; k++) {
					tempValue = 2 * tempResidual * itemSubspace[tempItemId][k];
					userSubspace[tempUserId][k] += alpha * tempValue;
				} // Of for j

				// Update item subspace
				for (int k = 0; k < rank; k++) {
					tempValue = 2 * tempResidual * userSubspace[tempUserId][k];
					itemSubspace[tempItemId][k] += alpha * tempValue;
				} // Of for k
			} // Of for j
		} // Of for i
	}// Of update

	/**
	 ************************ 
	 * Compute \sum|x_ij|.
	 * 
	 * @param paraMatrix The given matrix.
	 ************************ 
	 */
	public static double absoluteValueSum(double[][] paraMatrix) {
		double result = 0;
		for (int i = 0; i < paraMatrix.length; i++) {
			for (int j = 0; j < paraMatrix[i].length; j++) {
				if (paraMatrix[i][j] >= 0) {
					result += paraMatrix[i][j];
				} else {
					result -= paraMatrix[i][j];
				} // Of if
			} // Of for j
		} // Of for i
		return result;
	}// Of absoluteValueSum

	/**
	 ************************ 
	 * Compute \sum(x_ij)^2. Frobenius is abbreviated as fro.
	 * 
	 * @param paraMatrix The given matrix.
	 ************************ 
	 */
	public static double froNormSquare(double[][] paraMatrix) {
		double result = 0;
		for (int i = 0; i < paraMatrix.length; i++) {
			for (int j = 0; j < paraMatrix[i].length; j++) {
				result += paraMatrix[i][j] * paraMatrix[i][j];
			} // Of for j
		} // Of for i
		return result;
	}// Of froNormSquare

	/**
	 ************************ 
	 * Compute \sum(x_ij)2.
	 * 
	 * @param paraMatrix The given matrix.
	 ************************ 
	 */
	public static double froNorm(double[][] paraMatrix) {
		return Math.sqrt(froNormSquare(paraMatrix));
	}// Of froNorm

	/**
	 ************************ 
	 * Get the average value of user subspace (absolute value).
	 ************************ 
	 */
	public double getAverageU() {
		double tempTotal = absoluteValueSum(userSubspace);
		double result = tempTotal / userSubspace.length / userSubspace[0].length;
		return result;
	}// Of getAverageU

	/**
	 ************************ 
	 * Get the average value of item subspace (absolute value).
	 ************************ 
	 */
	public double getAverageV() {
		double tempTotal = absoluteValueSum(itemSubspace);
		double result = tempTotal / itemSubspace.length / itemSubspace[0].length;
		return result;
	}// Of getAverageV

	/**
	 ************************ 
	 * Get the average squared user subspace values.
	 ************************ 
	 */
	public double getAverageSquareU() {
		double tempTotal = froNormSquare(userSubspace);
		double result = tempTotal / userSubspace.length / userSubspace[0].length;
		return result;
	}// Of getAverageSquareU

	/**
	 ************************ 
	 * Get the average squared item subspace values.
	 ************************ 
	 */
	public double getAverageSquareV() {
		double tempTotal = froNormSquare(itemSubspace);
		double result = tempTotal / itemSubspace.length / itemSubspace[0].length;
		return result;
	}// Of getAverageSquareV

	/**
	 ************************ 
	 * From a triple matrix to a triple array.
	 * 
	 * @param paraDataset The given dataset.
	 * @return An array of triples.
	 ************************ 
	 */
	public Triple[] tripleMatrixToArray(Triple[][] paraDataset) {
		// Step 1. Copy data to an array.
		int tempLength = 0;
		for (int i = 0; i < paraDataset.length; i++) {
			tempLength += paraDataset[i].length;
		} // Of for i

		Triple[] resultDataArray = new Triple[tempLength];
		int tempIndex = 0;
		for (int i = 0; i < paraDataset.length; i++) {
			for (int j = 0; j < paraDataset[i].length; j++) {
				resultDataArray[tempIndex] = paraDataset[i][j];
				tempIndex++;
			} // Of for i
		} // Of for i

		return resultDataArray;
	}// Of tripleMatrixToArray

	/**
	 * 
	 * @param paraTrainingMatrix
	 * @param paraTestingMatrix
	 * @param paraNumItems
	 * @param paraK
	 * @return
	 */
	public int[] computeRankList(int paraUser, int paraNumItems, int paraK, int paraGroundTruthItem) {
		double[] tempPredictions = new double[paraNumItems];
		int countLarger = 0;
		int[] rankList = new int[paraK];
		// Step 1: compute all predictions

		for (int j = 0; j < paraNumItems; j++) {
			tempPredictions[j] = predict(paraUser, j);
		} // Of for j

		// Step 2: compute the number of items whose predicted rating is larger than the
		// groundTruthItem.

		for (int k = 0; k < paraNumItems; k++) {
			if (tempPredictions[k] > tempPredictions[paraGroundTruthItem]) {
				countLarger++;
			} // Of if
		} // Of for k

		if (countLarger > paraK) {
			return null;// The groundTruthItem does not rank in top100.
		} // Of if

		rankList = findTopK(tempPredictions, paraK);

		return rankList;
	}// Of rankList

	/**
	 * 
	 * @param paraVector
	 * @param paraTopK
	 * @return
	 */
	public int[] findTopK(double[] paraVector, int paraTopK) {
		int[] result;
		int[] index;
		double temp;
		int tempId;
		int tempTopK = paraTopK;

		if (paraTopK > paraVector.length) {
//			System.out.println("Error: paraTopK > paraVector.length");
			tempTopK = paraVector.length;
		} // Of if

		result = new int[tempTopK];
		index = new int[paraVector.length];
		for (int i = 0; i < index.length; i++) {
			index[i] = i;
		} // Of for i

		for (int i = 0; i < tempTopK; i++) {
			for (int j = 0; j <= paraVector.length - i - 2; j++) {
				if (paraVector[j] > paraVector[j + 1]) {
					temp = paraVector[j];
					paraVector[j] = paraVector[j + 1];
					paraVector[j + 1] = temp;

					tempId = index[j];
					index[j] = index[j + 1];
					index[j + 1] = tempId;
				} // Of if
			} // Of for j
		} // Of for i

		for (int i = 0; i < tempTopK; i++) {
			result[i] = index[index.length - 1 - i];
		} // Of for i

		return result;

	}// Of findTopK

	/**
	 * 
	 * @param paraPreditions
	 * @param paraTopK
	 * @return the indices of items which have the highest predicted rating.
	 */
	public int[] findTopKForEachUser(double[] paraPreditions, boolean[] paraKnownItems, int paraTopK) {
		int[] result;
		int[] index;

		int tempTopK = paraTopK;
		double[] tempVector;

		// count the number of items not in training and validation set.
		int tempCnt = 0;
		for (int i = 0; i < paraKnownItems.length; i++) {
			if (paraKnownItems[i]) {
				continue;
			} // Of if
			tempCnt++;
		} // Of for i
//		System.out.printf("the number of items not in training set:%d\r\n", tempCnt);
		if (tempCnt < paraTopK) {
			tempTopK = tempCnt;
		} // Of if
//		System.out.printf("real topK:%d\r\n", tempTopK);
		result = new int[tempTopK];
		index = new int[tempCnt];
		tempVector = new double[tempCnt];

		int tempInd = 0;
		for (int i = 0; i < paraKnownItems.length; i++) {
			if (paraKnownItems[i]) {
				continue;
			} // Of if
			index[tempInd] = i;
			tempVector[tempInd] = paraPreditions[i];
			tempInd++;
		} // Of for i

		for (int i = 0; i < tempTopK; i++) {
			for (int j = 0; j <= tempVector.length - i - 2; j++) {
				if (tempVector[j] > tempVector[j + 1]) {
					double temp = tempVector[j];
					tempVector[j] = tempVector[j + 1];
					tempVector[j + 1] = temp;

					int tempId = index[j];
					index[j] = index[j + 1];
					index[j + 1] = tempId;
				} // Of if
			} // Of for j
		} // Of for i

//		System.out.println(Arrays.toString(tempVector));

//		System.out.printf("index.length:%d\r\n", index.length);
		for (int i = 0; i < tempTopK; i++) {
			result[i] = index[index.length - 1 - i];
//			System.out.printf("index.length - 1 - i:%d\r\n", index.length - 1 - i);
//			System.out.printf("i:%d, result[i]:%d, index[index.length - 1 - i]:%d\r\n", i, result[i],
//					index[index.length - 1 - i]);
		} // Of for i
//		System.out.println(Arrays.toString(result));
		return result;
	}// Of findTopK

	public int[][] computeRankList(Triple[][] paraTestingMatrix, int paraK, boolean paraValidation) {
		int[][] resultRankList = new int[numUsers][];
		boolean[] tempKnownItems = new boolean[numItems];
		double[] tempPredictions = new double[numItems];
		for (int i = 0; i < numUsers; i++) {
			for (int j = 0; j < numItems; j++) {
				tempKnownItems[j] = false;
			} // Of for j

			for (int j = 0; j < trainingSet[i].length; j++) {
				tempKnownItems[trainingSet[i][j].item] = true;
			} // Of for j

			if (paraValidation) {
				for (int j = 0; j < validationSet[i].length; j++) {
					tempKnownItems[validationSet[i][j].item] = true;
				} // Of for j
			} // Of if

			for (int j = 0; j < numItems; j++) {
				if (tempKnownItems[j]) {
					continue;
				} // Of if
				tempPredictions[j] = predict(i, j);
			} // Of for j

			resultRankList[i] = findTopKForEachUser(tempPredictions, tempKnownItems, paraK);
		} // Of for i
		return resultRankList;
	}// Of computeRankList

	/**
	 * 
	 * @param paraVector
	 * @param paraTopK
	 * @return
	 */
	public int[] findTopK(double[] paraVector, int[] paraIndices, int paraTopK) {
		int[] result = new int[paraTopK];
		int numOfTestingItems = paraVector.length;
		double temp;
		int tempId;
		int topK = paraTopK;

		if (paraTopK > numOfTestingItems) {
			System.out.println("Error: paraTopK > numOfTestingItems");
			topK = numOfTestingItems;
		} // Of if
			// find topK items
		for (int i = 0; i < topK; i++) {
			for (int j = 0; j <= paraVector.length - i - 2; j++) {
				if (paraVector[j] > paraVector[j + 1]) {
					temp = paraVector[j];
					paraVector[j] = paraVector[j + 1];
					paraVector[j + 1] = temp;

					tempId = paraIndices[j];
					paraIndices[j] = paraIndices[j + 1];
					paraIndices[j + 1] = tempId;
				} // Of if
			} // Of for j
		} // Of for i
		for (int i = 0; i < topK; i++) {
			result[i] = paraIndices[numOfTestingItems - 1 - i];
		} // Of for i
		return result;

	}// Of findTopK

	/**
	 * 
	 */
	public void evaluation() {
		Triple[][] tempTestingPred = predictions(data.getTestingMatrix());
		System.out.println("MAE\t" + Metrics.mae(tempTestingPred, data.getTestingMatrix()));
	}// Of evaluation
}// Of class SimpleMatrixFactorization
