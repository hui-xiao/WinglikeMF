package tool;

import datamodel.Triple;

/*
 * Metrics for recommender systems. 
 * 
 * @author Heng-Ru Zhang zhanghrswpu@163.com.
 */

public class Metrics {
	/**
	 * 
	 * @param paraRankLists
	 * @param paraTestingMatrix
	 * @return
	 */
	public static double hr(int[][] paraRankLists, Triple[][] paraTestingMatrix) {
		double resultHr = 0;
		int tempTotalNumHitUsers = 0;
		boolean tempHitItemsForEachUser = false;
		int tempNumUsers = paraRankLists.length;
		
		for(int i = 0; i < paraRankLists.length; i ++) {//user
			if(paraTestingMatrix[i].length == 0) {
				tempNumUsers --;
				continue;
			}//of if
			
			tempHitItemsForEachUser = false;
			for(int j = 0; j < paraRankLists[i].length; j ++) {//ranked items
				int tempItem = paraRankLists[i][j]; 
				for(int k = 0; k < paraTestingMatrix[i].length; k ++) {
					int tempGTTestingItem = paraTestingMatrix[i][k].item;
					if(tempItem == tempGTTestingItem) {
						tempTotalNumHitUsers++;
						tempHitItemsForEachUser = true;
						break;
					}//of if
				}//of for k
				if(tempHitItemsForEachUser)	{
//					System.out.printf("The hit user:%d\r\n",i);
					break;
				}//of if	
			}//of for j
		}//of for i
		
		resultHr = (1.0 * tempTotalNumHitUsers) / tempNumUsers; 
		
		return resultHr;
	}//of hr
	
	/**
	 * 
	 * @param paraRankLists
	 * @param paraTestingMatrix
	 * @return
	 */
	public static double map(int[][] paraRankLists, Triple[][] paraTestingMatrix) {
		double resultMap = 0;
		int tempNumUsers = paraRankLists.length;
		
		for(int i = 0; i < paraRankLists.length; i ++) {//user
			if(paraTestingMatrix[i].length == 0) {
				tempNumUsers --;
				continue;
			}//of if
			
			int tempHitItemsForEachUser = 0;
			double tempMapForEachUser = 0;
			for(int j = 0; j < paraRankLists[i].length; j ++) {//ranked items
				int tempItem = paraRankLists[i][j]; 
				for(int k = 0; k < paraTestingMatrix[i].length; k ++) {
					int tempGTTestingItem = paraTestingMatrix[i][k].item;
					if(tempItem == tempGTTestingItem) {
						tempHitItemsForEachUser ++;
						tempMapForEachUser += 1.0 * tempHitItemsForEachUser 
								/ (j + 1);
					}//of if
				}//of for k	
			}//of for j
			if (tempHitItemsForEachUser > 0) {
				tempMapForEachUser /= tempHitItemsForEachUser;
			} else {
				tempMapForEachUser = 0;
			} // Of if
			resultMap += tempMapForEachUser;
		}//of for i
		
		if (tempNumUsers > 0) {
			resultMap /= tempNumUsers; 
		}//of if
		
		return resultMap;
	}//of map
	
	/**
	 * 
	 * @param paraRankLists
	 * @param paraTestingMatrix
	 * @return
	 */
	public static double ndcg(int[][] paraRankLists, Triple[][] paraTestingMatrix) {
		double resultNdcg = 0;
		double tempDcg = 0;
		double tempIdcg = 0;
		int tempNumUsers = paraRankLists.length;
		
		for(int i = 0; i < paraRankLists.length; i ++) {//user
			if(paraTestingMatrix[i].length == 0) {
				tempNumUsers --;
				continue;
			}//of if
			
			int tempHitItemsForEachUser = 0;
			for(int j = 0; j < paraRankLists[i].length; j ++) {//ranked items
				int tempItem = paraRankLists[i][j]; 
				for(int k = 0; k < paraTestingMatrix[i].length; k ++) {
					int tempGTTestingItem = paraTestingMatrix[i][k].item;
					if(tempItem == tempGTTestingItem) {
						tempHitItemsForEachUser ++;
						tempDcg += Math.log(2) / Math.log(j + 2);
					}//of if
				}//of for k	
			}//of for j
			
			for (int j = 0; j < tempHitItemsForEachUser; j++) {
				tempIdcg += Math.log(2) / Math.log(j + 2);
			}// Of for j

			if (tempIdcg > 0) {
				resultNdcg += tempDcg / tempIdcg;
			}// of if
		}//of for i
		
		if (tempNumUsers > 0) {
			resultNdcg /= tempNumUsers; 
		}//of if
		
		return resultNdcg;	
	}//of ndcg
	
	/**
	 * 
	 * @param paraPredictions
	 * @param paraTestingMatrix
	 * @return
	 */
	public static double mae(Triple[][] paraPredictions, Triple[][] paraTestingMatrix) {
		double resultMae = 0;
		int tempCount = 0;
		
		for(int i = 0; i < paraPredictions.length; i ++) {
			for(int j = 0; j < paraPredictions[i].length; j ++) {
//				System.out.printf("user:%d, item: %d\r\n", i, paraPredictions[i][j].item);
				resultMae += Math.abs(paraPredictions[i][j].rating 
						- paraTestingMatrix[i][j].rating);
				tempCount ++;	
			}//of for j
		}//of for i
		
		if(tempCount > 0) {
			resultMae /= tempCount;
		}//of if
		
		return resultMae;
	}//of mae
	
	/**
	 * 
	 * @param paraPredictions
	 * @param paraTestingMatrix
	 * @return
	 */
	public static double mae(double[][] paraPredictions, double[][] paraTestingMatrix) {
		double resultMae = 0;
		int tempCount = 0;
		
		for(int i = 0; i < paraPredictions.length; i ++) {
			for(int j = 0; j < paraPredictions[i].length; j ++) {
				resultMae += Math.abs(paraPredictions[i][j] 
						- paraTestingMatrix[i][j]);
				tempCount ++;	
			}//of for j
		}//of for i
		
		if(tempCount > 0) {
			resultMae /= tempCount;
		}//of if
		
		return resultMae;
	}//of mae
	
	/**
	 * 
	 * @param paraPredictions
	 * @param paraTestingMatrix
	 * @return
	 */
	public static double rmse(Triple[][] paraPredictions, Triple[][] paraTestingMatrix) {
		double resultRmse = 0;
		int tempCount = 0;
		
		for(int i = 0; i < paraPredictions.length; i ++) {
			for(int j = 0; j < paraPredictions[i].length; j ++) {
				resultRmse += Math.pow(paraPredictions[i][j].rating 
						- paraTestingMatrix[i][j].rating, 2);
				tempCount ++;	
			}//of for j
		}//of for i
		
		if(tempCount > 0) {
			resultRmse = Math.sqrt(resultRmse / tempCount);
		}//of if
		
		return resultRmse;
	}//of mae
	
	 
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}//of main

}//of class Metrics
