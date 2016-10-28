package application;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import application.model.*;
import application.view.ShowQueueController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	private Stage primaryStage;
	private ShowQueueController controller;
	private Producer producer;
	private Consumer consumer;
	private Thread tProducer;
	private Thread tConsumer;
	private int iSleepInterval = 0;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Blocking Queue Example");

		showMainScreen();
	}

	public void showMainScreen() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ShowQueue.fxml"));
			BorderPane mainScreen = (BorderPane) loader.load();

			// Give the controller access to the main app.
			controller = loader.getController();
			controller.setMainApp(this);

			// Show the scene containing the root layout.
			Scene scene = new Scene(mainScreen);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void LaunchRace(ShowQueueController controller, int iProducerSleep, int iConsumerSleep, eBlockingMethod ProducerMethod, eBlockingMethod ConsumerMethod) {
		
		//BlockingQueue queue = new ArrayBlockingQueue(100);
		BlockingQueue queue = new PriorityBlockingQueue(100);


		producer = new Producer(queue, controller, ProducerMethod, iProducerSleep);
		consumer = new Consumer(queue, controller, ConsumerMethod, iConsumerSleep);

		tProducer = new Thread(producer);
		tConsumer = new Thread(consumer);
	
		tProducer.start();
		tConsumer.start();
	}
	
	public void KillRace()
	{
		if (tProducer.isAlive())
		{
			tProducer.interrupt();
		}
		
		if (tConsumer.isAlive())
		{
			tConsumer.interrupt();	
		}
	
	}
	
	public void PauseResume(int iSleep)
	{
		this.iSleepInterval = iSleep;
		
		try {
			tProducer.sleep(iSleepInterval);
			tConsumer.sleep(iSleepInterval);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) {
		launch(args);
	}

	public Producer getProducer() {
		return producer;
	}

	public void setProducer(Producer producer) {
		this.producer = producer;
	}

	public Consumer getConsumer() {
		return consumer;
	}

	public void setConsumer(Consumer consumer) {
		this.consumer = consumer;
	}

}
