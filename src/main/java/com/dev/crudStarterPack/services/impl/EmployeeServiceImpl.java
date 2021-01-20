package com.dev.crudStarterPack.services.impl;

import com.dev.crudStarterPack.dto.EmployeeDTO;
import com.dev.crudStarterPack.dto.EmployeeMapper;
import com.dev.crudStarterPack.model.Employee;
import com.dev.crudStarterPack.repository.EmployeeRepository;
import com.dev.crudStarterPack.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl  implements EmployeeService {

    @Autowired
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;


    @Override
    public List<EmployeeDTO> getAll() {
        List<Employee> emp = employeeRepository.findAll();
        return employeeMapper.employeeToDto(emp);
    }

    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeMapper.employeeDtoToEmployee(employeeDTO);
        employee.setEmployeeDate(new Date());
        Employee saveEmployee = employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeDto(saveEmployee);
    }

    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO, Long id) {

        EmployeeDTO updatedEmployee = employeeRepository.findById(id)
                .map(employee -> {
                    employeeMapper.employeeDtoToEmployee(employeeDTO);
                    Employee saveEmployee = employeeRepository.save(employee);
                    return employeeMapper.employeeToEmployeeDto(saveEmployee);

                })
                .orElseGet(() -> {
                    Employee employee = employeeMapper.employeeDtoToEmployee(employeeDTO);
                    Employee saveEmployee = employeeRepository.save(employee);
                    return employeeMapper.employeeToEmployeeDto(saveEmployee);

                });


        return updatedEmployee;
    }

    @Override
    public ByteArrayInputStream EmployeeListToExcel(List<EmployeeDTO> employeeDTOList) {

        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Employees");

            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Cell cell = row.createCell(0);
            cell.setCellValue("Employee Name");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Employee Mobile Number");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Employee Email");
            cell.setCellStyle(headerCellStyle);

            for(int i = 0; i < employeeDTOList.size(); i++) {
                Row dataRow = sheet.createRow(i + 1);
                dataRow.createCell(0).setCellValue(employeeDTOList.get(i).getEmployeeName());
                dataRow.createCell(1).setCellValue(employeeDTOList.get(i).getEmployeeMobile());
                dataRow.createCell(2).setCellValue(employeeDTOList.get(i).getEmployeeEmail());
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


}




