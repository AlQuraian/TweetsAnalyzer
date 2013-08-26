package alquraian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class TweetsAnalyzerApp {
	static boolean toContinue = true;
	// The factory instance is re-useable and thread safe.
	final static Twitter twitter = TwitterFactory.getSingleton();

	public static void main(String[] args) {
		printDecorativeLine();
		System.out.println("-- Welcome to TweetsAnalyzer --"
				+ "Type 'exit' anytime to exit from app.");
		printDecorativeLine();
		System.out.println("\n");

		String screenName = "";
		User user = null;
		int howManyTweets = 0;

		while (toContinue) {
			System.out.print("Username to analyze: @");
			screenName = readUsername();
			// screenName = "AJALive";
			try {
				user = twitter.showUser(screenName);
			} catch (TwitterException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			howManyTweets = readHowManyTweets();

			System.out.println("User: " + "@" + user.getScreenName()
					+ ", fetching " + howManyTweets + " tweets.");

			if (!screenName.isEmpty() && howManyTweets > 0) {
				startAnalyzer(user, howManyTweets);
			}

			readAndUpdateToContinue();
		}

		printExitMessage();
	}

	private static void startAnalyzer(final User user, final int howManyTweets) {
		final Paging paging = new Paging(1, howManyTweets);
		final int TOP_COUNT = 5;

		try {
			final List<Status> statuses = twitter.getUserTimeline(
					user.getScreenName(), paging);

			System.out.println("\n");
			printDecorativeLine();
			System.out.println("Top " + TOP_COUNT + " mentioned users: ");
			printDecorativeLine();
			AnalyticsHelper.printTopMentioned(statuses, TOP_COUNT);

			System.out.println("\n");
			printDecorativeLine();
			System.out.println("Top " + TOP_COUNT + " retweeted: ");
			printDecorativeLine();
			AnalyticsHelper.printTopRetweeted(statuses, TOP_COUNT);

			System.out.println("\n");
			printDecorativeLine();
			System.out.println("Top " + TOP_COUNT + " hashtags are:");
			printDecorativeLine();
			AnalyticsHelper.printMostCommonHashTags(statuses, TOP_COUNT);

		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get timeline: " + te.getMessage());
		}
	}

	private static int readHowManyTweets() {
		int howMany = 0;
		System.out.print("How many tweets do you want to fetch? ");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));

		while (true) {
			try {
				howMany = Integer.valueOf(shouldExit(bufferRead.readLine()
						.trim()));
				break;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.out.print("Please enter a valid number: ");
			}
		}
		return howMany;
	}

	private static void readAndUpdateToContinue() {
		String yesOrNo = "";
		while (true) {
			System.out
					.print("\n\nDo you want to analyse another user's tweets (Y/n)? ");
			new BufferedReader(new InputStreamReader(System.in));
			try {
				BufferedReader bufferRead = new BufferedReader(
						new InputStreamReader(System.in));
				yesOrNo = shouldExit(bufferRead.readLine().trim());
				shouldExit(yesOrNo);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (yesOrNo.equalsIgnoreCase("yes")
					|| yesOrNo.equalsIgnoreCase("ye")
					|| yesOrNo.equalsIgnoreCase("y")) {
				toContinue = true;
				break;
			} else if (yesOrNo.equalsIgnoreCase("no")
					|| yesOrNo.equalsIgnoreCase("n")) {
				toContinue = false;
				break;
			}
		}
	}

	private static String readUsername() {
		String name = "";
		try {
			BufferedReader bufferRead = new BufferedReader(
					new InputStreamReader(System.in));
			name = shouldExit(bufferRead.readLine().trim());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * If the text equals 'exit' then exit of the app, ealse return same string
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
