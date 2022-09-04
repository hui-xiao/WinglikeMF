package experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import algorithm.*;
import datamodel.*;


import tool.MatrixOpr;
import tool.Metrics;

/**
 * Manage the whole experiment process.
 * 
 * @author Fan Min minfanphd@163.com
 */
public class WinglikeMF{
	/**
	 * Used to generate random numbers.
	 */
	Random rand = new Random();

	int numUsers;

	int numItems;

	int numRatings;

	int rank;

	/**
	 * The Triple format of training matrix.
	 */
	Triple[][] trainingSet;

	/**
	 * The Triple format of testing matrix.
	 */
	Triple[][] testingSet;

	/**
	 * The Triple format of validation matrix.
	 */
	Triple[][] validationSet;

	/**
	 * The transpose of Triple training matrix.
	 */
	Triple[][] transposeTrainingMatrix;

	/**
	 * The transpose of Triple testing matrix.
	 */
	Triple[][] transposeTestingMatrix;

	 
	/**
	 * The rating system.
	 */
	RatingSystem wholeD;

	/**
	 * The constantC
	 */
	public static double constantC = 1.5;

	/**
	 * The default value of alpha
	 */
	public static double DEFAULT_ALPHA = 0.001;

	/**
	 * The regularization coefficient
	 */
	double alpha = DEFAULT_ALPHA;

	/**
	 * The threshold of Huber loss.
	 */
	public static double DEFAULT_DELTA = 2;

	double delta = DEFAULT_DELTA;

	/**
	 * whether to regularize
	 */
	boolean regular;
	
	 
	/**
	 * The sub-matrices in Sigmoid-like loss.
	 */
	double[][] userSubspace;
	double[][] itemSubspace;

	/**
	 * The lowerbound
	 */
	double ratingLowerBound;

	/**
	 * The upperbound
	 */
	double ratingUpperBound;

	/**
	 * Determine whether the validation set is required
	 */
	boolean validation;

	/**
	 * If the rating matrix has fewer rows than columns, it needs to be transposed.
	 */
	boolean transpose;

	/**
	 * The default value of alpha
	 */
	public static double DEFAULT_LAMBDA = 0.005;

	/**
	 * The learning rate
	 */
	double lambda = DEFAULT_LAMBDA;

	/**
	 * The number of schemes.
	 */
	public static final int NUM_SCHEMES = 1;
		
	/**
	 * The scheme number of the Wing-like loss.
	 */
	public static final int WING_LIKE_LOSS_MF_3 = 0;
	
	/**
	 * The top-k recommendation.
	 */
	public static final int TOP_K = 10;

	/**
	 * 
	 * @param paraData
	 * @param paraNumUsers
	 * @param paraNumItems
	 * @param paraNumRatings
	 * @throws IOException
	 */
	public WinglikeMF(RatingSystem paraData) throws IOException { 
		wholeD = paraData;
	}// Of SVDExperiment

	/**
	 * 
	 */
	public void initialize() {
		numUsers = wholeD.getNumUsers();
		numItems = wholeD.getNumItems();
		numRatings = wholeD.getNumRatings();
		rank = wholeD.getRank();
		regular = wholeD.getRegular();

		trainingSet = wholeD.getTrainingMatrix();
		testingSet = wholeD.getTestingMatrix();
		validationSet = wholeD.getValidationMatrix();
		ratingLowerBound = wholeD.getRatingLowerBound();
		ratingUpperBound = wholeD.getRatingUpperBound();
		transposeTrainingMatrix = tool.MatrixOpr.compressedMatrixTranspose(trainingSet, numItems, true);
		transposeTestingMatrix = tool.MatrixOpr.compressedMatrixTranspose(testingSet, numItems, true);

		validation = wholeD.getValiadation();
		userSubspace = new double[numUsers][rank];
		itemSubspace = new double[numItems][rank];
//		initializeSubspaces();
		System.out.println("regular\t" + regular);
		System.out.println("validation\t" + validation);
	}// Of initialize

	/**
	 * 
	 * @param paraRank
	 */
	public void initializeSubspaces() {
		for (int i = 0; i < numUsers; i++) {
			for (int j = 0; j < rank; j++) {
				userSubspace[i][j] = (rand.nextDouble() - 0.5) / 10;
				 
			} // Of for j
		} // Of for i
		for (int i = 0; i < numItems; i++) {
			for (int j = 0; j < rank; j++) {
				itemSubspace[i][j] = (rand.nextDouble() - 0.5) / 10;
			} // Of for j
		} // Of for i
	}// Of initializeSubspaces

	
	

