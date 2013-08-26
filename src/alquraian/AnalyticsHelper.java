package alquraian;

import java.util.ArrayList;
import java.util.Collection;
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
		final Collection<Integer> values = hashTags.values();
		values.remove(1);

		int mostCommon = 0;

		// Get the most common hashtag's count
		for (String tag : hashTags.keySet()) {
			if (hashTags.get(tag) > mostCommon) {
				mostCommon = hashTags.get(tag);
			}
		}

		if (mostCommon < 2) {
			return;
		}

		System.out.println("Most common tags are:");

		for (int i = mostCommon; i > 1; i--) {
			for (String tag : hashTags.keySet()) {
				if (hashTags.get(tag).equals(i)) {
					System.out.println(tag + " mentioned " + hashTags.get(tag)
							+ " times.");
				}
			}
		}

	}
}
