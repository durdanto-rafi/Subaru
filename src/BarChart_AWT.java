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
	static long contentNumber = 5577;
	
	public BarChart_AWT(String applicationTitle, String chartTitle) {
		super(applicationTitle);
		JFreeChart barChart = ChartFactory.createBarChart(chartTitle, "Content Time", "Action Count", createDataset(), PlotOrientation.VERTICAL, true, true, false);

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
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		

		Activity activity = new Activity();
		if(contents.size() == 0)
			contents = activity.getData();
		
		Map<Integer, Long> contentNumbers = new TreeMap<Integer, Long>();
		for(Map.Entry<Long, List<Event>> entry : contents.entrySet()) {
			contentNumbers.put(entry.getValue().size(), entry.getKey());
		}
		
		List<Event> events = contents.get(contentNumber);
		
		if(events != null) {
			// Group by position value of a single content
			LinkedHashMap<Integer, List<Event>> pauses = new LinkedHashMap<Integer, List<Event>>();
			LinkedHashMap<Integer, List<Event>> rewinds = new LinkedHashMap<Integer, List<Event>>();
			
			for (Event event : events) {
				if(event.type==1) {
					List<Event> newEvent = new ArrayList<>();
					if(pauses.get((int)event.position) != null) {
						newEvent = pauses.get((int)event.position);
					}
					newEvent.add(event);
					pauses.put((int)event.position, newEvent);
				}
				if(event.type==2) {
					List<Event> newEvent = new ArrayList<>();
					if(rewinds.get((int)event.position) != null) {
						newEvent = rewinds.get((int)event.position);
					}
					newEvent.add(event);
					rewinds.put((int)event.position, newEvent);
				}
			}
			
			// Printing pause value
			for (Integer position : sortHashmap(pauses)){
				dataset.addValue(pauses.get(position).size() , pauses.size()+" Pause", getTime(position));
			}
			
			// Printing rewind value
			for (Integer position : sortHashmap(rewinds)){
				dataset.addValue(rewinds.get(position).size() , rewinds.size()+" Rewind", getTime(position));
			}
		}
		return dataset;
	}

	public static void main(String[] args) {
		BarChart_AWT chart = new BarChart_AWT("LMS Data Statistics", "Content Number - "+contentNumber);
		chart.pack();
		RefineryUtilities.centerFrameOnScreen(chart);
		chart.setVisible(true);
	}
	
	private String getTime(int second) {
		return LocalTime.MIN.plusSeconds(second).toString();
	}
	
	private List<Integer> sortHashmap(LinkedHashMap<Integer, List<Event>> hashMap){
		List<Integer> sortedKeys = new ArrayList<>();
		sortedKeys.addAll(hashMap.keySet());
		Collections.sort(sortedKeys);
		return sortedKeys;
	}
	
	public void actionPerformed(final ActionEvent e) {

		if (e.getActionCommand().equals("ADD_DATASET")) {
			//createDataset(5533);
		} 

	}
}