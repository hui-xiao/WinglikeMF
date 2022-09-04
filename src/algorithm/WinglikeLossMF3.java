package algorithm;

import datamodel.RatingSystem;
import datamodel.Triple;

/*
 * Matrix factorization with PQ regulation.
 * 
 * @author Fan Min minfanphd@163.com.
 */
public class WinglikeLossMF3 extends SimpleMatrixFactorization {

	RatingSystem data;
	
	/**
	 * The constantC
	 */
	public static double Gamma = 1.1;
	
	/**
	 ************************ 
	 * The first constructor.
	 * 
	 * @param paraDataset
	 *            The given dataset.
	 * @param paraFilename
	 *            The data filename.
	 * @param paraNumUsers
	 *            The number of users.
	 * @param paraNumItems
	 *            The number of items.
	 * @param paraNumRatings
	 *            The number of ratings.
	 ************************ 
	 */
	public WinglikeLossMF3(RatingSystem paraData) {
		super(paraData);
		data = paraData;
	}// Of the constructor

	/**
	 * 
	 */
	public void update() {
		if (regular) {
			updateRegular();
		} else {
			updateNoRegular();
		} // Of if
	}// Of update
	
	/**
	 ************************ 
	 * Update sub-spaces using the training data. Regularization item is considered.
	 * 
	 ************************ 
	 */
	
	public void updateRegular() {
		//lambda = 0;
		// Step1: update subU
		double tempQij = 0; // The residual
		double tempExp = 0;
		double tempCoefficent = 0;
		//boolean tempSign = true; // Positive
		for (int i = 0; i < trainingSet.length; i++) {
			for (int j = 0; j < trainingSet[i].length; j++) {
				int tempUser = trainingSet[i][j].user;
				int tempItem = trainingSet[i][j].item;
				double tempRate = trainingSet[i][j].rating;
				tempQij = tempRate - predict(tempUser, tempItem);
				if(Math.abs(tempQij) > 2) {
					tempCoefficent =  6 * tempQij / Math.pow(1 + Math.abs(tempQij), 3);
					
				}else if(Math.abs(tempQij) <= 2){
					tempCoefficent = 2.0 / 3 * tempQij;

					
				}//Of if
								
				double tempValue = 0;
				for (int k = 0; k < rank; k++) {
					tempValue = tempCoefficent * itemSubspace[tempItem][k]
							- lambda * userSubspace[tempUser][k];
					userSubspace[tempUser][k] += alpha * tempValue;
				} // Of for k

				// Update item subspace
				for (int k = 0; k < rank; k++) {
					tempValue = tempCoefficent * userSubspace[tempUser][k]
							- lambda * itemSubspace[tempItem][k];
					itemSubspace[tempItem][k] += alpha * tempValue;
				} // Of for k
			} // Of for j
		} // Of for i
	}// Of updateRegular
	
	public void updateNoRegular() {
		//lambda = 0;
		// Step1: update subU
		double tempQij = 0; // The residual
		double tempExp = 0;
		double tempCoefficent = 0;
		//boolean tempSign = true; // Positive
		for (int i = 0; i < trainingSet.length; i++) {
			for (int j = 0; j < trainingSet[i].length; j++) {
				int tempUser = trainingSet[i][j].user;
				int tempItem = trainingSet[i][j].item;
				double tempRate = trainingSet[i][j].rating;
				tempQij = tempRate - predict(tempUser, tempItem);
				if(Math.abs(tempQij) > 2) {
					tempCoefficent =  6 * tempQij / Math.pow(1 + Math.abs(tempQij), 3);
				}else if(Math.abs(tempQij) <= 2){
					tempCoefficent = 2.0 / 3 * tempQij;
				}//Of if
				
				double tempValue = 0;
				for (int k = 0; k < rank; k++) {
					tempValue = tempCoefficent * itemSubspace[tempItem][k];
					userSubspace[tempUser][k] += alpha * tempValue;
				} // Of for k

				// Update item subspace
				for (int k = 0; k < rank; k++) {
					tempValue = tempCoefficent * userSubspace[tempItem][k];
					itemSubspace[tempItem][k] += alpha * tempValue;
				} // Of for k
			} // Of for j
		} // Of for i
	}// Of updateRegular
}// Of class PQRegularMF
