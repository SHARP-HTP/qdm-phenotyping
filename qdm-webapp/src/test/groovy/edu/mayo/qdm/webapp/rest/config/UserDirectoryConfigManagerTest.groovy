package edu.mayo.qdm.webapp.rest.config;

import static org.junit.Assert.*

import org.junit.Test

class UserDirectoryConfigManagerTest {
	
	@Test
	void TestGetConfigDirectory() {
		def manager = new UserDirectoryConfigManager();
		
		assertEquals System.getProperty("user.home") + File.separator + '.qdm-phenotyping',
			manager.getConfigDirectory();
	}

}
