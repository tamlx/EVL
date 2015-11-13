package com.thanhle.englishvocabulary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * @author thanh.lecaominh
 * 
 */
public class FlipView extends FrameLayout {
	public static final int INTERPOLATOR_LINEAR = 1;
	public static final int INTERPOLATOR_NONLINEAR = 2;
	public static final int DIRECTION_HORIZONTAL = 1;
	public static final int DIRECTION_VERTICAL = 2;
	public static final int PIVOT_CENTER = 1;
	public static final int PIVOT_LEFT = 2;
	public static final int PIVOT_RIGHT = 3;
	public static final int PIVOT_TOP = 2;
	public static final int PIVOT_BOTTOM = 3;

	protected boolean m_IsFrontFacing = true;
	protected boolean m_IsAnimating = false;

	private int m_InterpolatorType = INTERPOLATOR_LINEAR;
	private int m_Direction = DIRECTION_HORIZONTAL;
	private int m_Pivot = PIVOT_CENTER;

	protected long m_HalfAnimationDuration = 500;

	protected RelativeLayout m_FrontFace;
	protected RelativeLayout m_BackFace;
	protected AnimationListener mListener;

	public FlipView(Context context) {
		super(context);
	}

	public FlipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlipView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlipView(Context context, RelativeLayout frontFace,
			RelativeLayout backFace) {
		super(context);

		setFrontFace(frontFace);
		setBackFace(backFace);
	}

	public void setAnimationListener(AnimationListener listener) {
		mListener = listener;
	}

	/**
	 * Set point rotation (center, left, right, top, bottom)
	 * 
	 * @param pvt
	 */
	public void setPivot(int pvt) {
		if (m_IsAnimating)
			return;

		if (pvt == PIVOT_CENTER || pvt == PIVOT_LEFT || pvt == PIVOT_RIGHT
				|| pvt == PIVOT_TOP || pvt == PIVOT_BOTTOM) {
			m_Pivot = pvt;
		} else
			m_Pivot = PIVOT_CENTER;
	}

	/**
	 * set front layout
	 * 
	 * @param frontFace
	 */
	public void setFrontFace(RelativeLayout frontFace) {
		if (m_IsAnimating)
			return;

		if (m_FrontFace != null)
			this.removeView(m_FrontFace);

		m_FrontFace = frontFace;

		if (m_FrontFace != null) {
			this.addView(m_FrontFace);

			if (!m_IsFrontFacing)
				m_FrontFace.setVisibility(View.GONE);
		}
	}

	public View getFrontFace() {
		return m_FrontFace;
	}

	/**
	 * set back layout
	 * 
	 * @param backFace
	 */
	public void setBackFace(RelativeLayout backFace) {
		if (m_IsAnimating)
			return;

		if (m_BackFace != null)
			this.removeView(m_BackFace);

		m_BackFace = backFace;

		if (m_BackFace != null) {
			this.addView(m_BackFace);

			if (m_IsFrontFacing)
				m_BackFace.setVisibility(View.GONE);
		}
	}

	public View getBackFace() {
		return m_BackFace;
	}

	public View getCurrentFaceView() {
		if (m_IsFrontFacing) {
			return getFrontFace();
		} else {
			return getBackFace();
		}
	}

	//
	public void setInterpolator(int interpolator) {
		if (interpolator == INTERPOLATOR_NONLINEAR)
			m_InterpolatorType = INTERPOLATOR_NONLINEAR;
		else
			m_InterpolatorType = INTERPOLATOR_LINEAR;
	}

	//
	public void setAnimationDuration(long milliseconds) {
		if (milliseconds > 20)
			m_HalfAnimationDuration = (long) milliseconds / 2;
	}

	//
	public void setDirection(int dir) {
		if (m_IsAnimating)
			return;

		if (dir == DIRECTION_VERTICAL)
			m_Direction = DIRECTION_VERTICAL;
		else
			m_Direction = DIRECTION_HORIZONTAL;
	}

	//
	public void flip() {
		if (m_IsAnimating || m_FrontFace == null || m_BackFace == null) {
			return;
		}

		if (m_IsFrontFacing)
			applyRotation(0, 90, m_HalfAnimationDuration);
		else
			applyRotation(0, -90, m_HalfAnimationDuration);

		m_IsFrontFacing = !m_IsFrontFacing;
	}

