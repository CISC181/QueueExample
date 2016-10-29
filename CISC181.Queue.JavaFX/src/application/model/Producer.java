package application.model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import application.view.ShowQueueController;

import java.util.*;

public class Producer implements Runnable {

	protected BlockingQueue queue = null;
	private ShowQueueController controller;
	private int iRandomInterval;
	private eBlockingMethod eBlockingMethod;

	public Producer(BlockingQueue queue, ShowQueueController controller, eBlockingMethod eBlockingMethod, int iRandomInterval) {
		this.queue = queue;
		this.controller = controller;
		this.eBlockingMethod = eBlockingMethod;
		this.iRandomInterval = iRandomInterval;
	}

	private Random Random = new Random();

	protected int getiRandomInterval() {
		return iRandomInterval;
	}

	public void setiRandomInterval(int iRandomInterval) {
		this.iRandomInterval = iRandomInterval;
	}

	public eBlockingMethod geteBlockingMethod() {
		return eBlockingMethod;
	}

	public void seteBlockingMethod(eBlockingMethod eBlockingMethod) {
		this.eBlockingMethod = eBlockingMethod;
	}

	public void run() {
		try {
			while (true) {
				int i = Random.nextInt(100);
				Thread.sleep(iRandomInterval);
				try {
					switch (eBlockingMethod) {
					case ADD:
						queue.add(i);
						break;
					case OFFER:
						if (!queue.offer(i))
						{
							throw new Exception("Offer Method failed");
						}
						break;
					case PUT:
						queue.put(i);
						break;
					case OFFER_TIME:
						if (!queue.offer(i, 1000, TimeUnit.MILLISECONDS))
						{
							throw new Exception("Offer Method failed");
						}
						break;
					}
				} catch (Exception e) {
					controller.setTxtMessage(e.getMessage(),true);
					controller.IncrementLblProducerExceptions();
				}
				controller.UpdateProgress((double) queue.size() / 100);
				controller.ShowTxtCapacity(Integer.toString(queue.remainingCapacity()));
				controller.ShowTxtSize(Integer.toString(queue.size()));
				
			}
		} catch (InterruptedException e) {
			controller.IncrementLblProducerExceptions();
			controller.setTxtMessage("Producer Thread Interrupted");
			
		} catch (IllegalStateException e) {
			controller.IncrementLblProducerExceptions();
			controller.setTxtMessage(e.getMessage(),true);
		}
	}
}