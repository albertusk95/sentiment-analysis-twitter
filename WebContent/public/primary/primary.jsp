<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ page import="java.text.MessageFormat" %>

<%@ page import="saitweet.Tweet" %>
<%@ page import="visuals.PieChartData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- VERTICAL NAVBAR -->	

<div id="analysis_navbar" class="navbar navbar-custom navbar-fixed-left">
	
	<ul id="navbarVertical" class="nav navbar-nav">
		<li><a ng-class="{'activePrimary': selectedView === 0}" ng-click="setView(0)">DASHBOARD</a></li>
		<li><a ng-class="{'activePrimary': selectedView === 1}" ng-click="setView(1)">SENTIMENT</a></li>
		<li><a ng-class="{'activePrimary': selectedView === 2}" ng-click="setView(2)">SEMANTIC</a></li>
		<li><a ng-class="{'activePrimary': selectedView === 3}" ng-click="setView(3)">VISUAL</a></li>
	</ul>

</div>
<!-- END OF VERTICAL NAVBAR -->



<!-- SUPPORT VARIABLE -->
<c:set var="tweetNum" value="<%= Tweet.qrTweets_TotalItem %>" />
<c:set var="sentimentItem" value="<%= Tweet.qrTweets_Sentiment %>" />
<c:set var="tweetTextItem" value="<%= Tweet.qrTweets_Text %>" />
<c:set var="tweetPrepropItem" value="<%= Tweet.qrTweets_Preprocessed %>" />
<c:set var="tweetPrepropFeature" value="<%= Tweet.qrTweets_PreprocFeature %>" />
<c:set var="tweetPrepropComplex" value="<%= Tweet.qrTweets_PreprocComplex %>" />
<c:set var="classDistText" value="<%= Tweet.qrTweets_ClassDistText %>" />
<c:set var="classDistFeature" value="<%= Tweet.qrTweets_ClassDistFeature %>" />
<c:set var="classDistComplex" value="<%= Tweet.qrTweets_ClassDistComplex %>" />
<c:set var="classDistLexicon" value="<%= Tweet.qrTweets_ClassDistLexicon %>" />
<c:set var="predClass" value="<%= Tweet.qrTweets_PredClass %>" />
<c:set var="semanticItem" value="<%= Tweet.qrTweets_Semantic %>" />
<c:set var="semanticPredAndDist" value="<%= Tweet.qrTweets_ClassDistSemantic %>" />



