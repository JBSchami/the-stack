package simpleStackUp;

//Meant as a data object to hold the results of the monte carlo analysis.
public class MonteCarloResults implements java.io.Serializable{

	private double absMax;
	private double statMax;
	private double nominal;
	private double statMin;
	private double absMin;
	private static final long serialVersionUID = 12L;

	/*CONSTRUCTORS =====================================================================
	==================================================================================*/
	public MonteCarloResults() {
		absMax = 0;
		statMax = 0;
		nominal = 0;
		statMin = 0;
		absMin = 0;
	}

	/*SETTERS ==========================================================================
	==================================================================================*/
	public void setAbsMax(double value) {
		absMax = value;
	}
	
	public void setStatMax(double value) {
		statMax = value;
	}
	
	public void setNominal(double value) {
		nominal = value;
	}
	
	public void setStatMin(double value) {
		statMin = value;
	}
	
	public void setAbsMin(double value) {
		absMin = value;
	}

	/*GETTERS ==========================================================================
	==================================================================================*/
	public double getAbsMax() {
		return absMax;
	}
	
	public double getStatMax() {
		return statMax;
	}
	
	public double getNominal() {
		return nominal;
	}
	
	public double getStatMin() {
		return statMin;
	}
	
	public double getAbsMin() {
		return absMin;
	}
}
