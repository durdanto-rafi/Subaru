import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import java.time.LocalTime;

public class BarChart_AWT extends ApplicationFrame {

	public BarChart_AWT(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Content Time", "Action Count", createDataset(),
				PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
	}

	private CategoryDataset createDataset() {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		Activity activity = new Activity();
		Map<Long, List<Event>> contents = activity.getData();
		List<Event> events = contents.get((long)5533);
		
		if(events != null) {
			// Group by position value of a single content
			LinkedHashMap<Integer, List<Event>> pauses = new LinkedHashMap<Integer, List<Event>>();
			for (Event event : events) {
				if(event.type==1) {
					List<Event> newEvent = new ArrayList<>();
					if(pauses.get((int)event.position) != null) {
						newEvent = pauses.get((int)event.position);
					}
					newEvent.add(event);
					pauses.put((int)event.position, newEvent);
				}
			}
			
			//Sorting in ascending order
			List<Integer> sortedKeys = new ArrayList<>();
			sortedKeys.addAll(pauses.keySet());
			Collections.sort(sortedKeys);
			
			// Generating Graph
			for (Integer position : sortedKeys){
				dataset.addValue(pauses.get(position).size() , "Second", getTime(position));
			}
		}
		return dataset;
	}

	public static void main(String[] args) {
		BarChart_AWT chart = new BarChart_AWT("Car Usage Statistics", "Pause Graph");
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);
	}
	
	private String getTime(int second) {
		return LocalTime.MIN.plusSeconds(second).toString();
	}
}