	protected void applyRotation(float start, float end, long duration) {
		m_IsAnimating = true;

		final float centerX = (m_Pivot == PIVOT_LEFT) ? 0
				: ((m_Pivot == PIVOT_CENTER) ? (m_FrontFace.getWidth() / 2.0f)
						: m_FrontFace.getWidth());
		final float centerY = (m_Pivot == PIVOT_LEFT) ? 0
				: ((m_Pivot == PIVOT_CENTER) ? (m_FrontFace.getHeight() / 2.0f)
						: m_FrontFace.getHeight());

		final FlipAnimation rotation = new FlipAnimation(start, end, centerX,
				centerY, ((m_Direction == DIRECTION_VERTICAL) ? false : true));
		rotation.setDuration(duration);
		rotation.setFillAfter(true);

		if (m_InterpolatorType == INTERPOLATOR_LINEAR)
			rotation.setInterpolator(new LinearInterpolator());
		else
			rotation.setInterpolator(new AccelerateInterpolator());

		rotation.setAnimationListener(new HalfWayListener(m_IsFrontFacing,
				duration));

		if (m_IsFrontFacing)
			m_FrontFace.startAnimation(rotation);
		else
			m_BackFace.startAnimation(rotation);
	}

	protected void runSecondAnimation(boolean frontFace, long duration) {

		FlipAnimation rotation;
		// final float centerX = m_FrontFace.getWidth() / 2.0f;
		// final float centerY = m_FrontFace.getHeight() / 2.0f;
		final float centerX = (m_Pivot == PIVOT_LEFT) ? 0
				: ((m_Pivot == PIVOT_CENTER) ? (m_FrontFace.getWidth() / 2.0f)
						: m_FrontFace.getWidth());
		final float centerY = (m_Pivot == PIVOT_LEFT) ? 0
				: ((m_Pivot == PIVOT_CENTER) ? (m_FrontFace.getHeight() / 2.0f)
						: m_FrontFace.getHeight());

		if (frontFace) {
			m_FrontFace.setVisibility(View.GONE);
			m_BackFace.setVisibility(View.VISIBLE);
			m_BackFace.requestFocus();
			rotation = new FlipAnimation(-90, 0, centerX, centerY,
					((m_Direction == DIRECTION_VERTICAL) ? false : true));
		} else {
			m_BackFace.setVisibility(View.GONE);
			m_FrontFace.setVisibility(View.VISIBLE);
			m_FrontFace.requestFocus();
			rotation = new FlipAnimation(90, 0, centerX, centerY,
					((m_Direction == DIRECTION_VERTICAL) ? false : true));
		}

		rotation.setDuration(duration);
		rotation.setFillAfter(true);

		if (m_InterpolatorType == INTERPOLATOR_LINEAR)
			rotation.setInterpolator(new LinearInterpolator());
		else
			rotation.setInterpolator(new DecelerateInterpolator());

		rotation.setAnimationListener(new EndListener());

		if (frontFace)
			m_BackFace.startAnimation(rotation);
		else
			m_FrontFace.startAnimation(rotation);
	}

	private final class HalfWayListener implements Animation.AnimationListener {
		private boolean m_FrontFace;
		private long m_Duration;

		public HalfWayListener(boolean frontFace, long duration) {
			m_FrontFace = frontFace;
			m_Duration = duration;
		}

		public void onAnimationStart(Animation animation) {
			if (mListener != null)
				mListener.onAnimationStart(animation);
		}

		public void onAnimationEnd(Animation animation) {
			runSecondAnimation(m_FrontFace, m_Duration);
		}

		public void onAnimationRepeat(Animation animation) {
			if (mListener != null)
				mListener.onAnimationRepeat(animation);
		}
	}

	private final class EndListener implements Animation.AnimationListener {

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			m_IsAnimating = false;
			if (mListener != null)
				mListener.onAnimationEnd(animation);
		}

		public void onAnimationRepeat(Animation animation) {
			if (mListener != null)
				mListener.onAnimationRepeat(animation);
		}
	}
}