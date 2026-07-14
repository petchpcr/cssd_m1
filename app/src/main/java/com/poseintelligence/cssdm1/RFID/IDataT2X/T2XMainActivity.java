package com.poseintelligence.cssdm1.RFID.IDataT2X;

import static android.os.BatteryManager.EXTRA_STATUS;
import static android.os.BatteryManager.EXTRA_VOLTAGE;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_BATTERY_ERROR;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_FULL;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_GENERAL;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_CHG_QUICK;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_DSG_UHF;
import static realid.rfidlib.EmshConstant.EmshBatteryPowerMode.EMSH_PWR_MODE_STANDBY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.IDataT2X.Menu_Dispensing.DispensingRFIDT2XFrament;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.BaseFragment;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.GetRFIDThread;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.OnKeyDownListener;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.OnKeyListener;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.OnLowPower;
import com.poseintelligence.cssdm1.RFID.IDataT2X.util.MUtil;
import com.poseintelligence.cssdm1.RFID.IDataT2X.util.ThreadUtil;
import com.tencent.mmkv.MMKV;
import com.uhf.base.UHFManager;
import com.uhf.base.UHFModuleType;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import realid.rfidlib.CommonUtil;
import realid.rfidlib.EmshConstant;

public class T2XMainActivity extends AppCompatActivity {

    private FragmentManager manager;

    private Object currentFragment;


