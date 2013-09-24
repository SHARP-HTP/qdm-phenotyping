/*
 * Copyright: (c) 2004-2012 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.qdm.webapp.rest.store;

import edu.mayo.qdm.webapp.rest.config.ConfigManager;
import edu.mayo.qdm.webapp.rest.store.ExecutionInfo.Status;
import jodd.util.PropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * The Class Md5HashFileSystemResolver.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class Md5HashFileSystemResolver implements FileSystemResolver {
	
	private static final int SUB_DIRECTORY_SPLIT = 2;
	
	private static final String DATA_DIR = "dataFiles";

	private static final String RESULT_XML_FILE = "xmlresult.xml";
	
	private static final String DATA_FILE = "data.properties";
	
	private static final String INPUT_XML_FILE = "input.xml";
	
	private static final String ID_PROP = "id";
	private static final String STATUS_PROP = "status";
	private static final String START_PROP = "start";
	private static final String FINISH_PROP = "finish";
	private static final String ERROR_PROP = "error";
	private static final String START_DATE_PARAM_PROP = "startDateParam";
	private static final String END_DATE_PARAM_PROP = "endDateParam";
	private static final String XML_FILE_NAME_PARAM_PROP = "xmlFileNameParam";
	
	@Resource
	private ConfigManager configManager;

	/**
	 * Instantiates a new md5 hash file system resolver.
	 */
	public Md5HashFileSystemResolver() {
		super();
	}
	
	/**
	 * Instantiates a new md5 hash file system resolver.
	 *
	 * @param configManager the config manager
	 */
	public Md5HashFileSystemResolver(ConfigManager configManager) {
		super();
		this.configManager = configManager;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.webapp.rest.store.FileSystemResolver#setExecutionInfo(java.lang.String, edu.mayo.webapp.rest.store.ExecutionInfo)
	 */
	public void setExecutionInfo(String id, ExecutionInfo info){
		File dataFile = this.getFiles(id).getData();
		
		try {
			PropertiesUtil.writeToFile(this.executionInfoToProperties(info), dataFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Execution info to properties.
	 *
	 * @param info the info
	 * @return the properties
	 */
	private Properties executionInfoToProperties(ExecutionInfo info){
		Properties props = new Properties();
		props.setProperty(ID_PROP, info.getId());
		
		if(info.getStatus() != null){
			props.setProperty(STATUS_PROP, info.getStatus().toString());
		}
		if(info.getStart() != null){
			props.setProperty(START_PROP, Long.toString(info.getStart().getTime()));
		}
		if(info.getFinish() != null){
			props.setProperty(FINISH_PROP, Long.toString(info.getFinish().getTime()));
		}
		if(info.getError() != null){
			props.setProperty(ERROR_PROP, info.getError());
		}
		if(info.getParameters() != null){
			props.setProperty(START_DATE_PARAM_PROP, info.getParameters().getStartDate());
			props.setProperty(END_DATE_PARAM_PROP, info.getParameters().getEndDate());
			props.setProperty(XML_FILE_NAME_PARAM_PROP, info.getParameters().getXmlFileName());
		}
		
		return props;
	}
	
	private File getStorageDir(String id, boolean create){
		String hash = this.getMd5Hash(id);
		
		String splitHash = 
				this.splitHash(hash);
		
		splitHash = StringUtils.removeStart(splitHash, File.separator);
		splitHash = StringUtils.removeEnd(splitHash, File.separator);

		String directoryPath = this.getDataDir() + 
			File.separator +
			splitHash;
		
		File dir = new File(directoryPath);
		if(!dir.exists() && !create){
			throw new ExecutionNotFoundException();
		} else {
			dir.mkdirs();
		}
		
		return dir;
	}
	
	@Override
	public FileSystemResult getNewFiles(String id) {
		return this.doGetFiles(id, true);
	}
	
	@Override
	public FileSystemResult getFiles(String id) {
		return this.doGetFiles(id, false);
	}

	protected FileSystemResult doGetFiles(String id, boolean create) {
		File storageDir = this.getStorageDir(id, create);

		File inputXml = new File(storageDir, INPUT_XML_FILE);
		File data = new File(storageDir, DATA_FILE);
		File resultXml = new File(storageDir, RESULT_XML_FILE);
		
		for(File file : Arrays.asList(inputXml,data,resultXml)){
			if(!file.exists()){
				try {
					file.getParentFile().mkdirs();
					file.createNewFile();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return new FileSystemResult(inputXml, resultXml, data);
	}
	
	/**
	 * Gets the data dir.
	 *
	 * @return the data dir
	 */
	private File getDataDir(){
		File file = new File(this.configManager.getConfigDirectory() +
				File.separator + 
				DATA_DIR);
		
		if(!file.exists()){
			file.mkdirs();
		}
		
		return file;
	}
	
	/**
	 * Split hash.
	 *
	 * @param hash the hash
	 * @return the string
	 */
	protected String splitHash(String hash){
		StringBuilder path = new StringBuilder();
		
		char[] chars = hash.toCharArray();
		
		for(int i=0;i<chars.length;i++){

			if(i >= SUB_DIRECTORY_SPLIT && 
					i % SUB_DIRECTORY_SPLIT == 0){
				path.append(File.separator);
			}
			
			path.append(chars[i]);
		}
		
		return path.toString();
	}

	/**
	 * Gets the md5 hash.
	 *
	 * @param token the token
	 * @return the md5 hash
	 */
	protected String getMd5Hash(String token) {

        MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
        m.update(token.getBytes());
       
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1,digest);
        String hashtext = bigInt.toString(16);
        
        // Now we need to zero pad it if you actually want the full 32 chars.
        while(hashtext.length() < 32 ){
           hashtext = "0"+hashtext;
        }
        
        return hashtext;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.webapp.rest.store.FileSystemResolver#getExecutionInfo(java.lang.String)
	 */
	@Override
	public ExecutionInfo getExecutionInfo(String id) {
		File dataFile = this.getFiles(id).getData();
		
		return this.fileToExecutionInfo(dataFile);
	}

	/* (non-Javadoc)
	 * @see edu.mayo.webapp.rest.store.FileSystemResolver#getExecutionInfo()
	 */
	@Override
	public Set<ExecutionInfo> getExecutionInfo() {
		Set<File> files = this.getDataFiles(this.getDataDir());
		
		Set<ExecutionInfo> executions = new HashSet<ExecutionInfo>();
		
		for(File file : files){
			executions.add(this.fileToExecutionInfo(file));
		}
		
		return executions;
	}
	
	/**
	 * File to execution info.
	 *
	 * @param dataFile the data file
	 * @return the execution info
	 */
	private ExecutionInfo fileToExecutionInfo(File dataFile){
		ExecutionInfo info = new ExecutionInfo();
		Properties props = new Properties();
		try {
			PropertiesUtil.loadFromFile(props, dataFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		info.setId((String)props.get(ID_PROP));
		String status = (String)props.get(STATUS_PROP);
		
		Status statusEnum;
		if(StringUtils.isBlank(status)){
			statusEnum = Status.UNKNOWN;
		} else {
			statusEnum = Status.valueOf(status);
		}
		info.setStatus(statusEnum);
		
		String start = (String)props.get(START_PROP);
		info.setStart(new Date(Long.parseLong(start)));
		
		String finish = (String)props.get(FINISH_PROP);
		if(StringUtils.isNotBlank(finish)){
			info.setFinish(new Date(Long.parseLong(finish)));
		}
		
		String error = (String)props.get(ERROR_PROP);
		if(StringUtils.isNotBlank(error)){
			info.setError(error);
		}
		
		String startParam = (String)props.get(START_DATE_PARAM_PROP);
		String endParam = (String)props.get(END_DATE_PARAM_PROP);
		String zipParam = (String)props.get(XML_FILE_NAME_PARAM_PROP);
		
		Parameters params = new Parameters(startParam, endParam, zipParam);
		
		info.setParameters(params);
		
		return info;
	}

	@Override
	public void remove(String executionId) {
		try {
			File storageDir = this.getStorageDir(executionId, false);
			File parentDir = storageDir.getParentFile();
			
			FileUtils.deleteDirectory(
					this.getStorageDir(executionId, false));
			
			this.deleteDirectory(parentDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void deleteDirectory(File directory){
		if(this.isDirEmpty(directory)){
			File parent = directory.getParentFile();
			directory.delete();
			
			if(!parent.equals(this.getDataDir())){
				this.deleteDirectory(parent);
			}
		}
	}
	
	private boolean isDirEmpty(File directory){
		if(directory.isDirectory()){
			if(directory.list().length > 0){
				return false;
			} else{
				return true;
			}
		} else{
			throw new RuntimeException(directory + " is not a Directory.");
		}
	}

	/**
	 * Gets the data files.
	 *
	 * @param directory the directory
	 * @return the data files
	 */
	protected Set<File> getDataFiles(File directory){
		Set<File> files = new HashSet<File>();
		
		for(File file : directory.listFiles()){
			if(file.isDirectory()){
				files.addAll(this.getDataFiles(file));
			} else {
				if(file.getName().equals(DATA_FILE)){
					files.add(file);
				}
			}
		}
		
		return files;
	}

	/**
	 * Gets the config manager.
	 *
	 * @return the config manager
	 */
	public ConfigManager getConfigManager() {
		return configManager;
	}

	/**
	 * Sets the config manager.
	 *
	 * @param configManager the new config manager
	 */
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

}
