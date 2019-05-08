package dice.server.storage;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AWSFileSystemStorageServiceTest {

	AWSFileSystemStorageService service;

	@Before
	public void setUp() throws Exception {
		service = new AWSFileSystemStorageService("us-east-1", "diceserver-test-bucket");
	}

	@After
	public void tearDown() throws Exception {
		service.deleteAll();
	}

	@Test
	public void testPut() {
		service.put("TEST", new byte[] { 0, 3, 5, 1, 5 });
		byte[] bytes = service.get("TEST");
		if (bytes[1] != 3)
			fail("did not pull test data after putting in bucket");
	}
}
