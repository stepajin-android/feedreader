package cz.cvut.stepajin.feedreader.update;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class RefreshService extends Service {

    static boolean DEBUG = true;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("RefreshService", "StartCommand");

		RefreshTask task = new RefreshTask(this);
        task.execute();
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void schedule(Context context) {
        Log.d("RefreshService", "Shedule");

        Intent intent = new Intent(context, RefreshService.class);
		PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 14);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		final AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pending);

		if (DEBUG) {
			alarm.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime() + 30 * 1000, 30 * 1000, pending);
		} else {
			alarm.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
					AlarmManager.INTERVAL_HALF_DAY, pending);
		}
	}
}
