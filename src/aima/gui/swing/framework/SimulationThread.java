package aima.gui.swing.framework;

import java.awt.EventQueue;

import Presentation.EightPuzzleApp;
import Presentation.EightPuzzleTree;
import aima.core.util.CancelableThread;

/**
 * Background thread, which is used for simulation.
 * 
 * @author Ruediger Lunde
 */
public class SimulationThread extends CancelableThread {
	private AgentAppFrame frame;
	private AgentAppController controller;
	/** Decides whether the controller's run or step method is called. */
	private boolean stepMode;
	private boolean isCalculatingManHattanHeuristic;
	private boolean isCalculatingMistiledHeuristic;
	private boolean isDrawingTree;
	
	/** Standard constructor. */
	public SimulationThread(AgentAppFrame frame, AgentAppController controller, boolean stepMode) {
		this.isCalculatingManHattanHeuristic = false;
		this.frame = frame;
		this.controller = controller;
		this.stepMode = stepMode;
	}
	
	/** Cancels and interrupts the thread. */
	@Override
	public void interrupt() {
		cancel();
		super.interrupt();
	}
	
	/**
	 * Calls the run or step method of the controller and then lets the
	 * event dispatching thread perform the update method.
	 */
	@Override
	public void run() {
		MessageLogger logger = frame.getMessageLogger();
		try {
			if (isCalculatingManHattanHeuristic) {
				controller.calculateManhattanHeuristic();
				isCalculatingManHattanHeuristic = false;
			} else if (isCalculatingMistiledHeuristic) {
				controller.calculateMistiledHeuristic();
				isCalculatingMistiledHeuristic = false;
			}
			else if (isDrawingTree) {
				EightPuzzleApp.mainView.drawTree2();
				isDrawingTree = false;
				EightPuzzleTree.clearTree();
			} else {
				if (!stepMode)
					controller.run(frame.getMessageLogger());
				else
					controller.step(frame.getMessageLogger());
			}
		} catch (Exception e) {
			logger.log
			("Error: Somthing went wrong running the agent (" + e + ").");
			e.printStackTrace(); // for debugging
		}
		try {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					controller.update(SimulationThread.this);
					frame.setSimulationThread(null);
				}
			});
		} catch(Exception e) {
			logger.log
			("Error: Somthing went wrong when updating the GUI (" + e + ").");
			e.printStackTrace(); // for debugging
		}
	}

	public void calculateManhattanHeuristic() {
		this.isCalculatingManHattanHeuristic = true;
	}


	public void calculateMistiledHeuristic() {
		this.isCalculatingMistiledHeuristic = true;
	}

	public void drawTree() {
		isDrawingTree = true;
	}
}

