package model;

import java.io.Serializable;

public class GovernmentPermit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean isGovtPermitApproved;

	public Boolean getIsGovtPermitApproved() {
		return isGovtPermitApproved;
	}

	public void setIsGovtPermitApproved(Boolean isGovtPermitApproved) {
		this.isGovtPermitApproved = isGovtPermitApproved;
	}
	
}
