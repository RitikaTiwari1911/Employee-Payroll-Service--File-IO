package com.empPayrollService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDataBase {
    public Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String passWord = "root";

        Connection connection;
        System.out.println("Connecting to database"+jdbcURL);
        connection = DriverManager.getConnection(jdbcURL,userName,passWord);
        System.out.println("Connection is successful!!"+connection);
        return connection;
    }

    // reading data from database and returning list
    public List<EmployeePayrollData> readData() throws PayrollDatabaseException {
        String sql = "SELECT * FROM employee_payroll";

        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()){
                int id=result.getInt("id");
                String name=result.getString("name");
                double salary=result.getDouble("salary");
                LocalDate startDate=result.getDate("start").toLocalDate();
                employeePayrollDataList.add(new EmployeePayrollData(id,name,salary,startDate));
            }
            connection.close();
        }catch (SQLException e){
            throw new PayrollDatabaseException("Connection Error");
        }
        return employeePayrollDataList;
    }
}
