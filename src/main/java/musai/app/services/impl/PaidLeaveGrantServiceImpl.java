package musai.app.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
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

	@Transactional
	public void grantPaidLeave() {
		logger.info("Starting the paid leave granting process...");

		LeaveType paidLeaveType = leaveTypeRepository.findByValue("PAID_LEAVE");
		if (paidLeaveType == null) {
			logger.error("Could not find 'PAID_LEAVE' leave type.");
			return;
		}

		List<Employee> allEmployees = employeeRepository.findAll();
		for (Employee employee : allEmployees) {
			processEmployeeGrant(employee, paidLeaveType);
		}

		logger.info("Paid leave granting process completed.");
	}

	private void processEmployeeGrant(Employee employee, LeaveType paidLeaveType) {
		LocalDate joinDate = employee.getJoinDate();
		if (joinDate == null)
			return;

		long monthsWorked = ChronoUnit.MONTHS.between(joinDate, LocalDate.now());
		if (monthsWorked < 6)
			return;

		List<EmployeeLeave> existingGrants = employeeLeaveRepository.findByEmployeeAndLeaveType(employee,
				paidLeaveType);

		int grantsSoFar = existingGrants.size();
		int expectedGrants = (int) ((monthsWorked - 6) / 12) + 1;

		for (int i = grantsSoFar; i < expectedGrants; i++) {
			int daysToGrant = calculatePaidLeaveDays(joinDate, i);
			if (daysToGrant == 0)
				continue;

			LocalDate validFrom = joinDate.plusMonths(6).plusYears(i);
			LocalDate validTo = validFrom.plusYears(2).minusDays(1);

			EmployeeLeave newLeave = new EmployeeLeave();
			newLeave.setEmployee(employee);
			newLeave.setLeaveType(paidLeaveType);
			newLeave.setTotalDays((double) daysToGrant);
			newLeave.setUsedDays(0.0);
			newLeave.setRemainedDays((double) daysToGrant);
			newLeave.setValidFrom(validFrom);
			newLeave.setValidTo(validTo);

			employeeLeaveRepository.save(newLeave);

			logger.info("Employee: {}, granted {} days of paid leave (valid period: {} ～ {})", employee.getFullName(),
					daysToGrant, validFrom, validTo);
		}
	}

	private int calculatePaidLeaveDays(LocalDate joinDate, int grantIndex) {
		long monthsWorked = ChronoUnit.MONTHS.between(joinDate, LocalDate.now());
		int monthsRequired = 6 + grantIndex * 12;

		if (monthsWorked < monthsRequired) {
			return 0;
		}

		switch (grantIndex) {
		case 0:
			return 10; // 6ヶ月
		case 1:
			return 11; // 1年6ヶ月
		case 2:
			return 12; // 2年6ヶ月
		case 3:
			return 14; // 3年6ヶ月
		case 4:
			return 16; // 4年6ヶ月
		case 5:
			return 18; // 5年6ヶ月
		default:
			return 20; // 6年6ヶ月以上
		}
	}

}