    private boolean ifCharge = false; //whether the pistol trigger is charging
    // RFID tag information acquisition thread
    private GetRFIDThread rfidThread = GetRFIDThread.getInstance();
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private boolean ifRequesetPermission = true;
    private boolean ifPowerOn;
    private int setPower = 33;
    private MMKV mkv;
    private TextView textViewUHFStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_t2x_main);
        if (Build.VERSION.SDK_INT > 22)
            requestPermission();
        else
            init();
    }

    private void init() {
        Log.d("tog_t2x","init");
        mkv = MMKV.defaultMMKV();
        UHFModuleType mType = UHFModuleType.UM_MODULE;
        mkv.encode(CommonUtil.CURRENT_UHF_MODULE, mType.name());
        UHFT2X.getMyApp().setUhfMangerImpl(UHFManager.getUHFImplSigleInstance(mType));
        manager = getSupportFragmentManager();

        MUtil.showProgressDialog(getString(R.string.init_msg), this);

        Log.d("tog_t2x","rfidThread isAlive = " + rfidThread.isAlive());
        Log.d("tog_t2x","rfidThread getState = " + rfidThread.getState());

        if(rfidThread.getState()==Thread.State.NEW){
            rfidThread.start();
        } else{
            rfidThread.restartThread();
        }

        ThreadUtil.getInstance().getExService().execute(new Runnable() {
            @Override
            public void run() {
                // Initialisation of the device and serial port configuration （only 50 equipment）
                if (UHFT2X.getMyApp().getUhfMangerImpl().getDeviceInfo().isIfHaveTrigger()) {
                    ifPowerOn = UHFT2X.getMyApp().getUhfMangerImpl().powerOn();
                    Log.d("tog_t2x","powerOn = " + ifPowerOn);
                    UHFT2X.getMyApp().getUhfMangerImpl().changeConfig(true);
                    monitorEmsh();
                } else {
                    if (UHFT2X.isChangeBaud) {
                        if ("921600".equals(MMKV.defaultMMKV().decodeString("baud")) && UHFManager.getType() == UHFModuleType.SLR_MODULE)
                            UHFT2X.getMyApp().getUhfMangerImpl().isHighUHFBaud(true);
                        else if ("115200".equals(MMKV.defaultMMKV().decodeString("baud")) && UHFManager.getType() == UHFModuleType.SLR_MODULE)
                            UHFT2X.getMyApp().getUhfMangerImpl().isHighUHFBaud(false);
                    }
                    Log.d("tog_t2x","powerOn = " + UHFT2X.getMyApp().getUhfMangerImpl().powerOn());
                }
                getModuleInfo();
                ua01DeviceToConfigure();
            }
        });

        registerPowerCapacity();
        acquireWakeLock();

        Bundle bd = getIntent().getExtras();
        int fragment = bd.getInt("fragment");
        setCurrentPage(fragment);

        ImageView backpage= (ImageView) findViewById(R.id.backpage);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout ll_setbeem= (LinearLayout) findViewById(R.id.ll_setbeem);
        ImageView bt_setting_power= (ImageView) findViewById(R.id.bt_setting_power);
        TextView textCurrentPowerLevel = (TextView) findViewById(R.id.textCurrentPowerLevel);
        TextView textPowerLevelSelect = (TextView) findViewById(R.id.textPowerLevelSelect);
        Button btPowerLevelUp= (Button) findViewById(R.id.btPowerLevelUp);
        Button btPowerLevelDown= (Button) findViewById(R.id.btPowerLevelDown);
        Button btSetPowerLevel= (Button) findViewById(R.id.btSetPowerLevel);
        SeekBar powerLevelBar= (SeekBar) findViewById(R.id.powerLevelBar);
        textViewUHFStatus = (TextView) findViewById(R.id.textViewUHFStatus);

        bt_setting_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll_setbeem.getVisibility()==View.VISIBLE){
                    ll_setbeem.setVisibility(View.GONE);
                }else{
                    int powerLevel = UHFT2X.getMyApp().getUhfMangerImpl().powerGet();
                    Log.d("tog_rfid", "UHFHelper getPowerLevel = " + powerLevel);
                    textCurrentPowerLevel.setText(powerLevel+" ");
                    textPowerLevelSelect.setText(powerLevel+" ");
                    powerLevelBar.setProgress(powerLevel);

                    ll_setbeem.setVisibility(View.VISIBLE);
                }
            }
        });

        powerLevelBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textPowerLevelSelect.setText(i+" ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btPowerLevelUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerLevelBar.setProgress(powerLevelBar.getProgress()+1);
            }
        });

        btPowerLevelDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerLevelBar.setProgress(powerLevelBar.getProgress()-1);
            }
        });

        btSetPowerLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pl = powerLevelBar.getProgress();
                boolean ifValue = UHFT2X.getMyApp().getUhfMangerImpl().powerSet(pl);

                if (ifValue && UHFT2X.saveSet){
                    MMKV.defaultMMKV().encode("power",pl);
                    Toast.makeText(T2XMainActivity.this, "Power level is "+pl+" ", Toast.LENGTH_SHORT).show();
                    ll_setbeem.setVisibility(View.GONE);
                }else{
                    Toast.makeText(T2XMainActivity.this, "ผิดพลาด ไม่สามารถตั้งค่าได้", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void setTextViewUHFStatus(boolean isScanning) {
        textViewUHFStatus.setText(isScanning ? "Scanning...":"Ready" );
    }

    public void goBack(int resultCode, Intent intent) {
        setResult(resultCode, intent);
        finish();
    }

    private void requestPermissionR() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                ifRequesetPermission = false;
                init();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1024);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1024 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                ifRequesetPermission = false;
                init();
            } else {
//                Toast.makeText(this,"获取设备权限失败，请重新运行app并授权，否则将无法正常运行！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ua01DeviceToConfigure() {
        int val = Settings.System.getInt(getContentResolver(), "idata_uhf_service_state", -1);

        Log.d("tog_t2x","val = " + val);
        if (val == 0) {
            registerPowerStatus();
        }
    }

    private PowerStatusCheck powerStatusCheck;

    private void registerPowerStatus() {
        if (powerStatusCheck == null) {
            powerStatusCheck = new PowerStatusCheck(this);
            IntentFilter mIntent = new IntentFilter("com.idata.uhf.power.supply");
            registerReceiver(powerStatusCheck, mIntent);
        }
    }

    private void unRegisterPowerStatus() {
        if (powerStatusCheck != null) {
            unregisterReceiver(powerStatusCheck);
        }
    }

    private int currentPowerStatus = 0;

    static class PowerStatusCheck extends BroadcastReceiver {
        private WeakReference<T2XMainActivity> mWeak;

        public PowerStatusCheck(T2XMainActivity mainActivity) {
            this.mWeak = new WeakReference<>(mainActivity);
        }


        @Override
        public void onReceive(Context context, final Intent intent) {
            ThreadUtil.getInstance().getExService().execute(new Runnable() {
                @Override
                public void run() {
                    int uhfPowerSupply = intent.getIntExtra("uhfPowerSupply", -1);
                    Log.d("tog_t2x","uhfPowerSupply = " + uhfPowerSupply + " currentPowerStatus = " + mWeak.get().currentPowerStatus);
                    if (uhfPowerSupply >= -1 && uhfPowerSupply != mWeak.get().currentPowerStatus) {
                        if (GetRFIDThread.getInstance().isIfPostMsg()) {
                            GetRFIDThread.getInstance().setIfPostMsg(false);
                            UHFT2X.getMyApp().getUhfMangerImpl().stopInventory();
                            UHFT2X.getMyApp().getUhfMangerImpl().powerOff();
                            SystemClock.sleep(100);
                            UHFT2X.getMyApp().getUhfMangerImpl().powerOn();
                            UHFT2X.getMyApp().getUhfMangerImpl().getRFIDProtocolStandard();
                            UHFT2X.getMyApp().getUhfMangerImpl().startInventoryTag();
                            UHFT2X.getMyApp().getUhfMangerImpl().readTagModeSet(UHFT2X.currentInvtDataType,0,0,0);
                            GetRFIDThread.getInstance().setIfPostMsg(true);
                        }
                    }
                    mWeak.get().currentPowerStatus = uhfPowerSupply;
                    Log.d("tog_t2x","uhfPowerSupply = " + uhfPowerSupply);
                }
            });

        }
    }

    public PowerCapacityBroadcastReceiver powerCapacity;

    private void registerPowerCapacity() {
        IntentFilter filter = new IntentFilter();
        powerCapacity = new PowerCapacityBroadcastReceiver();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(powerCapacity,filter);
    }

    private void unRegisterPowerCapacity() {
        if (powerCapacity != null) {
            unregisterReceiver(powerCapacity);
            powerCapacity = null;
        }
    }

    private OnLowPower mOnLowPower;

    private int  electricQuantity = 100;

    public class PowerCapacityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int currentElectricQuantity = intent.getIntExtra("level", 0);    //电池剩余电量
            int batteryVolt = intent.getIntExtra(EXTRA_VOLTAGE, -1);
            int status = intent.getIntExtra(EXTRA_STATUS,0); // 电池状态
            Log.d("TAG", "currentElectricQuantity " + currentElectricQuantity + " status " + status );
            if (currentElectricQuantity < 5) {
                if (!UHFT2X.isLowPower) {
                    UHFT2X.isLowPower = true;
                    if (currentFragment!=null && currentFragment instanceof OnLowPower)
                        mOnLowPower = (OnLowPower)currentFragment;
                    if (mOnLowPower!=null)
                        mOnLowPower.chargeChange(true);
                }
            }else {
                if (UHFT2X.isLowPower) {
                    UHFT2X.isLowPower = false;
                }
            }

