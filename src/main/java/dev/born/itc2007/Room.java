package dev.born.itc2007;

/**
 * Models a room in which an exam can be scheduled.
 */
public class Room {
	public final int number;
	public final int capacity;
	public final int penalty;

	/**
	 * @param number   - the identifier for the room.
	 * @param capacity - the number of students that a room can hold.
	 * @param penalty  - the penalty associated with a room.
	 */
	public Room(int number, int capacity, int penalty) {
		this.number = number;
		this.capacity = capacity;
		this.penalty = penalty;
	}

	@Override
	public String toString() {
		return "Room{" +
				"number=" + number +
				", capacity=" + capacity +
				", penalty=" + penalty +
				'}';
	}
}
