package com.sliide.task.di

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.sliide.task.BuildConfig
import com.sliide.task.constants.TIME_OUT
import com.sliide.task.network.ApiService
import com.sliide.task.responsitory.NetworkRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
@Module
class FakeAppModule {

    @Singleton
    @Provides
    fun provideOKHttpClientLoggingInterceptor(): HttpLoggingInterceptor {
        return  HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            if (!BuildConfig.DEBUG)
                level = HttpLoggingInterceptor.Level.NONE
        }
    }
    @Singleton
    @Provides
    fun provideOKHttpClientInterceptor(): Interceptor {
        return object: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {

                val original = chain.request()

                val newRequest = original.newBuilder()
                    .addHeader("Authorization", "Bearer " + BuildConfig.TOKEN)
                    .build()

                return chain.proceed(newRequest)
            }
        }
    }

    @Singleton
    @Provides
    fun provideOKHttpClient(logInterceptor: HttpLoggingInterceptor,interceptor: Interceptor): OkHttpClient {
        return  OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8080")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    @Provides
    @Singleton
    fun providesNetworkService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}