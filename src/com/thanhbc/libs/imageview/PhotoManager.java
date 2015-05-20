package com.thanhbc.libs.imageview;

import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.thanhbc.market.R;

/**
 * 
 * @author HuynhPhuc
 * 
 * Lá»›p cho phÃ©p thá»±c hiá»‡n táº¡o bá»™ lÆ°u trá»¯ cÃ¡c tiáº¿n trÃ¬nh táº£i ngáº§m.
 * Viá»‡c thiáº¿t láº­p kÃ­ch thÆ°á»›c bá»™ lÆ°u trá»¯ vÃ  kÃ­ch thÆ°á»›c bá»™ nhá»› Cache dá»±a trÃªn
 * tá»«ng xá»­ lÃ½ riÃªng biá»‡t.
 * <p>
 * Thuáº­t toÃ¡n viáº¿t trong lá»›p nÃ y khÃ´ng bao quÃ¡t cho 
 * táº¥t cáº£ cÃ¡c trÆ°á»�ng há»£p, vÃ¬ tháº¿ khi sá»­ dá»¥ng tá»‘t nháº¥t nÃªn thá»±c hiá»‡n tÃ¹y chá»‰nh 
 * kÃ­ch thÆ°á»›c bá»™ lÆ°u trá»¯ cho phÃ¹ há»£p vá»›i á»©ng dá»¥ng. Trong nhiá»�u trÆ°á»�ng há»£p, ta 
 * sáº½ thiáº¿t láº­p má»™t kÃ­ch thÆ°á»›c cá»¥ thá»ƒ sau Ä‘Ã³ má»›i Ä‘o lÆ°á»�ng kÃ­ch thÆ°á»›c sá»­ dá»¥ng thá»±c táº¿. 
 * <p>
 * Thá»±c táº¿ trong lá»›p nÃ y ta xÃ¢y dá»±ng hai bá»™ lÆ°u trá»¯ Ä‘á»ƒ háº¡n cháº¿ sá»‘ lÆ°á»£ng
 * tiáº¿n trÃ¬nh hÃ¬nh áº£nh Ä‘Æ°á»£c mÃ£ hÃ³a cÃ¹ng lÃºc so vá»›i sá»‘ nhÃ¢n xá»­ lÃ½ trÃªn thiáº¿t bá»‹.
 * <p>
 * Trong lá»›p nÃ y cÅ©ng Ä‘á»‹nh nghÄ©a Ä‘á»‘i tÆ°á»£ng cho phÃ©p truyá»�n thÃ´ng Ä‘iá»‡p Ä‘áº¿n tiáº¿n
 * trÃ¬nh giao diá»‡n.
 */
public class PhotoManager {
	
	/*
	 * Biáº¿n Ä‘á»‹nh nghÄ©a tráº¡ng thÃ¡i
	 */
	static final int DOWNLOAD_FAILED = -1;
	static final int DOWNLOAD_STARTED = 1;
	static final int DOWNLOAD_COMPLETE = 2;
	static final int DECODE_STARTED = 3;
	static final int TASK_COMPLETE = 4;
	
	// Biáº¿n thiáº¿t láº­p kÃ­ch thÆ°á»›c bá»™ lÆ°u trá»¯ Cache
	private static final int IMAGE_CACHE_SIZE = 1024*1024*4;
	
	// Biáº¿n thiáº¿t láº­p thá»�i gian chá»� cho tiáº¿n trÃ¬nh trÆ°á»›c khi há»§y
	private static final int KEEP_ALIVE_TIME = 1;
	
	// Biáº¿n thiáº¿t láº­p Ä‘Æ¡n vá»‹ thá»�i gian theo giÃ¢y
	private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
	
	// Biáº¿n khá»Ÿi táº¡o kÃ­ch thÆ°á»›c lÆ°u trá»¯ tiáº¿n trÃ¬nh
	private static final int CORE_POOL_SIZE = 8;
	
	// Biáº¿n thiáº¿t láº­p kÃ­ch thÆ°á»›c lá»›n nháº¥t cÃ³ thá»ƒ lÆ°u trá»¯ tiáº¿n trÃ¬nh
	private static final int MAXIMUM_POOL_SIZE = 8;
	
