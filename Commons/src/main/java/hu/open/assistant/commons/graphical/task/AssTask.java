package hu.open.assistant.commons.graphical.task;

import java.util.TimerTask;

/**
 * Custom task used by AppWindow or AppPanel for creating tasks on a separate thread.
 */
public class AssTask extends TimerTask {

	RunComponent component;
	TaskName name;
	TaskType type;
	String parameter;

	/**
	 * Return the name of the task
	 */
	public TaskName getName() {
		return name;
	}

	/**
	 * Return the type of the task
	 */
	public TaskType getType() {
		return type;
	}

	/**
	 * Return the task parameter.
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * Create a task with a given name and type then call the runTask() method of the given component on another thread.
	 *
	 * @param component - containing the runTask method
	 * @param name      - of the task
	 * @param type      - of the task
	 */
	public AssTask(RunComponent component, TaskName name, TaskType type) {
		this(component, name, type, "");
	}

	/**
	 * Create a task with a given name, type and parameter then call the runTask() method of the given component on another thread.
	 *
	 * @param component - containing the runTask method
	 * @param name      - of the task
	 * @param parameter - of the task
	 */
	public AssTask(RunComponent component, TaskName name, TaskType type, String parameter) {
		this.component = component;
		this.name = name;
		this.type = type;
		this.parameter = parameter;
	}

	@Override
	public void run() {
		component.runTask(this);
	}
}
