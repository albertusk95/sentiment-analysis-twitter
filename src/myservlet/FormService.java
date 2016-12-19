package myservlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import twitter4j.TwitterException;

import saitweet.Tweet;
import sentiment.Processor;
import semantic.Runner;
import visuals.PieChartData;

/**
 * Servlet implementation class FormService
 * Get the user's query and execute the whole processes
 */
@WebServlet("/FormService")
public class FormService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public FormService() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		   
		// query text
		String query = request.getParameter("query");
		
		
		// extracts Twitter data
		try {
			Tweet.setQuery(query);
			Tweet.extractTweet(query);
			Tweet.setTweetText();
			Tweet.setTotalTweet();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		
		
		// initiates sentiment processor
		try {
			Processor.startProcessor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// initiates semantic processor
		Runner.startRunner();
		
		// initiates visual data (graph)
		PieChartData.startVisualize();
		
		// debugging
		System.out.println("HELLO DOPOST");
		
		// redirect to primary.jsp with the query text
		//request.setAttribute("query", query);
		//request.getRequestDispatcher("#/primary").forward(request, response);
		//response.sendRedirect("localhost:8080/TwitterServlet/#/primary"); 
		
    	String contextPath = request.getContextPath();
    	System.out.println("context: " + contextPath);
    	System.out.println("servlet: " + request.getServletContext());
		
		//RequestDispatcher rd = request.getRequestDispatcher("/#/primary") ;
    	//rd.forward(request, response) ;
    	
		response.sendRedirect(contextPath + "#/primary"); 
		
	}

}
