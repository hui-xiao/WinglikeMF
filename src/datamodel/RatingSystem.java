package datamodel;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Random;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Title: RatingSystem.java
 * 
 * @author Yuan-Yuan Xu, Fan Min www.fansmale.com Email: minfan@swpu.edu.cn
 *         Created: Nov 6, 2019
 * @date 2021/08/04
 */
public class RatingSystem {
	/**
	 * Used to generate random numbers.
	 */
	Random rand = new Random();

	/**
	 * The number of users.
	 */
	int numUsers;

	/**
	 * The number of items.
	 */
	int numItems;

	/**
	 * Total number of ratings in the rating matrix.
	 */
	int numRatings;

	/**
	 * The whole data.
	 */
	Triple[][] ratingMatrix;

	/**
	 * The matrix of training set, users as the rows.
	 */
	public Triple[][] trainingMatrix;

	/**
	 * The matrix of validation set, users as the rows.
	 */
	public Triple[][] validationMatrix;

	/**
	 * The matrix of testing set, users as the rows.
	 */
	public Triple[][] testingMatrix;

	/**
	 * The mean/average value of rating for the training set. It is equal to
	 * sum(trainVector)/trainVector.length
	 */
	public double meanRatingOfTrain;

	/**
	 * The split sign.
	 */
	public static final String SPLIT_SIGN = new String(",");

	/**
	 * The startingUserID
	 */
	public int startingUserID;
	
	/**
	 * The startingItemID
	 */
	public int startingItemID;
	
	/**
	 * The rank
	 */
	public int rank;
	
	/**
	 * The value of missing rating
	 */
	public static final double DEFAULT_MISSING_RATING = 0;
	
	public static final double JESTER_DEFAULT_MISSING_RATING = 99;

	public String fileName = "";
	
	public String trainFileName = "";
	
	public String testFileName = "";
	
	public String validationFileName = "";
	/**
	 * The lower bound of the dataset.
	 */
	double ratingLowerBound;
	
	/**
	 * The upper bound of the dataset.
	 */
	double ratingUpperBound;
	
	/**
	 * whether to regularize
	 */
	public boolean regular; 
	
	/**
	 * Determine whether the validation set is required
	 */
	boolean validation;
	
