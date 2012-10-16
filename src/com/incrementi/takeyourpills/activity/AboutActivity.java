package com.incrementi.takeyourpills.activity;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.incrementi.takeyourpills.R;

public class AboutActivity extends SherlockActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.about);
		setContentView(R.layout.activity_about);
	}
}
