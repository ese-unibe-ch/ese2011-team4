package models;

/**
 * The RepeatingType enumeration represents all possible repeating types for an {@link Event}.
 * <p>
 * Repeating types:
 * <ul>
 * <li>NONE: The event doesn't repeat at all</li>
 * <li>DAILY: The event repeats every day</li>
 * <li>WEEKLY: The event repeats every week</li>
 * <li>MONTHLY: The event repeats every month</li>
 * <li>YEARLY: The event repeats every year</li>
 * </ul>
 */
public enum RepeatingType {
	NONE, DAILY, WEEKLY, MONTHLY, YEARLY;
	
	public static RepeatingType parseFromString(String str) {
		for(RepeatingType e : RepeatingType.values()) {
			if(str.equalsIgnoreCase(e.toString()))
				return e;
		}
		return null;
	}
}
