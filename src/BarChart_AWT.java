import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;

public class BarChart_AWT extends ApplicationFrame implements ActionListener {
	Map<Long, List<Event>> contents = new LinkedHashMap<Long, List<Event>>();
	static long contentNumber = 5533;
	static int durationInSec = 0;
	int pauseCount, rewindCount, forwardCount;;

	LinkedHashMap<Integer, List<Event>> pauses = new LinkedHashMap<Integer, List<Event>>();
	LinkedHashMap<Integer, List<Event>> rewinds = new LinkedHashMap<Integer, List<Event>>();
	LinkedHashMap<Integer, List<Event>> forwards = new LinkedHashMap<Integer, List<Event>>();

	public BarChart_AWT(String applicationTitle, String chartTitle) {

		super(applicationTitle);
		// Getting application data
		getData();

		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Content Time " + getTime(durationInSec),
				"Action Count", createDataset(), PlotOrientation.VERTICAL, true, true, false);

		final JPanel content = new JPanel(new BorderLayout());

		final ChartPanel chartPanel = new ChartPanel(barChart);
		content.add(chartPanel);

		final JButton btnSubmit = new JButton("Submit");
		btnSubmit.setActionCommand("ADD_DATASET");
		btnSubmit.addActionListener(this);

		final JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(btnSubmit);
		buttonPanel.add(btnSubmit);

		content.add(buttonPanel, BorderLayout.SOUTH);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(content);
	}

	private CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < durationInSec; i++) {

			// Printing rewind value
			dataset.addValue(forwards.get(i) == null ? 0 : forwards.get(i).size(), forwardCount + " Forward", getTime(i));

			// Printing rewind value
			dataset.addValue(rewinds.get(i) == null ? 0 : rewinds.get(i).size(), rewindCount + " Rewind", getTime(i));

			// Printing pause value
			dataset.addValue(pauses.get(i) == null ? 0 : pauses.get(i).size(), pauseCount + " Pause", getTime(i));
		}
		return dataset;
	}

	public static void main(String[] args) {
		BarChart_AWT chart = new BarChart_AWT("LMS Data Statistics", "Content Number - " + contentNumber);
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

	private void getData() {
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

			for (Event event : events) {
				// Setting duration in second
				if (durationInSec == 0)
					durationInSec = (int) event.duration;

				// Checking for pause value
				if (event.type == 1) {
					pauseCount ++;
					List<Event> newEvent = new ArrayList<>();
					if (pauses.get((int) event.position) != null) {
						newEvent = pauses.get((int) event.position);
					}
					newEvent.add(event);
					pauses.put((int) event.position, newEvent);
				}

				// Checking for rewind value
				if (event.type == 2) {
					rewindCount ++;
					List<Event> newEvent = new ArrayList<>();
					if (rewinds.get((int) event.position) != null) {
						newEvent = rewinds.get((int) event.position);
					}
					newEvent.add(event);
					rewinds.put((int) event.position, newEvent);
				}

				// Checking for forward value
				if (event.type == 3) {
					forwardCount ++;
					List<Event> newEvent = new ArrayList<>();
					if (forwards.get((int) event.position) != null) {
						newEvent = forwards.get((int) event.position);
					}
					newEvent.add(event);
					forwards.put((int) event.position, newEvent);
				}
			}
		}
	}
}