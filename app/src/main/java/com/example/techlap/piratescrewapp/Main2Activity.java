package com.example.techlap.piratescrewapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kogitune.activity_transition.ActivityTransition;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialize.MaterializeBuilder;
import com.mikepenz.typeicons_typeface_library.Typeicons;

public class Main2Activity extends AppCompatActivity {
    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private MiniDrawer miniResult = null;
    private ViewPager mPager;
    private Crossfader crossFader;
    private PrimaryDrawerItem piratesWall,myProfile,myCommittee;
    private SecondaryDrawerItem myTasks,addPost,sendTask,settings,logout,Commitees,BoardOfDirectors;
    private final int PIRATES_WALL_ID=1;
    private final int MY_PROFILE_ID=2;
    private final int MY_COMMITTEE_ID=3;

    private final int COMMITEES_ID=4;
    private final int BOD_ID=5;
    private final int MY_TASKS_ID=6;

    private final int ADD_POST_ID=7;
    private final int SEND_TASK_ID=8;
    private final int SETTINGS_ID=9;

    private final int LOG_OUT_ID=10;
    String commitee="";
    Bundle bundle;
    Intent intent;


    //get User data
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    private String Uid,name,emai,image;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        if (null != toolbar){
            setSupportActionBar(toolbar);
            //set the back arrow in the toolbar
           // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Pirates EG");
        intialItems();
        context=this;
        //get User data
        // if i would launch from these activity
        mAuth=FirebaseAuth.getInstance();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        Uid=mAuth.getCurrentUser().getUid();

        name=getIntent().getExtras().getString("name");
        emai=getIntent().getExtras().getString("email");
        image=getIntent().getExtras().getString("image");

        //Toast.makeText(Main2Activity.this,Uid,Toast.LENGTH_SHORT).show();

        mPager =(ViewPager)findViewById(R.id.pager);
        FragmentManager fragmentManager =getSupportFragmentManager();
        mPager.setAdapter(new MyPagerAdapter(fragmentManager));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0){
                    result.setSelection(piratesWall);
                }else if (position==1){
                    result.setSelection(myProfile);
                }else if (position==2){
                    result.setSelection(myCommittee);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ActivityTransition.with(getIntent()).to(mPager).start(savedInstanceState);

        //handle the style
        new MaterializeBuilder(this).build();

        final IProfile prof=new ProfileDrawerItem().withName(name).withEmail(emai).withIcon(image);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withHeaderBackground(R.drawable.navigation)
                .addProfiles(
                        prof
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        miniResult.onProfileClick();

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        final DrawerBuilder builder = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        piratesWall,
                        myProfile,
                        myCommittee,
                        Commitees,
                        BoardOfDirectors,
                        new SectionDrawerItem().withName("BOD Section").withTextColor(R.color.blackcode),
                        myTasks,
                        addPost,
                        sendTask,
                        new SectionDrawerItem().withName("Others").withTextColor(R.color.blackcode)
                        ,logout
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if(drawerItem.getIdentifier()==PIRATES_WALL_ID)
                        {
                            mPager.setCurrentItem(0);
                        }else if(drawerItem.getIdentifier()==MY_PROFILE_ID)
                        {
                            mPager.setCurrentItem(1);
                        }else if (drawerItem.getIdentifier()==MY_COMMITTEE_ID){
                            mPager.setCurrentItem(2);
                        }else{
                            bundle=new Bundle();
                            bundle.putString("name",name);
                            bundle.putString("email",emai);
                            bundle.putString("image",image);
                            switch ((int) drawerItem.getIdentifier()){
                                case COMMITEES_ID:
                                    intent=new Intent(Main2Activity.this,CommiteesActivity.class);
                                    intent.putExtras(bundle);//Extras not Extra
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case BOD_ID:
                                    intent=new Intent(Main2Activity.this,CommiteeActivity.class);
                                    bundle.putString("Commitee","Board Of Directors");
                                    intent.putExtras(bundle);//Extras not Extra
                                    startActivity(intent);
                                    break;
                                case MY_TASKS_ID:
                                    intent=new Intent(Main2Activity.this,MyTasks.class);
                                    intent.putExtras(bundle);//Extras not Extra
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;
                                case ADD_POST_ID:
                                    intent=new Intent(Main2Activity.this,PostActivity.class);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    break;
                                case SEND_TASK_ID:
                                    utilMembers(SEND_TASK_ID);
                                    break;
                                case LOG_OUT_ID:
                                    new AlertDialog.Builder(context).setMessage("Log Out ?")
                                            .setCancelable(false)
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            })
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    mAuth.signOut();
                                                    Intent intent1=new Intent(Main2Activity.this,NotificationListener.class);
                                                    stopService(intent1);
                                                    intent=new Intent(Main2Activity.this,SplashActivity.class);
                                                    startActivity(intent);
                                                }
                                            }).show();
                                    break;
                            }

                        }
                        miniResult.onItemClick(drawerItem);

                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withSliderBackgroundColor(getResources().getColor(R.color.md_white_1000));

        // build only the view of the Drawer (don't inflate it automatically in our layout which is done with .build())
        result = builder.buildView();
        // create the MiniDrawer and deinfe the drawer and header to be used (it will automatically use the items from them)
        miniResult = new MiniDrawer().withDrawer(result).withAccountHeader(headerResult);


        //IMPORTANT Crossfader specific implementation starts here (everything above is MaterialDrawer):

        //get the widths in px for the first and second panel
        int firstWidth = (int) UIUtils.convertDpToPixel(300, this);
        int secondWidth = (int) UIUtils.convertDpToPixel(72, this);

        //create and build our crossfader (see the MiniDrawer is also builded in here, as the build method returns the view to be used in the crossfader)
        crossFader = new Crossfader()
                .withContent(findViewById(R.id.pager))
                .withFirst(result.getSlider(), firstWidth)
                .withSecond(miniResult.build(this), secondWidth)
                .withGmailStyleSwiping()
                .withSavedInstance(savedInstanceState)
                .build();

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new CrossfadeWrapper(crossFader));

        //define a shadow (this is only for normal LTR layouts if you have a RTL app you need to define the other one
        crossFader.getCrossFadeSlidingPaneLayout().setShadowResourceLeft(R.drawable.material_drawer_shadow_left);
        Intent intent=new Intent(this,NotificationListener.class);
        startService(intent);

    }

    private void utilMembers(final int Use) {
        intent=new Intent(Main2Activity.this,TaskActivity.class);
        bundle=new Bundle();
        bundle.putString("name",name);
        bundle.putString("email",emai);
        bundle.putString("image",image);
        Uid=mAuth.getCurrentUser().getUid();
        mdatabase.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commitee=dataSnapshot.child("commitee").getValue(String.class);
                Log.d("CHECK22","User found Vivz"+commitee);
                if(!commitee.equals("")){
                    bundle.putString("Commitee",commitee);
                    if(Use==SEND_TASK_ID){
                        bundle.putInt("Use",1);
                    }else{
                        bundle.putInt("Use",0);
                    }
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CHECK22","User not found");
            }
        });
    }

    private void intialItems() {
        piratesWall =new PrimaryDrawerItem().withName("Pirates Wall").withIcon(FontAwesome.Icon.faw_google_wallet).withIdentifier(PIRATES_WALL_ID)
                .withIconColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedIconColor(getResources().getColor(R.color.md_blue_grey_500))
                .withTextColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedTextColor(getResources().getColor(R.color.md_blue_grey_500));

        myProfile=new PrimaryDrawerItem().withName("My Profile").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(MY_PROFILE_ID)
                .withIconColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedIconColor(getResources().getColor(R.color.md_blue_grey_500))
                .withTextColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedTextColor(getResources().getColor(R.color.md_blue_grey_500));

        myCommittee=new PrimaryDrawerItem().withName("My Commitee").withIcon(Typeicons.Icon.typ_group_outline).withIdentifier(MY_COMMITTEE_ID)
                .withIconColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedIconColor(getResources().getColor(R.color.md_blue_grey_500))
                .withTextColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedTextColor(getResources().getColor(R.color.md_blue_grey_500));

        Commitees=(SecondaryDrawerItem) new SecondaryDrawerItem().withName("Commitees").withIcon(Typeicons.Icon.typ_group).withIdentifier(COMMITEES_ID)
                .withIconColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedIconColor(getResources().getColor(R.color.md_blue_grey_500))
                .withTextColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedTextColor(getResources().getColor(R.color.md_blue_grey_500));

        BoardOfDirectors=(SecondaryDrawerItem) new SecondaryDrawerItem().withName("Board Of Directos").withIcon(GoogleMaterial.Icon.gmd_wb_iridescent).withIdentifier(BOD_ID)
                .withIconColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedIconColor(getResources().getColor(R.color.md_yellow_A700))
                .withTextColor(getResources().getColor(R.color.md_deep_purple_500))
                .withSelectedTextColor(getResources().getColor(R.color.md_yellow_A700));
        //    private SecondaryDrawerItem myTasks,addPost,sendTask,settings,logout;
        myTasks= (SecondaryDrawerItem) new SecondaryDrawerItem().withName("My Tasks").withIcon(GoogleMaterial.Icon.gmd_attachment)
        .withTextColor(R.color.blackcode).withIconColor(R.color.blackcode).withIdentifier(MY_TASKS_ID);
        addPost= (SecondaryDrawerItem) new SecondaryDrawerItem().withName("Add Post").withIcon(GoogleMaterial.Icon.gmd_comment)
                .withTextColor(R.color.blackcode).withIconColor(R.color.blackcode).withIdentifier(ADD_POST_ID);
        sendTask= (SecondaryDrawerItem) new SecondaryDrawerItem().withName("Send Task").withIcon(GoogleMaterial.Icon.gmd_mail_send)
                .withTextColor(R.color.blackcode).withIconColor(R.color.blackcode).withIdentifier(SEND_TASK_ID);
        settings= (SecondaryDrawerItem) new SecondaryDrawerItem().withName("Settings").withIcon(CommunityMaterial.Icon.cmd_settings)
                .withTextColor(R.color.blackcode).withIconColor(R.color.blackcode).withIdentifier(SETTINGS_ID);
        logout= (SecondaryDrawerItem) new SecondaryDrawerItem().withName("Log Out").withIcon(CommunityMaterial.Icon.cmd_logout)
                .withTextColor(R.color.blackcode).withIconColor(R.color.blackcode).withIdentifier(LOG_OUT_ID);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        //add the values which need to be saved from the crossFader to the bundle
        outState = crossFader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_1).setIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_sort).color(Color.WHITE).actionBar());
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("Exit ?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case R.id.menu_1:
                crossFader.crossFade();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //then call this method
        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            if(position==0){
                fragment=new FragmentHome();
            }else if(position==1){
                fragment=new FragmentProfile();
            }else if(position==2){
                fragment=new FragmentMyCommitee();
            }
            return fragment;
        }

        //1st view pager call this method to know how many pages are there
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0){
                return "Tab A";

            }else if(position==1){
                return "Tab B";
            }else if(position==2){
                return "Tab C";
            }
            return null;
        }
    }
}
