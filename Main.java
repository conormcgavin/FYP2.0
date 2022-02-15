import org.chocosolver.solver.Solution;


public class Main {
	public static void main(String[] args) {
		Employee e1 = new Employee("John", 0, 4, 1);
		Employee e2 = new Employee("Mary", 0, 4, 1);
		Employee e3 = new Employee("Paul", 0, 100, 1);
		Employee e4 = new Employee("Joanne", 10, 10, 1);
		Employee e5 = new Employee("Joanne", 0, 40, 1);
		Employee e6 = new Employee("Joanne", 0, 40, 1);
		Employee e7 = new Employee("Joanne", 0, 40, 1);
		
		Controller c = new Controller();
		c.addEmployee(e1);
		c.addEmployee(e2);
		
		/*
		c.addEmployee(e4);
		c.addEmployee(e5);
		c.addEmployee(e6);
		c.addEmployee(e7);
		*/
		
		c.initialiseScheduler("hi");
		
		e1.addPreference(c.week, 0, 1, true);
		e1.addPreference(c.week, 1, 2, true);
		e1.addPreference(c.week, 2, 3, true);
		
		e2.addPreference(c.week, 3, 1, true);
		e2.addPreference(c.week, 4, 2, true);
		e2.addPreference(c.week, 5, 3, true);
		
		e1.addRequest(c.week, 6, true);
		
		/*
		e3.addPreference(c.week, 0, 9, 1, true);
		e3.addPreference(c.week, 10, 19, 2, true);
		e3.addPreference(c.week, 20, 29, 3, true);
		
		e4.addPreference(c.week, 0, 9, 1, true);
		e4.addPreference(c.week, 10, 19, 2, true);
		e4.addPreference(c.week, 20, 29, 3, true);
		
		e5.addPreference(c.week, 0, 9, 1, true);
		e5.addPreference(c.week, 10, 19, 2, true);
		e5.addPreference(c.week, 20, 29, 3, true);
		
		e6.addPreference(c.week, 0, 9, 1, true);
		e6.addPreference(c.week, 10, 19, 2, true);
		e6.addPreference(c.week, 20, 29, 3, true);
		
		e7.addPreference(c.week, 0, 9, 1, true);
		e7.addPreference(c.week, 10, 19, 2, true);
		e7.addPreference(c.week, 20, 29, 3, true);
	*/
		Solution s = c.runWeek();
		c.newWeek();
		c.runWeek();
	}
}




/*
 * -------QUESTIONS FOR KEN--------
 * 1. Recording solutions properly, cant figure out yet.
 */
