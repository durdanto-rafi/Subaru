import org.jfree.chart.ChartPanel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChart_AWT extends ApplicationFrame {

	public LineChart_AWT(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		JFreeChart lineChart = ChartFactory.createLineChart(chartTitle, "Years", "Number of Schools", createDataset(), PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
	}

	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		Activity activity = new Activity();
		Map<Long, List<Event>> contents = activity.getData();
		List<Event> events = contents.get((long)5533);
		
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
		
		for (Integer position : sortedKeys){
			dataset.addValue(pauses.get(position).size() , "Pause", position+"");
		}
		return dataset;
	}

	public static void main(String[] args) {
		LineChart_AWT chart = new LineChart_AWT("School Vs Years", "Numer of Schools vs years");
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);
	}
	
	
}