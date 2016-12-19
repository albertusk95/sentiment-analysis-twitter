package saitweet;

import java.util.ArrayList;
import java.util.List;

import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Tweet {

	public static String tweetQuery;
	public static List<Status> qrTweets;
	public static List<String> qrTweets_Text = new ArrayList<String>();
	public static List<String> qrTweets_Sentiment = new ArrayList<String>();
	public static List<String> qrTweets_Preprocessed = new ArrayList<String>();
	public static List<String> qrTweets_PreprocFeature = new ArrayList<String>();
	public static List<String> qrTweets_PreprocComplex = new ArrayList<String>();
	public static List<String> qrTweets_Semantic = new ArrayList<String>();
	
	public static List<double[]> qrTweets_ClassDistText = new ArrayList<double[]>();
	public static List<double[]> qrTweets_ClassDistFeature = new ArrayList<double[]>();
	public static List<double[]> qrTweets_ClassDistComplex = new ArrayList<double[]>();
	public static List<double[]> qrTweets_ClassDistLexicon = new ArrayList<double[]>();
	
	public static List<double[]> qrTweets_ClassDistSemantic = new ArrayList<double[]>();
	
	public static List<String[]> qrTweets_PredClass = new ArrayList<String[]>();
	
	public static int qrTweets_TotalItem;
	
	/*
	 * Extract Twitter data
	 */
	public static void extractTweet(String tweet) throws TwitterException {
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		
		cb.setDebugEnabled(true);
		
		/*
		cb.setOAuthConsumerKey("74ydo2PwWnePf3X4zBzleNJ2t");
		cb.setOAuthConsumerSecret("KfIjEiPnosHTf1XZieGWXrldtDQWKFFh1jO63dxfjGqYDp2bLd");
		cb.setOAuthAccessToken("2729380164-fQvHtakqqd8Uuwi5BFAGb7bjANsV6FZzMDHfkZg");
		cb.setOAuthAccessTokenSecret("fDBcQDeiaBH1BVUVJ1OduRdsHTs4ozZMScOY8KYqgd0It");
		*/
		
		
		cb.setOAuthConsumerKey("iI4Yy9PZHzjcUIRAZDiBhlsIz");
		cb.setOAuthConsumerSecret("qDfkTJhwUIzlTNBbBj1NqOo5Ws7hJqmk6ok2VHMoXtPaxVaiTq");
		cb.setOAuthAccessToken("2729380164-Al3tdtx9uezMqtbEz1V8KpMIXUF2ViBHKbeYVvB");
		cb.setOAuthAccessTokenSecret("czILLdY6SASlHvOJ7vTi51o4PLb5CnJ7IY47Qb91qF9RR");
		
		
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
				
		Query query = new Query(tweet+" +exclude:retweets");
		query.setCount(10); 	// set tweets per page to 10
		query.setLang("en");
		
		QueryResult qr = twitter.search(query);
		qrTweets = qr.getTweets();
		
		//Preprocessor pp = new Preprocessor();
 		
		System.out.println("Extracted tweets");
		
		for(Status t : qrTweets) {
 			System.out.println(t.getText());
 			
 			// tweets is same with qrTweets
 			//Tweet tt = (Tweet) qrTweets.get(0);
 	        //imgTemp = t.getUser().getProfileImageURL();        
 	        
 	        //img = new ImageIcon(imgTemp);
 	        
 			//tmp = pp.preprocessDocumentKeepSmiles(t.getText());
 			//tmp = tmp.replaceAll("[\n\r]", "");
 			//System.out.println("\tPreprocessed tweet: "+tmp);
 			//result.add("?,"+Utils.quote(tmp));
 		
		    for (MediaEntity mediaEntity : t.getMediaEntities()) {
		    	System.out.println("media: " + mediaEntity.getMediaURLHttps());
            }
        
 		}
		  
	}
	
	/** SETTER **/
	
	// set the query
	public static void setQuery(String tweetQuery) {
		Tweet.tweetQuery = tweetQuery;
	}
	
	// set the value for list of tweet text
	public static void setTweetText() {
		
		if (qrTweets_Text != null) {
			qrTweets_Text.clear();
		}
		
		for (Status t : qrTweets) {
			qrTweets_Text.add(t.getText());
		}
	}
	
	// set the total number of the extracted tweet
	public static void setTotalTweet() {
		qrTweets_TotalItem = qrTweets.size();
	}
	
	// set the sentiment value for every tweet
	public static void setTweetSentiment(String sv) {
		
		qrTweets_Sentiment.add(sv);
	}
	
	// set the semantic value for every tweet
	public static void setTweetSemantic(String sv) {
		
		qrTweets_Semantic.add(sv);
	}
	
	// set the list of preprocessed tweet
	public static void setTweetPreprocessed(String sv) {
		
		qrTweets_Preprocessed.add(sv);
	}
	
	// set the list of preprocessed tweet (feature)
	public static void setTweetPreprocFeature(String sv) {
		
		qrTweets_PreprocFeature.add(sv);
	}
	
	// set the list of preprocessed tweet (complex)
	public static void setTweetPreprocComplex(String sv) {
		
		qrTweets_PreprocComplex.add(sv);
	}
	
	// set the list of class distribution (text)
	public static void setClassDistText(double[] cd) {
	
		qrTweets_ClassDistText.add(cd);
	}
	
	// set the list of class distribution (text)
	public static void setClassDistFeature(double[] cd) {
		
		qrTweets_ClassDistFeature.add(cd);
	}
	
	// set the list of class distribution (text)
	public static void setClassDistComplex(double[] cd) {
		
		qrTweets_ClassDistComplex.add(cd);
	}
	
	// set the list of lexicon classifier result
	public static void setClassDistLexicon(double[] lc) {
		
		qrTweets_ClassDistLexicon.add(lc);
	}
	
	// set the list of predicted class
	public static void setPredictedClass(String[] pcls) {
		
		qrTweets_PredClass.add(pcls);
	}
	
	// set the list of class distribution for semantic
	public static void setClassDistSemantic(double[] ds) {
		
		qrTweets_ClassDistSemantic.add(ds);
	}
	
	/** INITIALIZER **/
	
	// empty the list of tweet sentiment
	public static void initTweetSentimentList() {
		if (qrTweets_Sentiment != null) {
			qrTweets_Sentiment.clear();
		}
	}
	
	// empty the list of preprocessed tweet
	public static void initTweetPreprocessed() {
		if (qrTweets_Preprocessed != null) {
			qrTweets_Preprocessed.clear();
		}
	}

	// empty the list of preprocessed tweet (feature)
	public static void initTweetPreprocFeature() {
		if (qrTweets_PreprocFeature != null) {
			qrTweets_PreprocFeature.clear();
		}
	}

	// empty the list of preprocessed tweet (complex)
	public static void initTweetPreprocComplex() {
		if (qrTweets_PreprocComplex != null) {
			qrTweets_PreprocComplex.clear();
		}
	}
	
	// empty the list of tweet semantic
	public static void initTweetSemanticList() {
		if (qrTweets_Semantic != null) {
			qrTweets_Semantic.clear();
		}
	}
	
	// empty the list of class distribution text
	public static void initClassDistText() {
		if (qrTweets_ClassDistText != null) {
			qrTweets_ClassDistText.clear();
		}
	}
	
	// empty the list of class distribution text
	public static void initClassDistFeature() {
		if (qrTweets_ClassDistFeature != null) {
			qrTweets_ClassDistFeature.clear();
		}
	}
		
	// empty the list of class distribution text
	public static void initClassDistComplex() {
		if (qrTweets_ClassDistComplex != null) {
			qrTweets_ClassDistComplex.clear();
		}
	}
	
	// empty the list of lexicon classifier result
	public static void initClassDistLexicon() {
		if (qrTweets_ClassDistLexicon != null) {
			qrTweets_ClassDistLexicon.clear();
		}
	}
	
	// empty the list of predicted class from PolarityClassifier (normal classification process)
	public static void initPredClass() {
		if (qrTweets_PredClass != null) {
			qrTweets_PredClass.clear();
		}
	}
	
	// empty the list of class distribution for semantic
	public static void initClassDistSemantic() {
		if (qrTweets_ClassDistSemantic != null) {
			qrTweets_ClassDistSemantic.clear();
		}
	}

}