	/**
	 * Biáº¿n ghi nháº­n sá»‘ lÆ°á»£ng bá»™ vi xá»­ lÃ½
	 * LÆ°u Ã½ trong cÃ¡c phiÃªn báº£n Android má»›i cÃ¡c thiáº¿t bá»‹ Ä‘Æ°á»£c trang bá»‹ nhÃ¢n
	 * xá»­ lÃ½ dáº¡ng plug-and-play, cÃ³ nghÄ©a lÃ  sá»‘ lÆ°á»£ng nhÃ¢n xá»­ lÃ½ tráº£ vá»� cÃ³
	 * thá»ƒ Ã­t hÆ¡n so vá»›i thá»±c táº¿. 
	 */
	private static int NUMBERS_OF_CORES = Runtime.getRuntime().availableProcessors();
	
	/*
	 * Táº¡o máº£ng byte táº¡m dá»±a trÃªn URL cá»§a áº£nh. Khi cÃ³ dá»¯ liá»‡u má»›i Ä‘Æ°á»£c thÃªm vÃ o
	 * dá»¯ liá»‡u cÅ© sáº½ Ä‘Æ°á»£c gá»¡ bá»� vÃ  dá»�n dáº¹p bá»™ nhá»›.
	 */	
	private final LruCache<URL, byte[]> mPhotoCache;
	
	// Biáº¿n hÃ ng Ä‘á»£i cÃ¡c Runnable lÆ°u trá»¯ ná»™i dung táº£i
	private final BlockingQueue<Runnable> mDownloadWorkQueue;
	
	// Biáº¿n hÃ ng Ä‘á»£i cÃ¡c Runnable lÆ°u trá»¯ ná»™i dung mÃ£ hÃ³a
	private final BlockingQueue<Runnable> mDecodeWorkQueue;	
	
	// Biáº¿n hÃ ng Ä‘á»£i cÃ¡c tÃ¡c vá»¥ PhotoManager. TÃ¡c vá»¥ bao gá»“m cÃ¡c bá»™ lÆ°u trá»¯ ThreadPool.
	private final Queue<PhotoTask> mPhotoTaskWorkQueue;
	
	// Biáº¿n quáº£n lÃ½ bá»™ lÆ°u trá»¯ cÃ¡c tiáº¿n trÃ¬nh táº£i ngáº§m
	private final ThreadPoolExecutor mDownloadThreadPool;
	
	// Biáº¿n quáº£n lÃ½ bá»™ lÆ°u trá»¯ cÃ¡c tiáº¿n trÃ¬nh mÃ£ hÃ³a ngáº§m
	private final ThreadPoolExecutor mDecodeThreadPool;
	
	// Ä�á»‘i tÆ°á»£ng quáº£n lÃ½ cÃ¡c Message trong má»™t tiáº¿n trÃ¬nh
	private Handler mHandler;
	
	// Táº¡o Ä‘á»‘i tÆ°á»£ng thá»±c thá»ƒ lá»›p PhotoManager, dÃ¹ng cÃ¡c lá»›p khÃ¡c khá»Ÿi táº¡o.
	private static PhotoManager sInstance = null;
	
	// Khá»Ÿi táº¡o biáº¿n trong lá»›p
	static {
		
		// Thiáº¿t láº­p Ä‘Æ¡n vá»‹ thá»�i theo giÃ¢y
		KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
		
		// Khá»Ÿi táº¡o Ä‘á»‘i tÆ°á»£ng PhotoManager
		sInstance = new PhotoManager();
	}
	