	/**
	 * 
	 * @param paraData
	 * @param paraMinimalRounds
	 * @param paraNumExperiments
	 * @throws IOException 
	 */
	public static void schemeComparison(RatingSystem paraData, int paraMinimalRounds, int paraNumExperiments) throws IOException {
		double[][] tempTrainMaeMatrix = new double[NUM_SCHEMES][paraNumExperiments];
		double[][] tempTrainRoundsMatrix = new double[NUM_SCHEMES][paraNumExperiments];
		double[][] tempTestMaeMatrix = new double[NUM_SCHEMES][paraNumExperiments];
		double[][] tempTrainRmseMatrix = new double[NUM_SCHEMES][paraNumExperiments];
		double[][] tempTestRmseMatrix = new double[NUM_SCHEMES][paraNumExperiments];
		double[][] tempTestHrMatrix = new double[NUM_SCHEMES][paraNumExperiments];
		double[][] tempTestMapMatrix = new double[NUM_SCHEMES][paraNumExperiments];
		double[][] tempTestNdcgMatrix = new double[NUM_SCHEMES][paraNumExperiments];

		double[] tempTrainAverageMaeArray = new double[NUM_SCHEMES];
		double[] tempTrainAverageRoundsArray = new double[NUM_SCHEMES];
		double[] tempTrainAverageRmseArray = new double[NUM_SCHEMES];
		double[] tempTestAverageMaeArray = new double[NUM_SCHEMES];
		double[] tempTestAverageRmseArray = new double[NUM_SCHEMES];

		double[] tempTestAverageHrArray = new double[NUM_SCHEMES];
		double[] tempTestAverageMapArray = new double[NUM_SCHEMES];
		double[] tempTestAverageNdcgArray = new double[NUM_SCHEMES];
		Triple[][] tempTestingMatrix = paraData.getTestingMatrix();
		SimpleMatrixFactorization tempMF = null;

		long startTime = 0;
		for (int j = 0; j < paraNumExperiments; j++) {
//			for (int i = L1_LOSS_MF; i < MSLE_LOSS_MF + 1; i++) {
				for (int i = L1_LOSS_MF; i < WING_LIKE_LOSS_MF_3 + 1; i++) {
				startTime = System.currentTimeMillis();
				System.out.println("\r\nThe scheme\t" + getNameOfScheme(i) );
				 
				switch (i) {
				case WING_LIKE_LOSS_MF_3:
					tempMF = new WinglikeLossMF3(paraData);
					break;
				default:
					System.out.println("Unsupported algorithm type: " + i);
					System.exit(0);
				}// Of switch

				// Step 2. set the values of the rank, alpha and lambda.
//				tempMF.setParameters(5, 1e-3, 5e-4);
//				tempMF.setParameters(5, 0.01, 0.05);//the parameters of winglike
				tempMF.setParameters(5, 0.0002, 0.01);//the parameters of L1 loss, L2 loss, winglike3
				
				// Step 3. train
				System.out.println("\r\nBeign to train " + getNameOfScheme(i) +" for #" + j + " round(s)");
				tempTrainRoundsMatrix[i][j] = tempMF.train(paraMinimalRounds, 
						paraData.getValiadation(), i, WING_LIKE_LOSS_MF_3);
				long endTime = System.currentTimeMillis();
				System.out.println("The runtime \t" + (endTime - startTime));
				
				//MAE and RMSE of testing set 
				Triple[][] tempPredictions = tempMF.predictions(tempTestingMatrix);
				tempTestMaeMatrix[i][j] = Metrics.mae(tempPredictions, tempTestingMatrix);
				tempTestRmseMatrix[i][j] = Metrics.rmse(tempPredictions, tempTestingMatrix);
				
				File fileDeviationTest = new File(paraData.fileName + "-"+ getNameOfScheme(i) + "-deviationForTest");
				FileWriter outTest = new FileWriter(fileDeviationTest);
				for (int ii = 0; ii < tempTestingMatrix.length; ii++) {
					for(int jj = 0; jj < tempTestingMatrix[ii].length; jj++) {
						double tempDeviation = tempTestingMatrix[ii][jj].rating - tempPredictions[ii][jj].rating;
						outTest.write(ii+","+tempTestingMatrix[ii][jj].item+","+
								tempDeviation + "\r\n");
					}//Of for jj 
				} // Of for ii
				outTest.close();
				
				int[][] tempRankList = tempMF.computeRankList(tempTestingMatrix, TOP_K, paraData.getValiadation());
				tempTestHrMatrix[i][j] = Metrics.hr(tempRankList, tempTestingMatrix);
				tempTestMapMatrix[i][j] = Metrics.map(tempRankList, tempTestingMatrix);
				tempTestNdcgMatrix[i][j] = Metrics.ndcg(tempRankList, tempTestingMatrix);

				System.out.println("\r\nMAE: " + tempTestMaeMatrix[i][j]);
				System.out.println("RSME: " + tempTestRmseMatrix[i][j]);
				System.out.println("HR: " + tempTestHrMatrix[i][j]);
				System.out.println("MAP: " + tempTestMapMatrix[i][j]);
				System.out.println("NDCG: " + tempTestNdcgMatrix[i][j]);
			} // Of for i
		} // Of for j
		
		for (int i = 0; i < NUM_SCHEMES; i++) {
			tempTrainAverageMaeArray[i] = computeAverage(tempTrainMaeMatrix[i]);
			tempTrainAverageRoundsArray[i] = computeAverage(tempTrainRoundsMatrix[i]);
			tempTrainAverageRmseArray[i] = computeAverage(tempTrainRmseMatrix[i]);
			tempTestAverageMaeArray[i] = computeAverage(tempTestMaeMatrix[i]);
			tempTestAverageRmseArray[i] = computeAverage(tempTestRmseMatrix[i]);
			tempTestAverageHrArray[i] = computeAverage(tempTestHrMatrix[i]);
			tempTestAverageMapArray[i] = computeAverage(tempTestMapMatrix[i]);
			tempTestAverageNdcgArray[i] = computeAverage(tempTestNdcgMatrix[i]);
		}//Of for i
			
		double[] tempTrainMaeDeviationArray = new double[NUM_SCHEMES];
		double[] tempTrainRoundsDeviationArray = new double[NUM_SCHEMES];
		double[] tempTrainRmseDeviationArray = new double[NUM_SCHEMES];
		double[] tempTestMaeDeviationArray = new double[NUM_SCHEMES];
		double[] tempTestRmseDeviationArray = new double[NUM_SCHEMES];
		double[] tempTestHrDeviationArray = new double[NUM_SCHEMES];
		double[] tempTestMapDeviationArray = new double[NUM_SCHEMES];
		double[] tempTestNdcgDeviationArray = new double[NUM_SCHEMES];
		for (int i = 0; i < NUM_SCHEMES; i++) {
			tempTrainMaeDeviationArray[i] = computeDeviation(tempTrainMaeMatrix[i], tempTrainAverageMaeArray[i]);
			tempTrainRoundsDeviationArray[i] = computeDeviation(tempTrainRoundsMatrix[i], tempTrainAverageRoundsArray[i]);
			tempTrainRmseDeviationArray[i] = computeDeviation(tempTrainRmseMatrix[i], tempTrainAverageRmseArray[i]);
			tempTestMaeDeviationArray[i] = computeDeviation(tempTestMaeMatrix[i], tempTestAverageMaeArray[i]);
			tempTestRmseDeviationArray[i] = computeDeviation(tempTestRmseMatrix[i], tempTestAverageRmseArray[i]);
			tempTestHrDeviationArray[i] = computeDeviation(tempTestHrMatrix[i], tempTestAverageHrArray[i]);
			tempTestMapDeviationArray[i] = computeDeviation(tempTestMapMatrix[i], tempTestAverageMapArray[i]);
			tempTestNdcgDeviationArray[i] = computeDeviation(tempTestNdcgMatrix[i], tempTestAverageNdcgArray[i]);
		} // Of for i

		System.out.println("===Here are final results===");
		for (int i = 0; i < NUM_SCHEMES; i++) {
			System.out.println("Scheme #" + getNameOfScheme(i));
			System.out.println("Train MAE: " + tempTrainAverageMaeArray[i] + " +- "
					+ tempTrainMaeDeviationArray[i]);
			System.out.println("Train Rounds: " + tempTrainAverageRoundsArray[i] + " +- "
					+ tempTrainRoundsDeviationArray[i]);
			System.out.println("Train RMSE: " + tempTrainAverageRmseArray[i] + " +- "
					+ tempTrainRmseDeviationArray[i]);
			System.out.println("Test MAE: " + tempTestAverageMaeArray[i] + " +- "
					+ tempTestMaeDeviationArray[i]);
			System.out.println("Test RMSE: " + tempTestAverageRmseArray[i] + " +- "
					+ tempTestRmseDeviationArray[i]);
			System.out.println("Test HR: " + tempTestAverageHrArray[i] + " +- "
					+ tempTestHrDeviationArray[i]);
			System.out.println("Test MAP: " + tempTestAverageMapArray[i] + " +- "
						+ tempTestMapDeviationArray[i]);
			System.out.println("Test NDCG: " + tempTestAverageNdcgArray[i] + " +- "
						+ tempTestNdcgDeviationArray[i]);
		} // Of for j

		System.out.println("Detail:");
		for (int i = 0; i < NUM_SCHEMES; i++) {
			System.out.print("Test MAE detail: ");
			for (int j = 0; j < paraNumExperiments; j++) {
				System.out.print("" + tempTestMaeMatrix[i][j] + ",");
			} // Of for j
			System.out.println();
			System.out.print("Test RMSE detail: ");
			for (int j = 0; j < paraNumExperiments; j++) {
				System.out.print("" + tempTestRmseMatrix[i][j] + ",");
			} // Of for j
			System.out.println();
			System.out.print("Test HR detail: ");
			for (int j = 0; j < paraNumExperiments; j++) {
				System.out.print("" + tempTestHrMatrix[i][j] + ",");
			} // Of for j
			System.out.println();
			System.out.print("Test MAP detail: ");
			for (int j = 0; j < paraNumExperiments; j++) {
				System.out.print("" + tempTestMapMatrix[i][j] + ",");
			} // Of for j
			System.out.println();
			System.out.print("Test NDCG detail: ");
			for (int j = 0; j < paraNumExperiments; j++) {
				System.out.print("" + tempTestNdcgMatrix[i][j] + ",");
			} // Of for j
			System.out.println();
		} // Of for i
	}// Of schemeComparison
	
