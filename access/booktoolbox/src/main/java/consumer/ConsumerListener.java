package consumer;

import util.Product;

public interface ConsumerListener {
	void publish(Product product);
}
