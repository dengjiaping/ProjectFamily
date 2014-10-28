package com.ant.liao.chuxin;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.chuxin.family.utils.CxLog;
/**
 * 
 * @author shichao.wang
 *
 */
public class EnhancedGifView extends GifView {

	private static final String TAG = "EnhancedGifView";
	private int mGifResId;
	private Uri mGifUri;
	private EnhancedGifDecoder mDecoder;
	private static LoadThread mLoadThread = new LoadThread();
	private static TimerThread mTimerThread = new TimerThread();
	   private static TimerThreadNew mTimerThreadNew = new TimerThreadNew();
	private Context mContext;

	public EnhancedGifView(Context context) {
		super(context);
		// gifDecoder = new GifDecoder(this);
		mContext = context;
		setScaleType(ImageView.ScaleType.FIT_XY);
	}

	public EnhancedGifView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

	}

	public EnhancedGifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	protected Parcelable onSaveInstanceState() {
		super.onSaveInstanceState();
		destroy();
		return null;
	}

	/**
	 * ����Դ��ʽ����gifͼƬ
	 * 
	 * @param resId
	 *            gifͼƬ����ԴID
	 */
	public void setGifImage(final int resId) {
		CxLog.d(TAG, "resId = " + resId + "| mGifResId = " + mGifResId);
		
		destroy();

		EnhancedGifDecoder decoder = mTimerThread.registerGifDecoder(resId,
				new TimerThread.Callback() {
					public InputStream call() {
						return getResources().openRawResource(resId);
					}
				});
		decoder.addView(this);
		mDecoder = decoder;
		mGifResId = resId;
		
//		if (!mDecoder.hasGifImage()) {
//		}
	}
	
    public void setGifDecoderImage(final Uri uri){

        CxLog.d(TAG, "Uri = " + uri + "| mGifUri = " + mGifUri);
        
        destroy();

        EnhancedGifDecoder decoder = mTimerThreadNew.registerGifDecoder(uri,
                new TimerThreadNew.CallbackNew() {
                    public InputStream callNew() {
                        ContentResolver resolver = mContext.getContentResolver();
                        InputStream inStream;
                        try {
                            inStream = resolver.openInputStream(uri);
                            return inStream;
                        } catch (FileNotFoundException e) {
                           CxLog.e("setGifDecoderImage", ""+e.getMessage());
                        } 
                        return null;
                    }
                });
        decoder.addView(this);
        mDecoder = decoder;
        mGifUri = uri;
    }

	public void destroy() {
		if (mGifResId != 0) {
			mTimerThread.unregisterGifDecoder(mGifResId);
			mDecoder.removeView(this);
		}
		if(null != mGifUri){
		    mTimerThreadNew.unregisterGifDecoder(mGifUri);
		    mDecoder.removeView(this);
		}
	}

	public void triggerRedraw() {
		if (null != getParent()) {
			//RkLog.d(TAG, "triggerRedraw");
			reDraw();
		} else {
			destroy();
		}
	}

	private static class DecoderControlBlock {
		public EnhancedGifDecoder mDecoder;
		public int mObseverNum;
		public boolean mReady;

		public DecoderControlBlock(EnhancedGifDecoder decoder, int observerNum) {
			mDecoder = decoder;
			mObseverNum = observerNum;
			mReady = false;
		}
	}

	private static class LoadThread extends Thread {
		private List<DecoderControlBlock> mWaitings = new ArrayList<DecoderControlBlock>();

		public void add(DecoderControlBlock dcb) {
			synchronized (mWaitings) {
				mWaitings.add(dcb);
			}

			// launch it now.
			if (!isAlive()) {
				start();
			}
		}

		private void work() {
			DecoderControlBlock dcb;
			synchronized (mWaitings) {
				if (mWaitings.size() == 0)
					return;

				dcb = mWaitings.remove(0);
				if (!dcb.mDecoder.hasGifImage()) {
					mWaitings.add(dcb);
					return;
				}
			}
			if (!dcb.mDecoder.parseOk()) {
				dcb.mDecoder.load();
			}
			if (dcb.mDecoder.parseOk()) {
				dcb.mReady = true;
			}

			if (dcb.mDecoder.getFrameCount() == 1) {
				// if there is only one frame, then just kick it off;
				dcb.mDecoder.next();
			}
		}

		public void run() {
			while (true) {
				work();
				SystemClock.sleep(100);
			}
		}
	}

	/**
	 * �����߳�
	 * 
	 * @author liao
	 * 
	 */
	private static class TimerThread extends Thread {

		private List<EnhancedGifDecoder> actives = new ArrayList<EnhancedGifDecoder>();
		private List<EnhancedGifDecoder> bads = new ArrayList<EnhancedGifDecoder>();
		private List<Integer> badIds = new ArrayList<Integer>();

		private HashMap<Integer, DecoderControlBlock> mGifDecoderMapping = new HashMap<Integer, DecoderControlBlock>();

		public static interface Callback {
			public InputStream call();
		}

		public EnhancedGifDecoder registerGifDecoder(int gifResId,
				Callback alloc) {
			boolean newDecoder = false;
			DecoderControlBlock dcb = null;
			synchronized (mGifDecoderMapping) {
				dcb = mGifDecoderMapping.get(gifResId);
				if (dcb == null) {
					EnhancedGifDecoder decoder = new EnhancedGifDecoder();
					decoder.setGifImage(alloc.call());
					dcb = new DecoderControlBlock(decoder, 1);
					mGifDecoderMapping.put(gifResId, dcb);
					newDecoder = true;
				} else {
					++(dcb.mObseverNum);
				}
			}

			if (newDecoder) {
				mLoadThread.add(dcb);
			}

			if (!isAlive()) {
				// kick it off;
				start();
			}
			return dcb.mDecoder;
		}
		
		public void unregisterGifDecoder(int gifResId) {
			synchronized (mGifDecoderMapping) {
				DecoderControlBlock dcb = mGifDecoderMapping.get(gifResId);
				if (dcb == null) {
					return;
				} else {
					--(dcb.mObseverNum);
				}
			}
		}

		public void work() {
			long now = (long) System.currentTimeMillis();
			actives.clear();
			bads.clear();
			badIds.clear();

			prepare();

			Iterator<EnhancedGifDecoder> it = actives.iterator();
			while (it.hasNext()) {
				EnhancedGifDecoder decoder = it.next();
				if (decoder.getFrameCount() > 1) {
					// check if it is OK to move to next frame
					GifFrame frame = decoder.getCurrentFrame();
					if (frame == null) {
						assert (false);
					}
					if (now >= frame.nextFrameStart) {
						frame = decoder.next();
						frame.nextFrameStart = now + frame.delay;
					}
				}
			}
		}

		public void prepare() {

			synchronized (mGifDecoderMapping) {
				Iterator<Entry<Integer, DecoderControlBlock>> it = mGifDecoderMapping
						.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, DecoderControlBlock> entry = it.next();
					DecoderControlBlock dcb = entry.getValue();
					if (dcb.mObseverNum > 0) {
						if (dcb.mReady) {
							actives.add(dcb.mDecoder);
						}
					} else {
						bads.add(dcb.mDecoder);
						badIds.add(entry.getKey());
					}
				}

				Iterator<Integer> it1 = badIds.iterator();
				while (it1.hasNext()) {
					mGifDecoderMapping.remove(it1.next());
				}
			}

			badIds.clear();

			Iterator<EnhancedGifDecoder> it2 = bads.iterator();
			while (it2.hasNext()) {
				it2.next().free();
			}
			bads.clear();
		}

		public void run() {
			while (true) {
				work();
				SystemClock.sleep(100);
			}
		}
	}
	
	   private static class TimerThreadNew extends Thread {

	        private List<EnhancedGifDecoder> actives = new ArrayList<EnhancedGifDecoder>();
	        private List<EnhancedGifDecoder> bads = new ArrayList<EnhancedGifDecoder>();
	        private List<String> badIds = new ArrayList<String>();

	        private HashMap<String, DecoderControlBlock> mGifDecoderMapping = new HashMap<String, DecoderControlBlock>();

	        public static interface CallbackNew {
	            public InputStream callNew();
	        }
	        
	          public EnhancedGifDecoder registerGifDecoder(Uri uri,
	                    CallbackNew alloc) {
	                boolean newDecoder = false;
	                DecoderControlBlock dcb = null;
	                synchronized (mGifDecoderMapping) {
	                    dcb = mGifDecoderMapping.get(uri.toString());
	                    if (dcb == null) {
	                        EnhancedGifDecoder decoder = new EnhancedGifDecoder();
	                        decoder.setGifImage(alloc.callNew());
	                        dcb = new DecoderControlBlock(decoder, 1);
	                        mGifDecoderMapping.put(uri.toString(), dcb);
	                        newDecoder = true;
	                    } else {
	                        ++(dcb.mObseverNum);
	                    }
	                }

	                if (newDecoder) {
	                    mLoadThread.add(dcb);
	                }

	                if (!isAlive()) {
	                    // kick it off;
	                    start();
	                }
	                return dcb.mDecoder;
	            }
	        
	          public void unregisterGifDecoder(Uri uri) {
	                synchronized (mGifDecoderMapping) {
	                    DecoderControlBlock dcb = mGifDecoderMapping.get(uri.toString());
	                    if (dcb == null) {
	                        return;
	                    } else {
	                        --(dcb.mObseverNum);
	                    }
	                }
	            }

	        public void work() {
	            long now = (long) System.currentTimeMillis();
	            actives.clear();
	            bads.clear();
	            badIds.clear();

	            prepare();

	            Iterator<EnhancedGifDecoder> it = actives.iterator();
	            while (it.hasNext()) {
	                EnhancedGifDecoder decoder = it.next();
	                if (decoder.getFrameCount() > 1) {
	                    // check if it is OK to move to next frame
	                    GifFrame frame = decoder.getCurrentFrame();
	                    if (frame == null) {
	                        assert (false);
	                    }
	                    if (now >= frame.nextFrameStart) {
	                        frame = decoder.next();
	                        frame.nextFrameStart = now + frame.delay;
	                    }
	                }
	            }
	        }

	        public void prepare() {

	            synchronized (mGifDecoderMapping) {
	                Iterator<Entry<String, DecoderControlBlock>> it = mGifDecoderMapping
	                        .entrySet().iterator();
	                while (it.hasNext()) {
	                    Entry<String, DecoderControlBlock> entry = it.next();
	                    DecoderControlBlock dcb = entry.getValue();
	                    if (dcb.mObseverNum > 0) {
	                        if (dcb.mReady) {
	                            actives.add(dcb.mDecoder);
	                        }
	                    } else {
	                        bads.add(dcb.mDecoder);
	                        badIds.add(entry.getKey());
	                    }
	                }

	                Iterator<String> it1 = badIds.iterator();
	                while (it1.hasNext()) {
	                    mGifDecoderMapping.remove(it1.next());
	                }
	            }

	            badIds.clear();

	            Iterator<EnhancedGifDecoder> it2 = bads.iterator();
	            while (it2.hasNext()) {
	                it2.next().free();
	            }
	            bads.clear();
	        }

	        public void run() {
	            while (true) {
	                work();
	                SystemClock.sleep(100);
	            }
	        }
	    }
	
    public Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

}