	/**
	 *  Khá»Ÿi táº¡o hÃ ng Ä‘á»£i vÃ  cÃ¡c bá»™ lÆ°u trá»¯ tiáº¿n trÃ¬nh cho viá»‡c táº£i vÃ  mÃ£ hÃ³a hÃ¬nh áº£nh
	 */
	@SuppressLint("HandlerLeak") private PhotoManager() {
		
		/*
		 * Khá»Ÿi táº¡o hÃ ng Ä‘á»£i cho cÃ¡c bá»™ lÆ°u trá»¯ Ä‘á»‘i tÆ°á»£ng tiáº¿n trÃ¬nh Ä‘Æ°á»£c sá»­ dá»¥ng trong viá»‡c táº£i
		 * dá»¯ liá»‡u báº±ng cÃ¡ch sá»­ dá»¥ng liÃªn káº¿t Ä‘áº¿n danh sÃ¡ch hÃ ng Ä‘á»£i, danh sÃ¡ch nÃ y sáº½ bá»‹ khÃ³a
		 * khi hÃ ng Ä‘á»£i rá»—ng.
		 */
		mDownloadWorkQueue = new LinkedBlockingDeque<Runnable>();
		
		/*
		 * Khá»Ÿi táº¡o hÃ ng Ä‘á»£i cho cÃ¡c bá»™ lÆ°u trá»¯ Ä‘á»‘i tÆ°á»£ng tiáº¿n trÃ¬nh Ä‘Æ°á»£c sá»­ dá»¥ng trong viá»‡c mÃ£ hÃ³a
		 * dá»¯ liá»‡u báº±ng cÃ¡ch sá»­ dá»¥ng liÃªn káº¿t Ä‘áº¿n danh sÃ¡ch hÃ ng Ä‘á»£i, danh sÃ¡ch nÃ y sáº½ bá»‹ khÃ³a
		 * khi hÃ ng Ä‘á»£i rá»—ng.
		 */
		mDecodeWorkQueue = new LinkedBlockingDeque<Runnable>();
		
		/*
		 * Khá»Ÿi táº¡o hÃ ng Ä‘á»£i cho táº­p Ä‘á»‘i tÆ°á»£ng tÃ¡c vá»¥ Ä‘Æ°á»£c sá»­ dá»¥ng trong viá»‡c quáº£n lÃ½
		 * táº£i vÃ  mÃ£ hÃ³a dá»¯ liá»‡u báº±ng cÃ¡ch sá»­ dá»¥ng liÃªn káº¿t Ä‘áº¿n danh sÃ¡ch hÃ ng Ä‘á»£i, danh sÃ¡ch nÃ y sáº½ bá»‹ khÃ³a
		 * khi hÃ ng Ä‘á»£i rá»—ng.
		 */
		mPhotoTaskWorkQueue = new LinkedBlockingDeque<PhotoTask>();
		
		/*
		 * Khá»Ÿi táº¡o bá»™ lÆ°u trá»¯ cho Ä‘á»‘i tÆ°á»£ng tiáº¿n trÃ¬nh sá»­ dá»¥ng cho hÃ ng Ä‘á»£i cÃ¡c ná»™i dung táº£i.
		 */
		mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 
				KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);
		
		/*
		 * Khá»Ÿi táº¡o bá»™ lÆ°u trá»¯ cho Ä‘á»‘i tÆ°á»£ng tiáº¿n trÃ¬nh sá»­ dá»¥ng cho hÃ ng Ä‘á»£i cÃ¡c ná»™i dung mÃ£ hÃ³a.
		 */
		mDecodeThreadPool = new ThreadPoolExecutor(NUMBERS_OF_CORES, NUMBERS_OF_CORES, 
				KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);
		
		// Khá»Ÿi táº¡o bá»™ lÆ°u trá»¯ Cache Æ°á»›c lÆ°á»£ng
		mPhotoCache = new LruCache<URL, byte[]>(IMAGE_CACHE_SIZE) {
			
			protected int sizeOf(URL paramURL, byte[] paramArrayOfByte) {
				return paramArrayOfByte.length;
			}
		};
		
