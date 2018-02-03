package simpleStackUp;
import java.util.Random;

public class StackLine implements java.io.Serializable  {
	private String description;
	private double nominalHeight;
	private double tolerance;
	private boolean plusMinus;
	private String plusMinusText;

	private static final long serialVersionUID = 98L;

	/*CONSTRUCTORS =====================================================================
	==================================================================================*/
	//Default constructor
	public StackLine() {
		description = "";
		nominalHeight = 0;
		tolerance = 0;
		plusMinus = false;
	}

	//Constructor when all data is known
	public StackLine(String descript, double nominal, double tol, boolean pm) {
		description = descript;
		nominalHeight = nominal;
		tolerance = tol;
		plusMinus = pm;
	}

	/*GETTERS ==========================================================================
	==================================================================================*/
	public String getDescription() {
		return description;
	}

	public double getNominalHeight() {
		return nominalHeight;
	}

	public double getTolerance() {
		return tolerance;
	}

	public boolean isPlusMinus() {
		return plusMinus;
	}

	//Used to convert extract the direction from the GUI
	public String getPlusMinusText() {
		setPlusMinusText();
		return plusMinusText;
	}

	/*SETTERS ==========================================================================
	==================================================================================*/
	public void setDescription(String description) {
		this.description = description;
	}

	public void setNominalHeight(double nominalHeight) {
		this.nominalHeight = nominalHeight;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public void setPlusMinus(boolean plusMinus) {
		this.plusMinus = plusMinus;
	}

	//Sets the string value for whether the stack line adds or removes from the total stack
	public void setPlusMinusText() {
		if(plusMinus == true){
			this.plusMinusText = "+";
		}
		else {
			this.plusMinusText = "-";
		}
	}


	/*OTHER ==========================================================================
	==================================================================================*/
	//Generates random numbers according to a standard distribution.
	private double getRandomDouble(double correctionFactor) {
		Random r = new Random();
		return r.nextGaussian()*correctionFactor;
	}

	//Comparator for stack lines
	@Override
	public boolean equals(Object stk) {
		if (this == stk){
			return true;
		}
		if (stk == null){
			return false;
		}
		if (getClass() != stk.getClass()){
			return false;
		}
		StackLine other = (StackLine) stk;
		return description == other.description;
	}

	//Determines the height of the samples stack line.
	public double lineHeight(int confidenceLevel, double correctionFactor) {
		double randomNum = getRandomDouble(correctionFactor);
		while( Math.abs(randomNum) > confidenceLevel) {
			randomNum = getRandomDouble(correctionFactor);
		}
		if(Math.abs(randomNum) <= confidenceLevel) {
			double adjustmentFactor = randomNum / confidenceLevel;
			return nominalHeight + (tolerance*adjustmentFactor);
		}
		else return -1;
	}
}
