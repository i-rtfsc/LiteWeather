package com.journeyOS.core.app;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.journeyOS.core.data.DataRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class AppViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile AppViewModelFactory INSTANCE;
    private final Application mApplication;
    private final DataRepository mRepository;

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

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            Class<?> settingsViewModel = Class.forName("com.journeyOS.setting.ui.viewmodel.SettingsViewModel");
            if (modelClass.isAssignableFrom(settingsViewModel)) {
                return (T) reflection(settingsViewModel);
            }

            Class<?> weatherViewModel = Class.forName("com.journeyOS.weather.ui.viewmodel.WeatherViewModel");
            if (modelClass.isAssignableFrom(weatherViewModel)) {
                return (T) reflection(weatherViewModel);
            }

            Class<?> cityViewModel = Class.forName("com.journeyOS.city.ui.viewmodel.CityViewModel");
            if (modelClass.isAssignableFrom(cityViewModel)) {
                return (T) reflection(cityViewModel);
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
