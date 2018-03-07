package domain.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import domain.model.ElectricPermit;

@RestController
public class ElectricPermitController {

	@RequestMapping(value="/getElectricalStatus", method=RequestMethod.GET, produces="application/json")
	public String getElectricPermitStatus(@RequestParam(required = true) String refId) {
		//ElectricPermit permit = new ElectricPermit();
		System.out.println("Called electrical service at :"+(new Date()));
		if(refId.equalsIgnoreCase("toApprove")) {
			return "APPROVED";
		}else if(refId.equalsIgnoreCase("toDeny")) {
			return "DENIED";
		}
		return "IN_PROGRESS";
	}
	
	@RequestMapping(value="/getStructuralStatus", method=RequestMethod.GET, produces="application/json")
	public String getStructuralPermitStatus(@RequestParam(required = true) String refId) {
		System.out.println("Called structural service at :"+(new Date()));
		if(refId.equalsIgnoreCase("toApprove")) {
			return "APPROVED";
		}else if(refId.equalsIgnoreCase("toDeny")) {
			return "DENIED";
		}
		return "IN_PROGRESS";
	}
}
