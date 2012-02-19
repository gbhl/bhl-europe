package com.bhle.access.jms;

import javax.jms.JMSException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.access.jms.util.FedoraAtomMessage;
import com.bhle.access.jms.util.Method;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JmsTest {
	@Autowired
	private JmsProducer producer;

	@Autowired
	private AtomFedoraJmsListenerDumb atomListener;

	@Autowired
	private FedoraJmsAtomMessageStubUtil util;

	@Before
	public void init() {
		try {
			// wait for FedoraJmsContainer to start
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReceiveIngestMessageFromFedora() throws JMSException,
			InterruptedException {
		FedoraAtomMessage atom;

		producer.send(util.getIngestPlainMessage());

		// wait enough time for the message to come
		Thread.sleep(1000);
		atom = atomListener.getAtomMessage();
		Assert.assertEquals(Method.INGEST, atom.getMethodName());
	}

	@Test
	public void testReceivePurgeMessageFromFedora() throws JMSException,
			InterruptedException {
		FedoraAtomMessage atom;

		producer.send(util.getPurgeObjectPlainMessage());

		// wait enough time for the message to come
		Thread.sleep(1000);
		atom = atomListener.getAtomMessage();
		Assert.assertEquals(Method.PURGE_OBJECT, atom.getMethodName());

		producer.send(util.getPurgeDatastreamPlainMessage());

		// wait enough time for the message to come
		Thread.sleep(1000);
		atom = atomListener.getAtomMessage();
		Assert.assertEquals(Method.PURGE_DATASTREAM, atom.getMethodName());
	}

	@Test
	public void testReceiveModifyMessageFromFedora() throws JMSException,
			InterruptedException {
		FedoraAtomMessage atom;

		producer.send(util.getModifyObjectPlainMessage());

		// wait enough time for the message to come
		Thread.sleep(1000);
		atom = atomListener.getAtomMessage();
		Assert.assertEquals(Method.MODIFY_OBJECT, atom.getMethodName());

		producer.send(util.getModifyDatastreamPlainMessage());

		// wait enough time for the message to come
		Thread.sleep(1000);
		atom = atomListener.getAtomMessage();
		Assert.assertEquals(Method.MODIFY_DATASTREAM, atom.getMethodName());
	}
}
