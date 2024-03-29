package com.incrementi.takeyourpills.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.incrementi.takeyourpills.TakeYourPillsApp;
import com.incrementi.takeyourpills.R;
import com.incrementi.takeyourpills.model.Speech;
import com.incrementi.takeyourpills.repository.support.Repository;
import com.incrementi.takeyourpills.support.DatabaseHelper;

public class SpeechExpandableAdapter extends BaseExpandableListAdapter {
	
	private Calendar[] groups;
	private List<List<Speech>> childs;
	private Activity activity;
	private LayoutInflater inflater;
	private TakeYourPillsApp app;

	public SpeechExpandableAdapter(Activity activity, 
			Calendar[] groups, List<Speech> childs) {
		this.groups = groups;
		this.activity = activity;
		this.app = (TakeYourPillsApp) activity.getApplication();
		this.childs = new ArrayList<List<Speech>>(groups.length);

		for(int i = 0; i < groups.length; i++) {
			this.childs.add(new ArrayList<Speech>());
		}
		
		for(Speech speech : childs) {
			for(int i = 0; i < groups.length; i++) {
				if(speech.occursInDate(groups[i])) {
					this.childs.get(i).add(speech);
				}
			}
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.childs.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return this.childs.get(groupPosition).get(childPosition).getId();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		Speech speech = this.childs.get(groupPosition).get(childPosition);
		
		if (speech.getType().equals("OPENING")) {
			inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.opening_list_item, parent, false);
		} else {
			inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.speech_list_item, parent, false);
		}
		TextView title = (TextView) convertView.findViewById(R.id.title);
		title.setText(speech.getTitle());
		
		TextView time = (TextView) convertView.findViewById(R.id.time);
		time.setText(speech.getTime());
		
		if (!speech.getType().equals("OPENING")) {
		
			TextView speaker = (TextView) convertView.findViewById(R.id.speaker);
			if (speech.getSpeaker() != null) {
				speaker.setText(speech.getSpeaker().replace(",", "\n"));
			} else {
				speaker.setText("");
			}
			
			TextView space = (TextView) convertView.findViewById(R.id.space);
			space.setText(speech.getSpace());
	
			CheckBox watch = (CheckBox) convertView.findViewById(R.id.watch);
			watch.setChecked(speech.isWatch());
			watch.setTag(speech);
			watch.setOnClickListener(onClickListener);
		}
		
		return convertView;
	}
	
	OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CheckBox watch = (CheckBox) v;
			Speech speech = (Speech) watch.getTag();
			
			Repository repository = new Repository(new DatabaseHelper(activity));
			
			if(watch.isChecked()) {
				for(Speech s : app.getSpeechsChecked()) {
					if(speech.occursInDateTime(s.getDateTime())) {
						s.setWatch(false);
						repository.update(s);
					}
				}
			}
			
			speech.setWatch(watch.isChecked());
			repository.update(speech);
			repository.close();
			notifyDataSetChanged();
		}
	};
	
	@Override
	public int getChildrenCount(int groupPosition) {
		return this.childs.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return groups.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if(convertView == null) {
			inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
		}
		TextView group = (TextView) convertView.findViewById(android.R.id.text1);
		group.setText(DateFormat.format("'Dia' dd", groups[groupPosition]));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
}
