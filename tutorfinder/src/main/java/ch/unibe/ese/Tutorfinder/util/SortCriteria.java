package ch.unibe.ese.Tutorfinder.util;


public enum SortCriteria {
	
	/**
	 * The singleton instance for an order by rating.
	 * This has the numeric value of {@code 0}.
	 */
	RATING("Rating"),
	/**
	 * The singleton instance for an order by grade.
	 * This has the numeric value of {@code 1}.
	 */
	GRADE("Average grade"),
	/**
	 * The singleton instance for an order by alphabet.
	 * This has the numeric value of {@code 2}.
	 */
	ALPHABETICAL("Name");
	
	private String s;
	
	private SortCriteria(String s) {
		this.s = s;
	}
	
	/**
     * Private cache of all the constants.
     */
    private static final SortCriteria[] ENUMS = SortCriteria.values();
	
	
    /**
     * Obtains an instance of {@code SortOrder} from an {@code int} value.
     * <p>
     * {@code SortOrder} is an enum representing 
     * {@code RATING, AVAILABLE, RESERVED, ARRANGED}.
     * This factory allows the enum to be obtained from the {@code int} value.
     * The {@code int} value follows the javadoc above.
     *
     * @param availability  the availability to represent, from 0 (Unavailable) to 3 (Arranged)
     * @return the availability singleton, not null
     */
    public static SortCriteria of(int sortOrder) {
        assert (sortOrder >= 0 || sortOrder <= ENUMS.length);
        return ENUMS[sortOrder];
    }
    
    public static SortCriteria forName(String name) {
    	for(SortCriteria element : ENUMS) {
    		if (name.equals(element.s)) {
    			return element;
    		}
    	}
    	throw new RuntimeException();
    }
    
    /**
     * Gets the availability {@code int} value.
     * <p>
     * The values are numbered as follows: 0 (Rating), 1 (Grade), 2 (Alphabetical).
     *
     * @return the sort order as an integer representation
     */
    public Integer getValue() {
        return ordinal();
    }
    
    public String toString() {
    	return s;
    }
    
}
