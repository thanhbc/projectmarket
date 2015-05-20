package com.thanhbc.libs.imageview;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author HuynhPhuc
 * 
 * Lá»›p Ä‘á»‹nh nghÄ©a tÃ¡c vá»¥ táº£i dá»¯ liá»‡u Bytes tá»« Ä‘Æ°á»�ng dáº«n URL.
 * Khi thá»±c hiá»‡n xong phÆ°Æ¡ng thá»©c handleDownloadState sáº½ Ä‘Æ°á»£c gá»�i Ä‘á»ƒ
 * bÃ¡o cÃ¡o tÃ¬nh tráº¡ng.
 * 
 * CÃ¡c Ä‘á»‘i tÆ°á»£ng cá»§a lá»›p Ä‘Æ°á»£c khá»Ÿi táº¡o vÃ  quáº£n lÃ½ thÃ´ng qua cÃ¡c thá»±c thá»ƒ PhotoTask,
 * do lá»›p Phototask gá»�i thá»±c thi Interface {@link TaskRunnableDownloadMethods}.
 * 
 * CÃ¡c Ä‘á»‘i tÆ°á»£ng PhotoTask khi gá»�i PhotoDownloadRunnable sáº½ tá»± Ä‘á»™ng truyá»�n vÃ o tham sá»‘
 * báº±ng chÃ­nh Ä‘á»‘i tÆ°á»£ng Ä‘Ã³. Ä�á»ƒ tÄƒng tÃ­nh hiá»‡u quáº£, má»™t Ä‘á»‘i tÆ°á»£ng PhotoTask lÃ m viá»‡c vá»›i
 * má»™t Ä‘á»‘i tÆ°á»£ng PhotoDownloadRunnable sáº½ thÃ´ng qua cÃ¡c trÆ°á»�ng dá»¯ liá»‡u cá»§a lá»›p PhotoTask.
 */
public class PhotoDownloadRunnable implements Runnable {
	
	// Biáº¿n thiáº¿t láº­p kÃ­ch cá»¡ má»—i láº§n Ä‘á»�c dá»¯ liá»‡u (Ä‘Æ¡n vá»‹: Bytes)
	private static final int READ_SIZE = 1024*2;
	
	// Tag dÃ nh cho viá»‡c ghi LOG
	//private static final String LOG_TAG = "PhotoDownloadRunnable";
	
	// CÃ¡c biáº¿n háº±ng qui Ä‘á»‹nh tráº¡ng thÃ¡i táº£i
	static final int HTTP_STATE_FAILED = -1;
	static final int HTTP_STATE_STARTED = 0;
	static final int HTTP_STATE_COMPLETED = 1;
	
	// Ä�á»‹nh nghÄ©a trÆ°á»�ng dá»¯ liá»‡u gá»�i thá»±c thi lá»›p tá»« PhotoTask
	final TaskRunnableDownloadMethods mPhotoTask;

	/**
	 * 
	 * @author HuynhPhuc
	 * 
	 * Interfae cho phÃ©p Ä‘á»‹nh nghÄ©a cÃ¡c phÆ°Æ¡ng thá»©c Ä‘Æ°á»£c dÃ¹ng  trong lá»›p PhotoTask.
	 * ChÃ­nh Ä‘á»‘i tÆ°á»£ng PhotoTask sáº½ Ä‘Æ°á»£c truyá»�n vÃ o Interface thÃ´ng qua phÆ°Æ¡ng thá»©c
	 * khá»Ÿi táº¡o, do Ä‘Ã³ má»—i Ä‘á»‘i tÆ°á»£ng cÃ³ thá»ƒ tá»± truy xuáº¥t vÃ o Interface vÃ  quáº£n lÃ½
	 * dá»¯ liá»‡u cá»§a chÃ­nh nÃ³.
	 */
	interface TaskRunnableDownloadMethods {
		
