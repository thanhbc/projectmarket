package com.thanhbc.libs.imageview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


/**
 * 
 * @author HuynhPhuc
 * 
 * Lá»›p thiáº¿t láº­p trÃ¬nh mÃ£ hÃ³a dá»¯ liá»‡u áº£nh.
 * Khi thá»±c hiá»‡n xong phÆ°Æ¡ng thá»©c handleDecodeState sáº½ Ä‘Æ°á»£c gá»�i Ä‘á»ƒ
 * bÃ¡o cÃ¡o tÃ¬nh tráº¡ng.
 * 
 * CÃ¡c Ä‘á»‘i tÆ°á»£ng cá»§a lá»›p Ä‘Æ°á»£c khá»Ÿi táº¡o vÃ  quáº£n lÃ½ thÃ´ng qua cÃ¡c thá»±c thá»ƒ PhotoTask,
 * do lá»›p Phototask gá»�i thá»±c thi Interface {@link TaskRunnableDecodeMethods}.
 * 
 * CÃ¡c Ä‘á»‘i tÆ°á»£ng PhotoTask khi gá»�i PhotoDecodeRunnable sáº½ tá»± Ä‘á»™ng truyá»�n vÃ o tham sá»‘
 * báº±ng chÃ­nh Ä‘á»‘i tÆ°á»£ng Ä‘Ã³. Ä�á»ƒ tÄƒng tÃ­nh hiá»‡u quáº£, má»™t Ä‘á»‘i tÆ°á»£ng PhotoTask lÃ m viá»‡c vá»›i
 * má»™t Ä‘á»‘i tÆ°á»£ng PhotoDecodeRunnable sáº½ thÃ´ng qua cÃ¡c trÆ°á»�ng dá»¯ liá»‡u cá»§a lá»›p PhotoTask.
 *
 */
public class PhotoDecodeRunnable implements Runnable {
	
	// Biáº¿n qui Ä‘á»‹nh sá»‘ láº§n mÃ£ hÃ³a
	private static final int NUMBER_OF_DECODE_TRIES = 2;
	
	// Biáº¿n qui Ä‘á»‹nh thá»�i gian ngá»«ng giá»¯a má»—i láº§n mÃ£ hÃ³a (Ä�Æ¡n vá»‹: mili giÃ¢y)
	private static final long SLEEP_TIME_MILISECONDS = 250;
	
	// Tag dÃ¹ng Ä‘á»ƒ ghi LOG
	private static final String LOG_TAG = "PhotoDecodeRunnable";
	
	// Biáº¿n qui Ä‘á»‹nh tráº¡ng thÃ¡i mÃ£ hÃ³a
	static final int DECODE_STATE_FAILED = -1;
    static final int DECODE_STATE_STARTED = 0;
    static final int DECODE_STATE_COMPLETED = 1;
    
    // Ä�á»‹nh nghÄ©a trÆ°á»�ng dá»¯ liá»‡u gá»�i thá»±c thi lá»›p tá»« PhotoTask
    final TaskRunnableDecodeMethods mPhotoTask;
    
    /**
	 * 
	 * @author HuynhPhuc
	 * 
	 * Interfae cho phÃ©p Ä‘á»‹nh nghÄ©a cÃ¡c phÆ°Æ¡ng thá»©c Ä‘Æ°á»£c dÃ¹ng  trong lá»›p PhotoTask.
	 * ChÃ­nh Ä‘á»‘i tÆ°á»£ng PhotoTask sáº½ Ä‘Æ°á»£c truyá»�n vÃ o Interface thÃ´ng qua phÆ°Æ¡ng thá»©c
	 * khá»Ÿi táº¡o, do Ä‘Ã³ má»—i Ä‘á»‘i tÆ°á»£ng cÃ³ thá»ƒ tá»± truy xuáº¥t vÃ o Interface vÃ  quáº£n lÃ½
	 * dá»¯ liá»‡u cá»§a chÃ­nh nÃ³.
	 */
    interface TaskRunnableDecodeMethods {
    	
    	/**
		 * Thiáº¿t láº­p tiáº¿n trÃ¬nh Ä‘á»‘i tÆ°á»£ng sáº½ thá»±c thi
		 * @param currentThread
		 */
		void setImageDecodeThread(Thread currentThread);
		
		/**
		 * Tráº£ vá»� dá»¯ liá»‡u cá»§a ná»™i dung táº£i
		 * @return Dá»¯ liá»‡u byte cuá»‘i cÃ¹ng Ä‘Æ°á»£c táº£i vá»�
		 */
		byte[] getByteBuffer();
		
		
		/**
		 * Ä�á»‹nh nghÄ©a tráº¡ng thÃ¡i cho má»—i Ä‘á»‘i tÆ°á»£ng PhotoTask
		 * @param state Tráº¡ng thÃ¡i hiá»‡n thá»�i cá»§a tÃ¡c vá»¥
		 */
		void handleDecodeState(int state);
		
