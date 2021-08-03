package com.journeyOS.liteweather.app;

import com.journeyOS.liteweather.data.DataRepository;
import com.journeyOS.liteweather.data.source.HttpDataSource;
import com.journeyOS.liteweather.data.source.LocalDataSource;
import com.journeyOS.liteweather.data.source.http.HttpDataSourceImpl;
import com.journeyOS.liteweather.data.source.local.LocalDataSourceImpl;


/**
 * 注入全局的数据仓库，可以考虑使用Dagger2。（根据项目实际情况搭建，千万不要为了架构而架构）
 */
public class Injection {
    public static DataRepository provideDemoRepository() {
        //网络数据源
        HttpDataSource httpDataSource = HttpDataSourceImpl.getInstance();
        //本地数据源
        LocalDataSource localDataSource = LocalDataSourceImpl.getInstance();
        //两条分支组成一个数据仓库
        return DataRepository.getInstance(httpDataSource, localDataSource);
    }
}
