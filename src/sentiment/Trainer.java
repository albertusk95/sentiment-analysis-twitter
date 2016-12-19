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
	
	/**Sets the folder name where the "training" .arff files are located.*/
	/**
	 * @param f main folder = resources/
	 */
	public Trainer(String f) {
		folder = f;
		tba = new DualHashBidiMap<String, Integer>();
		fba = new DualHashBidiMap<String, Integer>();
		cba = new DualHashBidiMap<String, Integer>();
	}
	
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
	
	// experiment code
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
	
	
	/**Training on the lexicon-based representation. Saves the model in
	 * order to use it on the provided test sets. The rest of the model
	 * representation forms will be created on-the-fly because of the 
	 * minimum term frequency threshold that takes both train and test
	 * sets into consideration.*/
	public void trainLexicon() {
		
		System.out.println("trainLexicon started");
		
		DataSource ds;
		Instances data = null;
		try {
			ds = new DataSource(folder+"train/0L.arff");
			//ds = new DataSource(folder+"train/mylex.arff");
			
			data =  ds.getDataSet();
			System.out.println("dataset for lexicon loaded");
		} catch (Exception e) {
			System.out.println("Lexicon training file not found.");
		}
		data.setClassIndex(6);
		Classifier cls = (Classifier)new LibSVM();

		// exp code
		//LibSVM cls = new LibSVM();
		//cls.setShrinking(true);
		// end of exp code
		
		try {
			System.out.println("building classifier");
			cls.buildClassifier(data);
			System.out.println("classifier built");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Cannot build classifier on the lexicon-based represetnation...");
		}
		try {
			weka.core.SerializationHelper.write(folder+"models/lexicon.model", cls);
			//weka.core.SerializationHelper.write(folder+"models/mylex.model", cls);
			
			System.out.println("training lexicon done");
		} catch (Exception e) {
			System.out.println("Could not save the generated model...");
		}
	}
	
	
	/**Builds and saves the text-based model built on the training set.*/
	private void trainText(){
		Instances data = null;
		try {
			data = getText(folder+"train/0T.arff");
			saveFile(data, "T");
			twr = new BufferedWriter(new FileWriter(new File(folder+"attributes/text.tsv")));	// writes the attributes in a file
			for (int i=0; i<data.numAttributes(); i++){
				tba.put(data.attribute(i).name(), i);
				twr.write(data.attribute(i).name()+"\t"+i+"\n");
			}
			twr.close();
		} catch (Exception e) {
			System.out.println("Text-based training file not found.");
		}
		Classifier cls = (Classifier)new NaiveBayesMultinomial();
		try {
			cls.buildClassifier(data);
		} catch (Exception e) {
			System.out.println("Cannot build classifier on the text-based represetnation...");
		}
		try {
			weka.core.SerializationHelper.write(folder+"models/text.model", cls);
		} catch (Exception e) {
			System.out.println("Could not save the generated model...");
		}
	}
	
	/**Builds and saves the feature-based model built on the training set.*/
	private void trainFeatures(){
		Instances data = null;
		try {
			data = getFeature(folder+"train/0F.arff");
			saveFile(data, "F");
			fwr = new BufferedWriter(new FileWriter(new File(folder+"attributes/feature.tsv")));	// writes the attributes in a file

			for (int i=0; i<data.numAttributes(); i++){
				fba.put(data.attribute(i).name(), i);
				fwr.write(data.attribute(i).name()+"\t"+i+"\n");
			}
			fwr.close();
		} catch (Exception e) {
			System.out.println("Feature-based training file not found.");
		}
		Classifier cls = (Classifier)new NaiveBayesMultinomial();
		try {
			cls.buildClassifier(data);
		} catch (Exception e) {
			System.out.println("Cannot build classifier on the feature-based represetnation...");
		}
		try {
			weka.core.SerializationHelper.write(folder+"models/feature.model", cls);
		} catch (Exception e) {
			System.out.println("Could not save the generated model...");
		}
	}
	
	/**Builds and saves the combined model built on the training set.*/
	private void trainCombined(){
		Instances data = null;
		try {
			data = getComplex(folder+"train/0C.arff");
			saveFile(data, "C");
			cwr = new BufferedWriter(new FileWriter(new File(folder+"attributes/complex.tsv")));	// writes the attributes in a file

			for (int i=0; i<data.numAttributes(); i++){
				cba.put(data.attribute(i).name(), i);
				cwr.write(data.attribute(i).name()+"\t"+i+"\n");
			}
			cwr.close();
		} catch (Exception e) {
			System.out.println("Combined training file not found.");
		}
		Classifier cls = (Classifier)new NaiveBayesMultinomial();
		try {
			cls.buildClassifier(data);
		} catch (Exception e) {
			System.out.println("Cannot build classifier on the complex represetnation...");
		}
		try {
			weka.core.SerializationHelper.write(folder+"models/complex.model", cls);
		} catch (Exception e) {
			System.out.println("Could not save the generated model...");
		}
	}
	
	/**Returns the text-based Representations.*/
	private Instances getText(String fileText) throws Exception{
		DataSource ds = new DataSource(fileText);
		Instances data =  ds.getDataSet();
		data.setClassIndex(1);
		StringToWordVector filter = new StringToWordVector();
		filter.setInputFormat(data);
		filter.setLowerCaseTokens(true);
		filter.setMinTermFreq(1);
		//filter.setUseStoplist(false);
		filter.setTFTransform(false);
		filter.setIDFTransform(false);		
		filter.setWordsToKeep(1000000000);
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(2);
		filter.setTokenizer(tokenizer);	
		Instances newData = weka.filters.Filter.useFilter(data, filter);
		return newData;
	}
	

	/**Returns the Feature-based Representations.*/
	private Instances getFeature(String fileFeature) throws Exception{
		DataSource ds = new DataSource(fileFeature);
		Instances data =  ds.getDataSet();
		data.setClassIndex(1);
		StringToWordVector filter = new StringToWordVector();
		filter.setInputFormat(data);
		filter.setLowerCaseTokens(true);
		filter.setMinTermFreq(1);
		//filter.setUseStoplist(false);
		filter.setTFTransform(false);
		filter.setIDFTransform(false);		
		filter.setWordsToKeep(1000000000);
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(1);
		tokenizer.setNGramMaxSize(1);
		filter.setTokenizer(tokenizer);	
		Instances newData = weka.filters.Filter.useFilter(data, filter);
		return newData;
	}
	
	/**Returns the Combined (text+POS) Representations.*/
	private Instances getComplex(String fileComplex) throws Exception{
		DataSource ds = new DataSource(fileComplex);
		Instances data =  ds.getDataSet();
		data.setClassIndex(1);
		StringToWordVector filter = new StringToWordVector();
		filter.setInputFormat(data);
		filter.setLowerCaseTokens(true);
		filter.setMinTermFreq(1);
		//filter.setUseStoplist(false);
		filter.setTFTransform(false);
		filter.setIDFTransform(false);		
		filter.setWordsToKeep(1000000000);
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(2);
		filter.setTokenizer(tokenizer);	
		Instances newData = weka.filters.Filter.useFilter(data, filter);
		return newData;
	}	
	
	public void saveFile(Instances dataset, String type){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		try {
			saver.setFile(new File(folder+"train/"+type+".arff"));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}