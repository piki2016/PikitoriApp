package piki.example.com.loginpikiapp.pikitori.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import piki.example.com.loginpikiapp.R;
import piki.example.com.loginpikiapp.android.JSONResult;
import piki.example.com.loginpikiapp.android.SafeAsyncTask;
import piki.example.com.loginpikiapp.pikitori.Service.FollowService;
import piki.example.com.loginpikiapp.pikitori.Service.UserService;
import piki.example.com.loginpikiapp.pikitori.core.BasicInfo;
import piki.example.com.loginpikiapp.pikitori.core.Utils;
import piki.example.com.loginpikiapp.pikitori.domain.UserVo;
import piki.example.com.loginpikiapp.pikitori.ui.Login.LoginUserActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.camera.CameraActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.follow.FollowActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.follow.FollowerActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.map.GoogleMapFragment;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostImageSelectActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.post.PostListFragment;
import piki.example.com.loginpikiapp.pikitori.ui.main.profile.ProfileModifyActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.search.SearchActivity;
import piki.example.com.loginpikiapp.pikitori.ui.main.setting.SettingActivity;

public class pikiMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "pikiMainActivity";

    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;

    UserService userService = new UserService();
    FollowService followService = new FollowService();

    public MainTabsAdapter mainTabsAdapter;
    private ImageView profileImageView;
    private TextView postCount;
    private TextView followerCount;
    private TextView followingCount;
    private TextView userName;
    private LinearLayout linearLayout;
    private Button hideButton;
    private boolean hideButtonStatus=true;

    private Button btn_profile_modify, btn_follow_add;
    FloatingActionButton fab_button, fab_camera, fab_image, fab_add;
    Animation FabOpen, FabClose, FabRClockwise, FabRantiClockwise ;
    boolean isOpen = false;

    private UserVo user;
    Uri imageFileUri;

    Adapter adapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piki_main);

        /*ActionBar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.pager);
        if(viewPager !=null){
            adapter = new Adapter(getSupportFragmentManager());
            viewPager.setAdapter(adapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        profileImageView = (ImageView) findViewById(R.id.ProfileImageView);
        userName = (TextView) findViewById(R.id.userName);
        postCount = (TextView) findViewById(R.id.postCount);
        followerCount = (TextView) findViewById(R.id.followerCount);
        followingCount = (TextView) findViewById(R.id.followingCount);
        btn_profile_modify = (Button) findViewById(R.id.btn_profile_modify);
        btn_follow_add = (Button) findViewById(R.id.btn_follow_add);

        profileImageView.setOnClickListener(this);
        followerCount.setOnClickListener(this);
        followingCount.setOnClickListener(this);
        btn_profile_modify.setOnClickListener(this);
        btn_follow_add.setOnClickListener(this);

        linearLayout = (LinearLayout) findViewById(R.id.profile_layout);

//      2. FloatingButton
        FabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        FabRClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        FabRantiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(this);
        fab_image = (FloatingActionButton) findViewById(R.id.fab_image);
        fab_image.setOnClickListener(this);
        fab_camera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fab_camera.setOnClickListener(this);
        fab_button = (FloatingActionButton) findViewById(R.id.fab_button);
        fab_button.setOnClickListener(this);

        //token
        tokenCheck(((UserVo)Utils.getUserPreferences(this,"PikiUser")).getUser_no());

    }

    private void tokenCheck(Long no){
        String token = FirebaseInstanceId.getInstance().getToken();
        String msg = getString(R.string.msg_token_fmt, token);
        Log.d(TAG, msg);
//        Toast.makeText(pikiMainActivity.this, msg, Toast.LENGTH_SHORT).show();

        new TokenCheckAsyncTask(no,token).execute();
    }


    @Override
    protected void onStart() {
        super.onStart();

        //1.  view_no
        Long no = getIntent().getLongExtra("view_no",0L) > 0 ?  getIntent().getLongExtra("view_no",0L) : ((UserVo)Utils.getUserPreferences(this,"PikiUser")).getUser_no();

        //1. view_no save
        Utils.setStringPreferences(this,"PostUserNo",String.valueOf(no));

        //2.  view_no == pikiUser
        Long user_no = ((UserVo)Utils.getUserPreferences(this,"PikiUser")).getUser_no();
        String view_no = Utils.getStringPreferences(this,"PostUserNo");
        if(!view_no.equals(String.valueOf(user_no))){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        update();
    }

    private void update() {
        new UpdateInfoTask(getApplicationContext()).execute();
    }

    private void updateUI(UserVo user) {
        /*profile*/
        ImageLoader.getInstance().displayImage(user.getUser_profile_url_thumb(), profileImageView, BasicInfo.displayImageOption);
        userName.setText(user.getUser_name());
        postCount.setText(Long.toString(user.getUser_post_count()));
        followerCount.setText(Long.toString(user.getUser_follower_count()));
        followingCount.setText(Long.toString(user.getUser_following_count()));
        if( user.getUser_no().equals(  ((UserVo)Utils.getUserPreferences(getApplicationContext(),"PikiUser")).getUser_no()) ) {
            btn_profile_modify.setVisibility(View.VISIBLE);
            btn_follow_add.setVisibility(View.GONE);
            followerCount.setOnClickListener(this);
            followingCount.setOnClickListener(this);
        }else{
            btn_follow_add.setVisibility(View.VISIBLE);
            btn_profile_modify.setVisibility(View.GONE);
            followerCount.setOnClickListener(null);
            followingCount.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ProfileImageView:{
                Intent intent = new Intent(this, ProfileViewActivity.class);
                intent.putExtra("userProfileImage",user.getUser_profile_url());
                startActivity(intent);
            }
            break;
            case R.id.btn_profile_modify: {
                Intent intent = new Intent(this, ProfileModifyActivity.class);
                startActivityForResult(intent,BasicInfo.RESULT_CODE_PROFILE_MODIFY);
            }
            break;
            case R.id.btn_follow_add:{
                if(btn_follow_add.getText().equals("팔로우 하기")){
                    new pikiMainActivity.CreateFollowTask(getApplicationContext()).execute();
                    new UpdateInfoTask(getApplicationContext()).execute();
                }else{
                    new pikiMainActivity.UnFollowTask(getApplicationContext()).execute();
                    new UpdateInfoTask(getApplicationContext()).execute();
                }
            }
            break;
            case R.id.followerCount:{
                Intent i = new Intent(getApplicationContext(),FollowerActivity.class);
                i.putExtra("view_no",user.getUser_no());
                startActivity(i);
            }
            break;
            case R.id.followingCount:{
                Intent i = new Intent(getApplicationContext(),FollowActivity.class);
                i.putExtra("view_no",user.getUser_no());
                startActivity(i);
            }
            break;
            case R.id.fab_button :{
                if ((isOpen)) {
                    fab_camera.startAnimation(FabClose);
                    fab_image.startAnimation(FabClose);
                    fab_add.startAnimation(FabClose);
                    fab_button.startAnimation(FabRantiClockwise);
                    fab_camera.setClickable(false);
                    fab_image.setClickable(false);
                    fab_add.setClickable(false);
                    isOpen = false;
                } else {
                    fab_camera.startAnimation(FabOpen);
                    fab_image.startAnimation(FabOpen);
                    fab_add.startAnimation(FabOpen);
                    fab_button.startAnimation(FabRClockwise);
                    fab_camera.setClickable(true);
                    fab_image.setClickable(true);
                    fab_add.setClickable(true);
                    isOpen = true;
                }
            }
            break;
            case R.id.fab_add:{

            }
            break;
            case R.id.fab_image:{
                Intent i = new Intent(getApplicationContext(),PostImageSelectActivity.class);
                startActivityForResult(i, BasicInfo.RESULT_CODE_IMAGE_GALLERY );
            }
            break;
            case R.id.fab_camera:{
                /*기본 카메라 어플*/
/*                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                startActivityForResult(intent, BasicInfo.RESULT_CAMERA );*/
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivityForResult(intent, BasicInfo.RESULT_CODE_CAMERA);
            }
            break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                this.finish();
            }
            break;
            case R.id.search:{
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.setting:{
                Intent intent =new Intent(this, SettingActivity.class);
                startActivity(intent);

            }
            break;
            case R.id.logout:{
                Utils.removePreferences(this,"user_login_status");
                Utils.removePreferences(this,"user_type");
                Utils.removePreferences(this,"PikiUser");
                Utils.removePreferences(this,"sessionID");
                Intent i = new Intent(this, LoginUserActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();

            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BasicInfo.RESULT_POST_UPDATE){
//            PostListFragment postListFragment = (PostListFragment) mainTabsAdapter.getItem(0);
//            postListFragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    public Adapter getFragmentPagerAdapter() {
        return adapter;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    private class TokenCheckAsyncTask extends  SafeAsyncTask<String>{
        Long user_no;
        String token;

        public TokenCheckAsyncTask(Long user_no, String token){
            this.user_no=user_no;
            this.token = token;
        }

        @Override
        public String call() throws Exception {
            String user_token = userService.tokenCheck(user_no, token);

            return user_token;
        }

        @Override
        protected void onSuccess(String user_token) throws Exception {
            super.onSuccess(user_token);

        }
    }

    private class UpdateInfoTask extends SafeAsyncTask<JSONResult> {
        Context context;
        public UpdateInfoTask(Context context) {
            this.context = context;
        }

        @Override
        public JSONResult call() throws Exception {
            return userService.getUserProfileInfo(context);
        }

        @Override
        protected void onSuccess(JSONResult jsonResult) throws Exception {
            if("fail".equals(jsonResult.getResult())){
                Toast.makeText(getApplicationContext(),"update user info 실패",Toast.LENGTH_SHORT).show();
            }else{
                user = (UserVo)jsonResult.getData();
                updateUI(user);
                Log.d(TAG,"UpdateTask" + (UserVo)jsonResult.getData());

                //1. 팔로우 여부 확인.
                new pikiMainActivity.UpdateFollowCheckTask(getApplicationContext()).execute();
            }
        }
    }

    private class UpdateFollowCheckTask extends SafeAsyncTask<String>{

        Context context;
        public UpdateFollowCheckTask(Context context) {
            this.context = context;
        }

        @Override
        public String call() throws Exception {
            return followService.followCheck(context);
        }

        @Override
        protected void onSuccess(String s) throws Exception {
            super.onSuccess(s);

            //1. null인경우(첫화면)
            if(s == null){
                Log.d(TAG, "UpdateFollowCheckTask 가 null입니다.");
            }
            //2. "follow"인경우.
            else if(s.equals("follow")){
                btn_follow_add.setText("팔로우 취소");
            }
            //3, "unfollow"인경우.
            else if(s.equals("unfollow")){
                btn_follow_add.setText("팔로우 하기");
            }
        }
    }

    private class CreateFollowTask extends SafeAsyncTask<String> {
        Context context;
        public CreateFollowTask(Context context) {
            this.context = context;
        }

        @Override
        public String call() throws Exception {
            return followService.following(context);
        }

        @Override
        protected void onSuccess(String result) throws Exception {
            super.onSuccess(result);

            //1. null 값인 경우.
            if(result == null){
                Log.d(TAG, "CreateFollowTask 가 null입니다.");
            }

            //2. "fail"인 경우.
            if("fail".equals(result)){
                Log.d(TAG, "CreateFollowTask 을 수행하지 못하였습니다.");
            }

            if("create".equals(result)) {
                Toast.makeText(getApplicationContext(), "팔로우를 맺었습니다.", Toast.LENGTH_SHORT).show();
                update();
                btn_follow_add.setText("팔로우 취소");
            }
        }
    }

    private class UnFollowTask extends SafeAsyncTask<String>{
        Context context;
        public UnFollowTask(Context context) {
            this.context = context;
        }

        @Override
        public String call() throws Exception {
            return followService.unFollowing(context);
        }

        @Override
        protected void onSuccess(String result) throws Exception {
            super.onSuccess(result);

            //1. null 값인 경우.
            if(result == null){
                Log.d(TAG, "UnFollowTask 가 null입니다.");
            }

            //2. "fail"인 경우.
            if("fail".equals(result)){
                Log.d(TAG, "UnFollowTask 을 수행하지 못하였습니다.");
            }

            if("unfollowed".equals(result)) {
                Toast.makeText(getApplicationContext(), "팔로우를  해제하였습니다.", Toast.LENGTH_SHORT).show();
                update();
                btn_follow_add.setText("팔로우 하기");
            }

        }
    }

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누르면 꺼버린다.", Toast.LENGTH_SHORT).show();
        }

//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setTitle("Are you Sure?")
//                .setMessage("Are you sure you want to close ?")
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
//                    }
//                })
//                .setNegativeButton("No",null)
//                .show();
    }

    private class Adapter extends FragmentPagerAdapter  {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        private final List<Integer> mFragmentICons = new ArrayList<>();

        public Adapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);

            addFragment(R.drawable.ic_post,"TimeLine",new PostListFragment());
            addFragment(R.drawable.ic_map,"Map",new GoogleMapFragment());
        }

        public void addFragment(int iconResId, String title,Fragment fragment){
            mFragments.add(fragment);
            mFragmentTitles.add(title);
            mFragmentICons.add(iconResId);
        }
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable image = ContextCompat.getDrawable(pikiMainActivity.this, mFragmentICons.get(position));
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//            SpannableString sb = new SpannableString(" " + mFragmentTitles.get(position));
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
//            return mFragmentTitles.get(position);
//            return super.getPageTitle(position);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
