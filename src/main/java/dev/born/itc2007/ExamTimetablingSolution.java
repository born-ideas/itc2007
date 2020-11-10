package dev.born.itc2007;

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
	 * @param problem  - the problem that this solution will solve.
	 * @param bookings - the list of bookings for this solution.
	 */
	public ExamTimetablingSolution(ExamTimetablingProblem problem, List<Booking> bookings) {
		this.problem = problem;
		this.bookings = bookings;
	}

	/**
	 * @return a string representation of the bookings. One line should describe each exam.
	 * The exams should be in sequential order as that given in the input file. The timeslot number, the room number.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < bookings.size(); i++) {
			final int examNum = i;
			Booking booking = bookings.stream().filter(b -> b.exam.number == examNum).findFirst().orElseThrow(UnknownError::new);

			builder.append(booking.period.number).append(",").append(booking.room.number).append("\n");
		}

		return builder.toString();
	}

	/**
	 * @return the number of hard constraint violations (Distance to Feasibility) which is the total of the following:
	 * <p>
	 * Conflicts: Two conflicting exams in the same period.
	 * <p>
	 * RoomOccupancy: More seating required in any individual period than that available.
	 * <p>
	 * PeriodUtilisation: More time required in any individual period than that available.
	 * <p>
	 * PeriodRelated: Ordering requirements not obeyed.
	 * <p>
	 * RoomRelated: Room requirements not obeyed
	 * <p>
	 * See http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm for more details.
	 */
	public int distanceToFeasibility() {
		return this.conflictingExams() + this.overbookedPeriods() + this.tooShortPeriods()
				+ this.periodConstraintViolations() + this.roomConstraintViolations();
	}

	/**
	 * @return the total penalty of soft constraint violations. This is the total of the following:
	 * <p>
	 * Two Exams in a Row
	 * <p>
	 * Two Exams in a Day
	 * <p>
	 * Period Spread
	 * <p>
	 * Mixed Durations
	 * <p>
	 * Larger Exams Constraints
	 * <p>
	 * Room Penalty
	 * <p>
	 * See http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm for more details.
	 */
	public int softConstraintViolations() {
		return this.twoInARowPenalty() + this.twoInADayPenalty() + this.frontloadPenalty() +
				this.mixedDurationsPenalty() + this.periodSpreadPenalty() + this.roomPenalty() + this.periodPenalty();
	}

	private int conflictingExams = -1;

	/**
	 * @return the number of exams that occur at the same time and share students.
	 */
	public int conflictingExams() {
		if (conflictingExams != -1) return conflictingExams;

		conflictingExams = 0;
		for (Booking bookingA : bookings) {
			for (Booking bookingB : bookings) {
				if (bookingA.equals(bookingB)) continue;
				boolean doClash = bookingA.period.number == bookingB.period.number;
				boolean doShareStudents = problem.clashMatrix[bookingA.exam.number][bookingB.exam.number] > 0;
				if (doClash && doShareStudents) conflictingExams++;
			}
		}
		return conflictingExams;
	}

	private int overbookedPeriods = -1;

	/**
	 * @return the number of periods where the required capacity exceeds the capacity of the room.
	 */
	public int overbookedPeriods() {
		if (overbookedPeriods != -1) return overbookedPeriods;

		overbookedPeriods = 0;
		for (Period period : problem.periods) {
			for (Room room : problem.rooms) {
				List<Booking> periodRoomBookings = bookings.stream().filter(b -> b.period.number == period.number && b.room.number == room.number).collect(Collectors.toList());
				int numSeatsNeeded = periodRoomBookings.stream().mapToInt(b -> b.exam.students.size()).sum();
				if (numSeatsNeeded > room.capacity) overbookedPeriods++;
			}
		}
		return overbookedPeriods;
	}

	private int tooShortPeriods = -1;

	/**
	 * @return the number of periods that are too short for the exams that have been booked in those periods.
	 */
	public int tooShortPeriods() {
		if (tooShortPeriods != -1) return tooShortPeriods;

		tooShortPeriods = 0;
		HashSet<Booking> seen = new HashSet<>();
		for (Booking booking : bookings) {
			if (booking.exam.duration > booking.period.duration && seen.add(booking)) tooShortPeriods++;
		}
		return tooShortPeriods;
	}

	private int periodConstraintViolations = -1;

	/**
	 * @return the number of period constraint violations - See 'PeriodRelated' http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int periodConstraintViolations() {
		if (periodConstraintViolations != -1) return periodConstraintViolations;

		periodConstraintViolations = 0;
		for (PeriodHardConstraint constraint : problem.periodHardConstraints) {
			Booking bookingOne = bookings.stream().filter(b -> b.exam.number == constraint.examOneNum).findFirst().orElse(null);
			Booking bookingTwo = bookings.stream().filter(b -> b.exam.number == constraint.examTwoNum).findFirst().orElse(null);
			if (bookingOne == null || bookingTwo == null) continue;

			if (constraint.constraintType.equals("EXAM_COINCIDENCE")) {
				if (problem.clashMatrix[constraint.examOneNum][constraint.examTwoNum] > 0) continue;
				if (bookingOne.period.number != bookingTwo.period.number) periodConstraintViolations++;
			}

			if (constraint.constraintType.equals("EXCLUSION")) {
				if (bookingOne.period.number == bookingTwo.period.number) periodConstraintViolations++;
			}

			if (constraint.constraintType.equals("AFTER")) {
				if (bookingOne.period.getDateTime().isAfter(bookingTwo.period.getDateTime()))
					periodConstraintViolations++;
			}
		}
		return periodConstraintViolations;
	}

	private int roomConstraintViolations = -1;

	/**
	 * @return The number of room hard constraint violations in this solution. See 'RoomRelated' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int roomConstraintViolations() {
		if (roomConstraintViolations != -1) return roomConstraintViolations;

		roomConstraintViolations = 0;
		for (RoomHardConstraint constraint : problem.roomHardConstraints) {
			if (constraint.constraintType.equals("ROOM_EXCLUSIVE")) {
				Booking booking = bookings.stream().filter(b -> b.exam.number == constraint.examNum).findFirst().orElse(null);
				if (booking == null) continue;
				boolean isNotBookedAlone = bookings.stream().anyMatch(b -> b.room.number == booking.room.number && b.period.number == booking.period.number);
				if (isNotBookedAlone) roomConstraintViolations++;
			}
		}
		return roomConstraintViolations;
	}

	private int twoInARowPenalty = -1;

	/**
	 * @return The number of occurrences where two examinations are taken by students straight after one another. See 'Two Exams in a Row' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int twoInARowPenalty() {
		if (twoInARowPenalty != -1) return twoInARowPenalty;

		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("TWOINAROW")).findFirst().orElse(null);
		if (weighting == null) return 0;

		twoInARowPenalty = 0;
		for (Booking bookingA : bookings) {
			for (Booking bookingB : bookings) {
				boolean areInARow = Math.abs(bookingA.period.number - bookingB.period.number) == 1;
				boolean areOnSameDay = bookingA.period.date.isEqual(bookingB.period.date);
				if (areInARow && areOnSameDay)
					twoInARowPenalty += weighting.paramOne * problem.clashMatrix[bookingA.exam.number][bookingB.exam.number];
			}
		}
		return twoInARowPenalty;
	}

	private int twoInADayPenalty = -1;

	/**
	 * @return The number of occurrences of students having two exams in a day which are not directly adjacent. See 'Two Exams in a Day' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int twoInADayPenalty() {
		if (twoInADayPenalty != -1) return twoInADayPenalty;

		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("TWOINADAY")).findFirst().orElse(null);
		if (weighting == null) return 0;

		twoInADayPenalty = 0;
		for (Booking bookingA : bookings) {
			for (Booking bookingB : bookings) {
				if (bookingA.equals(bookingB)) continue;
				boolean areNotAdjacent = Math.abs(bookingA.period.number - bookingB.period.number) != 1;
				boolean areOnSameDay = bookingA.period.date.isEqual(bookingB.period.date);
				if (areNotAdjacent && areOnSameDay)
					twoInADayPenalty += weighting.paramOne * problem.clashMatrix[bookingA.exam.number][bookingB.exam.number];
			}
		}
		return twoInADayPenalty;
	}

	private int periodSpreadPenalty = -1;

	/**
	 * @return Occurrences of enrolled students who have to sit other exams within the desired period spread. See 'Period Spread' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int periodSpreadPenalty() {
		if (periodSpreadPenalty != -1) return periodSpreadPenalty;

		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("PERIOD_SPREAD")).findFirst().orElse(null);
		if (weighting == null) return 0;

		periodSpreadPenalty = 0;
		for (Booking bookingA : bookings) {
			for (Booking bookingB : bookings) {
				int spread = bookingB.period.number - bookingA.period.number;
				if (spread <= 0) continue;
				boolean areWithinSpread = spread <= weighting.paramOne;
				if (areWithinSpread)
					periodSpreadPenalty += problem.clashMatrix[bookingA.exam.number][bookingB.exam.number];
			}
		}
		return periodSpreadPenalty;
	}

	private int mixedDurationsPenalty = -1;

	/**
	 * @return This applies a penalty to a ROOM and PERIOD (not Exam) where there are mixed durations. See 'Mixed Durations' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int mixedDurationsPenalty() {
		if (mixedDurationsPenalty != -1) return mixedDurationsPenalty;

		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("NONMIXEDDURATIONS")).findFirst().orElse(null);
		if (weighting == null) return 0;

		mixedDurationsPenalty = 0;
		for (Period period : problem.periods) {
			for (Room room : problem.rooms) {
				List<Booking> myBookings = bookings.stream().filter(b -> b.period.number == period.number && b.room.number == room.number).collect(Collectors.toList());
				if (myBookings.isEmpty()) continue;
				HashSet<Object> seen = new HashSet<>();
				myBookings.removeIf(b -> !seen.add(b.exam.duration));
				int numDifferentDurations = myBookings.size();
				mixedDurationsPenalty += (numDifferentDurations - 1) * weighting.paramOne;
			}
		}
		return mixedDurationsPenalty;
	}

	private int frontloadPenalty = -1;

	/**
	 * @return This applies a penalty for largest numbers of students are timetabled at the beginning of the examination session. See 'Larger Exams Constraints' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int frontloadPenalty() {
		if (frontloadPenalty != -1) return frontloadPenalty;

		InstitutionalWeighting weighting = problem.institutionalWeightings.stream().filter(w -> w.weightingType.equals("FRONTLOAD")).findFirst().orElse(null);
		if (weighting == null) return 0;

		List<Exam> largestExams = problem.exams.stream()
				.sorted((e1, e2) -> e2.students.size() - e1.students.size())
				.collect(Collectors.toList()).subList(0, weighting.paramOne);
		int lastPeriodIndex = problem.periods.size() - weighting.paramTwo;
		if (lastPeriodIndex < 0) lastPeriodIndex = 0;
		List<Period> lastPeriods = problem.periods.subList(lastPeriodIndex, problem.periods.size());

		frontloadPenalty = 0;
		for (Exam exam : largestExams) {
			Booking examBooking = bookings.stream().filter(b -> b.exam.number == exam.number).findFirst().orElse(null);
			if (examBooking == null) continue;
			boolean isInLastPeriods = lastPeriods.stream().anyMatch(p -> p.number == examBooking.period.number);
			if (isInLastPeriods) frontloadPenalty += weighting.paramThree;
		}

		return frontloadPenalty;
	}

	private int roomPenalty = -1;

	/**
	 * @return For each period, if a room used within the solution has an associated penalty, the penalty for that room for that
	 * period is calculated by multiplying the associated penalty by the number of times the room is used. See 'Room Penalty' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int roomPenalty() {
		if (roomPenalty != -1) return roomPenalty;

		roomPenalty = 0;
		for (Booking booking : bookings) {
			roomPenalty += booking.room.penalty;
		}
		return roomPenalty;
	}

	private int periodPenalty = -1;

	/**
	 * @return For each period the penalty is calculated by multiplying the associated penalty by the number of times the exams
	 * timetabled within that period. See 'Period Penalty' at http://www.cs.qub.ac.uk/itc2007/examtrack/exam_track_index_files/examevaluation.htm.
	 */
	public int periodPenalty() {
		if (periodPenalty != -1) return periodPenalty;

		periodPenalty = 0;
		for (Booking booking : bookings) {
			periodPenalty += booking.period.penalty;
		}
		return periodPenalty;
	}
}
