package sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

import saitweet.Tweet;

public class SentimentAnalyser {
	
	Trainer tr;
	PolarityClassifier pc;
	TweetPreprocessor tp;
	
	boolean useSlidingWindow;
	StringToWordVector stwv;

	Instances train;
	Instances test;
	
	BidiMap<String, Integer> train_attributes;	// bigram-position
	Classifier multiNB;
	Instances training_text;
	
	/** Constructor 
	 * "main_folder" - define the initial directory to work on (resources folder)
	 * "useSW" - whether the training should be made on the most recent 1000 tweets
	 * @throws Exception 
	 */
	
	/**
	 * @param main_folder resources/
	 * @param useSW false
	 * @param test_dataset Liebherr
	 * @throws Exception
	 */
	public SentimentAnalyser(String main_folder, boolean useSW, String test_dataset) throws Exception {
		
		/* 
		 * Initiates:
		 * - resource directory
		 * - BidiMap objects for text, feature, and complex representation
		 */
		tr = new Trainer(main_folder);			
		//tr.train();
		System.out.println("object tr created");
		
		
		/* 
		 * MODEL BUILDER
		 * only use this if the model has not been created yet or you want to create new ones.
		 * Note: the process may take a long time depends on the size of datasets and the capability
		 * of classifier
		 */
		 
		// re-build the lexicon model based on Weka 3.8
		//tr.trainLexicon();
		
		// evaluate lexicon model
		//tr.evaluateLexicon();
		
		
		/*
		 * Initiates:
		 * - resource directory
		 * - BidiMap objects for text, feature, and complex representation
		 * - Filter: StringToWordVector and Tokenizer: NGramTokenizer
		 * - Classifier: MNB and LibSVM
		 */
		pc = new PolarityClassifier(main_folder, tr.getTextAttributes(), tr.getFeatureAttributes(), tr.getComplexAttributes());
		System.out.println("object pc created");
		
		/*
		 * Initiates:
		 * - resource directory
		 * - preprocessor (filterer) for text, feature, and complex representation
		 * - part of speech tagger (POS Tagger)
		 * - preprocessor (filterer) for lexicon
		 */
		tp = new TweetPreprocessor(main_folder);
		System.out.println("object tp created");
		
		/*
		 * Initiates:
		 * - Filter: StringToWordVector and NGramTokenizer
		 */
		initializeFilter();
		System.out.println("initializeFilter done");
		
		useSlidingWindow = useSW;
		
		
		/*
         * Initiates:
         * -  Attributes elements, namely text (String) and sentimentClass (positive, negative)
         */
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        ArrayList<String> classVal = new ArrayList<String>();
        classVal.add("positive");
        classVal.add("negative");
        attributes.add(new Attribute("text", (ArrayList<String>) null));
        attributes.add(new Attribute("sentimentClassValue", classVal));
        
        /*
		 * Initiates:
		 * - Instances for train data with initial size equals 0
		 */
        train = new Instances("somerel", attributes, 0);
		train.setClassIndex(1);
		
		/*
		 * Initiates:
		 * - Instances for data test with initial size equals 0
		 */
		test = new Instances("somerel", attributes, 0);
		test.setClassIndex(1);
		
		if (useSlidingWindow == false) {
			multiNB = (Classifier) weka.core.SerializationHelper.read(main_folder+"/test_models/"+test_dataset+".model");
			BufferedReader rd = new BufferedReader(new FileReader(new File(main_folder+"test_models/"+test_dataset+"-attributes.tsv")));
			train_attributes = new DualHashBidiMap<String, Integer>();
			String inline;
			int cnt = 0;
			
			while ((inline = rd.readLine()) != null){
				train_attributes.put(inline, cnt);
				cnt++;
			}
			rd.close();
			
			BufferedReader frd = new BufferedReader(new FileReader(new File(main_folder+"test_models/"+test_dataset+"-attributes.arff")));
			training_text = new Instances(frd);
			frd.close();
		}
	}

	

