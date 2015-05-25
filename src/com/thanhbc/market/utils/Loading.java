package com.thanhbc.market.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.thanhbc.market.R;

public class Loading {
	public static void showLoading(ImageView loadingImage,Context context) {
		try {
			BitmapDrawable frame1 = (BitmapDrawable) context.getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo1);
			BitmapDrawable frame2 = (BitmapDrawable) context.getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo2);
			BitmapDrawable frame3 = (BitmapDrawable) context.getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo3);
			BitmapDrawable frame4 = (BitmapDrawable) context.getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo4);
			BitmapDrawable frame5 = (BitmapDrawable) context.getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo5);
			BitmapDrawable frame6 = (BitmapDrawable) context.getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo6);
			BitmapDrawable frame7 = (BitmapDrawable) context.getResources()
					.getDrawable(
							R.drawable.apptheme_progressbar_indeterminate_holo7);

			AnimationDrawable Anim = new AnimationDrawable();
			Anim.addFrame(frame1, 100);
			Anim.addFrame(frame2, 100);
			Anim.addFrame(frame3, 100);
			Anim.addFrame(frame4, 100);
			Anim.addFrame(frame5, 100);
			Anim.addFrame(frame6, 100);
			Anim.setOneShot(false);
			loadingImage.setBackgroundDrawable(Anim);
			 Anim.start();
		} catch (Exception e) {

		}
	}


}
