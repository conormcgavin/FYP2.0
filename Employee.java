import java.util.ArrayList;

public class Employee {
	int bank;
	int requests_made_this_week;
	int requests_made;
	int requests_received;
	int total_given_score;
	
	int[] reqs_score_history;
	int[] pref_score_history;
	
	int[] preferences_received;
	int[] bank_history;
	Preference[] preferences;
	ArrayList<Preference> preference_history;
	Request[] requests;
	ArrayList<Request> request_history;
	
	String name;
	int min_days_per_week;
	int max_days_per_week;
	int skill_level;
	
	public Employee(String name, int min_days, int max_days, int skill_level) {
		this.bank = Constants.START_BANK;
		this.preferences = new Preference[Constants.PREFERENCES_PER_PERSON];
		this.requests = new Request[Constants.MAX_REQUESTS_PER_PERSON];
		this.requests_made_this_week = 0;
		
		this.requests_made = 0;
		this.requests_received = 0;
		this.total_given_score = 0;
		this.preferences_received = new int[Constants.PREFERENCES_PER_PERSON];
		
		this.preference_history = new ArrayList<Preference>();
		this.request_history = new ArrayList<Request>();
		this.pref_score_history = new int[1000000];
		this.reqs_score_history = new int[1000000];
		this.bank_history = new int[1000000];
		
		this.name = name;
		this.min_days_per_week = min_days;
		this.max_days_per_week = max_days;
		this.skill_level = skill_level;
	}
	
	public void addPreference(int week, int day, int order, boolean dayOff) {
		Preference p = new Preference(week, day, order, dayOff);
		preferences[order - 1] = p;
	}
	
	public void addRequest(int week, int day, boolean dayOff) {
		Request r = new Request(week, day, dayOff); 
		requests[requests_made_this_week++] = r;
		requests_made += 1;
	}
	
	public void addBank(int amount) {
		this.bank += amount;
	}
	
	public void removeBank(int amount) {
		this.bank -= amount;
	}
	
	public int getBank() {
		return this.bank;
	}

	public Preference[] getPreferences() {
		return this.preferences;
		
	}
	
	public Request[] getRequests() {
		return this.requests;
		
	}
	
	public void printStats() {
		System.out.println("-----------------------------------");
		System.out.println("\nPrinting stats for " + name);
		System.out.println("\nPrinting Preference stats...");
		
		System.out.println("\nPreferences received: ");
		for (int i=0; i<Constants.PREFERENCES_PER_PERSON; i++) {
			System.out.println(i + ": " + preferences_received[i]);
		}
		
		System.out.println("Preference history: ");
		for (Preference preference : preference_history) {
			System.out.println("Week: " + preference.week + ", Order: " + preference.order + ", Granted: " + preference.granted);
		}
		
		System.out.println("\nPrinting Request stats...");
		
		System.out.print("\nRequests made: ");
		System.out.println(requests_made);
		System.out.print("\nRequest received: ");
		System.out.println(requests_received); 
		
		System.out.println("\nRequest history: ");
		for (Request request : request_history) {
			System.out.println("Week: " + request.week + ", Day: " + request.day + ", Granted: " + request.granted);
		}
		System.out.println("-----------------------------------");
		System.out.println("Current Bank Balance: " + this.getBank());
		System.out.println("-----------------------------------");
		System.out.println("\nTotal score given: " + total_given_score);
		System.out.println("-----------------------------------");
	}
	
}