		/**
		 * Thiáº¿t láº­p tiáº¿n trÃ¬nh Ä‘á»‘i tÆ°á»£ng sáº½ thá»±c thi
		 * @param currentThread
		 */
		void setDownloadThread(Thread currentThread);
		
		/**
		 * Tráº£ vá»� dá»¯ liá»‡u cá»§a ná»™i dung táº£i
		 * @return Dá»¯ liá»‡u byte cuá»‘i cÃ¹ng Ä‘Æ°á»£c táº£i vá»�
		 */
		byte[] getByteBuffer();
		
		/**
		 * Thiáº¿t láº­p ná»™i dung táº£i
		 * @param buffer Dá»¯ liá»‡u byte Ä‘á»�c Ä‘Æ°á»£c
		 */
		void setByteBuffer(byte[] buffer);
		
		/**
		 * Ä�á»‹nh nghÄ©a tráº¡ng thÃ¡i cho má»—i Ä‘á»‘i tÆ°á»£ng PhotoTask
		 * @param state Tráº¡ng thÃ¡i hiá»‡n thá»�i cá»§a tÃ¡c vá»¥
		 */
		void handleDownloadState(int state);
		
		/**
		 * Truy xuáº¥t URL cá»§a dá»¯ liá»‡u áº£nh cáº§n táº£i.
		 * @return URL cá»§a dá»¯ liá»‡u áº£nh
		 */
		URL getImageURL();
	}
	
	/**
	 * PhÆ°Æ¡ng thá»©c khá»Ÿi táº¡o cho phÃ©p táº¡o Ä‘á»‘i tÆ°á»£ng PhotoDownloadRunnable
	 * vÃ  lÆ°u trá»¯ chÃºng vÃ o tham chiáº¿u cá»§a Ä‘á»‘i tÆ°á»£ng PhotoTask gá»�i phÆ°Æ¡ng thá»©c
	 * khá»Ÿi táº¡o nÃ y.
	 * @param photoTask
	 */
	public PhotoDownloadRunnable(TaskRunnableDownloadMethods photoTask) {
		mPhotoTask = photoTask;
	}

	/*
	 * Ä�á»‹nh nghÄ©a tÃ¡c vá»¥ sáº½ thá»±c thá»‹ trÃªn tiáº¿n trÃ¬nh.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		
		// LÆ°u trá»¯ láº¡i tiáº¿n trÃ¬nh hiá»‡n táº¡i vÃ o Ä‘á»‘i tÆ°á»£ng PhotoTask
		mPhotoTask.setDownloadThread(Thread.currentThread());
		
		// Cho tiáº¿n trÃ¬nh hiá»‡n táº¡i vÃ o cháº¿ Ä‘á»™ ngáº§m
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		
		/*
		 * Truy xuáº¥t bá»™ nhá»› Ä‘á»‡m tá»« Ä‘á»‘i tÆ°á»£ng PhotoTask dÃ¹ng chung cho cáº£
		 * PhotoDownloadRunnable vÃ  PhotoTask
		 */
		byte[] byteBuffer = mPhotoTask.getByteBuffer();
		
