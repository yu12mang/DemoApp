/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2013 YIXIA.COM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vov.vitamio.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import io.vov.vitamio.MediaFormat;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.MediaPlayer.OnSeekCompleteListener;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import io.vov.vitamio.MediaPlayer.TrackInfo;
import io.vov.vitamio.widget.MediaController.MediaPlayerControl;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.utils.ScreenResolution;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 * <p/>
 * VideoView also provide many wrapper methods for
 * {@link MediaPlayer}, such as {@link #getVideoWidth()},
 * {@link #setTimedTextShown(boolean)}
 */
public class VideoView extends SurfaceView
        implements MediaPlayerControl {
    public static final  int VIDEO_LAYOUT_ORIGIN       = 0;
    public static final  int VIDEO_LAYOUT_SCALE        = 1;
    public static final  int VIDEO_LAYOUT_STRETCH      = 2;
    public static final  int VIDEO_LAYOUT_ZOOM         = 3;
    public static final  int VIDEO_LAYOUT_FIT_PARENT   = 4;
    private static final int STATE_ERROR               = -1;
    private static final int STATE_IDLE                = 0;
    private static final int STATE_PREPARING           = 1;
    private static final int STATE_PREPARED            = 2;
    private static final int STATE_PLAYING             = 3;
    private static final int STATE_PAUSED              = 4;
    private static final int STATE_PLAYBACK_COMPLETED  = 5;
    private static final int STATE_SUSPEND             = 6;
    private static final int STATE_RESUME              = 7;
    private static final int STATE_SUSPEND_UNSUPPORTED = 8;

    private Uri mUri;
    // mCurrentState is a VideoView object's current state.
    // mTargetState is the state that a method caller intends to reach.
    // For instance, regardless the VideoView object's current state,
    // calling pause() intends to bring the object to a target state
    // of STATE_PAUSED.
    private int           mCurrentState  = STATE_IDLE;
    private int           mTargetState   = STATE_IDLE;
    private float         mAspectRatio   = 0;
    private int           mVideoLayout   = VIDEO_LAYOUT_SCALE;
    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;
    private MediaPlayer   mMediaPlayer   = null;
    private int mVideoWidth;
    private int mVideoHeight;

    private float mVideoAspectRatio;
    private int     mVideoChroma     = MediaPlayer.VIDEOCHROMA_RGBA;
    private boolean mHardwareDecoder = false;

    private int                            mSurfaceWidth;
    private int                            mSurfaceHeight;
    private MediaController                mMediaController;
    private View                           mMediaBufferingIndicator;
    private OnCompletionListener           mOnCompletionListener;
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private OnErrorListener                mOnErrorListener;
    private OnSeekCompleteListener         mOnSeekCompleteListener;
    private OnTimedTextListener            mOnTimedTextListener;
    private OnInfoListener                 mOnInfoListener;
    private OnBufferingUpdateListener      mOnBufferingUpdateListener;
    private int                            mCurrentBufferPercentage;
    private long                           mSeekWhenPrepared; // recording the seek position while preparing
    private Context                        mContext;
    private Map<String, String>            mHeaders;
    private int                            mBufSize;

    public VideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initVideoView(context);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * mVideoHeight / mVideoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * mVideoWidth / mVideoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = mVideoWidth;
                height = mVideoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * mVideoWidth / mVideoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * mVideoHeight / mVideoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        setMeasuredDimension(width, height);
    }


    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            Log.d("shenVitamio", "onSeekComplete");
            if (mOnSeekCompleteListener != null) {
                mOnSeekCompleteListener.onSeekComplete(mp);
            }
        }
    };
    private OnTimedTextListener    mTimedTextListener    = new OnTimedTextListener() {
        @Override
        public void onTimedTextUpdate(byte[] pixels, int width, int height) {
            Log.i("shenVitamio", "onSubtitleUpdate: bitmap subtitle, " + width + "x" + height);
            if (mOnTimedTextListener != null) {
                mOnTimedTextListener.onTimedTextUpdate(pixels, width, height);
            }
        }

        @Override
        public void onTimedText(String text) {
            Log.i("shenVitamio", "onSubtitleUpdate: " + text);
            if (mOnTimedTextListener != null) {
                mOnTimedTextListener.onTimedText(text);
            }
        }
    };


    /**
     * Set the display options
     *
     * @param layout      <ul>
     *                    <li>{@link #VIDEO_LAYOUT_ORIGIN}
     *                    <li>{@link #VIDEO_LAYOUT_SCALE}
     *                    <li>{@link #VIDEO_LAYOUT_STRETCH}
     *                    <li>{@link #VIDEO_LAYOUT_FIT_PARENT}
     *                    <li>{@link #VIDEO_LAYOUT_ZOOM}
     *                    </ul>
     * @param aspectRatio video aspect ratio, will audo detect if 0.
     */
    public void setVideoLayout(int layout, float aspectRatio) {
        LayoutParams lp = getLayoutParams();
        Pair<Integer, Integer> res = ScreenResolution.getResolution(mContext);
        int windowWidth = res.first.intValue(), windowHeight = res.second.intValue();
        float windowRatio = windowWidth / (float) windowHeight;
        float videoRatio = aspectRatio <= 0.01f ? mVideoAspectRatio : aspectRatio;
        mSurfaceHeight = mVideoHeight;
        mSurfaceWidth = mVideoWidth;
        if (VIDEO_LAYOUT_ORIGIN == layout && mSurfaceWidth < windowWidth && mSurfaceHeight < windowHeight) {
            lp.width = (int) (mSurfaceHeight * videoRatio);
            lp.height = mSurfaceHeight;
        } else if (layout == VIDEO_LAYOUT_ZOOM) {
            lp.width = windowRatio > videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
            lp.height = windowRatio < videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
        } else if (layout == VIDEO_LAYOUT_FIT_PARENT) {
            ViewGroup parent = (ViewGroup) getParent();
            float parentRatio = ((float) parent.getWidth()) / ((float) parent.getHeight());
            lp.width = (parentRatio < videoRatio) ? parent.getWidth() : Math.round(((float) parent.getHeight()) * videoRatio);
            lp.height = (parentRatio > videoRatio) ? parent.getHeight() : Math.round(((float) parent.getWidth()) / videoRatio);
        } else {
            boolean full = layout == VIDEO_LAYOUT_STRETCH;
            lp.width = (full || windowRatio < videoRatio) ? windowWidth : (int) (videoRatio * windowHeight);
            lp.height = (full || windowRatio > videoRatio) ? windowHeight : (int) (windowWidth / videoRatio);
        }
        setLayoutParams(lp);
        getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
        Log.d("shenVitamio",
                "VIDEO: " + mVideoWidth + "x" + mVideoHeight + "x" + mVideoAspectRatio + ", Surface: " + mSurfaceWidth + "x" + mSurfaceHeight +
                ", LP: " +
                lp.width + "x" + lp.height + ", Window:" + windowWidth + "x" + windowHeight + "x" + windowRatio);
        mVideoLayout = layout;
        mAspectRatio = aspectRatio;
    }

    @SuppressWarnings("deprecation")
    private void initVideoView(Context ctx) {
        mContext = ctx;
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().setFormat(PixelFormat.RGBA_8888); // PixelFormat.RGB_565
        getHolder().addCallback(mSHCallback);
        // this value only use Hardware decoder before Android 2.3
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && mHardwareDecoder) {
            getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (ctx instanceof Activity) {
            ((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    public boolean isValid() {
        return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    private void openVideo() {
//    if (mUri == null || mSurfaceHolder == null || !Vitamio.isInitialized(mContext))
//      return;
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        try {

            mMediaPlayer = new MediaPlayer(mContext, mHardwareDecoder);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnTimedTextListener(mTimedTextListener);
            mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setBufferSize(mBufSize);
            mMediaPlayer
                    .setVideoChroma(mVideoChroma == MediaPlayer.VIDEOCHROMA_RGB565 ? MediaPlayer.VIDEOCHROMA_RGB565 : MediaPlayer.VIDEOCHROMA_RGBA);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException ex) {
            Log.e("shenVitamio", "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            Log.e("shenVitamio", "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }

    public void setMediaController(MediaController controller) {
        if (mMediaController != null) {
            mMediaController.hide();
        }
        mMediaController = controller;
        attachMediaController();
    }

    public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
        if (mMediaBufferingIndicator != null) {
            mMediaBufferingIndicator.setVisibility(View.GONE);
        }
        mMediaBufferingIndicator = mediaBufferingIndicator;
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View) this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());

            if (mUri != null) {
                List<String> paths = mUri.getPathSegments();
                String name = paths == null || paths.isEmpty() ? "null" : paths.get(paths.size() - 1);
                mMediaController.setFileName(name);
            }
        }
    }

    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new MediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    Log.d("shenVitamio", "onVideoSizeChanged: (" + width + "x" + height + ")");
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoAspectRatio = mp.getVideoAspectRatio();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        setVideoLayout(mVideoLayout, mAspectRatio);
                    }
                }
            };
    MediaPlayer.OnPreparedListener         mPreparedListener    = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            Log.d("shenVitamio", "onPrepared");
            mCurrentState = STATE_PREPARED;


            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            mVideoAspectRatio = mp.getVideoAspectRatio();

            long seekToPosition = mSeekWhenPrepared;
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }

            if (mVideoWidth != 0 && mVideoHeight != 0) {
                setVideoLayout(mVideoLayout, mAspectRatio);
                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (mTargetState == STATE_PLAYING) {
                        start();
                        if (mMediaController != null) {
                            mMediaController.show();
                        }
                    } else if (!isPlaying() &&
                               (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null) {
                            // Show the media controls when we're paused into a video and make 'em stick.
                            mMediaController.show(0);
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }
            // Get the capabilities of the player for this stream
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
        }
    };
    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.d("shenVitamio", "onCompletion");
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private MediaPlayer.OnInfoListener mInfoListener =
            new MediaPlayer.OnInfoListener() {
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    Log.d("shenVitamio", "onInfo: (" + what + ", " + extra + ")");

                    if (MediaPlayer.MEDIA_INFO_UNKNOW_TYPE == what) {
                        Log.e("shenVitamio", " VITAMIO--TYPE_CHECK  stype  not include  onInfo mediaplayer unknow type ");
                    }

                    if (MediaPlayer.MEDIA_INFO_FILE_OPEN_OK == what) {
                        long buffersize = mMediaPlayer.audioTrackInit();
                        mMediaPlayer.audioInitedOk(buffersize);
                    }

                    Log.d("shenVitamio", "onInfo: (" + what + ", " + extra + ")");

                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, what, extra);
                    } /*else if (mMediaPlayer != null) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            mMediaPlayer.pause();
                            if (mMediaBufferingIndicator != null) {
                                mMediaBufferingIndicator.setVisibility(View.VISIBLE);
                            }
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            mMediaPlayer.start();
                            if (mMediaBufferingIndicator != null) {
                                mMediaBufferingIndicator.setVisibility(View.GONE);
                            }
                        }
                    }*/
                    return true;
                }
            };

    private MediaPlayer.OnErrorListener mErrorListener =
            new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                    Log.d("shenVitamio", "Error: " + framework_err + "," + impl_err);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }

            /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }

            /* Otherwise, pop up an error dialog so the user knows that
             * something bad has happened. Only try and pop up the dialog
             * if we're attached to a window. When we're going away and no
             * longer have a window, don't bother showing the user an error.
             */
                    if (getWindowToken() != null) {
                        int message = framework_err == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? getResources()
                                .getIdentifier("VideoView_error_text_invalid_progressive_playback", "string", mContext
                                        .getPackageName()) : getResources()
                                .getIdentifier("VideoView_error_text_unknown", "string", mContext.getPackageName());

                        new AlertDialog.Builder(mContext)
                                .setTitle(getResources().getIdentifier("VideoView_error_title", "string", mContext.getPackageName()))
                                .setMessage(message).setPositiveButton(getResources()
                                        .getIdentifier("VideoView_error_button", "string", mContext.getPackageName()),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        /* If we get here, there is no onError listener, so
                                         * at least inform them that the video is over.
                                         */
                                        if (mOnCompletionListener != null) {
                                            mOnCompletionListener.onCompletion(mMediaPlayer);
                                        }
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                    return true;
                }
            };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                    if (mOnBufferingUpdateListener != null) {
                        mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
                    }
                }
            };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    public void setOnTimedTextListener(OnTimedTextListener l) {
        mOnTimedTextListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    public void setOnInfoListener(OnInfoListener l) {
        mOnInfoListener = l;
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
                if (mMediaController != null) {
                    if (mMediaController.isShowing()) {
                        mMediaController.hide();
                    }
                    mMediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND && mTargetState == STATE_RESUME) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
                resume();
            } else {
                openVideo();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            if (mMediaController != null) {
                mMediaController.hide();
            }
            release(true);
        }
    };

    /*
     * release the media player in any state
     */
    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState = STATE_IDLE;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
                                     keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                                     keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                                     keyCode != KeyEvent.KEYCODE_MENU &&
                                     keyCode != KeyEvent.KEYCODE_CALL &&
                                     keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
                keyCode == KeyEvent.KEYCODE_SPACE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (!mMediaPlayer.isPlaying()) {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                       || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            Log.i("shenVitamio"," "+mMediaPlayer.isPlaying());
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void suspend() {
        if (isInPlaybackState()) {
            release(false);
            mCurrentState = STATE_SUSPEND_UNSUPPORTED;
            Log.d("shenVitamio", "Unable to suspend video. Release MediaPlayer.");
        }
    }

    public void resume() {
        if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
            mTargetState = STATE_RESUME;
        } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
            openVideo();
        }
    }

    @Override
    public long getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public long getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(long msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public float getVideoAspectRatio() {
        return mVideoAspectRatio;
    }

    /**
     * Must set before {@link #setVideoURI}
     *
     * @param chroma
     */
    public void setVideoChroma(int chroma) {
        getHolder().setFormat(chroma == MediaPlayer.VIDEOCHROMA_RGB565 ? PixelFormat.RGB_565 : PixelFormat.RGBA_8888); // PixelFormat.RGB_565
        mVideoChroma = chroma;
    }

    public void setHardwareDecoder(boolean hardware) {
        mHardwareDecoder = hardware;
    }

    public void setVideoQuality(int quality) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVideoQuality(quality);
        }
    }

    public void setBufferSize(int bufSize) {
        mBufSize = bufSize;
    }

    public boolean isBuffering() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isBuffering();
        }
        return false;
    }

    public String getMetaEncoding() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getMetaEncoding();
        }
        return null;
    }

    public void setMetaEncoding(String encoding) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setMetaEncoding(encoding);
        }
    }

    public SparseArray<MediaFormat> getAudioTrackMap(String encoding) {
        if (mMediaPlayer != null) {
            return mMediaPlayer.findTrackFromTrackInfo(TrackInfo.MEDIA_TRACK_TYPE_AUDIO, mMediaPlayer.getTrackInfo(encoding));
        }
        return null;
    }

    public int getAudioTrack() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getAudioTrack();
        }
        return -1;
    }

    public void setAudioTrack(int audioIndex) {
        if (mMediaPlayer != null) {
            mMediaPlayer.selectTrack(audioIndex);
        }
    }

    public void setTimedTextShown(boolean shown) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setTimedTextShown(shown);
        }
    }

    public void setTimedTextEncoding(String encoding) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setTimedTextEncoding(encoding);
        }
    }

    public int getTimedTextLocation() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getTimedTextLocation();
        }
        return -1;
    }

    public void addTimedTextSource(String subPath) {
        if (mMediaPlayer != null) {
            mMediaPlayer.addTimedTextSource(subPath);
        }
    }

    public String getTimedTextPath() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getTimedTextPath();
        }
        return null;
    }

    public void setSubTrack(int trackId) {
        if (mMediaPlayer != null) {
            mMediaPlayer.selectTrack(trackId);
        }
    }

    public int getTimedTextTrack() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getTimedTextTrack();
        }
        return -1;
    }

    public SparseArray<MediaFormat> getSubTrackMap(String encoding) {
        if (mMediaPlayer != null) {
            return mMediaPlayer.findTrackFromTrackInfo(TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mMediaPlayer.getTrackInfo(encoding));
        }
        return null;
    }


}