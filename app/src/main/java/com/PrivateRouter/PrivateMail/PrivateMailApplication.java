package com.PrivateRouter.PrivateMail;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatDelegate;

import com.PrivateRouter.PrivateMail.dbase.AppDatabase;
import com.PrivateRouter.PrivateMail.dbase.migration.Migration1to2;
import com.PrivateRouter.PrivateMail.logic.SyncLogic;
import com.PrivateRouter.PrivateMail.repository.HostManager;
import com.PrivateRouter.PrivateMail.repository.IdentitiesRepository;
import com.PrivateRouter.PrivateMail.repository.KeysRepository;
import com.PrivateRouter.PrivateMail.repository.LoggedUserRepository;
import com.PrivateRouter.PrivateMail.repository.SettingsRepository;
import com.crashlytics.android.Crashlytics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class PrivateMailApplication  extends Application {
    private AppDatabase database;
    static  PrivateMailApplication instance;


    private LoggedUserRepository loggedUserRepository;
    private KeysRepository keysRepository;
    private SyncLogic syncLogic;
    private IdentitiesRepository identitiesRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Fabric.with(this, new Crashlytics());
        }catch (Throwable e){
            //todo wtf
            e.printStackTrace();
        }
        instance = this;

        updateLocale();

        initDBase();

        initKeyRepository();

        initCheckerMemoryLeak();

        initSyncLogic();

        initHostManager();

        initIdentities();

        if (SettingsRepository.getInstance().isNightMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void initIdentities() {
        identitiesRepository = new IdentitiesRepository();
    }

    private void initHostManager() {
        HostManager.init();
    }

    private void initSyncLogic() {
        syncLogic = new SyncLogic();
    }

    private void initKeyRepository() {
        keysRepository = new KeysRepository();
    }


    private void initDBase() {
        database = Room.databaseBuilder(this, AppDatabase.class, "database")
                .addMigrations(new Migration1to2())
                .build();

    }

    private void updateLocale() {
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());



        Configuration configuration = new Configuration(Resources.getSystem().getConfiguration());
        configuration.setLocale(Locale.ENGLISH);
        Resources.getSystem().updateConfiguration(configuration, null);





    }

    private void initCheckerMemoryLeak() {/*
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            return;

        }
        LeakCanary.install(this);*/
    }

    public static PrivateMailApplication getInstance() {
        if (instance==null) {
            instance = getCurrentApplication();
        }
        return instance ;
    }

    public static Context getContext() {
        if (instance!=null) {
            return instance.getApplicationContext();
        }
        else {
            instance = getCurrentApplication();
            if (instance!=null)
                return instance.getApplicationContext();
        }
        return null;
    }


    public AppDatabase getDatabase() {
        return database;
    }

    private static PrivateMailApplication getCurrentApplication() {
        try {
            final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            return (PrivateMailApplication) method.invoke(null, (Object[]) null);
        } catch (final ClassNotFoundException e) {
            // handle exception
        } catch (final NoSuchMethodException e) {
            // handle exception
        } catch (final IllegalArgumentException e) {
            // handle exception
        } catch (final IllegalAccessException e) {
            // handle exception
        } catch (final InvocationTargetException e) {
            // handle exception
        }  catch (final  Exception e) {
            // handle exception
        }
        return null;
    }

    public static Application getApplicationUsingReflection() throws Exception {
        return (Application) Class.forName("android.app.AppGlobals")
                .getMethod("getInitialApplication").invoke(null, (Object[]) null);
    }

    public LoggedUserRepository getLoggedUserRepository() {
        return loggedUserRepository;
    }

    public void setLoggedUserRepository(LoggedUserRepository loggedUserRepository) {
        this.loggedUserRepository = loggedUserRepository;
    }

    public KeysRepository getKeysRepository() {
        return keysRepository;
    }

    public IdentitiesRepository getIdentitiesRepository() {return  identitiesRepository;}

    public SyncLogic getSyncLogic() {
        return syncLogic;
    }
}
