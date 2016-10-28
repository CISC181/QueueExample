package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.MainApp;
import application.model.eBlockingMethod;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ShowQueueController implements Initializable {

	private int iXAxisValue = 0;

	@FXML
	private ProgressIndicator bpiQueueSize;
	@FXML
	private ProgressBar bpQueueSize;
	@FXML
	private Button btnClearText;
	@FXML
	private Button btnStartStop;
	
	@FXML
	private Button btnPauseResume;
	@FXML
	private ComboBox cmbConsumer;
	@FXML
	private ComboBox cmbProducer;
	private application.MainApp MainApp;
	@FXML
	private Slider sdConsumer;

	@FXML
	private Slider sdProducer;

	@FXML
	private TextArea txtMessage;

	@FXML
	private TextField txtProducerExceptions;
	@FXML
	private TextField txtConsumerExceptions;

	@FXML
	private Pane pneRight;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		sdProducer.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				SliderMoved(sdProducer, newValue.intValue());
			}
		});

		sdConsumer.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				try {
					SliderMoved(sdConsumer, newValue.intValue());
				} catch (Exception e) {
					setTxtMessage(e.getMessage());
				}

			}
		});

		cmbProducer.getItems().add("ADD");
		cmbProducer.getItems().add("OFFER");
		cmbProducer.getItems().add("PUT");
		cmbProducer.getItems().add("OFFER w/Time");
		cmbProducer.getSelectionModel().select("PUT");

		cmbConsumer.getItems().add("REMOVE");
		cmbConsumer.getItems().add("POLL");
		cmbConsumer.getItems().add("TAKE");
		cmbConsumer.getItems().add("POLL w/Time");
		cmbConsumer.getSelectionModel().select("TAKE");

		pneRight.getChildren().add(createChart());
		
		
		btnPauseResume.setText("Pause");
	}

	protected LineChart<Number, Number> createChart() {

		final NumberAxis xAxis = new NumberAxis();

		//final NumberAxis yAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis("Y-Axis", 0d, 100d, 20);

		final LineChart<Number, Number> lc = new LineChart<Number, Number>(xAxis, yAxis);

		// setup chart

		lc.setTitle("Basic LineChart");

		xAxis.setLabel("X Axis");

		yAxis.setLabel("Y Axis");

		// add starting data

		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

		series.setName("Data Series 1");

		lc.getData().add(series);
		lc.setMaxSize(500, 200);
		return lc;

	}

	@FXML
	public final void btnClearText_Click(ActionEvent event) {
		Button btn = (Button) event.getSource();
		txtMessage.clear();
		
		Platform.runLater(() -> {
			try {
				LineChart lc = (LineChart) pneRight.getChildren().get(0);
				lc.getData().clear();
			} catch (Exception ex) {
				System.out.println("Exception in update progress");
			}
		});

	}
	
	@FXML
	public final void btnPauseResume_Click(ActionEvent event) {
		
		Button btn = (Button) event.getSource();
		
		if (btn.getText().contentEquals("Pause")) {
			btnPauseResume.setText("Resume");
			MainApp.PauseResume(0);
		} else {
			btnPauseResume.setText("Pause");
			MainApp.PauseResume(1000);
		}
	}
	
	@FXML
	public final void btnStartStop_Click(ActionEvent event) {
		Button btn = (Button) event.getSource();

		eBlockingMethod Pmethod = eBlockingMethod.get(cmbProducer.getSelectionModel().getSelectedItem().toString());
		eBlockingMethod Cmethod = eBlockingMethod.get(cmbConsumer.getSelectionModel().getSelectedItem().toString());

		if (btn.getText().contentEquals("Start")) {
			MainApp.LaunchRace(this, (int) sdProducer.getValue(), (int) sdConsumer.getValue(), Pmethod, Cmethod);
			btnStartStop.setText("Stop");
		} else {
			MainApp.KillRace();
			btnStartStop.setText("Start");
		}

	}

	@FXML
	public final void cmbConsumer_ItemSelected(ActionEvent event) {

		eBlockingMethod method = eBlockingMethod.get(cmbConsumer.getSelectionModel().getSelectedItem().toString());
		if (MainApp.getConsumer() != null)
			MainApp.getConsumer().seteBlockingMethod(method);
	}

	@FXML
	public final void cmbProducer_ItemSelected(ActionEvent event) {

		eBlockingMethod method = eBlockingMethod.get(cmbProducer.getSelectionModel().getSelectedItem().toString());
		if (MainApp.getProducer() != null)
			MainApp.getProducer().seteBlockingMethod(method);

	}

	public String getTxtMessage() {
		return txtMessage.getText();
	}

	public void setMainApp(MainApp mainApp) {
		this.MainApp = mainApp;
	}

	public void setTxtMessage(String strMessage) {
		try {
			this.txtMessage.setText(this.txtMessage.getText() + "\n" + strMessage);
		}

		catch (Exception e) {

		}
	}

	public void setTxtMessage(String strMessage, boolean bForce) {
		try {
			this.txtMessage.setText(strMessage);
		} catch (Exception e) {

		}

	}

	public void IncrementLblProducerExceptions() {

		int iCurrentValue = Integer.parseInt(txtProducerExceptions.getText().toString());
		iCurrentValue++;
		this.txtProducerExceptions.setText(Integer.valueOf(iCurrentValue).toString());

	}

	public void IncrementLblConsumerExceptions() {
		int iCurrentValue = Integer.parseInt(txtConsumerExceptions.getText().toString());
		iCurrentValue++;
		this.txtConsumerExceptions.setText(Integer.valueOf(iCurrentValue).toString());

	}

	public final void SliderMoved(Slider s, Number n) {

		if (s.getId().equals("sdConsumer")) {
			if (MainApp.getConsumer() != null)
				MainApp.getConsumer().setiRandomInterval((int) n);
		} else if (s.getId().equals("sdProducer")) {
			if (MainApp.getProducer() != null)
				MainApp.getProducer().setiRandomInterval((int) n);
		}
	}

	public final void UpdateProgress(double dValue) {
		bpQueueSize.setProgress(dValue);

		Platform.runLater(() -> {
			try {
				bpiQueueSize.setProgress(dValue);
			} catch (Exception ex) {
				System.out.println("Exception in update progress");
			}
		});
	}

	public final void PaintChart(int iValue) {

		
		iXAxisValue++;

		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		series.getData().add(new XYChart.Data<Number, Number>((double) iXAxisValue, (double) iValue));

		if (iXAxisValue % 10 == 0) {
			Platform.runLater(() -> {
				try {
					LineChart lc = (LineChart) pneRight.getChildren().get(0);
					lc.getData().add(series);
				} catch (Exception ex) {
					System.out.println("Exception in update progress");
				}
			});
		}

	}

}
