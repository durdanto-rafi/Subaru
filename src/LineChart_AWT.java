import org.jfree.chart.ChartPanel;

import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChart_AWT extends ApplicationFrame {
	Map<Long, List<Event>> contents = new LinkedHashMap<Long, List<Event>>();
	static long contentNumber = 5577;
	static int durationInSec = 0;

	public LineChart_AWT(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		JFreeChart lineChart = ChartFactory.createLineChart(chartTitle, "Years", "Number of Schools", createDataset(),
				PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
	}

	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		// Getting all database data in Hashmap
		Activity activity = new Activity();
		if (contents.size() == 0)
			contents = activity.getData();

		// Getting key as Event count value as Content number in Hashmap
		Map<Integer, Long> contentNumbers = new TreeMap<Integer, Long>();
		for (Map.Entry<Long, List<Event>> entry : contents.entrySet()) {
			contentNumbers.put(entry.getValue().size(), entry.getKey());
		}

		// Getting one content data
		List<Event> events = contents.get(contentNumber);

		if (events != null) {
			LinkedHashMap<Integer, List<Event>> pauses = new LinkedHashMap<Integer, List<Event>>();
			LinkedHashMap<Integer, List<Event>> rewinds = new LinkedHashMap<Integer, List<Event>>();

			for (Event event : events) {
				// Setting duration in second
				if (durationInSec == 0)
					durationInSec = (int) event.duration;

				// Checking for pause value
				if (event.type == 1) {
					List<Event> newEvent = new ArrayList<>();
					if (pauses.get((int) event.position) != null) {
						newEvent = pauses.get((int) event.position);
					}
					newEvent.add(event);
					pauses.put((int) event.position, newEvent);
				}

				// Checking for rewind value
				if (event.type == 2) {
					List<Event> newEvent = new ArrayList<>();
					if (rewinds.get((int) event.position) != null) {
						newEvent = rewinds.get((int) event.position);
					}
					newEvent.add(event);
					rewinds.put((int) event.position, newEvent);
				}
			}

			for (int i = 0; i < durationInSec; i++) {
				// Printing rewind value
				dataset.addValue(rewinds.get(i) == null ? 0 : rewinds.get(i).size(), rewinds.size() + " Rewind", getTime(i));
				
				// Printing pause value
				dataset.addValue(pauses.get(i) == null ? 0 : pauses.get(i).size(), pauses.size() + " Pause", getTime(i));
			}
		}
		return dataset;
	}

	public static void main(String[] args) {
		LineChart_AWT chart = new LineChart_AWT("School Vs Years", "Numer of Schools vs years");
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);
	}

	private String getTime(int second) {
		return LocalTime.MIN.plusSeconds(second).toString();
	}

	private List<Integer> sortHashmap(LinkedHashMap<Integer, List<Event>> hashMap) {
		List<Integer> sortedKeys = new ArrayList<>();
		sortedKeys.addAll(hashMap.keySet());
		Collections.sort(sortedKeys);
		return sortedKeys;
	}

	public void actionPerformed(final ActionEvent e) {

		if (e.getActionCommand().equals("ADD_DATASET")) {
			// createDataset(5533);
		}

	}

}