		/*
		 * Khá»Ÿi táº¡o Ä‘á»‘i tÆ°á»£ng quáº£n lÃ½ vÃ  Ä‘á»‹nh nghÄ©a phÆ°Æ¡ng thá»©c quáº£n lÃ½.
		 * Ä�á»‘i tÆ°á»£ng Handler pháº£i cháº¡y trÃªn tiáº¿n trÃ¬nh chÃ­nh (UI) Ä‘á»ƒ thá»±c hiá»‡n
		 * thao tÃ¡c chuyá»ƒn Ä‘á»‘i tÆ°á»£ng Bitmap lÃªn Ä‘á»‘i tÆ°á»£ng View.
		 */
		mHandler = new Handler(Looper.getMainLooper()) {
			
			public void handleMessage(android.os.Message inputMessage) {
				
				// Truy xuáº¥t Ä‘á»‘i tÆ°á»£ng PhotoTask tá»« Message Ä‘Æ°á»£c gá»­i Ä‘áº¿n
				PhotoTask photoTask = (PhotoTask)inputMessage.obj;
				
				// Thiáº¿t láº­p PhotoView tham chiáº¿u "má»�m" Ä‘áº¿n Ä‘á»‘i tÆ°á»£ng ImageView
				PhotoView localView = photoTask.getPhotoView();
				
				// Náº¿u Ä‘á»‘i tÆ°á»£ng View truyá»�n vÃ o Ä‘Ã£ khá»Ÿi táº¡o
				if (localView != null) {
					
					/*
					 * Truy xuáº¥t URL tá»« tham chiáº¿u "má»�m" Ä‘Ã£ gÃ¡n cho ImageView.
					 * Tham chiáº¿u nÃ y sáº½ khÃ´ng thay Ä‘á»•i cho dÃ¹ ImageView cÃ³ bá»‹ thay Ä‘á»•i.
					 */
					URL localUrl = localView.getLocation();
					
					/*
					 * So sÃ¡nh URL cá»§a Imageview vÃ  URL truy xuáº¥t tá»« tham chiáº¿u "má»�m".
					 * Chá»‰ cáº­p nháº­t dá»¯ liá»‡u áº£nh cho ImageView náº¿u tiáº¿n trÃ¬nh thá»±c hiá»‡n
					 * trÃªn tá»«ng ImageView.
					 */
					if (photoTask.getImageURL() == localUrl) 
						
						switch (inputMessage.what) {
						// Náº¿u quÃ¡ trÃ¬nh táº£i Ä‘Æ°á»£c gá»�i, thiáº¿t láº­p mÃ u ná»�n Xanh LÃ¡ CÃ¢y/ Empty_View
						case DOWNLOAD_STARTED:
							localView.setStatusResource(R.drawable.empty_photo);
							break;
							
						/*
						 * Náº¿u quÃ¡ trÃ¬nh táº£i hoÃ n thÃ nh nhÆ°ng váº«n pháº£i Ä‘á»£i mÃ£ hÃ³a
						 * Thiáº¿t láº­p mÃ u ná»�n VÃ ng
						 */
						case DOWNLOAD_COMPLETE:
							localView.setStatusResource(R.drawable.empty_photo);
							break;
						
						// Báº¯t Ä‘áº§u tiáº¿n trÃ¬nh mÃ£ hÃ³a, thiáº¿t láº­p mÃ u Cam
						case DECODE_STARTED:
							localView.setStatusResource(R.drawable.empty_photo);
							break;
							
						/*
						 * HoÃ n táº¥t quÃ¡ trÃ¬nh mÃ£ hÃ³a
						 * Tiáº¿n hÃ nh thiáº¿t láº­p Ä‘á»‘i tÆ°á»£ng Bitmap trong Message Ä‘Æ°á»£c gá»­i tá»›i
						 */
						case TASK_COMPLETE:
							localView.setImageBitmap(photoTask.getImage());
							recycleTask(photoTask);
							break;
							
						// QuÃ¡ trÃ¬nh táº£i tháº¥t báº¡i, thiáº¿t láº­p mÃ u ná»�n Ä�EN
						case DOWNLOAD_FAILED:
							localView.setStatusResource(R.drawable.imagedownloadfailed);
							
							// TÃ¡i sá»­ Ä‘á»‘i tÆ°á»£ng tÃ¡c vá»¥
							recycleTask(photoTask);
							break;

						default:
							// Gá»�i thá»±c thi phÆ°Æ¡ng thá»©c á»Ÿ lá»›p cha
							super.handleMessage(inputMessage);
						}
					
				}
				
			}
		};
	}
	
	/**
	 * PhÆ°Æ¡ng thá»©c truy xuáº¥t Ä‘á»‘i tÆ°á»£ng PhotoManager
	 * @return Ä�á»‘i tÆ°á»£ng PhotoManager toÃ n cá»¥c
	 */
	public static PhotoManager getInstance() {
		return sInstance;
	}
	
	/**
	 * Quáº£n lÃ½ cÃ¡c biáº¿n tráº¡ng thÃ¡i Ä‘Æ°á»£c gá»­i tá»« cÃ¡c tÃ¡c vá»¥ PhotoTask
	 * @param photoTask
	 * @param state
	 */
	public void handleState(PhotoTask photoTask, int state) {
		
		switch (state) {
		// HoÃ n thÃ nh viá»‡c táº£i vÃ  mÃ£ hÃ³a
		case TASK_COMPLETE:
			// LÆ°u tÃ¡c vá»¥ vÃ o Cache
			if(photoTask.isCacheEnabled()) {
				mPhotoCache.put(photoTask.getImageURL(), photoTask.getByteBuffer());
			}
			
			/*
			 * Truy xuáº¥t Ä‘á»‘i tÆ°á»£ng Message, 
			 * lÆ°u trá»¯ tráº¡ng thÃ¡i vÃ o Ä‘Ã³ vÃ  gá»­i láº¡i cho Handler
			 */
			Message completeMessage = mHandler.obtainMessage(state, photoTask);
			completeMessage.sendToTarget();			
			break;
		
		// HoÃ n thÃ nh viá»‡c táº£i dá»¯ liá»‡u áº£nh
		case DOWNLOAD_COMPLETE:
			/*
			 *  Tiáº¿n hÃ nh mÃ£ hÃ³a áº£nh
			 *  báº±ng cÃ¡ch gá»­i Ä‘á»‘i tÆ°á»£ng mÃ£ hÃ³a
			 *  vÃ o hÃ ng Ä‘á»£i vÃ  lÆ°u trá»¯ thÃ´ng tin tiáº¿n trÃ¬nh mÃ£ hÃ³a
			 */
			mDecodeThreadPool.execute(photoTask.getPhotoDecodeRunnable());
			break;
			
		// Trong cÃ¡c trÆ°á»�ng há»£p khÃ¡c, truyá»�n Message nhÆ°ng khÃ´ng cÃ³ hÃ nh Ä‘á»™ng
		default:
			mHandler.obtainMessage(state, photoTask).sendToTarget();
		}
	}
	
	/**
	 * ThoÃ¡t táº¥t cáº£ cÃ¡c tiáº¿n trÃ¬nh trong bá»™ lÆ°u trá»¯
	 */
	public static void cancleAll() {
		
		/*
		 * Táº¡o máº£ng cÃ¡c tÃ¡c vá»¥ báº±ng vá»›i sá»‘ lÆ°á»£ng trong hÃ ng Ä‘á»£i
		 */
		PhotoTask[] taskArray = new PhotoTask[sInstance.mDownloadWorkQueue.size()];
		
		// Gáº¯n dá»¯ liá»‡u máº£ng [cÃ¡c Ä‘á»‘i tÆ°á»£ng tÃ¡c vá»¥] vÃ o hÃ ng Ä‘á»£i
		sInstance.mDownloadWorkQueue.toArray(taskArray);
		
		// LÆ°u trá»¯ Ä‘á»™ dÃ i máº£ng cho vÃ²ng láº·p
		int taskArraylen = taskArray.length;
		
		/*
		 * KhÃ³a Ä‘á»‘i tÆ°á»£ng PhotoManager Ä‘áº£m báº£o cÃ¡c tiáº¿n trÃ¬nh khÃ¡c khÃ´ng thá»ƒ thay Ä‘á»•i.
		 * Sau Ä‘Ã³ thá»±c hiá»‡n láº·p máº£ng tÃ¡c vá»¥ vÃ  cháº·n tiáº¿n trÃ¬nh Ä‘ang cháº¡y.
		 */
		synchronized (sInstance) {
			
			// Láº·p máº£ng tÃ¡c vá»¥
			for (int taskArrayIndex =0; taskArrayIndex < taskArraylen; taskArrayIndex++) {
				
				// Truy xuáº¥t tiáº¿n trÃ¬nh Ä‘ang cháº¡y
				Thread thread = taskArray[taskArrayIndex].mThreadThis;
				
				if (null != thread) {
					thread.interrupt();
				}
			}
			
		}
	}
	
	/**
	 * Dá»«ng táº¥t cáº£ cÃ¡c tiáº¿n trÃ¬nh táº£i, xÃ³a luÃ´n trong bá»™ lÆ°u trá»¯ tiáº¿n trÃ¬nh
	 * 
	 * @param downloaderTask
	 * @param pictureUrl
	 */
	static public void removeDownload(PhotoTask downloaderTask, URL pictureUrl) {
		
		// Náº¿u tiáº¿n trÃ¬nh váº«n tá»“n táº¡i vÃ  URL Ä‘ang Ä‘Æ°á»£c thá»±c thi giá»‘ng vá»›i URL truyá»�n vÃ o
		if (downloaderTask != null && downloaderTask.getImageURL().equals(pictureUrl)) {
			
			/*
			 * KhÃ³a Ä‘á»‘i tÆ°á»£ng PhotoManager Ä‘áº£m báº£o cÃ¡c xá»­ lÃ½ khÃ¡c khÃ´ng thá»ƒ thay Ä‘á»•i cÃ¡c tiáº¿n trÃ¬nh
			 */
			synchronized(sInstance) {
				
				// Truy xuáº¥t tiáº¿n trÃ¬nh mÃ  tÃ¡c vá»¥ táº£i Ä‘ang xá»­ lÃ½
				Thread thread = downloaderTask.getCurrentThread();
				
				// Náº¿u tiáº¿n trÃ¬nh tá»“n táº¡i, buá»™c pháº£i dá»«ng láº¡i
				if(null != thread) {
					thread.interrupt();
				}
			}
			
			/*
			 * XÃ³a táº¥t cáº£ cÃ¡c tiá»ƒu trÃ¬nh khá»�i bá»™ lÆ°u trá»¯ tiáº¿n trÃ¬nh.
			 */
			sInstance.mDownloadThreadPool.remove(downloaderTask.getHTTPDownloadRunnable());
		}		
	}
	
	static public PhotoTask startDownload(PhotoView imageView, boolean cacheFlag) {
		
		/*
		 * Truy xuáº¥t tÃ¡c vá»¥ tá»« bá»™ lÆ°u trá»¯, 
		 * tráº£ vá»� null náº¿u bá»™ lÆ°u trá»¯ rá»—ng 
		 */
		PhotoTask downloadTask = sInstance.mPhotoTaskWorkQueue.poll();
		
		// Náº¿u hÃ ng Ä‘á»£i rá»—ng, táº¡o má»›i má»™t tÃ¡c vá»¥ thay tháº¿
		if (null == downloadTask) {
			downloadTask = new PhotoTask();
		}
		
		// Khá»Ÿi táº¡o tÃ¡c vá»¥
		downloadTask.initializeDownloaderTask(PhotoManager.sInstance, imageView, cacheFlag);
		
		/*
		 * Cung cáº¥p tÃ¡c vá»¥ táº£i kÃ¨m theo bá»™ nhá»› Cache tÆ°Æ¡ng á»©ng vá»›i URL
		 */
		downloadTask.setByteBuffer(sInstance.mPhotoCache.get(downloadTask.getImageURL()));
		
		// Náº¿u bá»™ nhá»› Ä‘á»‡m null => áº£nh khÃ´ng Ä‘Æ°á»£c Cache
		if (null == downloadTask.getByteBuffer()) {
			
			/*
			 * Thá»±c thi tiáº¿n trÃ¬nh cho phÃ©p táº£i áº£nh. 
			 * Náº¿u khÃ´ng cÃ³ tiáº¿n trÃ¬nh trong bá»™ lÆ°u trá»¯, Runnable sáº½ Ä‘Æ°á»£c xáº¿p vÃ o hÃ ng Ä‘á»£i
			 */
			sInstance.mDownloadThreadPool.execute(downloadTask.getHTTPDownloadRunnable());
			
			// Thiáº¿t láº­p hiá»ƒn thá»‹ áº£nh máº·c Ä‘á»‹nh trong lÃºc chá»� áº£nh táº£i vÃ  mÃ£ hÃ³a
			imageView.setStatusResource(R.drawable.imagequeued);
		
		// áº¢nh Ä‘Ã£ Ä‘Æ°á»£c lÆ°u Cache nÃªn khÃ´ng cáº§n lÆ°u Cache
		} else {
			
			/*
			 * Tiáº¿n trÃ¬nh táº£i Ä‘Æ°á»£c xem nhÆ° hoÃ n thÃ nh
			 * Chá»‰ cáº§n thá»±c táº£i truy xuáº¥t bá»™ nhá»› Ä‘á»‡m vÃ  tiáº¿n hÃ nh mÃ£ hÃ³a
			 */
			sInstance.handleState(downloadTask, DOWNLOAD_COMPLETE);
		}
		
		// Tráº£ vá»� Ä‘á»‘i tÆ°á»£ng tÃ¡c vá»¥
		return downloadTask;
	}
	
	/**
	 * TÃ¡i táº¡o tÃ¡c vá»¥ báº±ng cÃ¡ch gá»�i phÆ°Æ¡ng thá»©c recycle()
	 * vÃ  Ä‘áº©y chÃºng vÃ o hÃ ng Ä‘á»£i.
	 * @param downloadTask
	 */
	void recycleTask(PhotoTask downloadTask) {
		
		// Giáº£i phÃ³ng vÃ¹ng nhá»› trong tÃ¡c vá»¥
		downloadTask.recycle();
		
		// Ä�áº©y vÃ o hÃ ng Ä‘á»£i Ä‘á»ƒ tÃ¡i sá»­ dá»¥ng
		mPhotoTaskWorkQueue.offer(downloadTask);
	}
	
	

}
