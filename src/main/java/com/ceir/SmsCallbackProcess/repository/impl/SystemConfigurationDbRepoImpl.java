package com.ceir.SmsCallbackProcess.repository.impl;

import com.ceir.SmsCallbackProcess.model.SystemConfigurationDb;
import com.ceir.SmsCallbackProcess.repository.SystemConfigurationDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigurationDbRepoImpl {

	@Autowired
	SystemConfigurationDbRepository SystemConfigurationDbRepo;
	
	public SystemConfigurationDb getDataByTag(String tag) {
	try {
		SystemConfigurationDb systemConfigDb=new SystemConfigurationDb();
		systemConfigDb=SystemConfigurationDbRepo.getByTag(tag);
		return systemConfigDb;
	}
	catch(Exception e) {
		return null;
	}
	}

	public SystemConfigurationDb saveConfigDb(SystemConfigurationDb systemConfigurationDb) {

		try {
			return SystemConfigurationDbRepo.save(systemConfigurationDb);
		}
		catch(Exception e) {
			return null;
		}
	}
}