	/* 
	 * @param paraVector
	 * @return
	 */
	 public static double computeAverage(double[] paraVector) {
		 double resultAverage = 0;
		 for(int i = 0; i < paraVector.length; i++)
			resultAverage += paraVector[i] / paraVector.length;
		 return resultAverage;
	 }//Of computeAverage
	
	/**
	  * 
	  * @param paraVector
	  * @return
	  */
	 public static double computeDeviation(double[] paraVector, double paraAverage) {
		 double resultDeviation = 0;
		 for(int i = 0; i < paraVector.length; i++) {
			 double tempdiff = paraVector[i] - paraAverage;
			resultDeviation += tempdiff * tempdiff / paraVector.length;
		 }//Of for i 
		 return Math.sqrt(resultDeviation);
	 }//Of computeDeviation
	 
	 /**
	  * 
	  * @param paraIndexOfScheme
	  * @return
	  */
	 public static String getNameOfScheme(int paraIndexOfScheme) {
		 String reulstOfSchemName =  new String("");
		 switch(paraIndexOfScheme) {
		 case 0:
			 reulstOfSchemName =  "WinglikeLoss3";
			  break;
		 }//Of switch
			 return reulstOfSchemName;
		
	 }//Of 

	/**
	 ************************ 
	 * @param args
	 * @throws IOException
	 ************************ 
	 */
	@SuppressWarnings("resource")
	public static void main(String args[]) throws IOException {
		int numDatasets = 6;
 
		String[] datasetNames = { "Movielens100K", "Movielens1M", "DoubanMusic", "Yelp", "Netflix", "Jester" };
		for (int i = 0; i < numDatasets; i++) {
			String datasetName = datasetNames[i];
			System.out.println(datasetName);
			String dataProperty = new String("data/" + datasetName + ".properties");
			String regularAndValidation = new String("data/regularAndValidation.properties");
			RatingSystem tempData = new RatingSystem(dataProperty, regularAndValidation);
//			tempData.readJester(datasetName);
//			tempData.splitTrainValidationTest(0.8, 0.1);
			tempData.readData();// include computing the average rating of training matrix.
			tempData.centralizeAll();
			schemeComparison(tempData, 1000, 10);
			LocalDateTime localDateTime = LocalDateTime.now();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
			String tempDateString = localDateTime.format(dtf);
			File tempRecordResultFile = new File(
					datasetName + "-" + "Validation-"+ tempData.getValiadation() +"-Regular-" + tempData.getRegular() + "-" + tempDateString + "-" + "Result.txt");

			FileChannel inputChannel = null;
			FileChannel outputChannel = null;
			try {
				inputChannel = new FileInputStream("D:\\×¼\\xyyLS\\¶þ\\WinglikeMF - 008\\WinglikeMF - 008\\result.txt").getChannel();
				outputChannel = new FileOutputStream(tempRecordResultFile).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			} finally {
				inputChannel.close();
				outputChannel.close();
			} // Of finally
		} // Of for i
	}// Of main
}// Of class WinglikeMF 
