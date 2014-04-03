package edu.umsl.cs.group4.services.requirements;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("requirements")
public class Requirements {

	//TODO pull these out into a properties file or figure out where to get them dynamically
	private String max4000Hours = "12";
	private String min6000Hours = "3";
	private String minTotalHours = "30";
	private String maxYears = "6";
	private String maxExternalCredits = "6";
	private String[] coreCourses = {"4760","4250","5700","5500","5130"};

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Requirements getRequirements(){
		return this;
	}
	
	@GET
	@Path("/max4000Hours")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMax4000Hours() {
		return max4000Hours;
	}
	public void setMax4000Hours(String max4000Hours) {
		this.max4000Hours = max4000Hours;
	}
	

	@GET
	@Path("/min6000Hours")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMin6000Hours() {
		return min6000Hours;
	}
	public void setMin6000Hours(String min6000Hours) {
		this.min6000Hours = min6000Hours;
	}
	

	@GET
	@Path("/minTotalHours")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMinTotalHours() {
		return minTotalHours;
	}
	public void setMinTotalHours(String minTotalHours) {
		this.minTotalHours = minTotalHours;
	}
	

	@GET
	@Path("/maxYears")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaxYears() {
		return maxYears;
	}
	public void setMaxYears(String maxYears) {
		this.maxYears = maxYears;
	}
	

	@GET
	@Path("/maxExternalCredits")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaxExternalCredits() {
		return maxExternalCredits;
	}
	public void setMaxExternalCredits(String maxExternalCredits) {
		this.maxExternalCredits = maxExternalCredits;
	}
	

	@GET
	@Path("/coreCourses")
	@Produces(MediaType.APPLICATION_JSON)
	public String[] getCoreCourses() {
		return coreCourses;
	}
	public void setCoreCourses(String[] coreCourses) {
		this.coreCourses = coreCourses;
	}
	
	
}
