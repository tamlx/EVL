package com.thanhle.englishvocabulary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thanhle.englishvocabulary.R;

public class CustomActionBar extends RelativeLayout implements OnClickListener {
	private TextView tvTitle;
	private CharSequence title;

	public CustomActionBar(Context context) {
		super(context);
		init();
	}

	public CustomActionBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.layout_action_bar, this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(title);
	}

	public void setTitle(CharSequence title) {
		this.title = title;
		if (tvTitle != null) {
			tvTitle.setText(title);
		}
	}

	@Override
	public void onClick(View v) {
	}
}
