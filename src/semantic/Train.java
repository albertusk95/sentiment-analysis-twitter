package semantic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.classifiers.Evaluation;
import java.util.Random;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.tokenizers.NGramTokenizer;

public class Train {

	private String fileName;
	
	StringToWordVector stwv;
	Instances trainData;
	FilteredClassifier classifier;
		
	public Train(String fileName) {
		this.fileName = fileName;
	}
	
	/*
	 * Read the dataset
	 */
	public void loadDataset() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			trainData = arff.getData();
			System.out.println("===== Loaded dataset: " + fileName + " =====");
			reader.close();
		}
		catch (IOException e) {
			System.out.println("Problem found when reading: " + fileName);
		}
	}
	
	/*
	 * Trains the classifier on the loaded dataset.
	 */
	public void learn() {
		try {
			trainData.setClassIndex(0);
			
			// uncomment this 
			classifier = new FilteredClassifier();
			classifier.setFilter(stwv);
			classifier.setClassifier(new NaiveBayes());
			
			System.out.println("building classifier");
			
			classifier.buildClassifier(trainData);
			
			System.out.println("===== Training on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.out.println("Problem found when training");
		}
	}
	
	/*
	 * Evaluates the classifier (model)
	 */
	public void testModel() {
    	
    	try {
			//trainData.setClassIndex(0);
			
			Evaluation eTest = new Evaluation(trainData);
		
			System.out.println("evaluating...");
			
			//classifier = weka.core.SerializationHelper.read(main_folder+"/test_models/"+test_dataset+".model");
			
			//eTest.evaluateModel(classifier, trainData);
			
			///
			eTest.crossValidateModel(classifier, trainData, 4, new Random(1));
			///
			
			String strSummary = eTest.toSummaryString();
			System.out.println(strSummary);
			System.out.println("Status OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
	
	/*
	 * Save the built model into a file
	 */
	public void saveModel(String fileName) {
		try {
			/*
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(classifier);
            out.close();
			*/
			
			weka.core.SerializationHelper.write(fileName, classifier);
        
 			System.out.println("===== Saved model: " + fileName + " =====");
        } 
		catch (Exception e) {
			System.out.println("Problem found when writing: " + fileName);
		}
	}
	
	/*
	 * StringToWordVector filter initialization
	 */
	public void initializeFilter(){
		stwv = new StringToWordVector();
		stwv.setLowerCaseTokens(true);
		stwv.setMinTermFreq(1);
		stwv.setTFTransform(false);
		stwv.setIDFTransform(false);		
		stwv.setWordsToKeep(1000000000);
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(2);
		stwv.setTokenizer(tokenizer);
		stwv.setAttributeIndices("last");
	}
}
