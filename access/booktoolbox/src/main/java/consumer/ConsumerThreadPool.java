package consumer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ConsumerThreadPool {
	private Executor service = Executors.newCachedThreadPool();
	
	public void execute(Runnable runnable){
		service.execute(runnable);
	}
}
