package alquraian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TweetsAnalyzerApp {
	static boolean toContinue = true;

	public static void main(String[] args) {
		printDecorativeLine();
		System.out.println("-- Welcome to TweetsAnalyzer --"
				+ "Type 'exit' anytime to exit from app.");
		printDecorativeLine();
		System.out.println("\n");

		String user = "";
		int howManyTweets = 0;

		while (toContinue) {
			System.out.print("Username that you want to analyze: @");
			// user = readUsername();
			user = "AJALive";
			howManyTweets = readHowManyTweets();

			System.out.println("User: " + user + ", fetching " + howManyTweets
					+ " tweets.");

			if (!user.isEmpty() && howManyTweets > 0) {
				startAnalyzer(user, howManyTweets);
			}

			readAndUpdateToContinue();
		}

		printExitMessage();
	}

	private static void startAnalyzer(final String user, final int howManyTweets) {
		// The factory instance is re-useable and thread safe.
		final Twitter twitter = TwitterFactory.getSingleton();
		final HashMap<String, Integer> hashTagsCount = new HashMap<String, Integer>();
		final Paging paging = new Paging(1, howManyTweets);

		try {
			final List<Status> statuses = twitter.getUserTimeline(user, paging);

			System.out.println("Showing @" + user + "'s timeline.");

			hashTagsCount.putAll(AnalyticsHelper.updateCount(statuses));
			AnalyticsHelper.printMostCommonHashTags(hashTagsCount, 10);

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

	@SuppressWarnings("unused")
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
	 * If the text equals 'exit' then exity of the app, ealse return same string
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
