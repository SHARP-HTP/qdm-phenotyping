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

	private File inputQdmXml;
    private File ouptutResultXml;
    private File data;


    public FileSystemResult(File inputQdmXml, File ouptutResultXml, File data) {
        this.inputQdmXml = inputQdmXml;
        this.ouptutResultXml = ouptutResultXml;
        this.data = data;
    }

    public File getInputQdmXml() {
        return inputQdmXml;
    }

    public void setInputQdmXml(File inputQdmXml) {
        this.inputQdmXml = inputQdmXml;
    }

    public File getOuptutResultXml() {
        return ouptutResultXml;
    }

    public void setOuptutResultXml(File ouptutResultXml) {
        this.ouptutResultXml = ouptutResultXml;
    }

    public File getData() {
        return data;
    }

    public void setData(File data) {
        this.data = data;
    }
}