		/*
		 * Tiáº¿n hÃ nh táº£i dá»¯ liá»‡u thÃ´ng qua URL
		 */
		try {
			// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			
			// Kiá»ƒm tra bá»™ nhá»› Ä‘á»‡m
			if (null == byteBuffer) {
				
				//Thiáº¿t láº­p tráº¡ng thÃ¡i káº¿t ná»‘i
				mPhotoTask.handleDownloadState(HTTP_STATE_STARTED);
				
				// Biáº¿n lÆ°u trá»¯ dá»¯ liá»‡u táº£i
				InputStream byteStream = null;
				
				// Thá»±c hiá»‡n táº£i vÃ  báº¯t lá»—i
				try {
					
					// Má»Ÿ káº¿t ná»‘i tá»« URL trong PhotoTask
					HttpURLConnection httpConnection = 
							(HttpURLConnection) mPhotoTask.getImageURL().openConnection();
					
					// Thiáº¿t láº­p bÃ¡o cÃ¡o tá»« ngÆ°á»�i dÃ¹ng Ä‘áº¿n Server
					//httpConnection.setRequestProperty("User-Agent", Constant.USER_AGENT);
					
					// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
					
					// Truy xuáº¥t dá»¯ liá»‡u
					byteStream = httpConnection.getInputStream();
					
					// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
					
					/*
					 * Truy xuáº¥t kÃ­ch cá»¡ ná»™i dung táº£i
					 * CÃ³ thá»ƒ cÃ³ hoáº·c khÃ´ng
					 */
					int contentSize = httpConnection.getContentLength();
					
					/*
					 * Náº¿u khÃ´ng thÃ¬ giáº£i quyáº¿t theo khÃ´ng ^^
					 */
					if (contentSize == -1) {
						
						// Cáº¥p phÃ¡t bá»™ nhá»› Ä‘á»‡m táº¡m
						byte[] tempBuffer = new byte[READ_SIZE];
						
						// LÆ°u bá»™ nhá»› khÃ´ng gian Ä‘Ã£ khá»Ÿi táº¡o
						int bufferLeft = tempBuffer.length;
						
						/*
						 * Ä�á»‹nh nghÄ©a Ä‘á»‹a chá»‰ khá»Ÿi táº¡o cá»§a bá»™ nhá»› Ä‘á»‡m káº¿ tiáº¿p
						 * vÃ  khá»Ÿi táº¡o káº¿t quáº£ Ä‘á»�c dá»¯ liá»‡u nhá»‹ phÃ¢n
						 */
						int bufferOffset = 0;
						int readResult = 0;
						
						/*
						 * VÃ²ng láº·p "outer" cho phÃ©p láº·p Ä‘áº¿n khi táº¥t cáº£ dá»¯ 
						 * liá»‡u Ä‘Ã£ Ä‘Æ°á»£c táº£i vá»�. Má»™t vÃ²ng láº·p bÃªn trong ná»¯a cho
						 * phÃ©p thá»±c thi Ä‘áº¿n khi háº¿t bá»™ nhá»› táº¡m vÃ  cáº¥p phÃ¡t thÃªm
						 */
						outer: do {
							while (bufferLeft > 0) {
								
								/*
								 * Ä�á»�c dá»¯ liá»‡u tá»« URL vÃ o bá»™ nhá»› táº¡m
								 * Báº¯t Ä‘áº§u á»Ÿ vá»‹ trÃ­ Byte trá»‘ng vÃ  Ä‘á»�c
								 * sá»‘ lÆ°á»£ng Byte tÃ¹y thuá»™c vÃ o khÃ´ng gian
								 * trá»‘ng cÃ²n láº¡i trong bá»™ nhá»› táº¡m.
								 */
								readResult = byteStream.read(tempBuffer, bufferOffset, bufferLeft);
								
								/*
								 * PhÆ°Æ¡ng thá»©c InputStream.read() tráº£ vá»� 0
								 * khi Ä‘á»�c háº¿t dá»¯ liá»‡u táº­p tin.
 								 */
								if (readResult < 0) {
									break outer;
								}
								
								/*
								 * Náº¿u Ä‘á»�c chÆ°a xong, má»Ÿ rá»™ng bá»™ nhá»› táº¡m
								 * theo dá»¯ liá»‡u káº¿t quáº£ Ä‘á»�c Ä‘Æ°á»£c
								 */
								bufferOffset += readResult;
								
								// Trá»« Ä‘i sá»‘ Byte Ä‘Ã£ Ä‘á»�c Ä‘á»ƒ láº¥y Ä‘á»‹a chá»‰ táº¡m káº¿ tiáº¿p 
								bufferLeft -= readResult;
								
								// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
								if (Thread.interrupted()) {
									throw new InterruptedException();
								}								
							}
							
							/*
							 * Náº¿u bá»™ nhá»› táº¡m Ä‘Ã£ Ä‘áº§y, thá»±c hiá»‡n
							 * táº¡o bá»™ nhá»› táº¡m má»›i vá»«a chá»©a dá»¯ liá»‡u cÅ© vá»«a chá»©a
							 * dá»¯ liá»‡u má»›i á»Ÿ vÃ²ng láº·p káº¿ tiáº¿p
							 */
							
							// Thiáº¿t láº­p Ä‘á»‹a chá»‰ bá»™ nhá»› táº¡m lá»›n nháº¥t cÃ³ thá»ƒ
							bufferLeft = READ_SIZE;
							
							/*
							 * Thiáº¿t láº­p kÃ­ch thÆ°á»›c má»›i cÃ³ thá»ƒ chá»©a bá»™ Ä‘á»‡m
							 * cá»§a ná»™i dung cÅ© vÃ   dá»¯ liá»‡u má»›i á»Ÿ vÃ²ng láº·p káº¿ tiáº¿p
							 */
							int newSize = tempBuffer.length + READ_SIZE;
							
							/*
							 * Táº¡o má»›i bá»™ nhá»› táº¡m, di chuyá»ƒn ná»™i dung
							 * cá»§a bá»™ nhá»› táº¡m cÅ© vÃ o Ä‘Ã³, sau Ä‘Ã³ trá»� biáº¿n
							 * bá»™ nhá»› táº¡m Ä‘áº¿n vá»‹ trÃ­ má»›i.
							 */
							byte[] expandedBuffer = new byte[newSize];
							System.arraycopy(tempBuffer, 0, expandedBuffer, 0, tempBuffer.length);
							tempBuffer = expandedBuffer;							
						} while (true);
						
						/*
						 * Khi dá»¯ liá»‡u áº£nh Ä‘Æ°á»£c Ä‘á»�c xong, thá»±c hiá»‡n
						 * táº¡o má»™t bá»™ Ä‘á»‡m ná»¯a cÃ¹ng kÃ­ch thÆ°á»›c vá»›i sá»‘ lÆ°á»£ng Byte
						 * Ä‘Ã£ sá»­ dá»¥ng trong bá»™ nhá»› táº¡m.
						 */
						byteBuffer = new byte[bufferOffset];
						
						// ChÃ©p dá»¯ liá»‡u bá»™ nhá»› táº¡o vÃ o bá»™ Ä‘á»‡m dá»¯ liá»‡u áº£nh
						System.arraycopy(tempBuffer, 0, byteBuffer, 0, bufferOffset);
						
					/*
					 * Náº¿u dung lÆ°á»£ng táº£i Ä‘Ã£ cÃ³, thá»±c hiá»‡n táº¡o bá»™ Ä‘á»‡m báº±ng vá»›i dung lÆ°á»£ng Ä‘Ã³
					 */
					} else {
						
						byteBuffer = new byte[contentSize];
						
						// Sá»‘ lÆ°á»£ng bá»™ Ä‘á»‡m cÃ²n dÆ°
						int remainingLenght = contentSize;
						
						// Má»Ÿ rá»™ng khÃ´ng gian trong bá»™ nhá»› táº¡m
						int bufferOffset = 0;
						
						/*
						 *  Ä�á»�c dá»¯ liá»‡u vÃ o bá»™ Ä‘á»‡m cho Ä‘áº¿n khi sá»‘ Byte 
						 *  báº±ng chiá»�u dÃ i bá»™ Ä‘á»‡m
						 */
						while (remainingLenght > 0){
							
							int readResult = byteStream.read(byteBuffer, bufferOffset, remainingLenght);
							
							/*
							 * Báº¯t láº¡i ngoáº¡i lá»‡ EOF
							 * báº£o Ä‘áº£m vÃ²ng láº·p Ä‘á»�c chÃ­nh xÃ¡c dá»¯ liá»‡u byte cá»§a áº£nh
							 */
							if (readResult < 0) {
								throw new EOFException();
							}
							
							// Di chuyá»ƒn Ä‘áº¿n vá»‹ trÃ­ byte káº¿ tiáº¿p trong bá»™ Ä‘á»‡m
							bufferOffset += readResult;
							
							// Trá»« Ä‘i lÆ°á»£ng byte Ä‘Ã£ Ä‘á»�c
							remainingLenght -= readResult;
							
							// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
							if (Thread.interrupted()) {
								throw new InterruptedException();
							}						
						}						
					}
					
					// Kiá»ƒm tra tiáº¿n trÃ¬nh hiá»‡n táº¡i cÃ³ bá»‹ giÃ¡n Ä‘oáº¡n hay khÃ´ng?
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
					
				//	Báº¯t ngoáº¡i lá»‡ trong quÃ¡ trÃ¬nh nháº­p xuáº¥t dá»¯ liá»‡u
				} catch (IOException e) {
					e.printStackTrace();
					return;
				} finally {
					if (null != byteStream) {
						try {
							byteStream.close();
						} catch (Exception e) {
							
						}
					}
				}				 
			}
			/*
			 * LÆ°u trá»¯ láº¡i sá»‘ lÆ°á»£ng byte Ä‘Ã£ táº£i vÃ o bá»™ Ä‘á»‡m trong Ä‘á»‘i tÆ°á»£ng PhotoTask
			 */
			mPhotoTask.setByteBuffer(byteBuffer);
			
			/*
			 * Thiáº¿t láº­p tráº¡ng thÃ¡i káº¿t ná»‘i cho Ä‘á»‘i tÆ°á»£ng PhotoTask
			 * Ä‘á»“ng thá»�i cho phÃ©p ImageView bÃªn dÆ°á»›i Ä‘Æ°á»£c mÃ£ hÃ³a dá»¯ liá»‡u hÃ¬nh áº£nh.
			 */
			mPhotoTask.handleDownloadState(HTTP_STATE_COMPLETED);
		
		} catch (InterruptedException e){
			
		} finally {
			
			// Náº¿u khÃ´ng cÃ³ dá»¯ liá»‡u, bÃ¡o cÃ¡o táº£i tháº¥t báº¡i
			if (null == byteBuffer) {
				mPhotoTask.handleDownloadState(HTTP_STATE_FAILED);
			}
			
			/*
			 * Thá»±c thi phÆ°Æ¡ng thá»©c setHTTPDownloadThread() trong lá»›p PhotoTask
			 * sáº½ gá»�i phÆ°Æ¡ng thá»±c setCurrentThread, cho phÃ©p khÃ³a Ä‘á»‘i tÆ°á»£ng lÆ°u trá»¯ tiáº¿n trÃ¬nh
			 * vÃ  tráº£ vá»� tiáº¿n trÃ¬nh hiá»‡n táº¡i. Viá»‡c nÃ y sáº½ giÃºp giá»¯ láº¡i cÃ¡c tham chiáº¿u
			 * Ä‘áº¿n cÃ¡c tiáº¿n trÃ¬nh cÃ¹ng thá»�i Ä‘iá»ƒm cho Ä‘áº¿n khi tham chiáº¿u Ä‘áº¿n tiáº¿n trÃ¬nh hiá»‡n táº¡i
			 * Ä‘Æ°á»£c xÃ³a.
			 */
			
			// Thiáº¿t láº­p tiáº¿n trÃ¬nh hiá»‡n táº¡i NULL, xÃ³a bá»™ lÆ°u trá»¯
			mPhotoTask.setDownloadThread(null);
			
			// XÃ³a cÃ¡c biáº¿n cá»� ngoáº¡i lá»‡
			Thread.interrupted();
			
		}
	}

}
