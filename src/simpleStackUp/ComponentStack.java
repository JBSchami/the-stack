package simpleStackUp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ComponentStack implements java.io.Serializable  {
	private String componentName;
	private int confidenceLevel;
	private int sampleSize;
	private double distCorrectionFactor; //Distribution correction factor used in monte carlo calculations
	private String partNumber;
	private String placementRef;
	private MonteCarloResults mcr = new MonteCarloResults();
	private ArrayList<StackLine> stackList = new ArrayList<StackLine>(); //List of the different items that make up the stackup
	private static final long serialVersionUID = 222L;


	/*CONSTRUCTORS =====================================================================
	==================================================================================*/

	//Default constructor
	public ComponentStack() {
		confidenceLevel = 1;
		sampleSize = 10000;
		componentName = "New Component";
		distCorrectionFactor = 1;
	}
	//Constructor for named component
	public ComponentStack(String name) {
		confidenceLevel = 1;
		sampleSize = 10000;
		componentName = name;
	}

	/*GETTERS ==========================================================================
	==================================================================================*/
	public StackLine[] getStackList(){
		StackLine sll[] = new StackLine[stackList.size()];
		int i = 0;
		for(StackLine sl : stackList){
			sll[i] = sl;
			i++;
		}
		return sll;
	}

	public String getComponentName() {return componentName;}

	public String getPartNumber() {return partNumber;}

	public String getPlacementRef() {return placementRef;}

	public double getDistCorrectionFactor() {return distCorrectionFactor;}

	public int getSampleSize() {return sampleSize;}

	public int getConfidenceLevel() {return confidenceLevel;}

	/*SETTERS ==========================================================================
	==================================================================================*/

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public void setPlacementRef(String placementRef) {
		this.placementRef = placementRef;
	}

	public void setDistCorrectionFactor(double distCorrectionFactor) {
		this.distCorrectionFactor = distCorrectionFactor;
	}

	public void setConfidenceLevel(int cf) {
		if(cf > 3 || cf < 0) {
			System.out.println("The confidence level must be 1, 2 or 3. Set to default (1).");
			cf = 1;
		}
		confidenceLevel = cf;
	}

	public void setSampleSize(int ss) {
		if(ss < 0 || ss > 1000000) {
			ss = 1000000;
		}
		sampleSize = ss;
	}

	/*MONTE CARLO RELATED===============================================================
	==================================================================================*/

	//Perform the monte carlo analysis
	public void runMonteCarlo() {
		double[] simulatedResults = new double[sampleSize];
		for(int i = 0; i < sampleSize; i++) {
			simulatedResults[i] = stackGap();
		}

		mcr.setStatMax((getAverage(simulatedResults)+(getStandardDeviation(simulatedResults)*confidenceLevel)));
		mcr.setStatMin((getAverage(simulatedResults)-(getStandardDeviation(simulatedResults)*confidenceLevel)));
		mcr.setAbsMax(stackGapMax());
		mcr.setAbsMin(stackGapMin());
		mcr.setNominal(stackGapNom());
	}

	//Determine the maximum gap for the stack
	private double stackGapMax() {
		double sgMax = 0;
		for (StackLine sl : stackList) {
			if (sl.isPlusMinus() == true) {
				sgMax += sl.getNominalHeight()+sl.getTolerance();
			}
			else
				sgMax -= sl.getNominalHeight()-sl.getTolerance();
		}
		return sgMax;
	}

	//Determine the minimum gap for the stack
	private double stackGapMin() {
		double sgMin = 0;
		for (StackLine sl : stackList) {
			if (sl.isPlusMinus() == true) {
				sgMin += sl.getNominalHeight()-sl.getTolerance();
			}
			else
				sgMin -= sl.getNominalHeight()+sl.getTolerance();
		}
		return sgMin;
	}

	//Determine the nominal gap for the stack
	private  double stackGapNom() {
		double nom = 0;
		for (StackLine sl : stackList) {
			if (sl.isPlusMinus() == true) {
				nom += sl.getNominalHeight();
			}
			else
				nom -= sl.getNominalHeight();
		}
		return nom;
	}

	//Return max value in an array
	private double getMax(double[] array) {
		double maxVal = array[0];
		for(int i = 0; i < array.length; i++) {
			if(array[i]>maxVal)
				maxVal = array[i];
		}
		return maxVal;
	}

	//Return min value in an array
	private double getMin(double[] array) {
		double minVal = array[0];
		for(int i = 0; i < array.length; i++) {
			if(array[i] < minVal)
				minVal = array[i];
		}
		return minVal;
	}

	//Return average value of an array
	private double getAverage(double[] array) {
		double totalSum = 0;
		for(int i = 0; i < array.length; i++) {
			totalSum += array[i];
		}
		return (totalSum/array.length);
	}

	//Determine the standard deviation for an array
	private double getStandardDeviation(double[] array) {
		double temp = 0;
		double avg = getAverage(array);
		for(int i = 0; i < array.length; i++) {
			temp += Math.pow((array[i] - avg), 2);
		}

		return (Math.pow(temp/array.length, 0.5));
	}

	//Add a stack line to describe the current scenario
	public void addStackLine(String descript, double nominal, double tolerance, boolean pm) {
		StackLine sl = new StackLine(descript, nominal, tolerance, pm);
		stackList.add(sl);
	}

	//Add a stack line to describe the current scenario
	public void addStackLine(StackLine sl){
		stackList.add(sl);
	}

	//Remove a stack line
	public void deleteStackLine(StackLine sl){
		System.out.println(sl.getDescription());
		for(StackLine sls:stackList){
			System.out.print(sls.getDescription() + " ");
		}
		System.out.println(stackList.contains(sl));
		stackList.remove(sl);
	}

	public double stackGap() {
		double stackGap = 0;
		for (StackLine sl : stackList) {
			if(sl.isPlusMinus() == true)
				stackGap += sl.lineHeight(confidenceLevel, distCorrectionFactor);
			else
				stackGap -= sl.lineHeight(confidenceLevel, distCorrectionFactor);
		}
		return stackGap;
	}

	public double getAbsMax(){
		return mcr.getAbsMax();
	}

	public double getStatMax(){
		return mcr.getStatMax();
	}

	public double getNominal(){
		return mcr.getNominal();
	}

	public double getStatMin(){
		return mcr.getStatMin();
	}

	public double getAbsMin(){
		return mcr.getAbsMin();
	}

	//Print montecarlo result to console
	public void printResults() {
		System.out.println(" Abs Max: " + mcr.getAbsMax());
		System.out.println("Stat Max: " + mcr.getStatMax());
		System.out.println(" Nomimal: " + mcr.getNominal());
		System.out.println("Stat Min: " + mcr.getStatMin());
		System.out.println(" Abs Min: " + mcr.getAbsMin());
	}

	/*MONTE CARLO RELATED===============================================================
	==================================================================================*/
	public void saveComponent() {
		try {
			String filePath = componentName +".stk";
			FileOutputStream fileOut =
					new FileOutputStream(filePath);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this);
			out.close();
			fileOut.close();
			System.out.println("Component saved in " + filePath);
		}catch(IOException i) {
			i.printStackTrace();
		}
	}

}
