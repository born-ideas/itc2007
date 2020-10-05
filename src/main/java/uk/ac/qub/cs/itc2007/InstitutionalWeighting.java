package uk.ac.qub.cs.itc2007;

/**
 * Models an Institutional Weighting (soft constraint).
 */
public class InstitutionalWeighting {
	public final String weightingType;
	public final int paramOne;
	public final int paramTwo;
	public final int paramThree;

	/**
	 * @param weightingType - one of TWOINAROW, TWOINADAY, PERIODSPREAD or NONMIXEDDURATIONS. (cannot be used for FRONTLOAD)
	 * @param paramOne      - the parameter for the given type.
	 */
	public InstitutionalWeighting(String weightingType, int paramOne) {
		if (weightingType.equals("FRONTLOAD"))
			throw new IllegalArgumentException(weightingType + " requires 3 parameters.");

		this.weightingType = weightingType;
		this.paramOne = paramOne;
		this.paramTwo = -1;
		this.paramThree = -1;
	}

	/**
	 * @param weightingType - must be FRONTLOAD
	 * @param paramOne      - the first parameter (number of largest exams)
	 * @param paramTwo      - the second parameter (number of last periods to take into account)
	 * @param paramThree    - the third parameter (the penalty or weighting)
	 */
	public InstitutionalWeighting(String weightingType, int paramOne, int paramTwo, int paramThree) {
		if (!weightingType.equals("FRONTLOAD"))
			throw new IllegalArgumentException(weightingType + " only requires 1 parameter.");

		this.weightingType = weightingType;
		this.paramOne = paramOne;
		this.paramTwo = paramTwo;
		this.paramThree = paramThree;
	}
}