<!-- DASHBOARD AREA -->
<div class="testTweet" ng-show="selectedView === 0">
 
		<div class="titleContainer">
			<h1>Total tweet: <c:out value="${tweetNum}" /></h1>
		</div>
		
		<c:choose>
		
		<c:when test="${tweetNum == 0}">
			<div class="tweetContainer">
	    		<h1>Tweet not found</h1>
	    	</div>
	  	</c:when>
	  			
		<c:otherwise>
		<c:forEach var="item" items="<%= Tweet.qrTweets %>" varStatus="loop">
			
			<div class="tweetContainer">
				
				<!-- 
				<table border="1" class="table table-striped" style="width: 100%; display: block; table-layout: fixed;">
				-->
				<table class="table table-striped" style="width: 100%; table-layout: fixed;">
				
					<thead>
				  		<tr>
				    		<!-- user image profile -->
				      		<th rowspan="2" class="userIMG" style="width: 15%;">
				      		
				      			<img src="${item.getUser().getProfileImageURL()}" style="display: block; width: 100%; height: 100%" />
				      		
				      		</th>
				      		
				      		<!-- user full name -->
				      		<th class="userFullname" style="width: 85%; max-width:450px; word-wrap:break-word;">
				      			
				      			<c:out value="${item.getUser().getName()}" />
				      			
				      		</th>
				    	</tr>
					    <tr>
					    	<!-- username -->
					      	<th class="userUsername" style="width: 85%; max-width:450px; word-wrap:break-word;">
					      		<c:out value="${item.getUser().getScreenName()}" />
					      	</th>
					    </tr>
				  	</thead>
				  	<tbody>
				    	<tr>
				      		<td colspan="2" class="userText" style="max-width:700px; word-wrap:break-word;">
				      			<c:out value="${item.getText()}" />
				      		</td>
				    	</tr>
				  	</tbody>
				</table>
			
			</div>
			
			<!-- DETAIL BUTTON -->
			<div class="detailContainer">
				<div class="detailButtonContainer" ng-click="showDetails(${loop.index})">
					<b>Show details</b>
				</div>
				
				<div class="detailAnalysisContainer" ng-click="showAnalysis(${loop.index})">
					<b>Show Analysis</b>
				</div>
			</div>
			
			<!-- Table showing detail of the tweet -->
			<div id="${loop.index}" style="display: none; margin-bottom: 90px">
	            
	            <table class="table table-hover" style="width: 100%; table-layout: fixed;">
				  <tbody>
				  	<!-- Tweet info -->
				    <tr>
				      	<td rowspan="5" class="thead-inverse" style="width: 15%; text-align:center; vertical-align: middle;">
				      		<b>Tweet info</b>
				      	</td>
				      	<td style="width: 25%;">ID</td>
				      	<td style="max-width:450px; word-wrap:break-word;">
				      		<c:out value="${loop.index}" />
				      	</td>				      
				 	</tr>
				 	<tr>
				      	<td style="width: 25%;">Name</td>
				      	<td style="max-width:450px; word-wrap:break-word;">
				      		<c:out value="${item.getUser().getName()}" />
				      	</td>				      
				    </tr>
				    <tr>
				    	<td style="width: 25%;">Username</td>
				      	<td style="max-width:450px; word-wrap:break-word;">
				      		<c:out value="${item.getUser().getScreenName()}" />
				      	</td>
				    </tr>
				    <tr>
				    	<td style="width: 20%;">Posted time</td>
				      	<td style="max-width:450px; word-wrap:break-word;">
				      		<c:out value="${item.getCreatedAt()}" />
				      	</td>
				    </tr>
				    <tr>
				    	<td style="width: 20%;">Location</td>
				    	<td style="max-width:450px; word-wrap:break-word;">
				      		<c:choose>
							    <c:when test="${item.getUser().getLocation() != null}">
									<c:out value="${item.getUser().getLocation()}" />        
							    </c:when>    
							    <c:otherwise>
							        <c:out value="---" />
							    </c:otherwise>
							</c:choose>
				      	</td>
				    </tr>
				    
				    <!-- Tweet URL -->
				    <tr>
				    	<td style="text-align: center;">
				    		<b>Tweet URL</b>
				    	</td>
				    	<td colspan="2" style="max-width:450px; word-wrap:break-word;">
				    		<a href="https://twitter.com/${item.getUser().getScreenName()}/status/${item.getId()}">
				    			<c:out value="https://twitter.com/${item.getUser().getScreenName()}/status/${item.getId()}" />
				    		</a>
				    	</td>
				    </tr>
				    
				    <!-- Media Entities -->
			    	
			    	<c:forEach var="media" items="${item.getMediaEntities()}" varStatus="counterMedia">
				      	<tr>
				      		<td style="text-align: center;">
				      			<b><c:out value="Media ${counterMedia.index}" /></b>
				      		</td>
				      		<td colspan="2" style="max-width:450px; word-wrap:break-word;">
				      			<a href="${media.getMediaURLHttps()}">
				      				<c:out value="${media.getMediaURLHttps()}" />
				      			</a>
				      		</td>
				      	</tr>				      
				   	</c:forEach>
					
				  </tbody>
				  
				</table>
					            
	     	</div>
		
			<!-- Table showing analysis detail -->
			<div id="analysis${loop.index}" style="display: none; margin-bottom: 100px">
				<table class="table table-hover" style="width: 100%; table-layout: fixed;">
					<tbody>
						<tr>
							<td style="width: 25%; text-align: center;">
								<b>Text</b>
							</td>
							<td style="text-align: center; max-width: 450px; word-wrap: break-word;">
								<c:out value="${tweetTextItem.get(loop.index)}" />
							</td>
						</tr>
						<tr>
							<td style="width: 25%; text-align: center;">
								<b>Preprocessed text</b>
							</td>
							<td style="text-align: center; max-width: 450px; word-wrap: break-word;">
								<c:out value="${tweetPrepropItem.get(loop.index)}" />
							</td>
						</tr>
						<tr>
							<td style="width: 25%; text-align: center;">
								<b>Sentiment</b>
							</td>
							<td style="text-align: center; max-width: 450px; word-wrap: break-word;">
								<c:out value="${sentimentItem.get(loop.index)}" />
							</td>
						</tr>
						<tr>
							<td style="width: 25%; text-align: center;">
								<b>Semantic</b>
							</td>
							<td style="text-align: center; max-width: 450px; word-wrap: break-word;">
								<c:out value="${semanticItem.get(loop.index)}" />
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		
		</c:forEach>
		
		</c:otherwise>
	
		</c:choose>
	