	/**
	 ********************** 
	 * Read the dataset
	 * 
	 * @param paraFilename The file storing the data.
	 * @param paraNumUsers Number of users. This might be obtained through scanning
	 *                     the whole data. However, the approach requires an
	 *                     additional scanning. Therefore we do not adopt it.
	 * @param paraNumItems Number of items.
	 * @throws IOException It may occur if the given file does not exist.
	 ********************** 
	 */
//	public RatingSystem(String paraFilename, int paraNumUsers, int paraNumItems, int paraNumRatings,
//			int paraStartingUserID, int paraStartingItemID) {
	public RatingSystem(String paraPropertyFilename, String paraRegularAndValidationFileName) throws IOException {
		// Step 1. Accept basic settings.
		Properties settings1 = new Properties();
		InputStream tempInputStream1 = new BufferedInputStream(new FileInputStream(paraPropertyFilename));
		settings1.load(tempInputStream1);
		fileName = new String(settings1.getProperty("ratingData"));
		trainFileName = new String(fileName + "-train"); 
		testFileName = new String(fileName + "-test");
		validationFileName = new String(fileName + "-validation");
		numUsers = Integer.parseInt(settings1.getProperty("numUsers"));
		numItems = Integer.parseInt(settings1.getProperty("numItems"));
		numRatings = Integer.parseInt(settings1.getProperty("numRatings"));
		startingUserID = Integer.parseInt(settings1.getProperty("startingUserID"));
		startingItemID = Integer.parseInt(settings1.getProperty("startingItemID"));
		rank = Short.parseShort(settings1.getProperty("rank"));
		ratingLowerBound = Double.parseDouble(settings1.getProperty("ratingLowerBound"));
		ratingUpperBound = Double.parseDouble(settings1.getProperty("ratingUpperBound"));
//		SPLIT_SIGN = new String(settings1.getProperty("splitString"));
		Properties settings2 = new Properties();
		InputStream tempInputStream2 = new BufferedInputStream(new FileInputStream(paraRegularAndValidationFileName));
		settings2.load(tempInputStream2);
		regular = Boolean.parseBoolean(settings2.getProperty("regular"));
		validation = Boolean.parseBoolean(settings2.getProperty("validation"));
		// Step 2. Allocate space.
		ratingMatrix = new Triple[numUsers][];
		trainingMatrix = new Triple[numUsers][];
		testingMatrix = new Triple[numUsers][];
		validationMatrix = new Triple[numUsers][];
		meanRatingOfTrain = 0;

		int tempUserIndex, tempItemIndex;
		double tempRating;
		int[] tempUserRatings = new int[numUsers];

//		readJester(fileName);
		try {
			// Step 3. First scan to determine the number of ratings for each
			// user.
			File tempFile = new File(fileName);
			BufferedReader tempBufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
			String tempLine;
			String[] tempParts;
			for (int i = 0; i < numRatings; i++) {
				tempLine = tempBufferReader.readLine();
				tempParts = tempLine.split(SPLIT_SIGN);
				tempUserIndex = Integer.parseInt(tempParts[0]) - startingUserID;// user id
				tempUserRatings[tempUserIndex]++;
			} // Of while

			// Step 4. Allocate space for ratingMatrix.
			for (int i = 0; i < numUsers; i++) {
				ratingMatrix[i] = new Triple[tempUserRatings[i]];
			} // Of for i

			// Step 5. Second scan to store data.
			tempBufferReader.close();
			tempBufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
			int[] tempIndexForUsers = new int[numUsers];

			for (int i = 0; i < numRatings; i++) {
				tempLine = tempBufferReader.readLine();
				tempParts = tempLine.split(SPLIT_SIGN);
				tempUserIndex = Integer.parseInt(tempParts[0]) - startingUserID;// user id
				tempItemIndex = Integer.parseInt(tempParts[1]) - startingItemID;// item id
				tempRating = Double.parseDouble(tempParts[2]);// rating

				ratingMatrix[tempUserIndex][tempIndexForUsers[tempUserIndex]] = new Triple(tempUserIndex, tempItemIndex,
						tempRating);
				tempIndexForUsers[tempUserIndex]++;
			} // Of for i
			tempBufferReader.close();
		} catch (Exception ee) {
			System.out.println("Errors occurred while reading " + fileName);
			System.out.println(ee);
			System.exit(0);
		} // Of try
	}// Of RatingSystem

