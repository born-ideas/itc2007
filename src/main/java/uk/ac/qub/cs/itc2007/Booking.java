package uk.ac.qub.cs.itc2007;

/**
 * Models a specific booking for an exam.
 */
public class Booking {
	public final Exam exam;
	public final Period period;
	public final Room room;

	/**
	 * @param examNum - the number of the booked exam.
	 * @param period  - the time slot number of the booked exam.
	 * @param room    - the room number of the booked exam.
	 */
	public Booking(Exam examNum, Period period, Room room) {
		this.exam = examNum;
		this.period = period;
		this.room = room;
	}

	@Override
	public String toString() {
		return "[[ " + exam + " | " + period + " | " + room + " ]]";
	}
}
