public class Preference {
	int week;
	boolean granted;
	int day;
	boolean dayOff;
	int order;
		
	public Preference(int week, int day, int order, boolean dayOff) {
		this.week = week;
		this.day = day;
		this.dayOff = dayOff;
		this.granted = false;
		this.order = order;
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

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isDayOff() {
		return dayOff;
	}

	public void setDayOff(boolean dayOff) {
		this.dayOff = dayOff;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
