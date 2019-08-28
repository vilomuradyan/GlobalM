package com.globalm.platform.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.fragments.AssignmentsFragment;
import com.globalm.platform.fragments.ChannelsFragment;
import com.globalm.platform.fragments.ContactsListFragment;
import com.globalm.platform.fragments.ContentSummaryFragment;
import com.globalm.platform.fragments.CreateContentFragment;
import com.globalm.platform.fragments.NotificationsFragment;
import com.globalm.platform.fragments.NotificationsSettingsFragment;
import com.globalm.platform.fragments.RootDashboardFragment;
import com.globalm.platform.fragments.TechnicalPreferencesFragment;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.LogoutResponse;
import com.globalm.platform.network.CloudSyncWorker;
import com.globalm.platform.network.RequestManager;
import com.globalm.platform.utils.PermissionsUtil;
import com.globalm.platform.utils.SharedPreferencesUtil;
import com.globalm.platform.utils.Utils;
import com.matrixxun.starry.badgetextview.MaterialBadgeTextView;

import java.io.File;

import timber.log.Timber;

import static com.globalm.platform.utils.Settings.loadString;
import static com.globalm.platform.utils.Settings.saveString;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private RootDashboardFragment mRootDashboardFragment = new RootDashboardFragment();
    private ContentSummaryFragment mContentSummaryFragment = new ContentSummaryFragment();
    private ContactsListFragment mContactsListFragment = new ContactsListFragment();
    private AssignmentsFragment mAssignmentsListFragment = new AssignmentsFragment();
    private ChannelsFragment mChannelsFragment = new ChannelsFragment();
    private CreateContentFragment mCreateContentFragment = new CreateContentFragment();
    private NotificationsFragment mNotificationsFragment = new NotificationsFragment();
    private NotificationsSettingsFragment mNotificationsSettingsFragment = new NotificationsSettingsFragment();
    private Fragment mActiveFragment;
    private FragmentManager mFragmentManager;
    private TextView mTextTitle;
    private ImageView mButtonMenu;
    private ImageView mButtonClose;
    private ImageView mButtonNotifications;
    private ImageView mButtonNotificationSettings;
    private MaterialBadgeTextView mTextNotificationCount;
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView mNavigation;
    private RelativeLayout mLayoutSettingsAndProfile;
    private ImageView mButtonUserSettings;
    private ImageView mButtonUsersIcon;
    private ImageView mButtonCreateContentIcon;
    private PopupWindow mPopupWindow ;

    private boolean createAssignment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (mNotificationsFragment.isVisible() || mNotificationsSettingsFragment.isVisible()) {
                resetNotificationsFragment();
            }
            boolean result = false;
            switch (item.getItemId()) {
                case R.id.dashboard:
                    mTextTitle.setText(R.string.title_dashboard);
                    switchFragment(mRootDashboardFragment);
                    result = true;
                    break;

                case R.id.content_summary:
                    mTextTitle.setText(R.string.title_content_summary);
                    switchFragment(mContentSummaryFragment);
                    result = true;
                    break;

                case R.id.contacts:
                    mTextTitle.setText(R.string.title_contacts);
                    switchFragment(mContactsListFragment);
                    result = true;
                    break;

                case R.id.assignments:
                    mTextTitle.setText(R.string.title_assignments);
                    switchFragment(mAssignmentsListFragment);
                    result = true;
                    break;

                case R.id.channels:
                    mTextTitle.setText(R.string.title_channels);
                    switchFragment(mChannelsFragment);
                    result = true;
                    break;

            }
            setupActionBarActions(item.getItemId());
            return result;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createAssignment = false;
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_main_blue));
        setContentView(R.layout.activity_main);
        mTextTitle = findViewById(R.id.text_title);
//        mButtonMenu = findViewById(R.id.button_menu);
        mButtonClose = findViewById(R.id.button_close_notifications);
        mButtonNotifications = findViewById(R.id.button_notifications);
        mButtonNotificationSettings = findViewById(R.id.button_notification_settings);
        mTextNotificationCount = findViewById(R.id.text_notification_count_folder);
        mDrawerLayout = findViewById(R.id.layout_drawer);
        mNavigation = findViewById(R.id.bottom_navigation);
        mButtonUserSettings = findViewById(R.id.button_user_settings);
        mLayoutSettingsAndProfile = findViewById(R.id.layout_settings_and_profile);
        mButtonUsersIcon = findViewById(R.id.button_users_icon);
        mButtonCreateContentIcon = findViewById(R.id.button_create_content);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setupActionBarActions(mNavigation.getSelectedItemId());
        mFragmentManager = getSupportFragmentManager();
