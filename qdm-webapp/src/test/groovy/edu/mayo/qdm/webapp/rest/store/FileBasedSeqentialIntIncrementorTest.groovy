package edu.mayo.qdm.webapp.rest.store;

import static org.junit.Assert.*

import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Before;
import org.junit.Test

import edu.mayo.qdm.webapp.rest.config.ConfigManager



class FileBasedSeqentialIntIncrementorTest {
	
	def incrementor
	
	def filePath;
	
	@Before
	void init() {
		filePath = FileUtils.getTempDirectory().getPath() + File.separator + "testconfig"
		def config = [
			getConfigDirectory: {
				filePath
			}
		] as ConfigManager
	
		incrementor = new FileBasedSeqentialIntIncrementor(config)
	}
	
	@After
	void cleanup() {
		FileUtils.forceDelete(new File(filePath))
	}
	
	@Test
	void testGetNextFirstCall() {
		assertEquals "1", incrementor.getId()	
	
	}
	
	@Test
	void testGetNextTwoCalls() {
		incrementor.getId()
		assertEquals "2", incrementor.getId()
	
	}

}
