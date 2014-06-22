package utils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;

public class RelativeTime {

	public static String fromNow(LocalDateTime time){

		LocalDateTime now = LocalDateTime.now();
		
		Period period = Period.between(time.toLocalDate(), now.toLocalDate());
		
		if(period.getYears() > 0){
			if(period.getMonths() < 3)
				return "about a year ago";
			else return "more than a year ago";
		}
		
		if(period.getMonths() > 0){
			Period withoutMonths = period.minusMonths(period.getMonths());
			String months = "a month ago";
			if(period.getMonths() > 1)
				months = period.getMonths() + " months ago";
			if(withoutMonths.getDays() < 4)
				return months;
			else
				return "more than " + months;
		}
		
		if(period.getDays() == 1)
			return "yesterday";
		
		if(period.getDays() > 0)
			return period.getDays() + " days ago";
		
		if(period.getDays() == 0){
			Duration duration = Duration.between(time.toLocalTime(), now.toLocalTime());
			long totalSeconds = duration.getSeconds();
			long hours = totalSeconds / 3600;
			long leftOvers = totalSeconds % 3600;
			long minutes =  leftOvers / 60;
			long seconds = leftOvers % 60;
			
			if(hours == 1)
				return "an hour ago";
			if(hours > 1)
				return hours + " hours ago";
			if(minutes == 1)
				return "a minute ago";
			if(minutes > 1)
				return minutes + " minutes ago";
			if(seconds >= 0)
				return "seconds ago";
		}
		
		return "I have no idea then this occured";
	}
	
}
