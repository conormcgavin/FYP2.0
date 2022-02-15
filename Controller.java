import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;

public class Controller {
	
	ArrayList<Employee> employees;
	int week;
	Scheduler scheduler;
	
	public Controller() {
		this.employees = new ArrayList<Employee>();
		this.week = 0;
	}
	
	public void addEmployee(Employee e) {
		this.employees.add(e);
	}
	
	public void removeEmployee(Employee e) {
		this.employees.remove(e);
	}
	
	public Scheduler initialiseScheduler(String modelName) {
		System.out.println("Initialising Scheduler");
		this.scheduler = new Scheduler(this.employees);
		return scheduler;
	}
	
	public void addEmployeeRequestsAndPreferencesToModel() {
		System.out.println("Adding employee requests and preferences to model.");
		for (int i = 0; i<employees.size(); i++) {
			Preference[] prefs = employees.get(i).getPreferences();
			for (int j = 0; j<Constants.PREFERENCES_PER_PERSON; j++) {
				Preference pref = prefs[j];
				scheduler.addPref(i, pref.day, pref.order, employees.get(i).getBank());
			}
			if (employees.get(i).requests_made_this_week > 0) {
				Request[] reqs = employees.get(i).getRequests();
				for (int j = 0; j<employees.get(i).requests_made_this_week; j++) {
					Request req = reqs[j];
					scheduler.addRequest(i, req.day, employees.get(i).getBank());
				}
			}
		}
	}
	
	public void updateBanks() {
		System.out.println("Updating Bank Credits");
		// update banks based on what requests were assigned to people
		for (int employee = 0; employee < employees.size(); employee ++) {
			if (scheduler.requests_made_per_employee[employee] > 0) {;
				Employee e = employees.get(employee);
				for (int i=0; i<scheduler.requests_made_per_employee[employee]; i++) {
					if (scheduler.requests.get(employee)[i].getValue() == 1) {
						int amount = Constants.REQ_GRANTED_PENALTY;
						e.removeBank(amount);
					}
				}
			}
			
		}
		int sum = 0;
		for (int i = 0; i < employees.size(); i++) {
			sum += employees.get(i).bank;
		}
		for (int i = 0; i < employees.size(); i++) {
			float percentage_of_bank = (employees.get(i).bank * 100) / sum;
			float expected_score = scheduler.total_pref_score.getValue() * (percentage_of_bank / 100);
			int actual_score = scheduler.preference_score_assignment_after_solving[i].getValue();
		}
		
		
	}
	
	public void recordHistory() {
		System.out.println("Recording History.");
		
		for (int employee = 0; employee < employees.size(); employee ++) {
			Employee e = employees.get(employee);
			
			for (int i=0; i<Constants.PREFERENCES_PER_PERSON; i++) {
				Preference pref = e.preferences[i];
				if (scheduler.preferences.get(employee)[i].getValue() == 1) {
					pref.granted = true;
					e.preferences_received[i] += 1;
				}
				e.preference_history.add(pref);
				e.total_given_score += scheduler.preference_score_assignment_after_solving[employee].getValue();
			}
			if (scheduler.requests_made_per_employee[employee] > 0) {
				for (int i=0; i<scheduler.requests_made_per_employee[employee]; i++) {
					Request req = e.requests[i];
					if (scheduler.requests.get(employee)[i].getValue() == 1) {
						req.granted = true;
						e.requests_received += 1;
					}
					e.request_history.add(req);
					e.total_given_score += scheduler.request_score_assignment_after_solving[i].getValue();
					
				}
			}
			e.bank_history[week] = e.bank;
			e.pref_score_history[week] = scheduler.preference_score_assignment_after_solving[employee].getValue();
			e.reqs_score_history[week] = scheduler.request_score_assignment_after_solving[employee].getValue();
			e.printStats();
		}
	}
	
	public void clean() {
		for (int employee = 0; employee < employees.size(); employee ++) {
			 employees.get(employee).requests = new Request[]{};
			 employees.get(employee).requests_made_this_week = 0;
		}
	}

	public Solution runWeek() {
		addEmployeeRequestsAndPreferencesToModel();
		scheduler.optimise();
		Solution solution = scheduler.solve();
		updateBanks();
		recordHistory();
		return solution;
	}
	
	public void newWeek() {
		for (Employee employee : employees) {
			if (employee.requests_made_this_week == 0) {
				if (employee.getBank() < Constants.MAX_BANK) {
					employee.addBank(Constants.REGEN_PER_WEEK);
				}
			}
		}
		clean();
		week += 1;
		for (int employee = 0; employee < employees.size(); employee ++) {
			for (int i=0; i<Constants.PREFERENCES_PER_PERSON ; i++) {
				 employees.get(employee).preferences[i].week = week;
			}
			
		}
		scheduler = initialiseScheduler("FYP");
	}
}