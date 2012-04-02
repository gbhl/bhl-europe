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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.access.convert.ConverterManager;
import com.bhle.access.convert.DatastreamConverter;
import com.bhle.access.convert.Olef2OlefConverter;
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
		Olef2OlefConverter olefConvertor = new Olef2OlefConverter();
		List<DatastreamConverter> convertors = new ArrayList<DatastreamConverter>();
		convertors.add(olefConvertor);
		ConverterManager.converters = convertors;
	}

	@Test
	public void testFileMapperToInternalFileWithSubFolder() {
		FileMapper fileMapper = new SubFolderFileMapper();

		String internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:10706-a00000000000132805961115-00001/JPG"));
		Assert.assertEquals("jpg/a00000000000132805961115_00001.*", internalFile);

		internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:10706-a00000000000132805961115-00001"));
		Assert.assertEquals("*/a00000000000132805961115_00001.*", internalFile);
	}

	@Test
	public void testFileMapperToInternalFileWithoutSubFolder() {
		FileMapper fileMapper = new SubFolderFileMapper();

		String internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:10706-a00000000000132805961115/JP2"));
		Assert.assertEquals("a00000000000132805961115_jp2.*", internalFile);

		internalFile = fileMapper.getInternalFile(URI
				.create("info:fedora/bhle:10706-a00000000000132805961115"));
		Assert.assertEquals("a00000000000132805961115_*.*", internalFile);
	}

	@Test
	public void testFileMapperToExternalFile() {
		FileMapper fileMapper = new SubFolderFileMapper();

		String externalFile = fileMapper.getExternalFile(URI
				.create("file:a0000000/00001328/05961115/a00000000000132805961115/a00000000000132805961115_dc.xml"));
		Assert.assertEquals("DC", externalFile);

		externalFile = fileMapper.getExternalFile(URI
				.create("file:a0000000/00001328/05961115/a00000000000132805961115/dc/a00000000000132805961115_00001.xml"));
		Assert.assertEquals("DC", externalFile);
	}

	@Test
	public void testIdMapperStore() throws UnsupportedOperationException,
			IOException {
		BlobStoreConnection connnection = blobStore.openConnection(null, null);
		Blob blob = null;
		try {
			blob = connnection.getBlob(new URI("info:fedora/bhle:10706-a00000000000132805961115/OLEF"),
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
			blob = connnection.getBlob(new URI("info:fedora/bhle:10706-a00000000000132805961115/OLEF"),
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
					"info:fedora/bhle:10706-a00000000000132805961115/JPG"), null);
			blob.openOutputStream(-1, true);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Ignore
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