		/**
		 * Tráº£ vá»� Ä‘á»™ rá»™ng cá»§a áº£nh dá»±a trÃªn Ä‘á»‘i tÆ°á»£ng ImageView Ä‘Æ°á»£c táº¡o
		 * @return Ä�á»™ rá»™ng cá»§a áº£nh
		 */
		int getTargetWidth();
		
		/**
		 * Tráº£ vá»� Ä‘á»™ cao cá»§a áº£nh dá»±a trÃªn Ä‘á»‘i tÆ°á»£ng ImageView Ä‘Æ°á»£c táº¡o
		 * @return Ä�á»™ cao cá»§a áº£nh
		 */
		int getTargetHeight();
    	
		/**
		 * Thiáº¿t láº­p Ä‘á»‘i tÆ°á»£ng Bitmap cho ImageView
		 * @param Ä�á»‘i tÆ°á»£ng Bitmap
		 */   	
		void setImage(Bitmap image);
    }
    
    /**
	 * PhÆ°Æ¡ng thá»©c khá»Ÿi táº¡o cho phÃ©p táº¡o Ä‘á»‘i tÆ°á»£ng PhotoDecodeRunnable
	 * vÃ  lÆ°u trá»¯ chÃºng vÃ o tham chiáº¿u cá»§a Ä‘á»‘i tÆ°á»£ng PhotoTask gá»�i phÆ°Æ¡ng thá»©c
	 * khá»Ÿi táº¡o nÃ y.
	 * @param photoTask
	 */
    public PhotoDecodeRunnable(TaskRunnableDecodeMethods decodeTask) {
		mPhotoTask = decodeTask;
	}

