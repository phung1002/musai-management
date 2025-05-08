package musai.app.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import musai.app.services.PaidLeaveGrantService;

@Component
public class LeaveGrantScheduler {
	@Autowired
	private PaidLeaveGrantService grantService;

	// 毎日午前1時に実行されます
	@Scheduled(cron = "0 0 1 * * ?")
	public void scheduleLeaveGranting() {
		grantService.grantPaidLeave();
	}
}
