package alquraian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TweetsAnalyzerApp {
	static boolean toContinue = true;

	public static void main(String[] args) {
		printDecorativeLine();
		System.out.println("-- Welcome to TweetsAnalyzer --"
				+ "Type 'exit' anytime to exit from app.");
		printDecorativeLine();

		String user = "";
		int howManyTweets = 0;

		while (toContinue) {
			System.out.print("Username that you want to analyze: @");
			user = readUsername();
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

	private static void startAnalyzer(String user, int howManyTweets) {
	}

	private static int readHowManyTweets() {
		int howMany = 0;
		System.out.print("How many tweets do you want to fetch? ");

		// Scanner scan = new Scanner(System.in);
		// howMany = scan.nextInt();
		// scan.close();
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
					.print("Do you want to analyse another user's tweets (Y/n)? ");
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
			// bufferRead.close();
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
