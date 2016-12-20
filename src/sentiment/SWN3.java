package sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Set the general score for every lexicon (term)
 */
public class SWN3 {

	private Map<String, Double> dictionary;

	public SWN3(String pathToSWN) throws IOException {
		
		dictionary = new HashMap<String, Double>();
		HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

		BufferedReader csv = null;
		
		try {
			
			// initialize the location of SentiWordNet (SWN) file
			csv = new BufferedReader(new FileReader(pathToSWN));
			int lineNumber = 0;
			String line;
			
			// read the SWN file
			while ((line = csv.readLine()) != null) {
				
				lineNumber++;
				
				/**
				 * trim() removes the leading and trailing whitespace from a string
				 * startsWith() checks whether the first part of current string contains the string from parameter
				 * 
				 * The format of sentiwordnet dictionary:
				 * POS ID PosS NegS SynsetTerm#sensenumber Desc (6 attributes)
				 * 
				 * Example of SynsetTerm:
				 * - midway, middle, halfway, center which has the similar meaning, namely
				 * equally distant from the extremes
				 */
				if (!line.trim().startsWith("#")) {
					
					String[] data = line.split("\t");
					String wordTypeMarker = data[0];
					
					// must have 6 attributes
					if (data.length != 6) {
						throw new IllegalArgumentException("Incorrect tabulation format in file, line: "+ lineNumber);
					}
					
					// synsetScore as PosS - NegS
					Double synsetScore = Double.parseDouble(data[2]) - Double.parseDouble(data[3]);
					
					/** get the whole synonim terms from one row
					 * Example:
					 * synTermsSplit = [uncut#7, full-length#2]
					 */
					String[] synTermsSplit = data[4].split(" ");
					
					// iterate over the list of synonim terms
					for (String synTermSplit : synTermsSplit) {
						
						// get the synonim term and the sense number
						String[] synTermAndRank = synTermSplit.split("#");
						
						// assign synonim term as combination of the fetched syn term, #, and POS
						String synTerm = synTermAndRank[0] + "#" + wordTypeMarker;
						
						// assign the rank of synonim term (sense number)
						int synTermRank = Integer.parseInt(synTermAndRank[1]);
						
						/** 
						 * Find the map entry that has synTerm as the key
						 * If it can not be found, then the new map entry will be created
						 * Vice versa, it just stores the new pair value of synTerm rank and synset score
						 *
						 * The format of tempDictionary:
						 * [cook#v, {[3, 0.5], [2, 0.75], [1, 0.25]}]
						 */
						if (!tempDictionary.containsKey(synTerm)) {
							tempDictionary.put(synTerm, new HashMap<Integer, Double>());
						}
						
						tempDictionary.get(synTerm).put(synTermRank, synsetScore);
					
					}
				}
				
			}
			
			// go through all the terms
			for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary.entrySet()) {
				
				/**
				 * get the entry's key: cook#v
				 */
				String word = entry.getKey();
				
				/** 
				 * get the pair value of synonim rank and synonim score
				 * {[3, 0.5], [2, 0.75], [1, 0.25]}
				 */
				Map<Integer, Double> synSetScoreMap = entry.getValue();

				/** 
				 * calculate weighted average. Weigh the synsets according to
				 * their rank.
				 * Score= synScore1/synRank1 + synScore2/synRank2 + synScore3/synRank3 ..... etc.
				 * Sum = 1/synRank1 + 1/synRank2 + 1/synRank3 ...
				 */
				double score = 0.0;
				double sum = 0.0;
				
				for (Map.Entry<Integer, Double> setScore : synSetScoreMap.entrySet()) {
					score += setScore.getValue() / (double) setScore.getKey();
					sum += 1.0 / (double) setScore.getKey();
				}
				
				score /= sum;
				dictionary.put(word, score);
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csv != null) {
				csv.close();
			}
		}
		
  }
  
  public double extract(String word, String pos) {
	  if (dictionary.containsKey(word+"#"+pos))
		  return dictionary.get(word + "#" + pos);
	  else
		  return 0.0;
  }
}