//            if (currentElectricQuantity < 25) {
//                if (electricQuantity < 25) {
//
//                }else {
//                    if (GetRFIDThread.getInstance().isIfPostMsg()) {
//                        UHFT2X.getMyApp().getUhfMangerImpl().stopInventory();
//                        SystemClock.sleep(50);
//                        UHFT2X.getMyApp().getUhfMangerImpl().powerSet(25);
//                        UHFT2X.getMyApp().getUhfMangerImpl().startInventoryTag();
//                    } else {
//                        UHFT2X.getMyApp().getUhfMangerImpl().powerSet(25);
//                    }
//                }
//            } else if (currentElectricQuantity < 40) {
//                if (electricQuantity < 40) {
//
//                }else {
//                    if (GetRFIDThread.getInstance().isIfPostMsg()) {
//                        UHFT2X.getMyApp().getUhfMangerImpl().stopInventory();
//                        SystemClock.sleep(50);
//                        UHFT2X.getMyApp().getUhfMangerImpl().powerSet(30);
//                        UHFT2X.getMyApp().getUhfMangerImpl().startInventoryTag();
//                    } else {
//                        UHFT2X.getMyApp().getUhfMangerImpl().powerSet(30);
//                    }
//                }
//            }
            electricQuantity = currentElectricQuantity;
        }
    }

    private void getModuleInfo() {
        //Give time (according to the configuration performance of the model, 2.5S is generally sufficient) for the serial port and module to initialize
        SystemClock.sleep(2500);
        if (UHFModuleType.UM_MODULE == UHFManager.getType()) {
            //Initialize and judge the UHF module type of UM series
            String ver = UHFT2X.getMyApp().getUhfMangerImpl().hardwareVerGet();
            if (!TextUtils.isEmpty(ver)) {
                //Determine whether the UM 7 module function is supported
                char moduleType = ver.charAt(0);
                UHFT2X.ifSupportR2000Fun = moduleType == '7' || moduleType == '4' || moduleType == '5' || moduleType == '1';
                Log.d("tog_t2x","ifMode = " + UHFT2X.ifSupportR2000Fun + " ver =" + ver);
                UHFT2X.ifUM510 = moduleType == '5';
            }
            judgeModuleTypeAndRefreshUI(UHFT2X.ifSupportR2000Fun,false);
        } else if (UHFModuleType.SLR_MODULE == UHFManager.getType()) {
            String type = UHFT2X.getMyApp().getUhfMangerImpl().getUHFModuleType();
            Log.d("tog_t2x","type = " + type);
            if (!TextUtils.isEmpty(type)) {
                if (type.contains("SLR5100") ) {
                    UHFT2X.if5100Module = true;
                    judgeModuleTypeAndRefreshUI(false,false);
                }
                if (type.contains("7100")||type.contains("3100")||type.contains("SIM5100") || type.contains("3700") ||type.contains("3600")) {
                    UHFT2X.if7100Module = true;
                    judgeModuleTypeAndRefreshUI(true,false);
                    if (UHFT2X.saveSet) {
                        UHFT2X.getMyApp().getUhfMangerImpl().slrInventoryModeSet(MMKV.defaultMMKV().decodeInt("inventoryMode", 3));
                        UHFT2X.getMyApp().getUhfMangerImpl().powerSet(MMKV.defaultMMKV().decodeInt("power", 33));
                        UHFT2X.getMyApp().getUhfMangerImpl().frequencyModeSet(MMKV.defaultMMKV().decodeInt("frequencyModeSet", 3));
                    }else {
                        UHFT2X.getMyApp().getUhfMangerImpl().slrInventoryModeSet(3);
                    }
                }
            }
            MUtil.cancleDialog();
        }else if (UHFModuleType.RM_MODULE == UHFManager.getType()) {
            UHFT2X.getMyApp().getUhfMangerImpl().getRFIDProtocolStandard();
            UHFT2X.ifRMModule = true;
            judgeModuleTypeAndRefreshUI(false,false);
            MUtil.cancleDialog();
            Log.e("TAG", "getModuleInfo: " + UHFT2X.getMyApp().getUhfMangerImpl().powerGet() );
        }else if (UHFModuleType.GX_MODULE == UHFManager.getType()) {
            MUtil.cancleDialog();
            judgeModuleTypeAndRefreshUI(false,true);
        }
        if (UHFT2X.powerChange) {
            UHFT2X.getMyApp().getUhfMangerImpl().powerSet(Math.max(MMKV.defaultMMKV().decodeInt("setPower", 33), 5));
            UHFT2X.powerSize = MMKV.defaultMMKV().decodeInt("setPower", 33);
        }
        if (UHFT2X.isZhuYanCustom) {
            int mode = MMKV.defaultMMKV().decodeInt("inventoryMode", 4);
            UHFT2X.getMyApp().getUhfMangerImpl().slrInventoryModeSet(mode);
            if (mode == 4) {
                UHFT2X.getMyApp().getUhfMangerImpl().sessionModeSet(0);
            }
        }


        setTextViewUHFStatus(false);
    }

    private void judgeModuleTypeAndRefreshUI(final boolean isShowSearchPage,final boolean isShowInventoryPage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                searchTag.setVisibility(isShowSearchPage ? View.VISIBLE : View.GONE);
//                inventoryTag.setVisibility(isShowInventoryPage ? View.VISIBLE : View.GONE);
                MUtil.cancleDialog();
                if (currentFragment != null && currentFragment instanceof BaseFragment) {
                    ((BaseFragment) currentFragment).refreshUI();
                    //Log.e("tag", "  judgeModuleTypeAndRefreshUI   ");
                }
            }
        });
    }

    private final int requestPermissionCode = 10;

    private void requestPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestPermissionR();
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, requestPermissionCode);
            } else {
                ifRequesetPermission = false;
                init();
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == requestPermissionCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                recyleResoure();
            } else {
                ifRequesetPermission = false;
                init();
            }
        }
    }

    // Listening for device status at regular intervals
    private void monitorEmsh() {
        mEmshStatusReceiver = new EmshStatusBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EmshConstant.Action.INTENT_EMSH_BROADCAST);
        registerReceiver(mEmshStatusReceiver, intentFilter);

        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(EmshConstant.Action.INTENT_EMSH_REQUEST);
                intent.putExtra(EmshConstant.IntentExtra.EXTRA_COMMAND, EmshConstant.Command.CMD_REFRESH_EMSH_STATUS);
                sendBroadcast(intent);
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    // Set the currently displayed page
    private void setCurrentPage(int tabPage) {
        FragmentTransaction transaction = manager.beginTransaction();
        switch (tabPage) {
            // Receive screen
            case 0:
//                currentFragment = new ReceiveRFIDT2XFrament();
                break;
            // Dispensing screen
            case 1:
                currentFragment = new DispensingRFIDT2XFrament();
                break;
//            // Tag finder screen
//            case 2:
//                currentFragment = mSearchFragment = (mSearchFragment == null ? new SearchFragment() : mSearchFragment);
//                break;
//            case 3:
//                currentFragment = mInventoryFragment = (mInventoryFragment == null ? new InventoryFragment() : mInventoryFragment);
//                break;
            default:
                break;
        }
        transaction.replace(R.id.showData, (Fragment) currentFragment);
        transaction.commit();
    }

    // Handle button controls RFID thread reading and stopping
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("tog_t2x","keyCode" + keyCode);
        if (currentFragment instanceof OnKeyDownListener) {
            ((OnKeyDownListener) currentFragment).onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (currentFragment instanceof OnKeyListener) {
            ((OnKeyListener) currentFragment).onKeyUp(keyCode, event);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("tog_T2xMain","onResume");
        switchTriggerMode(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("tog_T2xMain","onPause");
        if (!ifRequesetPermission) {
            recyleResoure();
        }
        switchTriggerMode(true);
    }

    private void switchTriggerMode(boolean flag) {
//        Intent switchIScanKey = new Intent("android.intent.action.UHF_CHECK_TRIGGER");
//        switchIScanKey.putExtra("isEnableScan", flag);
//        sendBroadcast(switchIScanKey);
        if (!flag) {
            Intent switchIScanKey = new Intent("android.intent.action.BARCODEUNLOCKSCANKEY");
            sendBroadcast(switchIScanKey);
        }else {
            Intent switchIScanKey = new Intent("android.intent.action.BARCODELOCKSCANKEY");
            sendBroadcast(switchIScanKey);
        }
    }

    private volatile long lastTime = 0;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        Log.d("tog_T2xMain","onBackPressed");
        long currentTime = SystemClock.currentThreadTimeMillis();
        if (lastTime != 0 && currentTime - lastTime < 500) {
            recyleResoure();
        } else {
//            MUtil.show(R.string.exit_app);
        }
        lastTime = currentTime;
    }

    // Power down, recycle stop threads, exit application
    private void recyleResoure() {
        // Forced cessation of inventory, whether used or not
        UHFT2X.getMyApp().getUhfMangerImpl().stopInventory();
        switchTriggerMode(true);
        if (mEmshStatusReceiver != null) {
            unregisterReceiver(mEmshStatusReceiver);
            mEmshStatusReceiver = null;
        }
        if (mTimer != null || mTimerTask != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimerTask = null;
            mTimer = null;
        }

        rfidThread.destoryThread();

        Log.d("tog_t2x","poweroff = " + UHFT2X.getMyApp().getUhfMangerImpl().powerOff());
        UHFT2X.getMyApp().getUhfMangerImpl().changeConfig(false);
        unRegisterPowerStatus();
        releaseWakeLock();
        unRegisterPowerCapacity();
    }

    PowerManager.WakeLock wakeLock;

    public void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }

    public void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    private EmshStatusBroadcastReceiver mEmshStatusReceiver;

    private int oldStatue = -1;

    public class EmshStatusBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (EmshConstant.Action.INTENT_EMSH_BROADCAST.equalsIgnoreCase(intent.getAction())) {

                int sessionStatus = intent.getIntExtra("SessionStatus", 0);
                int batteryPowerMode = intent.getIntExtra("BatteryPowerMode", -1);
                Log.d("tog_t2x","sessionStatus = " + sessionStatus + "  batteryPowerMode  = " + batteryPowerMode);
                // Current status of battery
                if ((sessionStatus & EmshConstant.EmshSessionStatus.EMSH_STATUS_POWER_STATUS) != 0) {
                    // Same status does not process
                    if (batteryPowerMode == oldStatue) {
                        MUtil.cancelWaringDialog();
                        return;
                    }
                    oldStatue = batteryPowerMode;
                    switch (batteryPowerMode) {
                        case EMSH_PWR_MODE_STANDBY:
                            Log.d("tog_t2x","standby status 1 ifPowerOn = "+ifPowerOn);
                            break;
                        case EMSH_PWR_MODE_DSG_UHF:
                            Log.d("tog_t2x","DSG_UHF status");
//                            MUtil.show(R.string.poweron_success);
                            break;
                        case EMSH_PWR_MODE_CHG_GENERAL:
                        case EMSH_PWR_MODE_CHG_QUICK:
                            ifCharge = true;
                            ifPowerOn = false;
                            Log.d("tog_t2x","charging status");
//                            MUtil.show(R.string.charing);
                            break;
                        case EMSH_PWR_MODE_CHG_FULL:
                            ifCharge = true;
                            Log.d("tog_t2x","charging full status");
//                            MUtil.show(R.string.charing_full);
                            break;
                        default:
                            break;
                    }
                } else {
                    oldStatue = EMSH_PWR_MODE_BATTERY_ERROR;
                    Log.d("tog_t2x","unknown status");
                    MUtil.warningDialog(T2XMainActivity.this);
                }
            }
        }
    }
}
