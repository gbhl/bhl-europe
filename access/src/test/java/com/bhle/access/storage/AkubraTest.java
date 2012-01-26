package com.bhle.access.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.akubraproject.Blob;
import org.akubraproject.BlobStoreConnection;
import org.akubraproject.DuplicateBlobException;
import org.akubraproject.UnsupportedIdException;
import org.akubraproject.map.IdMappingBlobStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.access.convert.ConvertorManager;
import com.bhle.access.convert.DatastreamConvertor;
import com.bhle.access.convert.Olef2OlefConvertor;
import com.bhle.access.storage.akubra.mapper.file.FileMapper;
import com.bhle.access.storage.akubra.mapper.file.SubFolderFileMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AkubraTest {

	private static final Logger logger = LoggerFactory
			.getLogger(AkubraTest.class);

	@Autowired
	private IdMappingBlobStore blobStore;

	@Autowired
	private LowLevelStorage llStorage;

	@Before
	public void init() {
		Olef2OlefConvertor olefConvertor = new Olef2OlefConvertor();
		List<DatastreamConvertor> convertors = new ArrayList<DatastreamConvertor>();
		convertors.add(olefConvertor);
		ConvertorManager.convertors = convertors;
	}

	@Test
	public void testFileMapperToInternalFileWithSubFolder() {
		FileMapper fileMapper = new SubFolderFileMapper();

		String internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:a0hhmgs6/JPG"));
		Assert.assertEquals("a0hhmgs6_jpg.*", internalFile);

		internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:a0hhmgs6-00001/JPG"));
		Assert.assertEquals("jpg/a0hhmgs6_00001.*", internalFile);

		internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:a0hhmgs6-00001"));
		Assert.assertEquals("*/a0hhmgs6_00001.*", internalFile);
	}

	@Test
	public void testFileMapperToInternalFileWithoutSubFolder() {
		FileMapper fileMapper = new SubFolderFileMapper();

		String internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:a0hhmgs6/JPG"));
		Assert.assertEquals("a0hhmgs6_jpg.*", internalFile);

		internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:a0hhmgs6"));
		Assert.assertEquals("a0hhmgs6_*.*", internalFile);

		internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:a0hhmgs6-0001"));
		Assert.assertEquals("*/a0hhmgs6_0001.*", internalFile);
	}

	@Test
	public void testFileMapperToExternalFile() {
		FileMapper fileMapper = new SubFolderFileMapper();

		String externalFile = fileMapper.getExternalFile(URI
				.create("file:a0/hh/mg/s6/a0hhmgs6/a0hhmgs6_dc.xml"));
		Assert.assertEquals("DC", externalFile);

		externalFile = fileMapper.getExternalFile(URI
				.create("file:a0/hh/mg/s6/a0hhmgs6/dc/a0hhmgs6_00001.xml"));
		Assert.assertEquals("DC", externalFile);
	}

	@Test
	public void testInvalidDirectFileMapper() {
		FileMapper fileMapper = new SubFolderFileMapper();
		String externalFile = fileMapper.getExternalFile(URI
				.create("file:a0/hh/mg/s6/a0hhmgs6.jpg"));
		Assert.assertNull(externalFile);
	}

	@Test
	public void testIdMapperStore() throws UnsupportedOperationException,
			IOException {
		BlobStoreConnection connnection = blobStore.openConnection(null, null);
		Blob blob = null;
		try {
			blob = connnection.getBlob(new URI("info:fedora/bhle:a0hhmgs6/OLEF"),
					null);
		} catch (UnsupportedIdException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		OutputStream out = null;
		try {
			out = blob.openOutputStream(-1, true);
		} catch (DuplicateBlobException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.write("SOMETHING ID!".getBytes());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			blob = connnection.getBlob(new URI("info:fedora/bhle:a0hhmgs6/OLEF"),
					null);
			Assert.assertTrue(blob.exists());
			blob.delete();
			Assert.assertFalse(blob.exists());
			connnection.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	@ExpectedException(UnsupportedOperationException.class)
	public void testGettingBlobWithoutProperConvertor()
			throws UnsupportedOperationException, IOException {
		BlobStoreConnection connnection = blobStore.openConnection(null, null);
		try {
			Blob blob = connnection.getBlob(new URI(
					"info:fedora/bhle:a0hhmgs6/JPG"), null);
			blob.openOutputStream(-1, true);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testListBlobIds() {
		try {
			List<URI> ids = llStorage.list(null);
			for (URI id : ids) {
				logger.info(id.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
