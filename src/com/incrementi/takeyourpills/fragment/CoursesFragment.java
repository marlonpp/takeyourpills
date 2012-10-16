package com.incrementi.takeyourpills.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.incrementi.takeyourpills.TakeYourPillsApp;
import com.incrementi.takeyourpills.R;
import com.incrementi.takeyourpills.adapter.CourseExpandableAdapter;

public class CoursesFragment extends SherlockFragment {

	private TakeYourPillsApp app;
	private ExpandableListView list;
	private SherlockFragmentActivity activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.activity = getSherlockActivity();
		this.app = (TakeYourPillsApp) activity.getApplication();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab_fragment, container, false);
		
		list = (ExpandableListView) view.findViewById(android.R.id.list);
		
		list.setAdapter(new CourseExpandableAdapter(
				activity, app.getDaysOfEvent(), app.getCourses()));
		return view;
	}
}