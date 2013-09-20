package edu.mayo.qdm.webapp.rest.store;

import static org.junit.Assert.*


import org.apache.commons.io.FileUtils
import org.junit.Test

import edu.mayo.qdm.webapp.rest.config.ConfigManager
import edu.mayo.qdm.webapp.rest.store.ExecutionInfo.Status



class Md5HashFileSystemResolverTest {

	@Test
	void TestCreateMd5Hash() {
		def resolver = new Md5HashFileSystemResolver();
		
		assertEquals "c4ca4238a0b923820dcc509a6f75849b", resolver.getMd5Hash("1")
	
	}

	@Test
	void TestCreateMd5HashLarger() {
		def resolver = new Md5HashFileSystemResolver();

		assertEquals "342b5fe6486788799659c39bbfc3fa02", resolver.getMd5Hash("10015")
	
	}
	
	@Test
	void TestCreateMd5HashReallyBig() {
		def resolver = new Md5HashFileSystemResolver();
		
		assertEquals "35b191e5254ff43e38500000251d802c", resolver.getMd5Hash("10000202020")
	}
	
	@Test
	void TestSplitHash() {
		def resolver = new Md5HashFileSystemResolver();
		
		assertEquals "35/b1/91/e5/25/4f/f4/3e/38/50/00/00/25/1d/80/2c", resolver.splitHash("35b191e5254ff43e38500000251d802c")
	}
	
	@Test
	void TestGetNewFiles() {
		def filePath = FileUtils.getTempDirectory().getPath() + File.separator + "testconfig"
		def config = [
			getConfigDirectory: {
				filePath
			}
		] as ConfigManager
	
		def resolver = new Md5HashFileSystemResolver(config)
		
		def result = resolver.getNewFiles("1")
	
		def path = filePath + File.separator + "dataFiles" + File.separator + ["c4","ca","42","38","a0","b9","23","82","0d/cc","50","9a","6f","75","84","9b"].join(File.separator)
		
		assertEquals path + File.separator + "image.img", result.image.getPath()
		assertEquals path + File.separator + "xmlresult.xml", result.xml.getPath()
		
		new File(filePath).delete()
	}

	@Test
	void TestGetDataFilesNone() {
		def resolver = new Md5HashFileSystemResolver()
		
		def tempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString())
		
		tempFile.mkdirs();
		
		try{
		assertEquals 0, resolver.getDataFiles(tempFile).size()
		} finally {
			FileUtils.deleteDirectory(tempFile)	
		}
	}
	
	@Test
	void TestGetDataFilesOne() {
		def resolver = new Md5HashFileSystemResolver()
		
		def tempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString())
		
		tempFile.mkdirs();
		new File(tempFile, "data.properties").createNewFile()
		
		try{
		assertEquals 1, resolver.getDataFiles(tempFile).size()
		} finally {
			FileUtils.deleteDirectory(tempFile)
		}
	}
	
	@Test
	void TestGetDataFilesNested() {
		def resolver = new Md5HashFileSystemResolver()
		
		def tempFile = new File(FileUtils.getTempDirectory(), UUID.randomUUID().toString())
		
		def nestedFile = new File(new File(new File(tempFile, "nested"),"nested2"),"nested3")
		nestedFile.mkdirs()
		
		new File(nestedFile, "data.properties").createNewFile()
		
		try{
		assertEquals 1, resolver.getDataFiles(tempFile).size()
		} finally {
			FileUtils.deleteDirectory(tempFile)
		}
	}
	
	@Test
	void TestExecutionInfoToProperties() {
		def resolver = new Md5HashFileSystemResolver()
		
		def start = new Date(123)
		def finish = new Date(321)
		
		def props = resolver.executionInfoToProperties(new ExecutionInfo(
			id:"1",
			status:Status.COMPLETE,
			start:start,
			finish:finish,
			parameters:new Parameters(
				"11-JAN-1900",
				"5-APR-2063",
				"test.zip"
			)
		))
		
		assertEquals '1', props.getProperty("id")
		assertEquals 'COMPLETE', props.getProperty("status")
		assertEquals start.time.toString(), props.getProperty("start")
		assertEquals finish.time.toString(), props.getProperty("finish")
		assertEquals '11-JAN-1900', props.getProperty("startDateParam")
		assertEquals '5-APR-2063', props.getProperty("endDateParam")
		assertEquals 'test.zip', props.getProperty("xmlFileNameParam")
	}
	
	@Test
	void TestFileToExecutionInfo() {
		def resolver = new Md5HashFileSystemResolver()
	
		def file
		try {
		  file = File.createTempFile(UUID.randomUUID().toString(), ".props")
		  
		  def start = new Date(123)
		  def finish = new Date(321)
		  
		  def info = new ExecutionInfo(
			  id:"1",
			  status:Status.COMPLETE,
			  start:start,
			  finish:finish,
			  parameters:new Parameters(
				  "11-JAN-1900",
				  "5-APR-2063",
				  "test.zip"
			  )
		  )
		  
		  def props = resolver.executionInfoToProperties(info)
		  
		  props.store(new FileOutputStream(file),null)
		  
		  def infoFound = resolver.fileToExecutionInfo(file);
		  
		  assertEquals info.id, infoFound.id
		  assertEquals info.status, infoFound.status
		  assertEquals info.start, infoFound.start
		  assertEquals info.finish, infoFound.finish
		  assertEquals info.parameters.startDate, infoFound.parameters.startDate
		  assertEquals info.parameters.endDate, infoFound.parameters.endDate
		  assertEquals info.parameters.xmlFileName, infoFound.parameters.xmlFileName
		
		} finally {
			FileUtils.deleteQuietly(file)
		}
	
	}
}
