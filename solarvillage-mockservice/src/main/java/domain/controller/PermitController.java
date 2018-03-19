package domain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import domain.PermitJdbcRepository;
import domain.model.GovernmentPermit;

//import domain.model.ElectricPermit;

@RestController
public class PermitController {

	@Autowired
	PermitJdbcRepository repository;
	
	@RequestMapping(value="/submitPermitRequest", method=RequestMethod.GET, produces="application/json")
	public String submitPermitRequest(@RequestParam(required = true) String id) {
		GovernmentPermit newPermit = new GovernmentPermit();
		newPermit.setId(Long.parseLong(id));
		newPermit.setElectricalPermit("NOT_STARTED");
		newPermit.setStructuralPermit("NOT_STARTED");
		repository.insert(newPermit);
		String response = new String("New permit request with id \""+id+"\" submitted successfully.");
		return response;
	}
	
	@RequestMapping(value="/deletePermitRequest", method=RequestMethod.GET, produces="application/json")
	public String deletePermitRequest(@RequestParam(required = true) String id) {
		repository.deleteById(Long.parseLong(id));
		String response = new String("Permit request with id \""+id+"\" Deleted.");
		return response;
	}
	
	@RequestMapping(value="/rescindPermitRequest", method=RequestMethod.GET, produces="application/json")
	public String rescindPermitRequest(@RequestParam(required = true) String id) {
		repository.rescindById(Long.parseLong(id));
		String response = new String("Permit request with id \""+id+"\" Rescinded.");
		return response;
	}
	
	@RequestMapping(value="/setAllPermit", method=RequestMethod.GET, produces="application/json")
	public String setAllPermit(@RequestParam(required = true) String id, @RequestParam(required=true) String electricalStatus, @RequestParam(required=true) String structuralStatus) {
		GovernmentPermit newPermit = new GovernmentPermit();
		newPermit.setId(Long.parseLong(id));
		newPermit.setElectricalPermit(electricalStatus);
		newPermit.setStructuralPermit(structuralStatus);
		repository.update(newPermit);
		String response = new String("Electrical and Structural permit status for request with id \""+id+"\" successfully set.");
		return response;
	}
	
	@RequestMapping(value="/setElectricalPermit", method=RequestMethod.GET, produces="application/json")
	public String setElectricalPermit(@RequestParam(required = true) String id, @RequestParam(required=true) String electricalStatus) {
		GovernmentPermit newPermit = new GovernmentPermit();
		newPermit.setId(Long.parseLong(id));
		newPermit.setElectricalPermit(electricalStatus);
		repository.updateElectrical(newPermit);
		String response = new String("Electrical permit status for request with id \""+id+"\" successfully set.");
		return response;
	}
	
	@RequestMapping(value="/setStructuralPermit", method=RequestMethod.GET, produces="application/json")
	public String setStructuralPermit(@RequestParam(required = true) String id, @RequestParam(required=true) String structuralStatus) {
		GovernmentPermit newPermit = new GovernmentPermit();
		newPermit.setId(Long.parseLong(id));
		newPermit.setStructuralPermit(structuralStatus);
		repository.updateStructural(newPermit);
		String response = new String("Structural permit status for request with id \""+id+"\" successfully set.");
		return response;
	}
	
	@RequestMapping(value="/getStatusElectrical", method=RequestMethod.GET, produces="application/json")
	public String getStatusElectrical(@RequestParam(required = true) String id) {
		return repository.getElectricStatusById(Long.parseLong(id));
	}
	
	@RequestMapping(value="/getStatusStructural", method=RequestMethod.GET, produces="application/json")
	public String getStatusStructural(@RequestParam(required = true) String id) {
		return repository.getStructuralStatusById(Long.parseLong(id));
	}
	
}
