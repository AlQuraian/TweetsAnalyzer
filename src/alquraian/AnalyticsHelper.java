package alquraian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.Status;
import twitter4j.UserMentionEntity;

public final class AnalyticsHelper {

	public static void printTopRetweeted(final List<Status> statuses,
			final int topCount) {
		List<Status> mostCommon = new ArrayList<Status>();

		final Map<Status, Long> retweetsCount = new HashMap<Status, Long>(
				updateRetweetCount(statuses));

		long mostCommonCount = 0;
		for (Status status : retweetsCount.keySet()) {
			if (retweetsCount.get(status) > mostCommonCount) {
				mostCommonCount = retweetsCount.get(status);
			}
		}

		int topCounter = 0;
		for (long i = mostCommonCount; i > 1; i--) {
			if (!retweetsCount.values().contains(i)) {
				continue;
			}

			if (topCounter++ == topCount) {
				return;
			}

			for (Status tag : retweetsCount.keySet()) {
				if (retweetsCount.get(tag).equals(i)) {
					mostCommon.add(tag);
				}
			}

			for (Status status : mostCommon) {
				System.out.println("[" + status.getText() + "]"
						+ "\nRetweeted " + retweetsCount.get(status)
						+ " times.\n");
			}

			mostCommon.clear();
		}
	}

	private static Map<Status, Long> updateRetweetCount(List<Status> statuses) {
		Map<Status, Long> counterMap = new HashMap<Status, Long>();
		for (Status status : statuses) {
			counterMap.put(status, status.getRetweetCount());
		}
		return counterMap;
	}

	public static void printMostCommonHashTags(List<Status> statuses,
			final int topCount) {
		final Map<String, Integer> hashTags = new HashMap<String, Integer>(
				updateCount(statuses));
		List<String> mostCommon = new ArrayList<String>();

		int mostCommonCount = 0;
		// Get the most common hashtag's count
		for (String tag : hashTags.keySet()) {
			if (hashTags.get(tag) > mostCommonCount) {
				mostCommonCount = hashTags.get(tag);
			}
		}

		// Only show tags mentioned more than once
		if (mostCommonCount < 2) {
			return;
		}

		int topCounter = 0;
		for (int i = mostCommonCount; i > 1; i--) {
			if (!hashTags.values().contains(i)) {
				continue;
			}

			if (topCounter++ == topCount) {
				return;
			}

			for (String tag : hashTags.keySet()) {
				if (hashTags.get(tag).equals(i)) {
					mostCommon.add(tag);
				}
			}

			System.out.println("Hashtags referenced " + i + " times:");
			System.out.println(mostCommon);
			mostCommon.clear();
		}

	}

	private static Map<String, Integer> updateCount(List<Status> statuses) {
		Map<String, Integer> counterMap = new HashMap<String, Integer>();
		for (Status status : statuses) {
			for (String hashTag : getHashTags(status)) {
				counterMap.put(hashTag, counterMap.get(hashTag) == null ? 1
						: counterMap.get(hashTag) + 1);
			}
		}
		return counterMap;
	}

	/**
	 * This method will ignore these characters from the hashtags: #.,?!:;/
	 */
	private static List<String> getHashTags(final Status status) {
		List<String> list = new ArrayList<String>();
		final String[] words = status.getText().split("\\s");

		for (String word : words) {
			if (word.startsWith("#")) {
				list.add(word.replaceAll("[؟#.,?!:;/]", ""));
			}
		}

		return list;
	}

	public static void printTopMentioned(List<Status> statuses, int topCount) {
		Map<String, Integer> usersMentioned = updateMentionsCount(statuses);
		List<String> mostCommon = new ArrayList<String>();

		int mostCommonCount = 0;
		for (String user : usersMentioned.keySet()) {
			if (usersMentioned.get(user) > mostCommonCount) {
				mostCommonCount = usersMentioned.get(user);
			}
		}

		int topCounter = 0;
		for (int i = mostCommonCount; i > 1; i--) {
			if (!usersMentioned.values().contains(i)) {
				continue;
			}

			if (topCounter++ == topCount) {
				return;
			}

			for (String user : usersMentioned.keySet()) {
				if (usersMentioned.get(user).equals(i)) {
					mostCommon.add(user);
				}
			}

			for (String user : mostCommon) {
				System.out.println("@" + user + " was mentioned "
						+ usersMentioned.get(user) + " times.");
			}
			mostCommon.clear();
		}
	}

	private static Map<String, Integer> updateMentionsCount(
			final List<Status> statuses) {
		Map<String, Integer> counterMap = new HashMap<String, Integer>();
		UserMentionEntity[] users;

		for (Status status : statuses) {
			users = status.getUserMentionEntities();
			for (UserMentionEntity userMentionEntity : users) {
				counterMap
						.put(userMentionEntity.getScreenName(),
								counterMap.get(userMentionEntity
										.getScreenName()) == null ? 1
										: counterMap.get(userMentionEntity
												.getScreenName()) + 1);
			}
		}
		return counterMap;
	}

	// private static void printDecorativeLine() {
	// System.out.println("-------------------------------------");
	// }

}
