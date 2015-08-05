/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.data.aerospike.convert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.data.aerospike.mapping.AerospikeMetadataBin;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.Value;

/**
 * Value object to carry data to be read and to written in object conversion.
 * 
 * @author Oliver Gierke
 */
public class AerospikeData {

	/**
	 * 
	 */
	public static final String SPRING_ID_BIN = "SpringID";
	private Key key;
	private Record record;
	private final String namespace;
	private final List<Bin> bins;
	private AerospikeMetadataBin metaData;
	private String[] binNames;

	private AerospikeData(Key key, Record record, String namespace, List<Bin> bins, String[] binNames) {

		this.key = key;
		this.record = record;
		this.namespace = namespace;
		this.bins = bins;
		this.metaData =  new AerospikeMetadataBin();
		
	}


	public static AerospikeData forRead(Key key, String[] binNames) {
		return new AerospikeData(key, null, key.namespace, Collections.<Bin>emptyList(), binNames);
	}


	public static AerospikeData forWrite(String namespace) {
		return new AerospikeData(null, null, namespace, new ArrayList<Bin>(), null);
	}

	/**
	 * @return the key
	 */
	public Key getKey() {
		return key;
	}

	
	public void setID(byte[] ID){
		this.key = new Key(this.getNamespace(), this.getSetName(), ID);
	}
	public void setID(String ID){
		this.key = new Key(this.getNamespace(), this.getSetName(), ID);
	}
	public void setID(long ID){
		this.key = new Key(this.getNamespace(), this.getSetName(), ID);
	}
	public void setID(int ID){
		this.key = new Key(this.getNamespace(), this.getSetName(), ID);
	}
	public void setID(Value ID){		
		this.key = new Key(this.getNamespace(), this.getSetName(), ID);
	}
	public void setID(Serializable id) {
		//getMetaData().addKeyValuetoAerospikeMetaData(AerospikeData.SPRING_ID_BIN, id);
		setID(id.toString());
	}

	/**
	 * @return the record
	 */
	public Record getRecord() {
		return record;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}
	/**
	 * @return the set name
	 */
	public String getSetName(){
		return (key != null) ? key.setName : null;
	}

	/**
	 * @return the bins
	 */
	public List<Bin> getBins() {
		return bins;
	}

	public Bin[] getBinsAsArray() {
		return getBins().toArray(new Bin[bins.size()]);
	}

	public void add(List<Bin> bins) {
		this.bins.addAll(bins);
	}
	public void add(Bin bin) {
		this.bins.add(bin);
	}
	
	public void addMetaDataToBin() {
		add(getMetaData().getAerospikeMetaDataBin());
	}
	
	public void setMetaData(Bin bin){
		
		//this.metaData = Bin.asMap(name, value)
	}
	
	
	
	public void addMetaDataItem(String key, Object value){
		getMetaData().addKeyValuetoAerospikeMetaData(key, value);
	}

	public String[] getBinNames() {
		if (this.bins == null || this.bins.size()  == 0)
			return null;
		
		String[] binAsStringArray =  new String[bins.size()];
		int i = 0;
		for (Iterator<Bin> iterator = bins.iterator(); iterator.hasNext();) {
			Bin bin = (Bin) iterator.next();
			binAsStringArray[i] = bin.name;
			i++;
		}
		return binAsStringArray ;
	}

	@SuppressWarnings("unchecked")
	public void setRecord(Record record) {
		getMetaData().addMap((HashMap<String, Object>) record.getValue(AerospikeMetadataBin.AEROSPIKE_META_DATA));
		this.record = record;
	}


	public void setSetName(String setName) {
		this.key = new Key(this.getNamespace(), setName, this.key.userKey);
		
	}


	/**
	 * @return
	 */
	public Object getSpringId() {
		HashMap<String, Object> aerospikeMetaData = (HashMap<String, Object>) record.getValue(AerospikeMetadataBin.AEROSPIKE_META_DATA);
		return aerospikeMetaData==null?null:aerospikeMetaData.get(SPRING_ID_BIN);
	}


	public AerospikeMetadataBin getMetaData() {
		return metaData;
	}


	public void setMetaData(AerospikeMetadataBin metaData) {
		this.metaData = metaData;
	}

}