	/*
	 * Starts the whole process: preprocesses the given tweet, creates different representations
	 * of it (stored in "all[]" Instances) and tests it in the PolarityClassifier class
	 */
	public String getPolarity(String tweet){
		
		/*
		 * Stores temporary predicted classes coming from:
		 * - PolarityClassifier (tmp_predClass[0])
		 * - classifyOnSlidingWindow (tmp_predClass[1])
		 * - classifyOnModel (tmp_predClass[2])
		 */
		String[] tmp_predClass = new String[3];
		tmp_predClass[0] = "undefined";
		tmp_predClass[1] = "undefined";
		tmp_predClass[2] = "undefined";
		
		tp.setTweet(tweet);
		
		/*
		 * Initiates:
		 * - text instances (ex: ?, 'this is a text')
		 * - feature instances
		 * - complex instances
		 */
		tp.startProc();
		Instances[] all = tp.getAllInstances();
		
		String out = pc.test(all);
		
		// initiates PolarityClassifier predicted class
		tmp_predClass[0] = out;
		
		if (useSlidingWindow == true) {
		//if (useSlidingWindow == false){
			if (out.contains("pos") || out.contains("neg")) {	
				// if HC and LC agree ("positive"/"negative"), 
				// then put this document in the training set			
				double[] instanceValues = new double[train.numAttributes()];
		        instanceValues[0] = train.attribute(0).addStringValue(tweet);
		        
		        if (out.contains("pos")) {
		        	instanceValues[1] = 0;
		        }
		        else {
		        	instanceValues[1] = 1;
		        }
		        
		        if (train.numInstances()>1000){
		        	train.remove(0);
		        }
		        
		        train.add(new DenseInstance(1.0, instanceValues));
			} else {		
				// in case of HC & LC disagreement, 
				// add the document in the test set; it will be classified in the end of the process
				if (train.numInstances() > 0) {
					out = clarifyOnSlidingWindow(tweet);
					tmp_predClass[1] = out;
				}
				else {
					// unknown class for the train data is empty
					out = "positive (random)";
				}
			}
		} else {		
			// if useSlidingWindow is set to "false", then use the model
			if (out.contains("pos") || out.contains("neg")) {
				
				// set the list of predicted class comes from three possibilities
				Tweet.setPredictedClass(tmp_predClass);
				
				return out;
			} else {
				out = clarifyOnModel(tweet);
				tmp_predClass[2] = out;
			}
		}
		
		// set the list of predicted class comes from three possibilities
		Tweet.setPredictedClass(tmp_predClass);
		
		return out;
	}
	
	/*
	 * Decides upon a "disagreed" document by applying the learned model based on 
	 * the last 1,000 "agreed" documents
	 */
	private String clarifyOnSlidingWindow(String tweet){
		String out = "";
        double[] instanceValues = new double[train.numAttributes()];
        instanceValues[0] = train.attribute(0).addStringValue(tweet);
		train.add(new SparseInstance(1.0, instanceValues));
		try {
			stwv.setInputFormat(train);
			Instances newData = Filter.useFilter(train, stwv);
			Instances train_ins = new Instances(newData, 0, train.size()-1);
			Instances test_ins = new Instances(newData, train.size()-1, 1);
			Classifier mnb = (Classifier)new NaiveBayesMultinomial();
			mnb.buildClassifier(train_ins);
			double[] preds = mnb.distributionForInstance(test_ins.get(0));
			if (preds[0]>0.5)
				out = "positive";
			else
				out = "negative";
		} catch (Exception e) {
			e.printStackTrace();
		}
		train.remove(train.numInstances()-1);
		return out;
	}
	
	/*
	 * Decides upon a "disagreed" (out = nan) document by applying the learned model based on 
	 * the previously build model
	 */
	private String clarifyOnModel(String tweet){
		String out = "";
		
		// get the text-based representation of the document
        double[] instanceValues = new double[2];
        instanceValues[0] = test.attribute(0).addStringValue(tweet);
        test.add(new SparseInstance(1.0, instanceValues));
        
        try {
        	stwv.setInputFormat(test);
        	Instances newData = Filter.useFilter(test, stwv);
    		
        	// re-order attributes so that they are compatible with the training set's ones
        	Instances test_instance = reformatText(newData);
        	
        	// find the polarity of the document based on the previously built model
        	test_instance.setClassIndex(0);
        	double[] preds = multiNB.distributionForInstance(test_instance.get(0));
        	if (preds[0]>0.5)
        		out = "positive";
        	else
        		out = "negative";
        } catch (Exception e){
        	e.printStackTrace();
        }
        test.remove(0);
		return out;
	}
	
	/*
	 * Re-order the attributes of the given Instances according to the training file
	 */
	private Instances reformatText(Instances text_test){	
		// remove the attributes from the test set that are not used in the train set
		String[] options = new String[2];
		options[0] = "-R";
		String opt = "";
		boolean found = false;
		for (int j=0; j<text_test.numAttributes(); j++){
			if (train_attributes.get(text_test.attribute(j).name())==null){
				int pos = j+1;
				found = true;
				opt = opt+pos+",";
			} 
		}
		if (found==true)
			options[1] = opt.substring(0,opt.length()-1);
		else
			options[1] = "";
		Remove remove = new Remove();
		try {
			remove.setOptions(options);
			remove.setInputFormat(text_test);
			Instances newData = Filter.useFilter(text_test, remove);
			
			double[] values = new double[train_attributes.size()];			
			for (int at=0; at<newData.numAttributes(); at++){			
				int pos  = train_attributes.get(newData.attribute(at).name());		// get the index of this attribute in the train set
				values[pos] = newData.get(0).value(at);					// ...and its value
			}
			training_text.add(0, new SparseInstance(1.0, values));
			Instances tw = new Instances(training_text,0,1);
			training_text.remove(0);
			return tw;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * StringToWordVector filter initialization
	 */
	private void initializeFilter(){
		stwv = new StringToWordVector();
		stwv.setLowerCaseTokens(true);
		stwv.setMinTermFreq(1);
		//stwv.setUseStoplist(false);
		stwv.setTFTransform(false);
		stwv.setIDFTransform(false);		
		stwv.setWordsToKeep(1000000000);
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(2);
		stwv.setTokenizer(tokenizer);
		stwv.setAttributeIndices("first");
	}
}