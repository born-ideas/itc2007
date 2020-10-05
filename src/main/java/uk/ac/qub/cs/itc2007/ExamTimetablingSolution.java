package uk.ac.qub.cs.itc2007;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Models a solution to a exam timetabling problem instance.
 */
public class ExamTimetablingSolution {
	/**
	 * The problem for which this is a solution.
	 */
	private final ExamTimetablingProblem problem;

	/**
	 * The bookings forming part of this solution.
	 */
	public final List<Booking> bookings;

	/**
	 * @param problem - the problem that this solution will solve.
	 */
	public ExamTimetablingSolution(ExamTimetablingProblem problem, List<Booking> bookings) {
		this.problem = problem;
		this.bookings = bookings;
	}

	/**
	 * The number of conflicting exams in this solution.
	 *
	 * See 'Conflicts' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int conflictingExams() {
		int count = 0;

		for (Booking bookingA : bookings) {
			for (Booking bookingB : bookings) {
				if (bookingA.equals(bookingB)) continue;
				boolean doClash = bookingA.period.number == bookingB.period.number;
				boolean doShareStudents = problem.clashMatrix[bookingA.exam.number][bookingB.exam.number] > 0;
				if (doClash && doShareStudents) count++;
			}
		}

		return count;
	}

	/**
	 * The number of overbooked periods in this solution.
	 *
	 * See 'RoomOccupancy' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int overbookedPeriods() {
		int count = 0;

		for (Period period : problem.periods) {
			for (Room room : problem.rooms) {
				List<Booking> periodRoomBookings = bookings.stream().filter(b -> b.period.number == period.number && b.room.number == room.number).collect(Collectors.toList());
				int numSeatsNeeded = periodRoomBookings.stream().mapToInt(b -> b.exam.students.size()).sum();
				if (numSeatsNeeded > room.capacity) count++;
			}
		}

		return count;
	}

	/**
	 * The number of periods that are too short in this solution.
	 *
	 * See 'PeriodUtilisation' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int tooShortPeriods() {
		int count  = 0;

		HashSet<Booking> seen = new HashSet<>();
		for (Booking booking : bookings) {
			if (booking.exam.duration > booking.period.duration && seen.add(booking)) count++;
		}

		return count;
	}

	/**
	 * The number of period hard constraint violations in this solution.
	 *
	 * See 'PeriodRelated' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int periodConstraintViolations() {
		int count = 0;

		for (PeriodHardConstraint constraint : problem.periodHardConstraints) {
			Booking bookingOne = bookings.stream().filter(b -> b.exam.number == constraint.examOneNum).findFirst().orElse(null);
			Booking bookingTwo = bookings.stream().filter(b -> b.exam.number == constraint.examTwoNum).findFirst().orElse(null);
			if (bookingOne == null || bookingTwo == null) continue;

			if (constraint.constraintType.equals("EXAM_COINCIDENCE")) {
				if (problem.clashMatrix[constraint.examOneNum][constraint.examTwoNum] > 0) continue;
				if (bookingOne.period.number != bookingTwo.period.number) count++;
			}

			if (constraint.constraintType.equals("EXCLUSION")) {
				if (bookingOne.period.number == bookingTwo.period.number) count++;
			}

			if (constraint.constraintType.equals("AFTER")) {
				if (bookingOne.period.getDateTime().isAfter(bookingTwo.period.getDateTime())) count++;
			}
		}

		return count;
	}

	/**
	 * The number of room hard constraint violations in this solution.
	 *
	 * See 'RoomRelated' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int roomConstraintViolations() {
		int count = 0;

		for (RoomHardConstraint constraint : problem.roomHardConstraints) {
			if (constraint.constraintType.equals("ROOM_EXCLUSIVE")) {
				Booking booking = bookings.stream().filter(b -> b.exam.number == constraint.examNum).findFirst().orElse(null);
				if (booking == null) continue;
				boolean isNotBookedAlone = bookings.stream().anyMatch(b -> b.room.number == booking.room.number && b.period.number == booking.period.number);
				if (isNotBookedAlone) count++;
			}
		}

		return count;
	}

	/**
	 * The number of occurrences where two examinations are taken by students straight after one another.
	 *
	 * See 'Two Exams in a Row' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int twoInARowPenalty() {
		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("TWOINAROW")).findFirst().orElse(null);
		if (weighting == null) return 0;

		int penalty = 0;
		for (Booking bookingA : bookings) {
			for (Booking bookingB : bookings) {
				boolean areInARow = Math.abs(bookingA.period.number - bookingB.period.number) == 1;
				boolean areOnSameDay = bookingA.period.date.isEqual(bookingB.period.date);
				if (areInARow && areOnSameDay)
					penalty += weighting.paramOne * problem.clashMatrix[bookingA.exam.number][bookingB.exam.number];
			}
		}

		return penalty;
	}

	/**
	 * The number of occurrences of students having two exams in a day which are not directly adjacent.
	 *
	 * See 'Two Exams in a Day' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int twoInADayPenalty() {
		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("TWOINADAY")).findFirst().orElse(null);
		if (weighting == null) return 0;

		int penalty = 0;
		for (Booking bookingA : bookings) {
			for (Booking bookingB : bookings) {
				if (bookingA.equals(bookingB)) continue;
				boolean areNotAdjacent = Math.abs(bookingA.period.number - bookingB.period.number) != 1;
				boolean areOnSameDay = bookingA.period.date.isEqual(bookingB.period.date);
				if (areNotAdjacent && areOnSameDay)
					penalty += weighting.paramOne * problem.clashMatrix[bookingA.exam.number][bookingB.exam.number];
			}
		}

		return penalty;
	}

	/**
	 * Occurrences of enrolled students who have to sit other exams within the desired period spread.
	 *
	 * See 'Period Spread' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int periodSpreadPenalty() {
		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("PERIOD_SPREAD")).findFirst().orElse(null);
		if (weighting == null) return 0;

		int penalty = 0;
		for (Booking bookingA : bookings) {
			for (Booking bookingB : bookings) {
				int spread = bookingB.period.number - bookingA.period.number;
				if (spread <= 0) continue;
				boolean areWithinSpread = spread <= weighting.paramOne;
				if (areWithinSpread)
					penalty += problem.clashMatrix[bookingA.exam.number][bookingB.exam.number];
			}
		}

		return penalty;
	}

	/**
	 * This applies a penalty to a ROOM and PERIOD (not Exam) where there are mixed durations
	 *
	 * See 'Mixed Durations' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int mixedDurationsPenalty() {
		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("NONMIXEDDURATIONS")).findFirst().orElse(null);
		if (weighting == null) return 0;

		int penalty = 0;
		for (Period period : problem.periods) {
			for (Room room : problem.rooms) {
				List<Booking> myBookings = bookings.stream().filter(b -> b.period.number == period.number && b.room.number == room.number).collect(Collectors.toList());
				if (myBookings.isEmpty()) continue;
				HashSet<Object> seen = new HashSet<>();
				myBookings.removeIf(b -> !seen.add(b.exam.duration));
				int numDifferentDurations = myBookings.size();
				penalty += (numDifferentDurations - 1) * weighting.paramOne;
			}
		}

		return penalty;
	}

	/**
	 * This applies a penalty for largest numbers of students are timetabled at the beginning of the examination session.
	 *
	 * See 'Larger Exams Constraints' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int frontloadPenalty() {
		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("FRONTLOAD")).findFirst().orElse(null);
		if (weighting == null) return 0;

		List<Exam> largestExams = problem.exams.stream()
				.sorted((e1, e2) -> e2.students.size() - e1.students.size())
				.collect(Collectors.toList()).subList(0, weighting.paramOne);
		int lastPeriodIndex = problem.periods.size() - weighting.paramTwo;
		if (lastPeriodIndex < 0) lastPeriodIndex = 0;
		List<Period> lastPeriods = problem.periods.subList(lastPeriodIndex, problem.periods.size());

		int penalty = 0;
		for (Exam exam : largestExams) {
			Booking examBooking = bookings.stream().filter(b -> b.exam.number == exam.number).findFirst().orElse(null);
			if (examBooking == null) continue;
			boolean isInLastPeriods = lastPeriods.stream().anyMatch(p -> p.number == examBooking.period.number);
			if (isInLastPeriods) penalty += weighting.paramThree;
		}

		return penalty;
	}

	/**
	 * For each period, if a room used within the solution has an associated penalty, the penalty for that room for that
	 * period is calculated by multiplying the associated penalty by the number of times the room is used.
	 *
	 * See 'Room Penalty' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int roomPenalty() {
		int penalty = 0;

		for (Booking booking : bookings) {
			penalty += booking.room.penalty;
		}

		return penalty;
	}

	/**
	 * For each period the penalty is calculated by multiplying the associated penalty by the number of times the exams
	 * timetabled within that period.
	 *
	 * See 'Period Penalty' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int periodPenalty() {
		int penalty = 0;

		for (Booking booking : bookings) {
			penalty += booking.period.penalty;
		}

		return penalty;
	}
}
