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

/**
 * The Class FileSystemResult.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class FileSystemResult {
	
	private File image;
	private File xml;
	private File data;
	private File zip;

	/**
	 * Instantiates a new file system result.
	 *
	 * @param image the image
	 * @param xml the xml
	 * @param data the data
	 */
	public FileSystemResult(File image, File xml, File data, File zip) {
		super();
		this.image = image;
		this.xml = xml;
		this.data = data;
		this.zip = zip;
	}
	
	/**
	 * Gets the image.
	 *
	 * @return the image
	 */
	public File getImage() {
		return image;
	}
	
	/**
	 * Sets the image.
	 *
	 * @param image the new image
	 */
	public void setImage(File image) {
		this.image = image;
	}
	
	/**
	 * Gets the xml.
	 *
	 * @return the xml
	 */
	public File getXml() {
		return xml;
	}
	
	/**
	 * Sets the xml.
	 *
	 * @param xml the new xml
	 */
	public void setXml(File xml) {
		this.xml = xml;
	}
	
	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public File getData() {
		return data;
	}
	
	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(File data) {
		this.data = data;
	}

	public File getZip() {
		return zip;
	}

	public void setZip(File zip) {
		this.zip = zip;
	}

}
