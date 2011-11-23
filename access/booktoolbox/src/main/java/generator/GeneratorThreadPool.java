package generator;


import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import util.Product;

public class GeneratorThreadPool {
	private ThreadPoolExecutor threadPool = null;
	private CompletionService<Product> service = null;

	public GeneratorThreadPool(int fixedThreadNumber) {
		this.threadPool = new ThreadPoolExecutor(fixedThreadNumber,
				fixedThreadNumber, Long.MAX_VALUE,
				TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
		this.service = new ExecutorCompletionService<Product>(threadPool);
		
		
	}

	public Future<Product> execute(Generator generator) {
		return service.submit(generator);
	}

	public boolean isFull() {
		return threadPool.getCorePoolSize() <= threadPool.getActiveCount();
	}

	public Product waitAndGetProduct() throws InterruptedException,
			ExecutionException {
		return service.take().get();
	}
}
