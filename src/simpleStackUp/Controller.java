package simpleStackUp;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Objects;

public class Controller {

    @FXML private TableView<StackLine> stackLineTable;
    @FXML private Button runMonteCarloBTN;
    @FXML private CheckBox sigmaOneCBX;
    @FXML private CheckBox sigmaTwoCBX;
    @FXML private CheckBox sigmaThreeCBX;
    @FXML private TextField sampleSizeTF;
    @FXML private TextField descriptionTF;
    @FXML private TextField nominalTF;
    @FXML private TextField toleranceTF;
    @FXML private Button plusMinusBTN;
    @FXML private Button addLineBTN;
    @FXML private Button deleteLineBTN;
    @FXML private Button editLineBTN;
    @FXML private TextField statMinTF;
    @FXML private TextField nomTF;
    @FXML private TextField statMaxTF;
    @FXML private ComboBox<String> distributionFactorCB;
    @FXML private TableColumn plusMinusCol;

    private ObservableList<StackLine> data;

    private ComponentStack thisStack = new ComponentStack();

    public void initialize(){
        initializeConfidence();
        initializeSampleSize();
        initializeDistributionFactor();

        //Nominal dimension should be numeric
        nominalTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")){
                    nominalTF.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        //Tolerance should be numeric
        toleranceTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")){
                    toleranceTF.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }

    @FXML
    protected void plusMinusBTNPress(){
        if (plusMinusBTN.getText().equals("+")) {
            plusMinusBTN.setText("-");
        } else {
            plusMinusBTN.setText("+");
        }
    }

    @FXML
    protected void runMonteCarloAnalysisBTNPress(){
        sampleSizeTFChange();
        thisStack.runMonteCarlo();
        showResults();
    }

    @FXML
    protected void selectSigmaOneCBXPress(){
        sigmaOneCBX.setSelected(true);
        sigmaTwoCBX.setSelected(false);
        sigmaThreeCBX.setSelected(false);
        thisStack.setConfidenceLevel(1);
    }

    @FXML
    protected void selectSigmaTwoCBXPress(){
        sigmaTwoCBX.setSelected(true);
        sigmaThreeCBX.setSelected(false);
        sigmaOneCBX.setSelected(false);
        thisStack.setConfidenceLevel(2);
    }

    @FXML
    protected void selectSigmaThreeCBXPress(){
        sigmaThreeCBX.setSelected(true);
        sigmaTwoCBX.setSelected(false);
        sigmaOneCBX.setSelected(false);
        thisStack.setConfidenceLevel(3);
    }

    @FXML
    protected void sampleSizeTFChange(){
        try{
            thisStack.setSampleSize(Integer.parseInt(sampleSizeTF.getText()));
        }
        catch(NumberFormatException e){
            thisStack.setSampleSize(1000000);
        }
        sampleSizeTF.setText(Integer.toString(thisStack.getSampleSize()));
    }

    @FXML
    protected void addLineBTNPress(){
        if(checkLineFields()){
            data = stackLineTable.getItems();
            StackLine sl = new StackLine(getLineDescription(), getNominalValue(), getToleranceValue(), getPlusMinusValue());
            data.add(sl);
            thisStack.addStackLine(sl);
            clearLineFields();
            statMinTF.clear();
            statMaxTF.clear();
            nomTF.clear();
            nominalTF.clear();
        }
    }

    @FXML
    protected void deleteLineBTNPress(){
        if(!stackLineTable.getSelectionModel().isEmpty()){
            thisStack.deleteStackLine(stackLineTable.getSelectionModel().getSelectedItem());
            data.remove(stackLineTable.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    protected void editLineBTNPress(){
        StackLine sl = stackLineTable.getSelectionModel().getSelectedItem();
        if (sl!=null){

            if(editLineBTN.getText().equals("Edit")){
                stackLineTable.setDisable(true);
                editLineBTN.setText("Save");
                addLineBTN.setDisable(true);
                deleteLineBTN.setDisable(true);
                descriptionTF.setText(sl.getDescription());
                nominalTF.setText(Double.toString(sl.getNominalHeight()));
                toleranceTF.setText(Double.toString(sl.getTolerance()));
                plusMinusBTN.setText(sl.getPlusMinusText());
            }
            else{
                stackLineTable.setDisable(false);
                addLineBTN.setDisable(false);
                deleteLineBTN.setDisable(false);
                thisStack.deleteStackLine(sl);
                data.remove(sl);
                sl.setDescription(descriptionTF.getText().trim());
                sl.setNominalHeight(Double.parseDouble(nominalTF.getText()));
                sl.setTolerance(Double.parseDouble(toleranceTF.getText()));
                sl.setPlusMinus(Objects.equals(plusMinusBTN.getText(), "+"));
                data.add(sl);
                thisStack.addStackLine(sl);
                editLineBTN.setText("Edit");
                descriptionTF.clear();
                nominalTF.clear();
                toleranceTF.clear();
            }
        }
    }

    @FXML
    protected void distributionFactorCBSelect(){
        if(distributionFactorCB.getValue().equals("Normal"))
            thisStack.setDistCorrectionFactor(1);
        else if(distributionFactorCB.getValue().equals("Bender"))
            thisStack.setDistCorrectionFactor(1.5);
        else{
            thisStack.setDistCorrectionFactor(1.732);
        }
    }


    private void initializeConfidence(){
        if(thisStack.getConfidenceLevel() == 1){
            selectSigmaOneCBXPress();
        }
        else if(thisStack.getConfidenceLevel()==2){
            selectSigmaTwoCBXPress();
        }
        else{
            selectSigmaThreeCBXPress();
        }
    }

    private void initializeSampleSize(){
        sampleSizeTF.setText(Integer.toString(thisStack.getSampleSize()));
    }

    private void initializeDistributionFactor(){
        if(thisStack.getDistCorrectionFactor() == 1){
            distributionFactorCB.setValue("Normal");
        }
        else if(thisStack.getDistCorrectionFactor() == 1.5){
            distributionFactorCB.setValue("Bender");
        }
        else if(thisStack.getDistCorrectionFactor() == 1.732){
            distributionFactorCB.setValue("Flat");
        }
    }

    private boolean checkLineFields(){
        if(descriptionTF.getText() == null || descriptionTF.getText().trim().isEmpty() ||
           nominalTF.getText() == null     || nominalTF.getText().trim().isEmpty() ||
           toleranceTF.getText() == null   || toleranceTF.getText().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private String getLineDescription(){
        return descriptionTF.getText();
    }

    private double getNominalValue(){
        return Double.parseDouble(nominalTF.getText());
    }

    private double getToleranceValue(){
        return Double.parseDouble(toleranceTF.getText());
    }

    private boolean getPlusMinusValue(){
        if(plusMinusBTN.getText().equals("+")){
            return true;
        }
        else{return false;}
    }

    private void clearLineFields(){
        descriptionTF.clear();
        nominalTF.clear();
        toleranceTF.clear();
    }

    private void showResults(){
        statMaxTF.setText(String.format("%.4f",thisStack.getStatMax()));
        statMinTF.setText(String.format("%.4f",thisStack.getStatMin()));
        nomTF.setText(String.format("%.4f",thisStack.getNominal()));
    }

}

