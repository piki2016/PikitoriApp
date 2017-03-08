package piki.example.com.loginpikiapp.pikitori.ui.main.post.relay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.PictureService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.ImageUtils;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.ImageModel;
import piki.example.com.loginpikiapp.pikitori.domain.PictureVo;
import piki.example.com.loginpikiapp.pikitori.domain.PostVo;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.main.pikiMainActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostImageListActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostImageSelectActivity;

public class PostRelayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener{
    private static final String TAG = "PostRelayActivity";
    PictureService pictureService = new PictureService();
    private GridView post_update_image_grid;

    private int selected = 0;
    int id ;
    private boolean btn_addImage = true;

    Map<String,ImageModel> selecteditem = new LinkedHashMap<String,ImageModel>();
    postImageSelectorAdapter imageSelectAdapter;
    public static ArrayList<ImageModel> baseitem = new ArrayList<>();
//    public static ArrayList<ImageModel> tmpitem = new ArrayList<>();
    ArrayList<String> addeditem = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_relay);

        getSupportActionBar().setTitle("이미지 선택");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        post_update_image_grid = (GridView) findViewById(R.id.post_update_image_grid);
        imageSelectAdapter = new postImageSelectorAdapter(getApplicationContext());
        post_update_image_grid.setAdapter(imageSelectAdapter);
        post_update_image_grid.setOnItemClickListener(this);

        Toast.makeText(getApplicationContext(),"init",Toast.LENGTH_SHORT).show();
        initUI();

        ComponentName callingApplication = getCallingActivity();
        switch (callingApplication.getShortClassName()) {
            case ".pikitori.ui.main.pikiMainActivity": {
                Long no = getIntent().getLongExtra("post_no", 0L) > 0 ?getIntent().getLongExtra("post_no", 0L) : ((UserVo) Utils.getUserPreferences(this, "PikiUser")).getUser_no();
                PostVo post = new PostVo();
                post.setPost_no(no);
                new FetchPostPictureTask(post).execute();
            }
            break;
            case ".pikitori.ui.main.post.PostTagListActivity": {
                Long no = getIntent().getLongExtra("post_no", 0L) > 0 ?getIntent().getLongExtra("post_no", 0L) : ((UserVo) Utils.getUserPreferences(this, "PikiUser")).getUser_no();
                PostVo post = new PostVo();
                post.setPost_no(no);
                new FetchPostPictureTask(post).execute();
            }
            break;
            case ".pikitori.ui.main.camera.CameraActivity":{
                addeditem.clear();
                addeditem = getIntent().getStringArrayListExtra("selectedImage");
                Log.d(TAG,"selectedImage: recv: " + addeditem);
                updateUI();
            }
            break;
        }
    }

    public void initUI(){
        id = 0;
        baseitem.clear();
        selecteditem.clear();
        imageSelectAdapter.clear();

        //빈 url 넣기
        ImageModel model ;
        model = new ImageModel(String.valueOf(id++),"-1","",false);
        baseitem.add(model);
    }

   public void updateUI(){

       ImageModel model ;
        for(String  url: addeditem){
            model = new ImageModel(String.valueOf(id++),url,"",true);
            baseitem.add(model);
            selecteditem.put(model.getId(),model);
        }

       imageSelectAdapter.add(baseitem);
       imageSelectAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        ImageModel imageModel = (ImageModel) adapterView.getAdapter().getItem(position);
        if (imageModel.isSelected()) {
            imageModel.setSelected(false);
            selecteditem.remove(imageModel.getId());

            (view.findViewById(R.id.icon)).setVisibility(View.GONE);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ((ImageView) view.findViewById(R.id.Image)).setAlpha(255);
            } else {
                ((ImageView) view.findViewById(R.id.Image)).clearColorFilter();
            }
            selected--;

        } else {
            imageModel.setSelected(true);
            selecteditem.put(imageModel.getId(),imageModel);

            (view.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ((ImageView) view.findViewById(R.id.Image)).setAlpha(100);
            } else {
                ((ImageView) view.findViewById(R.id.Image)).setColorFilter(Color.argb(180, 53, 53, 53), PorterDuff.Mode.SRC_ATOP);
            }
            selected++;
        }
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, PostImageSelectActivity.class);
        startActivityForResult(i,BasicInfo.RESULT_CODE_IMAGE_SELECT_LIST);
    }

    private class postImageSelectorAdapter extends ArrayAdapter<ImageModel> {
        ArrayList<ImageModel> imageList;
        LayoutInflater layoutInflater;
        final int THUMBSIZE = 150;
        private DisplayImageOptions options;
        Paint paint = new Paint();
        boolean first = false;

        public postImageSelectorAdapter(Context context) {
            super(context, R.layout.select_grid_image_item);
            options = ImageUtils.universalImageConfiguration(context.getResources().getDimensionPixelSize(R.dimen.gallery_image_size), context.getResources().getDimensionPixelSize(R.dimen.gallery_image_size));
            layoutInflater = LayoutInflater.from(context);
        }

        @Nullable
        @Override
        public ImageModel getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ImageModel imageUri =  getItem(position);
            ViewHolderItem viewHolderItem = null;
            Log.d(TAG,"position:"+ position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.select_grid_image_item, null, false);
                viewHolderItem = new ViewHolderItem();
                viewHolderItem.imageView = (ImageView) convertView.findViewById(R.id.Image);
                viewHolderItem.icon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolderItem.btn_addImage = (Button) convertView.findViewById(R.id.btn_addImage);

                if(position == 0 && first == false) {
                    viewHolderItem.flag= true;
                    first = true;
                    Log.d(TAG,"position="+position+", first="+first);
                }
                convertView.setTag(viewHolderItem);

            } else {
                Log.d(TAG,"position:"+ position + "convertView not null " + imageUri.getUri());
                viewHolderItem = (ViewHolderItem) convertView.getTag();
            }

            if (imageUri.isSelected()) {
                viewHolderItem.btn_addImage.setVisibility(View.GONE);
                viewHolderItem.imageView.setVisibility(View.VISIBLE);
                viewHolderItem.icon.setVisibility(View.VISIBLE);
                viewHolderItem.imageView.setColorFilter(Color.argb(180, 53, 53, 53), PorterDuff.Mode.SRC_ATOP);

            } else {
                viewHolderItem.icon.setVisibility(View.GONE);
//                viewHolderItem.imageView.clearColorFilter();
                Log.d(TAG," 너여기지!");
            }

            if( first==true && position==0){ //&& viewHolderItem.flag == true && imageUri.getUri().equals("-1") ){
                Log.d(TAG,"here :"+ position + "convertView not null " + imageUri.getUri() + "flag "+viewHolderItem.flag);
                viewHolderItem.imageView.setVisibility(View.GONE);
                viewHolderItem.btn_addImage.setVisibility(View.VISIBLE);
                viewHolderItem.btn_addImage.setOnClickListener(PostRelayActivity.this);
                return convertView;
            }else{
                ImageLoader.getInstance().displayImage(imageUri.getUri(), viewHolderItem.imageView, options);
                return convertView;
            }

        }

        public void add(List<ImageModel> list){
            if( list == null ) {
                return;
            }

            for( ImageModel model : list ) {
                add( model );
            }
        }
        class ViewHolderItem {
            boolean flag = false;
            ImageView imageView;
            ImageView icon;
            Button btn_addImage;
        }
    }

    public ArrayList<String> getAllSelectedImages() {
        ArrayList<String> selectedList = new ArrayList<String>();
        Iterator<String> iter = selecteditem.keySet().iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            ImageModel value = selecteditem.get(key);
            Log.d(TAG, "selected: "+ value.getUri());
            selectedList.add(value.getUri());
        }
        return selectedList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this,pikiMainActivity.class);
                startActivity(intent);
                finish();
            }
            break;
            case R.id.next: {
                Intent intent = new Intent(this,PostImageListActivity.class);
                ArrayList<String> selectedImage = getAllSelectedImages();
                intent.putExtra("selectedImage",selectedImage);
                Log.d(TAG,"selectedImages: send: " + selectedImage);
                startActivity(intent);
            }
            break;
        }
        return true;
    }

