package piki.example.com.loginpikiapp.pikitori.ui.main.post;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;

public class PostImageListActivity extends AppCompatActivity {

    private static final String TAG = "PostImageListActivity";
    private ValueAnimator mAnimator;
    private AtomicBoolean mIsScrolling = new AtomicBoolean(false);

    private ArrayList<String> selectedImage;

    private GridLayout mGrid;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write_image_list);

        getSupportActionBar().setTitle("이미지 순서 만들기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mScrollView = (ScrollView)findViewById(R.id.scroll_view);
        mScrollView.setSmoothScrollingEnabled(true);

        mGrid = (GridLayout) findViewById(R.id.grid_layout);
        mGrid.setOnDragListener(new DragListener());
        final LayoutInflater inflater = LayoutInflater.from(this);


        selectedImage =getIntent().getStringArrayListExtra("selectedImage");
        for (int i = 0; i< selectedImage.size(); i++){
            Log.d(TAG,"selected recv list: "+ selectedImage.get(i));
            View itemView = inflater.inflate(R.layout.grid_item, mGrid, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView11);
            imageView.setVisibility(View.VISIBLE);
//            ImageLoader.getInstance().displayImage( "file://" + selectedImage.get(i), imageView, BasicInfo.displayImageOption );
            ImageLoader.getInstance().displayImage( selectedImage.get(i), imageView, BasicInfo.displayImageOption );
            itemView.setOnLongClickListener(new LongPressListener());
            mGrid.addView(itemView);
        }
    }

    static class LongPressListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            final ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    private int startindex = -1;
    private int endindex = -1;

    class DragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final View view = (View) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:

                    startindex = calculateNewIndex(event.getX(), event.getY());
                    System.out.println("-------------startindex"+startindex);
                    break;
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    // do nothing if hovering above own position
                    if (view == v) return true;
                    // get the new list index
                    final int index = calculateNewIndex(event.getX(), event.getY());
                    final Rect rect = new Rect();
                    mScrollView.getHitRect(rect);
                    final int scrollY = mScrollView.getScrollY();

                    if (event.getY() -  scrollY > mScrollView.getBottom() - 250) {
                        startScrolling(scrollY, mGrid.getHeight());
                    } else if (event.getY() - scrollY < mScrollView.getTop() + 250) {
                        startScrolling(scrollY, 0);
                    } else {
                        stopScrolling();
                    }

                    // remove the view from the old position
                    mGrid.removeView(view);
                    // and push to the new

                    mGrid.addView(view, index);
                    break;
                case DragEvent.ACTION_DROP:
                    view.setVisibility(View.VISIBLE);
                    endindex = calculateNewIndex(event.getX(),event.getY());

                    System.out.println("------------endindex"+endindex);
                    System.out.println("옮길인덱스: "+startindex +" 옮길자리인덱스: "+endindex);

                    if(startindex>=0){
                        arrayListChange(startindex, endindex);
                    }
                    startindex = -1;
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (!event.getResult()) {
                        view.setVisibility(View.VISIBLE);
                    }
                    break;
            }

            return true;
        }
    }

    private void startScrolling(int from, int to) {
        if (from != to && mAnimator == null) {
            mIsScrolling.set(true);
            mAnimator = new ValueAnimator();
            mAnimator.setInterpolator(new OvershootInterpolator());
            mAnimator.setDuration(Math.abs(to - from));
            mAnimator.setIntValues(from, to);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mScrollView.smoothScrollTo(0, (int) valueAnimator.getAnimatedValue());
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsScrolling.set(false);
                    mAnimator = null;
                }
            });
            mAnimator.start();
        }
    }

    private void stopScrolling() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    private int calculateNewIndex(float x, float y) {
        // calculate which column to move to
        final float cellWidth = mGrid.getWidth() / mGrid.getColumnCount();
        final int column = (int)(x / cellWidth);

        // calculate which row to move to
        final float cellHeight = mGrid.getHeight() / mGrid.getRowCount();
        final int row = (int)Math.floor(y / cellHeight);

        // the items in the GridLayout is organized as a wrapping list
        // and not as an actual grid, so this is how to get the new index
        int index = row * mGrid.getColumnCount() + column;
        if (index >= mGrid.getChildCount()) {
            index = mGrid.getChildCount() - 1;
        }
        return index;
    }

    private void arrayListChange(int startindex, int endindex){
        int status = ((startindex-endindex)>0)?(1):(-1);
        String tmpStr = null;

        if(status == -1){
            System.out.println("시작"+startindex+" 끝"+endindex);
            for(int i = startindex; i <= endindex; i++){
                if(i==startindex){
                    tmpStr=selectedImage.get(i);
                }
                if(i==endindex){
                    selectedImage.set(i,tmpStr);
                    for(int j = 0; j<selectedImage.size();j++) {
                        System.out.println(selectedImage.get(j));
                    }
                    return;
                }
                selectedImage.set(i,selectedImage.get(i+1));
            }
        } else if(status == 1){
            System.out.println("시작"+startindex+" 끝"+endindex);
            for(int i=startindex; i>=endindex; i--){
                if(i==startindex){
                    tmpStr=selectedImage.get(i);
                }
                if(i==endindex){
                    selectedImage.set(i,tmpStr);
                    for(int j = 0; j<selectedImage.size();j++) {
                        System.out.println(selectedImage.get(j));
                    }
                    return;
                }
                selectedImage.set(i,selectedImage.get(i-1));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                finish();
            }
            break;
            case R.id.next: {
                Intent intent = new Intent(this,PostWriteActivity.class);
                intent.putExtra("selectedImage",selectedImage);
                for(String s: selectedImage) {
                    Log.d(TAG, "selectedImage: send to write :" + s);
                }
                startActivity(intent);
            }
            break;
        }
        return true;
    }
}