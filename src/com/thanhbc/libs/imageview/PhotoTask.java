package com.thanhbc.libs.imageview;

import java.lang.ref.WeakReference;
import java.net.URL;

import android.graphics.Bitmap;

import com.thanhbc.libs.imageview.PhotoDecodeRunnable.TaskRunnableDecodeMethods;
import com.thanhbc.libs.imageview.PhotoDownloadRunnable.TaskRunnableDownloadMethods;


/**
 * 
 * @author HuynhPhuc
 * 
 *        Lớp cho phép quản lý trình PhotoDownloadRunnable và
 *         PhotoDecodeRunnable. Lớp không bao gồm các tác vụ tải và mã hóa dữ
 *         liệu, thay vào đó lớp sẽ quản lý bộ lưu trữ để lưu các tác vụ này.
 * 
 *         Lớp cũng thực thi các Interface được định nghĩa sẵn các lớp tải và mã
 *         hóa dữ liệu, sau đó được truyỿn trực tiếp đến các đối tượng có liên
 *         quan. Ŀể tăng tính hiệu quả, lớp thực hiện tạo một tiến trình riêng
 *         và thực hiện tải dữ liệu trên một đối tượng được ủy thác, sau đó mới
 *         tiến hành mã hóa.
 * 
 *         Lớp PhotoTask có thể dùng chung và tái sử dụng khi cần thiết.
 * 
 */
