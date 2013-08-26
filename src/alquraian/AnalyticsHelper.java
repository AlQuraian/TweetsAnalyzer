package alquraian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.Status;

public final class AnalyticsHelper {
	public static Map<String, Integer> updateCount(List<Status> statuses) {
		Map<String, Integer> tagsMap = new HashMap<String, Integer>();
		for (Status status : statuses) {
			for (String hashTag : getHashTags(status)) {
				tagsMap.put(hashTag,
						tagsMap.get(hashTag) == null ? 1
								: tagsMap.get(hashTag) + 1);
			}
		}
		return tagsMap;
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

	public static void printMostCommonHashTags(
			final Map<String, Integer> hashTags, final int topCount) {
		int mostCommonCount = 0;
		List<String> currentListToPrint = new ArrayList<String>();

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

		System.out.println("Top " + topCount + " common tags are:");

		int topCounter = 0;
		for (int i = mostCommonCount; i > 1; i--) {
			if (!hashTags.values().contains(i)) {
				continue;
			}

			if (topCounter++ == topCount) {
				return;
			}

			System.out.println("Hashtags mentioned " + i + " times:");
			for (String tag : hashTags.keySet()) {
				if (hashTags.get(tag).equals(i)) {
					currentListToPrint.add(tag);
				}
			}
			System.out.println(currentListToPrint);
			currentListToPrint.clear();
		}

	}

	public static void printTopRetweeted(final List<Status> statuses,
			final int topCount) {
		// TODO Auto-generated method stub

	}
}
