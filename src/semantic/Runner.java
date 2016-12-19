package semantic;

import java.util.ArrayList;
import java.util.List;

import saitweet.Tweet;

public class Runner {
	
	// Attributes
	static String pathToGetAttr = System.getProperty("user.home") + "/workspace/TwitterServlet/";
	static String main_folder = pathToGetAttr + "resources/semantic/";						 
	
	private static List<String> tweetPreproc;
	
	private static Classify cl;
	
	// Methods
	public static void startRunner() {
		
		// String loc_arff = main_folder + "fileARFF.arff";
		
		// train model - use this to build the model
		/* 
		Train tr = new Train(loc_arff);
		tr.initializeFilter();
		tr.loadDataset();
		tr.learn();
		tr.saveModel("semantic.model");
		tr.testModel();
		*/
		
		String semanticValue;
		
		// initiates tweetPreproc with the preprocessed tweet
		tweetPreproc = Tweet.qrTweets_Preprocessed;
		
		if (cl != null) {
			System.out.println("\nobject cl has aalready been CREATED");
			if (cl.classifier != null) {
				System.out.println("cl.classifier is NOT null");
			} else {
				System.out.println("cl classifier IS NULL");
			}
		} else {
			System.out.println("\ncl status: " + cl);
			
			cl = new Classify("semantic.model");
			
			// load the previous built model
			cl.loadModel();
		}
		
		// empty the list of semantic value
		Tweet.initTweetSemanticList();
	
		// empty the list of class distribution for semantic
		Tweet.initClassDistSemantic();
		
		// classify tweet semantic
		for (String prepTxt : tweetPreproc) {
			cl.makeInstance(prepTxt);
			semanticValue = cl.classify();
		
			Tweet.setTweetSemantic(semanticValue);
			
			System.out.println("Prep text semantic: " + prepTxt);
			System.out.println(semanticValue);
		}
		
	}
}
