

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.WithApplication;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.SchemaHelper;

public class testGetSchema extends WithApplication{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateSchema() {
		JsonNode node = SchemaHelper.getSchemaById("logMessage", "1");
		
		
	}

}