//        mFragmentManager.beginTransaction().replace(R.id.content_frame, new LeftMenuFragment()).commit();
//        mButtonMenu.setOnClickListener(this);
        mButtonClose.setOnClickListener(this);
        mButtonNotifications.setOnClickListener(this);
        mButtonNotificationSettings.setOnClickListener(this);
        mButtonUserSettings.setOnClickListener(this);
        mButtonUsersIcon.setOnClickListener(this);
        mButtonCreateContentIcon.setOnClickListener(this);
        buildFragments();
        checkIntent(getIntent());
        mPopupWindow = popupDisplay();
        checkAndCreateSyncFolder();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(LogoutEvent event) {
//        Intent i = new Intent(this, LoginActivity.class);
//        i.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK));
//        startActivity(i);
//    }

    public PopupWindow popupDisplay() {

        final PopupWindow popupWindow = new PopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.rounded_layout_profile, null);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getDrawable(R.drawable.rounded_drawable));
        popupWindow.setContentView(view);

        LinearLayout ButtonLogOut = view.findViewById(R.id.button_log_out);
        LinearLayout ButtonUsers = view.findViewById(R.id.button_users);
        ButtonUsers.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class).putExtra("account", "account"));
            popupWindow.dismiss();
        });
        ButtonLogOut.setOnClickListener(v -> logout());
        return popupWindow;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void buildFragments() {
        mFragmentManager.beginTransaction().add(R.id.layout_frame, mRootDashboardFragment, "Dashboard").commit();
        mFragmentManager.beginTransaction().add(R.id.layout_frame, mContentSummaryFragment, "GetContent Summary").hide(mContentSummaryFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.layout_frame, mContactsListFragment, "Contacts").hide(mContactsListFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.layout_frame, mAssignmentsListFragment, "Assignments").hide(mAssignmentsListFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.layout_frame, mChannelsFragment, "Channels").hide(mChannelsFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.layout_frame, mCreateContentFragment, "Create GetContent").hide(mCreateContentFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.layout_frame, mNotificationsFragment, "Notifications").hide(mNotificationsFragment).commit();
        mFragmentManager.beginTransaction().add(R.id.layout_frame, mNotificationsSettingsFragment, "Notification Settings").hide(mNotificationsSettingsFragment).commit();
        mActiveFragment = mRootDashboardFragment;
    }

    public void switchFragment(Fragment fragment) {
        if (fragment != mActiveFragment) {
            mFragmentManager.beginTransaction().hide(mActiveFragment).show(fragment).commit();
            mActiveFragment = fragment;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.button_menu:
//                openDrawer();
//                break;

            case R.id.button_notifications:
                mFragmentManager.beginTransaction().show(mNotificationsFragment).commit();
                mTextTitle.setText(R.string.notifications);
                setToolbarButtons(true);
                break;

            case R.id.button_notification_settings:
                mFragmentManager.beginTransaction().show(mNotificationsSettingsFragment).commit();
                mTextTitle.setText(R.string.notification_settings);
                mButtonNotificationSettings.setVisibility(View.GONE);
                break;

            case R.id.button_close_notifications:
                if (mNotificationsSettingsFragment.isVisible()) {
                    mFragmentManager.beginTransaction().hide(mNotificationsSettingsFragment).commit();
                    mTextTitle.setText(R.string.notifications);
                    mButtonNotificationSettings.setVisibility(View.VISIBLE);
                } else {
                    mFragmentManager.beginTransaction().hide(mNotificationsFragment).commit();
                    setToolbarButtons(false);
                    mTextTitle.setText(mActiveFragment.getTag());
                }
                break;
            case R.id.button_user_settings:
                new Handler().postDelayed(() -> startActivity(new Intent(this, SettingsActivity.class).putExtra("settings","")), 100);
                break;
            case R.id.button_users_icon:
                mPopupWindow.showAsDropDown(mButtonUserSettings, -80, 0, Gravity.END);
                break;
            case R.id.button_create_content:
                if (createAssignment) {
                    CreateAssignmentActivity.start(MainActivity.this);
                } else {
                    CreateContentActivity.start(this, CreateContentFragment.POSITION_CREATE_EVENT);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private void resetNotificationsFragment() {
        mFragmentManager.beginTransaction().hide(mNotificationsFragment).commit();
        mFragmentManager.beginTransaction().hide(mNotificationsSettingsFragment).commit();
//        mButtonMenu.setVisibility(View.VISIBLE);
        mLayoutSettingsAndProfile.setVisibility(View.VISIBLE);
        mTextNotificationCount.setVisibility(View.VISIBLE);
        mButtonNotifications.setVisibility(View.VISIBLE);
        mButtonClose.setVisibility(View.GONE);
        mButtonNotificationSettings.setVisibility(View.GONE);
        mTextTitle.setText(mActiveFragment.getTag());

    }

    private void copyFileToSyncFolder(Uri uri) {
        checkAndCreateSyncFolder();

        Utils.copyFileToDir(
                uri,
                getSyncFolderFile().getAbsolutePath(),
                this,
                String.valueOf(getCurrentTimeMillis()),
                result -> CloudSyncWorker.createSyncTask());
    }

    private long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    private void checkAndCreateSyncFolder() {
        if (!getSyncEnabled()) {
            return;
        }

        if (PermissionsUtil.checkAndRequestPermissions(
                this,
                PermissionsUtil.getStoragePermissions(),
                PermissionsUtil.STORAGE_PERMISSION_REQUEST_CODE)) {
            if (!getSyncFolderFile().mkdirs()) {
                logSyncFolderError();
            }
            CloudSyncWorker.createSyncTask();
        }
    }

    private void logSyncFolderError() {
        Timber.e("Unable to create sync folder");
    }

    private File getSyncFolderFile() {
        return new File(TechnicalPreferencesFragment.SYNC_FOLDER_PATH);
    }

    private boolean getSyncEnabled() {
        return SharedPreferencesUtil.getSyncFolderEnabledOption();
    }


    private void setToolbarButtons(boolean notifications) {
//        mButtonMenu.setVisibility(notifications ? View.GONE : View.VISIBLE);
        mButtonNotifications.setVisibility(notifications ? View.GONE : View.VISIBLE);
        mLayoutSettingsAndProfile.setVisibility(notifications ? View.GONE : View.VISIBLE);
        mTextNotificationCount.setVisibility(notifications ? View.GONE : View.VISIBLE);
        mButtonNotifications.setVisibility(notifications ? View.GONE : View.VISIBLE);
        mButtonClose.setVisibility(notifications ? View.VISIBLE : View.GONE);
        mButtonNotificationSettings.setVisibility(notifications ? View.VISIBLE : View.GONE);
    }

    public TextView getTextTitle() {
        return mTextTitle;
    }

    public RootDashboardFragment getDashboardFragment() {
        return mRootDashboardFragment;
    }

    public ContentSummaryFragment getContentSummaryFragment() {
        return mContentSummaryFragment;
    }

    public ContactsListFragment getContactsListFragment() {
        return mContactsListFragment;
    }

    public ChannelsFragment getChannelsFragment() {
        return mChannelsFragment;
    }

    public CreateContentFragment getCreateContentFragment() {
        return mCreateContentFragment;
    }

    public BottomNavigationView getNavigation() {
        return mNavigation;
    }

    private void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.START);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    public void logout() {
        RequestManager.getInstance().logout(loadString("email"), loadString("password"), new CallbackListener<LogoutResponse>() {
            @Override
            public void onSuccess(LogoutResponse logoutResponseResponse) {
                saveString("access_token", "");
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                finish();
            }

            @Override
            public void onFailure(Throwable error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIntent(Intent intent) {
        Parcelable stream = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (userDataManager.isUserLoggedIn()) {
            if (stream instanceof Uri) {
                showSharingActionDialog((Uri) stream);
            }
        } else {
            Intent i = new Intent(new Intent(MainActivity.this, SplashActivity.class));
            i.putExtra(Intent.EXTRA_STREAM, stream);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    private void showSharingActionDialog(Uri uri) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item);
        arrayAdapter.add(getString(R.string.sharing_upload));
        arrayAdapter.add(getString(R.string.sharing_add_to_sync_folder));
        builderSingle.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            dialog.dismiss();
        });
        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            switch (which) {
                case 0:
                    CreateContentActivity.start(this, uri);
                    break;
                case 1:
                    copyFileToSyncFolder(uri);
                    break;
            }
        });
        builderSingle.show();
    }

    private void setupActionBarActions(int itemId) {
        switch (itemId) {
            case R.id.content_summary: {
                createAssignment = false;
                mButtonUserSettings.setVisibility(View.GONE);
                mButtonUsersIcon.setVisibility(View.GONE);
                mButtonCreateContentIcon.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.assignments: {
                createAssignment = true;
                mButtonUserSettings.setVisibility(View.GONE);
                mButtonUsersIcon.setVisibility(View.GONE);
                mButtonCreateContentIcon.setVisibility(View.VISIBLE);
                break;
            }
            default: {
                createAssignment = false;
                mButtonUserSettings.setVisibility(View.VISIBLE);
                mButtonUsersIcon.setVisibility(View.VISIBLE);
                mButtonCreateContentIcon.setVisibility(View.GONE);
            }
        }
    }
}
