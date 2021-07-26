package com.thinksmarter786.smartLanguageTranslator.Service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.thinksmarter786.smartLanguageTranslator.Models.ClipBoardDataModel;
import com.thinksmarter786.smartLanguageTranslator.R;
import com.thinksmarter786.smartLanguageTranslator.Utils.SharedPrefs;
import com.thinksmarter786.smartLanguageTranslator.Utils.TranslateSingleton;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Myservice extends AccessibilityService {
    private TextToSpeech textToSpeech;
    private Context context;
    View actionBubble = null;
    private WindowManager windowManager;
    private ClipBoardDataModel clipBoardDataModel = null;
    TranslateSingleton translateSingleton;
    private Point szWindow = new Point();
    SharedPrefs sharedPrefs;
    private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;
    private boolean isLeft = true;

    public Myservice() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        translateSingleton = TranslateSingleton.getInstance();
        sharedPrefs = SharedPrefs.getInstance(getApplicationContext());
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        getWindowManagerDefaultDisplay();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        getWindowManagerDefaultDisplay();
        if(actionBubble != null) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) actionBubble.getLayoutParams();

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


                if (layoutParams.y + (actionBubble.getHeight() + getStatusBarHeight()) > szWindow.y) {
                    layoutParams.y = szWindow.y - (actionBubble.getHeight() + getStatusBarHeight());
                    windowManager.updateViewLayout(actionBubble, layoutParams);
                }

                if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                    resetPosition(szWindow.x);
                }

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

                if (layoutParams.x > szWindow.x) {
                    resetPosition(szWindow.x);
                }

            }
        }

    }
    private void getWindowManagerDefaultDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
            windowManager.getDefaultDisplay().getSize(szWindow);
        else {
            int w = windowManager.getDefaultDisplay().getWidth();
            int h = windowManager.getDefaultDisplay().getHeight();
            szWindow.set(w, h);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch(event.getEventType()){
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                //calltheTextToPerform(event);
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                calltheTextToPerform(event);
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                calltheTextToPerform(event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                break;
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
                calltheTextToPerform(event);
                break;
        }
    }

    private void calltheTextToPerform(AccessibilityEvent event) {
        int selectBegin = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if(event.getSource() != null){
                selectBegin = event.getFromIndex();
                int selectEnd = event.getToIndex();
                if (selectBegin == selectEnd)
                {
                    return;
                }
                String text = event.getText().toString().substring(selectBegin,selectEnd + 1).replaceAll("[^a-zA-Z0-9]","");
                if(!text.trim().isEmpty())
                {
                    if(actionBubble == null)
                        floatingiconAction();
                    clipBoardDataModel = new ClipBoardDataModel(text);
                }
            }

        }else{
            ClipboardManager manager = (ClipboardManager) getSystemService(getBaseContext().CLIPBOARD_SERVICE);

            manager.addPrimaryClipChangedListener(
                    new ClipboardManager.OnPrimaryClipChangedListener() {
                        @Override
                        public void onPrimaryClipChanged() {
                            //call floating Icon

                            if(manager.hasPrimaryClip()){
                                ClipData data = manager.getPrimaryClip();
                                ClipData.Item item = data.getItemAt(0);
                                clipBoardDataModel = new ClipBoardDataModel(item.getText().toString());
                                if(actionBubble == null)
                                    floatingiconAction();
                            }
                        }
                    }
            );
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED|AccessibilityEvent.TYPE_VIEW_CLICKED|AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED|AccessibilityEvent.TYPE_VIEW_SELECTED|AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        info.notificationTimeout = 1000;
        info.packageNames = null;
        this.setServiceInfo(info);
    }

    private void floatingiconAction() {
        actionBubble = LayoutInflater.from(context).inflate(R.layout.backgroundbubblelayout,null);

        final WindowManager.LayoutParams params ;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT
            );

        }
        else
        {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT
            );
        }


        params.gravity = Gravity.LEFT | Gravity.CENTER;
        params.x = 0;
        params.y = 100;

        windowManager.addView(actionBubble,params);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        View collapsedView = actionBubble.findViewById(R.id.collapse_view);
        View expandedView = actionBubble.findViewById(R.id.expanded_container);
        View rootView = actionBubble.findViewById(R.id.root_container);
        CircleImageView closeButtonCollapsed = (CircleImageView) actionBubble.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    disableSelf();
                }
                stopSelf();
            }
        });
        CircleImageView closeButtonExpanded = (CircleImageView) actionBubble.findViewById(R.id.expand_close_btn);

        closeButtonExpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                resetPosition(params.x);
            }
        });
        rootView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            int remove_img_width = 0, remove_img_height = 0;
            int XValues=0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) actionBubble.getLayoutParams();

                //get the touch location coordinates
                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                int x_cord_Destination, y_cord_Destination;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        x_init_margin = layoutParams.x;
                        y_init_margin = layoutParams.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isVisibleCollapsable()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                                callActionToPerform();
                            }

                        }
                        y_cord_Destination = y_init_margin+Ydiff;
                        int barHeight = getStatusBarHeight();
                        if (y_cord_Destination < 0) {
                            int destinationY = -y_cord_Destination;
                            int targetDestination = destinationY+actionBubble.getHeight()+barHeight;
                            if((szWindow.y /2 ) > targetDestination)
                                y_cord_Destination = -(destinationY) ;
                            else
                                y_cord_Destination = 0;
                        } else if (y_cord_Destination + (actionBubble.getHeight() + barHeight) > szWindow.y) {
                            y_cord_Destination = szWindow.y - (actionBubble.getHeight() + barHeight);
                        }
                        layoutParams.y = y_cord_Destination;
                        resetPosition(x_cord);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(actionBubble, params);
                        return true;
                }

                return false;
            }
        });

    }
    private void resetPosition(int x_cord_now) {
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true;
            moveToLeft(x_cord_now);
        } else {
            isLeft = false;
            moveToRight(x_cord_now);
        }

    }
    private void moveToLeft(final int current_x_cord) {
        final int x = szWindow.x - current_x_cord;

        new CountDownTimer(10, 1) {
            //get params of Floating Widget view
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) actionBubble.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                mParams.x = 0 - (int) (current_x_cord * current_x_cord * step);

                //If you want bounce effect uncomment below line and comment above line
                // mParams.x = 0 - (int) (double) bounceValue(step, x);


                //Update window manager for Floating Widget
                windowManager.updateViewLayout(actionBubble, mParams);
            }

            public void onFinish() {
                mParams.x = 0;

                //Update window manager for Floating Widget
                windowManager.updateViewLayout(actionBubble, mParams);
            }
        }.start();
    }

    /*  Method to move the Floating widget view to Right  */
    private void moveToRight(final int current_x_cord) {

        new CountDownTimer(10, 1) {
            //get params of Floating Widget view
            WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) actionBubble.getLayoutParams();

            public void onTick(long t) {
                long step = (500 - t) / 5;

                mParams.x = (int) (szWindow.x + (current_x_cord * current_x_cord * step) - actionBubble.getWidth());

                //If you want bounce effect uncomment below line and comment above line
                //  mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - mFloatingWidgetView.getWidth();

                //Update window manager for Floating Widget
                windowManager.updateViewLayout(actionBubble, mParams);
            }

            public void onFinish() {
                mParams.x = szWindow.x - actionBubble.getWidth();

                //Update window manager for Floating Widget
                windowManager.updateViewLayout(actionBubble, mParams);
            }
        }.start();
    }

    private int getStatusBarHeight() {
        return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
    }

    private void callActionToPerform() {
        if(!isVisibleCollapsable()){
            CircleImageView readMeExpanded = (CircleImageView) actionBubble.findViewById(R.id.readMe);
            CircleImageView translateExpanded = (CircleImageView) actionBubble.findViewById(R.id.translateForMe);
            readMeExpanded.setColorFilter(Color.BLACK);
            translateExpanded.setColorFilter(Color.BLACK);
            readMeExpanded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clipBoardDataModel!=null)
                        callLanguageDetector(clipBoardDataModel.getClipData(),"SPEAK");
                }
            });
            translateExpanded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(clipBoardDataModel!=null)
                        callLanguageDetector(clipBoardDataModel.getClipData(),"TRANSLATE");
                }
            });
        }
    }

    private boolean isVisibleCollapsable() {
        return  actionBubble == null || actionBubble.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }


    private void callLanguageDetector(String selectedContent,String mode) {
        if(!selectedContent.isEmpty()){
            //Toast.makeText(getApplicationContext(),"callLanguage::: " + selectedContent,Toast.LENGTH_LONG).show();
            LanguageIdentifier languageIdentifier = LanguageIdentification.getClient();
            languageIdentifier.identifyLanguage(selectedContent)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@Nullable String languageCode) {
                                    if (languageCode.equals("und")) {
                                        textToSpeech.setLanguage(Locale.ENGLISH);
                                        Toast.makeText(getApplicationContext(),"Unable to find the language",Toast.LENGTH_LONG).show();

                                    } else {
                                        //Toast.makeText(getApplicationContext(),""+languageCode,Toast.LENGTH_LONG).show();
                                        textToSpeech.setLanguage(new Locale(languageCode));
                                        if(mode.equalsIgnoreCase("speak"))
                                            textToSpeech.speak(selectedContent,TextToSpeech.QUEUE_FLUSH,null);
                                        else
                                            callTranslateText(selectedContent,languageCode);
                                    }
                                }

                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Something went wrong...",Toast.LENGTH_LONG).show();
                                }
                            }
                    );
        }
    }

    private void callTranslateText(String selectedContent, String languageCode) {
        String code = sharedPrefs.getCode("LanguageCode");
        Task<String> translatedText = translateSingleton.translate(selectedContent,languageCode,code);
        translatedText.addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String convertedResult) {
                textToSpeech.setLanguage(new Locale(code));
                textToSpeech.speak(convertedResult,TextToSpeech.QUEUE_FLUSH,null);

            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionBubble != null)windowManager.removeView(actionBubble);
    }
}
