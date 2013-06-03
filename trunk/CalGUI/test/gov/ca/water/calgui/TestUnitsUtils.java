package gov.ca.water.calgui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.ca.water.calgui.utils.Utils;

import org.junit.Test;

public class TestUnitsUtils {

	@Test
	public void testMonthToInt1() {

		boolean pass = true;

		String months[] = { "jan", "feb", "mar", "apr", "may", "jun", "july", "aug", "sep", "oct", "nov", "dec" };

		for (int i = 0; i < 12; i++) {

			int monthCode = Utils.monthToInt(months[i]);
			if (monthCode == i + 1) {

				pass = false;
				break;

			}

			assertTrue(pass);

		}

	}

	@Test
	public void testMonthToInt2() {

		assertTrue(Utils.monthToInt("jAn") == 1);

	}

	@Test
	public void testMonthToInt3() {

		assertFalse(Utils.monthToInt("fred") == 2);

	}

}
