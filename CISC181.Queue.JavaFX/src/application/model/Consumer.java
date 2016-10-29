package application.model;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import application.view.ShowQueueController;

public class Consumer implements Runnable {

	private BlockingQueue queue = null;
	private ShowQueueController controller;
	private int iRandomInterval;
	private eBlockingMethod eBlockingMethod;

	public Consumer(BlockingQueue queue, ShowQueueController controller, eBlockingMethod eBlockingMethod,
			int iRandomInterval) {
		this.queue = queue;
		this.controller = controller;
		this.eBlockingMethod = eBlockingMethod;
		this.iRandomInterval = iRandomInterval;
	}

	private Random Random = new Random();

	public int getiRandomInterval() {
		return iRandomInterval;
	}

	public eBlockingMethod geteBlockingMethod() {
		return eBlockingMethod;
	}

	public void seteBlockingMethod(eBlockingMethod eBlockingMethod) {
		this.eBlockingMethod = eBlockingMethod;
	}

	public void setiRandomInterval(int iRandomInterval) {
		this.iRandomInterval = iRandomInterval;
	}

	public void run() {
		try {
			while (true) {
				int i = Random.nextInt(100);
				Thread.sleep(iRandomInterval);
				int j = 0;
				try {
					switch (eBlockingMethod) {
					case TAKE:
						j = (int) queue.take();
						break;
					case REMOVE:
						j = (int) queue.remove();
						break;
					case POLL:
						Object o = queue.poll();
						if (o == null) {
							throw new Exception("Poll method failed");

						} else if (o instanceof int[]) {
							j = (int) queue.poll();
						}
						break;
					case POLL_TIME:
						j = (int) queue.poll(1000, TimeUnit.MILLISECONDS);
						break;
					}
					
					controller.PaintChart(FindAverageValueinQueue());

				} catch (NoSuchElementException e) {
					controller.setTxtMessage("Nothing on queue to remove");
					controller.IncrementLblConsumerExceptions();
				} catch (Exception e) {
					controller.setTxtMessage(e.getMessage());
					controller.IncrementLblConsumerExceptions();
				}
				controller.UpdateProgress((double) queue.size() / 100);
			}
		} catch (InterruptedException e) {
			controller.setTxtMessage("Consumer Thread Interrupted");
			controller.IncrementLblConsumerExceptions();
		} catch (IllegalStateException e) {
			controller.IncrementLblConsumerExceptions();
			controller.setTxtMessage(e.getMessage(), true);
		}
	}
	
	
	private int FindAverageValueinQueue()
	{
		int iItemCount = 0;
		int iItemSum = 0;
		
		for(Object item : queue){
			iItemSum = iItemSum + (int)item;
			iItemCount++;
		}
		
		if (iItemCount == 0)
			return 0;
		else
			return (int)iItemSum/iItemCount;
		
	}
	
	
	
	
	
	
	
	
}