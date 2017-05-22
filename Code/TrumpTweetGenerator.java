package twitterbotics;

import java.util.Vector;



public class TrumpTweetGenerator
{
	private static String CONSUMER_KEY    			= "2uNt8z17898089InRRXUIOKOJ";	
	private static String ACCESS_TOKEN 	  			= "830017898089414656-gyWwq178980894146W0J82N2fr";
	
	public static int TIME_BETWEEN_TWEETS			= 60*60*1000;

	private TweetLaunchpad launchpad      			= null;
	
	private ClockworkTrump trumpifier		  		= null;
	
	private String[] comparisonHandles				= {"@DarthVader", "@POTUS", "@Lord_Voldemort7", "@WarrenBuffett", "@Mike_Pence",
													   "@DeepDrumpf", "@DonaldDrumpf", "@TrumpScuttleBot", "@KellyannePolls",
													   "@EricTrump", "@DonaldJTrumpJr", "@IvankaTrump"};
	
	private Vector<String> comparisonSchedule		= new Vector<String>();
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    Constructor
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public TrumpTweetGenerator(TweetLaunchpad launchpad, String knowledgeDir)
	{
		this.launchpad 	   = launchpad;
		
		trumpifier  	   = new ClockworkTrump(knowledgeDir);	
		
		Dribbler.randomize(comparisonHandles);
		
		for (int i = 0; i < comparisonHandles.length; i++)
			comparisonSchedule.add(comparisonHandles[i]);
	}
	
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//    run() for Thread
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//

	public void startTweeting(int numTweets, long millisecondsBetweenTweets, Vector<String> context)
	{
		if (context != null)
			trumpifier.setContext(context);
		
		long lastTime = System.currentTimeMillis() - millisecondsBetweenTweets - 1;
		
		for (int i = 0; i < numTweets; i++)
		{		
			try {
				while (System.currentTimeMillis() < lastTime + millisecondsBetweenTweets)
				{
					try {Thread.sleep(10000);} catch (Exception e) {e.printStackTrace();}
				}
				
				try {Thread.sleep(3000);} catch (Exception e) {e.printStackTrace();}
				
				System.out.println("Awake after: " + (System.currentTimeMillis()-lastTime)/60000 + " minutes."); 
				
				Long tweetId = null;
				
				if (i % 12 == 0)
					tweetId = generateChartTweet();
				else
					tweetId = generateNextTweet();
						
				if (tweetId != null)
					lastTime = System.currentTimeMillis();	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	private Long generateChartTweet()
	{
		String nextComparison = comparisonSchedule.firstElement();
		
		comparisonSchedule.remove(0); // remove from schedule
		comparisonSchedule.add(nextComparison); // move back to the end
		
		AffectiveBarChart barchart = new AffectiveBarChart("@realDonaldTrump", nextComparison);
		
		String tweet = null;
		
		if (nextComparison == "@POTUS") {
			tweet = "Kellyanne crunched the numbers and compared my tweets on @realDonaldTrump to @POTUS tweets & " +
				       "the differences are UNPRESIDENTED:";
		}
		
		if (nextComparison == "@DarthVader") {
			tweet = "My #Failing critics say I have been seduced by the #DarkSide but @KellyannePolls's analysis proves " + 
					"they are #FakeNews:";
		}
		
		if (nextComparison == "@Lord_Voldemort7") {
			tweet = "#FakeNews CNN and #Failing NYTimes talk about me as if I were some " + trumpifier.getRandomDowner() +  
					" VILLAIN. The numbers prove them wrong:";
		}
		
		if (nextComparison == "@WarrenBuffett") {
			tweet = "I an NOT like other billionaires, and @KellyannePolls's analysis proves it with some alternate facts " +
					" about our tweets:";
		}
		
		if (nextComparison == "@DeepDrumpf") {
			tweet = "Some  " + trumpifier.getRandomDowner() + " imposters pretend to me on Twitter. #FakeNews! They can't " +
					" match me for tweets:";
		}
		
		if (nextComparison == "@DonaldDrumpf") {
			tweet = "Some  " + trumpifier.getRandomDowner() + " CLOWNS satirize me on Twitter but " + nextComparison + 
					" doesn't have my " + trumpifier.getRandomUpper() + " personality:";
		}

		if (nextComparison == "@TrumpScuttleBot") {
			tweet = "Critics doubt whether this account is operated by THE REAL DONALD. Kellyanne's numbers should prove who I am:";
		}
		
		if (nextComparison == "@KellyannePolls") {
			tweet = trumpifier.getRandomDowner() +" skeptics say that @KellyAnnePolls writes all my tweets. Well, Kellyanne's " +
					"numbers prove otherwise:";
		}
		
		if (nextComparison == "@Mike_Pence") {
			tweet = trumpifier.getRandomDowner() +" skeptics say that Mike Pence should take control of my Twitter! Mike's " +
				    trumpifier.getRandomUpper() + " but his tweets are nothing like mine:";
		}
		
		if (nextComparison == "@EricTrump") {
			tweet = "How alike are Eric and I, you ask? As alike as our tweeting styles. @KellyannePolls has crunched the numbers:";
		}
		
		if (nextComparison == "@DonaldJTrumpJr") {
			tweet = "How like me is Donald Jnr. you ask? As alike as two Skittles in a bowl! @KellyannePolls has crunched " + 
					"the Twitter numbers:";
		}
		
		if (nextComparison == "@IvankaTrump") {
			tweet = "Ivanka and I are very close! " + trumpifier.getRandomUpper() + " daughter! Though Kellyanne says our tweets " +
					" show marked differences:";
		}
		
		if (tweet == null) {
			tweet = "Kellyanne crunched the numbers and compared my tweets on @realDonaldTrump to @POTUS tweets & the differences" +
					" are " + trumpifier.getRandomDowner() + "!";
		}
		
		return launchpad.postTweetWithMedia(tweet, barchart.toInputStream(), null);
	}
	
	
	
	
	private Long generateNextTweet()
	{
		Vector<String> tweets = trumpifier.getRandomTrumpisms();	
		
		String bestCandidate  = tweets.firstElement();
		
		int leastRecency      = 1000000000;
		
		for (String tweet: tweets) 
		{			
			int recency = launchpad.getRecencyOfTheme(tweet);
			
			if (recency < leastRecency) {
				bestCandidate = tweet;
				leastRecency  = recency;					
			}
		}
		
		bestCandidate =  Dribbler.replaceWith(bestCandidate, "_", " ");
		
		if (bestCandidate.length() < 135) bestCandidate = bestCandidate + "\n#MAGA";
		
		System.out.println("\nAbout to tweet: " + bestCandidate + " (" + bestCandidate.length() + " characters)");
				
		return launchpad.postTweet(bestCandidate);
	}
	
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	//     Main Application Stub
	//--------------------------------------------------------------------------------------------------//
	//--------------------------------------------------------------------------------------------------//
	
	// In Documents/Workspace/WordNetBrowser
		
	
	public static void main(String[] args)
	{
		String kdir   = "/Users/tonyveale/Dropbox/CodeCamp2016/NOC/DATA/TSV Lists/";
		
		TweetLaunchpad launchpad = new TweetLaunchpad(CONSUMER_KEY, 
													  args[0], // CONSUMER_SECRET, 
													  ACCESS_TOKEN, 
													  args[1]); // ACCESS_SECRET;
		
		TrumpTweetGenerator theDonald = new TrumpTweetGenerator(launchpad, kdir);
		
		theDonald.startTweeting(100000000, TIME_BETWEEN_TWEETS, launchpad.analyzeTimeline("trumpscuttlebot", 10));
	}
			
}