public class PhotoTask implements TaskRunnableDownloadMethods,
		TaskRunnableDecodeMethods {

	/*
	 * Khởi tạo tham chiếu "mỿm" đến đối tượng ImageView đang được xử lý. Việc
	 * tạo tham chiếu "mỿm" cho phép chống các lỗi rò rỉ bộ nhớ, sự cố ứng dụng
	 * thông qua việc theo dõi trạng thái của các biến mà nó trả vỿ. Nếu tham
	 * chiếu trả vỿ không có giá trị, chứng tỿ bộ dỿn dẹp bộ nhớ đã được thực
	 * thi.
	 * 
	 * Kĩ thuật này khá quan tỿng trong việc thực hiện tham chiếu đến các đối
	 * tượng được gỿi trong một phần của vòng đỿi một thành phần. Khác với việc
	 * sử dụng tham chiếu "cứng" dễ dẫn đến tình trạng rò rỉ bộ nhớ khi giá trị
	 * của đối tượng tiếp tục được thay đổi, tệ hơn nữa các sự cố đóng ứng dụng
	 * có thể xảy ra nếu thành phần nào đó phía dưới bị hủy. Sử dụng tham chiếu
	 * "mỿm" đến đối tượng View bảo đảm việc tham chiếu này chỉ mang tính chất
	 * tạm thỿi.
	 */
	private WeakReference<PhotoView> mImageWeakRef;

	// Biến lưu trữ URL của ảnh
	private URL mImageURL;

	// Ŀộ cao và độ rộng của ảnh được mã hóa
	private int mTargetHeight;
	private int mTargetWidth;

	// Biến kiểm tra bật tắt bộ nhớ Cache
	private boolean mCacheEnabled;
	
	// Biến lưu trữ tiến trình đang xử lý
	Thread mThreadThis;

	/*
	 * Khai báo tham chiếu đến hai đối tượng Runnable: Tải và Mã Hóa
	 */
	private Runnable mDownloadRunnable;
	private Runnable mDecodeRunnable;

	// Bộ đệm chứa thông tin ảnh
	byte[] mImageBuffer;

	// Biến ảnh đã được mã hóa;
	private Bitmap mDecodedImage;

	// Khai báo tiến trình thực thi
	private Thread mCurrentThread;

	/*
	 * Khai báo đối tượng lưu trữ tiến trình
	 */
	private static PhotoManager sPhotoManager;

	/**
	 * Khởi tạo PhotoTask chưa đối tượng tải và đối tượng mã hóa
	 */
	public PhotoTask() {
		// Tạo các tiểu trình
		mDownloadRunnable = new PhotoDownloadRunnable(this);
		mDecodeRunnable = new PhotoDecodeRunnable(this);
		sPhotoManager = PhotoManager.getInstance();
	}

	/**
	 * Khởi tạo tác vụ PhotoTask
	 * 
	 * @param photoManager
	 * @param photoView
	 * @param cacheFlag
	 */
	void initializeDownloaderTask(PhotoManager photoManager,
			PhotoView photoView, boolean cacheFlag) {

		/*
		 * Thiết lập giá trị biến lưu trữ tiến trình từ tham số truyỿn vào
		 */
		sPhotoManager = photoManager;

		// Truy xuất địa chỉ URL của ảnh
		mImageURL = photoView.getLocation();

		// Khởi tạo tham chiếu "mỿm" cho đối tượng View truyỿn vào
		mImageWeakRef = new WeakReference<PhotoView>(photoView);

		// Bật cỿ lưu trữ Cache theo giá trị truyỿn vào
		mCacheEnabled = cacheFlag;

		// Truy xuất thông tin độ cao, độ rộng ImageView
		mTargetWidth = photoView.getWidth();
		mTargetHeight = photoView.getHeight();
	}

	// Thực thi phương thức HTTPDownloadRunnable.getByteBuffer
	@Override
	public byte[] getByteBuffer() {
		// Trả vỿ biến toàn cục
		return mImageBuffer;
	}

	/**
	 * Thực hiện tái tạo đối tượng PhotoTask trước khi truyỿn vào bộ lưu trữ
	 * tiến trình. Ŀơn giản là để tránh tràn bộ nhớ.
	 */
	void recycle() {

		// Xóa các tham chiếu "mỿm" đến đối tượng ImageView
		if (null != mImageWeakRef) {
			mImageWeakRef.clear();
			mImageWeakRef = null;
		}

		// Giải phóng các tham chiếu đến bộ đệm và Bitmap
		mImageBuffer = null;
		mDecodedImage = null;

	}

	// Thực thi phương thức PhotoDecodedRunnable.getTargetWidth. Trả vỿ biến
		// toàn cục
	@Override
	public int getTargetWidth() {
		return mTargetWidth;
	}

	// Thực thi phương thức PhotoDecodedRunnable.getTargetHeight. Trả vỿ biến
		// toàn cục
	@Override
	public int getTargetHeight() {
		return mTargetHeight;
	}

	// Kiểm tra việc lưu Cache
	public boolean isCacheEnabled() {
		return mCacheEnabled;
	}

	// Thực thi phương thức PhotoDownloadRunnable.getTargetHeight. Trả vỿ biến
		// toàn cục
	@Override
	public URL getImageURL() {
		return mImageURL;
	}

	// Thực thi phương thức PhotoDownloadRunnable.setByteBuffer. Trả vỿ biến
		// toàn cục
	@Override
	public void setByteBuffer(byte[] imageBuffer) {
		mImageBuffer = imageBuffer;
	}

	// Nhận ủy thác việc quản lý trạng thái tác vụ từ đối tượng PhotoManager
	void handleState(int state) {
		sPhotoManager.handleState(this, state);
	}

	// Trả vỿ ảnh PhotoDecodeRunnable đã mã hóa.
	Bitmap getImage() {
		return mDecodedImage;
	}

	// Trả vỿ tiểu trình tải dữ liệu ảnh
	Runnable getHTTPDownloadRunnable() {
		return mDownloadRunnable;
	}

	// Trả vỿ tiểu trình mã hóa dữ liệu ảnh
	Runnable getPhotoDecodeRunnable() {
		return mDecodeRunnable;
	}

	// Trả vỿ đối tượng ImageView
	public PhotoView getPhotoView() {
		if (null != mImageWeakRef) {
			return mImageWeakRef.get();
		}
		return null;
	}

	/*
	 * Trả vỿ tiến trình đang được thực thi PhotoTask Trước tiên cần khóa các
	 * trưỿng dữ liệu tĩnh (ThreadPool PhotoManager) Việc khóa cần thực hiện bởi
	 * tham chiếu đối tượng Thread được lưu trữ trong chính đối tượng đó =>
	 * không thể thay đổi giá trị bởi tiến trình khác.
	 */
	public Thread getCurrentThread() {
		synchronized (sPhotoManager) {
			return mCurrentThread;
		}
	}
	/*
	 * Thiết lập định danh cho tiến trình đang thực thi.
	 */
	public void setCurrentThread(Thread thread) {
		synchronized (sPhotoManager) {
			mCurrentThread = thread;
		}
	}

	/*
	 * Thực thi phương thức PhotoDecodeRunnable.setImage Thiết lập đối tượng
	 * Bitmap cho dữ liệu ảnh.
	 */
	@Override
	public void setImage(Bitmap decodedImage) {
		// TODO Auto-generated method stub
		mDecodedImage = decodedImage;
	}

	// Thực thi phương thức PhotoDownloadRunnable.setDownloadThread
	@Override
	public void setDownloadThread(Thread currentThread) {
		setCurrentThread(currentThread);
	}

	// Thực thi phương thức PhotoDownloadRunnale.handleDownloadState
	@Override
	public void handleDownloadState(int state) {
		int outState = state;

		// Chuyển đổi trạng thái tải thành trạng thái tương ứng
		switch (state) {
		case PhotoDownloadRunnable.HTTP_STATE_COMPLETED:
			outState = PhotoManager.DOWNLOAD_COMPLETE;
			break;
		case PhotoDownloadRunnable.HTTP_STATE_FAILED:
			outState = PhotoManager.DOWNLOAD_FAILED;
			break;
		default:
			outState = PhotoManager.DOWNLOAD_STARTED;
			break;
		}
		// Truyỿn dữ liệu vào bộ lưu trữ tiến trình
		handleState(outState);
	}

	// Thực thi phương thức PhotoDecodeRunnable.setImageDecodeThread
	@Override
	public void setImageDecodeThread(Thread currentThread) {
		setCurrentThread(currentThread);
	}

	// Thực thi phương thức PhotoDecodeRunnable.handleDecodeState
	@Override
	public void handleDecodeState(int state) {

		int outState;

		// Chuyển đổi trạng thái tải thành trạng thái tương ứng
		switch (state) {
		case PhotoDecodeRunnable.DECODE_STATE_COMPLETED:
			outState = PhotoManager.TASK_COMPLETE;
			break;
		case PhotoDecodeRunnable.DECODE_STATE_FAILED:
			outState = PhotoManager.DOWNLOAD_FAILED;
			break;
		default:
			outState = PhotoManager.DECODE_STARTED;
			break;
		}
		// Truyỿn dữ liệu vào bộ lưu trữ tiến trình
		handleState(outState);

	}

}
