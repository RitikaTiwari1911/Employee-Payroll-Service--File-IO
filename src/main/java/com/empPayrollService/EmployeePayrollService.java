package com.empPayrollService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {

    public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}
    private List<EmployeePayrollData> employeePayrollDataList;
    private EmployeePayrollDataBase employeePayrollDataBase;

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollDataList){
        this();
        this.employeePayrollDataList = employeePayrollDataList;
    }
    public EmployeePayrollService(){
        employeePayrollDataBase = (EmployeePayrollDataBase) EmployeePayrollDataBase.getInstance();
    }
    public static void main(String args[]) throws PayrollDatabaseException{
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<EmployeePayrollData>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        employeePayrollService.readEmployeePayrollData(IOService.FILE_IO);
        employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
    }
    public void updateEmployeeSalary(String name, double salary) {
        int result = employeePayrollDataBase.updateEmployeeData(name,salary);
        if(result == 0)
            return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if(employeePayrollData != null)
            employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollDataList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean checkEmployeePayrollInSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDataBase.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    //method to read data
    public List readEmployeePayrollData(IOService ioService) throws PayrollDatabaseException {
        if(ioService.equals(IOService.CONSOLE_IO)) {
            Scanner consoleInputReader = new Scanner(System.in);
            System.out.println("Enter Employee Id:");
            int id=consoleInputReader.nextInt();
            System.out.println("Enter Employee name:");
            consoleInputReader.nextLine();
            String name=consoleInputReader.nextLine();
            System.out.println("Enter Employee salary:");
            double salary=consoleInputReader.nextInt();
            employeePayrollDataList.add(new EmployeePayrollData(id,name,salary));
        }
        List<String> employeeList;
        if(ioService.equals(IOService.FILE_IO)) {
            employeeList = new EmployeePayrollFileIOService().readData();
            return employeeList;
        }
        if(ioService.equals(IOService.DB_IO)) {
            this.employeePayrollDataList = EmployeePayrollDataBase.readData();
            return employeePayrollDataList;
        }
        return null;
    }

    //method to write data on console
    public void writeEmployeePayrollData(IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster To console::\n"+employeePayrollDataList);
        else if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().writeData(employeePayrollDataList);
    }
    //method to count entries in a file
    public long count(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return 0;
    }

    //method to print entries from a file
    public void printData(IOService ioService){
        if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
    }

    //method to get a range of date
    public List<EmployeePayrollData> readEmployeePayrollDataForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDataBase.getEmployeePayrollForDateRange(startDate,endDate);
        return null;
    }

}
