package musai.app.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import musai.app.models.Employee;
import musai.app.models.EmployeeLeave;
import musai.app.models.LeaveType;
import musai.app.repositories.EmployeeLeaveRepository;
import musai.app.repositories.EmployeeRepository;
import musai.app.repositories.LeaveTypeRepository;
import musai.app.services.PaidLeaveGrantService;

@Service
public class PaidLeaveGrantServiceImpl implements PaidLeaveGrantService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeLeaveRepository employeeLeaveRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    private static final Logger logger = LoggerFactory.getLogger(PaidLeaveGrantService.class);

    public void grantPaidLeave() {
        logger.info("有給休暇の付与処理を開始します...");

        LeaveType paidLeaveType = leaveTypeRepository.findByValue("PAID_LEAVE");
        if (paidLeaveType == null) {
            logger.error("有給休暇の種類 'PAID_LEAVE' が見つかりません。");
            return;
        }

        List<Employee> allEmployees = employeeRepository.findAll();

        for (Employee employee : allEmployees) {
            LocalDate joinDate = employee.getJoinDate();
            if (joinDate == null) continue;

            long monthsWorked = ChronoUnit.MONTHS.between(joinDate, LocalDate.now());

            if (monthsWorked < 6) continue; // 入社6ヶ月未満

            List<EmployeeLeave> existingGrants = employeeLeaveRepository.findByEmployeeAndLeaveType(employee, paidLeaveType);

            int grantsSoFar = existingGrants.size();
            int expectedGrants = (int) ((monthsWorked - 6) / 12) + 1;

            if (grantsSoFar >= expectedGrants) continue; // すでに付与済み

            int days = 10 + (expectedGrants - 1);

            LocalDate validFrom = joinDate.plusMonths(6).plusYears(expectedGrants - 1);
            LocalDate validTo = validFrom.plusYears(2);

            EmployeeLeave newLeave = new EmployeeLeave();
            newLeave.setEmployee(employee);
            newLeave.setLeaveType(paidLeaveType);
            newLeave.setTotalDays((double) days);
            newLeave.setUsedDays(0.0);
            newLeave.setRemainedDays((double) days);
            newLeave.setValidFrom(validFrom);
            newLeave.setValidTo(validTo);

            employeeLeaveRepository.save(newLeave);

            logger.info("社員: {}, {}日分の有給休暇を付与しました（有効期間: {} ～ {}）", 
                        employee.getFullName(), days, validFrom, validTo);
        }

        logger.info("有給休暇の付与処理が完了しました。");
    }
}