</div>

<!-- SENTIMENT AREA -->
<div class="testTweet" ng-show="selectedView === 1">
	
	<div class="titleContainer">
		<h1>Select Tweet ID</h1>
	</div>
	
	<div class="tweetIDContainer">
		
		<h2>ID: {{selectedID}}</h2>
		
		<select class="form-control" ng-model="selectedOption" ng-change="selectedTweetID()">
			
			<!-- ORIGIN LOGIC -->
			<c:choose>
			    <c:when test="${tweetNum > 0}">
					
					<c:forEach var="tweetID" begin="0" end="${tweetNum-1}">
						<option><c:out value="${tweetID}" /></option>	
					</c:forEach>
					        
			    </c:when>    
			    <c:otherwise>
			                
			    </c:otherwise>
			</c:choose>
			
		</select>	
	
	</div>
	
	<!-- BISA DITAMPILKAN USESLIDINGWINDOW, FILTER, TOKENIZER NYA -->
	<c:choose>
	
	<c:when test="${tweetNum == 0}">
		<div class="sentimentContainer">
			<h1>Tweet not found</h1>
		</div>
	</c:when>
	
	<c:otherwise>
	<c:forEach var="itemSentiment" begin="0" end="${tweetNum-1}" varStatus="loopSentiment">
		
		<div class="sentimentContainer" ng-show="selectedID == ${loopSentiment.index}">
			
			<table class="table table-hover" style="width: 100%; table-layout: fixed;">
			<tbody>
				<tr>
					<td style="width: 20%; text-align: center;">
						<b>Text</b>
					</td>
					<td colspan="3" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${tweetTextItem.get(loopSentiment.index)}" />
					</td>
				</tr>
				<tr>
					<td rowspan="3" style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Preprocessed</b>
					</td>
					<td style="width: 20%; text-align: center;">Text</td>
					<td colspan="2" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${tweetPrepropItem.get(loopSentiment.index)}" />
					</td>
				</tr>
				<tr>
					<td style="width: 20%; text-align: center;">Feature</td>
					<td colspan="2" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${tweetPrepropFeature.get(loopSentiment.index)}" />
					</td>
				</tr>
				<tr>
					<td style="width: 20%; text-align: center;">Complex</td>
					<td colspan="2" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${tweetPrepropComplex.get(loopSentiment.index)}" />
					</td>
				</tr>
				
				<tr>
					<td rowspan="6" style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Class distribution</b>
					</td>
					<td rowspan="2" style="width: 20%; text-align: center; vertical-align: middle;">
						Text
					</td>
					<td>Positive</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistText.get(loopSentiment.index)[0]}" />
					</td>					
				</tr>	
				<tr>
					<td>Negative</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistText.get(loopSentiment.index)[1]}" />
					</td>
				</tr>
				<tr>
					<td rowspan="2" style="width: 20%; text-align: center; vertical-align: middle;">
						Feature
					</td>
					<td>Positive</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistFeature.get(loopSentiment.index)[0]}" />
					</td>
				</tr>
				<tr>
					<td>Negative</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistFeature.get(loopSentiment.index)[1]}" />
					</td>
				</tr>
				<tr>
					<td rowspan="2" style="width: 20%; text-align: center; vertical-align: middle;">
						Complex
					</td>
					<td>Positive</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistComplex.get(loopSentiment.index)[0]}" />
					</td>
				</tr>
				<tr>
					<td>Negative</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistComplex.get(loopSentiment.index)[1]}" />
					</td>
				</tr>
				
				<tr>
					<td rowspan="3" style="width: 25%; text-align: center; vertical-align: middle;">
						<b>Classification stage</b>
					</td>
					<td style="width: 20%; text-align: center;">Polarity Classifier</td>
					<td colspan="2" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${predClass.get(loopSentiment.index)[0]}" />
					</td>
				</tr>
				<tr>
					<td style="width: 20%; text-align: center;">classifyOnSW</td>
					<td colspan="2" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${predClass.get(loopSentiment.index)[1]}" />
					</td>
				</tr>
				<tr>
					<td style="width: 20%; text-align: center;">classifyOnModel</td>
					<td colspan="2" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${predClass.get(loopSentiment.index)[2]}" />
					</td>
				</tr>
				
			</tbody>
			</table>
			
			<!-- MORE MATH BUTTON -->
			<!-- 
			<div class="moreMathButtonContainer" ng-click="initMoreMath(${loopSentiment.index})">
				More Math
			</div>
			-->
			
			<div class="moreMathIntro" style="margin-top: 50px; margin-bottom: 40px;">
				<h1>More math</h1>
			</div>
			
		<!-- MORE MATH -->
		<!-- <div class="moreMathContainer" ng-show="moreMathValidity()"> -->
		
		<div class="moreMathContainer">
		
			<table class="table table-hover" style="width: 100%; table-layout: fixed;">
			<tbody>
				<tr>
					<td rowspan="6" style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Score</b>
					</td>
					<td rowspan="2" style="width: 20%; text-align: center; vertical-align: middle;">
						Text
					</td>
					<td style="width: 20%; text-align: center;">Positive</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistText.get(loopSentiment.index)[0] * 31.07}" />
					</td>
				</tr>
				<tr>
					<td style="width: 20%; text-align: center;">Negative</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistText.get(loopSentiment.index)[1] * 31.07}" />
					</td>
				</tr>
				<tr>
					<td rowspan="2" style="width: 20%; text-align: center; vertical-align: middle;">
						Feature
					</td>
					<td style="width: 20%; text-align: center;">Positive</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistFeature.get(loopSentiment.index)[0] * 11.95}" />
					</td>	
				</tr>
				<tr>
					<td style="width: 20%; text-align: center;">Negative</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistFeature.get(loopSentiment.index)[1] * 11.95}" />
					</td>
				</tr>
				<tr>
					<td rowspan="2" style="width: 20%; text-align: center; vertical-align: middle;">
						Complex
					</td>
					<td style="width: 20%; text-align: center;">Positive</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistComplex.get(loopSentiment.index)[0] * 30.95}" />
					</td>	
				</tr>
				<tr>
					<td style="width: 20%; text-align: center;">Negative</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistComplex.get(loopSentiment.index)[1] * 30.95}" />
					</td>
				</tr>
				
				<c:set var="totalPosScore" value="${classDistText.get(loopSentiment.index)[0] * 31.07 + classDistFeature.get(loopSentiment.index)[0] * 11.95 + classDistComplex.get(loopSentiment.index)[0] * 30.95}" />
				<c:set var="avgPosScore" value="${totalPosScore / 73.97}" />
				<c:set var="totalNegScore" value="${classDistText.get(loopSentiment.index)[1] * 31.07 + classDistFeature.get(loopSentiment.index)[1] * 11.95 + classDistComplex.get(loopSentiment.index)[1] * 30.95}" />
				<c:set var="avgNegScore" value="${totalNegScore / 73.97}" />
				
				<!-- Representation value -->
				<c:set var="repValue" value="${(1 + avgPosScore - avgNegScore) / 2}" />
				
				<tr>	
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Total positive score</b>
					</td>
					<td colspan="3" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${totalPosScore}" />
					</td>
				</tr>
				
				<tr>
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Average positive score</b>
					</td>
					<td colspan="3" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${avgPosScore}" />
					</td>	
				</tr>
				
				<tr>	
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Total negative score</b>
					</td>
					<td colspan="3" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${totalNegScore}" />
					</td>
				</tr>
				
				<tr>
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Average negative score</b>
					</td>
					<td colspan="3" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${avgNegScore}" />
					</td>	
				</tr>
				
				<tr>
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Representation score</b>
					</td>
					<td colspan="3" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:out value="${repValue}" />
					</td>	
				</tr>
				
				<tr>
					<td rowspan="3" style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Lexicon classification</b>
					</td>
					<td rowspan="2" style="width: 20%; text-align: center; vertical-align: middle;">
						Class distribution
					</td>
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						Positive
					</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistLexicon.get(loopSentiment.index)[1]}" />
					</td>
				</tr>
				<tr>
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						Negative
					</td>
					<td style="text-align: center; max-width: 300px; word-wrap: break-word;">
						<c:out value="${classDistLexicon.get(loopSentiment.index)[2]}" />
					</td>
				</tr>
				<tr>
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						Predicted class
					</td>
					<td colspan="2" style="text-align: center; max-width: 400px; word-wrap: break-word;">
						<c:choose>
							<c:when test="${classDistLexicon.get(loopSentiment.index)[0] == 0}">
						    	<c:out value="positive" />
						  	</c:when>
							<c:otherwise>
								<c:out value="negative" />
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				
				<tr>
					<td rowspan="2" style="width: 20%; text-align: center; vertical-align: middle;">
						<b>Final state for Polarity Classifier</b>
					</td>
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						Condition
					</td>
					<td colspan="2" style="text-align: center; max-width: 350px; word-wrap: break-word;">
						<c:choose>
							<c:when test="${repValue < 0.5 && classDistLexicon.get(loopSentiment.index)[0] == 1}">
						    	<c:out value="representation is proportional to lexicon" />
						  	</c:when>
						  	<c:when test="${repValue > 0.5 && classDistLexicon.get(loopSentiment.index)[0] == 0}">
						  		<c:out value="representation is proportional to lexicon" />
						  	</c:when>
							<c:otherwise>
								<c:out value="representation is not proportional to lexicon (nan)" />
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td style="width: 20%; text-align: center; vertical-align: middle;">
						State
					</td>
					<td colspan="2" style="text-align: center; max-width: 350px; word-wrap: break-word;">
						<c:out value="${predClass.get(loopSentiment.index)[0]}" />
					</td>
				</tr>
			</tbody>
			</table>
		</div>
		
		</div>
	</c:forEach>
	
	</c:otherwise>
	
	</c:choose>
