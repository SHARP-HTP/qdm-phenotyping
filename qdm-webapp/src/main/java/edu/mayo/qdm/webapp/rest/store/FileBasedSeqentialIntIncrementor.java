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

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import edu.mayo.qdm.webapp.rest.config.ConfigManager;

/**
 * The Class FileBasedSeqentialIntIncrementor.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class FileBasedSeqentialIntIncrementor implements InitializingBean, IdGenerator {

	private static final String INCREMENTOR_FILE_NAME = "incrementor.file";

	@Resource
	private ConfigManager configManager;

	private File incrementorFile;
	
	/**
	 * Instantiates a new file based seqential int incrementor.
	 */
	public FileBasedSeqentialIntIncrementor(){
		super();
	}
	
	/**
	 * Instantiates a new file based seqential int incrementor.
	 *
	 * @param configManager the config manager
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public FileBasedSeqentialIntIncrementor(ConfigManager configManager) throws IOException {
		super();
		this.configManager = configManager;
		this.afterPropertiesSet();
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws IOException {
		this.incrementorFile = 
				new File(this.configManager.getConfigDirectory() + File.separator + INCREMENTOR_FILE_NAME);
		
		File parent = this.incrementorFile.getParentFile();
		if(!parent.exists()){
			if(! parent.mkdirs()){
				throw new RuntimeException("Couldn't create file!!");
			}
		}
		this.incrementorFile.createNewFile();
	}

	/**
	 * Increment.
	 *
	 * @param file the file
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected int increment(File file) throws IOException {
		
		synchronized(file){
			String content = FileUtils.readFileToString(file);
	
			int nextNumber;
			if (StringUtils.isBlank(content)) {
				nextNumber = 1;
			} else {
				nextNumber = Integer.parseInt(content) + 1;
			}
	
			FileUtils.write(file, String.valueOf(nextNumber));
	
			return nextNumber;
		}
	}

	/* (non-Javadoc)
	 * @see edu.mayo.webapp.rest.store.IdGenerator#getId()
	 */
	public String getId() {
		try {
			return Integer.toString(this.increment(this.incrementorFile));
		} catch (IOException e) {
			throw new RuntimeException("Error writing to incrementor file.", e);
		}
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
