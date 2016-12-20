package visuals;


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;

import saitweet.Tweet;

public class PieChartData {
	
	/*
	public static void startVisualize() {
	
		String pathToRes = System.getProperty("user.home") + "/workspace/TwitterServlet/WebContent/assets/img/";
		//String pathToRes = "http://localhost:8080/TwitterServlet/assets/img/";
		
		double[][] data = new double[][]{
		  {100, 200, 100, 100, 100},
		  {500, 300, 100, 100, 100}
		  };

		 final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(  "Team ", "", data);

		 final JFreeChart chart = ChartFactory.createAreaChart(
		  "Area Chart", "", "Value", dataset, PlotOrientation.VERTICAL, true, true, false);

		 final CategoryPlot plot = chart.getCategoryPlot();
		 plot.setForegroundAlpha(0.5f);

		 chart.setBackgroundPaint(new ChartColor(249, 231, 236));

		 try {
			 final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());

			 final File file1 = new File(pathToRes + "areachart.png");
		 
			 ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	*/
	
	
	// get the number of positive sentiment
	public static int getNumPos() {
		
		int counter = 0;
		for (String itm : Tweet.qrTweets_Sentiment) {
			if (itm.contains("pos")) {
				counter++;
			}
		}
		
		return counter;
	}
	
	// get the number of negative sentiment
	public static int getNumNeg() {
		
		int counter = 0;
		for (String itm : Tweet.qrTweets_Sentiment) {
			if (itm.contains("neg")) {
				counter++;
			}
		}
		
		return counter;
	}
	
	// starts the visualization
	public static void startVisualize() {
		
		// path to store the image
		String pathToRes = System.getProperty("user.home") + "/workspace/sentiment-analysis-twitter/WebContent/assets/img/";
		
		// get the total of positive and negative sentiment
		int numPosSentiment = getNumPos();
		int numNegSentiment = getNumNeg();
		
		DefaultPieDataset dataset = new DefaultPieDataset( );
	    dataset.setValue("positive", numPosSentiment);
	    dataset.setValue("negative", numNegSentiment);
	
	    JFreeChart chart = ChartFactory.createPieChart(
	       "Tweets sentiment", // chart title
	       dataset, // data
	       true, // include legend
	       true,
	       false);
	       
	   
	    PiePlot plot = (PiePlot) chart.getPlot();
        //plot.setSectionPaint(KEY1, Color.green);
        //plot.setSectionPaint(KEY2, Color.red);
        plot.setExplodePercent("positive", 0.10);
        //plot.setSimpleLabels(true);

        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator("{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
        plot.setLabelGenerator(gen);
	    
        
	    int width = 640; 
	    int height = 480; 
	    
	    try {
	    	
	    	ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());

	    	File pieChart = new File(pathToRes + "PieChart.png"); 
	    
	    	ChartUtilities.saveChartAsPNG(pieChart , chart , width , height, info);
			
	    } catch (Exception e) {
	    	System.out.println("Exception in startVisualize()");
	    	e.printStackTrace();
	    }
	    
	}
	
}