package dev.born.itc2007;

/**
 * Models a hard constraint associated with a period.
 */
public class PeriodHardConstraint {
	public final int examOneNum;
	public final String constraintType;
	public final int examTwoNum;

	/**
	 * @param examOneNum     the first exam for the constraint.
	 * @param constraintType the constraint type (one of EXAM_COINCIDENCE, EXCLUSION, and AFTER).
	 *                       e.g. 0, EXAM_COINCIDENCE, 1    Exam 0 and Exam 1 should be timetabled in the same period.
	 *                       If two exams are set associated in this manner yet 'clash' with each other due to
	 *                       student enrolment, this hard constraint is ignored.
	 *                       <p>
	 *                       e.g. 0, EXCLUSION, 2           Exam 0 and Exam 2 should be not be timetabled in the same period.
	 *                       <p>
	 *                       e.g. 0, AFTER, 3               Exam 0 should be timetabled after Exam 3.
	 * @param examTwoNum     the second exam for the constraint.
	 */
	public PeriodHardConstraint(int examOneNum, String constraintType, int examTwoNum) {
		this.examOneNum = examOneNum;
		this.constraintType = constraintType;
		this.examTwoNum = examTwoNum;
	}
}