	@Override
	public void run() {
		
		// LÆ°u trá»¯ láº¡i tiáº¿n trÃ¬nh hiá»‡n táº¡i vÃ o Ä‘á»‘i tÆ°á»£ng PhotoTask
		mPhotoTask.setImageDecodeThread(Thread.currentThread());
		
		/*
		 * Truy xuáº¥t bá»™ nhá»› Ä‘á»‡m tá»« Ä‘á»‘i tÆ°á»£ng PhotoTask dÃ¹ng chung cho cáº£
		 * PhotoDownloadRunnable vÃ  PhotoTask
		 */
		byte[] imageBuffer = mPhotoTask.getByteBuffer();
		
		// Khai bÃ¡o Ä‘á»‘i tÆ°á»£ng Bitmap sá»­ dá»¥ng trong tiáº¿n trÃ¬nh
		Bitmap returnBitmap = null;
		
		// Tiáº¿n hÃ nh mÃ£ hÃ³a áº£nh Ä‘Ã£ táº£i
		try {
			
			// Thiáº¿t láº­p tráº¡ng thÃ¡i mÃ£ hÃ³a
			mPhotoTask.handleDecodeState(DECODE_STATE_STARTED);
			
			// Thiáº¿t láº­p thÃ´ng sá»‘ táº¡o Ä‘á»‘i tÆ°á»£ng Bitmap
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			
			// Truy xuáº¥t thÃ´ng sá»‘ Ä‘á»™ rá»™ng vÃ  Ä‘á»™ cao tá»« Ä‘á»‘i tÆ°á»£ng ImageView
			int targetWidth = mPhotoTask.getTargetWidth();
			int targetHeight = mPhotoTask.getTargetHeight();
			
			// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
			if (Thread.interrupted()) {
				return;
			}
			
			/*
			 *  Báº¥t cháº¥p bá»™ mÃ£ hÃ³a cÃ³ thiáº¿t láº­p Ä‘Æ°á»£c Ä‘á»‘i tÆ°á»£ng Bitmap
			 *  hay khÃ´ng, váº«n báº­t cá»� bÃ¡o tráº£ vá»� diá»‡n tÃ­ch Ä‘Ã£ táº¡o
			 */
			bitmapOptions.inJustDecodeBounds = true;
			
			/*
			 * Láº§n 1:
			 * Truyá»�n táº¥t cáº£ thÃ´ng sá»‘ vá»� tá»‰ lá»‡ vÃ  thÃ´ng sá»‘ máº«u
			 */
			BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length, bitmapOptions);
			
			/*
			 * Thiáº¿t láº­p thÃ´ng sá»‘ co dÃ£n theo chiá»�u dá»�c vÃ  chiá»�u ngang
			 * cho Ä‘á»‘i tÆ°á»£ng áº£nh sá»­ dá»¥ng trong trÆ°á»�ng há»£p áº£nh cÃ³ thá»ƒ bá»‹
			 * kÃ©o dÃ£n hoáº·c nÃ©n láº¡i so vá»›i kÃ­ch thÆ°á»›c tháº­t cho phá»³ há»£p
			 * vá»›i kÃ­ch thÆ°á»›c cá»§a Ä‘á»‘i tÆ°á»£ng ImageView
			 */
			int hScale = bitmapOptions.outHeight/ targetHeight;
			int wScale = bitmapOptions.outWidth/ targetWidth;
			
			/*
			 * Thiáº¿t láº­p kÃ­ch thÆ°á»›c máº«u so vá»›i tá»‰ lá»‡ co dÃ£n xÃ©t trÃªn
			 * chiá»�u dá»�c hay chiá»�u ngang lá»›n hÆ¡n.
			 */
			int sampleSize = Math.max(hScale, wScale);
			
			/*
			 * Náº¿u má»™t trong hai tá»‰ lá»‡ trÃªn (hScale & wScale) lá»›n hÆ¡n 1, 
			 * suy ra kÃ­ch thÆ°á»›c tháº­t sá»± cá»§a áº£nh lá»›n hÆ¡n kÃ­ch thÆ°á»›c hiá»‡n cÃ³.
			 * Do Ä‘Ã³ BitmapFactory pháº£i nÃ©n áº£nh dá»±a trÃªn tá»‰ lá»‡ cao hÆ¡n 
			 * (hScale hoáº·c wScale), dÃ¹ng tham sá»‘ inSampleSize Ä‘á»ƒ thá»±c hiá»‡n.
			 */
			if (sampleSize > 1) {
				bitmapOptions.inSampleSize = sampleSize;
			}
			
			// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
			if (Thread.interrupted()) {
				return;
			}
			
			/*
			 *  Láº§n 2: náº¿u Bitmap khÃ´ng Ä‘Æ°á»£c táº¡o, 
			 *  => khÃ´ng thiáº¿t láº­p dá»¯ liá»‡u trong Bitmap
			 */
			bitmapOptions.inJustDecodeBounds = false;
			
			/*
			 * Thá»±c hiá»‡n mÃ£ hÃ³a trÃªn bá»™ Ä‘á»‡m.
			 * Náº¿u bá»™ mÃ£ hÃ³a gáº·p tÃ¬nh tráº¡ng trÃ n bá»™ nhá»›,
			 * báº¯t láº¡i Exception hoáº·c Error cÃ³ thá»ƒ cÃ³ Ä‘á»ƒ thá»­
			 * mÃ£ hÃ³a láº¡i
			 */
			for (int i=0; i < NUMBER_OF_DECODE_TRIES; i++) {
				try {
					returnBitmap = BitmapFactory.decodeByteArray(imageBuffer, 0, imageBuffer.length, bitmapOptions);
					
					/*
					 * Náº¿u mÃ£ hÃ³a Ä‘Æ°á»£c ngá»«ng vÃ²ng láº·p
					 * Náº¿u khÃ´ng tiáº¿n hÃ nh yÃªu cáº§u thÃªm bá»™ nhá»›
					 */
				} catch (Throwable e) {
					// Tiáº¿n hÃ nh ghi LOG náº¿u lá»—i
					Log.e(LOG_TAG, "Lá»—i trÃ n bá»™ nhá»› trong quÃ¡ trÃ¬nh mÃ£ hÃ³a áº£nh");
					
					// Tiáº¿n hÃ nh dá»�n dáº¹p bá»™ nhá»› náº¿u cáº§n thiáº¿t
					java.lang.System.gc();
					
					// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
					if (Thread.interrupted()) {
						return;
					}
					
					/*
					 * Táº¡m dá»«ng tiáº¿n trÃ¬nh 1/4 giÃ¢y vÃ  báº¯t láº¡i ngoáº¡i lá»‡
					 */
					try {
						Thread.sleep(SLEEP_TIME_MILISECONDS);
					} catch (InterruptedException interruptedException) {
						return;
					}
				}
			}
			
			// Tiáº¿n trÃ¬nh bá»‹ lá»—i trong quÃ¡ trÃ¬nh hoáº¡t Ä‘á»™ng
			// Báº¯t vÃ  kiá»ƒm tra ngoáº¡i lá»‡
		} finally {
			// QuÃ¡ trÃ¬nh mÃ£ hÃ³a tháº¥t báº¡i
			if(null == returnBitmap) {
				
				// BÃ¡o cÃ¡o lá»—i trong quÃ¡ trÃ¬nh xá»­ lÃ½
				mPhotoTask.handleDecodeState(DECODE_STATE_FAILED);
				
				// Tiáº¿n hÃ nh ghi LOG
				Log.e(LOG_TAG, "QuÃ¡ trÃ¬nh xá»­ lÃ½ áº£nh tháº¥t báº¡i");
			} else {
				
				// Thiáº¿t láº­p áº£nh cho ImageView
				mPhotoTask.setImage(returnBitmap);
				
				// BÃ¡o cÃ¡o hoÃ n táº¥t quÃ¡ trÃ¬nh mÃ£ hÃ³a
				mPhotoTask.handleDecodeState(DECODE_STATE_COMPLETED);
				
				// HÃ¹y tiáº¿n trÃ¬nh Ä‘ang cháº¡y, xÃ³a bá»™ nhá»›
				mPhotoTask.setImageDecodeThread(null);
				
				// XÃ³a biáº¿n cá»� báº¯t ngoáº¡i lá»‡
				Thread.interrupted();
			}
		}
		
		
	}

}