	/**
	 ********************** 
	 * Split the data to obtain the training, validation and testing sets,
	 * respectively.
	 * 
	 * @param paraTrainingProportion   the proportion of the training set.
	 * @param paraValidationProportion the proportion of the validation set.
	 * @throws IOException
	 * @throws o
	 ********************** 
	 */
	public void splitTrainValidationTest(double paraTrainingProportion, double paraValidationProportion)
			throws IOException {
		// Step 1. Scan to determine which ones belong to training/testing sets.
		// No adjust is undertaken for simplicity.
		int[] tempTrainingCountArray = new int[numUsers];
		int[] tempValidationCountArray = new int[numUsers];
		int[] tempTestingCountArray = new int[numUsers];

		boolean[][] tempIndexTrainingMatrix = new boolean[ratingMatrix.length][];
		boolean[][] tempIndexValidationMatrix = new boolean[ratingMatrix.length][];
		boolean[][] tempIndexTestingMatrix = new boolean[ratingMatrix.length][];

		double tempRandom;
		for (int i = 0; i < ratingMatrix.length; i++) {
			tempIndexTrainingMatrix[i] = new boolean[ratingMatrix[i].length];
			tempIndexValidationMatrix[i] = new boolean[ratingMatrix[i].length];
			tempIndexTestingMatrix[i] = new boolean[ratingMatrix[i].length];

			// The training matrix must have at least one rating for each user.
			int tempIndex = rand.nextInt(ratingMatrix[i].length);
			tempIndexTrainingMatrix[i][tempIndex] = true;
			tempTrainingCountArray[i]++;
			for (int j = 0; j < ratingMatrix[i].length; j++) {
				// Reserved for the training set.
				if (j == tempIndex) {
					continue;
				} // Of if

				tempRandom = rand.nextDouble();
				if (tempRandom < paraTrainingProportion) {
					tempIndexTrainingMatrix[i][j] = true;
					tempTrainingCountArray[i]++;
				} else if (tempRandom < paraTrainingProportion + paraValidationProportion) {
					tempIndexValidationMatrix[i][j] = true;
					tempValidationCountArray[i]++;
				} else {
					tempIndexTestingMatrix[i][j] = true;
					tempTestingCountArray[i]++;
				} // Of if
			} // Of for j
		} // Of for i

		// Step 2. Allocate space.
		trainingMatrix = new Triple[numUsers][];
		validationMatrix = new Triple[numUsers][];
		testingMatrix = new Triple[numUsers][];

		for (int i = 0; i < ratingMatrix.length; i++) {
			trainingMatrix[i] = new Triple[tempTrainingCountArray[i]];
			validationMatrix[i] = new Triple[tempValidationCountArray[i]];
			testingMatrix[i] = new Triple[tempTestingCountArray[i]];
//			indexTrainingMatrix[i] = new boolean[tempTrainingCountArray[i]];
//			indexTestingMatrix[i] = new boolean[tempTestingCountArray[i]];
			int tempCounter1 = 0;
			int tempCounter2 = 0;
			int tempCounter3 = 0;
			for (int j = 0; j < ratingMatrix[i].length; j++) {
//				System.out.printf("i:%d, j:%d\r\n", i,j);
				if (tempIndexTrainingMatrix[i][j]) {
					if (trainingMatrix[i].length > 0) {
						trainingMatrix[i][tempCounter1] = new Triple(ratingMatrix[i][j]);
						tempCounter1++;
					} // Of if
				} else if (tempIndexValidationMatrix[i][j]) {
					if (validationMatrix[i].length > 0) {
						validationMatrix[i][tempCounter2] = new Triple(ratingMatrix[i][j]);
						tempCounter2++;
					} // Of if
				} else if(tempIndexTestingMatrix[i][j]) {
//					System.out.println("indexTestingMatrix" + indexTestingMatrix[i][j]);
					if (testingMatrix[i].length > 0) {
						testingMatrix[i][tempCounter3] = new Triple(ratingMatrix[i][j]);
						tempCounter3++;
					} // Of i
				} // Of if
			} // Of for j
		} // Of for i

		meanRatingOfTrain = computeAverageRating(trainingMatrix);

		File fileTrain = new File(fileName + "-train");
		FileWriter outTrain = new FileWriter(fileTrain);
		for (int i = 0; i < trainingMatrix.length; i++) {
			for (int j = 0; j < trainingMatrix[i].length; j++) {
				outTrain.write(i + "," + trainingMatrix[i][j].item + "," + trainingMatrix[i][j].rating + "\r\n");
			} // Of for j
		} // Of for i
		outTrain.close();

		File fileTest = new File(fileName + "-test");
		FileWriter outTest = new FileWriter(fileTest);
		for (int i = 0; i < testingMatrix.length; i++) {
			for (int j = 0; j < testingMatrix[i].length; j++) {
				outTest.write(i + "," + testingMatrix[i][j].item + "," + testingMatrix[i][j].rating + "\r\n");
			} // Of for j
		} // Of for i
		outTest.close();

		File fileValidation = new File(fileName + "-validation");
		FileWriter outValidation = new FileWriter(fileValidation);
		for (int i = 0; i < validationMatrix.length; i++) {
			for (int j = 0; j < validationMatrix[i].length; j++) {
				outValidation
						.write(i + "," + validationMatrix[i][j].item + "," + validationMatrix[i][j].rating + "\r\n");
			} // Of for j
		} // Of for i
		outValidation.close();
	}// Of splitTrainValidationTest

