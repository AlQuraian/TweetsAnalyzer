package alquraian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.json.DataObjectFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class TweetsAnalyzerApp {
	// The factory instance is re-useable and thread safe.
	private static final Twitter twitter = TwitterFactory.getSingleton();
	private static final int TOP_COUNT = 5;
	private static MongoClient mongoClient = null;
	private static DB tweetsAnalyzerDB = null;
	private static DBCollection statusesColl = null;
	private static boolean toContinue = true;

	public static void main(String[] args) {
		printDecorativeLine();
		System.out.println("-- Welcome to TweetsAnalyzer --"
				+ "               Type 'exit' anytime to exit from app.");
		printDecorativeLine();
		System.out.println("\n");

		String screenName = "";
		User user = null;
		int howManyTweets = 0;

		try {
			initDatabase();
			System.out.println("Fetching data from database...");
			fetchDataFromDB();
		} catch (UnknownHostException e) {
			System.out.println("Failed to fetch from database: "
					+ e.getMessage());
		} catch (MongoException e) {
			System.out.println("Database is not available!");
		}

		while (toContinue) {
			System.out.print("Username to analyze: @");
			screenName = readUsername();
			try {
				user = twitter.showUser(screenName);
			} catch (TwitterException e) {
				System.out.println("Please enter a valid name!");
				continue;
			}
			howManyTweets = howManyTweetsToFetch();

			System.out.println("User: " + "@" + user.getScreenName()
					+ ", fetching " + howManyTweets + " tweets.");

			try {
				final Paging paging = new Paging(1, howManyTweets);
				final List<Status> statuses = twitter.getUserTimeline(
						user.getScreenName(), paging);
				// System.out.println("Statuses successfully fetched,"
				// + " saving the data to the database...");
				analyze(statuses);
				initDatabase();
				saveToDB(statuses);
			} catch (TwitterException te) {
				System.out
						.println("Failed to get timeline: " + te.getMessage());
			} catch (UnknownHostException e) {
				System.out.println("Failed to save to database: "
						+ e.getMessage());
			} catch (MongoException e) {
				System.out.println("Database is not available!");
			}

			toContinue = confirm("\n\nDo you want to analyse another user's tweets (Y/n)? ");
		}

		printExitMessage();
	}

	private static void analyze(final List<Status> statuses) {
		printSmallMessage(" mentioned users:");
		AnalyticsHelper.printTopMentioned(statuses, TOP_COUNT);

		printSmallMessage(" retweeted:");
		AnalyticsHelper.printTopRetweeted(statuses, TOP_COUNT);

		printSmallMessage(" hashtags:");
		AnalyticsHelper.printMostCommonHashTags(statuses, TOP_COUNT);
	}

	private static void printSmallMessage(String message) {
		System.out.println("\n");
		System.out.println("--------------------------");
		System.out.println("Top " + TOP_COUNT + message);
		System.out.println("--------------------------");
	}

	private static void saveToDB(List<Status> statuses) {
		String statusRaw = null;
		for (Status status : statuses) {
			statusRaw = DataObjectFactory.getRawJSON(status);
			if (status != null) {
				DBObject doc = (DBObject) JSON.parse(statusRaw);
				statusesColl.insert(doc);
			}
		}
	}

	private static void fetchDataFromDB() {
		DBCursor cursor = statusesColl.find();

		if (cursor.size() == 0) {
			System.out.println("Database is empty!");
			return;
		}

		System.out.println(cursor.size() + " statuses have been fetched.");

		if (confirm("Do you want to print statuses fetched from the database (Y, n)?")) {
			List<Status> statuses = new ArrayList<Status>();
			try {
				while (cursor.hasNext()) {
					System.out.println(cursor.next());
				}
				if (!statuses.isEmpty()) {
					analyze(statuses);
				}
			} finally {
				cursor.close();
			}
		}
	}

	private static void initDatabase() throws UnknownHostException {
		if (mongoClient == null) {
			mongoClient = new MongoClient();
		}
		if (tweetsAnalyzerDB == null) {
			tweetsAnalyzerDB = mongoClient.getDB("TweetsAnalyzerDatabase");
		}
		if (statusesColl == null) {
			statusesColl = tweetsAnalyzerDB.getCollection("statuses");
		}
	}

	private static int howManyTweetsToFetch() {
		int howMany = 0;
		System.out.print("How many tweets do you want to fetch? ");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));

		while (true) {
			try {
				howMany = Integer.valueOf(shouldExit(bufferRead.readLine()
						.trim()));
				if (howMany < 1) {
					System.out.print("Please enter a valid number: ");
					continue;
				}
				break;
			} catch (IOException e) {
				// e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.print("Please enter a valid number: ");
			}
		}
		return howMany;
	}

	private static boolean confirm(String message) {
		String yesOrNo = "";
		while (true) {
			System.out.print(message);
			new BufferedReader(new InputStreamReader(System.in));
			try {
				BufferedReader bufferRead = new BufferedReader(
						new InputStreamReader(System.in));
				yesOrNo = shouldExit(bufferRead.readLine().trim());
				shouldExit(yesOrNo);
			} catch (IOException e) {
				// e.printStackTrace();
			}

			if (yesOrNo.equalsIgnoreCase("yes")
					|| yesOrNo.equalsIgnoreCase("ye")
					|| yesOrNo.equalsIgnoreCase("y")) {
				return true;
			} else if (yesOrNo.equalsIgnoreCase("no")
					|| yesOrNo.equalsIgnoreCase("n")) {
				return false;
			}
		}
	}

	private static String readUsername() {
		String name = "";
		try {
			BufferedReader bufferRead = new BufferedReader(
					new InputStreamReader(System.in));
			name = shouldExit(bufferRead.readLine().trim().split("\\s")[0]);
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return name;
	}

	/**
	 * If the text equals 'exit' then exit of the app, else return same string
	 */
	private static String shouldExit(String input) {
		if (input.equalsIgnoreCase("exit")) {
			printExitMessage();
			System.exit(0);
		}
		return input;
	}

	private static void printExitMessage() {
		System.out.println("\n");
		printDecorativeLine();
		System.out
				.println("Thank you for using TweetsAnalyzer. Have a good day!");
		printDecorativeLine();
	}

	private static void printDecorativeLine() {
		System.out
				.println("====================================================");
	}
}
