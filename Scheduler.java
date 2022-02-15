import java.util.ArrayList;
import org.chocosolver.util.tools.ArrayUtils;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;


public class Scheduler {
	Model model;
	
	int[] workers_min_days_per_week;
	int[] workers_max_days_per_week;
	int[] banks;
	
	int[] workers_needed_per_day = {1, 1, 1, 1, 1, 1, 1};
	
	int num_workers;
	
	IntVar[][] timetable;
	
	ArrayList<IntVar[]> preferences;
	IntVar[] preference_score_assignment_after_solving;
	int[][] preference_scores;
	
	ArrayList<IntVar[]> requests;
	IntVar[] request_score_assignment_after_solving;
	int[][] request_scores;
	int[] requests_made_per_employee;
	
	IntVar total_pref_score;
	IntVar total_reqs_score;
	IntVar total_overall_score;
	
	public Scheduler(ArrayList<Employee> employees) {
		Model model = new Model("FYP");
		this.model = model;
		this.num_workers = employees.size();
		
		this.workers_max_days_per_week = new int[this.num_workers];
		this.workers_min_days_per_week = new int[this.num_workers];
		this.banks = new int[this.num_workers];
		
		for (int i=0; i<this.num_workers; i++) {
			this.workers_max_days_per_week[i] = employees.get(i).max_days_per_week;
			this.workers_min_days_per_week[i] = employees.get(i).min_days_per_week;
			this.banks[i] = employees.get(i).bank;
		}
		
		this.timetable = model.intVarMatrix("Timetable", num_workers, Constants.days_per_week, 0, 1);
		
		
		this.preferences = new ArrayList<IntVar[]>();
		this.preference_score_assignment_after_solving = model.intVarArray("FinalPrefScores", num_workers, 0, 10000000);
		this.preference_scores = new int[num_workers][Constants.PREFERENCES_PER_PERSON];
		for (int i=0; i<num_workers; i++) {
			preferences.add(model.intVarArray("PreferenceAssignments", Constants.PREFERENCES_PER_PERSON, 0, 1));
		}
		 
		this.requests = new ArrayList<IntVar[]>();
		this.request_score_assignment_after_solving = model.intVarArray("FinalReqsScores", this.num_workers, 0, 10000000);
		this.request_scores = new int[num_workers][Constants.MAX_REQUESTS_PER_PERSON];
		this.requests_made_per_employee = new int[num_workers];
		for (int i=0; i<num_workers; i++) {
			requests.add(model.intVarArray("RequestsAssignments", Constants.MAX_REQUESTS_PER_PERSON, 0, 1));
		}
		
		
		this.total_pref_score = model.intVar("TotalPrefScore", 0, 100000);
		this.total_reqs_score = model.intVar("TotalReqsScore", 0, 100000);
		this.total_overall_score = model.intVar("TotalOverallScore", 0, 100000);
		
		this.addHardConstraints();
	}

	private void addHardConstraints() {
		// the total of the entire matrix should add up to the sum of workers_hours_per_week
		int sum_workers_needed = 0;
	    for (int value : workers_needed_per_day) {
	        sum_workers_needed += value;
	    }
	    System.out.println(sum_workers_needed);
		model.sum(ArrayUtils.flatten(timetable), "=", sum_workers_needed).post();
			
		// every column should have a sum of exactly the sum of workers needed in that day
		for (int i=0; i<Constants.days_per_week; i++) {
			model.sum(ArrayUtils.getColumn(timetable, i), "=", workers_needed_per_day[i]).post();
		}
		
		// workers must work between their working day limits every week
		for (int i=0; i<num_workers; i++) {
			model.sum(timetable[i], "<=", workers_max_days_per_week[i]).post();
			model.sum(timetable[i], ">=", workers_min_days_per_week[i]).post();
		}
	}
	
	public void addPref(int person, int day, int order, int bank) { // no dayOff/dayOn thing
		model.ifOnlyIf(this.model.arithm(timetable[person][day], ">", 0), this.model.arithm(preferences.get(person)[order-1], "<", 1));
		int score = bank * (30 / order) * Constants.BASE_SCORE_PREF;
		preference_scores[person][order-1] = score;
	}
	
	public void addRequest(int person, int day, int bank) {
		model.ifOnlyIf(model.arithm(timetable[person][day], ">", 0), model.arithm(requests.get(person)[requests_made_per_employee[person]], "<", 1));
		int score = bank * Constants.BASE_SCORE_REQ;
		request_scores[person][requests_made_per_employee[person]] = score;
		requests_made_per_employee[person]++;
	}
	