	/**
	 * 
	 * @param paraFile
	 * @return
	 * @throws IOException
	 */
	public Triple[][] readData(String paraFile) throws IOException {
		Triple[][] result = new Triple[numUsers][];
		File tempFile = new File(paraFile);
		BufferedReader tempBufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
		String tempLine;
		String[] tempParts;
		int tempUserIndex, tempItemIndex;
		double tempRating;
		int[] tempUserRatings = new int[numUsers];
		int tempNumLines = 0;
		while ((tempLine = tempBufferReader.readLine()) != null) {
			tempNumLines++;
//			tempLine = tempBufferReader.readLine();
			tempParts = tempLine.split(SPLIT_SIGN);
			tempUserIndex = Integer.parseInt(tempParts[0]);// user id
			tempItemIndex = Integer.parseInt(tempParts[1]);// item id
			tempRating = Double.parseDouble(tempParts[2]);// rating
			tempUserRatings[tempUserIndex]++;
		} // Of for i
		tempBufferReader.close();

		for (int i = 0; i < numUsers; i++) {
			result[i] = new Triple[tempUserRatings[i]];
		} // Of for i

		File tempFile2 = new File(paraFile);
		BufferedReader tempBufferReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile2)));
		int[] tempIndexForUsers = new int[numUsers];
		for (int i = 0; i < tempNumLines; i++) {
			tempLine = tempBufferReader2.readLine();
			tempParts = tempLine.split(SPLIT_SIGN);
			tempUserIndex = Integer.parseInt(tempParts[0]);// user id
			tempItemIndex = Integer.parseInt(tempParts[1]);// item id
			tempRating = Double.parseDouble(tempParts[2]);// rating
			result[tempUserIndex][tempIndexForUsers[tempUserIndex]] = new Triple(tempUserIndex, tempItemIndex,
					tempRating);
			tempIndexForUsers[tempUserIndex]++;
//			System.out.printf("user: %d, item: %d, rating: %f\r\n", tempUserIndex, tempItemIndex,
//					tempRating);
		} // Of for i
		tempBufferReader2.close();
		return result;
	}// Of readData

	/**
	 * 
	 * @param paraFile
	 * @return
	 * @throws IOException
	 */
	public Triple[][] readJester(String paraFile) throws IOException {
		Triple[][] result = new Triple[numUsers][];
		File tempFile = new File(paraFile);
		BufferedReader tempBufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile)));
		String tempLine;
		String[] tempParts = new String[numItems + 1];
		int tempUserIndex, tempItemIndex = 0;
		double tempRating = JESTER_DEFAULT_MISSING_RATING;
		int[] tempUserRatings = new int[numUsers];
		int tempNumLines = 0;
		int tempNumRatings = 0;
		while ((tempLine = tempBufferReader.readLine()) != null) {
			tempNumLines++;
			tempParts = tempLine.split(SPLIT_SIGN);
			tempUserIndex =  tempNumLines - 1;
			for(int tempPart = 1; tempPart <= numItems; tempPart++) {
				tempRating = Double.parseDouble(tempParts[tempPart]);
				if(tempRating != JESTER_DEFAULT_MISSING_RATING) {
					tempItemIndex = tempPart - 1;
					tempUserRatings[tempUserIndex]++;
					tempNumRatings++;
				}//Of if 
			}//Of for tempPart
		} // Of for i
		tempBufferReader.close();
		System.out.printf("The number of ratings in Jester:%d\r\n", tempNumRatings);
		
		for (int i = 0; i < numUsers; i++) {
			result[i] = new Triple[tempUserRatings[i]];
		} // Of for i

		File tempFile2 = new File(paraFile);
		BufferedReader tempBufferReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile2)));
		int[] tempIndexForUsers = new int[numUsers];
		for (int i = 0; i < tempNumLines; i++) {
			tempLine = tempBufferReader2.readLine();
			tempParts = tempLine.split(SPLIT_SIGN);
			tempUserIndex =  i;
			for(int tempPart = 1; tempPart <= numItems; tempPart++) {
				tempRating = Double.parseDouble(tempParts[tempPart]);
				if(tempRating != JESTER_DEFAULT_MISSING_RATING) {
					tempItemIndex = tempPart - 1;
					result[tempUserIndex][tempIndexForUsers[tempUserIndex]] = new Triple(tempUserIndex, tempItemIndex,
							mappingJesterToFiveStarRatings(tempRating));
					tempIndexForUsers[tempUserIndex]++;
//					System.out.printf("user: %d, item: %d, rating: %f\r\n", tempUserIndex, tempItemIndex,
//							tempRating);
				}//Of if 
			}//Of for tempPart	
		} // Of for i
		tempBufferReader2.close();
		
		File fileTemp = new File(fileName + "-temp");
		FileWriter outTemp = new FileWriter(fileTemp);
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[i].length; j++) {
				outTemp.write(i + "," + result[i][j].item + "," + result[i][j].rating + "\r\n");
			} // Of for j
		} // Of for i
		outTemp.close();
		return result;
	}// Of readJester
	
	/**
	 * 
	 * @param paraOriginalRating
	 * @return
	 */
	public double mappingJesterToFiveStarRatings(double paraOriginalRating) {
//		double result = 0;
//		result = (paraOriginalRating + 10) / 4;
//		return result;		
		double result = 0;
		result = (paraOriginalRating + 10) / 5 + 1;
		return result;		
	}//Of mappingJesterToFiveStarRatings
	
	public void readData() throws IOException {
//		System.out.println("test : validation = " +testFileName+":"+validationFileName);
		
		trainingMatrix = readData(trainFileName);
		testingMatrix = readData(testFileName);
		validationMatrix = readData(validationFileName);
		meanRatingOfTrain = computeAverageRating(trainingMatrix);
	}//Of readData
	
	/**
	 ********************** 
	 * Compute the average rating of the data.
	 ********************** 
	 */
	public double computeAverageRating(Triple[][] paraMatrix) {
		double tempSum = 0;
		int tempCounts = 0;
		for (int i = 0; i < paraMatrix.length; i++) {
			for (int j = 0; j < paraMatrix[i].length; j++) {
				tempSum += paraMatrix[i][j].rating;
				tempCounts++;
			} // Of for j
		} // Of for i

		return tempSum / tempCounts;
	}// Of computeAverageRating

	/**
	 ********************** 
	 * Centralize. Each rating subtracts the mean value. In this way the average
	 * value would be 0.
	 * 
	 * @param paraMatrix The given matrix.
	 ********************** 
	 */
	public void centralize(Triple[][] paraMatrix) {
		for (int i = 0; i < paraMatrix.length; i++) {
			for (int j = 0; j < paraMatrix[i].length; j++) {
				paraMatrix[i][j].rating -= meanRatingOfTrain;
			} // Of for j
		} // Of for i
	}// Of centralize

	/**
	 ********************** 
	 * Centralize. Each rating adds the mean value. In this way, the predicted
	 * rating is in [1,5].
	 * 
	 * @param paraMatrix The given matrix.
	 ********************** 
	 */
	public Triple[][] deCentralize(Triple[][] paraMatrix) {
		Triple[][] result = new Triple[paraMatrix.length][];
		for (int i = 0; i < paraMatrix.length; i++) {
			result[i] = new Triple[paraMatrix[i].length];
		} // Of for i
		for (int i = 0; i < paraMatrix.length; i++) {
			for (int j = 0; j < paraMatrix[i].length; j++) {
				result[i][j] = new Triple(i, paraMatrix[i][j].item, paraMatrix[i][j].rating + meanRatingOfTrain);
			} // Of for j
		} // Of for i
		return result;
	}// Of deCentralize

	/**
	 ********************** 
	 * Centralize all data.
	 ********************** 
	 */
