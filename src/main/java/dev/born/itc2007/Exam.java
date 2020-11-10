package dev.born.itc2007;

import java.util.List;

/**
 * Models a single exam that needs to be booked.
 */
public class Exam {
	public final int number;
	public final int duration;
	public final List<String> students;

	/**
	 * @param number - the identifier for the exam.
	 * @param duration - the duration that the exam will last.
	 * @param students - the list of students partaking in the exam.
	 */
	public Exam(int number, int duration, List<String> students) {
		this.number = number;
		this.duration = duration;
		this.students = students;
	}

	@Override
	public String toString() {
		return "Exam{" +
				"number=" + number +
				", duration=" + duration +
				", #students=" + students.size() +
				'}';
	}
}
