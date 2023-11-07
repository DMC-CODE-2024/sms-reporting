package com.ceir.SmsCallbackProcess.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_param")
public class SystemConfigurationDb implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "created_on")
	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date createdOn;

	@Column(name = "modified_on")
	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date modifiedOn;

	private String tag;

	private String value;

	private String description;

	@Column(columnDefinition = "INT DEFAULT 0")
	private Integer type;

	private String remark;

	@Column(name = "active", columnDefinition = "INT DEFAULT 0")
	private Integer active;

	@Column(name = "feature_name")
	private String featureName;

	@Column(name = "user_type")
	private String userType;

	@Column(name = "modified_by")
	private String modifiedBy;

	public SystemConfigurationDb() {
		super();
	}
	public SystemConfigurationDb(String tag, String value) {
		this.tag = tag;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SystemConfigurationDb{");
		sb.append("id=").append(id);
		sb.append(", createdOn=").append(createdOn);
		sb.append(", modifiedOn=").append(modifiedOn);
		sb.append(", tag='").append(tag).append('\'');
		sb.append(", value='").append(value).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append(", type=").append(type);
		sb.append(", remark='").append(remark).append('\'');
		sb.append(", active=").append(active);
		sb.append(", featureName='").append(featureName).append('\'');
		sb.append(", userType='").append(userType).append('\'');
		sb.append(", modifiedBy='").append(modifiedBy).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
