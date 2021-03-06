package com.empPayrollService;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDataBase {
    private static ResultSet resultSet;
    private static Object employeePayrollDataBase;
    private PreparedStatement employeePayrollDataStatement;

    public static Object getInstance(){
        if(employeePayrollDataBase == null)
            employeePayrollDataBase = new EmployeePayrollDataBase();
        return employeePayrollDataBase;
    }

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
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeeDataUsingPreparedStatement(name,salary);
    }

    private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
        String sql = String.format("UPDATE payroll_table SET salary = %.2f WHERE name = '%s';",salary,name);
         try(Connection connection = this.getConnection()){
             PreparedStatement statement = connection.prepareStatement(sql);
             return statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name) {
        List<EmployeePayrollData> employeePayrollList = null;
        if(this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try{
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e){
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    //for date
    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("select * from payroll_table WHERE start BETWEEN '%s' AND '%s';",Date.valueOf(startDate),Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try {
            Connection connection = this.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try{
            while (resultSet.next()){
                int id=resultSet.getInt("id");
                String name=resultSet.getString("name");
                double salary=resultSet.getDouble("salary");
                LocalDate startDate=resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id,name,salary,startDate));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        List<EmployeePayrollData> employeePayrollDataList = null;
        return null;
    }
    private void prepareStatementForEmployeeData() {
        try {
            Connection connection = this.getConnection();
            String sql = "SELECT * FROM payroll_table WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
