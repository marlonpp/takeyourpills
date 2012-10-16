package com.incrementi.takeyourpills;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.incrementi.takeyourpills.R;
import com.incrementi.takeyourpills.activity.AboutActivity;
import com.incrementi.takeyourpills.listener.LatinowareTabListener;
import com.incrementi.takeyourpills.listener.LatinowareTabListener.Tag;
import com.incrementi.takeyourpills.model.Course;
import com.incrementi.takeyourpills.model.Speech;
import com.incrementi.takeyourpills.repository.CourseRepository;
import com.incrementi.takeyourpills.repository.SpeechRepository;
import com.incrementi.takeyourpills.support.DatabaseHelper;
import com.incrementi.takeyourpills.task.FetchSpeechs;

public class Latinoware extends SherlockFragmentActivity implements Runnable {
	
	private static final String PREFERENCES = "LatinowarePrefs";
	
	private SharedPreferences sharedPreferences;

	private TakeYourPillsApp app;

	private int selectedTab = 0;
	
	boolean dataLoadedFromServer = false;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setSupportProgressBarIndeterminateVisibility(true);
		setSupportProgressBarIndeterminate(true);
		setSupportProgressBarVisibility(true);
		setContentView(R.layout.activity_main);
		initParams(savedInstanceState);
		
		if(dataLoadedFromServer) {
			if(app.speechsIsEmpty()) {
				loadingDataFromDatabase();
			}
			createTabs(selectedTab);
		} else {
			new FetchSpeechs(this).execute();
		}
	}

	private void initParams(Bundle savedState) {
		handler = new Handler();
		app = (TakeYourPillsApp) getApplication();
		sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
		dataLoadedFromServer = sharedPreferences.getBoolean("success", false);
		if(savedState != null) {
			selectedTab = savedState.getInt("selectedTab");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		int index = getSupportActionBar().getSelectedNavigationIndex();
		outState.putInt("selectedTab", index);
	}

	private void createTabs(int selectedTab) {

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		TabListener tabListener = new LatinowareTabListener(this);

		Tab tab1 = getSupportActionBar().newTab().setTag(Tag.SPEECHS)
				.setText(getString(R.string.palestras))
				.setTabListener(tabListener);
		getSupportActionBar().addTab(tab1, false);

		Tab tab2 = getSupportActionBar().newTab().setTag(Tag.MY_GRID)
				.setText(getString(R.string.minha_grade))
				.setTabListener(tabListener);
		getSupportActionBar().addTab(tab2, false);

		Tab tab3 = getSupportActionBar().newTab().setTag(Tag.COURSES)
				.setText(getString(R.string.cursos))
				.setTabListener(tabListener);
		getSupportActionBar().addTab(tab3, false);

		getSupportActionBar().getTabAt(selectedTab).select();

		setSupportProgressBarIndeterminateVisibility(false);
		setSupportProgressBarVisibility(false);
	}

	public Handler getHandler() {
		return handler;
	}
	
	public boolean isDataLoaded() {
		return dataLoadedFromServer;
	}
	
	public void setDataLoadedFromServer(boolean dataLoadedFromServer) {
		this.dataLoadedFromServer = dataLoadedFromServer;
		Editor edit = getSharedPreferences().edit();
		edit.putBoolean("success", dataLoadedFromServer);
		edit.commit();
	}
	
	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
	
	private void loadingDataFromDatabase() {
		DatabaseHelper databaseHelper = new DatabaseHelper(this);
		
		SpeechRepository speechRepository = new SpeechRepository(databaseHelper);
		List<Speech> speechs = speechRepository.getAll();
		speechRepository.close();
		app.setSpeechs(speechs);
		
		CourseRepository courseRepository = new CourseRepository(databaseHelper);
		List<Course> courses = courseRepository.getAll();
		courseRepository.close();
		app.setCourses(courses);
	}

	@Override
	public void run() {
		if(dataLoadedFromServer) {
			Toast.makeText(this, "Os dados foram carregados com sucesso.", 
					Toast.LENGTH_LONG).show();
		}
		loadingDataFromDatabase();
		createTabs(selectedTab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.latinoware, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_about) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
	
}