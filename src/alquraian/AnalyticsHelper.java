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
		// List<Status> mostCommon = new ArrayList<Status>();
		final Map<Status, Long> statucCount = new HashMap<Status, Long>(
				updateRetweetCount(statuses));

		final Map<String, Long> retweetsCount = new HashMap<String, Long>();
		for (Status status : statucCount.keySet()) {
			retweetsCount.put(status.getText(), statucCount.get(status));
		}
		List<String> mostCommon = getMostCommon(retweetsCount, topCount);

		for (String status : mostCommon) {
			System.out.println("[" + status + "]" + "\nRetweeted "
					+ retweetsCount.get(status) + " times.\n");
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
		Map<String, Long> usersMentioned = updateMentionsCount(statuses);
		List<String> mostCommon = new ArrayList<String>();

		mostCommon = getMostCommon(usersMentioned, topCount);

		long mentionsCounter = 0;

		for (String user : mostCommon) {
			if (usersMentioned.get(user) != mentionsCounter) {
				mentionsCounter = usersMentioned.get(user);
				System.out.println("Users mentioned " + mentionsCounter
						+ " times:");
			}
			System.out.println("@" + user);
		}
	}

	private static List<String> getMostCommon(Map<String, Long> map,
			int topCount) {
		long mostCommonCount = 0;
		List<String> mostCommon = new ArrayList<String>();
		for (String user : map.keySet()) {
			if (map.get(user) > mostCommonCount) {
				mostCommonCount = map.get(user);
			}
		}

		int topCounter = 0;
		for (long i = mostCommonCount; i > 1; i--) {
			if (!map.values().contains(i)) {
				continue;
			}

			if (topCounter++ == topCount) {
				break;
			}

			for (String user : map.keySet()) {
				if (map.get(user).equals(i)) {
					mostCommon.add(user);
				}
			}
		}
		return mostCommon;
	}

	private static Map<String, Long> updateMentionsCount(
			final List<Status> statuses) {
		Map<String, Long> counterMap = new HashMap<String, Long>();
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
}
