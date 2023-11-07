package com.ceir.SmsCallbackProcess.repository;

import com.ceir.SmsCallbackProcess.model.SystemConfigurationDb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigurationDbRepository extends JpaRepository<SystemConfigurationDb, Long> {

	public SystemConfigurationDb getByTag(String tag);
	public SystemConfigurationDb getById(Long id);
	public SystemConfigurationDb save(SystemConfigurationDb systemConfigurationDb);

}
