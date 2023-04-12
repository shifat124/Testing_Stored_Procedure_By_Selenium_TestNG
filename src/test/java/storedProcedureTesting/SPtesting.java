package storedProcedureTesting;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SPtesting {

	Connection con = null;
	Statement stmt = null;
	ResultSet rs;
	CallableStatement cstmnt;
	ResultSet rs1;
	ResultSet rs2;

	@BeforeClass
	void setup() throws SQLException {
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/classicmodels", "root", "root");
	}

	@AfterClass
	void tearDown() throws SQLException {
		con.close();
	}
    //Test case for checking the existence of procedure in database
	@Test(priority = 1)
	void testStoredProceduresExist() throws SQLException {
		stmt = con.createStatement();
		rs = stmt.executeQuery("show procedure status where Name = \"SelectAllCustomer\"");
		rs.next();
		Assert.assertEquals(rs.getString("Name"), "SelectAllCustomer");
	}
    
	@Test(priority = 2)
	void testSelectAllCustomer() throws SQLException {
		cstmnt = con.prepareCall("{call SelectAllCustomer()}");
		rs1 = cstmnt.executeQuery();

		stmt = con.createStatement();
		rs2 = stmt.executeQuery("select * from customers");

		Assert.assertEquals(compareResultSet(rs1, rs2), true);
	}

	@Test(priority = 3)
	void testSelectAllCustomerByCity() throws SQLException {
		cstmnt = con.prepareCall("{call SelectAllCustomerByCity(?)}");
		cstmnt.setString(1, "Singapore");
		rs1 = cstmnt.executeQuery();

		stmt = con.createStatement();
		rs2 = stmt.executeQuery("select * from customers where city = 'Singapore'");

		Assert.assertEquals(compareResultSet(rs1, rs2), true);
	}

	@Test(priority = 4)
	void testSelectAllCustomerByCityAndPin() throws SQLException {
		cstmnt = con.prepareCall("{call SelectAllCustomerByCityAndPin(?,?)}");
		cstmnt.setString(1, "Singapore");
		cstmnt.setString(2, "079903");
		rs1 = cstmnt.executeQuery();

		stmt = con.createStatement();
		rs2 = stmt.executeQuery("select * from customers where city = 'Singapore' and postalCode = '079903'");

		Assert.assertEquals(compareResultSet(rs1, rs2), true);
	}

	public boolean compareResultSet(ResultSet r1, ResultSet r2) throws SQLException {
		while (r1.next()) {
			r2.next();
			int c = r1.getMetaData().getColumnCount();
			for (int i = 1; i <= c; i++) {
				if (!StringUtils.equals(r1.getString(i), r2.getString(i))) {
					return false;
				}

			}
		}
		return true;
	}

}
