package twitterbotics;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class ClockworkTrump 
{
	public static Random RND 					= new Random();
	
	public static int CONTEXT_KEYHOLE			= 8;
	
	public static String[] EVENT_PREPOSITIONS	= {"For", "With", "Against", "About", "Of", "On"};
	
	public static String[] EVENT_VERBS			= {"create", "enjoy", "commit", "build", "hire", "teach", "sell", "conduct", "perform", "join",
												   "study", "specialize_in", "manage", "run", "spread", "work_in", "suffer_from", 
												   "manipulate", "live_in", "lead", "serve", "follow", "inherit", "buy", "support",
												   "win", "lose", "advise", "are_exploited_by", "obey", "overcome", "control",
												   "profit_from"};
	
	public static String[] TARGET_MEDIA			= {"failing NYTimes", "FAKE NEWS CNN", "Meet the Press", "60 Minutes", "Mess-NBC"};
	
	public static String[] INFO_AGENCIES		= {"CIA", "FBI", "NSA", "Justice Dept."};
	
	
	public static String[] TRUMP_DOWNERS		= {"DREADFUL", "DUMB", "SICK", "STUPID", "TERRIBLE", "DISGRACEFUL", "REGRETTABLE", 
												   "SAD", "DISGUSTING", "#FAILING", "Sad (or Sick?)"};

	public static String[] TRUMP_UPPERS			= {"SUPER", "HUGE", "GREAT", "TERRIFIC", "FANTASTIC", "WONDERFUL", "LEGENDARY", 
												   "BRILLIANT", "TREMENDOUS"};

	public static String[] SUPERLATIVE_UPPERS	= {"BEST", "BIGGEST", "GREATEST", "most TERRIFIC", "most FANTASTIC", "most WONDERFUL", 
												   "most LEGENDARY", "most BRILLIANT", "most TREMENDOUS"};

	public static String[] SUPERLATIVE_DOWNERS	= {"most DREADFUL", "DUMBEST", "SICKEST", "STUPIDEST", " most TERRIBLE", 
												   "most DISGRACEFUL", "most REGRETTABLE", "SADDEST", "most DISGUSTING", "WORST"};
	
	public static String[] TARGET_NATIONALITIES = {"Mexican", "Chinese", "foreign", "migrant", "radical", "ISIS", "extremist", "undocumented"};
	
	public static String[] TRUMP_EPITHETS		= {"are LOSERS", "are SAD", "are SO SAD", "are the WORST", "are bad hombres", 
												   "supported Hillary", "are swamp dwellers", "are low-lifes", "are haters", "" +
												   "are un-American", "stink", "are not like us", "have small hands", "hate America",
												   "hate the USA", "hate US", "spit on the constitution", "just want your cash",
												   "are losers, big-league", "are good? Give ME a BREAK", "hate America",
												   "carry germs", "spread disease", "steal our jobs", "are bums", "are FIRED!",
												   "deserve what they get", "are unpresidented losers", "will rip you off", 
												   "are LOW-energy", "cannot be trusted", "make me sick", "make me wanna puke",
												   "get lousy ratings", "pay no tax", "rip off America", "lean Democrat",
												   "have fat fingers", "sweat too much", "have no morals", "hate freedom",
												   "are over-rated", "were coddled by Obama", "love ObamaCare", "crippled America",
												   "just want hand-outs", "hate our way of life", "are greasier than Don Jnr.'s hair",
												   "spread fake news", "keep America weak", "rig elections", "watch CNN",
												   "read failing NYTimes", "hate democracy", "fear my VIGOR", "are BAD people",
												   "tell more lies than Ted Cruz", "are more crooked than Hillary",
												   "are SO DANGEROUS", "have no soul", "are terrible golfers",
												   "are worse than Rosie O'Donnell", "are no beauty queens", "are meaner than Hillary",
												   "run private email servers", "have bad hair", "are TOO tanned"
												  };
	
	public static String[] TRUMP_COMPLIMENTS	= {"are the BEST", "are TREMENDOUS", "are TERRIFIC", "make others look SAD",
												   "love USA", "love the constitution", "will drain the swamp", "will fund my wall",
												   "are winners", "love ME", "are great, BELIEVE ME", "are bad? Give ME a BREAK",
												   "are HIGH energy", "are real troopers", "are great guys", "are real patriots",
												   "put America first", "will build my wall", "would die for me", "pay taxes",
												   "vote Republican", "have manly hands", "get MY vote", "have no equal",
												   "need our support", "need the work", "deserve our support", "buy Ivanka's clothes",
												   "love Ivanka's clothes", "stay in Trump hotels", "wear #MAGA hats",
												   "hate Obamacare", "follow me on Twitter", "watch FOXNews", "love Melania",
												   "remember 9/11", "are FANTASTIC", "read Breitbart news", "were made in USA"
												  };
	
	
	private KnowledgeBaseModule EVENT_ROLES		=	null;
	
	private Hashtable verbsToEvents				= 	null;
	
	private Vector<String> scheduledFields		=	new Vector<String>(); // the order in which fields are tweeted about

	private Vector<String> contextTweets		=	new Vector<String>(); // the most recent tweets generated with this object	

	private Vector<String> scheduledVerbs		=	new Vector<String>(); // run through available verbs in some sequence
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	
	public ClockworkTrump(String kdir)
	{
		EVENT_ROLES = new KnowledgeBaseModule(kdir + "Action frames and roles.idx", 0);
		
		verbsToEvents = EVENT_ROLES.getInvertedField("Action");
		
		Dribbler.randomize(EVENT_PREPOSITIONS);
		Dribbler.randomize(EVENT_VERBS);
		
		for (int i = 0; i < EVENT_PREPOSITIONS.length; i++)  {
			scheduledFields.add("<empty>");
			scheduledFields.add(EVENT_PREPOSITIONS[i]);
		}
		
		for (int i = 0; i < EVENT_VERBS.length; i++) 
			scheduledVerbs.add(EVENT_VERBS[i]);
	}
	
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Generate a Trump-like burst of nonsense
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String getRandomTrumpism()
	{	
		Vector<String> trumpisms = getRandomTrumpisms();
		
		if (trumpisms.size() == 0) 
			return null;
		
		if (trumpisms.size() == 1) 
			return Dribbler.replaceWith(trumpisms.firstElement(), "_", " ");
		
		return Dribbler.replaceWith(trumpisms.elementAt(RND.nextInt(trumpisms.size())), "_", " ");
	}
	
	
	public Vector<String> getRandomTrumpisms()
	{
		Vector<String> eventPool = EVENT_ROLES.getAllFrames();
		
		String eventField = scheduledFields.firstElement(), eventAction = "<empty>";
		
		if (eventField == "<empty>") {
			eventAction = scheduledVerbs.firstElement();
			eventPool   = (Vector<String>)verbsToEvents.get(eventAction);
			
			scheduledVerbs.add(scheduledVerbs.firstElement());
			scheduledVerbs.remove(0);
		}
		
		//System.out.println(eventAction);
		
		String event = eventPool.elementAt(RND.nextInt(eventPool.size()));
		
		scheduledFields.remove(0);  // once this field is up next
		scheduledFields.add(eventField); // cycle to end of the schedule
		
		//System.out.print(eventField + ":" + event);
	
		Vector<String> trumpisms = getRandomTrumpisms(event, eventField);
		
		while (trumpisms.size() == 0) {
			event     = eventPool.elementAt(RND.nextInt(eventPool.size()));
			
			trumpisms = getRandomTrumpisms(event, eventField);
		}
		
		//System.out.println(",  " + eventField + ":" + event + " = " + trumpisms.size());
		
		return trumpisms;
	}
	
	
	public Vector<String> getRandomTrumpisms(String event, String field)
	{
		Vector<String> trumpisms = new Vector<String>();
		
		String subjects = EVENT_ROLES.getFirstValue("Subjects", event);
		String   action = EVENT_ROLES.getFirstValue("Action", event);
		String  objects = EVENT_ROLES.getFirstValue("Objects", event);
		
		if (field == "<empty>") 
			addTweetsForSpecificRoles(subjects, action, objects, field, trumpisms);
		else
		if (field == "For")
			addTrumpismsFor(subjects, action, objects, EVENT_ROLES.getFieldValues("For", event), trumpisms);
		else
		if (field == "Against")
			addTrumpismsAgainst(subjects, action, objects, EVENT_ROLES.getFieldValues("Against", event), trumpisms);
		else
		if (field == "With")
			addTrumpismsWith(subjects, action, objects, EVENT_ROLES.getFieldValues("With", event), trumpisms);
		else
		if (field == "About")
			addTrumpismsAbout(subjects, action, objects, EVENT_ROLES.getFieldValues("About", event), trumpisms);
		else
		if (field == "Of")
			addTrumpismsOf(subjects, action, objects, EVENT_ROLES.getFieldValues("Of", event), trumpisms);
		else
		if (field == "On")
			addTrumpismsOn(subjects, action, objects, EVENT_ROLES.getFieldValues("On", event), trumpisms);
		
		return trumpisms;
	}
	
	
	
	
	private void addTrumpismsFor(String subjects, String action, String objects, 
								 Vector<String> roles, Vector<String> trumpisms)
	{
		if (roles == null || roles.size() == 0) return;
		
		String target = getRandomTarget() + " ";
		
		for (String role: roles) {
			
			addTweetsForSpecificRoles(subjects, action, objects, role, trumpisms);
			
			if (action == "do") {
				
				addTweet("American " + subjects + " do " + getRandomUpper() + " " + objects + "!\nYet some American " +
						 role + " still hire " + getRandomDowner() + " " + target + subjects + " (who " + getRandomEpithet() + ")",
						trumpisms);
				
				continue;
			}

			addTweet("I'm SMARTER than other " + role + "!\nI'll get the best deal from the " + subjects + " we hire to " + 
					  action + " " + objects + ", BELIEVE ME!",
					  trumpisms);
			
			addTweet("If you need " + subjects + " to " + action + " " + objects + " for YOU, hire AMERICAN. " + 
					  Dribbler.capitalizeFirst(target) + subjects + " " + getRandomEpithet() + "!", 
					  trumpisms);
			
			addTweet("If you need " + subjects + " to " + action + " " + objects + " for YOU, why not hire AMERICAN? " + 
					  "Our " + subjects + " " + getRandomCompliment() + "!", 
					  trumpisms);

			addTweet("#AmericaFirst means U.S. " + subjects + " should " + action + " " + objects + " for AMERICAN " + role + "! " +
					  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!", 
					  trumpisms);
			
			addTweet("Am I wrong? American " + subjects + " MUST " + action + " " + objects + " for AMERICAN " + role + "! " +
					  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!", 
					  trumpisms);
			
			addTweet("I wish American " + subjects + " would " + action + " " + objects + " for AMERICAN " + role + "! " +
					  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!", 
					  trumpisms);
			
			addTweet("I will tax American " + subjects + " who " + action + " " + objects + " for " + target + role + "! " +
				      Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!",
				      trumpisms);

			addTweet("We must reward " + subjects + " who " + action + " " + objects + " for AMERICAN " + role + "! " +
				      Dribbler.capitalizeFirst(target)  + role + " " + getRandomEpithet() + "!",
				      trumpisms);

			addTweet("American " + subjects + " should " + action + " " + objects + " for AMERICAN " + role + "! " +
				     "Or Russian " + role + " -- they " + getRandomCompliment() + " too",
				     trumpisms);		
			
			addTweet("I will deport " + target + subjects + " who " + action + " " + objects + " for AMERICAN " + role + "! " +
					 Dribbler.capitalizeFirst(target) + subjects + " " + getRandomEpithet() + "!",
					 trumpisms);

			addTweet("American " + subjects + " should NEVER " + action + " " + objects + " for " + target + role + "! " +
				     "American " + role + " " + getRandomCompliment() + "!",
				     trumpisms);

			addTweet("To pay for My Wall, " + 
					 "I will tax U.S. " + subjects + " who " + action + " " + objects + " for " + target + role + ". ",
					 trumpisms);
					
		}
	}
				

	private void addTrumpismsAgainst(String subjects, String action, String objects, 
									 Vector<String> roles, Vector<String> trumpisms)
	{
		if (roles == null || roles.size() == 0) return;
		
		String target = getRandomTarget() + " ";
		
		for (String role: roles) {
			
			if ("enemies".equals(role)) role = "citizens";
			
			addTweetsForSpecificRoles(subjects, action, objects, role, trumpisms);
			
			addTweet("Would I prefer to appear on 'FOX & Friends' or 'CNN & " + Dribbler.capitalizeEach(subjects) + "'? That's EASY: " +
					 "CNN covers up " + getRandomDowner() + " " + objects + " against American " + role + "!",
					 trumpisms);
			
			addTweet("To critics who demand 'evidence' for my claims that " + target + subjects + " come here to " + action + 
					 " " + objects +  " against AMERICANS, I say OPEN YOUR EYES!",
					 trumpisms);

			addTweet("The critics who doubt my SECRET PLAN for defeating " + target + subjects + " -- who " + action + " " + 
					 getRandomDowner() + " " + objects + " against us -- " + getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("Still waiting for protests at " + target + subjects + " who " + action + " " + objects + 
					 " against U.S. " + role + ".\nThose " + subjects + " " + getRandomEpithet(),
					 trumpisms);
			
			addTweet("Still waiting for Seth Meyers to take 'A Closer Look' at " + target + subjects + " who " + action + " " + 
					 objects + " against American " + role + ".\nOne-Sided!",
					 trumpisms);
			
			addTweet("The media won't report on the " + target + subjects + " who " + action + " " + objects + 
					 " against American " + role + ". DISHONEST! They " + getRandomEpithet(),
					 trumpisms);
			
			addTweet("Reports of U.S. " + subjects + " who " + action + " " + objects + 
					 " against " + target + role + " are FAKE NEWS! Reporters " + getRandomEpithet(),
					 trumpisms);
			
			addTweet("I HATE American " + subjects + " who " + action + " " + objects + " against other Americans! " +
					 "Target " + target + role + ": they " + getRandomEpithet(),
					 trumpisms);
			
			addTweet("Hats off to the American " + subjects + " who " + action + " " + objects + " against " + target + role + "! " +
					 Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet(),
					 trumpisms);
			
			addTweet("I APPLAUD American " + subjects + " who " + action + " " + objects + " against " + target + role + "!\n" +
					 "OUR " + role + " " + getRandomCompliment(),
					 trumpisms);

			addTweet("Why do American " + subjects + " " + action + " " + objects + " against AMERICAN " + role + "? " +
					  "Traitors! U.S. " + role + " " + getRandomCompliment() + "!",
					  trumpisms);		

			addTweet( Dribbler.capitalizeFirst(target)  + role + " " + getRandomEpithet() + "!\n" +
					 "American " + subjects + " can easily " + action + " " + objects + " against " + target + role,
					  trumpisms);	
		
			addTweet("I will protect American " + role + " from " + target + subjects + " who " + action + " " + objects + "! " +
					  Dribbler.capitalizeFirst(target)  + subjects + " " + getRandomEpithet() + "!",
					  trumpisms);
			
			addTweet("American " + role + " must be saved from " + target + subjects + " who " + action + " " + objects + "! " +
					  "U.S. " + role + " " + getRandomCompliment() + "!",
					  trumpisms);
			
			addTweet("I will ban " + target + subjects + " who want to " + action + " " + objects + " against American " + role + ".\n" +
					  "They " +  getRandomEpithet()  + " and WILL be stopped",
					  trumpisms);
					 
		}
	}

	
	private void addTrumpismsWith(String subjects, String action, String objects, 
								  Vector<String> roles, Vector<String> trumpisms)
	{
		if (roles == null || roles.size() == 0) return;
		
		String target = getRandomTarget() + " ";
		
		for (String role: roles) {
			
			addTweetsForSpecificRoles(subjects, action, objects, role, trumpisms);
		
			addTweet("I will #DrainTheSwamp of American " + subjects + " who " + action + " " + objects + 
					 " with " + target + role + ". Those " + subjects + " " + getRandomEpithet(),
					 trumpisms);
			
			addTweet("The Carnage Ends Now! " +  Dribbler.capitalizeFirst(subjects) + ", choose American " + role + " to " + 
					 action + " " + objects + " with! " +  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet(),
					 trumpisms);
			
			addTweet("Americans make the best " + role + ". So why do some " + subjects + " " + action + " " + objects + " with " + target + role + "?\n" +
					 Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("Why won't more American " + subjects + " " + action + " " + objects + " with AMERICAN " + role + "? " +
				     "OUR " + role + " " + getRandomCompliment(),
				     trumpisms);
			
			addTweet("Why won't more American " + subjects + " " + action + " " + objects + " with AMERICAN " + role + "? " +
					 Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!",
				     trumpisms);
			
			addTweet("I wish American " + subjects + " would " + action + " " + objects + " with AMERICAN " + role + "! " +
					  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!", 
					  trumpisms);
		}
	}
	
	
	
	
	private void addTrumpismsOf(String subjects, String action, String objects, 
								Vector<String> roles, Vector<String> trumpisms)
	{
		if (roles == null || roles.size() == 0) return;
		
		String target = getRandomTarget() + " ";
		
		for (String role: roles) {
			
			addTweetsForSpecificRoles(subjects, action, objects, role, trumpisms);

			addTweet("#BuildTheWall so " + target + subjects + " no longer " + action + " " + objects + 
					  " of U.S. " + role + "!\nAmerican " + subjects + " for AMERICAN " + role + "!",
					  trumpisms);
			
			addTweet("Americans make great " + role + ". Yet so many " + subjects + " " + action + " the " + objects + " of " + target 
					 + role + "!\n" +  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("Why don't American " + subjects + " " + action + " the " + objects + " of AMERICAN " + role + "? " +
				     "OUR " + role + " " + getRandomCompliment(),
				     trumpisms);
			
			addTweet("Why won't American " + subjects + " " + action + " the " + objects + " of AMERICAN " + role + "? " +
					 Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!",
				     trumpisms);
			
			addTweet("I wish American " + subjects + " would " + action + " the " + objects + " of AMERICAN " + role + "! " +
					  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!", 
					  trumpisms);
		}
	}	
	
	
	
	private void addTrumpismsOn(String subjects, String action, String objects, 
								Vector<String> roles, Vector<String> trumpisms)
	{
		if (roles == null || roles.size() == 0) return;
		
		String target = getRandomTarget() + " ";
		
		for (String role: roles) {
			
			addTweetsForSpecificRoles(subjects, action, objects, role, trumpisms);

			addTweet("#BuildTheWall so " + target + subjects + " no longer " + action + " " + objects + 
					  " on U.S. " + role + "!\nAmerican " + subjects + " for AMERICAN " + role + "!",
					  trumpisms);

			addTweet("Americans make great " + role + "! But so many " + subjects + " " + action + " " + objects + " on " + target 
					 + role + "!\n" +  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("Not enough " + subjects + " " + action + " " + objects + " on AMERICAN " + role + "!\n" +
				     "American " + role + " " + getRandomCompliment() + ", " + target + role + " " + getRandomEpithet(),
				     trumpisms);
			
			addTweet("Why won't American " + subjects + " " + action + " more " + objects + " on AMERICAN " + role + "? " +
					 Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!",
				     trumpisms);
			
			addTweet("I wish American " + subjects + " would " + action + " more " + objects + " on AMERICAN " + role + "! " +
					  Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!", 
					  trumpisms);
		}
	}
	
	
	
	
	private void addTrumpismsAbout(String subjects, String action, String objects, 
								   Vector<String> roles, Vector<String> trumpisms)
	{
		if (roles == null || roles.size() == 0) return;
		
		String target = getRandomTarget() + " ";
		
		for (String role: roles) {
			
			addTweet("Another day, another biased report by media " + subjects + " who " + action + " " + getRandomDowner() + 
					 " " + objects + " about American " + role + ". Shame on You!", 
					  trumpisms);
			
			addTweet("Build my WALL so " + target + subjects + " no longer " + action + " " + objects + 
					  " about U.S. " + role + "!\nAmerican " + subjects + " for AMERICAN " + role + "!",
					  trumpisms);

			addTweet("American " + role + " are good enough for AMERICAN " + subjects + " to " + action + " " + objects + " about! " +
					 Dribbler.capitalizeFirst(target) + role + " " + getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("American " + subjects + " should only " + action + " " + objects + " about U.S. " + role + "! " +
				     "Our " + role + " " + getRandomCompliment(),
				     trumpisms);
		}
	}
	
	
	private void addTweetsForSpecificRoles(String subjects, String action, String objects, String role, Vector<String> trumpisms)
	{
		if (role.equals(subjects) || role.equals(objects)) return;
		
		String target = getRandomTarget() + " ";
		
		if (role != "<empty>" && RND.nextInt(4) == 1)
		{
			addTweet("I will reform NAFTA so U.S. " + subjects + " can cheaply " + action + " the " + 
					 getRandomSuperlativeUpper() + " " + objects + "! Go to Canada if you want " +
					 getRandomDowner() + " " + objects + "!",
					 trumpisms);
		
			addTweet("Reform NAFTA so American " + subjects + " get a " + getRandomUpper() + 
					 " way to cheaply " + action + " the " + getRandomSuperlativeUpper() + " " + objects + "! Mexico is full of " +
					  getRandomDowner() + " " + subjects + "!",
					  trumpisms);
			
			addTweet("Even when U.S " + subjects + " go BAD and " + action + " the " + getRandomSuperlativeDowner() + " " + objects +
					 " they're still better than " + target + subjects + ", who " + getRandomEpithet() + "!",
					 trumpisms);
		}
		
		if ("accomplices".equals(role))
		{
			addTweet("Reports in NYTimes that U.S. troops aided " + target + subjects + " to " + action + " " + getRandomDowner() +
					 " " + objects + " are FAKE NEWS!\nNYT reporters " + getRandomEpithet(),
					trumpisms);
			
			addTweet("When " + target + subjects + " " + action + " their " + objects + " and America counts the cost, " +
					 "the FAKE NEWS media are willing accomplices. " + getRandomDowner() + "!",
					 trumpisms);
		}
		
		
		if ("dupes".equals(role)) {
			addTweet("The '" + subjects + "' in the FAKE NEWS media continue to " + action + " " + getRandomDowner() + " " + objects +
					 " and treat the American public as fools.",
					 trumpisms);		
		}
		
		
		if ("victims".equals(role))
		{
			addTweet("Fear of " + target + subjects + " is as RATIONAL as fear of the " + getRandomDowner() + " " + objects + 
					" that these " + subjects + " (who " +  getRandomEpithet() + ") " + action + "!",
					trumpisms);
			
			addTweet("The critics who question my SECRET PLAN for defeating " + target + subjects + " -- who " + action + " " + 
					 getRandomDowner() + " " + objects + " -- " + getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("To critics who want evidence for my claims that " + target + subjects + " are flooding in to " + action + 
					 " their " + objects + " I say OPEN YOUR EYES!",
					 trumpisms);
			
			addTweet("Reports on @CNN that American troops aided " + target + subjects + " to " + action + " " + getRandomDowner() + 
					" " + objects + " are FAKE NEWS!\nCNN Reporters " + getRandomEpithet(),
					trumpisms);
			
			addTweet("So many " + target + subjects + " pouring across non-existent border to " + action + " " + objects +
					 " and hurt AMERICANS! #BuildTheWall NOW!",
					trumpisms);
			
			addTweet("Our legal system is broken! " +  Dribbler.capitalizeFirst(target)  + subjects + " come into our country to " + 
					 action + " " + objects + " and hurt U.S. citizens. WE MUST ACT NOW!!",
					trumpisms);		
			
			addTweet("I've just met the brave American victims of " + target + subjects + " and their " + getRandomDowner() +
					 " " + objects + ".\nThis Carnage must end NOW!",
					 trumpisms);
			
			addTweet("The LYING media are HYPER-critical of me yet so nice to the " + target + subjects + " who " + action + " " + 
					 getRandomDowner() + " " + objects + " to hurt us all! FAKE NEWS!",
					 trumpisms);
			
		}
		

		if ("companions".equals(role) || "friends".equals(role) || "comrades".equals(role) || "colleagues".equals(role)  
				|| role.endsWith("mates") ||  role.endsWith("buddies"))
		{
			addTweet("Just met group of " + role + " at Mar-a-Lago (Winter White House) to discuss " + 
					 "the " + objects + " we will " + action + " together.\n" + Dribbler.capitalizeEach(subjects) + " LOVE me!",
					 trumpisms);			
		}
		
		
		if (action.equals("hire"))
		{
			addTweet("Here's what patriotic American " + subjects + " MUST say to the " + target + objects + 
					 " (who " + getRandomEpithet() + "!) on their payroll: " + "You're FIRED!",
					 trumpisms);
			
			addTweet("If only the " + subjects + " who need to hire " + objects + " would hire AMERICAN " + objects + 
					 " we could put this " + getRandomUpper() + " country back to WORK!",
					 trumpisms);
		}
		
		
		if (action.equals("teach"))
		{
			addTweet("My new Trump University will hire " + getRandomUpper() + " AMERICAN " + subjects + " to teach the BEST " + 
					 objects + "! " + Dribbler.capitalizeEach(target) + subjects + " " + getRandomEpithet(),
					 trumpisms);
		}
		
		
		if (action.equals("enjoy")) 
		{
			addTweet("I enjoy " + objects + " as much as other American " + subjects + " but FAKE NEWS media " +
					 "present this as a big deal. So Dishonest!",
					 trumpisms);	
			
			addTweet("Next thing you know the Dems will want me to de-criminalize un-American " + objects + 
					 "!\nThey're in the pockets of " + target + Dribbler.capitalizeFirst(subjects) +"! So " + getRandomDowner() + "!", 
					 trumpisms);
		}
		
		
		if (action.equals("commit")) {
			
			addTweet("Hillary called my supporters a 'basket of " + subjects + "' but it is Hillary and the DEMs that commit the " +
					 getRandomSuperlativeDowner() + " " + objects + "! " + getRandomDowner() + "!",
					 trumpisms);
			
			addTweet("Is it SO " + getRandomDowner() +  " that I want Russia to help us defeat the " + target + subjects +
					 " who commit such " + getRandomDowner() + " " + objects + " against America?",
					 trumpisms);
			
			addTweet("The " + Dribbler.capitalizeFirst(subjects) + " in FAKE NEWS media ignore their own " + objects + 
					 " while inventing " +  getRandomDowner() + " " + objects + " to place at my door. They will pay.",
					 trumpisms);	
			
			addTweet("#FakeNews @CNN accuses me of " + getRandomDowner() + " " + objects + " while kissing up to the real " + subjects + 
					 "! Shut them DOWN and Lock them UP!",
					 trumpisms);
			
			addTweet("#Failing NYTimes accuses me of " + getRandomDowner() + " " + objects + " while kissing up to the real " + subjects + 
					 "! Well, they don't kiss up TO ME!",
					 trumpisms);
			
			addTweet("We will protect Americans from " + target + subjects + " and their " + getRandomDowner() + " " + objects + ".\n" +
					 "Say what you will, American " + subjects + " " + getRandomCompliment() + "!",
					 trumpisms);	
			
			addTweet("I would have won the popular vote too if half the " + target + subjects + " who " + action + " " + objects +
					 " voted for me instead of Hillary. So " + getRandomDowner() + "!", 
					 trumpisms);
			
			addTweet("The LYING media are HYPER-critical of me yet so forgiving of the " + target + subjects + " who commit " + 
					 getRandomDowner() + " " + objects + " against Americans! FAKE NEWS!",
					 trumpisms);
			
			addTweet("Would I prefer to appear on 'FOX & Friends' or 'CNN & " + Dribbler.capitalizeEach(subjects) + "'? That's a NO-BRAINER: " +
					 "CNN reporters cover up " + getRandomDowner() + " " + objects + "!",
					 trumpisms);
			
			addTweet("Would I prefer to appear on 'FOX & Friends' or 'CNN & " + Dribbler.capitalizeEach(subjects) + "'? NO CONTEST: " +
					 "CNN reporters accuse ME of " + getRandomDowner() + " " + objects + "!",
					 trumpisms);
			
			addTweet("The " + getRandomDowner() + " Media is a bigger enemy of the American people than the " + target +
					 subjects + " coming to hurt us with their " + objects + ".",
					 trumpisms);
		}
		
		
		if (action.equals("create")) {
			addTweet("I will levy HEAVY taxes on U.S. " + subjects + " who move production of their " + objects + 
					 " outside the country.\n#AmericaFIRST",
					 trumpisms);		
			
			addTweet("Heads up, unpatriotic " + subjects + "! If you make your " + objects + " OUTSIDE the U.S. " +
					 "I'll tax you BIGLY to bring them back INSIDE the U.S.",
					 trumpisms);
		}
		
		
		if (action.equals("spread")) {
			String agency =  getRandomAgency();
			
			addTweet("The " + agency + " leaks our secrets the way " + subjects + " spread their " + 
					  objects +"! It MUST STOP! " + agency + " agents " + getRandomEpithet(), 
					 trumpisms);
			
			addTweet("The " + agency + " is full of '" + subjects + "' who " + action + " their " + objects + " by leaking to " +
					 getRandomMedia() + "! STOP IT! You " + getRandomEpithet() +  "!",
					 trumpisms);
		}
		
		
		if (action.equals("suffer_from")) {
			
			addTweet("When I #RepealAndReplace ObamaCare, " + subjects + " who suffer from pre-existing " + objects + " will get " +
					 getRandomUpper() + " cover + LOWER premiums, BELIEVE ME!",
					 trumpisms);
			
			addTweet("The #FakeNews media say I suffer from " + objects + " but they are the " + getRandomDowner() + " " + subjects +
					 ", not me. So " + getRandomDowner() + "!",
					 trumpisms);
		}
		
		
		if (action.equals("work_in")) {
			addTweet("There are " + subjects + " in " + objects + " all over the USA who elected me to stop " + 	
					 target + subjects + " from taking their jobs! Put #AmericaFirst",
					 trumpisms);
		}
		
		
		if (action.equals("live_in")) {
			addTweet("The media says my Treasury guy foreclosed on 1000s of " + subjects + "' " + objects + ". But the " +
					 objects + " were " + getRandomDowner() + " and the " + subjects + " " + getRandomEpithet(),
					 trumpisms);
			
			addTweet("To the " + subjects + " who lost their " + objects + " in the Obama era: I will build " + 
					 getRandomUpper() + " NEW " + objects +  " for you to live in!",
					 trumpisms);
		}
		
		
		if (action.equals("lead")) {
			addTweet("I come from a long line of " + getRandomUpper() + " " + subjects + " who led a long line of " + 
					 getRandomUpper() + " " + objects + ". The lying media don't tell you THAT!",
					 trumpisms);
		}
		
		
		if (action.equals("serve")) {
			addTweet("Our lying newspapers serve their readers the way " + getRandomDowner() + " " + subjects + " serve their " +
					 objects + "! We deserve BETTER!",
					trumpisms);
			
			addTweet("Our FAKE NEWS networks serve their viewers the way " + getRandomDowner() + " " + subjects + " serve their " +
					 objects + "! AMERICA deserves MORE!",
					trumpisms);
			
			addTweet("When in power the DEMs acted like " + getRandomDowner() + " " + objects + " and treated voters as " + subjects + " to serve THEM!" +
			        " Well I am here to serve YOU!",
			        trumpisms);
		}
		
		
		if (action.equals("manage")) {
			addTweet("If " + subjects + " don't criticize the way I run the country I won't slam the way they manage their " +
					 objects + " unless they " + getRandomEpithet(),
					 trumpisms);
		}
		
		
		if (action.equals("run")) {
			addTweet("If " + subjects + " don't criticize the way I run the country I won't slam the way they run their " +
					 objects + " (unless they are truly " + getRandomDowner() + ")",
					 trumpisms);
			
			addTweet("My administration is a WELL-OILED machine! I model myself on the " + getRandomUpper() + " " + subjects + 
					 " who have always run the most " + getRandomUpper() + " " + objects + "!",
					 trumpisms);
			
			addTweet("To the #Failing " + subjects + " who tell ME how to run the country: Maybe I should run your " + 
					getRandomDowner() + " " + objects + " for you to make them " + getRandomUpper() + " again!",
					trumpisms);
		}
		
		
		if (action.equals("control")) {
			addTweet("The White House is running VERY WELL. I exercise as much control over my appointees as " + subjects + 
					" do over their " + objects + "!",
					trumpisms);		
			
			addTweet("I inherited a " + getRandomDowner() + " MESS! But I am in CHARGE now and I have" + 
					" as much control over my guys as " + subjects + " have over their " + objects + "!",
					trumpisms);	
		}
		
		
		if (action.equals("build")) {
			addTweet("You elected me because I build " + getRandomUpper() + " things! And with me as POTUS, " + subjects + 
					 " will build " + getRandomUpper() + " " + objects + " all over America!",
					 trumpisms);
			
			addTweet("Before Obama we Americans were " + getRandomUpper() + " " + subjects + ". Under me we will build " + 
					 getRandomUpper() + " " + objects + " again, BELIEVE ME!",
					 trumpisms);
		}
		
		
		if (action.equals("manipulate")) {
			addTweet("The " + getRandomDowner() + " Media manipulate " + getRandomUpper() + " Americans with FAKE NEWS the way " + subjects + 
					" manipulate their " + objects + "!",
					trumpisms);
		}
		
		
		if (action.equals("join")) {
			addTweet("I will give tax breaks to American " + objects + " so they can attract " + getRandomUpper() + " American " + 
					 subjects + "! " + Dribbler.capitalizeEach(target) + subjects + " " + getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("If our nation's " + objects + " discriminate against American " + subjects + " in favor of " + target + subjects 
					 + " -- NO MORE Federal Funds?",
					 trumpisms);
		}
		
		
		if (action.equals("profit_from")) {
			
			addTweet("Americans used to be the go-to guys for " + objects + " before Mexico stole our jobs. Reform NAFTA to " +
					 "Make American " + Dribbler.capitalizeEach(subjects) + " Great Again! ",
					 trumpisms);
			
			addTweet("As a businessman I made a fortune from Trump " + objects + ". So only I can save us from " + target + subjects +
					 " and their " + getRandomDowner() + " " + objects + "!",
					 trumpisms);
			
			addTweet(getRandomMedia() + " says I made millions from "  + objects + ". That makes me SMART! " +
					 "Rival " + subjects + " lost their shirts on " + getRandomDowner() + " " +  objects + "!",
					 trumpisms);
		}
		
		
		if (action.equals("sell")) {
			
			addTweet("When it comes to selling " + objects + " Americans face unfair competition from Mexican " + subjects + 
					 "! We MUST reform NAFTA now!",
					 trumpisms);
			
			addTweet("Heads up, unpatriotic " + subjects + "! You may source your " + objects + " OUTSIDE the U.S. " +
					 "but I will tax you BIGLY to sell them INSIDE the U.S.",
					 trumpisms);
			
			addTweet("Mexico WILL pay for my wall, even if I have to super-tax the Mexican " + subjects + " who sell " +
					 getRandomDowner() + " " + objects + " to unsuspecting Americans!",
					 trumpisms);
			
			addTweet("American " + subjects + " have the right NOT to sell their " + objects + " to " + target + "customers. They " +
					 getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("As I put my businesses into a BLOND TRUST, I can't tell you about the " + getRandomUpper() + " " + objects +
					 " sold by TRUMP " + Dribbler.capitalizeEach(subjects) + " Inc. (ask Ivanka)",
					 trumpisms);
			
			addTweet("KellyAnne was " + getRandomUpper() + " on FOX & Friends today when she promoted Ivanka's new line of " + objects + 
					 ". Other " + subjects + " are " + getRandomDowner() + " by comparison!",
					 trumpisms);
			
			addTweet("If U.S " + subjects + " spent MORE time selling " + objects + " to " + target + "customers they would " +
					 "lose LESS business to " + getRandomDowner() + " " + target + subjects + "!",
					 trumpisms);
			
			addTweet("American " + subjects + " should spend MORE time selling " + objects + " to " + target + "customers " +
					 "and export LESS jobs to " + getRandomDowner() + " " + target + "factories!",
					 trumpisms);
		}
		
		
		if (action.equals("buy")) {
			addTweet("Patriotic " + subjects + " buy American-made " + objects + ". Unpatriotic " + subjects + " who buy their " +
					 objects + " from " + target + "sources " + getRandomEpithet() + "!",
					 trumpisms);
			
			addTweet("I can't tell patriotic " + subjects + " to buy their next " + objects + " from a TRUMP subsidiary, but " +	
					 "I won't tell you NOT TO either. #AmericaFirst",
					 trumpisms);
			
			addTweet(getRandomDowner() + " retailers can drop Ivanka's new line of " + objects + ", but patriotic " + subjects + 
					 " can still buy " + getRandomUpper() + " Trump " + objects + " on her website!",
					 trumpisms);
		}
		
		
		if (action.equals("lose")) {
			addTweet("The " + getRandomUpper() + " " + subjects + " who voted for CHANGE will regain the " + objects + 
					 " they lost in the Obama years, BELIEVE ME!",
					 trumpisms);
		}
		
		
		if (action.equals("win")) {
			addTweet("With me as POTUS America will WIN so Much! That's why I filled my cabinet with " + subjects + 
					 " who know how to win " + objects + "!",
					 trumpisms);
		}
		
		
		if (action.equals("support")) {
			addTweet("I have " + getRandomUpper() + " Supporters that support me the way " + subjects + " support " + 
					 getRandomUpper() + " " + objects + "! They " + getRandomCompliment() + "!",
					 trumpisms);
		}
		
		

		if (action.equals("advise")) {
			addTweet("I'm SMART so I've filled my cabinet with " + getRandomUpper() + " experts in their fields, to brief me " +
					 "the way "+ subjects + " brief their " + objects + "!",
					 trumpisms);
		}
		
		
		if (action.equals("are_exploited_by")) {
			
			addTweet("The American " + subjects + " who were exploited by " + target + objects + 
					 " in the Obama years will be treated as " + getRandomUpper() + " " + subjects + " by ME!",
					 trumpisms);
			
			addTweet("NAFTA has allowed Mexico to exploit Americans the way " + objects + " exploit " + subjects + "! Well, U.S. " +
					 subjects + " are fighting back!",
					 trumpisms);
		}
		
		
		if (action.equals("obey")) {
			addTweet("I will bend the " + getRandomDowner() + " " + getRandomAgency() + 
					 " to MY WILL and make it serve America the way " + subjects +
					 " obey their " + objects + ", BELIEVE ME!",
					 trumpisms);
			
			addTweet("Putin has the right idea: " + getRandomDowner() + " Media '" + subjects + "' must serve America the way " +
					 getRandomUpper() + " " + subjects + " serve their " + objects + ", OR ELSE!",
					 trumpisms);
		}
		
		
		if (action.equals("overcome")) {
			addTweet("The " + getRandomDowner() + " Media is the ENEMY of the American people! We MUST overcome them the way " +
					 subjects + " must overcome their " + objects + "!",
					 trumpisms);
		}
		
		
		if (action.equals("conduct")) {
			
			addTweet("Obama tapped Trump people when they interviewed new " + subjects + ". That guy was obsessed with our " + 
					objects + "! So " + getRandomDowner()  + "!",
					trumpisms);
			
			addTweet("I will reduce regulations BIG TIME on U.S. " + subjects + " so they can conduct their " + objects +
					" with less red tape.\nRegulators " + getRandomEpithet() + "!",
					 trumpisms);		
			
			addTweet("#BuildTheWall to keep " + target + subjects + " and their " + getRandomDowner() + " " + objects + 
					 " away from American " + subjects + " and their " + getRandomUpper() + " " + objects + "!",
					 trumpisms);
			
			addTweet("My " + getRandomUpper() + " new tax plan will cut taxes for American " + subjects + 
					 " so that patriotic taxpayers can get the " + getRandomSuperlativeUpper() + " " + objects + ", BELIEVE ME!",
					 trumpisms);
		}
		
		
		if (action.equals("perform")) {
			
			addTweet("Obama tapped my people when they held talks with Trump " + subjects + ". That guy was obsessed with our " + 
					objects + "! So " + getRandomDowner()  + "!",
					trumpisms);
			
			addTweet("American " + subjects + " will perform " + getRandomUpper() + " " + objects + " on My Watch.\n" +
					 Dribbler.capitalizeFirst(target) + subjects + " do truly " + getRandomDowner() + " " + objects,
					 trumpisms);		
			
			addTweet("More #FakeNews from " + getRandomMedia() + " that I stiffed 100s of " + subjects + " who worked at TRUMP businesses. " +
					 "I refuse to pay for " + getRandomDowner() + " " + objects + "!",
					 trumpisms);
			
			addTweet("#BuildTheWall to keep " + target + subjects + " and their " + getRandomDowner() + " " + objects + 
					 " away from American " + subjects + " and their " + getRandomUpper() + " " + objects + "!",
					 trumpisms);
			
			addTweet("My " + getRandomUpper() + " new tax plan will cut taxes for American " + subjects + 
					 " so that patriotic taxpayers will get the " + getRandomSuperlativeUpper() + " " + objects + ", BELIEVE ME!",
					 trumpisms);
		}
		
		
		if (action.equals("follow")) {
			addTweet("I led you ALL THE WAY to the White House. I will lead you to future " + getRandomUpper() + 
					 "NESS if you follow me the way " + subjects + " follow " + objects + "!",
					 trumpisms);
		}
		
		
		if (action.equals("inherit")) {
			
			if (subjects.startsWith("inherit") || objects.startsWith("inherit"))
				addTweet("Critics say I inherited everything, the way " + subjects + " are given " + objects + ". Like me, " + 
						getRandomUpper() + " " + subjects + " build their OWN " + objects + "!",
						trumpisms);
			else
				addTweet("Critics say I inherited everything, the way " + subjects + " inherit their " + objects + ". Like me, " + 
						getRandomUpper() + " " + subjects + " build their OWN " + objects + "!",
						trumpisms);
			
		}
		
		
		if (action.equals("study")) {
			addTweet("TRUMP University will re-open to offer " +  getRandomUpper() + " Degrees in " + Dribbler.capitalizeFirst(objects) + 
					" to AMERICAN " + subjects + ". " + Dribbler.capitalizeFirst(target) + subjects + " " + getRandomEpithet() +  "!",
					 trumpisms);
		}
		
		
		if (action.equals("specialize_in")) {
			addTweet("TRUMP University will hire " +  getRandomUpper() + " American " + subjects + " to teach courses in " +
					 objects + ". Trump graduates " + getRandomCompliment(),
					 trumpisms);
			
			addTweet("Some over-rated " + subjects + " criticized me on FAKE NEWS @CNN, but I know more about " + objects +
					 " than those " + getRandomDowner() + " " + subjects + " ever will!",
					 trumpisms);
			
			addTweet("Some so-called " + subjects + " criticized me on Meet-the-Press, but I've forgotten more about " + objects +
					 " than " + getRandomDowner() + " " + subjects + " will ever know!",
					 trumpisms);
			
			addTweet("Obama filled his cabinet with elitist cronies and 'experts' in " + objects + ". Well, there are no " + 
					 getRandomDowner() + " " + subjects + " in MY cabinet!",
					 trumpisms);
		}
	}
	
	
	
	
	protected String getRandomUpper()
	{
		return TRUMP_UPPERS[RND.nextInt(TRUMP_UPPERS.length)];
	}
	
	
	protected String getRandomSuperlativeUpper()
	{
		return SUPERLATIVE_UPPERS[RND.nextInt(SUPERLATIVE_UPPERS.length)];
	}
	
	
	
	protected String getRandomDowner()
	{
		return TRUMP_DOWNERS[RND.nextInt(TRUMP_DOWNERS.length)];
	}
	
	
	protected String getRandomSuperlativeDowner()
	{
		return SUPERLATIVE_DOWNERS[RND.nextInt(SUPERLATIVE_DOWNERS.length)];
	}
	
	
	protected String getRandomAgency()
	{
		return INFO_AGENCIES[RND.nextInt(INFO_AGENCIES.length)];
	}
	
	
	protected String getRandomMedia()
	{
		return TARGET_MEDIA[RND.nextInt(TARGET_MEDIA.length)];
	}

	
	protected String getRandomCompliment()
	{
		return TRUMP_COMPLIMENTS[RND.nextInt(TRUMP_COMPLIMENTS.length)];
	}
	
	
	protected String getRandomEpithet()
	{
		return TRUMP_EPITHETS[RND.nextInt(TRUMP_EPITHETS.length)];
	}
	
	
	protected String getRandomTarget()
	{
		return TARGET_NATIONALITIES[RND.nextInt(TARGET_NATIONALITIES.length)];
	}
	

	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Add a new tweet to a list of current candidates
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//


	private boolean addTweet(String tweet, Vector<String> tweets)
	{
		if (tweet == null || tweet.length() > 140) return false;
		
		if (tweet.length() < 140) {
			tweet = Dribbler.replaceWith(tweet, "American enemies", "American citizens");
			tweet = Dribbler.replaceWith(tweet, "AMERICAN enemies", "AMERICAN citizens");
			tweet = Dribbler.replaceWith(tweet, "U.S. enemies", "U.S. citizens");
		}
		
//		if (tweets.size() > 0 && tooSimilarToContext(tweet)) 
//			return false;
		
		tweets.add(tweet);
		
		return true;
	}
	
	
	
	public void addContext(String tweet)
	{
		contextTweets.insertElementAt(tweet, 0);
	}
	
	
	public void setContext(Vector <String> tweets)
	{
		contextTweets = tweets;
	}
	
	
	public boolean tooSimilarToContext(String tweet)
	{
		if (contextTweets.size() == 0) return false;
		
		String probe = tweet.substring(0, CONTEXT_KEYHOLE);
		
		for (int i = 0; i < Math.min(contextTweets.size(), scheduledFields.size()); i++)
		{
			String recent = contextTweets.elementAt(i);
			
			if (recent.startsWith(probe)) return true;
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Knowledge base Access methods
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public String getRandomEvent()
	{
		Vector all = EVENT_ROLES.getAllFrames();
		
		return (String)all.elementAt(RND.nextInt(all.size()));
	}
	
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main test stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

	public static void main(String[] args)
	{
		String kdir   = "/Users/tonyveale/Dropbox/CodeCamp2016/NOC/DATA/TSV Lists/";
		
		ClockworkTrump trumpifier = new ClockworkTrump(kdir);
		
		for (int i = 0; i < 1000; i++) {
			String tweet = trumpifier.getRandomTrumpism();
		
			System.out.println(i + ". " + tweet + " (" + tweet.length() + ")\n");
			
			trumpifier.addContext(tweet);
		}
	}
}
