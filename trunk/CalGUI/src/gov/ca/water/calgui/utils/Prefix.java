package gov.ca.water.calgui.utils;

public class Prefix {
	public String getType(String name) {
		String type;
		if (name.startsWith("S_") || name.startsWith("s_")) {
			type = "STORAGE";
		} else if (name.startsWith("C_") || name.startsWith("c_")) {
			type = "FLOW-CHANNEL";
		} else if (name.startsWith("D_") || name.startsWith("d_")) {
			type = "FLOW-DELIVERY";
		} else if (name.startsWith("R_") || name.startsWith("r_")) {
			type = "RETURN-FLOW";
		} else if (name.startsWith("I_") || name.startsWith("i_")) {
			type = "INFLOW";
		} else if (name.startsWith("AD_") || name.startsWith("ad_")) {
			type = "FLOW-ACCRDEPL";
		} else if (name.startsWith("S") || name.startsWith("s")) {
			type = "STORAGE";
		} else if (name.startsWith("D") || name.startsWith("d")) {
			type = "FLOW-DELIVERY";
		} else if (name.startsWith("C") || name.startsWith("c")) {
			type = "FLOW-CHANNEL";
		} else {
			type = "";
		}
		return type;
	}
}
