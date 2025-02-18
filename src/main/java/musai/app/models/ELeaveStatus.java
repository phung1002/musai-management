package musai.app.models;

public enum ELeaveStatus {
	PENDING, APPROVED, REJECTED, REQUESTED_CHANGE, REVOKED, CANCELED;

	public static boolean isValidStatus(String status) {
		try {
			ELeaveStatus.valueOf(status.toUpperCase());
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
