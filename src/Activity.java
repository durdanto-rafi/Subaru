import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

	public List<Event> getData() {
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			resultSet = stmt.executeQuery("\r\n" + "SELECT\r\n" + "  lse.event_number,\r\n"
					+ "  lse.history_number,\r\n" + "  lse.progress_time,\r\n" + "  lse.position,\r\n"
					+ "  lse.event_action_number,\r\n" + "  lse.speed_number,\r\n" + "  lse.volume_number,\r\n"
					+ "  lss.school_contents_number,\r\n" + "  lss.student_number,\r\n"
					+ "  lss.registered_datetime,\r\n" + "  lss.contents_download_datetime,\r\n"
					+ "  lss.history_upload_datetime,\r\n" + "  lss.play_start_datetime,\r\n" + "  mse.event,\r\n"
					+ "  mss.speed,\r\n" + "  tbs.name,\r\n" + "  tbsc.name as contents_name,\r\n"
					+ "  tbss.name as subject_section_name,\r\n" + "  tbsub.name as subject_name\r\n" + "FROM\r\n"
					+ " (SELECT\r\n" + "   history_number\r\n" + "   , school_contents_number\r\n"
					+ "   , student_number\r\n" + "   , player3_code\r\n" + "   , key_word\r\n"
					+ "   , registered_datetime\r\n" + "   , contents_download_datetime\r\n"
					+ "   , history_upload_datetime\r\n" + "   , duration\r\n" + "   , play_start_datetime \r\n"
					+ "  FROM\r\n" + "    log_school_contents_history_student \r\n" + "  WHERE\r\n"
					+ "    contents_download_datetime between '2016-03-01 0:00:00' and '2016-08-31 0:00:00'\r\n"
					+ "  AND \r\n" + "    history_upload_datetime is not null\r\n" + "  AND\r\n"
					+ "    duration is not null\r\n" + " ) as lss\r\n" + "INNER JOIN\r\n"
					+ " log_school_contents_history_student_event as lse\r\n" + "ON\r\n"
					+ " lss.history_number = lse.history_number\r\n" + "LEFT OUTER JOIN\r\n"
					+ " mst_event_action as mse\r\n" + "ON\r\n"
					+ " lse.event_action_number = mse.event_action_number\r\n" + "LEFT OUTER JOIN\r\n"
					+ " mst_speed as mss\r\n" + "ON\r\n" + " lse.speed_number = mss.speed_number\r\n"
					+ "LEFT OUTER JOIN\r\n" + " tbl_student as tbs\r\n" + "ON\r\n"
					+ " tbs.student_number = lss.student_number\r\n" + "LEFT OUTER JOIN\r\n"
					+ " tbl_school_contents as tbsc\r\n" + "ON\r\n"
					+ " lss.school_contents_number = tbsc.school_contents_number\r\n" + "LEFT OUTER JOIN\r\n"
					+ " tbl_school_subject_section as tbss\r\n" + "ON\r\n"
					+ " tbss.school_subject_section_number = tbsc.school_subject_section_number\r\n"
					+ "LEFT OUTER JOIN\r\n" + " tbl_school_subject as tbsub\r\n" + "ON\r\n"
					+ " tbsub.school_subject_number = tbss.school_subject_number;");

			List<Event> events = new ArrayList<Event>();
			while (resultSet.next()) {
				if (resultSet != null) {
					
					//Eliminating initial and closing value
					if (resultSet.getDouble("event_action_number") != 0 && resultSet.getDouble("event_action_number") != 255) {
						Event event = new Event();
						event.event_number = resultSet.getDouble("event_number");
						event.history_number = resultSet.getDouble("history_number");
						event.progress_time = resultSet.getDouble("progress_time");
						event.position = resultSet.getDouble("position");
						event.event_action_number = resultSet.getDouble("event_action_number");
						event.speed_number = resultSet.getDouble("speed_number");
						event.school_contents_number = resultSet.getDouble("school_contents_number");
						event.student_number = resultSet.getDouble("student_number");
						event.subject_name = resultSet.getString("subject_name");
						events.add(event);
					}
				}
			}

			resultSet.close();
			stmt.close();
			conn.close();

			return events;

		} catch (Exception ex) {
			System.out.println(ex);
			return null;
		}
	}
}
