package domain.model;

public class GovernmentPermit {

	private Long id;
	private String electricalPermit;
	private String structuralPermit;
	
	public GovernmentPermit() {
		super();
	}
	
	public GovernmentPermit(Long id, String electricalPermit, String structuralPermit) {
		super();
		this.id = id;
		this.electricalPermit = electricalPermit;
		this.structuralPermit = structuralPermit;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getElectricalPermit() {
		return electricalPermit;
	}
	public void setElectricalPermit(String electricalPermit) {
		this.electricalPermit = electricalPermit;
	}
	public String getStructuralPermit() {
		return structuralPermit;
	}
	public void setStructuralPermit(String structuralPermit) {
		this.structuralPermit = structuralPermit;
	}
	
}
