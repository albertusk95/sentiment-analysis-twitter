package semantic;

import java.util.ArrayList;

import weka.core.*;
import weka.classifiers.meta.FilteredClassifier;

import saitweet.Tweet;

public class Classify {
	
	private String fileName = "";
	
	FilteredClassifier classifier;
	Instances instances;

	public Classify(String fName) {
		fileName = fileName + System.getProperty("user.home") + "/workspace/TwitterServlet/resources/semantic/" + fName;
	}
	
	/*
	 * Read the previous built model
	 */
	public void loadModel() {
		try {
			System.out.println("===semantic: loading model===");
			classifier = (FilteredClassifier) weka.core.SerializationHelper.read(fileName);
 			System.out.println("=== Loaded model: " + fileName + " ===");
       } 
		catch (Exception e) {
			System.out.println("Problem found when reading: " + fileName);
			e.printStackTrace();
		}
	}
	
	/*
	 * Create the attributes, class and text
	 */
	public void makeInstance(String textTweet) {
		
		System.out.println("===semantic: makeInstance===");
		
		ArrayList<String> fvNominalVal = new ArrayList<String>();
		fvNominalVal.add("alt.atheism");
		fvNominalVal.add("comp.graphics");
		fvNominalVal.add("comp.os.ms-windows.misc");
		fvNominalVal.add("comp.sys.ibm.pc.hardware");
		fvNominalVal.add("comp.sys.mac.hardware");
		fvNominalVal.add("comp.windows.x");
		fvNominalVal.add("misc.forsale");
		fvNominalVal.add("rec.autos");
		fvNominalVal.add("rec.motorcycles");
		fvNominalVal.add("rec.sport.baseball");
		fvNominalVal.add("rec.sport.hockey");
		fvNominalVal.add("sci.crypt");
		fvNominalVal.add("sci.electronics");
		fvNominalVal.add("sci.med");
		fvNominalVal.add("sci.space");
		fvNominalVal.add("soc.religion.christian");
		fvNominalVal.add("talk.politics.guns");
		fvNominalVal.add("talk.politics.mideast");
		fvNominalVal.add("talk.politics.misc");
		fvNominalVal.add("talk.religion.misc");
		
		Attribute attribute1 = new Attribute("category", fvNominalVal);
		Attribute attribute2 = new Attribute("text", (ArrayList<String>)null);
		
		// Create list of instances with one element
		ArrayList<Attribute> fvWekaAttributes = new ArrayList<Attribute>(2);
		fvWekaAttributes.add(attribute1);
		fvWekaAttributes.add(attribute2);
		
		instances = new Instances("Test relation", fvWekaAttributes, 1);           
		
		// Set class index
		instances.setClassIndex(0);
		
		// Create and add the instance
		DenseInstance instance = new DenseInstance(2);
		instance.setValue(attribute2, textTweet);
		
		instances.add(instance);
 		
		System.out.println("=== Instance created with reference dataset ===");
		//System.out.println(instances);
	}
	
	public String classify() {
		
		int counter = 0;
		
		String predictedCls = null;
		
		/*
		 * clsDistSem stores the distribution for all classes
		 */
		double[] clsDistSem = new double[20];
		
		try {
			double pred = classifier.classifyInstance(instances.instance(0));
			double distribution[] = classifier.distributionForInstance(instances.instance(0));

			for (double distItem : distribution) {
				clsDistSem[counter] = distItem;
				counter++;
			}
			
			// set the list of class distribution for semantic
			Tweet.setClassDistSemantic(clsDistSem);
			
			/*
			System.out.println("===== Classified instance =====");
			System.out.println("Instance: " + instances.instance(0));
			
			for (int i = 0; i < distribution.length; i++) {
				System.out.println("distribution: " + String.valueOf(distribution[i]));
			}
			*/
			
			predictedCls = instances.classAttribute().value((int) pred);
			
			if ((int)pred == 0) {
				predictedCls = "atheism";
			} else if ((int)pred == 1) {
				predictedCls = "computer graphics";
			} else if ((int)pred == 2) {
				predictedCls = "computer OS Windows MISC (various kinds of Windows)";
			} else if ((int)pred == 3) {
				predictedCls = "computer system IBM (PC / hardware)";
			} else if ((int)pred == 4) {
				predictedCls = "computer system MAC (hardware)";
			} else if ((int)pred == 5) {
				predictedCls = "computer OS Windows X";
			} else if ((int)pred == 6) {
				predictedCls = "MISC forsale (various kinds of goods to sale)";
			} else if ((int)pred == 7) {
				predictedCls = "recreational autos";
			} else if ((int)pred == 8) {
				predictedCls = "recreational motorcycles";
			} else if ((int)pred == 9) {
				predictedCls = "recreational sport baseball";
			} else if ((int)pred == 10) {
				predictedCls = "recreational sport hockey";
			} else if ((int)pred == 11) {
				predictedCls = "science cryptography";
			} else if ((int)pred == 12) {
				predictedCls = "science electronics";
			} else if ((int)pred == 13) {
				predictedCls = "science medical";
			} else if ((int)pred == 14) {
				predictedCls = "science space";
			} else if ((int)pred == 15) {
				predictedCls = "social religion christian";
			} else if ((int)pred == 16) {
				predictedCls = "talk politics (guns)";
			} else if ((int)pred == 17) {
				predictedCls = "talk politics (mideast)";
			} else if ((int)pred == 18) {
				predictedCls = "talk politics MISC (various kinds of politics talk)";
			} else if ((int)pred == 19) {
				predictedCls = "talk religion MISC (various kinds of religions talk)";
			} 
			
			//System.out.println("Class predicted: " + predictedCls);
			
		}
		catch (Exception e) {
			System.out.println("Problem found when classifying the text");
			e.printStackTrace();
		}	

		return predictedCls;
	}
}
