package generator;

import java.util.concurrent.Callable;

import consumer.ConsumerListener;

import util.Product;

public interface Generator extends Callable<Product> {
	public Product generate();

	public void setProduct(Product product);

	public void addConsumerListener(ConsumerListener listener);
}