//	public void centralizeAll() {
//		centralize(ratingMatrix);
//	}// Of centralizeAll

	public void centralizeAll() {
		centralize(trainingMatrix);
		centralize(testingMatrix);
		centralize(validationMatrix);
		ratingLowerBound -= meanRatingOfTrain;
		ratingUpperBound -= meanRatingOfTrain;
	}//Of centralizeAll
	/**
	 ********************** 
	 * Centralize training matrix.
	 ********************** 
	 */
	public void centralizeTrainingMatrix() {
		centralize(trainingMatrix);
	}// Of centralizeTrainingMatrix

	
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public Triple[][] getRatingMatrix() {
		return ratingMatrix;
	}// Of getRatingMatrix

	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public Triple[][] getTrainingMatrix() {
		return trainingMatrix;
	}// Of getTrainingMatrix

	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public Triple[][] getValidationMatrix() {
		return validationMatrix;
	}// Of getValidationMatrix

	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public Triple[][] getTestingMatrix() {
		return testingMatrix;
	}// Of getTestingMatrix

	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public double getMeanRatingOfTrain() {
		return meanRatingOfTrain;
	}// Of getMeanRatingOfTrain
 
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public int getNumUsers() {
		return numUsers;
	}//Of getNumUsers
	
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public int getNumItems() {
		return numItems;
	}//Of getNuItems
	
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public int getNumRatings() {
		return numRatings;
	}//Of getNumRatings
	
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public int getRank() {
		return rank;
	}//Of getRank
	
	
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public double getRatingLowerBound() {
		return ratingLowerBound;
	}//Of getRatingLowerBound
	
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public double getRatingUpperBound() {
		return ratingUpperBound;
	}//Of getRatingUpperBound
	
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public boolean getRegular() {
		return regular;
	}//Of getRatingUpperBound
	
	/**
	 ********************** 
	 * Getter
	 ********************** 
	 */
	public boolean getValiadation() {
		return validation;
	}
}// Of class RatingSystem
