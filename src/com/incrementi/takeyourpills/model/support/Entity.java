package com.incrementi.takeyourpills.model.support;

import android.content.ContentValues;

public interface Entity {
	
	public long getId();
	
	public String getTableName();
	
	public ContentValues toContentValues();
}
