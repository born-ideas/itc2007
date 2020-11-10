package dev.born.itc2007;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Models a period in which an exam can be scheduled.
 */
public class Period {
	public final int number;
	public final LocalDate date;
	public final LocalTime time;
	public final int duration;
	public final int penalty;

	/**
	 * @param number   - the identifier for the period.
	 * @param date     - the date for the period.
	 * @param time     - the time for the period.
	 * @param duration - the duration of the period.
	 * @param penalty  - the penalty associated with the period.
	 */
	public Period(int number, LocalDate date, LocalTime time, int duration, int penalty) {
		this.number = number;
		this.date = date;
		this.time = time;
		this.duration = duration;
		this.penalty = penalty;
	}

	public LocalDateTime getDateTime() {
		return LocalDateTime.of(date, time);
	}

	@Override
	public String toString() {
		return "Period{" +
				"number=" + number +
				", date=" + date +
				", time=" + time +
				", duration=" + duration +
				", penalty=" + penalty +
				'}';
	}
}
