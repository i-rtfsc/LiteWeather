package com.journeyOS.liteweather.app;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.journeyOS.liteweather.data.DataRepository;

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
//        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
//            return (T) new SettingsViewModel(mApplication, mRepository);
//        } else if (modelClass.isAssignableFrom(WeatherViewModel.class)) {
//            return (T) new WeatherViewModel(mApplication, mRepository);
//        }
        try {
            Class<?> settingsViewModel = Class.forName("com.journeyOS.liteweather.ui.weather.fragment.SettingsViewModel");
            if (modelClass.isAssignableFrom(settingsViewModel)) {
                return (T) reflection(settingsViewModel);
            }

            Class<?> weatherViewModel = Class.forName("com.journeyOS.liteweather.ui.weather.fragment.WeatherViewModel");
            if (modelClass.isAssignableFrom(weatherViewModel)) {
                return (T) reflection(weatherViewModel);
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
            Object viewModel = ct.newInstance(arguments);
            return viewModel;
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
