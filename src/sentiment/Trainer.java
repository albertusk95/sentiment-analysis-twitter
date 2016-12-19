package sentiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;


public class Trainer {
	
	String folder;
	BidiMap<String, Integer> tba;
	BidiMap<String, Integer> fba;
	BidiMap<String, Integer> cba;
	BufferedWriter twr;
	BufferedWriter fwr;
	BufferedWriter cwr;
	
	/**
	 * Set the folder name where the data training files are located
	 * @param f main folder = resources/
	 */
	public Trainer(String f) {
		folder = f;
		tba = new DualHashBidiMap<String, Integer>();
		fba = new DualHashBidiMap<String, Integer>();
		cba = new DualHashBidiMap<String, Integer>();
	}
	
	/*
	 * Train model based on 3 representations:
	 * - lexicon
	 * - text
	 * - features
	 * - complex (text and POS)
	 */
	public void train(){
		trainLexicon();
		trainText();
		trainFeatures();
		trainCombined();
	}
	
	/**
	 * GETTERS
	 */
	
	/*
	 * Text: all of the two words combinations
	 */
	public BidiMap<String, Integer> getTextAttributes(){
		try{
			tba.clear();
			
			BufferedReader rdr = new BufferedReader(new FileReader(new File(folder+"attributes/text.tsv")));
			String inline;
			
			while ((inline = rdr.readLine()) != null) {
				// TAB as delimiter
				String[] dic = inline.split("\\t");
				tba.put(dic[0], Integer.parseInt(dic[1]));
			}
			
			rdr.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return tba;
	}
	
	/*
	 * Feature: all of the one word combinations
	 */
	public BidiMap<String, Integer> getFeatureAttributes(){
		
		try {
			fba.clear();
			
			BufferedReader rdr = new BufferedReader(new FileReader(new File(folder+"attributes/feature.tsv")));
			String inline;
			
			while ((inline = rdr.readLine()) != null){
				String[] dic = inline.split("\\t");
				fba.put(dic[0], Integer.parseInt(dic[1]));
			}
			
			rdr.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return fba;
	}
	
	/*
	 * Complex: all of the two words combinations where each word has their own TAGGER 
	 */
	public BidiMap<String, Integer> getComplexAttributes(){
		
		try {
			cba.clear();
			
			BufferedReader rdr = new BufferedReader(new FileReader(new File(folder+"attributes/complex.tsv")));
			String inline;
			
			while ((inline = rdr.readLine()) != null){
				String[] dic = inline.split("\\t");
				cba.put(dic[0], Integer.parseInt(dic[1]));
			}
			
			rdr.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return cba;
	}
	
	// experiment code for evaluation of lexicon model
	public void evaluateLexicon() {
		System.out.println("evaluateLexicon");
		
		DataSource dsR;
		Instances dataR = null;
		try {
			//dsR = new DataSource(folder+"train/0L.arff");
			dsR = new DataSource(folder+"train/mylex.arff");
			
			dataR =  dsR.getDataSet();
			System.out.println("dataset for lexicon loaded");
		} catch (Exception e) {
			System.out.println("Lexicon training file not found.");
		}
		dataR.setClassIndex(6);
		
		LibSVM lexcls;
		
		try {
			lexcls = (LibSVM) weka.core.SerializationHelper.read(folder+"/models/lexicon.model");
			System.out.println("model read");
			Evaluation eTest = new Evaluation(dataR);
			eTest.evaluateModel(lexcls, dataR);
	        String strSummary = eTest.toSummaryString();
	        System.out.println(strSummary);
			System.out.println("Status OK");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception lexcls or eTest");
		}
	}
	// end of experiment code
	
	
	/**
	 * Supervised learning using LibSVM
	 * Training on the lexicon-based representation.
	 * Saves the model in order to use it on the provided test sets. 
	 * The rest of the model representation forms will be created on-the-fly because of the 
	 * minimum term frequency threshold that takes both train and test
	 * sets into consideration.
	 */
	public void trainLexicon() {
		
		System.out.println("trainLexicon started");
		
		DataSource ds;
		Instances data = null;
		
		try {
			ds = new DataSource(folder + "train/0L.arff");
				
			data =  ds.getDataSet();
			
			System.out.println("dataset for lexicon loaded");
		} catch (Exception e) {
			System.out.println("Lexicon training file not found.");
		}
		
		data.setClassIndex(6);
		
		// create a LibSVM classifier
		Classifier cls = (Classifier)new LibSVM();
		
		// build model
		try {
			System.out.println("building classifier - trainLexicon");
			cls.buildClassifier(data);
			System.out.println("classifier built - trainLexicon");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot build classifier on the lexicon-based representation");
		}
		
		// save model
		try {
			weka.core.SerializationHelper.write(folder + "models/lexicon.model", cls);	
			System.out.println("lexicon model saved successfully");
		} catch (Exception e) {
			System.out.println("could not save the lexicon model");
		}
	}
	
	
	/**
	 * TEXT-BASED OPINION MINING
	 * Assignment of sentiment to whole opinion or division of opinions
	 * 
	 * Builds and saves the text-based model built on the training set
	 * The training set has already been preprocessed, where every special symbol
	 * is converted into a string representing the symbol
	 * ex. converts @user into 'usermentionsymbol',
	 * www.example.com into 'urllinksymbol',
	 * etc.
	 *
	 * General steps:
	 * 1. retrieve a new instances with a filter inside
	 * 2. save the instances to a file named according to the type of representation.
	 *    In this case, we use text representation, so the name will be 'T.arff'
	 * 3. write the attributes from the filtered instances (can be retreived from 'T.arff')
	 *    to a file in 'attributes' folder (text.tsv). The attributes are the tokenized words and
	 *    the representation is based on the used tokenizer
	 * 4. create classifier (NaiveBayesMultinomial), build model, and save model
	 */
	private void trainText() {
		
		Instances data = null;
		
		try {
			
			// get the instances with a filter within it
			data = getText(folder+"train/0T.arff");
			
			// save the filtered text-based instances
			saveFile(data, "T");
			
			// write the attributes to a file (with TAB as the delimiter)
			twr = new BufferedWriter(new FileWriter(new File(folder + "attributes/text.tsv")));	
			
			for (int i = 0; i < data.numAttributes(); i++) {
				tba.put(data.attribute(i).name(), i);
				twr.write(data.attribute(i).name() + "\t" + i + "\n");
			}
			
			twr.close();
		} catch (Exception e) {
			System.out.println("text-based training file not found");
		}
		
		// create a classifier
		Classifier cls = (Classifier)new NaiveBayesMultinomial();
		
		// build model
		try {
			System.out.println("building classifier - text-based");
			cls.buildClassifier(data);
			System.out.println("classifier built - text-based");
		} catch (Exception e) {
			System.out.println("could not build classifier on the text-based representation");
		}
		
		// save model
		try {
			weka.core.SerializationHelper.write(folder + "models/text.model", cls);
		} catch (Exception e) {
			System.out.println("could not save the text-based model");
		}
		
	}
	
	/**
	 * FEATURE-BASED OPINION MINING
	 * Discovering what aspects of product users like or dislike.
	 * 
	 * Builds and saves the feature-based model built on the training set
	 * The training set has already been preprocessed, where every special symbol
	 * is converted into a string representing the symbol
	 * ex. converts @user into 'usermentionsymbol',
	 * www.example.com into 'urllinksymbol',
	 * etc.
	 *
	 * General steps:
	 * 1. retrieve a new instances with a filter inside
	 * 2. save the instances to a file named according to the type of representation.
	 *    In this case, we use feature representation, so the name will be 'F.arff'
	 * 3. write the attributes from the filtered instances (can be retreived from 'F.arff')
	 *    to a file in 'attributes' folder (feature.tsv). The attributes are the tokenized words and
	 *    the representation is based on the used tokenizer
	 * 4. create classifier (NaiveBayesMultinomial), build model, and save model
	 */
	private void trainFeatures(){
		
		Instances data = null;
		
		try {
			
			// get the instances with a filter within it
			data = getFeature(folder + "train/0F.arff");
			
			// save the instances with a filter inside
			saveFile(data, "F");
			
			// write the attributes 
			fwr = new BufferedWriter(new FileWriter(new File(folder + "attributes/feature.tsv")));	// writes the attributes in a file

			for (int i = 0; i < data.numAttributes(); i++){
				fba.put(data.attribute(i).name(), i);
				fwr.write(data.attribute(i).name() + "\t" + i + "\n");
			}
			
			fwr.close();
		} catch (Exception e) {
			System.out.println("feature-based training file not found.");
		}
		
		// create a classifier
		Classifier cls = (Classifier)new NaiveBayesMultinomial();
		
		// build model
		try {
			cls.buildClassifier(data);
		} catch (Exception e) {
			System.out.println("could not build classifier on the feature-based representation");
		}
		
		// save model
		try {
			weka.core.SerializationHelper.write(folder + "models/feature.model", cls);
		} catch (Exception e) {
			System.out.println("could not save the feature-based model");
		}
	}
	
	/**
	 * COMPLEX-BASED OPINION MINING
	 * Train model based on the combination of text and POS (Part of Speech).
	 * Each word is assigned to the corresponding POS
	 * 
	 * Builds and saves the complex-based model built on the training set
	 * The training set has already been preprocessed, where every special symbol
	 * is converted into a string representing the symbol
	 * ex. converts @user into 'usermentionsymbol',
	 * www.example.com into 'urllinksymbol',
	 * etc.
	 *
	 * General steps:
	 * 1. retrieve a new instances with a filter inside
	 * 2. save the instances to a file named according to the type of representation.
	 *    In this case, we use complex representation, so the name will be 'C.arff'
	 * 3. write the attributes from the filtered instances (can be retreived from 'C.arff')
	 *    to a file in 'attributes' folder (complex.tsv). The attributes are the tokenized words and
	 *    the representation is based on the used tokenizer
	 * 4. create classifier (NaiveBayesMultinomial), build model, and save model
	 */
	private void trainCombined(){
		
		Instances data = null;
		
		try {
			
			// get the instances with a filter inside
			data = getComplex(folder + "train/0C.arff");
			
			// save the instances to a file
			saveFile(data, "C");
			
			// write the attributes
			cwr = new BufferedWriter(new FileWriter(new File(folder + "attributes/complex.tsv")));	// writes the attributes in a file

			for (int i = 0; i < data.numAttributes(); i++){
				cba.put(data.attribute(i).name(), i);
				cwr.write(data.attribute(i).name() + "\t" + i + "\n");
			}
			
			cwr.close();
			
		} catch (Exception e) {
			System.out.println("combined training file not found.");
		}
		
		// create a classifier
		Classifier cls = (Classifier)new NaiveBayesMultinomial();
		
		// build model
		try {
			cls.buildClassifier(data);
		} catch (Exception e) {
			System.out.println("could not build classifier on the complex-based representation");
		}
		
		// save model
		try {
			weka.core.SerializationHelper.write(folder + "models/complex.model", cls);
		} catch (Exception e) {
			System.out.println("could not save the complex-based model");
		}
	}
	
	
	/**
	 * Returns the text-based instances with StringToWordVector as the filter
	 */
	private Instances getText(String fileText) throws Exception {
		
		// create object for data train (text based)
		DataSource ds = new DataSource(fileText);
		
		// create new instances for data train (text based)
		Instances data =  ds.getDataSet();
		data.setClassIndex(1);
		
		// set the filter for the dataset
		StringToWordVector filter = new StringToWordVector();
		filter.setInputFormat(data);
		filter.setLowerCaseTokens(true);
		filter.setMinTermFreq(1);
		//filter.setUseStoplist(false);
		filter.setTFTransform(false);
		filter.setIDFTransform(false);		
		filter.setWordsToKeep(1000000000);
		
		// set NGram tokenizer for the filter
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(2);
		
		filter.setTokenizer(tokenizer);	
		
		// create a new instances which has a filter 
		Instances newData = weka.filters.Filter.useFilter(data, filter);
		
		return newData;
	
	}
	

	/**
	 * Returns the feature-based representations with StringToWordVector as the filter
	 */
	private Instances getFeature(String fileFeature) throws Exception {
		
		// create object for the data train (feature-based)
		DataSource ds = new DataSource(fileFeature);
		
		// create an instances for the dataset
		Instances data =  ds.getDataSet();
		data.setClassIndex(1);
		
		// create a STWV filter
		StringToWordVector filter = new StringToWordVector();
		filter.setInputFormat(data);
		filter.setLowerCaseTokens(true);
		filter.setMinTermFreq(1);
		//filter.setUseStoplist(false);
		filter.setTFTransform(false);
		filter.setIDFTransform(false);		
		filter.setWordsToKeep(1000000000);
		
		// create a tokenizer
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(1);
		tokenizer.setNGramMaxSize(1);
		
		// add the tokenizer to the filter
		filter.setTokenizer(tokenizer);	
		
		// create a new instances which has a filter 
		Instances newData = weka.filters.Filter.useFilter(data, filter);
		
		return newData;
	
	}
	
	
	/**
	 * Returns the combined (text + POS) representations with StringToWordVector as the filter
	 */
	private Instances getComplex(String fileComplex) throws Exception {
		
		// create object for the data train (complex-based)
		DataSource ds = new DataSource(fileComplex);
		
		// create a new instances
		Instances data =  ds.getDataSet();
		data.setClassIndex(1);
		
		// create a STWV filter
		StringToWordVector filter = new StringToWordVector();
		filter.setInputFormat(data);
		filter.setLowerCaseTokens(true);
		filter.setMinTermFreq(1);
		//filter.setUseStoplist(false);
		filter.setTFTransform(false);
		filter.setIDFTransform(false);		
		filter.setWordsToKeep(1000000000);
		
		// create a tokenizer for STWV
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(2);
		
		// set the tokenizer as part of the filter
		filter.setTokenizer(tokenizer);	
		
		// create a new instances which has a filter 
		Instances newData = weka.filters.Filter.useFilter(data, filter);
		
		return newData;
	}	
	
	
	/**
	 * Save the filtered instances to an ARFF file
	 * The instances has been filtered with STWV
	 */
	public void saveFile(Instances dataset, String type){
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		
		try {
			saver.setFile(new File(folder + "train/" + type + ".arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}