//    private List<Bitmap> imageDownload(ArrayList<ImageModel> list) {
//        List<Bitmap> result = new ArrayList<Bitmap>();
//        for(ImageModel model : list) {
//            Bitmap bmp = Utils.GetBitmapfromUrl(model.getUri());
//            result.add(bmp);
//        }
//        return result;
//    }
//
//    private void imageLoaderBitmap(ArrayList<ImageModel> list){
//        List<File> result = new ArrayList<File>();
//        for(ImageModel model : list) {
//            File f = ImageLoader.getInstance().getDiskCache().get(model.getUri());
//            result.add(f);
//        }
//    }
//
//    public void nextPostImageList(){
//        Intent intent = new Intent(this,PostImageListActivity.class);
//        ArrayList<String> selectedImage = getAllSelectedImages();
//        intent.putExtra("selectedImage",selectedImage);
//        startActivityForResult(intent, BasicInfo.RESULT_POST_UPDATE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == BasicInfo.RESULT_CODE_IMAGE_SELECT_LIST){
                baseitem.clear();
                ArrayList<String> addeditem  = data.getStringArrayListExtra("selectedImage");
                for (int i = 0; i< addeditem.size(); i++){
                    Log.d(TAG,"select item: " + addeditem.get(i));
                    ImageModel model = new ImageModel(String.valueOf(id++), addeditem.get(i) ,"",true);
                    baseitem.add(model);
                    selecteditem.put(model.getId(),model);
                }
                for(ImageModel model : baseitem){
                    Log.d(TAG,"select item3 "+  model.getId() +"/"+ model.getUri());
                }
                imageSelectAdapter.add(baseitem);
                imageSelectAdapter.notifyDataSetChanged();
            }
        }
    }

    private class FetchPostPictureTask extends SafeAsyncTask<List<PictureVo>>{

        PostVo post;
        public FetchPostPictureTask(PostVo post) {
            this.post = post;
        }

        @Override
        public List<PictureVo> call() throws Exception {
            return pictureService.getPictureGallery(getApplicationContext(),post);
        }

        @Override
        protected void onSuccess(List<PictureVo> list) throws Exception {
            super.onSuccess(list);
            id = 0;
            baseitem.clear();
            selecteditem.clear();
            imageSelectAdapter.clear();
            for(PictureVo post: list){
                Log.d(TAG, "FetchPostPictureTask added" + post);
                addeditem.add(post.getPicture_url());
            }
            updateUI();

        }
    }

}
