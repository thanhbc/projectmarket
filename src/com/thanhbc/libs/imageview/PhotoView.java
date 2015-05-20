package com.thanhbc.libs.imageview;

import java.lang.ref.WeakReference;
import java.net.URL;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.thanhbc.market.R;

/**
 * 
 * @author HuynhPhuc
 * 
 *         Lớp kế thừa từ lớp ImageView bổ sung một số phương thức cho phép tải
 *         và tùy chỉnh thuộc tính trước khi hiển thị hiển thị.
 */
public class PhotoView extends ImageView {

	// Biến cỿ tùy chỉnh việc kích hoạt lưu Cache
	private boolean mCacheFlag;

	// Biến kiểm tra phương thức onDraw đã được thực thi
	private boolean mIsDrawn;

	/*
	 * Tạo tham chiếu "mỿm" từ đối tượng ImageView đến đối tượng PhotoView. Việc
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
	private WeakReference<View> mThisview;

	// Biến lưu trữ ID của View
	private int mHideShowResId = -1;

	// URL trỿ đến nơi lưu trữ dữ liệu ảnh
	private URL mImageURL;

	// Tiến trình thực hiện tải ảnh
	private PhotoTask mDownloadThread;

	/**
	 * Phương thức khởi tạo đối tượng mặc định
	 * 
	 * @param context
	 */
	public PhotoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Phương thức khởi tạo đối tượng và truy xuất các thuộc tính của đối tượng
	 * 
	 * @param context
	 * @param attributeSet
	 */
	public PhotoView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		// Gỿi phương thức truy xuất thuộc tính
		getAttributes(attributeSet);
	}
	
	/**
	 * Phương thức khởi tạo đối tượng, truy xuất thuộc tính và gán kiến trúc mặc định
	 * @param context
	 * @param attributeSet
	 * @param defaultStyle
	 */
	public PhotoView(Context context, AttributeSet attributeSet,
			int defaultStyle) {
		super(context, attributeSet, defaultStyle);
		// Gỿi phương thức truy xuất thuộc tính
		getAttributes(attributeSet);
	}

	/**
	 * Phương thức truy xuất các thuộc tính của một đối tượng View
	 * 
	 * @param attributeSet
	 */
	private void getAttributes(AttributeSet attributeSet) {

		// Truy xuất mảng thuộc tính cho đối tượng View
		TypedArray attributes = getContext().obtainStyledAttributes(
				attributeSet, R.styleable.ImageDownloaderView);

		// Truy xuất Id của View cho việc hiển thị
		mHideShowResId = attributes.getResourceId(
				R.styleable.ImageDownloaderView_hideShowSibling, -1);

		// Trả lại mảng cho việc tái sử dụng mảng
		attributes.recycle();
	}
	
	/**
	 * Thiết lập hiển thị của đối tượng PhotoView
	 * @param visSate
	 */
	private void showView(int visSate) {
		
		// Nếu đối tượng View đang có chứa nội dung
		if (mThisview != null) {
			
			// Tham chiếu "cứng" đến đối tượng View
			View localView = mThisview.get();
			
			// Kiểm tra nếu tham chiếu "mỿm" thật sự có chứa dữ liệu, thiết lập hiển thị
			if (localView != null) {
				localView.setVisibility(visSate);
			}
		}
	}
	
	/**
	 * Phương thức thiết lập dữ liệu null cho ImageView và hiển thị View
	 */
	public void clearImage() {
		setImageDrawable(null);
		showView(View.VISIBLE);
	}
	
	/**
	 * Phương thức trả vỿ URL của đối tượng PhotoView
	 * @return
	 */
	final URL getLocation() {
		return mImageURL;
	}
	
	/*
	 * Phương thức được gỿi khi hệ thống gắn ImageView lên Window.
	 * Phương thức này được gỿi trước hàm onDrawn() nhưng sau hàm onMeasure()
	 * @see android.widget.ImageView#onAttachedToWindow()
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		// Kiểm trả đối tượng View thay thế đã được thiết lập hay chưa
				// và lớp cha của đối tượng View này có phải là đối tượng thuộc lớp View hay không ? @@
		if((this.mHideShowResId != -1) && ((getParent() instanceof View))){
			
			// Tạo đối tượng lưu trữ đối tượng View thay thế
			View localView = ((View)getParent()).findViewById(this.mHideShowResId);
			
			// Nếu đối tượng View thay thế chứa dữ liệu, thực hiện tạo tham chiếu "mỿm".
			if(localView != null){
				this.mThisview = new WeakReference<View>(localView);
			}
		}
	}
	
	/*
	 * Phương thức được gỿi khi ImageView được tháo khỿi Window.
	 * Phương thức này chưa được thiết lập chống rò rỉ bộ nhớ
	 */
	@Override
	protected void onDetachedFromWindow() {
		
		// Xóa dữ liệu hình ảnh, tắt bộ nhớ Cache, hủy kết nối giữa URL và đối tượng View
		setImageURL(null, false, null);
		
		// Truy xuất đối tượng Drawable, có thể null nếu chưa được thiết lập
		Drawable localDrawable = getDrawable();
		
		// Nếu Drawble khác null, gỡ bỿ khỿi View
		if (localDrawable != null)
			localDrawable.setCallback(null);
		
		// Nếu đối tượng View vẫn tồn tại, xóa tham chiếu, sau đó thiết lập tham chiếu null
		if (mThisview != null) {
			mThisview.clear();
			mThisview = null;
		}
		
		// Thiết lập trình tải null
		this.mDownloadThread = null;
		
		super.onDetachedFromWindow();
	}
	
	/*
	 * Phương thức được gỿi khi đối tượng View được bắt đầu hình thành.
	 * Nếu đối tượng View chưa được vẽ, nhưng URL có giá trị thưc hiện gỿi tác vụ tải.
	 * Ngược lại, đơn giản chỉ cần gỿi lại phương thức ở lớp cha
	 * 
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		// Nếu View chưa được vẽ nhưng URL có giá trị
		if ((!mIsDrawn) && (mImageURL != null)) {
			
			// Thực hiện tác vụ tải
			mDownloadThread = PhotoManager.startDownload(this, mCacheFlag);
			
			mIsDrawn = true;
		}
		super.onDraw(canvas);
	}
	
	/**
	 * Thiết lập tham chiếu "mỿm" của đối tượng View hiện tại
	 * đến đối tượng View mới.
	 * @param view
	 */
	public void setHideView(View view) {
		this.mThisview = new WeakReference<View>(view);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
	}
	
	@Override
	public void setImageDrawable(Drawable drawable) {
		// Trạng thái hiển thị của View
		int viewState;
		
		/*
		 * Thiết lập trạng thái hiển thị nếu phương thức được gỿi với tham số null
		 * Ngược lại thiết lập ẩn để làm mới đối tượng View.
		 */
		if (drawable == null) {
			viewState = View.VISIBLE;
		} else {
			viewState = View.INVISIBLE;
		}
		
		// Việc ẩn hiện phụ thuộc triang thái View
		showView(viewState);
		
		
		super.setImageDrawable(drawable);
	}
	
	@Override
	public void setImageResource(int resId) {
		// TODO Auto-generated method stub
		super.setImageResource(resId);
	}
	
	@Override
	public void setImageURI(Uri uri) {
		// TODO Auto-generated method stub
		super.setImageURI(uri);
	}
	
	/**
	 * Trong phương thức này ta sẽ thử thiết lập đưỿng dẫn URL cho ImageView và tải vỿ
	 * <p>
	 * Nếu URL đã tồn tại trong View này, kiểm tra URL nhập vào có trùng hay không?
	 * Nếu không trùng thực hiện xóa các dữ liệu trong View, hủy các kết nối tải.
	 * <p>
	 * Nếu trùng không cần làm gì cả.
	 * <p>
	 * Nếu URL trong View không có giá trị, thực hiển tải và mã hóa dữ liệu ảnh.
	 * 
	 * @param pictureURL
	 * @param cacheFlag
	 * @param imageDrawable
	 */
	public void setImageURL(URL pictureURL, boolean cacheFlag, Drawable imageDrawable) {
		// Nếu URL đã tồn tại trong View này
		if(mImageURL != null) {
			
			// Nếu URL không trùng với URL truyỿn vào => thay đổi ảnh cho View
			if(!mImageURL.equals(pictureURL)) {
				
				// Dừng bất kì tiến trình nào đang thực thị
				PhotoManager.removeDownload(mDownloadThread, mImageURL);
			} else {
				// // Nếu URL trùng với URL truyỿn vào => không cần làm gì hết
				return;
			}
		}
		
		// Thiết lập Drawable cho View
		setImageDrawable(imageDrawable);
		
		// Lưu trữ lại URL của ảnh
		mImageURL = pictureURL;
		
		// Nếu phương thức vẽ đối tượng ImageView hoàn tất và URL có giá trị
		if ((mIsDrawn) && (pictureURL != null)) {
			
			// Thiết lập cỿ lưu Cache
			mCacheFlag = cacheFlag;
			
			/*
			 * Thực hiện tải ảnh. 
			 * Nếu bộ nhớ Cache đang bật có thể truy xuất nội dung ảnh từ đó.
			 */
			mDownloadThread = PhotoManager.startDownload(this, cacheFlag);
		}
	}
	
	/**
	 * Thiết lập đối tượng Drawable mặc định 
	 * @param drawable
	 */
	public void setStatusDrawable(Drawable drawable) {
		
		// Nếu View không có nội dung, thiết lập Drawable
		if(mThisview == null){
			setImageDrawable(drawable);
		}
	}
	
	/**
	 * Thiết lập đối tượng Drawable mặc định 
	 * @param drawable
	 */
	public void setStatusResource(int resId) {
		
		// Nếu View không có nội dung, thiết lập Drawable
		if(mThisview == null){
			setImageResource(resId);
		}
	}

}
