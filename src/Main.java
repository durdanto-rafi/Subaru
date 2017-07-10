import java.util.List;

public class Main{
	public static void main(String[] args)
	{
		Activity activity = new Activity();
		List<Event> events = activity.getData();
		System.out.println(events.size());
	}
}