import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Activity {
	private Connection conn;
	private Statement stmt;
	private ResultSet resultSet;
	MysqlDataSource dataSource;

	public Activity() {
		dataSource = new MysqlDataSource();
		dataSource.setUser("root");
		dataSource.setPassword("root123");
		dataSource.setServerName("localhost");
		dataSource.setDatabaseName("subaru");
	}

	public Map<Long, List<Event>> getData() {
		try {
			System.out.println("Loading");
			Instant queryStart = Instant.now();
			Thread.sleep(10);

			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery("SELECT\r\n" + 
					"	lse.event_number,\r\n" + 
					"	lse.history_number,\r\n" + 
					"	lse.progress_time,\r\n" + 
					"	lse.position,\r\n" + 
					"	lss.duration,\r\n" + 
					"	lse.event_action_number,\r\n" + 
					"	lse.speed_number,\r\n" + 
					"	lse.volume_number,\r\n" + 
					"	lss.school_contents_number,\r\n" + 
					"	lss.student_number,\r\n" + 
					"	lss.registered_datetime,\r\n" + 
					"	lss.contents_download_datetime,\r\n" + 
					"	lss.history_upload_datetime,\r\n" + 
					"	lss.play_start_datetime,\r\n" + 
					"	mse. EVENT,\r\n" + 
					"	mss.speed,\r\n" + 
					"	tbs. NAME,\r\n" + 
					"	tbsc. NAME AS contents_name,\r\n" + 
					"	tbss. NAME AS subject_section_name,\r\n" + 
					"	tbsub. NAME AS subject_name\r\n" + 
					"FROM\r\n" + 
					"	(\r\n" + 
					"		SELECT\r\n" + 
					"			history_number,\r\n" + 
					"			school_contents_number,\r\n" + 
					"			student_number,\r\n" + 
					"			player3_code,\r\n" + 
					"			key_word,\r\n" + 
					"			registered_datetime,\r\n" + 
					"			contents_download_datetime,\r\n" + 
					"			history_upload_datetime,\r\n" + 
					"			duration,\r\n" + 
					"			play_start_datetime\r\n" + 
					"		FROM\r\n" + 
					"			log_school_contents_history_student\r\n" + 
					"		WHERE\r\n" + 
					"			contents_download_datetime BETWEEN '2016-03-01 0:00:00'\r\n" + 
					"		AND '2016-08-31 0:00:00'\r\n" + 
					"		AND history_upload_datetime IS NOT NULL\r\n" + 
					"		AND duration IS NOT NULL\r\n" + 
					"	) AS lss\r\n" + 
					"INNER JOIN log_school_contents_history_student_event AS lse ON lss.history_number = lse.history_number\r\n" + 
					"LEFT OUTER JOIN mst_event_action AS mse ON lse.event_action_number = mse.event_action_number\r\n" + 
					"LEFT OUTER JOIN mst_speed AS mss ON lse.speed_number = mss.speed_number\r\n" + 
					"LEFT OUTER JOIN tbl_student AS tbs ON tbs.student_number = lss.student_number\r\n" + 
					"LEFT OUTER JOIN tbl_school_contents AS tbsc ON lss.school_contents_number = tbsc.school_contents_number\r\n" + 
					"LEFT OUTER JOIN tbl_school_subject_section AS tbss ON tbss.school_subject_section_number = tbsc.school_subject_section_number\r\n" + 
					"LEFT OUTER JOIN tbl_school_subject AS tbsub ON tbsub.school_subject_number = tbss.school_subject_number\r\n" + 
					"WHERE\r\n" + 
					"	lse.event_action_number <> 255\r\n" + 
					"AND ! (\r\n" + 
					"	lse.event_action_number = 1\r\n" + 
					"	AND lse.position = lss.duration\r\n" + 
					")\r\n" + 
					"AND ! (\r\n" + 
					"	lse.event_action_number = 1\r\n" + 
					"	AND lse.position = 0\r\n" + 
					")\r\n" + 
					"AND ! (\r\n" + 
					"	lse.progress_time = 0\r\n" + 
					"	AND lse.position = 0\r\n" + 
					"	AND lse.event_action_number = 0\r\n" + 
					")");

			// getHistory(resultSet);
			int a = 0;
			Map<Long, List<Event>> groups = new HashMap<Long, List<Event>>();
			Event previousEvent = new Event();
			while (resultSet.next()) {
				if (resultSet != null) {

					Event event = new Event();
					event.duration = resultSet.getDouble("duration")/ 1000;
					event.event_number = resultSet.getLong("event_number");
					event.history_number = resultSet.getLong("history_number");
					event.progress_time = resultSet.getDouble("progress_time")/ 1000;
					event.position = resultSet.getDouble("position")/ 1000;
					event.event_action_number = resultSet.getLong("event_action_number");
					event.speed_number = resultSet.getLong("speed_number");
					event.school_contents_number = resultSet.getLong("school_contents_number");
					event.student_number = resultSet.getLong("student_number");
					event.subject_name = resultSet.getString("subject_name");
					
					// Pause flagging
					if (event.speed_number == 0 && event.event_action_number == 2)
						event.type = 1;
					
					// Rewind Flagging
					if(event.event_action_number == 1 && previousEvent.position > event.position)
						event.type = 2;
					
					// Forward Flagging
					if(event.event_action_number == 1 && previousEvent.position < event.position)
						event.type = 3;

					List<Event> group = groups.get(event.school_contents_number);
					if (group == null) {
						group = new ArrayList<Event>();
						groups.put(event.school_contents_number, group);
					}
					group.add(event);
					previousEvent = event;
				}
				
				Instant processEnd = Instant.now();
				System.out.println("Process Time :" + Duration.between(queryStart, processEnd));
			}
			
			resultSet.close();
			stmt.close();
			conn.close();
			return groups;

		} catch (Exception ex) {
			System.out.println(ex);
			return null;
		}
	}

	public LinkedHashMap<Long, List<Event>> getHistory(ResultSet resultSet) {
		Instant processStart = Instant.now();
		List<Event> events = new ArrayList<Event>();
		LinkedHashMap<Long, List<Event>> histories = new LinkedHashMap<Long, List<Event>>();
		Event previousEvent = new Event();

		try {
			while (resultSet.next()) {
				if (resultSet != null) {

					// Removing ending data
					if (resultSet.getDouble("event_action_number") == 255)
						continue;

					// Removing initial data
					if (resultSet.getDouble("progress_time") == 0 && resultSet.getDouble("position") == 0
							&& resultSet.getDouble("event_action_number") == 0)
						continue;

					if (previousEvent.history_number != resultSet.getDouble("history_number")) {
						histories.put(previousEvent.history_number, events);
						events = new ArrayList<Event>();
					}

					Event event = new Event();
					event.event_number = resultSet.getLong("event_number");
					event.history_number = resultSet.getLong("history_number");
					event.progress_time = resultSet.getLong("progress_time");
					event.position = resultSet.getLong("position");
					event.event_action_number = resultSet.getLong("event_action_number");
					event.speed_number = resultSet.getLong("speed_number");
					event.school_contents_number = resultSet.getLong("school_contents_number");
					event.student_number = resultSet.getLong("student_number");
					event.subject_name = resultSet.getString("subject_name");

					// Pause eliminate
					if (resultSet.getDouble("position") == previousEvent.position && previousEvent.speed_number == 0)
						event.type = 1;

					events.add(event);
					previousEvent = event;
				}
			}
			Instant processEnd = Instant.now();
			System.out.println("Process Time :" + Duration.between(processStart, processEnd));

			return histories;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
