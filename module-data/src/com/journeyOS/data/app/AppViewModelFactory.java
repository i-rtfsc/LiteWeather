package com.journeyOS.data.app;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.journeyOS.data.DataRepository;
import com.journeyOS.liteframework.utils.KLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class AppViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static final String TAG = AppViewModelFactory.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static volatile AppViewModelFactory INSTANCE;

    private final Application mApplication;
    private final DataRepository mRepository;
    private final List<String> mClassNameList = new ArrayList<>();

    public static AppViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AppViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppViewModelFactory(application, Injection.provideDemoRepository());
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private AppViewModelFactory(Application application, DataRepository repository) {
        this.mApplication = application;
        this.mRepository = repository;
    }

    public <T> void setModel(Class<T> model) {
        String className = model.getName();
        KLog.d(TAG, "className = [" + className + "]");
        if (!mClassNameList.contains(className)) {
            mClassNameList.add(className);
        }
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            for (String className : mClassNameList) {
                KLog.d(TAG, "className = [" + className + "], modelClass = [" + modelClass.getName() + "]");
                Class<?> cls = Class.forName(className);
                if (modelClass.isAssignableFrom(cls)) {
                    return (T) reflection(cls);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }

    private Object reflection(Class<?> cls) {
        try {
            Class<?>[] param_types = new Class<?>[2];
            Object[] arguments = new Object[2];

            param_types[0] = Application.class;
            param_types[1] = DataRepository.class;

            arguments[0] = mApplication;
            arguments[1] = mRepository;

            Constructor<?> ct = cls.getConstructor(param_types);
            return ct.newInstance(arguments);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