</div>

<!-- SEMANTIC AREA -->
<div class="testTweet" ng-show="selectedView === 2">
	
	<div class="titleContainer">
		<h1>Select Tweet ID</h1>
	</div>
	
	<div class="tweetIDContainer">
			
		<h2>ID: {{selectedID}}</h2>
		
		<select class="form-control" ng-model="selectedOptSemantic" ng-change="slctTweetIDSemantic()">
			
			<!-- ORIGIN LOGIC -->
			<c:choose>
			    <c:when test="${tweetNum > 0}">
					
					<c:forEach var="tweetIDSem" begin="0" end="${tweetNum-1}">
						<option><c:out value="${tweetIDSem}" /></option>	
					</c:forEach>
					        
			    </c:when>    
			    <c:otherwise>
			                
			    </c:otherwise>
			</c:choose>
			
		</select>	
	
	</div>
	
	<c:choose>
	
	<c:when test="${tweetNum == 0}">
		<div class="semanticContainer">
			<h1>Tweet not found</h1>
		</div>
	</c:when>
	
	<c:otherwise>
	<c:forEach var="itemSemantic" begin="0" end="${tweetNum-1}" varStatus="loopSemantic">
		
		<div class="semanticContainer" ng-show="selectedIDSemantic == ${loopSemantic.index}">
			
			<table class="table table-hover" style="width: 100%; table-layout: fixed;">
				<tbody>
					<tr>
						<td style="width: 30%; text-align: center;">
							<b>Preprocessed text</b>
						</td>
						<td colspan="2" style="text-align: center; max-width: 450px; word-wrap: break-word;">
							<c:out value="${tweetPrepropItem.get(loopSemantic.index)}" />
						</td>
					</tr>
						
					<!-- 0 -->	
					<tr>
						<td rowspan="20" style="width: 25%; text-align: center; vertical-align: middle;">
							<b>Class distribution</b>
						</td>
						<td style="width: 30%; text-align: center;">
							atheism
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[0]}" />
						</td>
					</tr>
						
					<!-- 1 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							computer graphics
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[1]}" />
						</td>
					</tr>
					
					<!-- 2 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							computer OS Windows MISC (various kinds of Windows)
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[2]}" />
						</td>
					</tr>
					
					<!-- 3 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							computer system IBM (PC / hardware)
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[3]}" />
						</td>
					</tr>
					
					<!-- 4 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							computer system MAC (hardware)
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[4]}" />
						</td>
					</tr>
					
					<!-- 5 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							computer OS Windows X
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[5]}" />
						</td>
					</tr>
					
					<!-- 6 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							MISC forsale (various kinds of goods to sale)
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[6]}" />
						</td>
					</tr>
					
					<!-- 7 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							recreational autos
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[7]}" />
						</td>
					</tr>
					
					<!-- 8 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							recreational motorcycles
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[8]}" />
						</td>
					</tr>
					
					<!-- 9 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							recreational sport baseball
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[9]}" />
						</td>
					</tr>
					
					<!-- 10 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							recreational sport hockey
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[10]}" />
						</td>
					</tr>
					
					<!-- 11 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							science cryptography
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[11]}" />
						</td>
					</tr>
					
					<!-- 12 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							science electronics
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[12]}" />
						</td>
					</tr>
					
					<!-- 13 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							science medical
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[13]}" />
						</td>
					</tr>
					
					<!-- 14 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							science space
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[14]}" />
						</td>
					</tr>
					
					<!-- 15 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							social religion christian
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[15]}" />
						</td>
					</tr>
					
					<!-- 16 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							talk politics (guns)
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[16]}" />
						</td>
					</tr>
					
					<!-- 17 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							talk politics (mideast)
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[17]}" />
						</td>
					</tr>
					
					<!-- 18 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							talk politics MISC (various kinds of politics talk)
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[18]}" />
						</td>
					</tr>
					
					<!-- 19 -->
					<tr>
						<td style="width: 30%; text-align: center;">
							talk religion MISC (various kinds of religion talk)
						</td>
						<td style="text-align: center;">
							<c:out value="${semanticPredAndDist.get(loopSemantic.index)[19]}" />
						</td>
					</tr>
					
					<tr>
						<td style="width: 30%; text-align: center;">
							<b>Semantic - topic</b>
						</td>
						<td colspan="2" style="text-align: center; max-width: 450px; word-wrap: break-word;">
							<c:out value="${semanticItem.get(loopSemantic.index)}" />
						</td>
					</tr>
					
				</tbody>
			</table>
			
		</div>
		
	</c:forEach>
	
	</c:otherwise>
	
	</c:choose>
</div>



<!-- VISUAL VIEW CONTAINER (PIE GRAPH) -->
<div class="testTweet" ng-show="selectedView === 3">
	
	<div class="titleContainer">
		<h1>Sentiment Chart</h1>
	</div>
	
	<c:choose>
	
	<c:when test="${tweetNum == 0}">
		<div class="chartContainer">
			<h1>Tweet not found</h1>
		</div>
	</c:when>
	<c:otherwise>
		<div class="chartContainer">
		
			<p>
				<img src="assets/img/PieChart.png" width="640" height="480" border="0" />
		   	</p>
			
		</div>
	</c:otherwise>
	
	</c:choose>
</div>
