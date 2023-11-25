package com.ceir.SmsCallbackProcess.repository;

import com.ceir.SmsCallbackProcess.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

	public Notification save(Notification notification);

	Notification findByCorelationId(String correlationId);
}