	public void optimise() {
		for (int i=0; i<num_workers; i++) {
			model.scalar(preferences.get(i), preference_scores[i], "=", preference_score_assignment_after_solving[i]).post();
			model.scalar(requests.get(i), request_scores[i], "=", request_score_assignment_after_solving[i]).post();
		}
		
		model.sum(preference_score_assignment_after_solving, "=", total_pref_score).post();
		model.sum(request_score_assignment_after_solving, "=", total_reqs_score).post();
		model.arithm(total_pref_score, "+", total_reqs_score, "=", total_overall_score).post();
		
	}
	
	public Solution solve() {
		Solver solver = model.getSolver();
		model.setObjective(Model.MAXIMIZE, total_overall_score);
		Solution solution = new Solution(model);
		while (solver.solve()) {
			solution.record();
			System.out.println("Solution " + solver.getSolutionCount() + ":");
			printSolution();
			printInformation();
			return solution;
		}
		System.out.println();
		System.out.println();
		solver.printStatistics();
		
		return solution;
	}
	
	public void printSolution() {
		System.out.println("Printing Optimal Solution");
		String row_sol;
		for (int i = 0; i < num_workers; i++) {
			int count = 0;
			row_sol = "Worker " + i + ":\t";
			for (int j = 0; j < Constants.days_per_week; j++) {
				row_sol += timetable[i][j].getValue() + "\t";
				count += 1;
				if (count == Constants.hours_per_day) {
					row_sol += " | \t";
					count = 0;
				}
			}
			System.out.println(row_sol);
			
		}	
		System.out.println();
	}
	
	public void printInformation() {
		System.out.println("-----------------------------------");
		System.out.println("Printing preference assignments...");
		for (int i = 0; i < num_workers; i++) {
			System.out.println("Worker " + i + ": ");
			for (int j = 0; j < Constants.PREFERENCES_PER_PERSON; j++) {
				System.out.print(preferences.get(i)[j].getValue() + "\t");
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println("Printing preference scores...");
		for (int i = 0; i < num_workers; i++) {
			System.out.println("Worker " + i + ": ");
			for (int j = 0; j < Constants.PREFERENCES_PER_PERSON; j++) {
				System.out.print(preference_scores[i][j] + "\t");
			}
			System.out.println();
			
		}
		System.out.println();
		
		System.out.println("Printing preference scores/assignments scalar IntVars...");
		for (int i = 0; i < num_workers; i++) {
			System.out.print(preference_score_assignment_after_solving[i].getValue() + "\t");
		}
		System.out.println("\n");
		
		System.out.println("Printing total preference score...");
		System.out.println(total_pref_score + "\n");
		
		System.out.println("-----------------------------------");
		System.out.println();
		System.out.println("Printing request assignments...");
		for (int i = 0; i < num_workers; i++) {
			if (requests_made_per_employee[i] > 0) {
				System.out.println("Worker " + i + ": ");
				for (int j = 0; j < requests_made_per_employee[i]; j++) {
					System.out.print(requests.get(i)[j].getValue() + "\t");
				}
				System.out.println();
			}
		}
		
		
		System.out.println("Printing request scores...");
		
		for (int i = 0; i < num_workers; i++) {
			if (requests_made_per_employee[i] > 0) {
				System.out.println("Worker " + i + ": ");
				for (int j = 0; j < requests_made_per_employee[i]; j++) {
					System.out.print(request_scores[i][j] + "\t");
				}
				System.out.println();
			}
		}
		System.out.println();
		
		System.out.println("Printing request scores/assignments scalar IntVars...");
		for (int i = 0; i < num_workers; i++) {
			if (requests_made_per_employee[i] > 0) {
				System.out.print(request_score_assignment_after_solving[i].getValue() + "\t");
			}
		}
		System.out.println("\n");
		
		System.out.println("Printing total requests score...");
		System.out.println(total_reqs_score + "\n");
		
		System.out.println("-----------------------------------");
		System.out.println("Printing total overall score...");
		System.out.println(total_overall_score + "\n");
		System.out.println("-----------------------------------");
		
		System.out.println("-----------------------------------");
		System.out.println("Printing average preference score...");
		System.out.println(total_pref_score.getValue() / num_workers + "\n");
		System.out.println("-----------------------------------");
		
		System.out.println("-----------------------------------");
		System.out.println("Printing expected preference score per worker...");
		int sum = 0;
		for (int i = 0; i < num_workers; i++) {
			sum += banks[i];
		}
		for (int i = 0; i < num_workers; i++) {
			float percentage_of_bank = (banks[i] * 100) / sum;
			System.out.println("Worker " + i + ": " + total_pref_score.getValue() * (percentage_of_bank / 100) + "\n");
		}
		System.out.println("-----------------------------------");
	}

}






