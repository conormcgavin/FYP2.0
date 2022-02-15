
public class Request {
	int week;
	boolean granted;
	int day;
	boolean dayOff;
		
	public Request(int week, int day, boolean dayOff) {
		this.week = week;
		this.day = day;
		this.dayOff = dayOff;
		this.granted = false;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public boolean isGranted() {
		return granted;
	}

	public void setGranted(boolean granted) {
		this.granted = granted;
	}

	public boolean isDayOff() {
		return dayOff;
	}

	public void setDayOff(boolean dayOff) {
		this.dayOff = dayOff;
	}
}
