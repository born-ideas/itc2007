package dev.born.itc2007;

/**
 * Models a hard constraint associated with a room.
 */
public class RoomHardConstraint {
	public final int examNum;
	public final String constraintType;

	/**
	 * @param examNum        - the exam number of the exam to which the constraint applies.
	 * @param constraintType - should always be ROOM_EXCLUSIVE, An exam must be timetabled in a room by itself.
	 */
	public RoomHardConstraint(int examNum, String constraintType) {
		if (!constraintType.equals("ROOM_EXCLUSIVE"))
			throw new IllegalArgumentException(constraintType + " is not supported.");

		this.examNum = examNum;
		this.constraintType = constraintType;
	}
}
